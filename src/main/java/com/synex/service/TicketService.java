package com.synex.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synex.entity.Employee;
import com.synex.entity.Ticket;
import com.synex.enums.TicketAction;
import com.synex.enums.TicketStatus;
import com.synex.repository.EmployeeRepository;
import com.synex.repository.TicketRepository;


@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService historyService;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final EmployeeRepository employeeRepository;

    public TicketService(TicketRepository ticketRepository,
                         TicketHistoryService historyService,
                         EmailService emailService,
                         PdfService pdfService,
                         EmployeeRepository employeeRepository) {
        this.ticketRepository   = ticketRepository;
        this.historyService     = historyService;
        this.emailService       = emailService;
        this.pdfService         = pdfService;
        this.employeeRepository = employeeRepository;
    }
    
    //USER creates ticket
    @Transactional
    public Ticket createTicket(Ticket ticket, Employee creator) {
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreationDate(new Date());
        ticket.setCreatedBy(creator);
        Ticket saved = ticketRepository.save(ticket);
        
        // Log creation in history
        historyService.logAction(saved, TicketAction.CREATED, creator, "Ticket created");
        
        // email to notify ticket creation
        emailService.sendTicketCreationEmail(saved);
        
        // email to manager
        if (creator.getManagerId() != null) {
            employeeRepository.findById(creator.getManagerId()).ifPresent(manager ->
                emailService.sendNewTicketToManagerEmail(saved, manager)
            );
        }
        
        return saved;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }
    
    public List<Ticket> getTicketsByCreator(Employee employee) {
        return ticketRepository.findByCreatedBy(employee);
    }

    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> getTicketsByAssignee(Employee assignee) {
        return ticketRepository.findByAssignee(assignee);
    }
    
    public List<Ticket> getTicketsByManagedEmployees(Long managerId) {
        return ticketRepository.findByCreatedBy_ManagerIdOrderByCreationDateDesc(managerId);
    }
    
    //MANAGER approves ticket
    @Transactional
    public Ticket approveTicket(Long ticketId, Employee manager, String comments) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new RuntimeException("Only OPEN tickets can be approved");
        }
        
        ticket.setStatus(TicketStatus.APPROVED);
        Ticket updated = ticketRepository.save(ticket);
        
        // Log approval
        historyService.logAction(updated, TicketAction.APPROVED, manager, 
                comments != null ? comments : "Ticket approved by manager");
        
        return updated;
    }
    
    //MANAGER rejects ticket
    @Transactional
    public Ticket rejectTicket(Long ticketId, Employee manager, String reason) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new RuntimeException("Only OPEN tickets can be rejected");
        }
        
        ticket.setStatus(TicketStatus.REJECTED);
        Ticket updated = ticketRepository.save(ticket);
        
        // Log rejection
        historyService.logAction(updated, TicketAction.REJECTED, manager, 
                reason != null ? reason : "Ticket rejected");
        
        // rejection email to user
        emailService.sendTicketRejectedEmail(updated, reason);
        
        return updated;
    }
    
    //Ticket is Assigned
    @Transactional
    public Ticket assignTicket(Long ticketId, Employee assignee, Employee assignedBy, String comments) {
        Ticket ticket = getTicketById(ticketId);
        
        // Can assign tickets that are APPROVED or REOPENED
        if (ticket.getStatus() != TicketStatus.APPROVED && ticket.getStatus() != TicketStatus.REOPENED) {
            throw new RuntimeException("Only APPROVED or REOPENED tickets can be assigned");
        }
        
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setAssignee(assignee);
        Ticket updated = ticketRepository.save(ticket);
        
        // Log assignment
        historyService.logAction(updated, TicketAction.ASSIGNED, assignedBy, 
                comments != null ? comments : "Ticket assigned to " + assignee.getEmail());

        // assignment email to admin
        emailService.sendTicketAssignedEmail(updated);
        
        return updated;
    }
    
    // ADMIN resolves ticket
    @Transactional
    public Ticket resolveTicket(Long ticketId, Employee resolver, String resolutionDetails) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getStatus() != TicketStatus.ASSIGNED) {
            throw new RuntimeException("Only ASSIGNED tickets can be resolved");
        }
        
        ticket.setStatus(TicketStatus.RESOLVED);
        Ticket updated = ticketRepository.save(ticket);
        
        // Log resolution
        historyService.logAction(updated, TicketAction.RESOLVED, resolver, 
                resolutionDetails != null ? resolutionDetails : "Ticket resolved");
        
        // Get full history for PDF to generate pdf and email it on resolution to user
        List<com.synex.entity.TicketHistory> history = historyService.getHistoryByTicketId(ticketId);
        byte[] pdfBytes = pdfService.generateResolutionPdf(updated, history, resolutionDetails);
        emailService.sendTicketResolutionEmail(updated, pdfBytes, resolutionDetails);
        
        return updated;
    }
    
    // USER closes ticket
    @Transactional
    public Ticket closeTicket(Long ticketId, Employee closedBy, String comments) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new RuntimeException("Only RESOLVED tickets can be closed");
        }
        
        ticket.setStatus(TicketStatus.CLOSED);
        Ticket updated = ticketRepository.save(ticket);
        
        // Log closure
        historyService.logAction(updated, TicketAction.CLOSED, closedBy, 
                comments != null ? comments : "Ticket closed");
        
        return updated;
    }
    
    // USER reopens ticket
    @Transactional
    public Ticket reopenTicket(Long ticketId, Employee reopenedBy, String reason) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getStatus() != TicketStatus.CLOSED && ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new RuntimeException("Only CLOSED or RESOLVED tickets can be reopened");
        }
        
        ticket.setStatus(TicketStatus.REOPENED);
        ticket.setAssignee(null); // Clear assignee for reassignment
        Ticket updated = ticketRepository.save(ticket);
        
        // Log reopening
        historyService.logAction(updated, TicketAction.REOPENED, reopenedBy, 
                reason != null ? reason : "Ticket reopened");
        
        // Email manager: ticket reopened, reassignment needed
        if (updated.getCreatedBy().getManagerId() != null) {
            employeeRepository.findById(updated.getCreatedBy().getManagerId()).ifPresent(manager ->
                emailService.sendTicketReopenedToManagerEmail(updated, reason, manager)
            );
        }
        
        return updated;
    }
}
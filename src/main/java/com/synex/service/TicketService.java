package com.synex.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synex.entity.Employee;
import com.synex.entity.Ticket;
import com.synex.enums.TicketAction;
import com.synex.enums.TicketStatus;
import com.synex.repository.TicketRepository;


@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService historyService;

    public TicketService(TicketRepository ticketRepository, TicketHistoryService historyService) {
        this.ticketRepository = ticketRepository;
        this.historyService = historyService;
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
        
        return updated;
    }
    
    //Ticket is Assigned
    @Transactional
    public Ticket assignTicket(Long ticketId, Employee assignee, Employee assignedBy, String comments) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getStatus() != TicketStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED tickets can be assigned");
        }
        
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setAssignee(assignee);
        Ticket updated = ticketRepository.save(ticket);
        
        // Log assignment
        historyService.logAction(updated, TicketAction.ASSIGNED, assignedBy, 
                comments != null ? comments : "Ticket assigned to " + assignee.getEmail());
        
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
        
        return updated;
    }
}
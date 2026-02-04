package com.synex.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.synex.entity.Employee;
import com.synex.entity.Ticket;
import com.synex.entity.TicketHistory;
import com.synex.enums.TicketStatus;
import com.synex.enums.TicketPriority;
import com.synex.repository.EmployeeRepository;
import com.synex.repository.TicketHistoryRepository;
import com.synex.repository.TicketRepository;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    
	public TicketService(TicketRepository ticketRepository, 
						TicketHistoryRepository historyRepository,
						EmployeeRepository employeeRepository) {
		this.ticketRepository = ticketRepository;
		this.historyRepository = historyRepository;
		this.employeeRepository = employeeRepository;
	}
	
	public Ticket getTicketById(Long id) {
	    return ticketRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Ticket not found"));
	}

	public List<Ticket> getTicketsByUser(Long userId) {
	    Employee emp = getEmployee(userId);
	    return ticketRepository.findByCreatedBy(emp);
	}

	public List<Ticket> getPendingApprovalTickets(Long managerId) {
	    return ticketRepository.findByStatus(TicketStatus.PENDING_APPROVAL);
	}

	public List<Ticket> getApprovedTickets() {
	    return ticketRepository.findByStatus(TicketStatus.APPROVED);
	}

	public Ticket updateTicket(Long id, Ticket updated) {
	    Ticket ticket = getTicket(id);

	    validateStatus(ticket, TicketStatus.OPEN, TicketStatus.PENDING_APPROVAL);

	    ticket.setTitle(updated.getTitle());
	    ticket.setDescription(updated.getDescription());
	    ticket.setPriority(updated.getPriority());
	    ticket.setCategory(updated.getCategory());

	    return ticketRepository.save(ticket);
	}
	
	// USER Creates Ticket
	public Ticket createTicket(Long userId, Ticket ticket) {
        Employee user = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setCreatedBy(user);
        ticket.setStatus(TicketStatus.PENDING_APPROVAL);
        ticket.setCreationDate(new Date());

        Ticket saved = ticketRepository.save(ticket);

        logHistory(saved, "CREATED", user, "Ticket created");

        return saved;
	}
	
	// Manager Approval
    public Ticket approveTicket(Long ticketId, Long managerId) {
        Ticket ticket = getTicket(ticketId);
        validateStatus(ticket, TicketStatus.PENDING_APPROVAL);

        ticket.setStatus(TicketStatus.APPROVED);
        Ticket saved = ticketRepository.save(ticket);

        logHistory(saved, "APPROVED", getEmployee(managerId), "Approved");

        return saved;
    }
    
    // MANAGER rejects
    public Ticket rejectTicket(Long ticketId, Long managerId, String reason) {
        Ticket ticket = getTicket(ticketId);
        validateStatus(ticket, TicketStatus.PENDING_APPROVAL);

        ticket.setStatus(TicketStatus.REJECTED);
        Ticket saved = ticketRepository.save(ticket);

        logHistory(saved, "REJECTED", getEmployee(managerId), reason);

        return saved;
    }

    // ADMIN resolves
    public Ticket resolveTicket(Long ticketId, Long adminId) {
        Ticket ticket = getTicket(ticketId);
        validateStatus(ticket, TicketStatus.ASSIGNED, TicketStatus.APPROVED);

        ticket.setStatus(TicketStatus.RESOLVED);
        Ticket saved = ticketRepository.save(ticket);

        logHistory(saved, "RESOLVED", getEmployee(adminId), "Resolved");

        return saved;
    }
    
    // Helpers
    private Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private void validateStatus(Ticket ticket, TicketStatus... allowed) {
        for (TicketStatus s : allowed) {
            if (ticket.getStatus() == s) return;
        }
        throw new IllegalStateException("Invalid ticket state");
    }
	
    private void logHistory(Ticket ticket, String action,
            				Employee by, String comments) {
    	TicketHistory h = new TicketHistory();
    	h.setTicket(ticket);
    	h.setAction(action);
    	h.setActionBy(by);
    	h.setActionDate(new Date());
    	h.setComments(comments);

    	historyRepository.save(h);
    }
    
}

package com.synex.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.synex.entity.Employee;
import com.synex.entity.Ticket;
import com.synex.enums.TicketStatus;
import com.synex.repository.EmployeeRepository;
import com.synex.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final EmployeeRepository employeeRepository;

    public TicketController(TicketService ticketService, EmployeeRepository employeeRepository) {
        this.ticketService = ticketService;
        this.employeeRepository = employeeRepository;
    }
    
    // Create Ticket
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket, Authentication auth) {
        Employee creator = getEmployeeFromAuth(auth);
        Ticket created = ticketService.createTicket(ticket, creator);
        return ResponseEntity.ok(created);
    }
    
    // Get all Tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }
    
    // Ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }
    
    // Retrieve User tickets
    @GetMapping("/my-tickets")
    public ResponseEntity<List<Ticket>> getMyTickets(Authentication auth) {
        Employee employee = getEmployeeFromAuth(auth);
        return ResponseEntity.ok(ticketService.getTicketsByCreator(employee));
    }
    
    // Retrieve tickets on status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable TicketStatus status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }
    
    // Get assigned tickets (queue for manager and admin)
    @GetMapping("/assigned-to-me")
    public ResponseEntity<List<Ticket>> getAssignedTickets(Authentication auth) {
        Employee employee = getEmployeeFromAuth(auth);
        return ResponseEntity.ok(ticketService.getTicketsByAssignee(employee));
    }
    
    // Manager Approve
    @PostMapping("/{id}/approve")
    public ResponseEntity<Ticket> approveTicket(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication auth) {
        Employee manager = getEmployeeFromAuth(auth);
        String comments = body != null ? body.get("comments") : null;
        Ticket approved = ticketService.approveTicket(id, manager, comments);
        return ResponseEntity.ok(approved);
    }

    // Manager Reject
    @PostMapping("/{id}/reject")
    public ResponseEntity<Ticket> rejectTicket(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Employee manager = getEmployeeFromAuth(auth);
        String reason = body.get("reason");
        Ticket rejected = ticketService.rejectTicket(id, manager, reason);
        return ResponseEntity.ok(rejected);
    }
    
    // Assign Ticket to Admin
    @PostMapping("/{id}/assign")
    public ResponseEntity<Ticket> assignTicket(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        Employee assignedBy = getEmployeeFromAuth(auth);
        Long assigneeId = Long.valueOf(body.get("assigneeId").toString());
        Employee assignee = employeeRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
        String comments = (String) body.get("comments");
        
        Ticket assigned = ticketService.assignTicket(id, assignee, assignedBy, comments);
        return ResponseEntity.ok(assigned);
    }
    
    // Admin resolve ticket
    @PostMapping("/{id}/resolve")
    public ResponseEntity<Ticket> resolveTicket(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Employee resolver = getEmployeeFromAuth(auth);
        String resolutionDetails = body.get("resolutionDetails");
        Ticket resolved = ticketService.resolveTicket(id, resolver, resolutionDetails);
        return ResponseEntity.ok(resolved);
    }
    
    // close ticket
    @PostMapping("/{id}/close")
    public ResponseEntity<Ticket> closeTicket(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication auth) {
        Employee closedBy = getEmployeeFromAuth(auth);
        String comments = body != null ? body.get("comments") : null;
        Ticket closed = ticketService.closeTicket(id, closedBy, comments);
        return ResponseEntity.ok(closed);
    }
    
    // reopen ticket
    @PostMapping("/{id}/reopen")
    public ResponseEntity<Ticket> reopenTicket(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Employee reopenedBy = getEmployeeFromAuth(auth);
        String reason = body.get("reason");
        Ticket reopened = ticketService.reopenTicket(id, reopenedBy, reason);
        return ResponseEntity.ok(reopened);
    }

    private Employee getEmployeeFromAuth(Authentication auth) {
        String email = auth.getName();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
    
}
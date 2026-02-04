package com.synex.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.synex.entity.Ticket;
import com.synex.enums.TicketPriority;
import com.synex.enums.TicketStatus;
import com.synex.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    // create
    @PostMapping("/tickets")
    public Ticket createTicket(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam TicketPriority priority,
            @RequestParam String category,
            @RequestParam(required = false) MultipartFile attachment
    ) throws IOException {

        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setCategory(category);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreationDate(new Date());

        if (attachment != null && !attachment.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + attachment.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, attachment.getBytes());
            ticket.setFileAttachmentPath(path.toString());
        }

        return ticketRepository.save(ticket);
    }
    
    // Read ticket id
    @GetMapping("/{id}")
    public Ticket getById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
    
    // READ: user tickets
    @GetMapping("/user/{userId}")
    public List<Ticket> userTickets(@PathVariable Long userId) {
        return ticketService.getTicketsByUser(userId);
    }

    // READ: manager approval queue
    @GetMapping("/manager/{managerId}")
    public List<Ticket> managerTickets(@PathVariable Long managerId) {
        return ticketService.getPendingApprovalTickets(managerId);
    }

    // READ: admin queue
    @GetMapping("/admin")
    public List<Ticket> adminTickets() {
        return ticketService.getApprovedTickets();
    }

    // UPDATE (user edit before approval)
    @PutMapping("/{id}")
    public Ticket update(@PathVariable Long id,
                         @RequestBody Ticket updated) {
        return ticketService.updateTicket(id, updated);
    }
    
    @PostMapping("/{id}/approve/{managerId}")
    public Ticket approve(@PathVariable Long id,
                          @PathVariable Long managerId) {
        return ticketService.approveTicket(id, managerId);
    }

    @PostMapping("/{id}/reject/{managerId}")
    public Ticket reject(@PathVariable Long id,
                         @PathVariable Long managerId,
                         @RequestParam String reason) {
        return ticketService.rejectTicket(id, managerId, reason);
    }

    @PostMapping("/tickets/{id}/resolve")
    public String resolveTicket(@PathVariable Long id) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setStatus(TicketStatus.RESOLVED);

        String pdfPath = pdfService.generateResolutionPdf(ticket);

        emailService.sendResolutionEmail(
                ticket.getCreatedBy().getEmail(),
                pdfPath
        );

        ticketRepository.save(ticket);
        return "Ticket resolved and emailed";
    }
}
package com.synex.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.synex.entity.Employee;
import com.synex.entity.Ticket;
import com.synex.entity.TicketHistory;
import com.synex.enums.TicketPriority;
import com.synex.enums.TicketStatus;
import com.synex.repository.EmployeeRepository;
import com.synex.service.FileStorageService;
import com.synex.service.TicketHistoryService;
import com.synex.service.TicketService;

@Controller
@RequestMapping("/tickets")
public class TicketViewController {

    private final TicketService ticketService;
    private final TicketHistoryService historyService;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;

    public TicketViewController(TicketService ticketService, 
                                TicketHistoryService historyService,
                                EmployeeRepository employeeRepository,
                                FileStorageService fileStorageService) {
        this.ticketService = ticketService;
        this.historyService = historyService;
        this.employeeRepository = employeeRepository;
        this.fileStorageService = fileStorageService;
    }

    // create ticket form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("priorities", TicketPriority.values());
        return "tickets/create";
    }

    // Submit new ticket
    @PostMapping("/create")
    public String createTicket(@ModelAttribute Ticket ticket,
                              @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        try {
            Employee creator = getEmployeeFromAuth(auth);

            // Only USERs can create tickets
            boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("USER"));
            if (!isUser) {
                redirectAttrs.addFlashAttribute("error", "Only users can create tickets!");
                return "redirect:/dashboard";
            }

            // Handle file upload
            if (attachment != null && !attachment.isEmpty()) {
                String filename = fileStorageService.storeFile(attachment);
                ticket.setFileAttachmentPath(filename);
                ticket.setOriginalFileName(attachment.getOriginalFilename());
            }

            ticketService.createTicket(ticket, creator);
            redirectAttrs.addFlashAttribute("success", "Ticket created! A confirmation email has been sent.");
            return "redirect:/tickets/my_tickets";
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error creating ticket: " + e.getMessage());
            return "redirect:/tickets/create";
        }
    }

    // View Tickets (role based)
    @GetMapping("/my_tickets")
    public String viewMyTickets(Model model, Authentication auth) {
        Employee employee = getEmployeeFromAuth(auth);

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MANAGER"));

        List<Ticket> tickets;
        String viewTitle;

        if (isAdmin) {
            // ADMINs see every ticket in the system
            tickets = ticketService.getAllTickets();
            viewTitle = "All Tickets";
        } else if (isManager) {
            // MANAGERs see tickets from employees they manage
            tickets = ticketService.getTicketsByManagedEmployees(employee.getId());
            viewTitle = "My Team's Tickets";
        } else {
            // USERs see only their own tickets
            tickets = ticketService.getTicketsByCreator(employee);
            viewTitle = "My Tickets";
        }
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("viewTitle", viewTitle);
        return "tickets/my_tickets";
    }

    // Manager pending approval
    @GetMapping("/pending_approval")
    public String viewPendingApproval(Model model) {
        List<Ticket> tickets = ticketService.getTicketsByStatus(TicketStatus.OPEN);
        model.addAttribute("tickets", tickets);
        return "tickets/pending_approval";
    }

    // Admin assigned tickets
    @GetMapping("/assigned")
    public String viewAssignedTickets(Model model, Authentication auth) {
        Employee admin = getEmployeeFromAuth(auth);
        List<Ticket> tickets = ticketService.getTicketsByAssignee(admin);
        model.addAttribute("tickets", tickets);
        return "tickets/assigned";
    }

    // View all approved tickets (for assignment)
    @GetMapping("/approved")
    public String viewApprovedTickets(Model model) {
        // Get both APPROVED and REOPENED tickets (reopened tickets need reassignment)
        List<Ticket> approvedTickets = ticketService.getTicketsByStatus(TicketStatus.APPROVED);
        List<Ticket> reopenedTickets = ticketService.getTicketsByStatus(TicketStatus.REOPENED);
        
        // Combine both lists
        List<Ticket> tickets = new java.util.ArrayList<>();
        tickets.addAll(approvedTickets);
        tickets.addAll(reopenedTickets);
        
        // Get only employees with ADMIN role
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> admins = allEmployees.stream()
                .filter(emp -> emp.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ADMIN")))
                .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("admins", admins);
        return "tickets/approved";
    }
    
    // View ticket details
    @GetMapping("/view/{id}")
    public String viewTicketDetails(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        List<TicketHistory> history = historyService.getHistoryByTicketId(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("history", history);
        return "tickets/details";
    }

    // Approve ticket
    @PostMapping("/{id}/approve")
    public String approveTicket(@PathVariable Long id, 
                               @RequestParam(required = false) String comments,
                               Authentication auth,
                               RedirectAttributes redirectAttrs) {
        try {
            // Only MANAGERs can approve tickets
            boolean isManager = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("MANAGER"));
            if (!isManager) {
                redirectAttrs.addFlashAttribute("error", "Only managers can approve tickets!");
                return "redirect:/dashboard";
            }
            
            Employee manager = getEmployeeFromAuth(auth);
            ticketService.approveTicket(id, manager, comments);
            redirectAttrs.addFlashAttribute("success", "Ticket approved successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/tickets/pending_approval";
    }
    
    // Reject ticket
    @PostMapping("/{id}/reject")
    public String rejectTicket(@PathVariable Long id, 
                              @RequestParam String reason,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        try {
            // Only MANAGERs can reject tickets
            boolean isManager = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("MANAGER"));
            if (!isManager) {
                redirectAttrs.addFlashAttribute("error", "Only managers can reject tickets!");
                return "redirect:/dashboard";
            }
            
            Employee manager = getEmployeeFromAuth(auth);
            ticketService.rejectTicket(id, manager, reason);
            redirectAttrs.addFlashAttribute("success", "Ticket rejected!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/tickets/pending_approval";
    }

    // Assign ticket
    @PostMapping("/{id}/assign")
    public String assignTicket(@PathVariable Long id,
                              @RequestParam Long assigneeId,
                              @RequestParam(required = false) String comments,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        try {
            // Only MANAGERs can assign tickets
            boolean isManager = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("MANAGER"));
            if (!isManager) {
                redirectAttrs.addFlashAttribute("error", "Only managers can assign tickets!");
                return "redirect:/dashboard";
            }
            
            Employee assignedBy = getEmployeeFromAuth(auth);
            Employee assignee = employeeRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            ticketService.assignTicket(id, assignee, assignedBy, comments);
            redirectAttrs.addFlashAttribute("success", "Ticket assigned successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/tickets/approved";
    }

    // Resolve ticket
    @PostMapping("/{id}/resolve")
    public String resolveTicket(@PathVariable Long id,
                               @RequestParam String resolutionDetails,
                               Authentication auth,
                               RedirectAttributes redirectAttrs) {
        try {
            // Only ADMINs can resolve tickets
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
            if (!isAdmin) {
                redirectAttrs.addFlashAttribute("error", "Only admins can resolve tickets!");
                return "redirect:/dashboard";
            }
            
            Employee resolver = getEmployeeFromAuth(auth);
            ticketService.resolveTicket(id, resolver, resolutionDetails);
            redirectAttrs.addFlashAttribute("success", "Ticket resolved successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/tickets/assigned";
    }

    // Close ticket
    @PostMapping("/{id}/close")
    public String closeTicket(@PathVariable Long id,
                             @RequestParam(required = false) String comments,
                             Authentication auth,
                             RedirectAttributes redirectAttrs) {
        try {
            // Only USERs can close tickets
            boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("USER"));
            if (!isUser) {
                redirectAttrs.addFlashAttribute("error", "Only users can close tickets!");
                return "redirect:/dashboard";
            }
            
            Employee closedBy = getEmployeeFromAuth(auth);
            ticketService.closeTicket(id, closedBy, comments);
            redirectAttrs.addFlashAttribute("success", "Ticket closed!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/tickets/my_tickets";
    }

    // Reopen ticket
    @PostMapping("/{id}/reopen")
    public String reopenTicket(@PathVariable Long id,
                              @RequestParam String reason,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        try {
            // Only USERs can reopen tickets
            boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("USER"));
            if (!isUser) {
                redirectAttrs.addFlashAttribute("error", "Only users can reopen tickets!");
                return "redirect:/dashboard";
            }
            
            Employee reopenedBy = getEmployeeFromAuth(auth);
            ticketService.reopenTicket(id, reopenedBy, reason);
            redirectAttrs.addFlashAttribute("success", "Ticket reopened!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/tickets/my_tickets";
    }
    
    // Download file attachment
    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename,
                                                  @RequestParam(required = false) String originalName) {
        try {
            java.nio.file.Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Use original filename for the download prompt if available
            String downloadName = (originalName != null && !originalName.isEmpty())
                    ? originalName : filename;

            // Detect content type
            String contentType = "application/octet-stream";
            try {
                contentType = java.nio.file.Files.probeContentType(filePath);
                if (contentType == null) contentType = "application/octet-stream";
            } catch (Exception ignored) {}

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + downloadName + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Employee getEmployeeFromAuth(Authentication auth) {
        String email = auth.getName();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
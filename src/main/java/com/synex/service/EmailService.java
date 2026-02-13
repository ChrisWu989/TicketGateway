package com.synex.service;

import com.synex.entity.Ticket;
import org.springframework.core.io.ByteArrayResource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private FileStorageService fileStorageService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm");

    // Ticket Created sent to user
    @Async
    public void sendTicketCreationEmail(Ticket ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(ticket.getCreatedBy().getEmail());
            helper.setSubject("TicketGateway - Ticket #" + ticket.getId() + " Created: " + ticket.getTitle());
            helper.setText(buildCreationEmailHtml(ticket), true);
            
            attachFileIfPresent(helper, ticket);
            mailSender.send(message);
            System.out.println("Ticket creation email sent to: " + ticket.getCreatedBy().getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send creation email: " + e.getMessage());
        }
    }
    
    // Ticket sent to manager for approval
    @Async
    public void sendNewTicketToManagerEmail(Ticket ticket, com.synex.entity.Employee manager) {
        if (manager == null) {
            System.err.println("No manager found to notify for ticket #" + ticket.getId());
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(manager.getEmail());
            helper.setSubject("TicketGateway - New Ticket #" + ticket.getId() + " Awaiting Your Approval: " + ticket.getTitle());
            helper.setText(buildNewTicketForManagerHtml(ticket), true);
            attachFileIfPresent(helper, ticket);
            mailSender.send(message);
            System.out.println("New ticket notification sent to manager: " + manager.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send manager notification: " + e.getMessage());
        }
    }

    // Ticket Resolved (with PDF attachment)
    @Async
    public void sendTicketResolutionEmail(Ticket ticket, byte[] pdfBytes, String resolutionDetails) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(ticket.getCreatedBy().getEmail());
            helper.setSubject("TicketGateway - Ticket #" + ticket.getId() + " Resolved: " + ticket.getTitle());
            helper.setText(buildResolutionEmailHtml(ticket, resolutionDetails), true);

            // Attach the PDF
            helper.addAttachment(
                    "Ticket_" + ticket.getId() + "_Resolution.pdf",
                    new org.springframework.core.io.ByteArrayResource(pdfBytes),
                    "application/pdf"
            );

            mailSender.send(message);
            System.out.println("Resolution email with PDF sent to: " + ticket.getCreatedBy().getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send resolution email: " + e.getMessage());
        }
    }
    
    // Ticket Assigned (to Admin)
    @Async
    public void sendTicketAssignedEmail(Ticket ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(ticket.getAssignee().getEmail());
            helper.setSubject("TicketGateway - Ticket #" + ticket.getId() + " Assigned to You: " + ticket.getTitle());
            helper.setText(buildAssignedEmailHtml(ticket), true);
            
            attachFileIfPresent(helper, ticket);
            mailSender.send(message);
            System.out.println("Assignment email sent to admin: " + ticket.getAssignee().getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send assignment email: " + e.getMessage());
        }
    }

    // Ticket Rejected (to User) 
    @Async
    public void sendTicketRejectedEmail(Ticket ticket, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(ticket.getCreatedBy().getEmail());
            helper.setSubject("TicketGateway - Ticket #" + ticket.getId() + " Rejected: " + ticket.getTitle());
            helper.setText(buildRejectedEmailHtml(ticket, reason), true);
            
            attachFileIfPresent(helper, ticket);
            mailSender.send(message);
            System.out.println("Rejection email sent to user: " + ticket.getCreatedBy().getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }
    }
    
    
    // Ticket reopened sent to manager
    @Async
    public void sendTicketReopenedToManagerEmail(Ticket ticket, String reason, com.synex.entity.Employee manager) {
        if (manager == null) {
            System.err.println("No manager found to notify for reopened ticket #" + ticket.getId());
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(manager.getEmail());
            helper.setSubject("TicketGateway - Ticket #" + ticket.getId() + " Reopened - Reassignment Required: " + ticket.getTitle());
            helper.setText(buildReopenedForManagerHtml(ticket, reason), true);
            
            mailSender.send(message);
            System.out.println("Reopened ticket notification sent to manager: " + manager.getEmail());
            
        } catch (MessagingException e) {
            System.err.println("Failed to send reopen notification: " + e.getMessage());
        }
    }

    private void attachFileIfPresent(MimeMessageHelper helper, Ticket ticket) {
        try {
            if (ticket.getFileAttachmentPath() != null && !ticket.getFileAttachmentPath().isEmpty()) {
                byte[] fileBytes = fileStorageService.loadFileAsBytes(ticket.getFileAttachmentPath());
                String displayName = ticket.getOriginalFileName() != null
                        ? ticket.getOriginalFileName()
                        : ticket.getFileAttachmentPath();
                helper.addAttachment(displayName, new ByteArrayResource(fileBytes));
            }
        } catch (Exception e) {
            System.err.println("Could not attach file to email: " + e.getMessage());
        }
    }
    
    // Email Templates 
    private String buildCreationEmailHtml(Ticket ticket) {
        String attachmentNote = (ticket.getFileAttachmentPath() != null && !ticket.getFileAttachmentPath().isEmpty())
                ? "      <p style='color: #888; font-size: 13px;'>📎 Your uploaded file (<strong>" + ticket.getOriginalFileName() + "</strong>) is attached to this email.</p>"
                : "";

        return "<!DOCTYPE html>" +
               "<html>" +
               "<head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
               "  <div style='max-width: 600px; margin: 30px auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

               // Header
               "    <div style='background-color: #2980b9; padding: 25px; text-align: center;'>" +
               "      <h1 style='color: white; margin: 0; font-size: 22px;'>TicketGateway</h1>" +
               "      <p style='color: #d6eaf8; margin: 5px 0 0 0; font-size: 14px;'>Your ticket has been received</p>" +
               "    </div>" +

               // Status Banner
               "    <div style='background-color: #f39c12; padding: 10px; text-align: center;'>" +
               "      <span style='color: white; font-weight: bold; font-size: 14px;'>STATUS: OPEN — Pending Manager Review</span>" +
               "    </div>" +

               // Body
               "    <div style='padding: 30px;'>" +
               "      <p style='color: #555; font-size: 15px;'>Hello,</p>" +
               "      <p style='color: #555; font-size: 15px;'>Your support ticket has been successfully submitted. Your manager will review it shortly.</p>" +

               // Ticket Card
               "      <div style='background-color: #f9f9f9; border-left: 4px solid #2980b9; padding: 15px 20px; border-radius: 4px; margin: 20px 0;'>" +
               "        <table style='width: 100%; border-collapse: collapse;'>" +
               row("Ticket ID",   "#" + ticket.getId()) +
               row("Title",       ticket.getTitle()) +
               row("Category",    ticket.getCategory()) +
               row("Priority",    ticket.getPriority().name()) +
               row("Status",      "OPEN") +
               row("Submitted",   DATE_FORMAT.format(ticket.getCreationDate())) +
               "        </table>" +
               "      </div>" +

               // Description
               "      <p style='color: #555; font-weight: bold; margin-bottom: 5px;'>Description:</p>" +
               "      <p style='color: #666; background-color: #f9f9f9; padding: 12px; border-radius: 4px; font-size: 14px;'>" + ticket.getDescription() + "</p>" +

               attachmentNote +

               "      <p style='color: #888; font-size: 13px; margin-top: 25px;'>You will receive another email when your ticket is approved or rejected by your manager.</p>" +
               "    </div>" +

               // Footer
               "    <div style='background-color: #f4f4f4; padding: 15px; text-align: center;'>" +
               "      <p style='color: #aaa; font-size: 12px; margin: 0;'>This is an automated message from TicketGateway. Please do not reply to this email.</p>" +
               "    </div>" +

               "  </div>" +
               "</body>" +
               "</html>";
    }
    
    private String buildNewTicketForManagerHtml(Ticket ticket) {
        String attachmentNote = (ticket.getFileAttachmentPath() != null && !ticket.getFileAttachmentPath().isEmpty())
                ? "      <p style='color: #555; font-size: 13px;'>📎 The user's uploaded file (<strong>" + ticket.getOriginalFileName() + "</strong>) is attached to this email for your review.</p>"
                : "";

        return "<!DOCTYPE html>" +
               "<html>" +
               "<head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
               "  <div style='max-width: 600px; margin: 30px auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

               // Header
               "    <div style='background-color: #2980b9; padding: 25px; text-align: center;'>" +
               "      <h1 style='color: white; margin: 0; font-size: 22px;'>TicketGateway</h1>" +
               "      <p style='color: #d6eaf8; margin: 5px 0 0 0; font-size: 14px;'>A new ticket requires your approval</p>" +
               "    </div>" +

               // Status Banner
               "    <div style='background-color: #f39c12; padding: 10px; text-align: center;'>" +
               "      <span style='color: white; font-weight: bold; font-size: 14px;'>ACTION REQUIRED — New Ticket Awaiting Your Approval</span>" +
               "    </div>" +

               // Body
               "    <div style='padding: 30px;'>" +
               "      <p style='color: #555; font-size: 15px;'>Hello,</p>" +
               "      <p style='color: #555; font-size: 15px;'>One of your team members has submitted a support ticket that requires your review and approval.</p>" +

               // Ticket Card
               "      <div style='background-color: #f9f9f9; border-left: 4px solid #f39c12; padding: 15px 20px; border-radius: 4px; margin: 20px 0;'>" +
               "        <table style='width: 100%; border-collapse: collapse;'>" +
               row("Ticket ID",    "#" + ticket.getId()) +
               row("Title",        ticket.getTitle()) +
               row("Category",     ticket.getCategory()) +
               row("Priority",     ticket.getPriority().name()) +
               row("Submitted By", ticket.getCreatedBy().getEmail()) +
               row("Submitted",    DATE_FORMAT.format(ticket.getCreationDate())) +
               "        </table>" +
               "      </div>" +

               // Description
               "      <p style='color: #555; font-weight: bold; margin-bottom: 5px;'>Description:</p>" +
               "      <p style='color: #666; background-color: #f9f9f9; padding: 12px; border-radius: 4px; font-size: 14px;'>" + ticket.getDescription() + "</p>" +

               attachmentNote +

               "      <p style='color: #888; font-size: 13px; margin-top: 25px;'>Please log in to TicketGateway to approve or reject this ticket.</p>" +
               "    </div>" +

               // Footer
               "    <div style='background-color: #f4f4f4; padding: 15px; text-align: center;'>" +
               "      <p style='color: #aaa; font-size: 12px; margin: 0;'>This is an automated message from TicketGateway. Please do not reply to this email.</p>" +
               "    </div>" +

               "  </div>" +
               "</body>" +
               "</html>";
    }

    private String buildResolutionEmailHtml(Ticket ticket, String resolutionDetails) {
        String resolvedBy = ticket.getAssignee() != null ? ticket.getAssignee().getEmail() : "IT Support";

        return "<!DOCTYPE html>" +
               "<html>" +
               "<head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
               "  <div style='max-width: 600px; margin: 30px auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

               // Header
               "    <div style='background-color: #2980b9; padding: 25px; text-align: center;'>" +
               "      <h1 style='color: white; margin: 0; font-size: 22px;'>TicketGateway</h1>" +
               "      <p style='color: #d6eaf8; margin: 5px 0 0 0; font-size: 14px;'>Your ticket has been resolved</p>" +
               "    </div>" +

               // Status Banner
               "    <div style='background-color: #27ae60; padding: 10px; text-align: center;'>" +
               "      <span style='color: white; font-weight: bold; font-size: 14px;'>STATUS: RESOLVED ✓</span>" +
               "    </div>" +

               // Body
               "    <div style='padding: 30px;'>" +
               "      <p style='color: #555; font-size: 15px;'>Hello,</p>" +
               "      <p style='color: #555; font-size: 15px;'>Great news! Your support ticket has been resolved. Please review the details below.</p>" +

               // Ticket Card
               "      <div style='background-color: #f9f9f9; border-left: 4px solid #27ae60; padding: 15px 20px; border-radius: 4px; margin: 20px 0;'>" +
               "        <table style='width: 100%; border-collapse: collapse;'>" +
               row("Ticket ID",    "#" + ticket.getId()) +
               row("Title",        ticket.getTitle()) +
               row("Category",     ticket.getCategory()) +
               row("Priority",     ticket.getPriority().name()) +
               row("Resolved By",  resolvedBy) +
               "        </table>" +
               "      </div>" +

               // Resolution Details
               "      <p style='color: #555; font-weight: bold; margin-bottom: 5px;'>Resolution Details:</p>" +
               "      <div style='color: #666; background-color: #eafaf1; padding: 15px; border-radius: 4px; border-left: 4px solid #27ae60; font-size: 14px;'>" +
               resolutionDetails +
               "      </div>" +

               // Action Required
               "      <div style='background-color: #fef9e7; border: 1px solid #f39c12; padding: 15px; border-radius: 4px; margin-top: 20px;'>" +
               "        <p style='color: #856404; font-weight: bold; margin: 0 0 5px 0; font-size: 14px;'>Action Required</p>" +
               "        <p style='color: #856404; margin: 0; font-size: 13px;'>Please review the resolution and either <strong>Close</strong> the ticket if you're satisfied, or <strong>Reopen</strong> it if the issue persists. You have <strong>7 days</strong> to reopen.</p>" +
               "      </div>" +

               "      <p style='color: #888; font-size: 13px; margin-top: 20px;'>📎 A detailed resolution report (PDF) is attached to this email for your records.</p>" +
               "    </div>" +

               // Footer
               "    <div style='background-color: #f4f4f4; padding: 15px; text-align: center;'>" +
               "      <p style='color: #aaa; font-size: 12px; margin: 0;'>This is an automated message from TicketGateway. Please do not reply to this email.</p>" +
               "    </div>" +

               "  </div>" +
               "</body>" +
               "</html>";
    }
    
    private String buildAssignedEmailHtml(Ticket ticket) {
        String attachmentNote = (ticket.getFileAttachmentPath() != null && !ticket.getFileAttachmentPath().isEmpty())
                ? "      <p style='color: #555; font-size: 13px;'>📎 The user's uploaded file (<strong>" + ticket.getOriginalFileName() + "</strong>) is attached to this email.</p>"
                : "";
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
               "  <div style='max-width: 600px; margin: 30px auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

               // Header
               "    <div style='background-color: #2980b9; padding: 25px; text-align: center;'>" +
               "      <h1 style='color: white; margin: 0; font-size: 22px;'>TicketGateway</h1>" +
               "      <p style='color: #d6eaf8; margin: 5px 0 0 0; font-size: 14px;'>A ticket has been assigned to you</p>" +
               "    </div>" +

               // Status Banner
               "    <div style='background-color: #17a2b8; padding: 10px; text-align: center;'>" +
               "      <span style='color: white; font-weight: bold; font-size: 14px;'>STATUS: ASSIGNED — Action Required</span>" +
               "    </div>" +

               // Body
               "    <div style='padding: 30px;'>" +
               "      <p style='color: #555; font-size: 15px;'>Hello,</p>" +
               "      <p style='color: #555; font-size: 15px;'>A support ticket has been assigned to you. Please review and resolve it as soon as possible.</p>" +

               // Ticket Card
               "      <div style='background-color: #f9f9f9; border-left: 4px solid #17a2b8; padding: 15px 20px; border-radius: 4px; margin: 20px 0;'>" +
               "        <table style='width: 100%; border-collapse: collapse;'>" +
               row("Ticket ID",    "#" + ticket.getId()) +
               row("Title",        ticket.getTitle()) +
               row("Category",     ticket.getCategory()) +
               row("Priority",     ticket.getPriority().name()) +
               row("Submitted By", ticket.getCreatedBy().getEmail()) +
               row("Status",       "ASSIGNED") +
               "        </table>" +
               "      </div>" +

               // Description
               "      <p style='color: #555; font-weight: bold; margin-bottom: 5px;'>Issue Description:</p>" +
               "      <p style='color: #666; background-color: #f9f9f9; padding: 12px; border-radius: 4px; font-size: 14px;'>" + ticket.getDescription() + "</p>" +
               attachmentNote +
               // Priority Warning
               "      <p style='color: #888; font-size: 13px; margin-top: 25px;'>Log in to TicketGateway to view the full ticket details and submit your resolution.</p>" +
               "    </div>" +

               // Footer
               "    <div style='background-color: #f4f4f4; padding: 15px; text-align: center;'>" +
               "      <p style='color: #aaa; font-size: 12px; margin: 0;'>This is an automated message from TicketGateway. Please do not reply to this email.</p>" +
               "    </div>" +
               "  </div>" +
               "</body></html>";
    }

    private String buildRejectedEmailHtml(Ticket ticket, String reason) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
               "  <div style='max-width: 600px; margin: 30px auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

               // Header
               "    <div style='background-color: #2980b9; padding: 25px; text-align: center;'>" +
               "      <h1 style='color: white; margin: 0; font-size: 22px;'>TicketGateway</h1>" +
               "      <p style='color: #d6eaf8; margin: 5px 0 0 0; font-size: 14px;'>Update on your support ticket</p>" +
               "    </div>" +

               // Status Banner
               "    <div style='background-color: #e74c3c; padding: 10px; text-align: center;'>" +
               "      <span style='color: white; font-weight: bold; font-size: 14px;'>STATUS: REJECTED</span>" +
               "    </div>" +

               // Body
               "    <div style='padding: 30px;'>" +
               "      <p style='color: #555; font-size: 15px;'>Hello,</p>" +
               "      <p style='color: #555; font-size: 15px;'>After review, your support ticket has been rejected. Please see the details and reason below.</p>" +

               // Ticket Card
               "      <div style='background-color: #f9f9f9; border-left: 4px solid #e74c3c; padding: 15px 20px; border-radius: 4px; margin: 20px 0;'>" +
               "        <table style='width: 100%; border-collapse: collapse;'>" +
               row("Ticket ID",  "#" + ticket.getId()) +
               row("Title",      ticket.getTitle()) +
               row("Category",   ticket.getCategory()) +
               row("Priority",   ticket.getPriority().name()) +
               row("Status",     "REJECTED") +
               "        </table>" +
               "      </div>" +

               // Rejection Reason
               "      <p style='color: #555; font-weight: bold; margin-bottom: 5px;'>Reason for Rejection:</p>" +
               "      <div style='color: #666; background-color: #fdf2f2; padding: 15px; border-radius: 4px; border-left: 4px solid #e74c3c; font-size: 14px;'>" +
               (reason != null && !reason.isEmpty() ? reason : "No specific reason provided.") +
               "      </div>" +

               "      <p style='color: #888; font-size: 13px; margin-top: 25px;'>If you believe this was rejected in error or need further assistance, please contact your manager directly or submit a new ticket with additional details.</p>" +
               "    </div>" +

               // Footer
               "    <div style='background-color: #f4f4f4; padding: 15px; text-align: center;'>" +
               "      <p style='color: #aaa; font-size: 12px; margin: 0;'>This is an automated message from TicketGateway. Please do not reply to this email.</p>" +
               "    </div>" +
               "  </div>" +
               "</body></html>";
    }
    
    private String buildReopenedForManagerHtml(Ticket ticket, String reason) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head><meta charset='UTF-8'></head>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
               "  <div style='max-width: 600px; margin: 30px auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

               // Header
               "    <div style='background-color: #2980b9; padding: 25px; text-align: center;'>" +
               "      <h1 style='color: white; margin: 0; font-size: 22px;'>TicketGateway</h1>" +
               "      <p style='color: #d6eaf8; margin: 5px 0 0 0; font-size: 14px;'>A ticket has been reopened</p>" +
               "    </div>" +

               // Status Banner
               "    <div style='background-color: #fd7e14; padding: 10px; text-align: center;'>" +
               "      <span style='color: white; font-weight: bold; font-size: 14px;'>ACTION REQUIRED — Ticket Reopened, Reassignment Needed</span>" +
               "    </div>" +

               // Body
               "    <div style='padding: 30px;'>" +
               "      <p style='color: #555; font-size: 15px;'>Hello,</p>" +
               "      <p style='color: #555; font-size: 15px;'>A user was not satisfied with the resolution and has reopened the following ticket. Please reassign it to an admin for further action.</p>" +

               // Ticket Card
               "      <div style='background-color: #f9f9f9; border-left: 4px solid #fd7e14; padding: 15px 20px; border-radius: 4px; margin: 20px 0;'>" +
               "        <table style='width: 100%; border-collapse: collapse;'>" +
               row("Ticket ID",    "#" + ticket.getId()) +
               row("Title",        ticket.getTitle()) +
               row("Category",     ticket.getCategory()) +
               row("Priority",     ticket.getPriority().name()) +
               row("Submitted By", ticket.getCreatedBy().getEmail()) +
               row("Status",       "REOPENED") +
               "        </table>" +
               "      </div>" +

               // Reason for Reopening
               "      <p style='color: #555; font-weight: bold; margin-bottom: 5px;'>Reason for Reopening:</p>" +
               "      <div style='color: #666; background-color: #fff3e0; padding: 15px; border-radius: 4px; border-left: 4px solid #fd7e14; font-size: 14px;'>" +
               (reason != null && !reason.isEmpty() ? reason : "No reason provided.") +
               "      </div>" +

               "      <p style='color: #888; font-size: 13px; margin-top: 25px;'>Please log in to TicketGateway to reassign this ticket to an admin.</p>" +
               "    </div>" +

               // Footer
               "    <div style='background-color: #f4f4f4; padding: 15px; text-align: center;'>" +
               "      <p style='color: #aaa; font-size: 12px; margin: 0;'>This is an automated message from TicketGateway. Please do not reply to this email.</p>" +
               "    </div>" +

               "  </div>" +
               "</body>" +
               "</html>";
    }
    
    private String row(String label, String value) {
        return "<tr>" +
               "  <td style='padding: 6px 0; color: #888; font-size: 13px; width: 40%;'>" + label + "</td>" +
               "  <td style='padding: 6px 0; color: #333; font-size: 13px; font-weight: bold;'>" + value + "</td>" +
               "</tr>";
    }
}
package com.synex.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.synex.entity.Ticket;
import com.synex.entity.TicketHistory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfService {

    private static final DeviceRgb HEADER_COLOR   = new DeviceRgb(41, 128, 185); 
    private static final DeviceRgb ACCENT_COLOR   = new DeviceRgb(39, 174, 96);   
    private static final DeviceRgb LIGHT_GRAY     = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb WHITE          = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb DARK_GRAY      = new DeviceRgb(80, 80, 80);
    private static final DeviceRgb BORDER_COLOR   = new DeviceRgb(200, 200, 200);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm");

    public byte[] generateResolutionPdf(Ticket ticket, List<TicketHistory> history, String resolutionDetails) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 50, 40, 50);

            PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Header Banner 
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100));

            Cell headerCell = new Cell()
                    .setBackgroundColor(HEADER_COLOR)
                    .setPadding(20)
                    .setBorder(Border.NO_BORDER);

            headerCell.add(new Paragraph("TICKET GATEWAY")
                    .setFont(boldFont).setFontSize(22)
                    .setFontColor(WHITE)
                    .setTextAlignment(TextAlignment.CENTER));

            headerCell.add(new Paragraph("Resolution Report")
                    .setFont(regularFont).setFontSize(14)
                    .setFontColor(WHITE)
                    .setTextAlignment(TextAlignment.CENTER));

            headerTable.addCell(headerCell);
            document.add(headerTable);
            document.add(new Paragraph("\n"));

            // Ticket Status Badge
            Table statusTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100));

            Cell statusCell = new Cell()
                    .setBackgroundColor(ACCENT_COLOR)
                    .setPadding(8)
                    .setBorder(Border.NO_BORDER);

            statusCell.add(new Paragraph("STATUS: RESOLVED")
                    .setFont(boldFont).setFontSize(12)
                    .setFontColor(WHITE)
                    .setTextAlignment(TextAlignment.CENTER));

            statusTable.addCell(statusCell);
            document.add(statusTable);
            document.add(new Paragraph("\n"));

            //Ticket Details Section
            document.add(new Paragraph("Ticket Information")
                    .setFont(boldFont).setFontSize(14)
                    .setFontColor(new DeviceRgb(41, 128, 185)));

            document.add(new com.itextpdf.layout.element.LineSeparator(
                    new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)));
            document.add(new Paragraph("\n").setFontSize(4));

            Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{35, 65}))
                    .setWidth(UnitValue.createPercentValue(100));

            addDetailRow(detailsTable, "Ticket ID",       "#" + ticket.getId(), boldFont, regularFont, true);
            addDetailRow(detailsTable, "Title",           ticket.getTitle(), boldFont, regularFont, false);
            addDetailRow(detailsTable, "Category",        ticket.getCategory(), boldFont, regularFont, true);
            addDetailRow(detailsTable, "Priority",        ticket.getPriority().name(), boldFont, regularFont, false);
            addDetailRow(detailsTable, "Created By",      ticket.getCreatedBy().getEmail(), boldFont, regularFont, true);
            addDetailRow(detailsTable, "Created Date",    DATE_FORMAT.format(ticket.getCreationDate()), boldFont, regularFont, false);

            if (ticket.getAssignee() != null) {
                addDetailRow(detailsTable, "Resolved By", ticket.getAssignee().getEmail(), boldFont, regularFont, true);
            }
            addDetailRow(detailsTable, "Resolved Date",   DATE_FORMAT.format(new Date()), boldFont, regularFont, false);

            document.add(detailsTable);
            document.add(new Paragraph("\n"));

            // Description 
            document.add(new Paragraph("Description")
                    .setFont(boldFont).setFontSize(14)
                    .setFontColor(new DeviceRgb(41, 128, 185)));

            document.add(new com.itextpdf.layout.element.LineSeparator(
                    new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)));
            document.add(new Paragraph("\n").setFontSize(4));

            document.add(new Paragraph(ticket.getDescription())
                    .setFont(regularFont).setFontSize(11)
                    .setFontColor(DARK_GRAY)
                    .setPadding(10)
                    .setBackgroundColor(LIGHT_GRAY));

            document.add(new Paragraph("\n"));

            // Resolution Details
            document.add(new Paragraph("Resolution Details")
                    .setFont(boldFont).setFontSize(14)
                    .setFontColor(new DeviceRgb(41, 128, 185)));

            document.add(new com.itextpdf.layout.element.LineSeparator(
                    new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)));
            document.add(new Paragraph("\n").setFontSize(4));

            document.add(new Paragraph(resolutionDetails)
                    .setFont(regularFont).setFontSize(11)
                    .setFontColor(DARK_GRAY)
                    .setPadding(10)
                    .setBackgroundColor(LIGHT_GRAY)
                    .setBorder(new SolidBorder(ACCENT_COLOR, 2)));

            document.add(new Paragraph("\n"));

            // Ticket History
            if (history != null && !history.isEmpty()) {
                document.add(new Paragraph("Ticket History")
                        .setFont(boldFont).setFontSize(14)
                        .setFontColor(new DeviceRgb(41, 128, 185)));

                document.add(new com.itextpdf.layout.element.LineSeparator(
                        new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)));
                document.add(new Paragraph("\n").setFontSize(4));

                Table historyTable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 50}))
                        .setWidth(UnitValue.createPercentValue(100));

                // Header row
                historyTable.addHeaderCell(buildHeaderCell("Action", boldFont));
                historyTable.addHeaderCell(buildHeaderCell("Performed By", boldFont));
                historyTable.addHeaderCell(buildHeaderCell("Comments", boldFont));

                boolean alt = false;
                for (TicketHistory entry : history) {
                    DeviceRgb rowColor = alt ? LIGHT_GRAY : WHITE;
                    historyTable.addCell(buildDataCell(entry.getAction().name(), regularFont, rowColor));
                    historyTable.addCell(buildDataCell(entry.getActionBy() != null
                            ? entry.getActionBy().getEmail() : "-", regularFont, rowColor));
                    historyTable.addCell(buildDataCell(
                            entry.getComments() != null ? entry.getComments() : "-", regularFont, rowColor));
                    alt = !alt;
                }

                document.add(historyTable);
                document.add(new Paragraph("\n"));
            }
            
            // File Attachment
            if (ticket.getFileAttachmentPath() != null && !ticket.getFileAttachmentPath().isEmpty()) {
                document.add(new Paragraph("File Attachment")
                        .setFont(boldFont).setFontSize(14)
                        .setFontColor(new DeviceRgb(41, 128, 185)));

                document.add(new com.itextpdf.layout.element.LineSeparator(
                        new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)));
                document.add(new Paragraph("\n").setFontSize(4));

                String displayName = ticket.getOriginalFileName() != null
                        ? ticket.getOriginalFileName()
                        : ticket.getFileAttachmentPath();

                // Embed image directly if it's an image type
                boolean isImage = displayName.matches("(?i).+\\.(jpg|jpeg|png|gif|bmp|webp)");

                if (isImage) {
                    try {
                        java.nio.file.Path imgPath = java.nio.file.Paths.get("uploads")
                                .toAbsolutePath().normalize().resolve(ticket.getFileAttachmentPath());
                        com.itextpdf.io.image.ImageData imageData =
                                com.itextpdf.io.image.ImageDataFactory.create(imgPath.toString());
                        com.itextpdf.layout.element.Image img =
                                new com.itextpdf.layout.element.Image(imageData);
                        img.setMaxWidth(400);
                        img.setHorizontalAlignment(HorizontalAlignment.LEFT);
                        document.add(new Paragraph("Attached image: " + displayName)
                                .setFont(regularFont).setFontSize(10).setFontColor(DARK_GRAY));
                        document.add(img);
                    } catch (Exception e) {
                        document.add(new Paragraph("📎 Attached file: " + displayName)
                                .setFont(regularFont).setFontSize(11).setFontColor(DARK_GRAY)
                                .setPadding(10).setBackgroundColor(LIGHT_GRAY));
                    }
                } else {
                    document.add(new Paragraph("📎 Attached file: " + displayName)
                            .setFont(regularFont).setFontSize(11).setFontColor(DARK_GRAY)
                            .setPadding(10).setBackgroundColor(LIGHT_GRAY));
                }

                document.add(new Paragraph("\n"));
            }
            
            //Footer
            document.add(new com.itextpdf.layout.element.LineSeparator(
                    new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(0.5f)));

            document.add(new Paragraph("Generated by TicketGateway on " + DATE_FORMAT.format(new Date()))
                    .setFont(regularFont).setFontSize(9)
                    .setFontColor(DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    // Helpers

    private void addDetailRow(Table table, String label, String value,
                               PdfFont boldFont, PdfFont regularFont, boolean shaded) {
        DeviceRgb bg = shaded ? LIGHT_GRAY : WHITE;

        table.addCell(new Cell()
                .add(new Paragraph(label).setFont(boldFont).setFontSize(10).setFontColor(DARK_GRAY))
                .setBackgroundColor(bg)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(5).setPaddingTop(5).setPaddingBottom(5));

        table.addCell(new Cell()
                .add(new Paragraph(value != null ? value : "-").setFont(regularFont).setFontSize(10))
                .setBackgroundColor(bg)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(5).setPaddingTop(5).setPaddingBottom(5));
    }

    private Cell buildHeaderCell(String text, PdfFont boldFont) {
        return new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(10)
                        .setFontColor(WHITE))
                .setBackgroundColor(HEADER_COLOR)
                .setBorder(Border.NO_BORDER)
                .setPadding(6);
    }

    private Cell buildDataCell(String text, PdfFont font, DeviceRgb bg) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(5);
    }
}
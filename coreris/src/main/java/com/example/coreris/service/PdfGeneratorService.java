package com.example.coreris.service;

import com.example.coreris.entity.Appointment;
import com.example.coreris.entity.Patient;
import com.example.coreris.entity.Radiologist;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    public byte[] generateReportPdf(Appointment appointment, String finding, Radiologist radiologist, Long adminId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Professional Clinical Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(30, 58, 138));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font findingFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

            // 1. Hospital Header
            Paragraph title = new Paragraph("CORERIS RADIOLOGY INFORMATION SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("DIAGNOSTIC IMAGING REPORT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY));
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(18);
            document.add(subtitle);

            // 2. Patient Demographics & Exam Info Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(15);

            Patient patient = appointment.getPatient();
            addCell(table, "Patient Name: " + (patient != null ? patient.getName() : "N/A"), headerFont);
            addCell(table, "Appointment ID: #" + appointment.getId(), headerFont);
            addCell(table, "Gender / DOB: " + (patient != null ? patient.getGender() + " / " + patient.getDob() : "N/A"), normalFont);
            addCell(table, "Scan Modality: " + appointment.getScanType(), normalFont);
            addCell(table, "Mobile: " + (patient != null ? patient.getMobileNo() : "N/A"), normalFont);
            addCell(table, "Date & Time: " + (appointment.getAppointmentTime() != null ? 
                    appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A"), normalFont);

            document.add(table);

            // 3. Scan Details Section
            Paragraph scanSection = new Paragraph("IMAGING DETAILS", headerFont);
            scanSection.setSpacingBefore(10);
            scanSection.setSpacingAfter(4);
            document.add(scanSection);

            String scanDetails = (appointment.getScanResult() != null && appointment.getScanResult().getScanDetails() != null)
                    ? appointment.getScanResult().getScanDetails()
                    : "Standard diagnostic imaging protocol completed.";
            Paragraph scanPara = new Paragraph(scanDetails, normalFont);
            scanPara.setSpacingAfter(15);
            document.add(scanPara);

            // 4. Clinical Findings Section
            Paragraph findingsHeader = new Paragraph("DIAGNOSTIC FINDINGS & IMPRESSION", headerFont);
            findingsHeader.setSpacingBefore(10);
            findingsHeader.setSpacingAfter(4);
            document.add(findingsHeader);

            Paragraph findingPara = new Paragraph(finding != null ? finding : "No specific abnormalities detected.", findingFont);
            findingPara.setSpacingAfter(40);
            document.add(findingPara);

            // 5. Medical Authorization Sign-off
            String signOff;
            if (radiologist != null) {
                signOff = "Interpreted by: " + (radiologist.getName() != null ? radiologist.getName() : "Radiologist #" + radiologist.getId());
            } else {
                signOff = "Authorized via: Administrator Override (User #" + adminId + ")";
            }
            Paragraph doctorSign = new Paragraph(signOff, headerFont);
            doctorSign.setAlignment(Element.ALIGN_RIGHT);
            document.add(doctorSign);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate clinical PDF report", e);
        }
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setPadding(6);
        table.addCell(cell);
    }
}

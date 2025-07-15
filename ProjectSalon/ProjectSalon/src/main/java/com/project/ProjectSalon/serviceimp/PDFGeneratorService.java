package com.project.ProjectSalon.serviceimp;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.project.ProjectSalon.entity.*;
import com.project.ProjectSalon.repo.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PDFGeneratorService {

    private final AppointmentRepository appointmentRepo;   // ⬅️ inject repo (needed only if you stay LAZY)

    public PDFGeneratorService(AppointmentRepository appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    public byte[] generateBillPDF(Optional<Bills> opt) throws Exception {
        Bills bill = opt.orElseThrow(() -> new Exception("Bill not found"));

        /* ------------------------------------------------------------
         *  1. Load the appointment with its services
         * ------------------------------------------------------------ */
        Appointment appt = bill.getAppointment();

        // If you KEPT LAZY, do a join‑fetch call; otherwise the next line is fine.
        List<Services> serviceList = appt.getServices();            // works when fetch = EAGER

        /* (uncomment if you kept LAZY + join‑fetch method)
        List<Services> serviceList = appointmentRepo
                .findByIdWithServices(appt.getAppointmentId())
                .orElseThrow(() -> new Exception("Appointment not found"))
                .getServices();
        */

        /* ------------------------------------------------------------
         *  2. Calculate totals – fall back to bill.amount if empty
         * ------------------------------------------------------------ */
        BigDecimal subTotal;
        if (serviceList == null || serviceList.isEmpty()) {
            // Graceful fallback (no exception) – still print a receipt
            serviceList = Collections.emptyList();
            subTotal    = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;
        } else {
            subTotal = serviceList.stream()
                    .map(Services::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal gst   = subTotal.multiply(BigDecimal.valueOf(0.18))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subTotal.add(gst);

        /* ------------------------------------------------------------
         *  3. Build PDF
         * ------------------------------------------------------------ */
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document doc = new Document(pdf);

        // Header
        doc.add(new Paragraph("Salon Invoice")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold());

        // Bill info
        doc.add(new Paragraph("Invoice ID   : " + bill.getBillId()));
        doc.add(new Paragraph("Bill Date    : " +
                bill.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        doc.add(new Paragraph("Customer     : " +
                appt.getCustomers().getFirstName() + " " +
                appt.getCustomers().getLastName()));

        /* ---------- Service table ---------- */
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2}))
                .useAllAvailableWidth();

        table.addHeaderCell(header("Service Name"));
        table.addHeaderCell(header("Price (₹)"));

        if (serviceList.isEmpty()) {
            table.addCell(new Paragraph("Service details unavailable"));
            table.addCell(new Paragraph("—"));
        } else {
            for (Services svc : serviceList) {
                table.addCell(new Paragraph(svc.getName()));
                table.addCell(new Paragraph("₹" +
                        svc.getPrice().setScale(2, RoundingMode.HALF_UP)));
            }
        }

        doc.add(table);

        // Totals
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("Number of Services : " + serviceList.size()));
        doc.add(new Paragraph("GST (18%)          : ₹" + gst));
        doc.add(new Paragraph("Grand Total        : ₹" + total).setBold());
        doc.add(new Paragraph("Payment Status     : " + bill.getPaymentStatus()));

        doc.close();
        return out.toByteArray();
    }

    private Cell header(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold();
    }
}

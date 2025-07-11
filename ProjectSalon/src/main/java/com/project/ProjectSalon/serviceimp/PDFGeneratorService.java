package com.project.ProjectSalon.serviceimp;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.project.ProjectSalon.entity.Bills;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@Service
public class PDFGeneratorService {

    public byte[] generateBillPDF(Optional<Bills> billsOptional) throws Exception {
        Bills bills = billsOptional.orElseThrow(() -> new Exception("Bill not found!"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // header
        Paragraph header = new Paragraph("Salon Shop")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20);
        document.add(header);

        //Add bill details
        //Bill :
        document.add(new Paragraph("Bill: " + bills.getBillId())
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12));

        //Date :
        document.add(new Paragraph("Date:" +
                bills.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12));

       //Add Customer Details
        document.add(new Paragraph("Customer Details :")
                .setFontSize(14)
                .setBold());

        //Name :
        document.add(new Paragraph(String.format("Name: %s %s",
                bills.getAppointment().getCustomers().getFirstName(),
                bills.getAppointment().getCustomers().getLastName())));

        //Phone :
        document.add(new Paragraph("Phone:" +
                bills.getAppointment().getCustomers().getPhone()));

        //Add services Details in Table
        Table table =new Table(UnitValue.createPointArray(new float[]{3,1}))
                .useAllAvailableWidth();

        table.addCell(new Cell().add(new Paragraph("Service"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        table.addCell(new Cell().add(new Paragraph("Amount"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        table.addCell(new Cell().add(new Paragraph(bills.getAppointment().getServices().getName())));
        table.addCell(new Cell().add(new Paragraph("₹" + bills.getAmount())));

        document.add(table);

        //Add Total
        document.add(new Paragraph("Total Amount:Rs" + bills.getAmount())
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14)
                .setBold());

        //Add Payment Details
        if(bills.getPaymentStatus() == Bills.PaymentStatus.PAID){
            document.add(new Paragraph("Payment Information:")
                    .setFontSize(12)
                    .setBold());
            document.add(new Paragraph("Status: Paid"));
            document.add(new Paragraph("Method: " + bills.getPaymentMethod()));
            document.add(new Paragraph("Transaction ID: " + bills.getTransactionId()));
        }

        // Add footer
        document.add(new Paragraph("Thank you !")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setItalic());

        document.close();
        return outputStream.toByteArray();
    }
}

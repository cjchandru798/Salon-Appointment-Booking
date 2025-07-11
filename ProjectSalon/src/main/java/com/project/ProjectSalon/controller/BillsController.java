package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Bills;
import com.project.ProjectSalon.serviceimp.BillsServiceImp;
import com.project.ProjectSalon.serviceimp.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillsController {

    @Autowired
    BillsServiceImp billsServiceImp;

    @Autowired
    PDFGeneratorService pdfGeneratorService;

    @GetMapping("/{billId}/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> getBillPdf(@PathVariable Long billId){

        try {
            Optional<Bills> bills = billsServiceImp.getBillById(billId);
            byte[] pdfContent = pdfGeneratorService.generateBillPDF(bills);

            HttpHeaders headers =new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename" , "bill"+ billId + ".pdf");

            return  ResponseEntity.ok().headers(headers).body(pdfContent);
        } catch (Exception e) {
           return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Bills createBills(@RequestBody Bills bills){
        return billsServiceImp.createBill(bills);
    }

    @DeleteMapping("/delete/{billId}")
    @PreAuthorize("hasRole('ADMIN')")
    public  void deleteBillInfo(@PathVariable Long billId){
        billsServiceImp.deleteBill(billId);
    }

}

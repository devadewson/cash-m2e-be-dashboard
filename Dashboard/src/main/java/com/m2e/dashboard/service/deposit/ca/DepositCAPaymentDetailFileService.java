package com.m2e.dashboard.service.deposit.ca;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.m2e.dashboard.dto.deposit.ca.response.DepositTrxReferenceDTO;
import com.m2e.dashboard.enums.PaymentTypeEnum;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class DepositCAPaymentDetailFileService {

    public byte[] generatePdf(DepositTrxReferenceDTO depositTrxReferenceDTO) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add Title
        document.add(new Paragraph("Deposit Current Accounts Payment Details Report")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold());

        document.add(new Paragraph("Payment Type: " + (depositTrxReferenceDTO.getPaymentTypeDesc() != null ? depositTrxReferenceDTO.getPaymentTypeDesc(): "-")));
        document.add(new Paragraph("Transaction Amount: " +  (depositTrxReferenceDTO.getTrxAmount() != null ? depositTrxReferenceDTO.getCurrency() + " " + depositTrxReferenceDTO.getTrxAmount() : depositTrxReferenceDTO.getCurrency() + " 0")));
        document.add(new Paragraph("Bank Reference Number: " +  (depositTrxReferenceDTO.getBankRefNo() != null ? depositTrxReferenceDTO.getBankRefNo() : "-")));
        document.add(new Paragraph("Transaction Type: " +  (depositTrxReferenceDTO.getTrxType() != null ? depositTrxReferenceDTO.getTrxType() : "-")));
        document.add(new Paragraph("Account: " +  (depositTrxReferenceDTO.getAcctName() != null ? depositTrxReferenceDTO.getAcctName() : "-")));
        document.add(new Paragraph((depositTrxReferenceDTO.getAcctNumber() != null ? depositTrxReferenceDTO.getAcctNumber() : "-")).setMarginLeft(51));
        document.add(new Paragraph("Payer: " +  (depositTrxReferenceDTO.getPayerName() != null ? depositTrxReferenceDTO.getPayerName().replaceAll("\\s+", " ") : "-")));
//        document.add(new Paragraph("Payer Account Number: " +  (depositTrxReferenceRes.getPayerAccount() != null ? depositTrxReferenceRes.getPayerAccount() : "-")));
        document.add(new Paragraph("Payer Bank: " +  (depositTrxReferenceDTO.getPayerBankCode() != null ? depositTrxReferenceDTO.getPayerBankCode() : "-")));
        document.add(new Paragraph((depositTrxReferenceDTO.getPayerBankName() != null ? depositTrxReferenceDTO.getPayerBankName() : "-")).setMarginLeft(70));
//        document.add(new Paragraph("Transaction Date: " +  (depositTrxReferenceRes.getTrxDate() != null ? depositTrxReferenceRes.getTrxDate() : "-")));
        document.add(new Paragraph("Credit Amount: " +  (depositTrxReferenceDTO.getCreditAmount() != null ? depositTrxReferenceDTO.getCurrency() + " " + depositTrxReferenceDTO.getCreditAmount() : depositTrxReferenceDTO.getCurrency() + " 0")));
        document.add(new Paragraph("Value Date: " +  (depositTrxReferenceDTO.getValueDate() != null ? depositTrxReferenceDTO.getValueDate() : "-")));
        document.add(new Paragraph("Total Charges: " +  (depositTrxReferenceDTO.getTotalCharges() != null ? depositTrxReferenceDTO.getCurrency() + " " + depositTrxReferenceDTO.getTotalCharges() : depositTrxReferenceDTO.getCurrency() + " 0")));
        document.add(new Paragraph("VAT Amount: " +  (depositTrxReferenceDTO.getVatAmount() != null ? depositTrxReferenceDTO.getCurrency() + " " + depositTrxReferenceDTO.getVatAmount() : depositTrxReferenceDTO.getCurrency() + " 0")));
        document.add(new Paragraph("Payment Details: " +  (depositTrxReferenceDTO.getPaymentDetail() != null ? depositTrxReferenceDTO.getPaymentDetail().replaceAll("\\s+", " ") : "-")));
//        document.add(new Paragraph("Cheque Number: " +  (depositTrxReferenceRes.getChequeNo() != null ? depositTrxReferenceRes.getChequeNo() : "-")));
//        document.add(new Paragraph("UETR Number: " +  (depositTrxReferenceRes.getUetrNo() != null ? depositTrxReferenceRes.getUetrNo() : "-")));
//        document.add(new Paragraph("Sender Name: " +  (depositTrxReferenceRes.getSenderName() != null ? depositTrxReferenceRes.getSenderName() : "-")));
//        document.add(new Paragraph("Bank: " +  (depositTrxReferenceRes.getBank() != null ? depositTrxReferenceRes.getBank() : "-")));
//        document.add(new Paragraph("Currency: " +  (depositTrxReferenceRes.getCurrency() != null ? depositTrxReferenceRes.getCurrency() : "-")));

        document.close();
        return baos.toByteArray();
    }
}

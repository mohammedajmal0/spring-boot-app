package com.banking.bank_project.service.impl;

import org.springframework.stereotype.Component;

import com.banking.bank_project.dto.EmailDetails;
import com.banking.bank_project.entity.Transaction;
import com.banking.bank_project.entity.User;
import com.banking.bank_project.repository.TransactionRepository;
import com.banking.bank_project.repository.UserRepository;
import com.banking.bank_project.utils.AccountUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;



@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    /**
     * retrive list of transaction within given date range
     * generate pdf
     * send via mail
     */
    public static final String FILE=System.getProperty("user.dir")+"/statements";
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;

    public List<Transaction> generateStatement(String accountNumber,String startDate, String endDate) throws FileNotFoundException, DocumentException{
        LocalDate start=LocalDate.parse(startDate,DateTimeFormatter.ISO_DATE);
        LocalDate end=LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList=transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
            .filter(transaction-> !transaction.getCreatedAt().isBefore(start))
            .filter(transaction-> !transaction.getCreatedAt().isAfter(end))
            .collect(Collectors.toList());
        
        User user=userRepository.findByAccountNumber(accountNumber);
        String customerName=user.getFirstName()+" "+user.getLastName();
            
        Rectangle statementSize=new Rectangle(PageSize.A4);
        Document document=new Document(statementSize);
        log.info("Setting size of document");
        String randomString=AccountUtils.generateRandomString();
        String fileLocation=FILE+"/"+randomString+".pdf";
        OutputStream outputStream=new FileOutputStream(fileLocation);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfPTable bankInfoPTable=new PdfPTable(1);
        PdfPCell bankName=new PdfPCell(new Phrase("The Project Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.GRAY);
        bankName.setPadding(20f);
        
        PdfPCell bankAddress=new PdfPCell(new Phrase("72 random address"));
        bankAddress.setBorder(0);
        bankInfoPTable.addCell(bankName);
        bankInfoPTable.addCell(bankAddress);

        PdfPTable statementInfo=new PdfPTable(2);
        PdfPCell customerInfo=new PdfPCell(new Phrase("Start Date :"+start));
        customerInfo.setBorder(0);
        PdfPCell statement=new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell toDate=new PdfPCell(new Phrase("end date :"+end));
        toDate.setBorder(0);
        PdfPCell custName=new PdfPCell(new Phrase("customer Name : "+ customerName));
        custName.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address=new PdfPCell(new Phrase("address :"+user.getAddress()));

        PdfPTable transactionTable=new PdfPTable(4);
        PdfPCell date=new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.GRAY);
        date.setBorder(0);
        PdfPCell transactionType=new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.GRAY);
        transactionType.setBorder(0);
        PdfPCell amount=new PdfPCell(new Phrase("AMOUNT"));
        amount.setBackgroundColor(BaseColor.GRAY);
        amount.setBorder(0);
        PdfPCell status=new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.GRAY);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(amount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction-> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));

        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(toDate);
        statementInfo.addCell(custName);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoPTable);
        document.add(statementInfo);
        document.add(transactionTable);
        document.close();

        EmailDetails emailDetails=EmailDetails.builder()
        .recipient(user.getEmail())
        .subject("Statement of account")
        .messageBody("Kindly find requested statemet below")
        .attachment(fileLocation)
        .build();

        emailService.sendEmailWithAttachment(emailDetails);
        return transactionList;    
    }
}

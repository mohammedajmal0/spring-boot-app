package com.banking.bank_project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.banking.bank_project.dto.EmailDetails;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendEmail(EmailDetails emailDetails) {
           try{ 
                SimpleMailMessage mailMessage=new SimpleMailMessage();
                mailMessage.setFrom(senderEmail);
                mailMessage.setTo(emailDetails.getRecipient());
                mailMessage.setText(emailDetails.getMessageBody());
                mailMessage.setSubject(emailDetails.getSubject());
                javaMailSender.send(mailMessage);
                System.out.println("Mail sent successfully");
           }catch(MailException e){
            System.out.println(e);
           }
    }
    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
       MimeMessage mimeMessage=javaMailSender.createMimeMessage();
       MimeMessageHelper mimeMessageHelper;
       try{
        mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom(senderEmail);
        mimeMessageHelper.setTo(emailDetails.getRecipient());
        mimeMessageHelper.setText(emailDetails.getMessageBody());
        mimeMessageHelper.setSubject(emailDetails.getSubject());
        FileSystemResource file=new FileSystemResource(new File(emailDetails.getAttachment()));
        mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
        javaMailSender.send(mimeMessage);
        log.info(file.getFilename()+ " file  sent to user");
       }
       catch(Exception err){

       }
    }
}

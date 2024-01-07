package com.project.springboot.controllers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {

    @GetMapping("/")
    public String goHome() {
        return "emailForm.html";
    }

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping("/emailForm")
    public String emailForm(HttpServletRequest request,
                            @RequestParam("attachment") MultipartFile multipartFile)
            throws MessagingException, UnsupportedEncodingException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String mailSubject = name + " sent an Email";
        String mailContent = "<p><b>From: </b> " + name + "</p>";
        mailContent += "<p><b>E-mail: </b> " + email + "</p>";
        mailContent += "<p><b>Subject: </b> " + subject + "</p>";
        mailContent += "<p> " + body + "</p>";

        helper.setFrom("youremail@email.com", "Sender"); // sender
        helper.setTo("youremail@email.com"); // recipient
        helper.setSubject(mailSubject);
        helper.setText(mailContent, true);

        // Limit of 1048576 bytes on file attachment, around 1MB
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            InputStreamSource source = new InputStreamSource() {
                @Override
                public InputStream getInputStream() throws IOException {
                    return multipartFile.getInputStream();
                }
            };
            helper.addAttachment(fileName, source);
        }

        emailSender.send(message);

        return "sent.html";
    }

}
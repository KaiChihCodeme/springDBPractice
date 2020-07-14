package com.tw.softmobile.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.mail.util.MailSSLSocketFactory;

@Controller
public class NoticeMailController {
	private Logger logger = Logger.getLogger(NoticeMailController.class);

	@RequestMapping("/SendMailC")
	@ResponseBody
	public void sendMail(HttpServletRequest req_messageBody, HttpServletResponse res) 
			throws GeneralSecurityException, IOException {
		String messageBody = null;
		try {
			// 讀取POST過來的訊息
			BufferedReader reader = req_messageBody.getReader();
			messageBody = reader.readLine();
			logger.info("messageBody: " + messageBody);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		
		String to = "kobe850829@gmail.com"; //收件人
		String from = "Spordan2018@gmail.com"; //寄件人
		String host = "smtp.gmail.com"; //host mail server
		Properties properties = System.getProperties();
		
		properties.setProperty("mail.smtp.host", host); //設定mail server
		properties.put("mail.smtp.auth", "true"); //需授權認證
		properties.put("mail.smtp.starttls.enable", "true"); //使用tls
		properties.put("mail.smtp.starttls.required", "true");
		properties.put("mail.smtp.port", 587); //gmail tls port 587
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
//		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);
		
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("Spordan2018@gmail.com", "SpordanFju"); 
				//寄件人帳號密碼授權
			}
		});
		
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from)); 
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); 
			message.setSubject("<MessageBook> You got a new message!");
			
			//利用template
			VelocityEngine ve = new VelocityEngine();
			//讓他去找resources folder
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();
			
			//指定template位置
			Template template = ve.getTemplate("templates/messageEmailTemplate.vm");
			
			//設定該template什麼地方要塞什麼值
			VelocityContext velocityContext = new VelocityContext();
			velocityContext.put("messageBody", messageBody);
			
			//將全部的template string做出來
			StringWriter stringWriter = new StringWriter();
			template.merge(velocityContext, stringWriter);
			
			//將mail內容塞入所有html內容
			message.setContent(stringWriter.toString(), "text/html; charset=UTF-8");
			
			Transport.send(message); //建立連線並送出信件
			logger.info("sent successfully");
			
			//因不論信件寄出成功或失敗，前端都不需知道，所以先不回傳任何狀態
			res.getWriter().append("0000").flush();
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
}

package com.tw.softmobile.controller;

import java.io.StringWriter;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.sun.mail.util.MailSSLSocketFactory;
import com.tw.softmobile.vtlTest;

@Controller
public class BaseController {
	private Logger logger = Logger.getLogger(BaseController.class);
	
	public void sendMail(String messageBody) throws Exception {
		String to = "kobe850829@gmail.com"; // 收件人
		String from = "Spordan2018@gmail.com"; // 寄件人
		String host = "smtp.gmail.com"; // host mail server
		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host); // 設定mail server
		properties.put("mail.smtp.auth", "true"); // 需授權認證
		properties.put("mail.smtp.starttls.enable", "true"); // 使用tls
		properties.put("mail.smtp.starttls.required", "true");
		properties.put("mail.smtp.port", 587); // gmail tls port 587
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
//			properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);

		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("Spordan2018@gmail.com", "SpordanFju");
				// 寄件人帳號密碼授權
			}
		});

//		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("<MessageBook> You got a new message!");

			// 利用template
			VelocityEngine ve = new VelocityEngine();
			// 讓他去找resources folder
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();

			// 指定template位置
			Template template = ve.getTemplate("templates/messageEmailTemplate.vm");

			// 設定該template什麼地方要塞什麼值
			VelocityContext velocityContext = new VelocityContext();
			velocityContext.put("messageBody", messageBody);

			// 將全部的template string做出來
			StringWriter stringWriter = new StringWriter();
			template.merge(velocityContext, stringWriter);

			// 將mail內容塞入所有html內容
			message.setContent(stringWriter.toString(), "text/html; charset=UTF-8");

			Transport.send(message); // 建立連線並送出信件
			logger.info("sent successfully");
//		} catch (MessagingException e) {
//			e.printStackTrace();
//			logger.error(e);
//		}
	}
	
	public void sendMailFirstBank() throws Exception {
		String to = "kobe850829@gmail.com"; // 收件人
		String from = "Spordan2018@gmail.com"; // 寄件人
		String host = "smtp.gmail.com"; // host mail server
		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host); // 設定mail server
		properties.put("mail.smtp.auth", "true"); // 需授權認證
		properties.put("mail.smtp.starttls.enable", "true"); // 使用tls
		properties.put("mail.smtp.starttls.required", "true");
		properties.put("mail.smtp.port", 587); // gmail tls port 587
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.socketFactory", sf);

		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("Spordan2018@gmail.com", "SpordanFju");
				// 寄件人帳號密碼授權
			}
		});

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("轉帳通知");

			// 利用template
			VelocityEngine ve = new VelocityEngine();
			// 讓他去找resources folder
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();

			// 指定template位置 (檔案內有中文，在這邊就需要以utf8做編碼)
			Template template = ve.getTemplate("templates/F0210(N).html", "UTF-8");

			// 設定該template什麼地方要塞什麼值
			VelocityContext velocityContext = new VelocityContext();
//			velocityContext.put("messageBody", messageBody);
			
			//VTL properties impl
//			Hashtable<String,String> req = new Hashtable<String,String>();
//			req.put("memo", "test");
//			req.put("custMailMemo", "haha");
//			velocityContext.put("req", req);
			
			//這邊就透過dataBean來取得資料還有把資料塞進去
			vtlTest vtl = new vtlTest();
			vtl.setPayerAcctNo("11222230293");
			vtl.setPayeeAcctNo("00062313311");
			vtl.setPayeeBank("中華郵政");
			vtl.setMemo("哈哈");
			vtl.setCustMailMemo("歌");
			
//			velocityContext.put("json", vtl);
			velocityContext.put("req", vtl);
			
			//此部分可能就是會去某個地方抓出json，把json放進去，並在template寫getString方法，他就會取得json對應欄位值
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("TrnsSeqNo", "0001");
			jsonObject.put("TxDT", String.valueOf(System.currentTimeMillis()));
			jsonObject.put("TxResult", "Success");
			velocityContext.put("json", jsonObject);
		

			// 將全部的template string做出來
			StringWriter stringWriter = new StringWriter();
			template.merge(velocityContext, stringWriter);

			// 將mail內容塞入所有html內容
			message.setContent(stringWriter.toString(), "text/html; charset=UTF-8");
			logger.debug(stringWriter.toString());

			Transport.send(message); // 建立連線並送出信件
			logger.info("sent successfully");
	}
}

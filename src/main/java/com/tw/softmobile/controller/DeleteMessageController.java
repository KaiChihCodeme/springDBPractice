package com.tw.softmobile.controller;

import java.io.BufferedReader;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tw.softmobile.dao.MessageDao;

@Controller
public class DeleteMessageController {
	@Autowired
	SessionFactory sessionFactory;

	private Logger logger = Logger.getLogger(DeleteMessageController.class);

	@RequestMapping("/DeleteMessageServlet")
	@ResponseBody
	public void deleteMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int getMessageId = -1;
		String res_code;
		try {
			// 讀取POST過來的訊息
			BufferedReader reader = request.getReader();
			String line = reader.readLine();
			logger.debug("DeleteServlet line: " + line.toString());
			// 取request的留言id json
			JSONObject jsonObj = new JSONObject(line);
			getMessageId = jsonObj.getInt("messageId");
			logger.debug("json delete request message id" + getMessageId);// 取得留言板id
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		// 若req取的到messageid才做刪除，否則回傳錯誤碼9000
		if (getMessageId != -1 && !String.valueOf(getMessageId).isEmpty()) {
			// 至資料庫刪除資料
			Session session = sessionFactory.openSession();
			try {
				Transaction tx = session.beginTransaction();
				// 以PK刪除
				MessageDao messageDao = new MessageDao();
				messageDao.deleteMessage(session, getMessageId).executeUpdate();
				tx.commit();
				// 回應已刪除成功，回傳0000
				response.setContentType("text/html;charset=UTF-8");
				res_code = "0000";
				logger.info("delete sucessfully");
			} catch (HibernateException e) {
				e.printStackTrace();
				// 9001:資料庫處理失敗
				res_code = "9001";
				logger.error("ERROR 9001/ " + e);
			}
			session.close();
		} else {
			// 9000:抓不到request
			res_code = "9000";
			logger.error("ERROR 9000");
		}

		// response json
		JSONObject res_json = new JSONObject();
		res_json.put("rc", res_code);
//		String res_json = "{\"rc\":\"" + res_code + "\"}";
		response.getWriter().append(res_json.toString()).flush();
	}
}

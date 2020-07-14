package com.tw.softmobile.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;

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
import com.tw.softmobile.model.MessageModel;

@Controller
public class SubmitMessageController extends BaseController {

	@Autowired
	SessionFactory sessionFactory;

	private Logger logger = Logger.getLogger(SubmitMessageController.class);

	@RequestMapping("/SubmitServlet")
	@ResponseBody
	public void submitMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String getMessage = null;
		String res_code;
		int getMessageBookId = -1;

		try {
			// 讀取POST過來的訊息
			BufferedReader reader = request.getReader();
			String line = reader.readLine();
			logger.debug("line: " + line);
			// 將json內的欄位取出來
			// 取單一用法
			// 取request的留言板id json
			JSONObject jsonObj = new JSONObject(line);
			getMessageBookId = jsonObj.getInt("messageBookId"); // 取得留言板id
			getMessage = jsonObj.getString("messageBody");// 取得傳送留言內容
			logger.debug("json request mes: " + getMessage);
			logger.debug("json request id: " + getMessageBookId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		// 若有抓到req的話，否則錯誤碼9000
		if (!getMessage.isEmpty() && getMessageBookId != -1 && !String.valueOf(getMessageBookId).isEmpty()) {
			Session session = sessionFactory.openSession();

			try {
				Transaction tx = session.beginTransaction();
				MessageDao messageDao = new MessageDao();

				// 設定要上傳的資料
				Timestamp now = new Timestamp(System.currentTimeMillis());
				MessageModel messageModel = null;
				int maxId = messageDao.queryMaxId(session);
				// 如果算出來的maxID不是預設值-1且不是null，代表有抓到東西
				if ((maxId != -1) && !String.valueOf(maxId).isEmpty()) {
					//在裡面才new，並把值塞進去
					messageModel = new MessageModel();
					messageModel.setMessage(getMessage);
					messageModel.setMessageBookId(getMessageBookId);
					messageModel.setTimeStamp(now);
					messageModel.setMessageId(maxId + 1);

					logger.debug("update data: " + getMessage + "/" + getMessageBookId + "/" + now + "/"
							+ (messageDao.queryMaxId(session) + 1));

					//將資料上傳至資料庫
					messageDao.submitMessage(session, tx, messageModel);

					// 上傳成功代碼回應給前端
					response.setContentType("text/html;charset=UTF-8");
					res_code = "0000";
					logger.info("upload successfully");

					// 上傳完成後丟出通知信
					try {
						sendMail(getMessage);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
				} else {
					// 9002:上傳資料欄位有誤(無法取得maxID)
					res_code = "9002";
					logger.error("ERROR 9002");
				}

				session.close();
			} catch (HibernateException e) {
				// 資料庫上傳失敗等問題
				e.printStackTrace();
				// 9001:資料庫錯誤(上傳失敗)
				res_code = "9001";
				logger.error("ERROR 9001/ " + e);
				session.close();
			}
		} else {
			// 9000:request抓取失敗
			res_code = "9000";
			logger.error("ERROR 9000");
		}

		// response json (mail報錯時不會response)
		JSONObject res_json = new JSONObject();
		res_json.put("rc", res_code);
//		String res_json = "{\"rc\":\""+ res_code + "\"}";
		logger.debug(res_json.toString());
		response.getWriter().append(res_json.toString()).flush();
	}
}

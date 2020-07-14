package com.tw.softmobile.controller;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.tw.softmobile.dao.MessageDao;
import com.tw.softmobile.model.MessageModel;

@Controller
public class QueryMessageController extends BaseController{
	@Autowired
	SessionFactory sessionFactory;

	private Logger logger = Logger.getLogger(QueryMessageController.class);

	@RequestMapping("/DataServlet")
	@ResponseBody
	public void queryMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int messageBookNo = -1; // 預設取得留言板id為-1
		String res_code;
		
		//mail test
		try {
//			sendMailFirstBank();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			BufferedReader reader = request.getReader();
			String line = reader.readLine();
			logger.debug("line: " + line.toString());
			// 取單一用法
			// 取request的留言板id json
			JSONObject jsonObj = new JSONObject(line);
			messageBookNo = jsonObj.getInt("messageBookId");
			logger.debug("json request messagebook id" + messageBookNo); // 取得留言板id
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e);
		}

		String query_json = null;
		// 如果沒有抓到messagebookid就沒有必要開連線浪費資源，否則回傳錯誤碼9000
		if (messageBookNo != -1 && !String.valueOf(messageBookNo).isEmpty()) {
			Session session = sessionFactory.openSession();

			// Gson lib 可將object轉換為json string(直接取obj參數為key,參數設定值為value)
			Gson gson = new Gson();
			MessageDao messageDao = new MessageDao();
			List<MessageModel> massage = messageDao.queryMessageToList(session, messageBookNo);
			if (massage != null) {
				query_json = gson.toJson(massage);
				logger.debug(query_json);
				// if (!(query_json.equals("[]")) && !(query_json.isEmpty()) &&
				// query_json!=null) {
				// 設定送出的編碼，避免中文亂碼
				// 將json傳入res
				res_code = "0000";
				logger.info("query sucessfully");
			} else {
				// 傳送錯誤代碼9001:query不到資料或搜尋失敗(資料庫錯誤)
				res_code = "9001";
				logger.error("ERROR 9001");
			}
			// 關閉連線
			session.close();
		} else {
			// 回應代碼9000:request時就抓不到messageBookId(request錯誤)
			res_code = "9000";
			logger.error("ERROR 9000");
		}

		// response json
//		String res_json = "{\"rc\":\""+ res_code + "\",\"body\":"+ query_json + "}";
		JSONObject res_json = new JSONObject();
		res_json.put("rc", res_code);
		res_json.put("body", query_json);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().append(res_json.toString()).flush();
	}
}

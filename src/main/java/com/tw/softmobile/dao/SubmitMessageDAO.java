package com.tw.softmobile.dao;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.tw.softmobile.model.MessageModel;

public class SubmitMessageDAO {
	public static int getMessageId;

	public static int queryMaxId(Session session) {
		String sqlQueryMaxId = "select max(m.messageId) from MessageModel m";
		Query<?> query_max = session.createQuery(sqlQueryMaxId);
		List<?> max_id = query_max.list();
		Iterator<?> iterator = max_id.iterator();
		while (iterator.hasNext()) {
			getMessageId = (int) iterator.next();
		}
		return getMessageId;
	}

	public static MessageModel submitMessage(Session session, 
			String getMessage, int getMessageBookId) {
		// 新增至資料庫
		Timestamp now = new Timestamp(System.currentTimeMillis());
		MessageModel messageModel = new MessageModel();
		messageModel.setMessage(getMessage);
		messageModel.setMessageBookId(getMessageBookId);
		messageModel.setTimeStamp(now);
		messageModel.setMessageId(queryMaxId(session) + 1);

		System.out.println("update data: " 
		+ getMessage + "/" + getMessageBookId + "/" 
						+ now + "/" + queryMaxId(session) + 1);

//		String sqlInsert = "insert into MessageModel(messageBody, messageBookId, timeStamp, messageId) "
//					+ "select messageBody, messageBookId, timeStamp, messageId from MessageModel";
//		Query query_update = session.createQuery(sqlInsert);
		// 本次上傳資料log
		// query_update.executeUpdate();

		return messageModel;
	}
}

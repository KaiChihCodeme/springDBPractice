package com.tw.softmobile.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.tw.softmobile.model.MessageModel;

public class QueryMessageDAO {
	public static List<MessageModel> queryMessageToList(Session session, int messageBookNo) {

		// 搜尋並以時間排序(由舊到新)
		String sqlQuery = "SELECT m.messageBody, m.timeStamp, m.messageId FROM MessageModel m WHERE m.messageBookId=?0"
				+ " ORDER BY m.timeStamp ASC";
		Query<?> query = session.createQuery(sqlQuery);
		query.setParameter(0, messageBookNo);
		List<?> result_list = query.list();
		Iterator<?> iterator = result_list.iterator();

		List<MessageModel> list = new ArrayList<MessageModel>();

		while (iterator.hasNext()) {
			Object[] obj = (Object[]) iterator.next();
			System.out.println(obj[0] + " / " + obj[1] + " / " + obj[2]);

			// 將每項結果加入json
			MessageModel messageOb = new MessageModel();
			messageOb.setMessage(obj[0].toString());
			messageOb.setTimeStamp((Timestamp) obj[1]);
			messageOb.setMessageId((int) obj[2]);
			list.add(messageOb);
		}

		return list;
	}
}

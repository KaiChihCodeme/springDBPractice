package com.tw.softmobile.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.tw.softmobile.model.MessageModel;

public class MessageDao {
	private Logger logger = Logger.getLogger(MessageDao.class);

	// Delete
	public Query deleteMessage(Session session, int getMessageId) {
		String sqlDelete = "delete from MessageModel m where m.messageId=?0";
		Query query = session.createQuery(sqlDelete);
		query.setParameter(0, getMessageId);

		return query;
	}

	// Query all message
	public List<MessageModel> queryMessageToList(Session session, int messageBookNo) {

		// 搜尋並以時間排序(由舊到新)且presentState需為0才可以呈現
		String sqlQuery = "SELECT m.messageBody, m.timeStamp, m.messageId FROM MessageModel m WHERE m.messageBookId=?0 AND m.presentState=0"
				+ " ORDER BY m.timeStamp ASC";
		Query query = session.createQuery(sqlQuery);
		query.setParameter(0, messageBookNo);
		List result_list = query.list();
		List<MessageModel> list = null;
		//若result_list有查詢到東西並轉為List
		if (result_list != null) {
			list = new ArrayList<MessageModel>();
			Iterator iterator = result_list.iterator();

			while (iterator.hasNext()) {	
				Object[] obj = (Object[]) iterator.next();
				logger.debug(obj[0] + " / " + obj[1] + " / " + obj[2]);

				// 將每項結果加入json
				MessageModel messageOb = new MessageModel();
				messageOb.setMessage(obj[0].toString());
				messageOb.setTimeStamp((Timestamp) obj[1]);
				messageOb.setMessageId((int) obj[2]);
				list.add(messageOb);
			}
		}

		return list;
	}

	// Query max id
	public int queryMaxId(Session session) {
		int getMessageId = -1;
		String sqlQueryMaxId = "select max(m.messageId) from MessageModel m";
		Query query_max = session.createQuery(sqlQueryMaxId);
		List max_id = query_max.list();
		Iterator iterator = max_id.iterator();
		while (iterator.hasNext()) {
			getMessageId = (int) iterator.next();
		}
		return getMessageId;
	}

	// Submit
	public void submitMessage(Session session, Transaction tx, MessageModel messageModel) {
		// 新增至資料庫
		session.save(messageModel);
		tx.commit();

//		String sqlInsert = "insert into MessageModel(messageBody, messageBookId, timeStamp, messageId) "
//					+ "select messageBody, messageBookId, timeStamp, messageId from MessageModel";
//		Query query_update = session.createQuery(sqlInsert);
		// 本次上傳資料log
		// query_update.executeUpdate();
	}

	// update present state to 1
	public Query updatePresentState(Session session, int getMessageId) {
		String updateStateHql = "update MessageModel m set m.presentState=1 where m.messageId=?0";
		Query query = session.createQuery(updateStateHql);
		query.setParameter(0, getMessageId);

		return query;
	}

}

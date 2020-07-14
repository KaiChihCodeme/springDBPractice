package com.tw.softmobile.dao;

import javax.persistence.Query;

import org.hibernate.Session;

public class DeleteMessageDAO {
	public static Query deleteMessage(Session session, int getMessageId) {
		String sqlDelete = "delete from MessageModel m where m.messageId=?0";
		Query query = session.createQuery(sqlDelete);
		query.setParameter(0, getMessageId);
		
		return query;
	}
}

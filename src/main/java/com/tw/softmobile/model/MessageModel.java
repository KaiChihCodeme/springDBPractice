package com.tw.softmobile.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//hibernate映射資料庫欄位至java物件
@Entity
@Table(name="message", 
uniqueConstraints = {@UniqueConstraint(columnNames= {"messageId"})})
public class MessageModel {
	@Column(name="messageBody", nullable=false)
	private String messageBody;
	
	@Column(name="timeStamp", nullable=false)
	private Timestamp timeStamp;
	
	@Id
	@Column(name="messageId")
	private int messageId;
	
	@Column(name="messageBookId")
	private int messageBookId;
	
	@Column(name="presentState")
	private int presentState;

	public String getMessage() {
		return messageBody;
	}

	public void setMessage(String messageBody) {
		this.messageBody = messageBody;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public int getMessageId() {
		return messageId;
	}
	
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getMessageBookId() {
		return messageBookId;
	}

	public void setMessageBookId(int messageBookId) {
		this.messageBookId = messageBookId;
	}
	
	public int getPresentState() {
		return presentState;
	}

	public void setPresentState(int presentState) {
		this.presentState = presentState;
	}
}

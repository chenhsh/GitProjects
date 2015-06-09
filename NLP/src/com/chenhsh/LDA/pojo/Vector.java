package com.chenhsh.LDA.pojo;

public class Vector {
	/**
	 * 词的ID
	 */
	private int id;
	
	/**
	 * 这个词所属的主题ID
	 */
	private int topicId;
	
	public Vector(int id, int topicId) {
		this.id = id;
		this.topicId = topicId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	
}

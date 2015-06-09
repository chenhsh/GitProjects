package com.chenhsh.LDA.pojo;

import java.util.ArrayList;
import java.util.List;

public class Doc {
	private String name;
	
	/**
	 * 文档词矩阵
	 * 文档包含的所有词
	 */
	private List<Vector> vectors = null;
	
	/**
	 * 文档中，每个主题包含的词的个数
	 */
	public int[] topicArray = null;
	
	public Doc(String name, int topicNum) {
		this.name = name;
		topicArray = new int[topicNum];
		vectors = new ArrayList<Vector>();
	}
	/**
	 * 文档添加一个词，初始化用
	 * @param vector
	 */
	public void addVector(Vector vector) {
		vectors.add(vector);
		try {
			topicArray[vector.getTopicId()]++;
		} catch(Exception e) {
			System.out.println("");
		}
	}

	/**
	 * 删除一个词的Topic
	 * @param vector
	 */
	public void removeVectorTopic(Vector vector) {
		topicArray[vector.getTopicId()]--;
	}
	
	/**
	 * 更新一个词的Topic
	 * @param vector
	 */
	public void updateVecortTopic(Vector vector) {
		topicArray[vector.getTopicId()]++;
	}

	public String getName() {
		return name;
	}
	public List<Vector> getVectors() {
		return vectors;
	}
	public int[] getTopicArray() {
		return topicArray;
	}
	public void setTopicArray(int[] topicArray) {
		this.topicArray = topicArray;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}

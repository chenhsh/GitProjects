package com.chenhsh.LDA.pojo;


public class Topic {
	
	/**
	 * 当前主题包含，总的词个数
	 */
	private int vCount;
	
	/**
	 * 当前主题，分别包含的词的个数
	 */
	private int[] vectorIdArray = null;
	
	public Topic(int vCount) {
		vectorIdArray = new int[vCount];
	}
	
	public void removeVector(Vector vector){
		this.vCount-- ;
		vectorIdArray[vector.getId()]-- ;
	}

	public void addVector(Vector vector) {
		this.vCount++ ;
		vectorIdArray[vector.getId()]++ ;
	}

	/**
	 * 当前主题包含，总的词个数
	 */
	public int getvCount() {
		return vCount;
	}

	/**
	 * 当前主题，分别包含的词的个数
	 */
	public int[] getVectorIdArray() {
		return vectorIdArray;
	}

	

}

package com.chenhsh.LDA;

import java.util.ArrayList;
import java.util.List;

import com.chenhsh.LDA.pojo.Doc;
import com.chenhsh.LDA.pojo.Vector;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * LDA数据类型，存储LDA中用的数据
 * 
 * @author chenhsh
 *
 */
public class LDA {
	
	//主题数
	private int topicNum;
	
	//总词数
	private int vCount;
	
	//总文档数
	private int dCount;
	
	//文档-词矩阵
	private List<Doc> docs = null;
	
	private BiMap<String, Integer> vectorMap = HashBiMap.create();

	
	public LDA(int topicNum) {
		this.topicNum = topicNum;
		docs = new ArrayList<Doc>();
	}
	
	/**
	 * 添加一篇文档
	 */
	public void addDoc(String name, List<String> words) {
		Doc docObj = new Doc(name, topicNum);
		Integer vId = null;
		int topicId = 0;
		this.dCount++;
		for (String w : words) {
			vId = vectorMap.get(w);
			if (vId == null) {
				vId = vCount;
				vectorMap.put(w, vId);
				vCount++;
			}
			//随机的为词分配一个主题
			topicId = (int) (Math.random() * topicNum);
			
			//文档增加词向量
			docObj.addVector(new Vector(vId, topicId));
		}
		docs.add(docObj);
	}
	
	/**
	 * 词和id双向map
	 */
	public BiMap<String, Integer> getVectorMap() {
		return vectorMap;
	}
	
	/**
	 * 主题数
	 * @return
	 */
	public int getTopicNum() {
		return topicNum;
	}

	/**
	 * 总词数
	 * @return
	 */
	public int getvCount() {
		return vCount;
	}
	
	/**
	 * 总文档数
	 * @return
	 */
	public int getdCount() {
		return dCount;
	}
	
	/**
	 * 文档-词矩阵
	 * @return
	 */
	public List<Doc> getDocs() {
		return docs;
	}
}

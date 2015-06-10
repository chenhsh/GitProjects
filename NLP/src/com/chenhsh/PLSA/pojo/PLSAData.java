package com.chenhsh.PLSA.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class PLSAData {
	//一共有的词项
	private int vCount;
	
	//一共有的文档数量
	private int dCount;
	
	//词 和 id 的双向索引
	private BiMap<String, Integer> vectorMap = HashBiMap.create();
	
	//key：词id；value：总出现的次数
	private Map<Integer, Integer> vectorCountMap = new HashMap<Integer, Integer>();
	
	private List<Doc> docs = new ArrayList<Doc>();
	
	public void addDoc(String name, List<String> words) {
		Doc doc = new Doc(name);
		dCount++;
		for (String w : words) {
			Integer termId = vectorMap.get(w);
			if (termId == null) {
				termId = vCount;
				vCount++;
				vectorMap.put(w, termId);
				vectorCountMap.put(termId, 1);
			} else {
				vectorCountMap.put(termId, vectorCountMap.get(termId) + 1); 				
			}
			doc.addTerm(termId);
		}
		docs.add(doc);
	}

	public int getvCount() {
		return vCount;
	}

	public int getdCount() {
		return dCount;
	}

	public BiMap<String, Integer> getVectorMap() {
		return vectorMap;
	}

	public Map<Integer, Integer> getVectorCountMap() {
		return vectorCountMap;
	}

	public List<Doc> getDocs() {
		return docs;
	}
	
	
}

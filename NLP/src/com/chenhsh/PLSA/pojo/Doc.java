package com.chenhsh.PLSA.pojo;

import java.util.ArrayList;
import java.util.List;

public class Doc {
	//文档名称
	private String name;
	
	//文档中包含的词
	private List<Integer> terms = new ArrayList<Integer>();
	
	public Doc(String name) {
		this.name = name;
	}
	
	public void addTerm(int termId) {
		terms.add(termId);
	}

	public String getName() {
		return name;
	}

	public List<Integer> getTerms() {
		return terms;
	}
	
}

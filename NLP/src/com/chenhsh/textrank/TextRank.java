package com.chenhsh.textrank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.chenhsh.FileUtil;

public class TextRank {
	
	// 阻尼系数，一般取值为0.85
	private static final double d = 0.85;
	
	//最大迭代次数
	private static final int iter = 100;
	
	private static final int wordRange = 5;

	public List<Entry<String, Double>> getKeyWord(String content) {
		List<String> termList = FileUtil.getSegResult(content);
		// 数据预处理
		// 将文本前后5个词，作为投票项
		Map<String, Set<String>> wordsSet = new HashMap<String, Set<String>>();
		
		//每个词的得分
		Map<String, Double> wordsScore = new HashMap<String, Double>();

		for (int i = 0; i < termList.size(); i ++) {
			String w = termList.get(i);
			wordsScore.put(w, 1.0);
			if (!wordsSet.containsKey(w)) {
				wordsSet.put(w, new HashSet<String>());
			}
			
			int iBegin = 0;
			int iEnd = 0;
			//获取当前词的前后5个词
			if (i < wordRange) {
				iBegin = 0;
			} else {
				iBegin = i - wordRange; 
			}
			
			if (i > termList.size() - wordRange) {
				iEnd = termList.size();
			} else {
				iEnd = i + wordRange;
			}
			while (iBegin < iEnd) {
				wordsSet.get(w).add(termList.get(iBegin));
				iBegin++;
			}
		}

		// 训练算法
		//初始化平均每个词的得分
		for (String key : wordsScore.keySet()) {
			wordsScore.put(key, 1.0/wordsScore.size());
		}
		
		//迭代计算每个词的得分
		for (int i = 0; i < iter; i++) {
			System.out.println("迭代次数:"+i);
			Map<String, Double> newWordsScore = new HashMap<String, Double>();
			
			for (Entry<String, Set<String>> wordsEntry : wordsSet.entrySet()) {
				String key = wordsEntry.getKey();
				Set<String> valueSet = wordsEntry.getValue();
				
				//当前key，其他投票的词的总得分
				double totalSocre = 0.0;
				for (String other: valueSet) {
					int size = wordsSet.get(other).size();
					if (key.equals(other) || (size == 0)) {
						continue;
					}
					//other这个词投票的权重
					//一共可以投size个票 * other这个词的总权重
					totalSocre += (1.0 / size) * wordsScore.get(other);
				}
				
				double newScore = (1.0 - d) + d * totalSocre;
				newWordsScore.put(key, newScore);
			}
			wordsScore = newWordsScore;
		}
		

		// 输出结果
		List<Entry<String, Double>> entryList = new ArrayList<Entry<String, Double>>(wordsScore.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
			}
		});

		return entryList;
	}

	public static void main(String[] args) {

		TextRank tr = new TextRank();

		String content = FileUtil.getFileContent("test_data/news1.txt");

		List<Entry<String, Double>> keyWordsList = tr.getKeyWord(content);
		
		System.out.println("关键词:");
		for (Entry<String, Double> w : keyWordsList) {
			System.out.println(w.getKey() + "=" + w.getValue());
		}

	}

}

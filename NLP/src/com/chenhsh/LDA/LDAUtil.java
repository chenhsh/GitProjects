package com.chenhsh.LDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.google.common.base.Charsets;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.io.Files;

/**
 * LDA实现的工具类，主要包含对文本的处理
 * 
 * @author chenhsh
 *
 */
public class LDAUtil {
	
	public static void printModleResult(LDAGibbsModel ldaGibbs) {
		// lda.twords phi[][] K*V
		int topNum = 20;
		double[] scores = null;
		VecotrEntry pollFirst = null;
		for (int i = 0; i < ldaGibbs.getLda().getTopicNum(); i++) {
			System.out.print("\n" + "topic " + i + ":");
			MinMaxPriorityQueue<VecotrEntry> mmp = MinMaxPriorityQueue.create();
			scores = ldaGibbs.getPhi()[i];
			for (int j = 0; j < ldaGibbs.getLda().getvCount(); j++) {
				mmp.add(new VecotrEntry(j, scores[j]));
			}

			for (int j = 0; j < topNum; j++) {
				if (mmp.isEmpty()) {
					break;
				}
				pollFirst = mmp.pollFirst();
				System.out.print(ldaGibbs.getLda().getVectorMap().inverse().get(pollFirst.id) + " ; ");
			}
			
		}
	}	
		
	/**
	 * 从文件中读取数据，并进行分词预处理，加入到lda数据类中
	 * 
	 * @param filename
	 * @param lda
	 */
	public static void loadDataFromFile(String filename, LDA lda) {
		BufferedReader newReader = null;
		try {
			newReader = Files.newReader(new File(filename), Charsets.UTF_8);
			String tempContent =null ;
			int i = 0;
			while((tempContent=newReader.readLine()) != null){
				List<String> words = getSegResult(tempContent);
				lda.addDoc(String.valueOf(++i),words);
				if(i>=1000){
					break ;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				newReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取分词结果
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static List<String> getSegResult(String content) throws IOException {
		BufferedReader br = null;
		List<String> allTerm = null;
		try {
			br = new BufferedReader(new StringReader(content));
			String temp = null;
			allTerm = new ArrayList<String>();
			while ((temp = br.readLine()) != null) {
				List<Term> paser = ToAnalysis.parse(temp);
				for (Term term : paser) {
					if (!filter(term)) {
						allTerm.add(term.getName());
					}
				}
			}
		} finally {
			if(br!=null)
				br.close() ;
		}
		return allTerm;
	}

	
	/**
	 * 词性过滤
	 * 
	 * @param term
	 * @return
	 */
	public static boolean filter(Term term) {
		String natureStr = term.getNatureStr();
		if (natureStr == null || "w".equals(natureStr) || "m".equals(natureStr)) {
			return true;
		}
		if (term.getName().length() == 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * tmd 排序类
	 * 
	 * @author ansj
	 * 
	 */
	static class VecotrEntry implements Comparable<VecotrEntry> {
		int id;
		double score;

		public VecotrEntry(int id, double score) {
			this.id = id;
			this.score = score;
		}

		@Override
		public int compareTo(VecotrEntry o) {
			// TODO Auto-generated method stub
			if (this.score > o.score) {
				return -1;
			} else {
				return 1;
			}
		}

	}
}

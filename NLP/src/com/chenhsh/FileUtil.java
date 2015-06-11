package com.chenhsh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class FileUtil {
	
	public static String getFileContent(String filename) {
		
		BufferedReader br = null;
		StringBuffer contentBuff = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader(new File(filename)));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				contentBuff.append(temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br!=null)
				try {
					br.close() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return contentBuff.toString();	
	}	

	/**
	 * 获取分词结果
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static List<String> getSegResult(String content) {
		BufferedReader br = null;
		List<String> allTerm = null;
		try {
			br = new BufferedReader(new StringReader(content));
			String temp = null;
			allTerm = new ArrayList<String>();
			try {
				while ((temp = br.readLine()) != null) {
					List<Term> paser = ToAnalysis.parse(temp);
					for (Term term : paser) {
						if (!filter(term)) {
							allTerm.add(term.getName());
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if(br!=null)
				try {
					br.close() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

}

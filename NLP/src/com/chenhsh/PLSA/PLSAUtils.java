package com.chenhsh.PLSA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.chenhsh.PLSA.pojo.PLSAData;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class PLSAUtils {
	public static void loadDataFromFile(String filename, PLSAData plsa) {
		BufferedReader newReader = null;
		try {
			newReader = Files.newReader(new File(filename), Charsets.UTF_8);
			String tempContent =null ;
			int i = 0;
			while((tempContent=newReader.readLine()) != null){
				System.out.println("loadfile:"+i);
				List<String> words = getSegResult(tempContent);
				plsa.addDoc(String.valueOf(i),words);
				i++;
				if(i>=100){
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
}
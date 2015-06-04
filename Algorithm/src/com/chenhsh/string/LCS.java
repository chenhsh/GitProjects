package com.chenhsh.string;



/**
 * 两个字符串的最长公共子序列
 * 
 * @author chenhsh
 *
 */
public class LCS {
	public static int getMax(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}
	
	public static void printLCS(int[][] arrb, String str1, String str2, int i,int j) {
		if (i == 1 || j == 1) {
			return ;
		}
		if (arrb[i-1][j-1] == 0) {
			System.out.print(str1.charAt(i-2));
			printLCS(arrb, str1, str2, i - 1, j - 1);
		} else if (arrb[i-1][j-1] == 1) {
			printLCS(arrb, str1, str2, i-1, j);
		} else {
			printLCS(arrb, str1, str2, i, j-1);
		}
	}
	

	
	public static int getLCS(String str1, String str2) {
		if (str1 == null || "".equals(str1)) {
			return 0;
		}
		if (str2 == null || "".equals(str2)) {
			return 0;
		}
		
		int len1 = str1.length();
		int len2 = str2.length();
		
		int[][] arr = new int[len1+1][len2+1];
		int[][] arrb = new int[len1+1][len2+1];
	
		for (int i = 1; i < len1+1; i++) {
			for (int j = 1; j < len2+1; j++) {
				if (str1.charAt(i-1) == str2.charAt(j-1)) {
					arr[i][j] = arr[i-1][j-1] + 1;
					arrb[i][j] = 0;
				} else if (arr[i-1][j] > arr[i][j-1]) {
					arr[i][j] = arr[i-1][j];
					arrb[i][j] = 1;
				} else {
					arr[i][j] = arr[i][j-1];
					arrb[i][j] = -1;
				}
			}
		}
		
		printLCS(arrb, str1, str2, len1+1, len2+1);
		
		
		
		return arr[len1][len2];
	}
	
	public static void main(String[] args) {
		String str1 = "cadfef";
		String str2 = "abcde";
		int maxLen = getLCS(str1, str2);
		System.out.println(maxLen);
	}
}

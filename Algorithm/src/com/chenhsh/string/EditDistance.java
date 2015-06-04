package com.chenhsh.string;

/**
 * 求两个字符串的编辑距离
 * @author chenhsh
 *
 */
public class EditDistance {

	public static int getMin(int i1, int i2, int i3) {
		int min = 0;
		min = i1 < i2 ? i1 : i2;
		min = min < i3 ? min : i3;
		
		return min;
	}
	
	public static int getStringDistance(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		
		if (len1 == 0) {
			return len2;
		}
		if (len2 == 0) {
			return len1;
		}
		
		//初始化数组
		int[][] arr = new int[len1+1][len2+1];
		
		for (int i = 0; i < len1+1; i++) {
			arr[i][0] = i;
		}
		for (int i = 0; i < len2+1; i++) {
			arr[0][i] = i;
		}
		
		//1,循环开始的位置，循环长度，交换的位置
		for (int i = 1; i < len1+1; i++) {
			for (int j = 1; j < len2+1; j++) {
				int temp = str1.charAt(i-1) == str2.charAt(j-1) ? 0 : 1;
				arr[i][j] = getMin(arr[i-1][j] + 1, arr[i][j-1] + 1, arr[i-1][j-1] + temp);
			}
		}
		
		return arr[len1][len2];
	}
	
	public static void main(String[] args) {
		String str1 = "ad";
		String str2 = "bd";
		int dis = getStringDistance(str1, str2);
		System.out.println(dis);
	}
}

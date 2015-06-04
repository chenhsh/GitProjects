package com.chenhsh.search;

public class BinarySearch {
	
	public static int binarySearch(int key, int[] a, int lo, int hi) {
		
		if (lo > hi) {
			return -1;
		}
		
		int mid = (lo + hi) / 2;
		
		if (key > a[mid]) {
			return binarySearch(key, a, mid + 1, hi);
		} else if (key < a[mid]) {
			return binarySearch(key, a, lo, mid - 1);
		} else {
			return mid;
		}
	}
	
	public static void main(String[] args) {
		int[] a = new int[10];
		for(int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		int key = 6;
		int r = 0;
		
		r = binarySearch(key, a, 0, a.length-1);
		System.out.println("query data:" + key);
		System.out.println("index:" + r);
		
		
		
	}
}

package com.chenhsh.sort;

public class QuickSort {
	
	public static void exchange(int[] a, int i, int j) {
		int k = a[i];
		a[i] = a[j];
		a[j] = k;
	}
	
	public static int patition(int[] a, int lo, int hi) {
		//定义扫描指针
		int i = lo;
		int j = hi + 1;
		//根据v划分区间
		int v = a[lo];
		
		while(true) {
			//左扫描，碰到大于分割变量v停止
			while (a[++i] < v) {
				if (i == hi) {
					break;
				}
			}
			
			//从右向做扫描，碰到小于分割变量v停止
			while (a[--j] > v) {
				if (j == lo) {
					break;
				}
			}
			
			//交换左， 右 扫描的指针
			if (i >= j) {
				break;
			}
			
			exchange(a, i, j);
			
		}
		
		// ** 设置划分变量的位置
		exchange(a, lo, j);
		
		return j;
	}
	
	public static void quickSort(int[] a, int lo, int hi) {
		
		if (lo >= hi) {
			return;
		}
		
		int j = patition(a, lo, hi);
		quickSort(a, lo, j-1);
		quickSort(a, j+1, hi);
		
		
	}
	
	public static void main(String[] args) {
		int[] a = {5,6,3,7,2,1};
		
		quickSort(a, 0, a.length - 1);
		
		for (int i : a) {
			System.out.println(i);
		}
		
	}

}

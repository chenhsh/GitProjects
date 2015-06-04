package com.chenhsh.sort;

public class QuickSort {
	
	public static void exchange(int[] a, int i, int j) {
		int k = a[i];
		a[i] = a[j];
		a[j] = k;
	}
	
	public static int patition(int[] a, int lo, int hi) {
		//����ɨ��ָ��
		int i = lo;
		int j = hi + 1;
		//����v��������
		int v = a[lo];
		
		while(true) {
			//��ɨ�裬�������ڷָ����vֹͣ
			while (a[++i] < v) {
				if (i == hi) {
					break;
				}
			}
			
			//��������ɨ�裬����С�ڷָ����vֹͣ
			while (a[--j] > v) {
				if (j == lo) {
					break;
				}
			}
			
			//������ �� ɨ���ָ��
			if (i >= j) {
				break;
			}
			
			exchange(a, i, j);
			
		}
		
		// ** ���û��ֱ�����λ��
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

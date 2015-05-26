package com.chenhsh.hmm;

import java.util.ArrayList;
import java.util.List;

public class Viterbi extends HMM {
	public Viterbi(int stateNum,int observationSymbolNum)
	{
		super(stateNum,observationSymbolNum);
	}
	//ob 已知的观察序列；probability 可能性最大的隐藏状态序列的概率；返回 可能性最大的隐藏状态序列
	public List viterbi(int[] ob, double probability) {
        double[][] delta = null;
        int[][] path = null;
        return viterbi(ob, delta, path, probability);
    }
	
	//delta 输出中间结果，局部最大概率；psi 输出中间结果，反向指针指示最可能路径；返回可能性最大的隐藏状态序列的概率
	public List viterbi(int[] ob, double[][] delta, int[][] path,double probability) {   
		delta = new double[N][ob.length];
		path = new int[N][ob.length];
		
		//1，初始化，第一个观测值状态的初始概率
		for(int i = 0; i < N; i++) {
			delta[i][0] = start_p[i] * emit_p[i][ob[0]];
			path[i][0] = i;
		}
		
		int maxNode = -1;
		
		//2，归纳
		for(int t = 1; t < ob.length; t++) {
			//i是观察序列的值
			int[][] newpath = new int[N][ob.length];
			for(int i = 0; i < N; i++) {
				//j是隐状态
				double prob = -1;
				int state = 0;
				for(int j = 0; j < N; j++) {
					//k 是 隐状态遍历游标
					//nprob 转移到这个隐状态的概率
					double nprob = delta[j][t-1] * trans_p[j][i] * emit_p[i][ob[t]];
					if (nprob > prob) {
						delta[i][t] = nprob;
						path[i][t] = j;
						prob = nprob;
						maxNode = j;
					}
				}
				
			}
		}
		
		int[] maxPath = new int[ob.length];
		
		//3，终止
		double prob = -1;

		//最后一个节点的概率
		for(int i = 0; i < N; i++) {
			if (delta[i][ob.length - 1] > prob) {
				prob = delta[i][ob.length - 1];
				maxPath[ob.length - 1] = i;
			}
		}
		
		//4，路径回溯，找出最可能的路径
		//从maxNode开始回溯
		for(int i = ob.length - 2; i >= 0; i--) {
			maxPath[i] = maxNode;
			maxNode = path[maxNode][i];
			
		}
		
		ArrayList maxList = new ArrayList();
		maxList.add(maxPath);
		
		return maxList;
    }
	
}
















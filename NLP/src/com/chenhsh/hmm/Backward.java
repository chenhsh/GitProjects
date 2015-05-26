package com.chenhsh.hmm;

public class Backward extends HMM {
	
	public Backward(int stateNum,int observationSymbolNum) {
		super(stateNum,observationSymbolNum);
	}	
	
	/**
	 * 向后算法
	 * @param obs
	 * @return
	 */
	public double backward(int[] obs) {
		double[][] beta = null;
		return backward(obs, beta);
	}
	
	public double backward(int[] obs, double[][] beta) {
		beta = new double[obs.length][N];
		
		//1，初始化
		for (int i = 0; i < N; i++) {
			beta[obs.length - 1][i] = 1.0;
		}
		
		
		//2，归纳
		for (int i = obs.length - 2; i >= 0; i--) {
			//i是观察值
			for (int j = 0; j < N; j++) {
				//j是隐状态
				double sum = 0.0;
				for (int k = 0; k < N; k++) {
					//k循环状态转移概率
					sum += trans_p[j][k] * emit_p[k][obs[i+1]] * beta[i + 1][k];
				}
				beta[i][j] = sum;
				
			}
		}
		
		//3，终止
		double probability = 0.0;
		for(int i = 0; i < N; i++) {
			probability += beta[0][i];
		}
		
		return probability;
	}
}

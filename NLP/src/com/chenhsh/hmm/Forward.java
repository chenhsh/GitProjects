package com.chenhsh.hmm;

public class Forward extends HMM{
	
	public Forward(int stateNum,int observationSymbolNum)
	{
		super(stateNum,observationSymbolNum);
		
	}	
	// 根据观测序列，返回观测序列的概率
	public double forward(int[] obs) {
		double[][] alpha = null;
		return forward(obs, alpha);
	}
	
	/**
	 * 
	 * @param obs 已知的观测序列
	 * @param alpha 中间结果，局部概率
	 * @return 返回观察序列的概率
	 */
	public double forward(int[] obs,double[][] alpha) {
		//1，初始化，计算初始时刻所有隐状态的初始概率
		alpha = new double[obs.length][N];
		//第一个观测值，对应各个隐状态初始概率
		for(int i = 0; i < N; i++) {
			alpha[0][i] = start_p[i] * emit_p[i][obs[0]];
		}

		//2，归纳，递归计算每个时间点的局部概率
		
		//从第一个观测值开始循环,i是观察值
		for(int i = 1; i < obs.length; i++) {
			//j是隐状态
			for(int j = 0; j < N; j++) {
				double sum = 0.0;
				for (int k = 0; k < N; k++) {
					sum += alpha[i-1][k] * trans_p[k][j]; 
				}
				
				alpha[i][j] = sum * emit_p[j][obs[i]];
			}
		}
		
		//3,终止，观察序列的概率等于最终时刻所有局部概率之和
		double probability = 0.0;
		for(int i = 0; i < N; i++) {
			probability += alpha[obs.length-1][i];
		}
		
		return probability;
	}
	
}




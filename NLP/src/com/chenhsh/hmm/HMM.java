package com.chenhsh.hmm;

public class HMM {
	// 隐状态
	protected int N;
	// 状态序列数
	protected int M;
	// 状态转移概率
	protected double[][] trans_p;
	// 隐状态表示为显状态的概率
	protected double[][] emit_p;
	// 初始概率（隐状态初始概率分布）
	protected double[] start_p;
	
	public HMM() {}
	
	public HMM(int N, int M) {
		this.N = N;
		this.M = M;
		this.start_p = new double[N];
		this.trans_p = new double[N][N];
		this.emit_p = new double[N][M];
	}
	
}

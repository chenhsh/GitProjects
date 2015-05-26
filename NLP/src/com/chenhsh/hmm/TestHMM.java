package com.chenhsh.hmm;

import java.util.List;


public class TestHMM {
	enum Box {one,two,three };  // 隐藏状态（箱子编号）
    enum Color {red,yellow,blue,green };  // 观察状态（观测到的颜色值）

    public static void main(String[] args) {
    	checkForwardAndBackward();
	}
    
    public static void checkForwardAndBackward() {
    	// 状态转移矩阵
        double[][] A = 
        {
            {0.500, 0.375, 0.125},
            {0.250, 0.125, 0.625},
            {0.250, 0.375, 0.375}
        };

        // 混淆矩阵
        double[][] B = 
        {
            {0.60, 0.20, 0.15, 0.05},
            {0.25, 0.25, 0.25, 0.25},
            {0.05, 0.10, 0.35, 0.50}
        };

        // 初始概率向量
        double[] PI = {0.63,0.17,0.20};
        int[] OB = {Color.red.ordinal(), Color.yellow.ordinal(), Color.blue.ordinal()};
        
        // 初始化HMM模型
        Forward forward = new Forward(A.length, B[0].length);
        forward.start_p = PI;
        forward.trans_p = A;
        forward.emit_p = B;
        
        
        System.out.println("------------前向算法-----------------");
        double probability = forward.forward(OB);
        System.out.println(probability);
        
        Backward backward = new Backward(A.length, B[0].length);
        backward.start_p = PI;
        backward.trans_p = A;
        backward.emit_p = B;
        
        
        System.out.println("------------向后算法-----------------");
        probability = backward.backward(OB);
        System.out.println(probability);
        
        System.out.println("------------维特比算法-----------------");
        
        // 初始化HMM模型
        Viterbi viterbi = new Viterbi(A.length, B[0].length);
        viterbi.trans_p = A;
        viterbi.emit_p = B;
        viterbi.start_p = PI;       
        // 观察序列
        int[] OBViterbi = {Color.red.ordinal(),Color.yellow.ordinal(), Color.blue.ordinal(), Color.yellow.ordinal(),Color.green.ordinal() };
        
        // 找出最有可能的隐藏状态序列
        probability = 0;

        List list=viterbi.viterbi(OBViterbi,probability);
        int[] Q = (int[]) list.get(0);//返回隐藏状态序列
        System.out.print("最可能的隐藏状态序列为：{");
        for(int value:Q)
        {
        	System.out.print(Box.values()[value]+" ");
        }
        System.out.println("}");
        

    }
    
}

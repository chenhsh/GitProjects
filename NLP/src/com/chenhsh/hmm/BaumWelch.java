package com.chenhsh.hmm;

import java.util.ArrayList;


/**
 * 训练HMM模型
 * @author chenhsh
 *
 */
public class BaumWelch {
	
	int N; // 隐藏状态的种数
	int M; // 观察序列的种类
	
    double[] PI = null; // 初始状态概率矩阵
    double[][] A = null; // 状态转移矩阵
    double[][] B = null; // 混淆矩阵

    ArrayList<Integer> observation = new ArrayList<Integer>(); // 观察到的集合
    ArrayList<Integer> state = new ArrayList<Integer>(); // 中间状态集合（隐状态）
    int[] out_seq = { 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1 }; // 测试用的观察序列
//    int[] hidden_seq = { 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1,
//            1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 }; // 测试用的隐藏状态序列
    int T = 32; // out_seq.length() 序列长度为32

    double[][] alpha = null; // 前向变量
    double PO;
    double[][] beta = null; // 后向变量
    
    double[][] gamma = null; //γ
    double[][][] xi = null; //ε 前向后向变量

    // 初始化参数。Baum-Welch得到的是局部最优解，所以初始参数直接影响解的好坏
    public void initParameters() {
        N = 2;
        M = 2;
        PI = new double[N];
        PI[0] = 0.5;
        PI[1] = 0.5;
        A = new double[N][N];
        B = new double[N][M];

        A[0][0] = 0.8125;
        A[0][1] = 0.1875;
        A[1][0] = 0.2;
        A[1][1] = 0.8;
        B[0][0] = 0.875;
        B[0][1] = 0.125;
        B[1][0] = 0.25;
        B[1][1] = 0.75;

        observation.add(1);
        observation.add(2);
        state.add(1);
        state.add(2);

        alpha = new double[T][N];
        beta = new double[T][N];
        gamma = new double[T][N];
    
        xi = new double[T-1][N][N];
        
    }

    // 更新向前变量
    public void updateAlpha() {
        for (int i = 0; i < N; i++) {
            alpha[0][i] = PI[i] * B[i][observation.indexOf(out_seq[0])];
        }
        for (int t = 1; t < T; t++) {
            for (int i = 0; i < N; i++) {
                alpha[t][i] = 0;
                for (int j = 0; j < N; j++) {
                    alpha[t][i] += alpha[t - 1][j] * A[j][i];
                }
                alpha[t][i] *= B[i][observation.indexOf(out_seq[t])];
            }
        }
    }

    // 更新观察序列出现的概率，它在一些公式中当分母
    public void updatePO() {
        for (int i = 0; i < N; i++)
            PO += alpha[T - 1][i];
    }

    // 更新向后变量
    public void updateBeta() {
        for (int i = 0; i < N; i++) {
            beta[T - 1][i] = 1;
        }
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    beta[t][i] += A[i][j]
                            * B[j][observation.indexOf(out_seq[t + 1])]
                            * beta[t + 1][j];
                }
            }
        }
    }

    // 更新xi
    public void updateXi() {
        for (int t = 0; t < T - 1; t++) {
            double frac = 0.0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    frac += alpha[t][i] * A[i][j]
                            * B[j][observation.indexOf(out_seq[t + 1])]
                            * beta[t + 1][j];
                }
            }
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    xi[t][i][j] = alpha[t][i] * A[i][j]
                            * B[j][observation.indexOf(out_seq[t + 1])]
                            * beta[t + 1][j] / frac;
                }
            }
        }
    }

    // 更新gamma
    public void updateGamma() {
        for (int t = 0; t < T - 1; t++) {
            double frac = 0.0;
            for (int i = 0; i < N; i++) {
                frac += alpha[t][i] * beta[t][i];
            }
            // double frac = PO;
            for (int i = 0; i < N; i++) {
                gamma[t][i] = alpha[t][i] * beta[t][i] / frac;
            }
            // for(int i=0;i<N;i++){
            // gamma[t][i]=0;
            // for(int j=0;j<N;j++)
            // gamma[t][i]+=xi[t][i][j];
            // }
        }
    }

    // 更新状态概率矩阵
    public void updatePI() {
        for (int i = 0; i < N; i++)
            PI[i] = gamma[0][i];
    }

    // 更新状态转移矩阵
    public void updateA() {
        for (int i = 0; i < N; i++) {
            double frac = 0.0;
            for (int t = 0; t < T - 1; t++) {
                frac += gamma[t][i];
            }
            for (int j = 0; j < N; j++) {
                double dem = 0.0;
                // for (int t = 0; t < T - 1; t++) {
                // dem += xi[t][i][j];
                // for (int k = 0; k < N; k++)
                // frac += xi[t][i][k];
                // }
                for (int t = 0; t < T - 1; t++) {
                    dem += xi[t][i][j];
                }
                A[i][j] = dem / frac;
            }
        }
    }

    // 更新混淆矩阵
    public void updateB() {
        for (int i = 0; i < N; i++) {
            double frac = 0.0;
            for (int t = 0; t < T; t++)
                frac += gamma[t][i];
            for (int j = 0; j < M; j++) {
                double dem = 0.0;
                for (int t = 0; t < T; t++) {
                    if (out_seq[t] == observation.get(j))
                        dem += gamma[t][i];
                }
                B[i][j] = dem / frac;
            }
        }
    }

    // 运行Baum-Welch算法
    public void run() {
        initParameters();
        int iter = 22; // 迭代次数
        while (iter-- > 0) {
            // E-Step
        	// 更新前向变量
            updateAlpha();
            // updatePO();
            // 更新后向变量
            updateBeta();
            // 更新gamma
            updateGamma();
            // 更新xi
            updateXi();
            
            // M-Step
//          // 更新初始状态概率矩阵，更新Pi算是M的步骤
            updatePI();
            // 更新状态转移矩阵
            updateA();
            // 更新混淆矩阵
            updateB();
        }
    }

    public static void main(String[] args) {
    	BaumWelch bw = new BaumWelch();
        bw.run();
        System.out.println("训练后的初始状态概率矩阵：");
        for (int i = 0; i < bw.N; i++)
            System.out.print(bw.PI[i] + "\t");
        System.out.println();
        System.out.println("训练后的状态转移矩阵：");
        for (int i = 0; i < bw.N; i++) {
            for (int j = 0; j < bw.N; j++) {
                System.out.print(bw.A[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("训练后的混淆矩阵：");
        for (int i = 0; i < bw.N; i++) {
            for (int j = 0; j < bw.M; j++) {
                System.out.print(bw.B[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
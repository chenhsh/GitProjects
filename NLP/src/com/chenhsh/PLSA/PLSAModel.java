package com.chenhsh.PLSA;

import java.util.Random;

import com.chenhsh.PLSA.pojo.Doc;
import com.chenhsh.PLSA.pojo.PLSAData;

public class PLSAModel {
	
	//plsa数据类
	private PLSAData plsaData;

	//主题数量
	private int topicNum;
	
	//迭代次数
	private int iteration;
	
	//dCount * vCount
	private int[][] docTermMatrix; // docTermMatrix
    
	//dCount * topicNum
    private double[][] docTopicProbs;//p(z|d)

    //topicNum * vCount
    private double[][] topicTermProbs;//p(w|z)

    //dCount * vCount * topic
    private double[][][] docTermTopicPros;//p(z|d,w)

	
	
	public PLSAModel(PLSAData plsaData, int topicNum, int iteration) {
		this.plsaData = plsaData;
		this.topicNum = topicNum;
		this.iteration = iteration;
		docTermMatrix = new int[plsaData.getdCount()][plsaData.getvCount()];
		docTopicProbs = new double[plsaData.getdCount()][topicNum];
		topicTermProbs = new double[topicNum][plsaData.getvCount()];
		docTermTopicPros = new double[plsaData.getdCount()][plsaData.getvCount()][topicNum];
	}
	
	public void trainModel() {
		System.out.println("初始化模型参数");
		//初始化模型参数
		initParameter();
		
		for (int i = 0; i < iteration; i++) {
			//E-Step
			estimationParameter();
			
			//M-Step
			maximumParameter();
			
			//计算最大似然函数，结果
			double l = computeLogLikelihood();
			System.out.println("iteration: " + i + " : " + l);
		}
	}
	
	/**
	 * 最大化参数估计
	 * 
	 * update
	 * p(w|z),p(w|z)=sum(n(d',w)*p(z|d',w))/sum(sum(n(d',w')*p(z|d',w')))
	 *
	 * d' represent all documents 
	 * w' represent all vocabularies update
	 *
	 * p(z|d),p(z|d)=sum(n(d,w')*p(z|d,w'))/sum(sum(n(d,w')*p(z'|d,w')))
	 * w' represent all vocabularies 
	 * z' represnet all topics
	 * 
	 */
	public void maximumParameter() {
		//update: p(w|z)
		for (int k = 0; k < topicNum; k++) {
			double totalDenominator = 0.0;
			for (int v = 0; v < plsaData.getvCount(); v++) {
				double numerator = 0.0;
				for (int d = 0; d < plsaData.getdCount(); d++) {
					numerator += docTermMatrix[d][v] * docTermTopicPros[d][v][k];
				}
				topicTermProbs[k][v] = numerator;
				totalDenominator += numerator;
			}
			if (totalDenominator == 0.0) {
				System.out.println("total zero");
                totalDenominator = avoidZero(totalDenominator);
            }
			for (int v = 0; v <plsaData.getvCount(); v++) {
				topicTermProbs[k][v] = topicTermProbs[k][v] / totalDenominator;
			}
		}
		
		//update：p(z|d)
		for (int d = 0; d < plsaData.getdCount(); d++) {
			double totalDenominator = 0.0;
			for (int k = 0; k < topicNum; k++) {
				double numerator = 0.0;
				for (int v = 0; v < plsaData.getvCount(); v++) {
					numerator += docTermMatrix[d][v] * docTermTopicPros[d][v][k];
				}
				docTopicProbs[d][k] = numerator;
				totalDenominator += numerator;
			}
			if (totalDenominator == 0.0) {
				System.out.println("total zero");
                totalDenominator = avoidZero(totalDenominator);
            }
			for (int k = 0; k < topicNum; k++) {
				docTopicProbs[d][k] = docTopicProbs[d][k] / totalDenominator;
			}
		}
	}
	
	/**
	 * 更新估计参数，P(z|w,d)
	 * 
	 * p(z|d,w)=p(z|d)*p(w|z)/sum(p(z'|d)*p(w|z'))
	 * 
     * z' represent all posible topic
     * 
	 */
	public void estimationParameter() {
		for (int d = 0; d < plsaData.getdCount(); d++) {
			for (int v = 0; v < plsaData.getvCount(); v++) {
				double total = 0.0;
				double[] preTopicProb = new double[topicNum];
				
				//已知文档d和词v时，观察到的主题k概率
				for (int k = 0; k < topicNum; k++) {
					double numerator = docTopicProbs[d][k] * topicTermProbs[k][v];
					total += numerator;
					preTopicProb[k] = numerator;
				}
				if (total == 0.0) {
					total = avoidZero(total);
					System.out.println("total zero");
				}
				for (int k = 0; k < topicNum; k++) {
					docTermTopicPros[d][v][k] = preTopicProb[k] / total;
				}
			}
		}
	}
	
	/**
	 * 计算最大似然估计值
	 * 
	 */
	public double computeLogLikelihood() {
		double L = 0.0;
		for (int d = 0; d < plsaData.getdCount(); d++) {
			for (int v = 0; v < plsaData.getvCount(); v++) {
				int nSize = docTermMatrix[d][v];
				double sumK = 0.0;
				for (int k = 0; k < topicNum; k++) {
					sumK += docTermTopicPros[d][v][k] * Math.log10(topicTermProbs[k][v] * docTopicProbs[d][k]);
				}
				L += nSize * sumK;
			}
		}
		return L;
		

//		double L = 0.0;
//		for (int d = 0; d < plsaData.getdCount(); d++) {
//			int docTermSize = plsaData.getDocs().get(d).getTerms().size();
//			double sumV = 0.0;
//			for (int v = 0; v < plsaData.getvCount(); v++) {
//				double sumK = 0.0;
//				for (int k = 0; k < topicNum; k++) {
//					sumK += docTopicProbs[d][k] * topicTermProbs[k][v];
//				}
//				sumV += ((double) docTermMatrix[d][v] / docTermSize) * Math.log10(sumK); 
//			}
//			L += docTermSize * sumV;
//		}
//		return L;
	}
	
	/**
	 * 初始化模型参数
	 */
	public void initParameter() {
		//初始化 docTermMatrix
		for (int d = 0; d < plsaData.getdCount(); d++) {
			Doc doc = plsaData.getDocs().get(d);
			for (int v = 0; v < doc.getTerms().size(); v++) {
				int termIndex = doc.getTerms().get(v);
				docTermMatrix[d][termIndex] += 1;
			}
		}
		
		//初始化 docTopicProbs
		for (int d = 0; d < plsaData.getdCount(); d++) {
			double[] tempProbs = randomProbilities(topicNum);
			for (int k = 0; k < topicNum; k++) {
				docTopicProbs[d][k] = tempProbs[k];
			}
 		}
		
		//初始化 topicTermProbs
		for (int k = 0; k < topicNum; k++) {
			double[] tempProbs = randomProbilities(plsaData.getvCount());
			for (int v = 0; v < plsaData.getvCount(); v++) {
				topicTermProbs[k][v] = tempProbs[v];
			}
		}
	}
	
	/**
	 * 随机生成一组概率，保证之和为1
	 * @param n
	 * @return
	 */
	public double[] randomProbilities(int n) {
		double[] probs = new double[n];
		double total = 0.0;
		Random r = new Random();
		for (int i = 0; i < n; i++) {
			probs[i] = r.nextInt(n) + 1;
			total += probs[i];
		}
		for (int i = 0; i < n; i++) {
			probs[i] = probs[i] / total;
		}
		return probs;
	}
	
    /**
     * 
     * avoid zero number.if input number is zero, we will return a magic
     * number.
     * 
     * 
     */
    private final static double MAGICNUM = 0.0000000000000001;

    public double avoidZero(double num) {
        if (num == 0.0) {
            return MAGICNUM;
        }

        return num;
    }

	public PLSAData getPlsaData() {
		return plsaData;
	}

	public int getTopicNum() {
		return topicNum;
	}

	public int getIteration() {
		return iteration;
	}

	public int[][] getDocTermMatrix() {
		return docTermMatrix;
	}


	public double[][] getDocTopicProbs() {
		return docTopicProbs;
	}

	public double[][] getTopicTermProbs() {
		return topicTermProbs;
	}

	public double[][][] getDocTermTopicPros() {
		return docTermTopicPros;
	}
	
}

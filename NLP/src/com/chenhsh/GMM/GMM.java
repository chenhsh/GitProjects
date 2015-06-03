package com.chenhsh.GMM;

import java.util.ArrayList;
import java.util.List;

public class GMM {
	// GMM:高斯混合模型
	/*
	 * http://czhsuccess.iteye.com/blog/1899582
	 * GMM算发步骤： 
	 * 1. 初始化参数，包括Gauss分布个数、均值、协方差； 
	 * 2. 计算每个节点属于每个分布的概率； 
	 * 3. 计算每个分布产生每个节点的概率； 
	 * 4. 更新每个分布的权值，均值和它们的协方差。
	 */

	/**
	 * GMM聚类算法实现，返回每条数据的类别(0 ~ k-1)
	 * 
	 * @param dataSet
	 * @param pMiu
	 * @param dataNum
	 * @param dataDimen
	 * @return
	 */
	public int[] GMMCluster(ArrayList<ArrayList<Double>> dataSet,
			ArrayList<ArrayList<Double>> pMiu, int dataNum, int k, int dataDim) {
		Parameter parameter = iniParameters(dataSet, dataNum, k, dataDim);
		
		//记录上一次聚类的误差
		double Lpre = -1000000;
		//停止迭代阈值
		double threshold = 0.0001;
		
		while(true) {
			double L = 0.0;
			//E-Step
			//根据初始化的节点，计算每条数据属于每个分布的概率
			ArrayList<ArrayList<Double>> px = computerProbablity(dataSet, pMiu, dataNum, k, dataDim);
			
			double[][] pGama = new double[dataNum][k];
			for(int i = 0; i < dataNum; i++) {
				for(int j = 0; j < k; j++) {
					pGama[i][j] = px.get(i).get(j) * parameter.getpPi().get(j);
				}
			}
			
			double[] sumPGama = GMMUtil.matrixSum(pGama, 2);
			for(int i = 0; i < dataNum; i++) {
				for(int j = 0; j < k; j++) {
					pGama[i][j] = pGama[i][j] / sumPGama[i];
				}
			}
			
			
			//M-Step
			
			//第k个高斯生成每个样本的概率的和，所有Nk的总和为N
			double[] NK = GMMUtil.matrixSum(pGama, 1);
			
			
			//更新pMiu
			//NK的倒数NKReciprocal
			double[] NKReciprocal = new double[NK.length];
			for(int i = 0; i < NK.length; i++) {
				NKReciprocal[i] = 1 / NK[i];
			}
			
			//缓存迭代的pMiu
			double[][] pMiutmp = GMMUtil.matrixMultiply(GMMUtil.matrixMultiply(GMMUtil.diag(NKReciprocal), GMMUtil.matrixReverse(pGama)), GMMUtil.listToArray(dataSet));
			
			
			//更新pPie
			double[][] pPie = new double[k][1];
			for(int i = 0; i < dataNum; i++) {
				pPie[i][0] = NK[i] / dataNum;
			}
			
			//更新k个pSigma
			double[][][] pSigmaTmp = new double[k][dataNum][dataDim];
			for(int i = 0; i < k; i++) {
				double[][] xShift = new double[dataNum][dataDim];
				for(int j = 0; j < dataNum; j++) {
					for(int l = 0; l < dataDim; l++) {
						xShift[j][l] = pMiutmp[i][l];
					}
				}
				
				//第k条pGama值
				double[] pGamaK = new double[dataNum];
				for(int j = 0; j < dataNum; j++) {
					pGamaK[j] = pGama[j][i];
				}
				
				double[][] diagPGamaK = GMMUtil.diag(pGamaK);
				
				double[][] pSigmaK = GMMUtil.matrixMultiply(GMMUtil.matrixReverse(xShift), GMMUtil.matrixMultiply(diagPGamaK, xShift));
				
				for(int j = 0; j < dataNum; j++) {
					for(int l = 0; l < dataDim; l++) {
						pSigmaTmp[i][j][l] = pSigmaK[j][l] / NK[i];
					}
				}
			}// for i(k个类型,中心节点)
			
			
			double[][] a = GMMUtil.matrixMultiply(GMMUtil.listToArray(px), pPie);
			
			for(int i = 0; i < dataNum; i++) {
				a[i][0] = Math.log(a[i][0]);
			}
			L = GMMUtil.matrixSum(a, 1)[0];
			
			//判断迭代是否结束
			if (L - Lpre < threshold) {
				break;
			}
			Lpre = L;
		}
		
		
		
		return null;
	}

	/**
	 * 计算每个节点(n个节点)，属于每个分布(k个分布)的概率
	 * 
	 * @param dataSet
	 * @param pMiu 中心点
	 * @param dataNum
	 * @param dataDimen
	 * @return
	 */
	public ArrayList<ArrayList<Double>> computerProbablity (
			ArrayList<ArrayList<Double>> dataSet,
			ArrayList<ArrayList<Double>> pMiu, int dataNum, int k, int dataDim) {
		//每条数据属于每个类型的概率
		double[][] px = new double[dataNum][k];
		int[] type = getTypes(dataSet, pMiu, k, dataNum);
		
		//计算k个分布的协方差矩阵
		ArrayList<ArrayList<ArrayList<Double>>> covList = computeCovSigma(dataSet, type, dataNum, k, dataDim);
		
		//计算每条数据属于每个分布的概率
		// Gaussian posterior probability   
	    // N(x|pMiu,pSigma) = 1/((2pi)^(D/2))*(1/(abs(sigma))^0.5)*exp(-1/2*(x-pMiu)'pSigma^(-1)*(x-pMiu))  
	    
		for(int i = 0; i < dataNum; i++) {
			for(int j = 0; j < k; j++) {
				ArrayList<Double> offset = GMMUtil.matrixMinus(dataSet.get(i), pMiu.get(j));
				
				ArrayList<ArrayList<Double>> invSigma = covList.get(j);
				//offset * invSigm : 返回每行之和
				//(x-pMiu)'pSigma^(-1)*(x-pMiu)
				double[] temp = GMMUtil.matrixSum(GMMUtil.matrixMultiply(GMMUtil.sigleListToArray(offset), GMMUtil.listToArray(invSigma)), 2);
				
				//1/((2pi)^(D/2))*(1/(abs(sigma))^0.5)
				double coef = Math.pow((2 * Math.PI), -(double)dataDim / 2.0) * Math.sqrt(GMMUtil.computeDet(GMMUtil.listToArray(invSigma), invSigma.size()));

				px[i][j] = coef * Math.pow(Math.E, -0.5 * temp[0]);
				
			}
		}
		
		return GMMUtil.arrayToList(px);
	}

	/**
	 * 
	 * @Title: iniParameters
	 * @Description: 初始化参数Parameter
	 * @return Parameter
	 * @throws
	 */
	public Parameter iniParameters(ArrayList<ArrayList<Double>> dataSet,
			int dataNum, int k, int dataDim) {
		Parameter res = new Parameter();
		ArrayList<ArrayList<Double>> pMiu = generateCentroids(dataSet, dataNum, k);
		res.setpMiu(pMiu);
		
		//计算每个样本与每个中心节点的距离，以此为据对样本节点进行分类计算，进而初始化k个分布的权值
		ArrayList<Double> pPi = new ArrayList<Double>();
		//所有样本点的初始分类
		int[] type = getTypes(dataSet, pMiu, k, dataNum);
		//计算每个类别的个数
		int[] typeNum = new int[k];
		for(int i = 0; i < dataNum; i++) {
			typeNum[type[i]]++;
		}
		
		//初始化pPi权值(k个GMM的权值)
		for(int i = 0; i < k; i++) {
			pPi.add((double)(typeNum[i]) / (double)dataNum);
		}
		res.setpPi(pPi);
		
		//计算k个分布的k个协方差
		ArrayList<ArrayList<ArrayList<Double>>> pSigma = computeCovSigma(dataSet, typeNum, dataNum, k, dataDim);		
		res.setpSigma(pSigma);
		
		return res;
	}
	
	/**
	 * 计算k个分布的k个协方差
	 * @param dataSet
	 * @param type
	 * @param dataNum
	 * @param k
	 * @param dataDim
	 * @return
	 */
	public ArrayList<ArrayList<ArrayList<Double>>> computeCovSigma(ArrayList<ArrayList<Double>> dataSet, int[] type, int dataNum, int k, int dataDim) {
		ArrayList<ArrayList<ArrayList<Double>>> pSigma = new ArrayList<ArrayList<ArrayList<Double>>>();
		for(int i = 0; i < k; i++) {
			ArrayList<ArrayList<Double>> tmp = new ArrayList<ArrayList<Double>>();
			//计算同一个分类下的协方差
			for(int j = 0; j < dataNum; j++) {
				if (type[j] == i) {
					tmp.add(dataSet.get(j));
				}
			}
			pSigma.add(GMMUtil.computeCov(tmp, dataDim, tmp.size()));
		}
		return pSigma;
	}


	/**
	 * 
	 * @Title: getTypes
	 * @Description: 返回每条数据的类别
	 * @return int[]
	 * @throws
	 */
	public int[] getTypes(ArrayList<ArrayList<Double>> dataSet,
			ArrayList<ArrayList<Double>> pMiu, int k, int dataNum) {
		int[] type = new int[dataNum];
		for(int i = 0; i < dataNum; i++) {
			double minDistance = GMMUtil.computeDistance(dataSet.get(i), pMiu.get(0));
			
			//初始0 作为这个数据的类别
			type[i] = 0;
			for(int j = 1; j < k; j++) {
				if (GMMUtil.computeDistance(dataSet.get(i), pMiu.get(j)) < minDistance) {
					minDistance = GMMUtil.computeDistance(dataSet.get(i), pMiu.get(j));
					type[i] = j;
				}
			}
		}
		return type;
	}

	/**
	 *  获取随机的k个中心点
	 * @param data 数据集
	 * @param dataNum 数据集大小
	 * @param k 选取k个中心点
	 * @return
	 */
	public ArrayList<ArrayList<Double>> generateCentroids(
			ArrayList<ArrayList<Double>> data, int dataNum, int k) {
		ArrayList<ArrayList<Double>> res = null;
		if (dataNum < k) {
			return res;
		} else {
			res = new ArrayList<ArrayList<Double>>();
			
			List<Integer> randomList = new ArrayList<Integer>();
			while (k > 0) {
				int index = (int)(Math.random() * dataNum);
				if (!randomList.contains(index)) {
					randomList.add(index);
					k--;
					res.add(data.get(index));
				}
			}
		}
		return res;
	}


}

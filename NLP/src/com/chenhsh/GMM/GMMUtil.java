package com.chenhsh.GMM;

import java.util.ArrayList;

public class GMMUtil {
	/**
	 * 计算任意两个节点间的距离 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double computeDistance(ArrayList<Double> d1, ArrayList<Double> d2) {
		double squareSum = 0.0;
		for (int i = 0; i < d1.size(); i++) {
			squareSum += (d1.get(i) - d2.get(i)) * (d1.get(i) - d2.get(i));
		}
		return Math.sqrt(squareSum);
	}
	
	/**
	 * 计算协方差矩阵
	 * @param dataSet
	 * @param dataDimen
	 * @param dataNum
	 * @return
	 */
	public static ArrayList<ArrayList<Double>> computeCov(ArrayList<ArrayList<Double>> dataSet, int dataDimen, int dataNum) {
		ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();
		
		//计算每一维的均值
		double[] sum = new double[dataDimen];
		for(ArrayList<Double> data : dataSet) {
			for(int i = 0; i < dataDimen; i++) {
				sum[i] += data.get(i);
			}
		}
		
		for(int i = 0; i < dataDimen; i++) {
			sum[i] = sum[i] / dataDimen;
		}
		
		//计算两组数据的协方差
		for(int i = 0; i < dataDimen; i++) {
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int j = 0; j < dataDimen; j++) {
				double cov = 0.0;
				for(ArrayList<Double> data : dataSet) {
					cov += (data.get(i) - sum[i]) * (data.get(j) - sum[j]); 
				}
				temp.add(cov);
			}
			res.add(temp);
		}
		return res;
	}
	
	/**
	 * 计算矩阵的逆矩阵
	 * 
	 * @param dataSet
	 * @return
	 */
	public static double[][] computeInv(ArrayList<ArrayList<Double>> dataSet) {
		int dataDim = dataSet.size();
		double[][] res = new double[dataDim][dataDim];
		
		//将list转为array
		double[][] a = listToArray(dataSet);
		
		//计算伴随矩阵
		double detA = computeDet(a, dataDim);
		for (int i = 0; i < dataDim; i++) {
			for (int j = 0; j < dataDim; j++) {
				double num;
				if ((i + j)  % 2 == 0) {
					num = computeDet(computeAC(a, i + 1, j + 1), dataDim - 1);
				} else {
					num = - computeDet(computeAC(a, i + 1, j + 1), dataDim - 1);
				}
				res[j][j] = num / detA;
			}
		}
		
		return res;
	}
	
	/**
	 * 两个矩阵相乘
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[][] matrixMultiply(double[][] a, double[][] b) {
		double[][] res = new double[a.length][b[0].length];
		//todo 检查两个矩阵是否可以相乘
		
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				for (int k = 0; k < a[0].length; i++) {
					res[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		return res;
	}
	
	/**
	 * 矩阵的点乘，即对应元素相乘
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[][] dotMatrixMultiply(double[][] a, double[][] b) {
		double[][] res = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				res[i][j] = a[i][j] * b[i][j];
			}
		}
		return res;
	}
	
	/**
	 * 矩阵的点除
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[][] dotMatrixDivide(double[][] a, double[][] b) {
		double[][] res = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				res[i][j] = a[i][j] / b[i][j];
			}
		}

		return res;
	}
	
	/**
	 * 
	 * @Title: matrixSum
	 * @Description: 返回矩阵每行之和(mark==2)或每列之和(mark==1)
	 * @return ArrayList<Double>
	 * @throws
	 */
	public static double[] matrixSum(double[][] a, int mark) {
		double res[] = new double[a.length];
		if (mark == 1) { // 计算每列之和，返回行向量
			res = new double[a[0].length];
			for (int i = 0; i < a[0].length; i++) {
				for (int j = 0; j < a.length; j++) {
					res[i] += a[j][i];
				}
			}
		} else if (mark == 2) { // 计算每行之和， 返回列向量
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < a[0].length; j++) {
					res[i] += a[i][j];
				}
			}

		}
		return res;
	}
	
	/**
	 * 
	 * @Title: matrixReverse
	 * @Description: 矩阵专制
	 * @return double[][]
	 * @throws
	 */
	public static double[][] matrixReverse(double[][] a) {
		double[][] res = new double[a[0].length][a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				res[j][i] = a[i][j];
			}
		}
		return res;
	}

    /** 
     *  
    * @Title: diag  
    * @Description: 向量对角化 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] diag(double[] a) {  
        double[][] res = new double[a.length][a.length];  
        for(int i = 0; i < a.length; i++) {  
            res[i][i] = a[i];  
        }  
        return res;  
    }  

    
	/**
	 * 计算行列式
	 * @param dataSet
	 * @param dataDim
	 * @return
	 */
	public static double computeDet(double[][] a, int dataDim) {
		double res = 0.0;
		if (dataDim == 2) {
			res = a[0][0] * a[1][1] - a[0][1] * a[1][0];
		}
		
		for (int i = 0; i < dataDim; i++) {
			if (i % 2 == 0) {
				res += a[0][1] * computeDet(computeAC(a, 1, i + 1), dataDim - 1);
			} else {
				res += -a[0][1] * computeDet(computeAC(a, 1, i + 1), dataDim - 1);
			}
		}
		
		return res;
	}
	
	/**
	 * 求制定行列式的代数余子式
	 * @param dataSet
	 * @param r 行号
	 * @param c 列号
	 * @return
	 */
	public static double[][] computeAC(double[][] dataSet, int r, int c) {
		int H = dataSet.length;
		int V = dataSet[0].length;
		
		double[][] newData = new double[H - 1][V - 1];
		for (int i = 0; i < newData.length; i++) {
			if (i < r - 1) {
				for(int j = 0; j < newData[i].length; j++) {
					if (j < c - 1) {
						newData[i][j] = dataSet[i][j];
					} else {
						newData[i][j] = dataSet[i][j + 1];
					}
				}
			} else {
				for (int j = 0; j < newData[i].length; j++) {
					if (j < c - 1) {
						newData[i][j] = dataSet[i+1][j];
					} else {
						newData[i][j] = dataSet[i+1][j+1];
					}
				}
				
			}
		}
		
		return newData;
		
	}
	
	/**
	 * 将ArrayList 转为 矩阵形式
	 * @param dataSet
	 * @return
	 */
	public static double[][] listToArray(ArrayList<ArrayList<Double>> dataSet) {
		int dataDim = dataSet.size();
		double[][] rs = new double[dataDim][dataDim];
		for(int i = 0; i < dataDim; i++) {
			for(int j = 0; j < dataDim; j++) {
				rs[i][j] = dataSet.get(i).get(j);
			}
		}
		return rs;
	}
	
	/**
	 * 一个向量（一列数据） 转为 矩阵形式
	 * @param dataSet
	 * @return
	 */
	public static double[][] sigleListToArray(ArrayList<Double> dataSet) {
		int dataDim = dataSet.size();
		double[][] rs = new double[1][dataDim];
		for(int i = 0; i < dataDim; i++) {
			rs[0][i] = dataSet.get(i);
		}
		return rs;
	}
	/** 
    *  
    * @Title: toList  
    * @Description: 将array转化成list 
    * @return ArrayList<ArrayList<Double>> 
    * @throws 
    */  
    public static ArrayList<ArrayList<Double>> arrayToList(double[][] a) {  
        ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();  
        for(int i = 0; i < a.length; i++) {  
            ArrayList<Double> tmp = new ArrayList<Double>();  
            for(int j = 0; j < a[i].length; j++) {  
                tmp.add(a[i][j]);  
            }  
            res.add(tmp);  
        }  
        return res;  
    }  
    
    /** 
     *  
    * @Title: matrixMinux  
    * @Description: 计算集合只差 
    * @return ArrayList<ArrayList<Double>> 
    * @throws 
     */  
    public static ArrayList<Double> matrixMinus(ArrayList<Double> a1, ArrayList<Double> a2) {  
        ArrayList<Double> res = new ArrayList<Double>();  
        for(int i = 0; i < a1.size(); i++) {  
            res.add(a1.get(i) - a2.get(i));  
        }  
        return res;  
    }  


}

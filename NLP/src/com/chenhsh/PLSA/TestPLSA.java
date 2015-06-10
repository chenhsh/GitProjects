package com.chenhsh.PLSA;

import com.chenhsh.PLSA.pojo.PLSAData;

public class TestPLSA {
	public static void main(String[] args) {
		System.out.println("加载数据");
		//1，加载训练的文档
		PLSAData plsaData = new PLSAData();
		PLSAUtils.loadDataFromFile("test_data/sample.txt", plsaData, 10);
		
		//2，训练模型
		System.out.println("创建训练模型对象");
		PLSAModel plsaModel = new PLSAModel(plsaData, 5, 30);
		System.out.println("训练模型");
		plsaModel.trainModel();
		
		//3，保存模型训练结果
		// TODO: 保存 topicTermProbs
		
	}
}

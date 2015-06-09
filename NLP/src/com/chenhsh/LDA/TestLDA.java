package com.chenhsh.LDA;


/**
 * 参考ansj_fast_lda实现的LDA模型
 * 
 * 1，是学习LDA算法
 * 2，是对原实现结构进行一些调整，更清晰一点
 * 
 * @author chenhsh
 *
 */
public class TestLDA {
	public static void main(String[] args) {
		//初始化参数
		int topicNum = 20;
		double alpha = (double)50 / topicNum;
		double beta = 0.1;
		int iteration = 10;
		int burninNum = 20;
		
		System.out.println("初始化LDA");
		//初始化设置主题20
		LDA lda = new LDA(topicNum);
		
		//1，加载测试文档数据
		System.out.println("加载数据");
		LDAUtil.loadDataFromFile("test_data/sample.txt", lda);
		
		//2，训练LDA模型
		System.out.println("训练模型");
		LDAGibbsModel ldaGibbs = new LDAGibbsModel(lda, alpha, beta, iteration, burninNum);
		
		ldaGibbs.trainModel();
		
		//3，保存模型
		System.out.println("保存模型结果");
		LDAUtil.printModleResult(ldaGibbs);

	}
}

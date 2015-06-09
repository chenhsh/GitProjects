package com.chenhsh.LDA;



import com.chenhsh.LDA.pojo.Doc;
import com.chenhsh.LDA.pojo.Topic;
import com.chenhsh.LDA.pojo.Vector;

/**
 * LDA算法模型类，实现LDA中用到的算法
 * 
 * @author chenhsh
 *
 */
public class LDAGibbsModel {
	
	private LDA lda;

	private double alpha = 0.5;

	private double beta = 0.1;
	/**
	 * 主题-词
	 */
	private double[][] phi = null;
	
	/**
	 * 文档-主题
	 */
	private double[][] theta = null;
	
	/**
	 * 迭代次数
	 * E-M参数估计的迭代次数
	 */
	private int iteration = 20;
	
	private int burninNum = 100;

	/**
	 * 主题-词矩阵
	 */
	private Topic[] topics = null;
	
	/**
	 * 初始化LDA训练模型，参数
	 * 
	 * @param lda  LDA模型数据
	 * @param alpha 
	 * @param beta
	 * @param iteration 迭代次数
	 */
	public LDAGibbsModel(LDA lda,double alpha, double beta, int iteration, int burninNum) {
		this.lda = lda;
		this.alpha = alpha;
		this.beta = beta;
		this.iteration = iteration;
		this.burninNum = burninNum;
		this.topics = new Topic[lda.getTopicNum()];
		phi = new double[lda.getTopicNum()][lda.getvCount()];
		theta = new double[lda.getdCount()][lda.getTopicNum()];
	}
	
	/**
	 * 训练模型
	 */
	public void  trainModel() {
		//初始化模型参数
		initModelParam();
		for (int i = 0; i < iteration; i++) {
			System.out.println("iteration:" + i);
			//MCMC中的burn-in 的过程
			//对所有文档中的词，重新抽样，设置主题
			for (int j = 0; j < burninNum; j++) {
				//M-Step： p(z_i = k|z_-i, w) 抽样
				for (Doc doc : lda.getDocs()) {
					for (Vector vector : doc.getVectors()) {
						sampleTopic(doc, vector);
					}
				}
			} //end for burn-in
			
			//更新估计参数
			updateEstimatedParameters();
			
			//保存参数
			saveModel();
			
		} 
	}
	
	/**
	 * 保存模型训练的参数
	 */
	public void saveModel() {
		// TODO:保存到文件
	}
	
	
	/**
	 * 初始化模型参数
	 */
	private void initModelParam() {
		for(int k = 0; k < lda.getTopicNum(); k++) {
			topics[k] = new Topic(lda.getvCount());
		}
		
		for(Doc doc : lda.getDocs()) {
			for(Vector v : doc.getVectors()) {
				topics[v.getTopicId()].addVector(v);
			}
		}
	}
	
	/**
	 * Compute p(z_i = k|z_-i, w) 抽样
	 * @param doc
	 * @param vector
	 */
	private void sampleTopic(Doc doc, Vector vector) {
		int oldTopic = vector.getTopicId();
		doc.removeVectorTopic(vector);
		topics[oldTopic].removeVector(vector);
		
		double[] p = new double[lda.getTopicNum()];
		
		for (int k = 0; k < lda.getTopicNum(); k++) {
			p[k] = ((topics[k].getVectorIdArray()[vector.getId()] + beta) / (topics[k].getvCount() + lda.getvCount() * beta) *
					(doc.getTopicArray()[k] + alpha) / (doc.getVectors().size() - 1 + lda.getTopicNum() * alpha) );
			//相当于 p[k] = phi[k][v] * theta[d][k] 
		}
		
		//MCMC：累计使得p[k]是前面所有topic可能性的和
		//对求的概率，求和
		for (int k = 1; k < lda.getTopicNum(); k++) {
			p[k] += p[k - 1];
		}

		double u = Math.random() * p[lda.getTopicNum() - 1];
		
		int newTopic = 0;
		for(; newTopic < lda.getTopicNum(); newTopic++) {
			if (u < p[newTopic]) {
				break;
			}
		}
		
		//更新Topic
		vector.setTopicId(newTopic);
		doc.updateVecortTopic(vector);
		topics[newTopic].addVector(vector);
		
	}

	/**
	 * 更新估计参数
	 */
	private void updateEstimatedParameters() {
		Topic topic = null;
		for (int k = 0; k < lda.getTopicNum(); k++) {
			topic = topics[k];
			for (int v = 0; v < lda.getvCount(); v++) {
				//主题k中第v个词的个数 + beta / 主题k包含所有词的个数 + 一共有的词项 * beta 
				phi[k][v] = (topic.getVectorIdArray()[v] + beta) / (topic.getvCount() + lda.getvCount() * beta);
			}
		}
		
		Doc doc = null;
		for (int d = 0; d < lda.getdCount(); d++) {
			doc = lda.getDocs().get(d);
			for (int k = 0; k < lda.getTopicNum(); k++) {
				//文档d中第k个主题包含词的个数 + alpha / 文档所的词 + 总的主题个数 * alpha
				theta[d][k] = (doc.topicArray[k] + alpha) / (doc.getVectors().size() + lda.getTopicNum() * alpha);
			}
		}
	}
	

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public Topic[] getTopics() {
		return topics;
	}

	public void setTopics(Topic[] topics) {
		this.topics = topics;
	}

	public LDA getLda() {
		return lda;
	}

	public double[][] getPhi() {
		return phi;
	}

	public double[][] getTheta() {
		return theta;
	}

	public int getBurninNum() {
		return burninNum;
	}
	
}

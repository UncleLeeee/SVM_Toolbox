package com.lee.svm;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import Jama.Matrix;

public class SMOAlgorithm {
	private Matrix trainingSamples;                             //rowNum*columnNum
	private Matrix svmModel;                                    //1*(columnNum+1)
	private ConcurrentHashMap<Integer, Double>  errorCache;	    //non-bound error cache
	private Matrix label;                                       //1*columnNum
	
	private int rowNum;                    //样本的维数
	private int columnNum;                 //样本的个数
	
	private double tol = 0.01;
	private double C=0.;
	/*
	 * 核函数的引用,默认为线性核函数
	 * */
	private KernelFunction kernel = new LinearKernel();
	/*
	 * 使用策略模式来实现核函数的计算，这个方法为设置核函数
	 * */
	public void setKernel(KernelFunction kernel) {
		this.kernel = kernel;
	}
	/*
	 * 构造方法，对SMO的初始化工作放在这里
	 * */
	public SMOAlgorithm(Matrix samples,Matrix label,double tol,double C,KernelFunction k){
		this.trainingSamples = samples;
		this.label = label;
		this.C = C;
		this.tol = tol;
		if(this.trainingSamples.getColumnDimension()!=this.label.getColumnDimension()){
			System.out.println("样本数不一致！！");
			return;
		}
		/*
		 * 输出模型的维数为样本数+1，前n维是alpha，最后一维为b
		 * 误差缓存的维数为样本数
		 * 取得输入样本矩阵的行数和列数
		 * */
		this.rowNum = samples.getRowDimension();
		this.columnNum = samples.getColumnDimension();
		svmModel = new Matrix(1, samples.getColumnDimension()+1);
		errorCache = new ConcurrentHashMap<Integer, Double>();
		/*
		 * 初始化模型参数全部为0
		 * */
		for(int i=0;i<svmModel.getColumnDimension();i++){
			svmModel.set(0, i, 0.0);
		}
		/*
		 * 计算初始误差E=U-Y
		 * 
		 * */
		for(int i=0;i<this.columnNum;i++){
			if(svmModel.get(0, i) != 0 && svmModel.get(0, i) != this.C){
				double res = this.calcError(i);
				errorCache.put(i, res);
			}
		}
	}
	/**
	 * 用于计算第i个样本的输出误差,计算结束后直接更新errorCache的第i个值
	 * @param i (i表示第i个样本)
	 * @return void
	 */
	public double calcError(int i){
		Matrix xi=trainingSamples.getMatrix(0, this.rowNum-1, i, i);
		double ui = svmOutput(xi);
		/*
		 * 设置第i个errorCache的值
		 * */
		//errorCache.set(0, i, ui-label.get(0, i));
		return (ui - label.get(0, i));
	}
	/**
	 * 样本列向量xi作为输入，将其输入到模型中，得出返回值ui
	 * @param xi 一个样本或测试集的列向量（xi的维数必须为rowNum）
	 * @return 
	 */
	public double svmOutput(Matrix xi) {
		/*
		 * 取得第i列样本列向量
		 * */
		double ui=0.;
		for(int j=0;j<this.columnNum;j++){
			Matrix xj = trainingSamples.getMatrix(0, this.rowNum-1, j, j);
			double kernelProduct = this.kernel.calculate(xi, xj);
			/*
			 * 计算ui的值，在循环结束后还需减去b！
			 * */
			ui += kernelProduct*svmModel.get(0, j)*label.get(0, j);
		}
		ui = ui - svmModel.get(0, this.columnNum);
		return ui;
	}
	/**
	 * 
	 * 
	 */
	public SVMStruct svmTrainning(){
		int numChanged = 0;
		boolean examineAll = true;
		int times = 0;
		/**
		 * 训练过程（遍历non-bound的alpha，更新它们，如果没有则遍历所有的alpha）
		 */
		while(numChanged > 0 | examineAll){
			numChanged = 0;
			times ++;
			System.out.println("迭代次数： " + times);
			System.out.println("errorCache Size:   "+errorCache.size());
			if(examineAll){
				for(int i=0;i<this.columnNum;i++){
					System.out.println("正在检查所有集合： "+i);
					System.out.println("numChanged: "+ numChanged);
					numChanged += examineSample(i);
				}
			}
			else{
				if(errorCache.size() > 0){
					Iterator iter = errorCache.entrySet().iterator();
					
					while(iter.hasNext()){
						Entry item = (Entry) iter.next();
						int i = (Integer)item.getKey();
						numChanged += examineSample(i);
						System.out.println("正在检查errorCache： "+i);
						System.out.println("numChanged: "+ numChanged);
					}
				}
			}
			if(examineAll) examineAll = false;
			else if(numChanged == 0) examineAll = true;
		}
		
		/**
		 * 训练完毕，生成SVMStruct，并返回给client
		 */
		int length = 0;
		for(int i=0;i<this.columnNum;i++){
			if(svmModel.get(0, i) != 0){
				length ++;
			}
		}
		Matrix sv = new Matrix(rowNum, length);
		Matrix label = new Matrix(1, length);
		Matrix alphas = new Matrix(1, length);
		int k=0;
		for(int i=0;i<this.columnNum;i++){
			if(svmModel.get(0, i) != 0){
				Matrix temp = trainingSamples.getMatrix(0, rowNum-1, i, i);
				sv.setMatrix(0, rowNum-1, k, k, temp);
				label.set(0, k, this.label.get(0, i));
				alphas.set(0, k, this.svmModel.get(0, i));
				k ++;
			}
		}
		return new SVMStruct(label, sv,alphas, length, svmModel.get(0, columnNum),this.kernel);
	}
	/**
	 * 
	 * @param i2
	 * @return
	 */
	public int examineSample(int i2){
		double y2 = label.get(0, i2);
		double alpha2 = svmModel.get(0, i2);
		double E2 = calcError(i2);
		double r2 = E2*y2;
		if((r2<-tol && alpha2 < C) || (r2 > tol && alpha2 >0 )){
			if(errorCache.size() >1 ){
				int i1 = heuristicsStep2(i2);
				if(takeStep(i1,i2)) return 1;
				
				Iterator iter = errorCache.entrySet().iterator();
				while(iter.hasNext()){
					Entry item = (Entry) iter.next();
					int nextI = (Integer) item.getKey();
					if(nextI!=i2){
						if(takeStep(nextI,i2)) return 1;
					}
				}
			}
			
			for(int i=0;i<this.columnNum;i++){
				int i1 = i;
				if(i1!=i2){
					if(takeStep(i1,i2)) return 1;	
				}
			}
		}
		return 0;
	}
	/**
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public boolean takeStep(int i1,int i2){
		if(i1 == i2) return false;
		double alpha1 = svmModel.get(0, i1);
		double y1 = label.get(0, i1);
		double E1 = calcError(i1);
		double alpha2 = svmModel.get(0, i2);
		double y2 = label.get(0, i2);
		double E2 = calcError(i2);
		double b = svmModel.get(0, columnNum);
		double s = y1 * y2;
		double L = 0.;
		double H = 0.;
		if(y1 == y2){
			L = Math.max(0., alpha2 + alpha1 - C);
			H = Math.min(C, alpha2 + alpha1); 
		}
		else{
			L = Math.max(0., alpha2 - alpha1);
			H = Math.min(C, C + alpha2 - alpha1);
		}
		if(L == H) return false;
		Matrix x1 = trainingSamples.getMatrix(0, this.rowNum-1, i1, i1);
		Matrix x2 = trainingSamples.getMatrix(0, this.rowNum-1, i2, i2);
		double k11 = this.kernel.calculate(x1, x1);
		double k12 = this.kernel.calculate(x1, x2);
		double k22 = this.kernel.calculate(x2, x2);
		double eta = k11 + k22 - 2*k12;
		
		double a2 = 0.;
		double a1 = 0.;
		if(eta > 0){
			a2 = alpha2 + y2*(E1 - E2)/eta;
			if(a2 < L) a2 = L;
			else if(a2 > H) a2 = H;
		}
		else{
			double f1 = y1*(E1 + b) - alpha1*k11 - s*alpha2*k12;
			double f2 = y2*(E2 + b) - s*alpha1*k12 - alpha2*k22;
			double L1 = alpha1 + s*(alpha2 - L);
			double H1 = alpha1 + s*(alpha2 - H);
			double WL = L1*f1 + L*f2 + 0.5*L1*L1*k11 + 0.5*L*L*k22 + s*L*L1*k12;
			double WH = H1*f1 + H*f2 + 0.5*H1*H1*k11 + 0.5*H*H*k22 + s*H*H1*k12;
			double eps = Math.ulp(WH);
			if(WL < WH - eps){
				a2 = L;
			}
			else if(WL > WH + eps){
				a2 = H; 
			}
			else{
				a2 = alpha2;
			}
		}
		double aEps = Math.ulp((a2 + alpha2));
		if(Math.abs(a2 - alpha2) < aEps*(a2 + alpha2 + aEps)) return false;
		a1 = alpha1 + s*(alpha2 - a2);
		
		double b1 = E1 + y1*(a1 - alpha1)*k11 + y2*(a2 - alpha2)*k12 + b;
		double b2 = E2 + y1*(a1 - alpha1)*k12 + y2*(a2 - alpha2)*k22 + b;
		if(a1>0 && a1<C) b = b1;
		else if(a2>0 && a2<C) b = b2;
		else b = (b1 + b2)/2;
		svmModel.set(0, i1, a1);
		svmModel.set(0, i2, a2);
		svmModel.set(0, columnNum, b);
		double newE1 = calcError(i1);
		double newE2 = calcError(i2);
		if(a1>0 && a1<C) errorCache.put(i1, newE1);
		else errorCache.remove(i1);
		if(a2>0 && a2<C) errorCache.put(i2, newE2);
		else errorCache.remove(i2);
		return true;
	}
	/**
	 * 启发式策略2
	 * @param i2
	 * @return
	 */
	public int heuristicsStep2(int i2){
		double E2 = calcError(i2);
		if(E2 > 0){
			double minE = Double.MAX_VALUE;
			int minId = 0;
			Iterator iter = errorCache.entrySet().iterator();
			while(iter.hasNext()){
				Entry item = (Entry) iter.next();
				int id = (Integer)item.getKey();
				double errorValue = (Double)item.getValue();
				if(errorValue < minE){
					minE = errorValue;
					minId = id;
				}
			}
			return minId;
		}
		else{
			double maxE = Double.MIN_VALUE;
			int maxId = 0;
			Iterator iter = errorCache.entrySet().iterator();
			while(iter.hasNext()){
				Entry item = (Entry) iter.next();
				int id = (Integer)item.getKey();
				double errorValue = (Double)item.getValue();
				if(errorValue > maxE){
					maxE = errorValue;
					maxId = id;
				}
			}
			return maxId;
		}
	}
	/**
	 * 
	 * @param i2
	 * @return
	 */
	public int chooseRandomPoint(int i2){
		int i1 = (int)(Math.random()*this.columnNum);
		while(i1 == i2){
			i1 = (int)(Math.random()*this.columnNum);
		}
		return i1;
	}
	/**
	 * 
	 * @param samples
	 * @param labels
	 * @param struct
	 * @return
	 */
	public static ResStruct svmTest(Matrix samples,Matrix labels,SVMStruct struct){
		Matrix oneSample = new Matrix(samples.getRowDimension(), 1);
		Matrix alphas = struct.getSupportAlphas();
		Matrix svmLabels = struct.getSupportLabels();
		Matrix svs = struct.getSupportVectors();
		KernelFunction kerF = struct.getKernelfunction();
		double b = struct.getB();
		
		if(svs.getRowDimension() != samples.getRowDimension()) return null;
		
		int right = 0;
		int wrong = 0;
		double accuracy = 0.; 
		for(int i=0;i<samples.getColumnDimension();i++){
			double ui = 0.;
			oneSample = samples.getMatrix(0, samples.getRowDimension()-1, i, i);
			for(int j=0;j<svs.getColumnDimension();j++){
				Matrix sv = svs.getMatrix(0, svs.getRowDimension()-1, j, j);
				ui += svmLabels.get(0, j)*alphas.get(0, j)*kerF.calculate(sv, oneSample);
			}
			ui = ui - b;
			if(ui>0) ui=1.;
			else ui=-1.;
			if(null != labels){
				if(ui == labels.get(0, i)) right++;
				else wrong++;
			}
			else{
				right = (int)ui;
			}

		}
		if(null == labels) return new ResStruct(right, 0, 0);
		accuracy = (double)(right)/((double)right+(double)wrong);
		return new ResStruct(right, wrong, accuracy);
	}
}

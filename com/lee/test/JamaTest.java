package com.lee.test;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.lee.imagehelper.*;

import Jama.Matrix;
import com.lee.svm.*;

public class JamaTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
//		double[][] val={{1.,2.,3.},{4.,5.,6.},{7.,8.,9.}};
//		double[][] val2={{0.,0.,1.}};
//		Matrix samples = new Matrix(val);
//		Matrix label = new Matrix(val2);
		//SMOAlgorithm smo = new SMOAlgorithm(samples,label);
		//double x = 6000.0005;
		//System.out.println(Math.ulp(x));
		ImageReader ir = new ImageReader();
		ir.getTrainningImages();
		RBFKernel rbfKernel = new RBFKernel(0.05);
//		Date date = new Date();
//		long begin = date.getTime();
		SMOAlgorithm smo = new SMOAlgorithm(ir.getTrainingSamples(), ir.getTrainingLabels(), 0.1, 0.2,rbfKernel);
//		
		SVMStruct struct = smo.svmTrainning();
//		
//		
		SVMStruct.saveToFile(struct, "struct_final_image");
		System.out.println("Finished!");
//		long end = date.getTime();
//		long time = end - begin;
//		System.out.println(begin + "  运行时间为： "+time+"   结束:" +end);
//		System.out.println("训练结束！");
//		double[][] valOfSamples = {{1.,0.},{3.5,4.},{1.,1.},{1.,2.},{2.,0.},{4.,3.},{4.5,4.},{5.,3.},{2.,1.},{2.5,7},{2,2},{2.7,2.3},{2,0}};
//		double[][] valOfLabels = {{-1.,1.,-1.,-1.,-1.,1.,1.,1.,-1.,1.,1,-1,-1}};
//		Matrix samples = new Matrix(valOfSamples);
//		samples = samples.transpose();
//		Matrix labels = new Matrix(valOfLabels);
////		SMOAlgorithm smo = new SMOAlgorithm(samples, labels, 0.1, 1.);
//		SVMStruct struct = new SMOAlgorithm(samples, labels, 0.1, 1.).svmTrainning();
//		
//		SVMStruct.saveToFile(struct, "struct1");
		
//		double[][] valOfTest = {{6,6},{7,7},{-1,0},{0,2},{1,0},{1,1},{4.2,5.1}};
//		double[][] valOftestLabel = {{1,1,-1,-1,-1,-1,1}};
//		
//		Matrix test = new Matrix(valOfTest);
//		test = test.transpose();
//		Matrix testlabels = new Matrix(valOftestLabel); 
//		SVMStruct struct_fromFile = SVMStruct.readFromFile("/home/unclelee/workspace/SVM/struct_test_image");
//		ResStruct res = SMOAlgorithm.svmTest(ir.getTestingSamples(), ir.getTestingLabels(), struct_fromFile);
//		
////		double[][] valOfTests = {{3.,3}};
////		Matrix tests = new Matrix(valOfTests);
////		double test = smo.svmOutput(tests.transpose());
//		System.out.println(res);
//		ConcurrentHashMap<Integer, Double> test = new ConcurrentHashMap<Integer, Double>();
//		test.put(1, 1.);
//		test.put(2, 2.);
//		test.put(3, 3.);
//		test.put(4, 4.);
//		
//		Iterator iter = test.entrySet().iterator();
//		while(iter.hasNext()){
//			Entry item = (Entry) iter.next();
//			int i = (Integer) item.getKey();
//			test.remove(i);
//		}
		
	}

}

package com.lee.test;
import Jama.Matrix;
import com.lee.svm.*;

public class SVMtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix samples = new Matrix(1,10);
		Matrix label = new Matrix(1,10);
		for(int i=0;i<10;i++){
			samples.set(0, i, i);
			label.set(0, i, i*i);
		}
		RBFKernel rbfKernel = new RBFKernel(0.05);
		SMOAlgorithm smo = new SMOAlgorithm(samples,label,0.1, 0.2,rbfKernel);
		SVMStruct struct = smo.svmTrainning();
		Matrix testSamples = new Matrix(1, 1);
		for(int i=0;i<3;i++){
			testSamples.set(0, 0, i);
			double out = smo.svmOutput(testSamples);
			System.out.println(out);
		}
		
	}

}

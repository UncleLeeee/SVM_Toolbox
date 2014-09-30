package com.lee.svm;

import java.io.Serializable;

import Jama.Matrix;

public class RBFKernel implements KernelFunction,Serializable {

	private double gama = 1;
	public RBFKernel(double gama) {
		// TODO Auto-generated constructor stub
		this.gama = gama;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RBF";
	}

	@Override
	public double calculate(Matrix xi, Matrix xj) {
		// TODO Auto-generated method stub
		double result = 0;
		if(xi.getRowDimension() != xj.getRowDimension() 
				|| xi.getColumnDimension() !=1 
					|| xj.getColumnDimension() != 1) return 0;
		for(int i=0;i<xi.getRowDimension();i++){
			result += Math.pow((xi.get(i, 0)-xj.get(i, 0)), 2);
		}
		result = Math.sqrt(result);
		result = Math.pow(-(this.gama * result),2);
		result = Math.exp(result);
		
		return result;
	}

}

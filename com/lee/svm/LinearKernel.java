package com.lee.svm;

import java.io.Serializable;

import Jama.Matrix;

public class LinearKernel implements KernelFunction,Serializable {


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "三次方多项式核函数，公式为K(x, x i ) = (x T x i + 1)3 ";
	}

	@Override
	public double calculate(Matrix xi, Matrix xj) {
		// TODO Auto-generated method stub
		double result = 0;
		if(xi.getRowDimension() != xj.getRowDimension() 
				|| xi.getColumnDimension() !=1 
					|| xj.getColumnDimension() != 1) return 0;
		
		for(int i=0;i<xi.getRowDimension();i++){
			result += xi.get(i, 0) * xj.get(i, 0);
		}
		result = Math.pow((result + 1),3);
		return result;
	}

}

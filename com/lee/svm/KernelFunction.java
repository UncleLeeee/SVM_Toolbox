package com.lee.svm;

import Jama.Matrix;

public interface KernelFunction{
	public double calculate(Matrix xi,Matrix xj);
	public String getName();
}

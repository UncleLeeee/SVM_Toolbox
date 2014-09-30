package com.lee.svm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import Jama.Matrix;

public class SVMStruct implements Serializable{
	
	private Matrix supportLabels;
	private Matrix supportVectors;
	private Matrix supportAlphas;
	private int numOfSV;
	private double b;
	private KernelFunction kernelfunction;
	
	public SVMStruct(Matrix labels,Matrix svs,Matrix alphas,int length,double b,KernelFunction kernel) {
		// TODO Auto-generated constructor stub
		this.supportLabels = labels;
		this.supportVectors = svs;
		this.supportAlphas = alphas;
		this.numOfSV = length;
		this.b = b;
		this.kernelfunction = kernel;
	}

	public KernelFunction getKernelfunction() {
		return kernelfunction;
	}

	public Matrix getSupportLabels() {
		return supportLabels;
	}

	public Matrix getSupportVectors() {
		return supportVectors;
	}

	public Matrix getSupportAlphas() {
		return supportAlphas;
	}

	public int getNumOfSV() {
		return numOfSV;
	}

	public double getB() {
		return b;
	}
	/**
	 * 
	 * @param struct
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveToFile(SVMStruct struct,String fileName) throws IOException{
		FileOutputStream fos = new FileOutputStream(fileName);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(struct);
			oos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static SVMStruct readFromFile(String filePath) throws Exception{
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		SVMStruct struct = (SVMStruct)ois.readObject();
		return struct;
	}
	
	
}

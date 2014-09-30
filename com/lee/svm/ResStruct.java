package com.lee.svm;

public class ResStruct {
	private int numOfRight;
	private int numOfWrong;
	private double accuracy;
	
	public ResStruct(int right,int wrong,double accu) {
		// TODO Auto-generated constructor stub
		this.numOfRight = right;
		this.numOfWrong = wrong;
		this.accuracy = accu;
	}

	public int getNumOfRight() {
		return numOfRight;
	}

	public int getNumOfWrong() {
		return numOfWrong;
	}

	public double getAccuracy() {
		return accuracy;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Right: "+this.numOfRight+"\n" 
				+"Wrong: "+this.numOfWrong+"\n"
				+"Accuracy: "+this.accuracy+"\n";
	}
	
}

package com.lee.imagehelper;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Jama.Matrix;

public class ImageReader {
	private static String trainingMalePath = "/home/unclelee/workspace/SVM/training files/male";
	private static String trainingFemalePath = "/home/unclelee/workspace/SVM/training files/female";
	private static String testingMalePath = "/home/unclelee/workspace/SVM/testing files/male";
	private static String testingFemalePath = "/home/unclelee/workspace/SVM/testing files/female";
	
	private Matrix trainingSamples;
	private Matrix trainingLabels;
	
	private Matrix testingSamples;
	private Matrix testingLabels;
	
	private Matrix oneSamples;
//	private Matrix oneLabels;
	
	private int trainingFileLength = 0;
	private int testingFileLength = 0;
	
	private int imgDimention = 625;
	
	private static int Depth = 256;
	
	public ImageReader() {
		// TODO Auto-generated constructor stub
		File trainMaleDir = new File(trainingMalePath);
		File trainFemaleDir = new File(trainingFemalePath);
		File testMaleDir = new File(testingMalePath);
		File testFemaleDir = new File(testingFemalePath);
		trainingFileLength = trainMaleDir.listFiles().length + trainFemaleDir.listFiles().length;
		testingFileLength = testMaleDir.listFiles().length + testFemaleDir.listFiles().length;
	}
	
	public Matrix getTrainingSamples() {
		return trainingSamples;
	}

	public Matrix getTrainingLabels() {
		return trainingLabels;
	}

	public Matrix getTestingSamples() {
		return testingSamples;
	}

	public Matrix getTestingLabels() {
		return testingLabels;
	}
	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void getOneSamples(File file) throws IOException{
		oneSamples = new Matrix(imgDimention, 1);
//		oneLabels = new Matrix(1, 1);		
		BufferedImage readInImage = ImageIO.read(file);
		histeq(readInImage);
		BufferedImage bufImage = new BufferedImage(25, 25, readInImage.getType());
		Graphics2D g = bufImage.createGraphics(); 
		
		g.drawImage(readInImage, 0, 0, bufImage.getWidth(),   
				bufImage.getHeight(), null);
		g.dispose();
		int height = bufImage.getHeight();
		int width = bufImage.getWidth();
		int rowIndex = 0;
		for(int j=0;j<height;j++){
			for(int k=0;k<width;k++){
				int rgb = bufImage.getRGB(j, k);
				int grey = (int)(0.3*((rgb&0xff0000)>>16) + 0.59*((rgb&0xff00)>>8) + 0.11*(rgb&0xff)); 
				oneSamples.set(rowIndex, 0, grey);
				rowIndex ++;
			}
		}
		nomalization(oneSamples);
	}
	/**
	 * 
	 * @param srcImage
	 */
	public void histeq(BufferedImage srcImage){
		WritableRaster raster = srcImage.getRaster();
		int width = raster.getWidth();
		int height = raster.getHeight();
		int[] iArray = null;
		int[] data = raster.getPixels(0, 0, raster.getWidth(), raster.getHeight(), iArray);
		
		double[] histogram = new double[Depth];
		int C = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for(int i=0;i<data.length;i++){
			if(data[i]>max) max = data[i];
			if(data[i]<min) min = data[i];
			histogram[data[i]]++;
		}
		for(int i=0;i<histogram.length;i++){
			histogram[i] = histogram[i]/data.length;
		}
		double[] p = new double[Depth];
		for(int i=0;i<Depth;i++){
			for(int j=0;j<i;j++){
				p[i]+=histogram[j];
			}
		}
		int[] outputArray = new int[data.length];
		for(int i=0;i<data.length;i++){
			outputArray[i] = (int)(p[data[i]]*(max-min) + min);
		}
		raster.setPixels(0, 0, width, height, outputArray);
	}

	public Matrix getOneSamples() {
		return oneSamples;
	}

	public void getTrainningImages() throws IOException{
		trainingSamples = new Matrix(imgDimention, trainingFileLength);
		trainingLabels = new Matrix(1, trainingFileLength);
		File trainMaleDir = new File(trainingMalePath);
		File trainFemaleDir = new File(trainingFemalePath);
		File[] maleFiles = trainMaleDir.listFiles();
		int columnIndex = 0;
		/**
		 * 读取male的训练图片文件
		 */
		for(int i=0;i<maleFiles.length;i++){
			BufferedImage bufImage = ImageIO.read(maleFiles[i]);
			int height = bufImage.getHeight();
			int width = bufImage.getWidth();
			int rowIndex = 0;
			for(int j=0;j<height;j++){
				for(int k=0;k<width;k++){
					int rgb = bufImage.getRGB(j, k);
					int grey = (int)(0.3*((rgb&0xff0000)>>16) + 0.59*((rgb&0xff00)>>8) + 0.11*(rgb&0xff)); 
					trainingSamples.set(rowIndex, i, grey);
					rowIndex ++;
				}
			}
			trainingLabels.set(0, i, 1);
			columnIndex ++;
		}
		/**
		 * 读取female的训练图片文件
		 */
		File[] femaleFiles = trainFemaleDir.listFiles();
		for(int i=0;i<femaleFiles.length;i++){
			BufferedImage bufImage = ImageIO.read(femaleFiles[i]);
			int height = bufImage.getHeight();
			int width = bufImage.getWidth();
			int rowIndex = 0;
			for(int j=0;j<height;j++){
				for(int k=0;k<width;k++){
					int rgb = bufImage.getRGB(j, k);
					int grey = (int)(0.3*((rgb&0xff0000)>>16) + 0.59*((rgb&0xff00)>>8) + 0.11*(rgb&0xff)); 
					trainingSamples.set(rowIndex, columnIndex, grey);
					rowIndex ++;
				}
			}
			trainingLabels.set(0, columnIndex, -1);
			columnIndex ++;
		}
		nomalization(trainingSamples);
		System.out.println("训练文件读取结束！");
	}
	/**
	 * 
	 * @param input
	 */
	public void nomalization(Matrix input){
		double mean = 0;
		double deta = 0;
		double sum = 0;
		for(int i=0;i<input.getColumnDimension();i++){
			sum = 0;
			/**
			 * 求平均值
			 */
			for(int j=0;j<input.getRowDimension();j++){
				sum += input.get(j, i);
			}
			mean = sum / input.getRowDimension();
			/**
			 * 求方差
			 */
			for(int j=0;j<input.getRowDimension();j++){
				deta += Math.pow((input.get(j, i) - mean), 2);
			}
			deta = Math.sqrt(deta/(input.getRowDimension()-1));
			/**
			 * 计算新的xi
			 */
			for(int j=0;j<input.getRowDimension();j++){
				input.set(j, i, ((input.get(j, i)-mean)/deta));
			}
		}
		System.out.println("归一化结束");
	}
	
	
	public void getTestingImages() throws IOException{
		testingSamples = new Matrix(imgDimention, testingFileLength);
		testingLabels = new Matrix(1, testingFileLength);
		File testMaleDir = new File(testingMalePath);
		File testFemaleDir = new File(testingFemalePath);
		File[] maleFiles = testMaleDir.listFiles();
		int columnIndex = 0;
		/**
		 * 读取male的训练图片文件
		 */
		for(int i=0;i<maleFiles.length;i++){
			BufferedImage bufImage = ImageIO.read(maleFiles[i]);
			int height = bufImage.getHeight();
			int width = bufImage.getWidth();
			int rowIndex = 0;
			for(int j=0;j<height;j++){
				for(int k=0;k<width;k++){
					int rgb = bufImage.getRGB(j, k);
					int grey = (int)(0.3*((rgb&0xff0000)>>16) + 0.59*((rgb&0xff00)>>8) + 0.11*(rgb&0xff)); 
					testingSamples.set(rowIndex, i, grey);
					rowIndex ++;
				}
			}
			testingLabels.set(0, i, 1);
			columnIndex ++;
		}
		/**
		 * 读取female的训练图片文件
		 */
		File[] femaleFiles = testFemaleDir.listFiles();
		for(int i=0;i<femaleFiles.length;i++){
			BufferedImage bufImage = ImageIO.read(femaleFiles[i]);
			int height = bufImage.getHeight();
			int width = bufImage.getWidth();
			int rowIndex = 0;
			for(int j=0;j<height;j++){
				for(int k=0;k<width;k++){
					int rgb = bufImage.getRGB(j, k);
					int grey = (int)(0.3*((rgb&0xff0000)>>16) + 0.59*((rgb&0xff00)>>8) + 0.11*(rgb&0xff)); 
					testingSamples.set(rowIndex, columnIndex, grey);
					rowIndex ++;
				}
			}
			testingLabels.set(0, columnIndex, -1);
			columnIndex ++;
		}
		nomalization(testingSamples);
		System.out.println("读取结束！");
	}
}

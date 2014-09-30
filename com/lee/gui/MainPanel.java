package com.lee.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Struct;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import com.lee.imagehelper.ImageReader;
import com.lee.svm.ResStruct;
import com.lee.svm.SMOAlgorithm;
import com.lee.svm.SVMStruct;



public class MainPanel {
	SVMStruct struct_fromFile;
	//定义Jframe
	JFrame mainframe = new JFrame("Gender Recognition");
	JMenuBar menuBar = new JMenuBar();
	//label用于显示图片,textLabel用于显示性别信息
	JLabel label = new JLabel();
	JLabel textLabel = new JLabel();
	JTextArea infoText = new JTextArea();
	JTextArea svmInfo = new JTextArea();
	//以当前路径创建文件选择器
	JFileChooser chooser = new JFileChooser(".");
	//定义文件过滤器
	ExtensionFileFilter filter = new ExtensionFileFilter();
	public void init() throws Exception{
		
		struct_fromFile = SVMStruct.readFromFile("/home/unclelee/workspace/SVM/struct_final_image");
		mainframe.setLayout(new GridLayout(2, 5));
		//创建一个filefilter
		filter.addExtension("bmp");
		filter.setDescription("图片文件(*.bmp)");
		chooser.addChoosableFileFilter(filter);
		//禁止“文件类型”下拉列表中显示“所有文件”选项
		chooser.setAcceptAllFileFilterUsed(false);
		//用于检测被选择文件的改变事件
		chooser.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//JFileChooser的被选文件已经发生了改变
				if(evt.getPropertyName() == 
					JFileChooser.SELECTED_FILE_CHANGED_PROPERTY){
					//获取用户选择的新文件
					File f = (File)evt.getNewValue();
					if(f == null) return;
					
					System.out.println("选中了文件： "+ f.getName());
				}
			}
		});
		/**
		 * 以下为该窗口的安装菜单
		 */
		JMenu menu = new JMenu("打开");
		menuBar.add(menu);
		JMenuItem openItem = new JMenuItem("打开图片");
		menu.add(openItem);
		//单击openItem菜单项显示“打开文件”的对话框
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				//设置文件对话框的当前路径
				//chooser.setCurrentDirectory(new File("."));
				//显示文件对话框
				int result = chooser.showDialog(mainframe,"打开图片文件");
				//如果用户选择了APPROVE按钮，即打开，保存及其等效按钮
				if(result == JFileChooser.APPROVE_OPTION){
					File bmpFile = chooser.getSelectedFile();
					Image img = null;
					try {
						img = ImageIO.read(bmpFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ImageIcon icon = new ImageIcon(img);
					label.setIcon(icon);
					
					ImageReader ir = new ImageReader();
					try {
						ir.getOneSamples(bmpFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						
						ResStruct res = SMOAlgorithm.svmTest(ir.getOneSamples(), null, struct_fromFile);
						if(res.getNumOfRight() == 1) textLabel.setText("Male");
						else textLabel.setText("Female");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.out.println("赞同按钮："+ name);
				}
			}
		});
		JMenuItem ShowSvm = new JMenuItem("SVM模型信息");
		menu.add(ShowSvm);
		ShowSvm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String number = Integer.toString(struct_fromFile.getNumOfSV());
				String kernel = struct_fromFile.getKernelfunction().getName();
				String infoNum = "SV的数量： "+number;
				String infoKer = "核函数类型： "+kernel;
				String[] multiLineMsg = {"SVM模型相关信息：",infoNum,infoKer,"文件名： struct_test_image"};
				JOptionPane.showMessageDialog(mainframe,multiLineMsg);
			}
		});
		
		JMenuItem exitItem = new JMenuItem("Exit");
		menu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		mainframe.setJMenuBar(menuBar);
		JPanel jp_image = new JPanel(new GridLayout(1,2));
		JPanel jp_other = new JPanel(new GridLayout(1,2));
		jp_image.setPreferredSize(new Dimension(100, 130));
		jp_image.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));
		jp_other.setPreferredSize(new Dimension(100, 50));
		jp_other.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));
		label.setPreferredSize(new Dimension(25, 25));
		label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));
		textLabel.setPreferredSize(new Dimension(70, 70));
		textLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));
		jp_image.add(label);
		jp_image.add(textLabel);
		JButton start = new JButton();
		start.setText("Test");
		start.setPreferredSize(new Dimension(90, 30));
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				ImageReader ir = new ImageReader();
				try {
					ir.getTestingImages();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SVMStruct struct_fromFile;
				try {
					struct_fromFile = SVMStruct.readFromFile("/home/unclelee/workspace/SVM/struct_final_image");
					ResStruct res = SMOAlgorithm.svmTest(ir.getTestingSamples(), ir.getTestingLabels(), struct_fromFile);
					infoText.setText(res.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		jp_other.add(start);
		infoText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		infoText.setBackground(Color.WHITE);
		jp_other.add(infoText);
		mainframe.add(jp_image,BorderLayout.NORTH);
		mainframe.add(jp_other,BorderLayout.SOUTH);
//		mainframe.pack();
		mainframe.setSize(500, 500);
//		mainframe.setResizable(false);
		mainframe.setVisible(true);
	}
	
	public static void main(String[] args) throws Exception {
		new MainPanel().init();
	}
}

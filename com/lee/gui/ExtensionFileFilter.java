package com.lee.gui;


import java.io.File;
import java.util.ArrayList;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter{

	private String description;
	private ArrayList<String> extensions = new ArrayList<String>();
	
	public void addExtension(String extension){
		if(!extension.startsWith(".")) extension = "." + extension;
		extensions.add(extension);
	}
	
	
	
	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}






	@Override
	public boolean accept(File pathname) {
		if(pathname.isDirectory()) return true;
		
		String name = pathname.getName().toLowerCase();
		
		for(String extension : extensions){
			if(name.endsWith(extension)) return true;
		}
		return false;
	}

}

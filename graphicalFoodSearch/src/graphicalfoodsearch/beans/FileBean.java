package graphicalfoodsearch.beans;

import graphicalfoodsearch.enums.OperationType;

public class FileBean extends GUIBean {
	private String Directory;
	private String FileName;
	private String FilePath;
	private OperationType Operation;
	
	public FileBean() {
		Directory = "";
		FileName  = "";
		FilePath  = "";
		Operation = OperationType.NEW;
	}
	
	public String GetDirectory() {
		return Directory;
	}
	
	public String GetFileName() {
		return FileName;
	}
	
	public String GetFilePath() {
		return FilePath;
	}
	
	public OperationType GetOperation() {
		return Operation;
	}
	
	public void SetDirectory(String dir) {
		Directory = dir;
	}
	
	public void SetFileName(String fn) {
		FileName = fn;
	}
	
	public void SetFilePath(String fp) {
		FilePath = fp;
	}
	
	public void SetOperation(OperationType ot) {
		Operation = ot;
	}
}
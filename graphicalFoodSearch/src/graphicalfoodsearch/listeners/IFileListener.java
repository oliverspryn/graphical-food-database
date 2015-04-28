package graphicalfoodsearch.listeners;

import graphicalfoodsearch.beans.FileBean;

public interface IFileListener extends IListener {
	public void NewHandler();
	public void OpenHandler(FileBean bean);
	public void SaveHandler(FileBean bean);
	public void SaveAsHandler(FileBean bean);
}
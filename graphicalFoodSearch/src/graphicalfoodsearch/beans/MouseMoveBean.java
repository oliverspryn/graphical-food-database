package graphicalfoodsearch.beans;

public class MouseMoveBean extends GUIBean {
	private int X;
	private int Y;
	
	public MouseMoveBean() {
		X = 0;
		Y = 0;
	}
	
	public int GetX() {
		return X;
	}
	
	public int GetY() {
		return Y;
	}
	
	public void SetX(int x) {
		X = x;
	}
	
	public void SetY(int y) {
		Y = y;
	}
}
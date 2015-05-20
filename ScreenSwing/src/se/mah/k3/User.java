package se.mah.k3;

import java.awt.Color;

public class User implements Comparable<User>{
	private String id;
	float xPos, pyPos;
	float yPos, pxPos;
	private double xRel = 0, pxRel = 0;
	private double yRel = 0, pyRel = 0;
	private Color color = new Color(100, 100, 100);

	public User(String id, float xPos, float yPos) {
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public String getId() {
		return id;
	}

	public void setId(String _id) {
		this.id = _id;
	}

	public float getxPos() {
		return xPos;
	}
	
	public void setxPos(float _xPos) {
		this.xPos = _xPos;
	}
	
	public float getyPos() {
		return yPos;
	}
	
	public void setyPos(float _yPos) {
		this.yPos = _yPos;
	}

	public double getyRel() {
		return yRel;
	}
	
	public void setyRel(double _yRel) {
		this.yRel = _yRel;
	}
	
	public double getxRel() {
		return xRel;
	}
	
	public void setxRel(double _xRel) {
		this.xRel = _xRel;
	}

	public double getpyRel() {
		return pyRel;
	}
	
	public void setpyRel(double _pyRel) {
		this.pyRel = _pyRel;
	}
	
	public double getpxRel() {
		return pxRel;
	}
	
	public void setpxRel(double _pxRel) {
		this.pxRel = _pxRel;
	}

	@Override
	public int compareTo(User _user) {
		return id.compareTo(_user.getId());
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color _color) {
		this.color = _color;
	}
}

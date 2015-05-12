package se.mah.k3;


//This is the class for the word object. It contains the words that
//the user will have displayed in their mobile app. 
//It also contains a boolean to check if the word is active or not.

public class Word {
	public boolean active = false;
	public String text = "";
	public int x, y;
	public int w, h;

	public Word(String text) {
		this.active = false;
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public void setXPos(int xPos){
		this.x = xPos;
	}
	
	public int getXPos(){
		return x;
	}
	
	public void setYPos(int yPos){
		this.y = yPos;
	}
	
	public int getYPos(){
		return y;
	}

	public void setWidth(int width){
		this.w = width;
	}

	public int getWidth(){
		return w;
	}
	
	public void setHeight(int height){
		this.h = height;
	}

	public int getHeight(){
		return h;
	}

	public void setText(String text){
		this.text = text;
	}

	public boolean isActive(){
		return active;
	}
}
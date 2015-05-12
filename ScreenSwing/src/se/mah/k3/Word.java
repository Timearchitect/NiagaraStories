package se.mah.k3;

// This is the class for the word object. It contains the words that
// the user will have displayed in their mobile app. 
// It also contains a boolean to check if the word is active or not.

public class Word {
	public boolean active = false;
	public String text = "";
	public int xPos, yPos;
	public int width, height;

	public Word(String text) {
		this.active = false;
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public void setXPos(int xPos){
		this.xPos = xPos;
	}
	
	public int getXPos(){
		return xPos;
	}
	
	public void setYPos(int yPos){
		this.yPos = yPos;
	}
	
	public int getYPos(){
		return yPos;
	}

	public void setWidth(int width){
		this.width = width;
	}

	public int getWidth(){
		return width;
	}
	
	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	public void setText(String text){
		this.text = text;
	}

	public boolean isActive(){
		return active;
	}
}
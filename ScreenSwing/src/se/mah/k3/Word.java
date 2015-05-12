package se.mah.k3;

// This is the class for the word-object. It contains the words that
// the user will have displayed in their mobile app. 
// It also contains a boolean to check if the word is active or not.

public class Word {
	public boolean isActive = false;
	public String text = "";
	public int x;
	public int y;
	public int w;
	public int h;

	public Word(String text) {
		this.isActive = false;
		this.text = text;
	}
	public String getText(){
		return text;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}

	public void setW(int w){
		int length = text.length();
		int width = length*10;
		w = width;
		this.w = w;
	}

	public int getW(){
		return w;
	}
	
	public void setH(int h){
		int length = text.length();
		int height = (int) (length*1.1);
		h = height;
		this.h = h;
	}

	public int getH(){
		return h;
	}

	public void setText(String text){
		this.text=text;
	}

	public boolean isActive(){
		return isActive;
	}
}
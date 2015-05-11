package se.mah.k3;

// This is the class for the word-object. It contains the words that
// the user will have displayed in their mobile app. 
// It also contains a boolean to check if the word is active or not.

public class Word {
	public boolean active = false;
	public String text = "";
	public int x;
	public int y;

public Word(String text) {
this.active = false;
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

public void setText(String text){
	this.text=text;
}
public boolean isActive(){
	return active;
}

}
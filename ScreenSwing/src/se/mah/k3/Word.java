package se.mah.k3;

public class Word {
	public boolean active = false;
	public String text = "";


public Word(String text) {
this.active = false;
this.text = text;
}
public String getText(){
	return text;
}
public void setText(String text){
	this.text=text;
}
public boolean getActive(){
	return active;
}

}
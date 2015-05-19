package se.mah.k3;

import java.awt.Color;
import se.mah.k3.particles.FrameParticle;
import se.mah.k3.particles.TextParticle;

//This is the class for the word object. It contains the words that
//the user will have displayed in their mobile app. 
//It also contains a boolean to check if the word is active or not.

public class Word {
	DrawPanel drawPanel;
	public boolean active = true, selected;
	public String owner = "";
	public String text = "";
	public int xPos, yPos, width, height, margin = 10;
	
	public Word(String _text, DrawPanel _drawPanel) {
		this.text = _text;
		this.drawPanel = _drawPanel;
		this.active = false;
	}
	
	public String getText(){
		return text;
	}
	
	public void setXPos(int _xPos){
		this.xPos = _xPos;
	}
	
	public int getXPos(){
		return xPos;
	}
	
	public void setYPos(int _yPos){
		this.yPos = _yPos;
	}
	
	public int getYPos(){
		return yPos;
	}

	public void setWidth(int _width){
		this.width = _width;
	}

	public int getWidth(){
		return width;
	}
	
	public void setHeight(int _height){
		this.height = _height;
	}

	public int getHeight(){
		return height;
	}

	public void setText(String _text){
		this.text = _text;
	}

	public boolean isActive(){
		return active;
	}
	
	public void setOwner(String _owner){
		this.owner = _owner;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public void respond(){
		DrawPanel.overParticles.add(new FrameParticle(xPos, yPos, this, 0));
	}
	
	public void appear(){
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, margin, 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, (- margin), 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, (int) (margin * 0.5), 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, (int) (- margin * 0.5), 0, text));
		
		active=true;
	}
	
	public void disappear(){
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, margin, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (margin * 0.5), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (margin * 0.2), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (- margin * 0.2), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (- margin * 0.5), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (- margin), text));
		
		active=false;
	}
	
	public void selected(){
		DrawPanel.overParticles.add( new FrameParticle(xPos, yPos, this, 0));
	}

	public void released(){
		DrawPanel.overParticles.add( new FrameParticle(xPos, yPos, this));
	}

	public void display() {
		DrawPanel.g2.setColor(Constants.wordBackground);
		DrawPanel.g2.fillRect((int) (xPos + 3 - (width * 0.5)) - margin, (int) (yPos + 3 - (height / 2) - margin / 2), width + margin * 2, height + 6);
		DrawPanel.g2.setColor(Color.white);
		DrawPanel.g2.setFont(Constants.lightFont);
		DrawPanel.g2.drawString(text, (int) (xPos - width / 2),(int) (yPos + height / 2));
	}
	
	public void update() {
		// TODO Auto-generated method stub
	}
}
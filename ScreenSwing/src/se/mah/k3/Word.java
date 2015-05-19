package se.mah.k3;

import java.awt.Color;
import java.awt.Graphics2D;

import se.mah.k3.particles.FrameParticle;
import se.mah.k3.particles.TextParticle;


//This is the class for the word object. It contains the words that
//the user will have displayed in their mobile app. 
//It also contains a boolean to check if the word is active or not.

public class Word {
	public enum State {onTray, hold, placed,locked};
	State state=State.onTray;
	public boolean active = true,grabed;
	public String text = "";
	public int x, y, w, h, margin = 10;
	public Word(String text) {
	//	dp=_dp;
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
	public void respond(){
		DrawPanel.overParticles.add( new FrameParticle(x,y,this,0));
	}
	public void appear(){
		
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,10,0,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,-10,0,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,5,0,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,-5,0,text));
		active=true;
	}
	public void disappear(){
		
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,10,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,5,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,2,text));
		
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,0,text));
		
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,-2,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,-5,text));
		DrawPanel.overParticles.add(new TextParticle(x,y,w,h,0,-10,text));
		
		active=false;
		
	}
	
	public void grabed(){
		DrawPanel.overParticles.add( new FrameParticle(x,y,this,0));
	}
	
	public void released(){
		DrawPanel.overParticles.add( new FrameParticle(x,y,this));
	}

	public void display(Graphics2D g2) {
		g2.setColor(Constants.wordBackground);
		g2.fillRect((int) (x + 3 - (w * 0.5)) - margin,(int) (y + 3 - (h * 0.5) - margin * 0.5), w+ margin * 2, h + 6);
		g2.setColor(Color.white);
		g2.setFont(Constants.lightFont);
		g2.drawString(text, (int) (x - w * 0.5),(int) (y + h * 0.25));
		
	}

	public void update() {
		
	}
	
	public int getState(){
		return state.ordinal();
	}
	
}
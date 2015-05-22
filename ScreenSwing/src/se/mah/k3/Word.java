package se.mah.k3;

import java.awt.Color;
import se.mah.k3.particles.FrameParticle;
import se.mah.k3.particles.TextParticle;

//This is the class for the word object. It contains the words that
//the user will have displayed in their mobile app. 
//It also contains a boolean to check if the word is active or not.

public class Word {
	//DrawPanel drawPanel;
	private String type="";
	public boolean active = true, selected;
	public String ownerId = "";
	public User owner;
	public enum State {onTray, draging, placed,locked};
	State state=State.onTray;
	public String text = "";
	public int xPos, yPos, width, height, margin = 20;
	public float pxPos, pyPos,txPos,tyPos;
	public Word(String _text, String _ownerId) {
		this.text = _text;
		this.ownerId = _ownerId;
		this.active = false;
	}
	public Word(String _text, String _ownerId,int _x,int _y, int _tx ,int _ty) {
		xPos=_x;
		yPos=_y;
		txPos=_tx;
		tyPos=_ty;
		this.text = _text;
		this.ownerId = _ownerId;
		this.active = false;
	}
	
	public void setUser(User _u){ 
		if(!owner.equals(_u)){
			owner=_u;
			ownerId=_u.getId();
		}
	}
	
	public User getUser(){
		return owner;
		
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
	
	public void setOwner(String _ownerId){
		this.ownerId = _ownerId;
		for(User u: DrawPanel.userList){
			if(u.getId().equals(this.getOwner())){
				owner=u;
			System.out.println("owner setted");
			}
		}
	}

	public String getOwner(){
		return ownerId;
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
		

		if ( owner!=null) {
			//DrawPanel.g2.setColor(Constants.wordStroke);
			DrawPanel.g2.setColor(owner.getColor());
		}else {
			DrawPanel.g2.setColor(Constants.wordStroke);
		}
		DrawPanel.g2.fillRect((int) (xPos + 3 - (width * 0.5)) - margin, (int) (yPos + 3 - (height * 0.5) - margin * 0.5), width + margin * 2, height + 6);
		DrawPanel.g2.setColor(Color.white);
		DrawPanel.g2.setFont(Constants.lightFont);
		DrawPanel.g2.drawString(text, (int) (xPos - width * 0.5),(int) (yPos + height* 0.25));
		
		if(state==State.draging){
			DrawPanel.g2.setStroke(Constants.wordOutline);
			DrawPanel.g2.drawRect((int) (xPos + 3 - (width * 0.5)) - margin, (int) (yPos + 3 - (height * 0.5) - margin * 0.5), width + margin * 2, height + 6);
		}
		
	}

	public void update() {
		float xDiff=txPos-xPos;
		float yDiff=tyPos-yPos;
		//txPos=Math.cos(angle);
		//tyPos=Math.sin(angle);
		xPos+=(int)(xDiff*0.2);
		yPos+=(int)(yDiff*0.2);
		pxPos=xPos;
		pyPos=yPos;
		
	}

	public void setState(State _state){
		this.state = _state;
	}

	public int getState(){
		return state.ordinal();
	}	


	
}
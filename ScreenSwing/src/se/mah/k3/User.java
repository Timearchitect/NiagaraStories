package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import se.mah.k3.particles.RippleParticle;

public class User implements Comparable<User>{
    public enum State {offline,online, holding,taping,droping,idle};
    public State state=State.online;
	final int DEFAULT_SIZE=100 ;
	private String id;
	int xPos, pyPos;
	int yPos, pxPos;
	float xTar;
	float yTar;
	private float xRel = 0;
	private float yRel = 0;
	float angle,pAngle,tAngle;
	private Color color = new Color(100, 100, 100);
	private float RotateAngle;
	private int size=100;
	int animSize;
	public User(String id, float _xRel, float _yRel) {
		this.id = id;
		this.xRel = _xRel;
		this.yRel = _yRel;
		this.xTar = _xRel*Constants.screenWidth;
		this.yTar= _yRel*Constants.screenHeight;
		//this.xPos = _xRel*Constants.screenWidth;
		//this.yPos = _yRel*Constants.screenHeight;
	}

	public String getId() {
		return id;
	}

	public void setId(String _id) {
		this.id = _id;
	}

	public int getxPos() {
		return xPos;
	}
	
	public void setxPos(int _xPos) {
		this.xPos =  _xPos;
	}
	
	public int getyPos() {
		return yPos;
	}
	
	public void setyPos(int _yPos) {
		this.yPos = _yPos;
	}

	public double getyRel() {
		return yRel;
	}
	
	public void setyRel(int _yRel) {
		this.yRel = _yRel;
		this.yPos = _yRel*Constants.screenHeight;
	}
	
	public double getxRel() {
		return xRel;
	}
	
	public void setxRel(int _xRel) {
		this.xRel = _xRel;
		this.xPos = _xRel*Constants.screenWidth;
		
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
	public void display(Graphics2D g2){
		
		if(state.ordinal()!=0){
		g2.setColor(color);
		g2.setStroke(Constants.userStroke);
		for(int i=0; i<3;i++){
			g2.drawArc((int)(xPos - (size+animSize)*0.5), (int)(yPos - (size+animSize)*0.5),size+animSize,size+animSize,(int)RotateAngle+i*120,60);
		}
		//g2.drawArc((int)(xPos - size*0.5), (int)(yPos - size*0.5),size,size,(int)RotateAngle+180,90);
		//g2.fillOval((int)(xPos - size*0.5), (int)(yPos - size*0.5), size, size);
		g2.setColor(Color.BLACK);
		g2.setFont(Constants.boldFont);

		//g2.drawLine((int)(xPos ), (int)(yPos ),(int)(xPos -Math.cos(Math.toRadians(angle))*50), (int)(yPos-Math.sin(Math.toRadians(angle))*50));
		g2.drawString(id, (int)(xPos + size*0.7), (int)(yPos + size*0.7));
		}else{
			g2.setColor(color);
			g2.fillOval((int)(xPos - 50*0.5), (int)(yPos - 50*0.5), 50, 50);

		}
		
	}
	public void update(){
		float xTdiff=xTar-xPos, yTdiff=yTar-yPos;

		//float aDiff=angle-tAngle;
		//tAngle=(float) Math.toDegrees(Math.atan2(yTdiff, xTdiff));
		//angle-=(float) (aDiff*0.1);
		angle=(float) Math.toDegrees(Math.atan2(yTdiff, xTdiff));
		RotateAngle+=(xTdiff+yTdiff)*0.1;
		size=(int)(Math.abs(xTdiff+yTdiff)*0.2+DEFAULT_SIZE);
		xPos+=(float) (xTdiff*0.1);
		yPos+=(float) (yTdiff*0.1);
		pxPos=xPos;
		pyPos=yPos;
		pAngle=angle;
		
		switch(state.ordinal()){
			case 0: // offline
				
			break;
			case 1: // online
				animSize+=((0-animSize)*0.5);
			break;
			case 2: // hold
		
			break;
			 case 3: 
				 if(xTdiff<100 && yTdiff<100)taping();
				// System.out.println("taping");
			 break;
		}
	}
	public void taping(){
		DrawPanel.overParticles.add( new RippleParticle(xPos,yPos,20));
		state=User.State.online;
			
	}

	public void release() {
		DrawPanel.overParticles.add( new RippleParticle(xPos,yPos));
		animSize=200;
		state=User.State.online;
	}
}

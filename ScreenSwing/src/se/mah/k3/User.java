package se.mah.k3;

import java.awt.Color;
import java.awt.Graphics2D;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import se.mah.k3.particles.RippleParticle;

public class User implements Comparable<User>{
	final private int  offlineTime = 30000,DeathTimer=60000;
	private  long spawnTime ,DeathTime;
    public enum State {offline,online, grabing,taping,droping,idle};
    public State state=State.online;
	final int DEFAULT_SIZE=100 ;
	private String id;
	public Firebase firebase = new Firebase("https://scorching-fire-1846.firebaseio.com/Users"); // Root;
	DataSnapshot dataSnapshot;
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
	public int moves = 4;
	
	public User(String id) {
		this.id = id;
		state=User.State.grabing;
		spawnTime=System.currentTimeMillis(); // set millis time
		//this.xPos = _xRel*Constants.screenWidth;
		//this.yPos = _yRel*Constants.screenHeight;
	}
	
	public User(String id, float _xRel, float _yRel) {
		this.id = id;
		this.xRel = _xRel;
		this.yRel = _yRel;
		this.xTar = _xRel*Constants.screenWidth;
		this.yTar= _yRel*Constants.screenHeight;
		state=User.State.grabing;
		spawnTime=System.currentTimeMillis(); // set millis time
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
	public void display(){
		if (!Constants.noUser && state!=User.State.offline){
			DrawPanel.g2.setColor(color);
			DrawPanel.g2.setStroke(Constants.userStroke);
			for(int i=0; i<moves;i++){
				DrawPanel.g2.drawArc((int)(xPos - (size+animSize)*0.5), (int)(yPos - (size+animSize)*0.5),size+animSize,size+animSize,(int)RotateAngle+i*(360/moves),(360/(moves*2)));
			}
	
			DrawPanel.g2.setColor(Color.BLACK);
			DrawPanel.g2.setFont(Constants.boldFont);
	
			if(  state!=User.State.idle)DrawPanel.g2.drawString(id, (int)(xPos + size*0.7), (int)(yPos + size*0.7));
		}
		if(Constants.debug){
			DrawPanel.g2.setColor(Color.BLACK);
			DrawPanel.g2.setFont(Constants.boldFont);
			DrawPanel.g2.drawString(state.toString(), (int)(xPos + size*0.7), (int)(yPos - size*0.7));
			DrawPanel.g2.drawLine((int)(xPos), (int)(yPos ),(int)(xPos -Math.cos(Math.toRadians(angle))*50), (int)(yPos-Math.sin(Math.toRadians(angle))*50));
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
				animSize=-100;
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
			case 4: // droping 
				
			break;
			case 5:  // idle
				animSize=-50;

			break;

		}
		
		if( System.currentTimeMillis()-spawnTime > offlineTime){state=state.idle;} // set offline after i minute
		if( System.currentTimeMillis()-DeathTimer > DeathTime){state=state.offline;} // set offline after i minute

	}
	public void taping(){
		if(!Constants.noUser)DrawPanel.overParticles.add( new RippleParticle(xPos,yPos,20));
		state=User.State.online;
		resetTimer();
	}

	public void release() {
		if(!Constants.noUser){
			DrawPanel.overParticles.add( new RippleParticle(xPos,yPos));
			animSize=200;
		}
		state=User.State.online;
		resetTimer();
	}
	
	private void resetTimer(){
		spawnTime=System.currentTimeMillis(); 
		DeathTime=System.currentTimeMillis(); 
	}

	public void removeFromFirebase() {
		firebase.child(this.id).removeValue();
		//firebase.removeValue();

	}
}

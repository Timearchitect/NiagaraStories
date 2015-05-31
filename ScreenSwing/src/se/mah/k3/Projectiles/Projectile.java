package se.mah.k3.Projectiles;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import se.mah.k3.Word;


abstract public class Projectile {
	float MAX_VELOCITY=200,MIN_VELOCITY=20; 
	float x,y,vx,vy,ax=0,ay=0;
	int size=50;
	
	public  BasicStroke roundStroke = new BasicStroke(size+20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	public Projectile(float _x,float _y ,float _vx, float _vy){
		x=_x;
		y=_y;
		vx=_vx;
		vy=_vy;
	}

	public void update(){
	}
	
	public void display(Graphics2D g2){
	}
	
	public void collision(Word w){
	}

	public void BoundCollision(){
	}

	void tranferForce(float _vx,float _vy){	
	}	
}
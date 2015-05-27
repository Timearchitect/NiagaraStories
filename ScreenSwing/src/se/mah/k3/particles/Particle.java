package se.mah.k3.particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import se.mah.k3.Constants;
import se.mah.k3.Word;

public abstract class Particle {
	public float x,y,vx,vy,ax,ay;
	public boolean dead;
	protected Color particleColor = Constants.waterColorTrans;
	protected Random r= new Random();
	public Particle(int x2, int y2){
		x=x2;
		y=y2;
	}

	public void update(){
		x+=Math.round(vx);
		y+=Math.round(vy);
		vx+=ax;
		vy+=ay;
	}

	public void display(Graphics2D g2) {
		g2.setColor(particleColor);
		g2.setStroke(Constants.squareStroke);
		g2.drawLine((int)x,(int) y,(int) x, (int)y);
	}

	public void collisionCircle(int _x,int _y, int _radius){
	}
	public void collisionCircle(int xPos, int yPos, int width, Word w) {		
	}
	public void collisionRect(int _x,int _y, int _w,int _h){
		if(_x+_w*0.5 > x &&  _x-_w*0.5 < x && _y+_h*0.5 > y &&  _y-_h*0.5 < y){
			vy*=-0.5;
			vx=5;
			kill();
		}
	}
	public void kill(){
		dead=true;
	}

	public void collisionVSParticle(Particle p) {


	}


}

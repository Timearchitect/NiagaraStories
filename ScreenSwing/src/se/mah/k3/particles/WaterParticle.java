package se.mah.k3.particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import se.mah.k3.Constants;
import se.mah.k3.DrawPanel;

public class WaterParticle extends Particle {
	private Random r =  new Random();
	boolean deathTime = false;
	int timeToDeath = 20;
	
  Color waterColor = particleColor;

	public WaterParticle(int x2, int y2){
		super((int)x2,(int)y2);
		x=x2;
		y=y2;
		vy=17;
		ay=0.4f;
	}
	
	public void update(){
		waterColor = particleColor;
		x+=Math.round(vx);
		y+=(int)vy;
		vx*=0.93;
		vx+=ax;
		vy+=ay;
		if (y > Constants.screenHeight+200)kill();
	}

	public void display(Graphics2D g2) {
		g2.setColor(waterColor);
		g2.setStroke(Constants.waterStroke);
		g2.drawLine((int)x,(int) y, (int)(x-vx*2), (int)(y-vy*6));
		
		if(!Constants.simple){DrawPanel.g2.setColor(Constants.waterEffect);
		DrawPanel.g2.setStroke(Constants.wordEffectLine);
		DrawPanel.g2.drawLine((int)x-2,(int) y, (int)(x-vx-2), (int)(y-vy*10));
		}
	}
	
	public void collisionCircle(int _x,int _y, int _radius){
		float xDiff=_x-x,yDiff= _y-y,dist;
		
		dist=(float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
		//dist=(float) Math.sqrt(Math.pow(xDiff,2)+Math.pow(yDiff,2));
			if(dist-_radius<Constants.WaterStrokeWidth){ // collision
				vy*=-0.5;
				vx=r.nextInt(10)-5;
			}
		
	}
	public void collisionRect(int _x,int _y, int _w,int _h){
		
		if(_x+_w*0.5 > x &&  _x-_w*0.5 < x && _y+_h*0.5 > y &&  _y-_h*0.5 < y){
				vy*=-0.5;
				vx=r.nextInt(10)-5;
		}
	}
	
	public void kill(){
		if((int)r.nextInt(12)==0)DrawPanel.overParticles.add(new SplashParticle((int)x,Constants.screenHeight,(int)vy*7)); //splash
		dead=true;
	}
}

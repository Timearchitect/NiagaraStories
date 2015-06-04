package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import se.mah.k3.Constants;

public class ScanParticle extends Particle {
	boolean deathTime = false;
	int timeToDeath = 20;
  Color waterColor = particleColor;

	public ScanParticle(int x2, int y2){
		super((int)x2,(int)y2);
		x=x2;
		y=y2;
		vy=200;
		//ay=0.4f;
	}
	
	public void update(){
		waterColor = particleColor;
		//x+=Math.round(vx);
		y+=(int)vy;
		//vx*=0.93;
		//vx+=ax;
		//vy+=ay;
		if (y > Constants.screenHeight)kill();
	}

	public void display(Graphics2D g2) {
		if(!Constants.simple){

		g2.setStroke(new BasicStroke((int)(vy*0.5)));
		g2.setColor(new Color(255,255,255,30));
		g2.drawLine((int)0,(int)( y-vy*0.5), (int)(Constants.screenWidth), (int)(y-vy*0.5));
		g2.setColor(new Color(255,255,255,20));
		g2.drawLine((int)0,(int) y, (int)(Constants.screenWidth), (int)(y));
		
		}
	}
	

	
	public void kill(){
	//	if((int)r.nextInt(12)==0)DrawPanel.overParticles.add(new SplashParticle((int)x,(int)y,(int)vy*7)); //splash
		dead=true;
	}
}

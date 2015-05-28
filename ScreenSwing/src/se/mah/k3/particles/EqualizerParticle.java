package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import se.mah.k3.Constants;
import se.mah.k3.Word;

public class EqualizerParticle extends Particle {
	float r;
	public float increment;
	float opacity=255;
	private BasicStroke circleStroke;
	WaterParticle wp;

	public EqualizerParticle(int _x, int _y,float f){
		super(_x,_y);
		x=_x;
		y=_y;
		increment=f;
	}

	public void update(){
		r+=increment;
		circleStroke = new BasicStroke(increment);
		increment*=0.99;
		opacity*=0.9;
		
		if(increment<10)kill();
	}

	public void display(Graphics2D g2) {
		g2.setColor(new Color(199,21,133,(int)opacity));
		g2.setStroke(circleStroke);
		g2.drawOval((int)(x-r*0.5), (int)(y-r*0.5), (int)r,(int) r);
	}

	public void collisionVSParticle(Particle p){
		float xDiff,yDiff,dist,angle;

		xDiff= p.x-x;
		yDiff= p.y-y;
		angle=(float) Math.atan2(xDiff, yDiff);
		dist=(float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));

		if(dist<r*0.5){
			//p.vx=5;
			//p.vy=-5;
			//p.vx+=Math.cos(angle+90)*(dist*2-r)*0.0002*opacity;
			//p.vy-=Math.sin(angle+90)*(dist*2-r)*0.0002*opacity;
			p.particleColor = (new Color(199, 21, 133,50));
		}
		
		if(dist<r*0.3){
			//p.vx=-5;
			p.vy=-1;
			p.ay=5;
			
			//p.vx+=Math.cos(angle+90)*(dist*2-r)*0.0002*opacity;
			//p.vy-=Math.sin(angle+90)*(dist*2-r)*0.0002*opacity;
			p.particleColor = Constants.waterColor;
		}
	}

	public void collisionCircle(int _x,int _y, int _radius,Word w){
		float xDiff=_x-x,yDiff= _y-y,dist;

		dist=(float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
		if(dist-_radius<r*2){ // collision
			vy*=-0.5;
			vx=5;
			w.dead();
		}		
	}
}
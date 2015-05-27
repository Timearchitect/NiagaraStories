package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

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

	public EqualizerParticle(int _x, int _y) {
		super(_x,_y);
		x=_x;
		y=_y;
		increment=500;
	}

	public void update(){
		//x+=Math.round(vx);
		//y+=Math.round(vy);
		r+=increment;
		circleStroke = new BasicStroke(increment);
		increment*=0.99;
		opacity*=0.9;
		if(increment<1)kill();
	}

	public void display(Graphics2D g2) {
		//g2.setColor(new Color(0,100,255,(int)opacity));
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
		//dist=(float) Math.sqrt(Math.pow(xDiff,2)+Math.pow(yDiff,2));
		if(dist<r*0.5){
			p.vx=0;
			p.vy=-5;
			p.vx+=Math.cos(angle+90)*(dist*2-r)*0.0002*opacity;
			p.vy-=Math.sin(angle+90)*(dist*2-r)*0.0002*opacity;
			p.particleColor = (new Color(199, 21, 133));
		}
	}
}
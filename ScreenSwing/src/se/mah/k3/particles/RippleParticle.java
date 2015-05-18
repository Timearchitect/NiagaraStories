package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import se.mah.k3.Word;

public class RippleParticle extends Particle{
	float r,increment,opacity=255;
	BasicStroke circleStroke;
	Graphics2D g2;
	public RippleParticle(int _x, int _y,int _increment){
		super((int)_x,(int)_y);
		x=_x;
		y=_y;
		increment=_increment;
	}

	public RippleParticle(int _x, int _y) {
		super((int)_x,(int)_y);
		x=_x;
		y=_y;
		increment=50;
		}
	

	public void update(){
		//x+=Math.round(vx);
		//y+=Math.round(vy);
		r+=increment;
		circleStroke = new BasicStroke(increment);
		increment*=0.9;
		opacity*=0.9;
		if(increment<5)kill();
	}
	
	public void display(Graphics2D g2) {
		g2.setColor(new Color(0,100,255,(int)opacity));
		g2.setStroke(circleStroke);
		g2.drawOval((int)(x-r*0.5), (int)(y-r*0.5), (int)r,(int) r);
	}
	/*public void collisionCircle(Word word){
		float wx=word.x,wy=word.y,ww=word.w,wh=word.h;
		
		float rHalfWidth = ww/2,
			    rHalfHeight = wh/ 2,
			    cx =x,
			    cy=y, distX, distY, distXSq, distYSq, maxDist;
		 
			if (cx > rHalfWidth + r) {
			//	return false;
			}
		 
			cy = Math.abs(y - wy - rHalfHeight);
		 
			if (cy > rHalfHeight + r) {
				//return false;
			}
		 
			if (cx <= rHalfWidth || cy <= rHalfHeight) {
			//	return true;
			
			}
		 
			distX = cx - rHalfWidth;
			distY = cy - rHalfHeight;
			distXSq = distX * distX;
			distYSq = distY * distY;
			maxDist = r* r;
		 
			//return distXSq + distYSq <= maxDist;
		
		
		
	}*/

}


package se.mah.k3.Projectiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import se.mah.k3.Constants;
import se.mah.k3.DrawPanel;
import se.mah.k3.Word;
import se.mah.k3.particles.RippleParticle;

public class PingBall extends Projectile {

	public  BasicStroke roundStroke = new BasicStroke(size+20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	public PingBall(float _x,float _y ,float _vx, float _vy){
		super(_x, _y, _vx, _vy);
		x=_x;
		y=_y;
		vx=_vx;
		vy=_vy;
	}

	public void update(){
		x+=vx;
		y+=vy;
		vx+=ax;
		vy+=ay;
		if( Math.abs(vx)+ Math.abs(vy)>MIN_VELOCITY){
			vx*=0.99;
			vy*=0.99;
		}
		//	ax*=0.95;
		//	ay*=0.95;
	}
	
	public void display(Graphics2D g2){
		g2.setColor(Color.white);
		g2.setStroke(roundStroke);
		g2.drawLine((int)(x), (int)(y), (int)(x-vx),(int)( y-vy));
		g2.setColor(Color.black);
		g2.fillOval((int)(x-size*0.5),(int)( y-size*0.5), size, size);
	}
	
	public void collision(Word w){
		if(w.yPos-w.height*0.5 < y+size*0.5 && w.yPos+w.height*0.5 > y-size*0.5 && w.xPos-w.width*0.5-Math.abs(w.xVel) < x+size*0.5+Math.abs(vx) && w.xPos+w.width*0.5+Math.abs(w.xVel) > x-size*0.5-Math.abs(vx) ){
			vx*=-1;
			vx+=w.xVel;
			x+=w.xVel;
		
			w.respond();
			w.damage(1);
			//w.xPos-=vx;
			//w.yPos-=vy;
			vy+=w.yVel*0.5;
			collisionEffect();	
		}  

		if(w.xPos-w.width*0.5 < x+size*0.5 && w.xPos+w.width*0.5 > x-size*0.5 && w.yPos-w.height*0.5-Math.abs(w.yVel) < y+size*0.5+Math.abs(vy) && w.yPos+w.height*0.5+Math.abs(w.yVel) > y-size*0.5-Math.abs(vy) ){

			vy*=-1;
			vy+=w.yVel;
			y+=w.yVel;
	
			w.respond();
			w.damage(1);
			//w.xPos-=vx;
			//w.yPos-=vy;
			vx+=w.xVel*0.5;
			collisionEffect();			
		}  
	}

	public void BoundCollision(){
		if(x<0){
			x=0;
			vx*=(-1);
			collisionEffect();
		}else if( x>Constants.screenWidth  ){
			x=Constants.screenWidth  ;
			vx*=(-1);
			collisionEffect();		
		} else if(y<0 ){
			y=0;
			vy*=(-1);
			collisionEffect();		
		}else if( y>Constants.screenHeight  ){
			y=Constants.screenHeight ;
			vy*=(-1);
			collisionEffect();	
		}
	}
	void collisionEffect(){
		DrawPanel.overParticles.add(new RippleParticle((int)x, (int)y,(int)((Math.abs(vx)+Math.abs(vy))*0.1)+20 ));
	}

	void tranferForce(float _vx,float _vy){	
	}	
}

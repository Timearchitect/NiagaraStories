package se.mah.k3.particles;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;

public class SplashParticle extends Particle {
	private Image img;
	Random r = new Random();
	private float angle,opacity=1;
	private int w,h;
	private AffineTransform tx = new AffineTransform(),oldTransform;
	public SplashParticle(int _x, int _y) {
		super(_x,_y);
		 img = Toolkit.getDefaultToolkit().getImage("images/mist.png");
		 w=img.getWidth(null);
		 h=img.getHeight(null);
		 angle= r.nextInt(360);
	}
	public void update(){
		//x+=Math.round(vx);
		//y+=(int)vy;
		//vx*=0.93;
		//vx+=ax;
		//vy+=ay;
		angle+=0.5;
		opacity-=0.04;
		if(opacity<0.1){kill();}
	}

	/*public void display(Graphics2D g2) {
			tx.translate(x,y);
			tx.rotate(Math.toRadians(angle));
			tx.translate(-w*0.5,-h*0.5);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)opacity));
				g2.drawImage(img, tx, null);
		    tx.translate(w*0.5,h*0.5);
		    tx.rotate(Math.toRadians(-angle));
			tx.translate(-x,-y);
		
	}*/
	
	public void display(Graphics2D g2) {
		 oldTransform = g2.getTransform();
				g2.translate(x,y);
				g2.rotate(Math.toRadians(angle));
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)opacity));
					g2.drawImage(img, (int)-(w*0.5), (int)-(h*0.5), w, h, null);
			g2.setTransform(oldTransform);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
					  
		}
		
}

package se.mah.k3.particles;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

import se.mah.k3.DrawPanel;

public class SplashParticle extends Particle {
	//private Image img;
	Random r = new Random();
	private float angle,opacity=0.7f;
	private int size=100;
	private AffineTransform tx = new AffineTransform(),oldTransform;
	public SplashParticle(int _x, int _y,int _size) {
		super(_x,_y);
		 //img = Toolkit.getDefaultToolkit().getImage("images/mist.png");
		 //w=img.getWidth(null);
		 //h=img.getHeight(null);
		size=_size;
		 angle= r.nextInt(360);
	}
	public void update(){
		size+=2;
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
					
					
					
					
					g2.drawImage(DrawPanel.mist, (int)-(size*0.5), (int)-(size*0.5), size, size, null);
					
					
					
					
					
			g2.setTransform(oldTransform);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
					  
		}
		
}

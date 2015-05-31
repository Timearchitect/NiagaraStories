package se.mah.k3;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class WordSkin{
	private float opacity = 1;
	//private AffineTransform tx = new AffineTransform(), oldTransform;
	Word owner;

	public WordSkin(Word _owner) {
		owner=_owner;
	}

	public void display (Graphics2D g2) {
		/*oldTransform = g2.getTransform();
		g2.translate(owner.xPos, owner.yPos);
		g2.rotate(Math.toRadians(owner.angle));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
		
		//Change image file to use as skin
		g2.drawImage(DrawPanel.moss, (int) (0 - owner.width * 0.5 - owner.margin), (int) (0 - owner.height * 0.5 - 7), (int) (owner.width + owner.margin * 1.5 + 10), owner.height, null);
		g2.setTransform(oldTransform);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		//oldTransform = g2.getTransform();
		//g2.translate(owner.xPos, owner.yPos);
		//g2.rotate(Math.toRadians(owner.angle));*/
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
		g2.drawImage(DrawPanel.moss, (int) (0- owner.margin-owner.width*0.5), (int) (0- owner.margin * 0.5-owner.height*0.5+3), (int) (owner.width + owner.margin * 1.5 + 10), owner.height+6, null);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

	public void update() {	

		/*if (opacity < 0.9)
			opacity += 0.001;*/
	}
}
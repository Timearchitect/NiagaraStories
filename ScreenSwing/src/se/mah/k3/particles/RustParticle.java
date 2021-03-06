package se.mah.k3.particles;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import se.mah.k3.DrawPanel;
import se.mah.k3.Health;
import se.mah.k3.Word;

public class RustParticle extends Particle implements Health{
	private float opacity;// health;
	private int width, height, xPos, yPos;
	//private AffineTransform tx = new AffineTransform();
	private AffineTransform oldTransform;
	Word selectedWord;

	public RustParticle(int _x, int _y, int _width, int _height, int _health) {
		super(_x, _y);
		this.width = _width;
		this.height = _height;
		this.xPos = _x;
		this.yPos = _y;
		setHealth(_health);
	}

	public void update() {
		damage(1);

		if (opacity > 0.99 || x != xPos || y != yPos)
			dead();
	}

	public void display (Graphics2D g2) {
		oldTransform = g2.getTransform();
		g2.translate(x, y); //Replace with word coordinates
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
		g2.drawImage(DrawPanel.rust, (int)-(width * 0.5), (int)-(height * 0.5), width, height, null);
		g2.setTransform(oldTransform);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

	@Override
	public void setHealth(float _amount) {
		this.opacity = _amount * (float) 0.0001;
	}

	@Override
	public void damage(float _amount) {
		this.opacity += 0.001;
	}

	@Override
	public void dead() {
		kill();
	}
}
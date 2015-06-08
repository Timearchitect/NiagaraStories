package se.mah.k3.skins;

import java.awt.AlphaComposite;
import java.io.IOException;

import javax.imageio.ImageIO;

import se.mah.k3.DrawPanel;
import se.mah.k3.Word;

abstract public class WordSkin{

	 float opacity = 1;
	 Word owner;


	public WordSkin() {
	}
		
	public WordSkin(Word _owner) {
		owner=_owner;	
	}
	
	 public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public Word getOwner() {
		return owner;
	}

	public void setOwner(Word owner) {
		this.owner = owner;
	}

	public void display () {
	}

	public void update() {	
	}
}
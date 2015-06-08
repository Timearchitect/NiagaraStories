package se.mah.k3.skins;

import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;


import se.mah.k3.DrawPanel;
import se.mah.k3.Word;

public class MossSkin extends WordSkin {
	private BufferedImage moss = DrawPanel.moss;
	public MossSkin(Word _owner) {
		super(_owner);
		this.opacity=1;
	}
	

	public MossSkin() { // Used in WordBiulder & gets its ownerWord  later after WordBiulder.biuld()
	}

	public void display () {
		DrawPanel.g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) this.opacity));
		DrawPanel.g2.drawImage(moss, (int) (0- owner.margin-owner.width*0.5), (int) (0- owner.margin * 0.5-owner.height*0.5+3), (int) (owner.width + owner.margin * 1.5 + 10), owner.height+6, null);
		DrawPanel.g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

	public void update() {	
	}
}

package se.mah.k3;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shadow extends WordSkin {
	Word owner;

	public Shadow(Word _owner) {
		super(_owner);
		owner=_owner;
	}

	public void display () {
		switch(owner.state.ordinal()){
		case 0:
			break;
		case 1 : // draging
			DrawPanel.g2.setColor(new Color(0, 0, 0, 50));;
			DrawPanel.g2.fillRect((int)(0 - owner.offsetX-owner.width*0.5),(int)(3- owner.offsetY * 0.5-owner.height*0.5) , owner.width + owner.margin * 2,(int) (owner.height + 6));
			break;
		case 2 : // placed

			break;
		case 3 : // locked

			break;
		default :
		}
	}
}
package se.mah.k3;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import se.mah.k3.particles.RippleParticle;

public class Mouse extends MouseMotionAdapter implements MouseListener ,MouseMotionListener{
	public static float offsetX ; // mouse
	public static float offsetY;
	public static float mouseY,mouseX;
	public static float pMouseX, pMouseY;
	public boolean hold;
	public static Word selectedWord;
	
	Mouse(){
		
		
		
		
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("click");		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	//	System.out.println("");		
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//System.out.println("yeayeayaeyaeyyae");		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		if (e.getButton() == MouseEvent.NOBUTTON) {
			// System.out.println(" no button clicked");
		} else if (e.getButton() == MouseEvent.BUTTON1) {

			// System.out.println(" left button clicked");
			for (Word word : DrawPanel.words) {
				if (word.active) {
					if (word.xPos + word.margin + (word.width * 0.5) > mouseX&& word.xPos - word.margin- (word.width * 0.5) < mouseX&& word.yPos + word.margin+ (word.height * 0.5) > mouseY&& word.yPos - word.margin- (word.height * 0.5) < mouseY) {
						selectedWord = word;
						selectedWord.selected();
						
						selectedWord.state = Word.State.draging;
						offsetX = word.xPos - mouseX;
						offsetY = word.yPos - mouseY;
						/*myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/occupied").setValue(true);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/active").setValue(true);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/state").setValue("draging");
						 */
						DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/text").setValue(selectedWord.text);
						try {
							DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/owner").setValue(selectedWord.owner.getId());
						} catch (Exception err) {
							DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/owner").setValue("");

						}

						DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/occupied").setValue(true);
						DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/active").setValue(true);
						DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
						DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
						DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/state").setValue("draging");

					}
				} 
			}
			if(selectedWord!= null)selectedWord.toTop(selectedWord); 
			DrawPanel.overParticles.add(new RippleParticle((int) mouseX,(int) mouseY, 30));

			// overParticles.add( new RippleParticle((int)mouseX,
			// (int)mouseY, 40));
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			// System.out.println(" middle button clicked");

			for (Word word : DrawPanel.words) {
				word.respond();
			}

			DrawPanel.overParticles.add(new RippleParticle((int) mouseX,
					(int) mouseY, 200));
		} else if (e.getButton() == MouseEvent.BUTTON3) {

			// System.out.println(" right button clicked");
			// overParticles.add(new SplashParticle((int)mouseX,
			// (int)mouseY));
			// overParticles.add(new RustParticle ((int) mouseX, (int)
			// mouseY, selectedWord.getText().length()));

			for (Word word : DrawPanel.words) {
				if (word.xPos + word.margin + (word.width * 0.5) > mouseX && word.xPos - word.margin - (word.width * 0.5) < mouseX && word.yPos + word.margin+ (word.height * 0.5) > mouseY && word.yPos - word.margin- (word.height * 0.5) < mouseY) {
					if (word.active) {
						word.disappear();
					} else {
						word.appear();
					}
				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		// String wordLength;

		if (e.getButton() == MouseEvent.NOBUTTON) {
			// System.out.println(" no button Release");
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			if (selectedWord != null) {

				// wordLength =
				// String.valueOf(selectedWord.getText().length());
				selectedWord.released();
				// overParticles.add(new RustParticle
				// (selectedWord.getXPos() + 3, selectedWord.getYPos() -
				// 4, 200, 100, Integer.valueOf(wordLength)));
				selectedWord.state = Word.State.placed;
				/*	myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/occupied").setValue(false); // false in regular temp
				myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/active").setValue(true);
				myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
				myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
				myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/state").setValue("placed");
				 */
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/occupied").setValue(false);
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/active").setValue(true);
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/state").setValue("placed");
				System.out.println("id placed: "+ selectedWord.getWordId());
				selectedWord = null;

			}

			DrawPanel.overParticles.add(new RippleParticle((int) mouseX,(int) mouseY));
		} else if (e.getButton() == MouseEvent.BUTTON2) {
		}
	}
		
	

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		if (SwingUtilities.isLeftMouseButton(e)) {

			if (selectedWord != null) {
				selectedWord.state=Word.State.draging;
				selectedWord.xPos = (int) (mouseX + offsetX);
				selectedWord.yPos = (int) (mouseY + offsetY);
				selectedWord.txPos = (int) (mouseX + offsetX);
				selectedWord.tyPos = (int) (mouseY + offsetY);
				//	myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/xRel").setValue(selectedWord.getXPos()/ Constants.screenWidth);
				//	myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/yRel").setValue(selectedWord.getYPos()/ Constants.screenHeight);
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
				DrawPanel.myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
			}

			DrawPanel.overParticles.add(new RippleParticle((int) mouseX,
					(int) mouseY, 10));
		}

		if (SwingUtilities.isMiddleMouseButton(e)) {

		}

		if (SwingUtilities.isRightMouseButton(e)) {

		}
	}
	

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
}

package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JPanel;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DrawPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Firebase myFirebaseRef;
	private ArrayList<User> users = new ArrayList<User>();
	Font font = new Font("Verdana", Font.BOLD, 20);
	
	public DrawPanel() {
		myFirebaseRef = new Firebase("https://klara.firebaseio.com/");
		myFirebaseRef.removeValue();
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr);
		 myFirebaseRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildRemoved(DataSnapshot arg0) {}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}
			
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				Iterable<DataSnapshot> dsList= arg0.getChildren();
				Collections.sort(users);
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0)); //Find the user usernama has to be unique
				 for (DataSnapshot dataSnapshot : dsList) {					 
					 if (dataSnapshot.getKey().equals("xRel")){
						 users.get(place).setxRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("yRel")){
						 users.get(place).setyRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("RoundTripTo")){
						 System.out.println("Roundtrip: "+(long)dataSnapshot.getValue());
						 myFirebaseRef.child(arg0.getKey()).child("RoundTripBack").setValue((long)dataSnapshot.getValue()+1);
					 }
				 }
				 repaint();
			}
			
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()){
					//System.out.println("ADD user with Key: "+arg1+ arg0.getKey());
					Random r = new Random();
					int x = r.nextInt(getSize().width);
					int y = r.nextInt(getSize().height);
						User user = new User(arg0.getKey(),x,y);
						if (!users.contains(user)){
							users.add(user);
							user.setColor(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
				 		}
				}
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}
		});
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2= (Graphics2D) g;
		g2.setFont(font);
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setColor(Color.BLACK);
		g.drawString("ScreenNbr: "+Constants.screenNbr, 10,  20);
		//Test
		for (User user : users) {
			int x = (int)(user.getxRel()*getSize().width);
			int y = (int)(user.getyRel()*getSize().height);
			g2.setColor( user.getColor());
			g2.fillOval(x,y, 10, 10);
			g2.setColor(Color.BLACK);
			g.drawString(user.getId(),x+15,y+15);
		}
		
	}
}

	
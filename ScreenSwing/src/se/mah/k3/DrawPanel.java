package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DrawPanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;
	private Firebase myFirebaseRef;
	public ArrayList<Particle> particles = new ArrayList<Particle>();
	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users = new Vector<User>();
	Font font = new Font("Verdana", Font.BOLD, 20);
	
	public DrawPanel() {
		 
		//myFirebaseRef = new Firebase("https://blinding-heat-7399.firebaseio.com/"); // mattias/Lars
		myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/");  // alrik
		myFirebaseRef.removeValue(); //Cleans out everything
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr);  //Has to be same as on the app. So place specific can't you see the screen you don't know the number
		myFirebaseRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildRemoved(DataSnapshot arg0) {}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}
			
			//A user changed some value so update
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				Iterable<DataSnapshot> dsList= arg0.getChildren();
				Collections.sort(users);
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0)); //Find the user usernama has to be unique uses the method compareTo in User
				 for (DataSnapshot dataSnapshot : dsList) {					 
					 if (dataSnapshot.getKey().equals("xRel")){
						 users.get(place).setxRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("yRel")){
						 users.get(place).setyRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("RoundTripTo")){
						 myFirebaseRef.child(arg0.getKey()).child("RoundTripBack").setValue((long)dataSnapshot.getValue()+1);
					 }
				 }
				 repaint(); // repaint() when changed
			}
			
			//We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()){
					//System.out.println("ADD user with Key: "+arg1+ arg0.getKey());
					Random r = new Random();
					int x = r.nextInt(getSize().width); // spawn 
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
	
	//Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {
		//super.paint(g);   
		Random r = new Random();
		particles.add(new Particle(r.nextInt(getSize().width),0));
		Graphics2D g2= (Graphics2D) g;
		g2.setFont(font);
		//g2.setColor(Color.LIGHT_GRAY);
		g2.setPaint(new Color(255,255,255,10));  
		g2.fillRect(0, 0, getSize().width, getSize().height); // repaint background
		g2.setColor(Color.BLACK);
		g2.drawString("ScreenNbr: "+Constants.screenNbr+ "   particles:"+ particles.size(), 10,  20);
		//Test
		for (User user : users) {
			int x = (int)(user.getxRel()*getSize().width);
			int y = (int)(user.getyRel()*getSize().height);
			int x2;
			int y2;
			
			g2.setColor(user.getColor());
			g2.fillOval(x-50,y-50, 100, 100);
			
			x2 =(int)(user.getpxRel()*getSize().width);
			y2 =(int)(user.getpyRel()*getSize().height);
			g2.setColor(Color.BLUE);
			g2.fillOval(x2-25,y2-25, 50, 50);
			user.setpxRel(user.getxRel());
			user.setpyRel(user.getyRel());
			
			
			
			g2.setColor(Color.BLACK);
			g.drawString(user.getId(),x+15,y+15);
			
			for(Particle p: particles){ // run all particles
				p.update();
				p.display(g2);
			  if(p.y>getSize().height){
				  particles.remove(p);
				 // g2.fillOval((int)p.x,(int)p.y, 100, 100);
			  }
			}
			/*for( int i=0; i<particles.size(); i++){ // run all particles
				particles.get(i).update();
				particles.get(i).display(g2);
			  if(particles.get(i).y>getSize().height)particles.remove(i);
			}*/
		}

		
	}

	@Override
	public void run() {
	
	            	 repaint();

	}

}

	
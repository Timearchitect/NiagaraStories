package se.mah.k3;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

import java.util.EventObject;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

//Mergetest

public class DrawPanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;
	private Firebase myFirebaseRef;
	private Firebase regularWordsRef;
	public ArrayList<Particle> particles = new ArrayList<Particle>();
	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users = new Vector<User>();
	Font font = new Font("Verdana", Font.BOLD, 20);
	private Random r = new Random(); // randomize siffror
	private Color backgroundColor =new Color(255,255,255,10);
    public static int myFrame = 0;
    
	// Creates an instance of the word object
    Word w = new Word("ord1");
	
	
	public DrawPanel() {
		w.x=500;
		w.y=300;
		//myFirebaseRef = new Firebase("https://blinding-heat-7399.firebaseio.com/"); // mattias/Lars
				myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/");  // Root
				regularWordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/regularWords"); // Regular Words Tree
				myFirebaseRef.removeValue(); //Cleans out everything
				
				// Run method to generate "general" words
				createRegularWords();
				
				// Run method to generate "themed" words
				createThemeWords();
				
				// Run method that listens for change in word list (active words for example).
				wordListener();
				
				// use method getText from the word class to set text to "word1" in the firebase db. 
				myFirebaseRef.child("Word1").setValue(w.getText());
			//	myFirebaseRef.child("Word1").addListenerForSingleValueEvent(new ValueEventListener() {

				myFirebaseRef.child("Word1").addValueEventListener(new ValueEventListener() {
				    @Override
				    public void onDataChange(DataSnapshot snapshot) {
				        System.out.println(snapshot.getValue());
				        w.setText(snapshot.getValue().toString());
				    }
				    @Override
				    public void onCancelled(FirebaseError firebaseError) {
				    }
				});
				
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
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0)); //Find the user username has to be unique uses the method compareTo in User
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
		//super.paint(g);    // no opacity repaint
		 int WIDTH = (int)getSize().width;
		 int HEIGHT = (int)getSize().height;
		particles.add(new Particle(r.nextInt(WIDTH),0));
		particles.add(new Particle(r.nextInt(WIDTH),0));
		Graphics2D g2= (Graphics2D) g; // grafik object beh�vs f�r att canvas ska paint p�
		g2.setFont(font); // init typsnitt
		//g2.setColor(Color.LIGHT_GRAY);
	    g2.setPaint(backgroundColor);  // color it med opacity  
		//g2.setPaint(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));  // color it med opacity  
		g2.fillRect(0, 0, WIDTH, HEIGHT); // repaint background
		g2.setColor(Color.BLACK); // svart system color
		g2.drawString("ScreenNbr: "+Constants.screenNbr+ "   particles:"+ particles.size() + "  frame :"+myFrame, 10,  20);
		
		//Test
		for (User user : users) {


			
			int x = (int)(user.getxRel()*WIDTH); // skalad x pos
			int y = (int)(user.getyRel()*HEIGHT); // skalad y pos
			int x2;
			int y2;
			
			g2.setColor(user.getColor());
			g2.fillOval(x-50,y-50, 100, 100);
			
			x2 =(int)(user.getpxRel()*WIDTH);
			y2 =(int)(user.getpyRel()*HEIGHT);
			g2.setColor(Color.BLUE);
			g2.fillOval(x2-25,y2-25, 50, 50);
			user.setpxRel(user.getxRel());
			user.setpyRel(user.getyRel());
			
			g2.setColor(Color.BLACK);
			g2.drawString(w.getText(),x,y);
			g.drawString(user.getId(),x+15,y+15);
			
			/*for( int i=0; i<particles.size(); i++){ // run all particles
				particles.get(i).update();
				particles.get(i).display(g2);
			  if(particles.get(i).y>getSize().height)particles.remove(i);
			}*/
		}
		
		
		for(int i=0; i<particles.size();i++){ // run all particles
			particles.get(i).update();
			particles.get(i).display(g2);
			if(particles.get(i).y>HEIGHT){
		
			//  if(particles.get(i).y>500){

			particles.remove(i);
			 //System.out.println("removed!!!");
			  // g2.fillOval((int)p.x,(int)p.y, 100, 100);
		  }
			
			
			//for(Word w:words){
				particles.get(i).collisionCircle(w.x,w.y);
				
			//}
		}
		/*for(Particle p: particles){ // run all particles
			p.update();
			p.display(g2);
			  if(p.y>500){

			particles.remove(p);
			 System.out.println("removed!!!");
			  // g2.fillOval((int)p.x,(int)p.y, 100, 100);
		  }
		}*/
	
	}
	
    public void run() {
       // while(DrawPanel.myFrame < 1000){
            while(true){
 	
            try{
                // System.out.println("Expl Thread: "+(++DrawPanel.myFrame));
                repaint(); // repaint() 
                Thread.sleep(4);
            } catch (InterruptedException iex) {
                System.out.println("Exception in thread: "+iex.getMessage());
            }
        }
    }
    public void update( Graphics  g )
    {
          paint( g );
    }
public void createRegularWords(){
	 Firebase wordList = myFirebaseRef.child("Regular Words");
	String[] regularWords = {"hey!", "let's", "go", "to", "the", "park", "and", "have", "an", "ice cream"};
for (int i=0; i < regularWords.length; i++){	
wordList.child("word"+i+"/text").setValue(regularWords[i]);

}
}
public void createThemeWords(){
	 Firebase themedWords = myFirebaseRef.child("Themed Words");
	String[] themeWords = {"DNS","floppy", "gamer", "geek", "tech", "firewall", "router", "java", "code", "brainstorm", "laser"};
for (int i=0; i < themeWords.length; i++){	
themedWords.child("word"+i+"/text").setValue(themeWords[i]);

}


}

// Method to listen for updates in the words list
private void wordListener() {

     // Creating a ref to a random child in the Regular Words tree on firebase
    Firebase fireBaseWords = myFirebaseRef.child("Regular Words");

    // Adding a child event listener to the firebasewords ref, to check for active words

    fireBaseWords.addChildEventListener(new ChildEventListener() {
		
		@Override
		public void onChildRemoved(DataSnapshot arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onChildMoved(DataSnapshot arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onChildChanged(DataSnapshot snapshot, String arg1) {
			// TODO Auto-generated method stub
			String isActive = "inactive";
			String changedWord = (String) snapshot.child("text").getValue().toString();
			if(snapshot.child("Active").getValue().toString()=="true"){
				 isActive = "active!";
				}
			
			System.out.println("Change in child! The word "+"\""+changedWord+"\""+" is now "+isActive);
		}
		
		@Override
		public void onChildAdded(DataSnapshot arg0, String arg1) {
			// TODO Auto-generated method stub
			System.out.println("child added");
		}
		
		@Override
		public void onCancelled(FirebaseError arg0) {
			// TODO Auto-generated method stub
			
		}
	}); 

}

public void MouseEvent(Event e) {
	
}


}



package se.mah.k3;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DrawPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	private Firebase myFirebaseRef;
	private Firebase regularWordsRef;
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public static ArrayList<Particle> overParticles = new ArrayList<Particle>();
	public static ArrayList<Word> words = new ArrayList<Word>();
	// A vector is like an ArrayList a little bit slower but Thread-safe. This
	// means that it can handle concurrent changes.
	private Vector<User> users = new Vector<User>();
	Graphics2D g2;
   static Font font = new Font("Verdana", Font.BOLD, 20);
	private Random r = new Random(); // randomize siffror

	Image bg = Toolkit.getDefaultToolkit().getImage("images/background.png");
	// private Color backgroundColor =new Color(255,255,255,10);
	public static int myFrame;
	int WIDTH=1800,HEIGHT=1000;
	// Creates an instance of the word object

	public String changedWord = "word";
	// Word w = new Word(changedWord);

	String wordBg = "#009688";
	Color wordBackground = (hexToRgb(wordBg));
	int margin = 10;

	public static Color hexToRgb(String colorString) {
		return new Color(Integer.valueOf(colorString.substring(1, 3), 16),
				Integer.valueOf(colorString.substring(3, 5), 16),
				Integer.valueOf(colorString.substring(5, 7), 16));
	}


	public DrawPanel() {
		try{
		    font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Roboto-Light.ttf")).deriveFont(24f);
		    
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		/*w.x = 800;
		w.y = 400;
		w.w = 100;
		w.h = 60;
		w.active = true;
		 */
		// myFirebaseRef = new Firebase("https://blinding-heat-7399.firebaseio.com/"); // mattias/Lars
		myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/"); // Root
		regularWordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/regularWords"); // Regular Words Tree
		myFirebaseRef.removeValue(); // Cleans out everything

		// Run method to generate "general" words
		createRegularWords();

		// Run method to generate "themed" words
		createThemeWords();

		// Run method that listens for change in word list (active words for example).
		wordListener();

		// use method getText from the word class to set text to "word1" in the
		// firebase db.
// myFirebaseRef.child("Word1").setValue(w.getText());
		// myFirebaseRef.child("Word1").addListenerForSingleValueEvent(new
		// ValueEventListener() {

/*		myFirebaseRef.child("Word1").addValueEventListener(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot snapshot) {
						System.out.println(snapshot.getValue());
						w.setText(snapshot.getValue().toString());

						if (snapshot.getKey().equals("Active")) {
							w.active = Boolean.parseBoolean((String) snapshot
									.getValue());
							System.out.println(snapshot.getValue());
						}

					}

					@Override
					public void onCancelled(FirebaseError firebaseError) {
					}
				});
*/
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr); // Has to be same as on the app. So place specific can't you see the screen you don't know the number
		myFirebaseRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			// A user changed some value so update
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				Iterable<DataSnapshot> dsList = arg0.getChildren();

				Collections.sort(users);
				int place = Collections.binarySearch(users,
						new User(arg0.getKey(), 0, 0)); // Find the user username has to be unique uses the method compareTo in User
				for (DataSnapshot dataSnapshot : dsList) {
					if (dataSnapshot.getKey().equals("xRel")) {
						users.get(place).setxRel(
								(double) dataSnapshot.getValue());
					}
					if (dataSnapshot.getKey().equals("yRel")) {
						users.get(place).setyRel(
								(double) dataSnapshot.getValue());
					}
					if (dataSnapshot.getKey().equals("RoundTripTo")) {
						myFirebaseRef.child(arg0.getKey())
								.child("RoundTripBack")
								.setValue((long) dataSnapshot.getValue() + 1);
					}
				}
				repaint(); // repaint() when changed
			}

			// We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()) {
					// System.out.println("ADD user with Key: "+arg1+
					// arg0.getKey());
					// Random r = new Random();
					int x = r.nextInt(getSize().width + 1); // spawn
					int y = r.nextInt(getSize().height + 1);
					User user = new User(arg0.getKey(), x, y);
					if (!users.contains(user)) {
						users.add(user);
						user.setColor(new Color(r.nextInt(255), r.nextInt(255),
								r.nextInt(255)));
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {

			}
		});

	}

	// Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {

		WIDTH = (int) getSize().width;
		HEIGHT = (int) getSize().height;
		for(int i=0;i<4;i++){
			 particles.add(new Particle(r.nextInt(WIDTH),0)); 
		}
		g2 = (Graphics2D) g; // grafik object beh�vs f�r at // canvas ska paint p�
		g2.drawImage(bg, 0, 0, WIDTH + 1, HEIGHT + 1, this);
		g2.setFont(font); // init typsnitt
		FontMetrics metrics = g2.getFontMetrics(font);
		/*w.w = metrics.stringWidth(w.text);
		w.h = metrics.getHeight();*/
		for (Word word : words) {
			word.w = metrics.stringWidth(word.text);
			word.h = metrics.getHeight();
		}
		//smooth font
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		// get the advance of my text in this font
		// and render context

		g2.setColor(Color.BLACK); // svart system color
		g2.drawString("ScreenNbr: " + Constants.screenNbr + "   particles:"+ particles.size() + "  frame :" + myFrame + "      words: "+ words.size(), 10, 20);

		// BufferedImage tmpImg = new BufferedImage(bg.getWidth(this) + 1,
		// bg.getHeight(this) + 1, BufferedImage.TYPE_INT_ARGB);
		// Graphics2D g2d = (Graphics2D) tmpImg.getGraphics();
		// g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
		// set Transparency level from 0.0f - 1.0f
		// g2d.drawImage(bg, 0, 0, null);
		// bg = tmpImg;

		/*for (User user : users) {
			int x = (int) (user.getxRel() * WIDTH); // skalad x pos
			int y = (int) (user.getyRel() * HEIGHT); // skalad y pos
			int x2;
			int y2;

			g2.setColor(user.getColor());
			g2.fillOval(x - 50, y - 50, 100, 100);

			x2 = (int) (user.getpxRel() * WIDTH);
			y2 = (int) (user.getpyRel() * HEIGHT);
			g2.setColor(Color.BLUE);
			g2.fillOval(x2 - 25, y2 - 25, 50, 50);
			user.setpxRel(user.getxRel());
			user.setpyRel(user.getyRel());

			g2.setColor(Color.BLACK);
			g2.drawString(w.getText(), x, y);
			g.drawString(user.getId(), x + 15, y + 15);

		}*/

		for (int i = particles.size()-1; 0<i ; i--) {  // run all particles
			particles.get(i).update();
			particles.get(i).display(g2);
			if (particles.get(i).y > HEIGHT) {
				particles.remove(i);
			}
			for (Word word : words) {
				if (word.active)particles.get(i).collisionCircle(word.x, word.y,margin);
			}

			//if (w.active)particles.get(i).collisionCircle(w.x, w.y,margin); //

		}

		for (Word word : words) {
			if (word.active) {
				g2.setColor(wordBackground);
				g2.fillRect((int) (word.x + 3 - (word.w * 0.5)) - margin,(int) (word.y + 3 - (word.h * 0.5) - margin * 0.5), word.w+ margin * 2, word.h + 6);
				g2.setColor(Color.white);
				g2.drawString(word.getText(), (int) (word.x - word.w * 0.5),(int) (word.y + word.h * 0.25));
			}
		}
		
		/*if (w.active) {
			g2.setColor(wordBackground);
			g2.fillRect((int) (w.x + 3 - (w.w * 0.5)) - margin, (int) (w.y + 3 - (w.h * 0.5) - margin * 0.5), w.w + margin * 2, w.h + 6);
			g2.setColor(Color.white);
			g2.drawString(w.getText(), (int) (w.x - w.w * 0.5),(int) (w.y + w.h * 0.25));
		}*/
		
		
		for (int i = overParticles.size()-1; 0 <i ; i--) { // run all overparticles
			overParticles.get(i).update();
			overParticles.get(i).display(g2);
			if(overParticles.get(i).kill)overParticles.remove(i);
		}

	}

	public void run() { // threa

		while (true) {
			try {
				repaint(); // repaint()
				Thread.sleep(3);
			} catch (InterruptedException iex) {
				System.out.println("Exception in thread: " + iex.getMessage());
			}
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void createRegularWords() {
		int count = 0;
		Firebase wordList = myFirebaseRef.child("Regular Words");
		String[] regularWords = { "easier", "interesting", "honest", "forests", "Saturday", "dinner", "comfortable", "gently", "fresh", "rest", "pal", "warmth", "rest", "welcome", "dearest", "useful", "safe", "better", "piano", "silk", "relif", "ryhme", "hi", "agree", "water", "pal" };
		for (int i = 0; i < regularWords.length; i++) {
			wordList.child("word" + i + "/text").setValue(regularWords[i]);
			wordList.child("word" + i + "/Active").setValue(false);
			words.add(new Word(regularWords[i]));
			words.get(words.size() - 1).x = r.nextInt(WIDTH+1); // skalad x pos
			words.get(words.size() - 1).y = r.nextInt(HEIGHT+1); // skalad y pos
			count++;
		}
		myFirebaseRef.child("Regular Words Size").setValue(count);
	}

	public void createThemeWords() {
		int count = 0;
		Firebase themedWords = myFirebaseRef.child("Themed Words");
		String[] themeWords = { "DNS", "floppy", "gamer", "geek", "tech","firewall", "router", "java", "code", "brainstorm", "laser" };
		for (int i = 0; i < themeWords.length; i++) {
			themedWords.child("word" + i + "/text").setValue(themeWords[i]);
			words.add(new Word(themeWords[i]));
			words.get(words.size() - 1).x = r.nextInt(WIDTH+1); // skalad x pos
			words.get(words.size() - 1).y = r.nextInt(HEIGHT+1); // skalad y pos
			count++;
		}
		myFirebaseRef.child("Themed Words Size").setValue(count);

	}

	// Method to listen for updates in the words list
	private void wordListener() {

		// Creating a ref to a random child in the Regular Words tree on
		// firebase
		Firebase fireBaseWords = myFirebaseRef.child("Regular Words");

		// Adding a child event listener to the firebasewords ref, to check for
		// active words

		fireBaseWords.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildChanged(DataSnapshot snapshot, String arg1) {

				//String isActive = "inactive";
				String isActive = "";

				changedWord = (String) snapshot.child("text").getValue().toString();
				if (snapshot.child("Active").getValue().toString() == "true") {
					isActive = "true";
					// words.get().active=true;
				}else{
					isActive = "false";
				}
				String s = snapshot.getRef().toString();
				int index=Integer.parseInt(s.substring(63));
				if(!isActive.equals("true")){ // listen for active changes and execute appear/disappear
					words.get(index).active = true;
					words.get(index).appear(DrawPanel.this);
			
				}else{
					words.get(index).active = false;
					words.get(index).disappear(DrawPanel.this);
				}
				
				System.out.println(index);
				System.out.println("Change in child! The word " + "\""+ changedWord + "\"" + " is now " + isActive);

			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				System.out.println("child added");
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});

	}

	public void MouseEvent(Event e) {

	}

}

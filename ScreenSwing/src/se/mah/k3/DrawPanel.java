package se.mah.k3;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import se.mah.k3.particles.Particle;
import se.mah.k3.particles.RippleParticle;
import se.mah.k3.particles.SplashParticle;
import se.mah.k3.particles.WaterParticle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DrawPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private Firebase myFirebaseRef, regularWordsRef, themedWordsRef;
	// A vector is like an ArrayList a little bit slower but Thread-safe. This
	// means that it can handle concurrent changes.
	private Vector<User> users = new Vector<User>();
	private Random r = new Random(); // randomize numbers
	private Image bg = Toolkit.getDefaultToolkit().getImage("images/background.png");
	// mouse variable
	private float offsetX, offsetY, mouseX, mouseY;
	boolean hold;
	Word selectedWord;
	public static ArrayList<Particle> particles = new ArrayList<Particle>(), overParticles = new ArrayList<Particle>();
	public static ArrayList<Word> words = new ArrayList<Word>();
	public static Graphics2D g2;
	public static int myFrame;
	
	// GrapicsConfig
	private GraphicsConfiguration config =
			GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration();
	
	// create a hardware accelerated image
	public final BufferedImage create(final int width, final int height,
			final boolean alpha) {
		return config.createCompatibleImage(width, height, alpha
				? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}

	public DrawPanel() {
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseX= e.getX();
				mouseY= e.getY();

				if (e.getButton() == MouseEvent.NOBUTTON) {
					System.out.println(" no button clicked");
				} else if (e.getButton() == MouseEvent.BUTTON1) {

					System.out.println(" left button clicked");
					for(Word word: words){
						if(word.active){
							if(word.xPos + word.margin + (word.width * 0.5) > mouseX && word.xPos - word.margin - (word.width * 0.5) < mouseX && word.yPos + word.margin + (word.height * 0.5) > mouseY && word.yPos - word.margin - (word.height * 0.5) < mouseY) {
								selectedWord = word;
								selectedWord.selected();
								offsetX = word.xPos - mouseX;
								offsetY = word.yPos - mouseY;
							} 
						}
					}

					overParticles.add( new RippleParticle((int)mouseX, (int)mouseY, 40));
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					System.out.println(" middle button clicked");

					for(Word word: words){
						word.respond();
					}

					overParticles.add( new RippleParticle((int)mouseX, (int)mouseY, 200));
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					System.out.println(" right button clicked");
					overParticles.add(new SplashParticle((int)mouseX, (int)mouseY));

					for(Word word: words){
						if(word.xPos + word.margin + (word.width * 0.5) > mouseX && word.xPos - word.margin - (word.width * 0.5) < mouseX && word.yPos + word.margin + (word.height * 0.5) > mouseY && word.yPos - word.margin - (word.height * 0.5) < mouseY) {
							
							if(word.active) {
								word.disappear();
							}else {
								word.appear();
							}
						} 
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				mouseX=e.getX();
				mouseY=e.getY();

				if (e.getButton() == MouseEvent.NOBUTTON) {
					System.out.println(" no button Release");
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if(selectedWord != null){
						selectedWord.released();
						selectedWord=null;
					}

					overParticles.add( new RippleParticle((int)mouseX,(int)mouseY));
					System.out.println(" left button Release");

				} else if (e.getButton() == MouseEvent.BUTTON2) {
					System.out.println(" middle button Release");
				}
			}
		});

		this.addMouseMotionListener (new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent ev) {
				mouseX=ev.getX();
				mouseY=ev.getY();

				if (SwingUtilities.isLeftMouseButton(ev)) {
					System.out.println("left");
					if(selectedWord!=null){
						selectedWord.xPos=(int) (mouseX+offsetX);
						selectedWord.yPos=(int) (mouseY+offsetY);
					}

					overParticles.add( new RippleParticle((int) mouseX,(int) mouseY,20));
				}

				if (SwingUtilities.isMiddleMouseButton(ev)) {
					System.out.println("middle");
				}

				if (SwingUtilities.isRightMouseButton(ev)) {
					System.out.println("right");
				}
			}
		});

		myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/"); // Root
		regularWordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/regularWords");
		themedWordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/themedWords");
		myFirebaseRef.removeValue(); // Cleans out everything

		createRegularWords();
		createThemeWords();

		// Run method that listens for change in word list (active words for example).
		wordListener();

		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr);

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
				repaint();
			}

			// We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()) {
					// System.out.println("ADD user with Key: "+arg1+
					// arg0.getKey());

					int x = r.nextInt(getSize().width + 1); // spawn
					int y = r.nextInt(getSize().height + 1);

					User user = new User(arg0.getKey(), x, y);

					if (!users.contains(user)) {
						users.add(user);
						user.setColor(new Color(r.nextInt(255), r.nextInt(255),r.nextInt(255)));
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
		Constants.screenWidth = (int) getSize().width;
		Constants.screenHeight = (int) getSize().height;

		g2 = (Graphics2D) g; // graphics object needed for canvas to paint
		g2.drawImage(bg, 0, 0, Constants.screenWidth, Constants.screenHeight, this);
		FontMetrics metrics = g2.getFontMetrics(Constants.font);

		for (Word word : words) {
			word.width = metrics.stringWidth(word.text);
			word.height = metrics.getHeight();
		}
		
		//smooth font
		g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// get the advance of my text in this font
		// and render context

		for(int i = 0; i < 8; i++) {
			particles.add(new WaterParticle((int)r.nextInt(Constants.screenWidth), 0)); 
		}

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

		for (int i = particles.size() - 1; 0 < i; i--) {  // run all particles
			particles.get(i).update();
			particles.get(i).display(g2);

			for (Word word : words) {
				if (word.active)particles.get(i).collisionCircle(word.xPos, word.yPos, word.margin);
			}

			if (particles.get(i).y > Constants.screenHeight ) {
				particles.get(i).kill();
			}

			if(particles.get(i).dead)particles.remove(i);
		}

		for (Word word : words) {
			if (word.active) {
				word.update();
				word.display();
			}
		}

		for (int i = overParticles.size() - 1; 0 < i; i--) { // run all overparticles
			overParticles.get(i).update();
			overParticles.get(i).display(g2);

			if(overParticles.get(i).dead)overParticles.remove(i);
		}

		g2.setColor(Color.BLACK);
		g2.setFont(Constants.boldFont); // Initialize font
		g2.drawString("ScreenNbr: " + Constants.screenNbr + "   particles:"+ particles.size() + "  frame :" + myFrame + "      words: "+ words.size(), 20, 40);
	}

	public void run() { // threading
		while (true) {
			try {
				repaint(); // repaint()
				Thread.sleep(2);
			} catch (InterruptedException iex) {
				//System.out.println("Exception in thread: " + iex.getMessage());
			}
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void createRegularWords() {
		Firebase wordList = myFirebaseRef.child("Regular Words");
		String[] regularWords = { 
				"easier", 
				"interesting", 
				"honest", 
				"forests", 
				"Saturday", 
				"dinner", 
				"comfortable", 
				"gently", 
				"fresh", 
				"rest", 
				"pal", 
				"warmth", 
				"rest", 
				"welcome", 
				"dearest", 
				"useful", 
				"safe", 
				"better", 
				"piano", 
				"silk", 
				"relif", 
				"ryhme", 
				"android", 
				"agree", 
				"water", 
				"prototype" };

		int count = 0;

		for (int i = 0; i < regularWords.length; i++) {
			wordList.child("word" + i + "/text").setValue(regularWords[i]);
			wordList.child("word" + i + "/Active").setValue(false);
			words.add(new Word(regularWords[i],DrawPanel.this));
			words.get(words.size() - 1).xPos = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			words.get(words.size() - 1).yPos = r.nextInt(Constants.screenHeight + 1); // skalad y pos
			count++;
		}

		myFirebaseRef.child("Regular Words Size").setValue(count);
	}

	public void createThemeWords() {
		Firebase themedWords = myFirebaseRef.child("Themed Words");
		String[] themeWords = { 
				"DNS", 
				"floppy", 
				"gamer", 
				"geek", 
				"tech", 
				"firewall", 
				"router", 
				"java", 
				"code", 
				"brainstorm",
				"laser" };

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			themedWords.child("word" + i + "/text").setValue(themeWords[i]);
			words.add(new Word(themeWords[i],DrawPanel.this));
			words.get(words.size() - 1).xPos = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			words.get(words.size() - 1).yPos = r.nextInt(Constants.screenHeight + 1); // skalad y pos
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
				String word = "word";
				String isActive = "";
				String s = snapshot.getRef().toString();

				int index=Integer.parseInt(s.substring(63));

				word = (String) snapshot.child("text").getValue().toString();

				if (snapshot.child("x").getValue() != null) {
					words.get(index).xPos=(int) (Float.valueOf(snapshot.child("x").getValue().toString()) * Constants.screenWidth);
					System.out.println("x is written to "+ index + "  word "+words.get(index).xPos);
				}

				if (snapshot.child("y").getValue() != null) {
					words.get(index).yPos=(int)  (Float.valueOf(snapshot.child("y").getValue().toString()) * Constants.screenHeight);
					System.out.println("y is written to "+ index + "  word "+words.get(index).yPos);
				}

				if (snapshot.child("Active").getValue().toString() == "true") {
					isActive = "true";
					words.get(index).appear();
				}else {
					isActive = "false";
					words.get(index).disappear();
				}

				System.out.println(index+" Change in child! The word " + "\""+ word + "\"" + " is now " + isActive);
			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});
	}
}
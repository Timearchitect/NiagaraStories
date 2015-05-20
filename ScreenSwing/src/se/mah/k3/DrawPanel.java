package se.mah.k3;

import java.awt.AlphaComposite;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
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
	private static ArrayList<User> userList = new ArrayList<User>();
	private Random r = new Random(); // randomize numbers
	public static Graphics2D g2;
	//private Image bg = Toolkit.getDefaultToolkit().getImage("images/background.bmp");
	public static BufferedImage bimage, mist;
	public static int myFrame; 
	 // mouse variable
	public String changedWord = "word";
	private float offsetX,offsetY,mouseX,mouseY,pMouseX,pMouseY;
	boolean hold;
	Word selectedWord;
	public static ArrayList<Particle> particles = new ArrayList<Particle>(), overParticles = new ArrayList<Particle>();
	public static ArrayList<Word> words = new ArrayList<Word>();
	User user;
	boolean onesRun=true;
	
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
    public void setup(){
    	Constants.screenWidth = (int) getSize().width;
		Constants.screenHeight = (int) getSize().height;
		FontMetrics metrics = g2.getFontMetrics(Constants.font);
		for (Word word : words) { // ini words height
			word.width = metrics.stringWidth(word.text);
			word.height = metrics.getHeight();
		}
		//smooth font
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    	onesRun=false;
    }
    
	public DrawPanel() {
    //     bimage = null;
        try {
        	bimage = ImageIO.read(new File("images/background.bmp"));
        	mist = ImageIO.read(new File("images/mist.png"));
        } catch (IOException e) {
        		System.out.println("no");
        }
		this.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				mouseX= e.getX();
				mouseY= e.getY();
				
				if (e.getButton() == MouseEvent.NOBUTTON) {
					//System.out.println(" no button clicked");
				} else if (e.getButton() == MouseEvent.BUTTON1) {

					//System.out.println(" left button clicked");
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
					//System.out.println(" middle button clicked");

					for(Word word: words){
						word.respond();
					}

					overParticles.add( new RippleParticle((int)mouseX, (int)mouseY, 200));
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					//System.out.println(" right button clicked");
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

			public void setOwnership(String owner) {
				selectedWord.setOwner(user.getId());
				Firebase wordList = myFirebaseRef.child("Regular Words");

				for (int i = 0; i < words.size(); i++) {
					wordList.child("word" + i + "/Owner").setValue(selectedWord.getOwner());
				}

				System.out.println("\"" + selectedWord.getText() + "\" " + "is now owned by " + selectedWord.getOwner());
			}

			public void mouseReleased(MouseEvent e) {
				mouseX=e.getX();
				mouseY=e.getY();

				if (e.getButton() == MouseEvent.NOBUTTON) {
					//System.out.println(" no button Release");
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if(selectedWord != null){
						selectedWord.released();
						selectedWord=null;
					}

					overParticles.add( new RippleParticle((int)mouseX,(int)mouseY));


				} else if (e.getButton() == MouseEvent.BUTTON2) {
				}
			}
		});

		this.addMouseMotionListener (new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent ev) {
				mouseX=ev.getX();
				mouseY=ev.getY();

				if (SwingUtilities.isLeftMouseButton(ev)) {
					//System.out.println("left");
					if(selectedWord!=null){
						selectedWord.xPos=(int) (mouseX+offsetX);
						selectedWord.yPos=(int) (mouseY+offsetY);
					}

					overParticles.add( new RippleParticle((int) mouseX,(int) mouseY,20));
				}

				if (SwingUtilities.isMiddleMouseButton(ev)) {

				}

				if (SwingUtilities.isRightMouseButton(ev)) {

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

		// use method getText from the word class to set text to "word1" in the
		// firebase db.
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr); // Has to be same as on the app. So place specific can't you see the screen you don't know the number
		myFirebaseRef.child("ScreenWidth").setValue(1000); // Has to be same as on the app. So place specific can't you see the screen you don't know the number
		myFirebaseRef.child("ScreenHeight").setValue(800); // Has to be same as on the app. So place specific can't you see the screen you don't know the number
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
				//System.out.println(arg0.getKey()+"  vem där?");
				
				if (arg0.getKey().equals("Users") && arg0.hasChildren()) {
					
				for (DataSnapshot dataSnapshot : dsList) {
					User u =new User(dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child("xRel").getValue().toString()), Float.parseFloat( dataSnapshot.child("yRel").getValue().toString()));

					for(User ul:userList){
							if(ul.getId().equals(u.getId())){ // check if it has the same ID
								//ul.xTar = u.xPos;
								//ul.yTar = u.yPos;
								ul.xTar = u.xTar;
								ul.yTar = u.yTar;
								u=null;
							}
						}	
				
					if ( u!=null){
						userList.add(u);
						u.setColor(new Color(r.nextInt(255), r.nextInt(255),r.nextInt(255)));
						System.out.println("Add user");
						System.out.println(dataSnapshot.getKey());
					}
				}
					/*if (dataSnapshot.getKey().equals("xRel")) {
						users.get(place).setxRel(
								(double) dataSnapshot.getValue() * Constants.screenWidth);
					}

					if (dataSnapshot.getKey().equals("yRel")) {
						users.get(place).setyRel(
								(double) dataSnapshot.getValue() * Constants.screenHeight);
					}
					 
					if (dataSnapshot.getKey().equals("id")) {
						users.get(place).setId(dataSnapshot.getValue().toString());
						System.out.println("User id: " + dataSnapshot.getValue().toString());
					}

					if (dataSnapshot.getKey().equals("RoundTripTo")) {
						myFirebaseRef.child(arg0.getKey())
						.child("RoundTripBack")
						.setValue((long) dataSnapshot.getValue() + 1);
					}*/
					
				}
				repaint();
			}

			// Add user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				/*if (arg0.hasChildren()) {
					// System.out.println("ADD user with Key: "+arg1+
					// arg0.getKey());
					// Random r = new Random();
					int x = r.nextInt(getSize().width+1 ); // spawn
					int y = r.nextInt(getSize().height+1);
					User user = new User(arg0.getKey(), x, y);
					if (!users.contains(user)) {
						users.add(user);
						user.setColor(new Color(r.nextInt(255), r.nextInt(255),r.nextInt(255)));
					}
				}*/
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});
	}

	// Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {
		g2 = (Graphics2D) g; // grafik object behï¿½vs fï¿½r at // canvas ska paint pï¿½
		if(onesRun)setup();
		// get the advance of my text in this font
		// and render context

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)0.4));
		//Image translucentImage = config.createCompatibleImage(WIDTH, HEIGHT, Transparency.TRANSLUCENT);
			g2.drawImage(bimage, 0, 0, Constants.screenWidth , Constants.screenHeight , this); 
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
		
		for(int i = 0; i < 8; i++) {
			particles.add(new WaterParticle((int)r.nextInt(Constants.screenWidth), 0)); 
		}

		for (int i = particles.size() - 1; 0 < i; i--) {  // run all particles
			particles.get(i).update();
			particles.get(i).display(g2);

			for (Word word : words) {
				if (word.active){
					particles.get(i).collisionCircle(word.xPos, word.yPos, word.margin);
					particles.get(i).collisionRect(word.xPos, word.yPos, word.width,word.height);
					}
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

			for(Particle p:particles){
				overParticles.get(i).collisionVSParticle(p);
			}
			
			if(overParticles.get(i).dead)overParticles.remove(i);
		}

		for (User user : userList) {
			int x = user.getxPos(); // skalad x pos
			int y = user.getyPos() ; // skalad y pos
			user.update();
			user.display(g2);
		}
		
		displayDebugText();
	}

	public void run() { // threading
		while (true) {
			try {
				repaint(); // repaint()
				Thread.sleep(20);
				
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
				"prototype" 
		};

		int count = 0;

		for (int i = 0; i < regularWords.length; i++) {
			wordList.child("word" + i + "/text").setValue(regularWords[i]);
			wordList.child("word" + i + "/Active").setValue(false);
			wordList.child("word" + i + "/Owner").setValue("false");

			words.add(new Word(regularWords[i],DrawPanel.this, null));
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
				"laser" 
		};

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			themedWords.child("word" + i + "/text").setValue(themeWords[i]);
			themedWords.child("word" + i + "/Active").setValue(false);
			themedWords.child("word" + i + "/Owner").setValue("false");

			words.add(new Word(themeWords[i],DrawPanel.this, null));
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
					words.get(index).xPos=(int) (Float.parseFloat(snapshot.child("x").getValue().toString()) * Constants.screenWidth);
					System.out.println("x is written to "+ index + "  word "+words.get(index).xPos);

				}
				
				if (snapshot.child("y").getValue() != null) {
					words.get(index).yPos=(int)  (Float.parseFloat(snapshot.child("y").getValue().toString()) * Constants.screenHeight);
					System.out.println("y is written to "+ index + "  word "+words.get(index).yPos);
				}

				if (snapshot.child("Active").getValue().toString() == "true") {
					isActive = "true";
					words.get(index).appear();
				}else {
					isActive = "false";
					words.get(index).disappear();
				}

				if (isActive == "true") {
					System.out.println("Word number " + index + ", " + "\""+ word + "\"" + " is now active");
				}else {
					System.out.println("Word number " + index + ", " + "\""+ word + "\"" + " is now inactive");

				}
			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});
	}

	public void displayDebugText(){
		g2.setColor(Color.BLACK); // svart system color
		g2.setFont(Constants.boldFont); // init typsnitt
		g2.drawString("ScreenNbr: " + Constants.screenNbr + "   particles:"+ particles.size() + "  frame :" + myFrame + "      words: "+ words.size(), 20, 40);
	}
}
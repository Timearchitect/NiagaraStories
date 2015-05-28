package se.mah.k3;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import se.mah.k3.Projectiles.Projectile;
import se.mah.k3.particles.EqualizerParticle;
import se.mah.k3.particles.Particle;
import se.mah.k3.particles.RippleParticle;
import se.mah.k3.particles.WaterParticle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DrawPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private int FPS,frames;
	private Firebase myFirebaseRef, regularWordsRef, themedWordsRef;
	public static ArrayList<User> userList = new ArrayList<User>();
	private Random r = new Random(); // randomize numbers
	public static Graphics2D g2;
	public static BufferedImage bimage, mist, rust, cracks,moss;
	public static int myFrame; 
	public String changedWord = "word";
	private float offsetX, offsetY, mouseX, mouseY, pMouseX, pMouseY; // mouse variable
	boolean hold;
	Word selectedWord;
	public static ArrayList<Particle> particles = new ArrayList<Particle>(), overParticles = new ArrayList<Particle>();
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
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

		g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		onesRun=false;
		//projectiles.add(new Projectile((int)(Constants.screenWidth*0.5),(int)(Constants.screenHeight*0.5),10,10));

	}

	public DrawPanel() {

		// image creation
		VolatileImage vImg = createVolatileImage(Constants.screenWidth,Constants.screenHeight);


		// rendering to the image


		//     bimage = null;
		try {
			bimage = ImageIO.read(new File("images/background.bmp"));
			mist = ImageIO.read(new File("images/mist.png"));
			rust = ImageIO.read(new File("images/rust.png"));
			moss = ImageIO.read(new File("images/moss.png"));
			cracks = ImageIO.read(new File("images/cracks.png"));
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
								selectedWord.state=Word.State.draging;
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/occupied").setValue(true);
								offsetX = word.xPos - mouseX;
								offsetY = word.yPos - mouseY;
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/occupied").setValue(true);
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/active").setValue(true);
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/xRel").setValue(((float)selectedWord.xPos/Constants.screenWidth));
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/yRel").setValue(((float)selectedWord.yPos/Constants.screenHeight));
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/state").setValue("draging");
								
								
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/text").setValue(selectedWord.text);
								try{
									myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/owner").setValue(selectedWord.owner.getId());
								}catch(Exception err){
									myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/owner").setValue("");

								}
								
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/occupied").setValue(true);
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/active").setValue(true);
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/xRel").setValue(((float)selectedWord.xPos/Constants.screenWidth));
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/yRel").setValue(((float)selectedWord.yPos/Constants.screenHeight));
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/state").setValue("draging");
								
							} 
						}
					}

					overParticles.add( new RippleParticle((int)mouseX, (int)mouseY, 40));

					//overParticles.add( new RippleParticle((int)mouseX, (int)mouseY, 40));
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					//System.out.println(" middle button clicked");

					for(Word word: words){
						word.respond();
					}

					overParticles.add( new RippleParticle((int)mouseX, (int)mouseY, 200));
				} else if (e.getButton() == MouseEvent.BUTTON3) {

					//System.out.println(" right button clicked");
					//overParticles.add(new SplashParticle((int)mouseX, (int)mouseY));
					//overParticles.add(new RustParticle ((int) mouseX, (int) mouseY, selectedWord.getText().length()));

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

				//String wordLength;

				if (e.getButton() == MouseEvent.NOBUTTON) {
					//System.out.println(" no button Release");
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if(selectedWord != null){

						//wordLength = String.valueOf(selectedWord.getText().length());
						selectedWord.released();						
						//overParticles.add(new RustParticle (selectedWord.getXPos() + 3, selectedWord.getYPos() - 4,  200, 100, Integer.valueOf(wordLength)));
						selectedWord.state=Word.State.placed;
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/occupied").setValue(false);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/active").setValue(true);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/xRel").setValue(((float)selectedWord.xPos/Constants.screenWidth));
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/yRel").setValue(((float)selectedWord.yPos/Constants.screenHeight));
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/state").setValue("placed");
						
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/occupied").setValue(false);
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/active").setValue(true);
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/xRel").setValue(((float)selectedWord.xPos/Constants.screenWidth));
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/yRel").setValue(((float)selectedWord.yPos/Constants.screenHeight));
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/state").setValue("placed");
						System.out.println("id placed:"+selectedWord.getWordId());
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
						selectedWord.txPos=(int) (mouseX+offsetX);
						selectedWord.tyPos=(int) (mouseY+offsetY);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/xRel").setValue(selectedWord.getXPos()/Constants.screenWidth);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+"/yRel").setValue(selectedWord.getYPos()/Constants.screenHeight);
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/xRel").setValue(((float)selectedWord.xPos/Constants.screenWidth));
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+"/attributes/yRel").setValue(((float)selectedWord.yPos/Constants.screenHeight));
					}

					overParticles.add( new RippleParticle((int) mouseX,(int) mouseY,10));
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
		//createUsedWords() ;
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
				//System.out.println(arg0.getKey()+"  vem d�r?");
				if (arg0.getKey().equals("Users") && arg0.hasChildren()) {

					/*	for (DataSnapshot dataSnapshot : dsList) {
						User u =new User(dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child("xRel").getValue().toString()), Float.parseFloat( dataSnapshot.child("yRel").getValue().toString()));
						boolean match = false;
						//	System.out.println("!!!!!!!!!!!!!!!!!!!!USER");
						for(User ul:userList){

							if( ul.getId().equals(u.getId())){ // check if it has the same ID
								String state="";
								if( dataSnapshot.child("state").getValue()!=null) state=dataSnapshot.child("state").getValue().toString();
								//ul.xTar = u.xPos;
								//ul.yTar = u.yPos;
								ul.setId(u.getId());
								ul.xTar = u.xTar;
								ul.yTar = u.yTar;
								switch (state){
								case "offline":
									ul.state=User.State.offline;
									System.out.println("offline");
									break;
								case "online":
									ul.state=User.State.online;
									System.out.println("online");

									break;
								case "taping":
									ul.state=User.State.taping;
									//System.out.println("taping: "+ul.getId()+"state: "+ state);
									break;
								default:
								}

								match=true;
							}

						}
						if (!match){
							userList.add(u);
							u.setColor(new Color(r.nextInt(255), r.nextInt(255),r.nextInt(255)));
							System.out.println("Add user");
							System.out.println(dataSnapshot.getKey());
						}
					}	*/

					for (DataSnapshot dataSnapshot : dsList) {
						//User u =new User(dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child("xRel").getValue().toString()), Float.parseFloat( dataSnapshot.child("yRel").getValue().toString()));
						boolean match = false;
						//	System.out.println("!!!!!!!!!!!!!!!!!!!!USER");
						for(User ul:userList){

							if( ul.getId().equals(dataSnapshot.getKey())){ // check if it has the same ID
								String state="";
								if( dataSnapshot.child("state").getValue()!=null) state=dataSnapshot.child("state").getValue().toString();
								//ul.xTar = u.xPos;
								//ul.yTar = u.yPos;
								ul.setId(dataSnapshot.getKey());
								try{
									ul.xTar = Float.parseFloat(dataSnapshot.child("xRel").getValue().toString())*Constants.screenWidth;
									ul.yTar = Float.parseFloat( dataSnapshot.child("yRel").getValue().toString())*Constants.screenHeight;
								}catch(Exception e){}
								try{
									ul.moves = Integer.parseInt( dataSnapshot.child("moves").getValue().toString());
								}catch(Exception e){}
								
								switch (state){
								case "offline":
									ul.state=User.State.offline;
									System.out.println("offline");
									break;
								case "online":
									ul.state=User.State.online;
									System.out.println("online");

									break;
								case "taping":
									ul.state=User.State.taping;
									//System.out.println("taping: "+ul.getId()+"state: "+ state);
									break;
								default:
								}

								match=true;
							}

						}
						if (!match){
							try{
								userList.add(new User(dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child("xRel").getValue().toString()), Float.parseFloat( dataSnapshot.child("yRel").getValue().toString())));
								userList.get(userList.size()-1).setColor(new Color(r.nextInt(255), r.nextInt(255),r.nextInt(255)));
								System.out.println("Add user");
								System.out.println(dataSnapshot.getKey());
							}catch(Exception e){

							}
						}
					}	
				}
				repaint();
			}

			// Add user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});
	}

	// Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {
		g2 = (Graphics2D) g; // grafik object beh�vs f�r at // canvas ska paint p�
		if(onesRun)setup();
		// get the advance of my text in this font
		// and render context

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.4f));
		//Image translucentImage = config.createCompatibleImage(WIDTH, HEIGHT, Transparency.TRANSLUCENT);
		g2.drawImage(bimage, 0, 0, Constants.screenWidth , Constants.screenHeight , this); 
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));

		for(int i = 0; i < 7; i++) {  // spawn particles
			particles.add(new WaterParticle((int)r.nextInt(Constants.screenWidth), 0)); 
		}
		while(particles.size()>Constants.PARTICLE_LIMIT) {  // run all particlesCap
			particles.remove(0);
		}
		while(overParticles.size()>Constants.HEAVY_PARTICLE_LIMIT) {  // run all OverparticlesCap
			overParticles.remove(0);
		}
		while(projectiles.size()>Constants.PROJECTILE_LIMIT) {  // run all OverparticlesCap
			projectiles.remove(0);
		}

		for (int i = particles.size() - 1; 0 < i; i--) {  // run all particles
			particles.get(i).update();
			particles.get(i).display(g2);

			for (Word word : words) { // collision

				if (word.active){
					//	particles.get(i).collisionCircle(word.xPos, word.yPos, word.margin);
					//	particles.get(i).collisionCircle(word.xPos, word.yPos, word.margin);
					if(!Constants.noCollision) particles.get(i).collisionRect(word.xPos, word.yPos, word.width,word.height);
				}
			}
			if(particles.get(i).dead)particles.remove(i);
		}

		if(!Constants.noUser){
			for (User user : userList) { // run all users
				user.update();
				user.display(g2);
			}
		}
		for (Projectile p:	projectiles){ // run all projectiles

			for (Word w : words) {
				if (w.active) {
					p.collision(w);
				}
			}

			p.BoundCollision();
			p.update();
			p.display(g2);
		}

		for (Word word : words) {  // run all words
			if (word.active) {
				word.update();
				word.display();
				if(word.state!=Word.State.draging)word.BoundCollision();
				for(Word word2 : words){ //word collision
					if (word!=word2 && word2.active && word.state!=Word.State.draging && word2.state!=Word.State.draging) {
						word.collisionVSWord(word2);
					}
				}
			}
		}

		
		if(!Constants.simple){
				for (int i = overParticles.size() - 1; 0 < i; i--) { // run all overparticles
				overParticles.get(i).update();
				overParticles.get(i).display(g2);
				for(Word w: words){
					if(w.active)overParticles.get(i).collisionCircle(w.xPos, w.yPos, w.width,w);
				}
				for(Particle p:particles){
					overParticles.get(i).collisionVSParticle(p);
				}
	
				if(overParticles.get(i).dead)overParticles.remove(i);
		}



		}

		displayDebugText();
		g2.dispose();
	}

	public void run() { // threading
		long lastTime = System.nanoTime();
		while (true) {

			repaint(); 
			try {
				Thread.sleep(0);
			} catch (InterruptedException iex) {
				//System.out.println("Exception in thread: " + iex.getMessage());
			}

			frames++;
			if( System.nanoTime() - lastTime>=100000L){
				FPS=(int) (frames*0.3);
				frames=0;
				lastTime=System.nanoTime();
				checkCrowdedScreen();
				if(!Constants.noTimer){
					Constants.cal=Calendar.getInstance();
					Constants.timeLeft=(long) (Constants.clearInterval-((Constants.cal.getTimeInMillis()-Constants.startTime)*0.001));
					if(Constants.timeLeft <0){ // reset timer
						clearScreen();
						Constants.startTime=Constants.cal.getTimeInMillis();
					}
				}
			}
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void createRegularWords() {

		Firebase wordList = myFirebaseRef.child("Regular Words");

		String[] regularWords = { 
				"When",
				"you",
				"use",
				"Taping",
				"calling",
				"draging",
				"mobile",
				"device",
				"should",
				"connect",
				"with",
				"internet",
				"Another",
				"important",
				"thing",
				"is",
				"you",
				"wrong",
				"unable",
				"to",
				"access",
				"Droping",
				"and",
				"some",
				"other",
				"emergency",
				"word",
				"via",
				"user",
				"rippeEffect",
				"If",
				"you",
				"want",
				"to",
				"make",
				"any",
				"not working",
				"login,",
				"logout",
				"animation",
				"make",
				"other",
				"communication",
				"arrangements",
				"How",
				"to",
				"Place",
				"your",
				"Whatsapp",
				"call?",
				"you",
				"can",
				"simply",
				"make",
				"a",
				"call",
				"through",
				"Whatsapp",
				"by",
				"the",
				"way",
				"of",
				"open",
				"the",
				"chat",
				"with",
				"who",
				"do",
				"you",
				"want",
				"call",
				"and",
				"on",
				"the",
				"top",
				"of",
				"the",
				"phone",
				"just",
				"tap",
				"the",
				"phone",
				"button",
				"Next",
				"we",
				"will",
				"see",
				"how",
				"to",
				"reveive",
				"whatsapp",
				"call?",
				"it's",
				"also",
				"quite",
				"simple,",
				"if",
				"someone",
				"calling",
				"you,",
				"while",
				"you",
				"will",
				"see",
				"the",
				"Whatsapp",
				"incoming",
				"call",
				"on",
				"your",
				"phone",
				"screen",
				"Afterward",
				"the",
				"ordinary",
				"phone",
				"call",
				"receving",
				"like",
				"process",
				"you",
				"want",
				"to",
				"do",
				"there",
				"For",
				"example",
				"green",
				"and",
				"red",
				"button",
				"will",
				"show,",
				"you",
				"want",
				"to",
				"attend",
				"the",
				"call",
				"just",
				"slide",
				"the",
				"green",
				"button",
				"or",
				"you",
				"dont",
				"want",
				"to",
				"like",
				"to",
				"answer",
				"that",
				"call",
				"just",
				"slide",
				"the",
				"red",
				"button",
				"then",
				"automatically",
				"call",
				"will",
				"declined",
				"and",
				"then",
				"substituly",
				"you",
				"can",
				"touch",
				"on",
				"the",
				"message",
				"icon",
				"on",
				"the",
				"whatsapp",
				"call",
				"screen",
				"to",
				"stop",
				"the",
				"call",
				"with",
				"rapid",
				"message"
		};

		int count = 0;
		for (int i = 0; i < regularWords.length; i++) {
			String wordId="word" + i ;
			wordList.child(wordId + "/occupied").setValue(false);
			wordList.child(wordId + "/text").setValue(regularWords[i]);
			wordList.child(wordId + "/active").setValue(false);
			wordList.child(wordId + "/owner").setValue("");
			int x=r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y=r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(regularWords[i], null,x,y,x,y));
			words.get(words.size()-1).setWordId(wordId);
			System.out.println(words.get(words.size()-1).getWordId());
			count++;
		}

		myFirebaseRef.child("Regular Words Size").setValue(count);
	}

	public void createThemeWords() {
		Firebase themedWords = myFirebaseRef.child("Themed Words");
		String[] themeWords = { 
				"too."
		};

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			String wordId="word" + i ;
			themedWords.child(wordId + "/text").setValue(themeWords[i]);
			themedWords.child(wordId + "/active").setValue(false);
			themedWords.child(wordId + "/owner").setValue("");
			int x=r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y=r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(themeWords[i], null,x,y,x,y));
			words.get(words.size()-1).setWordId(wordId);
			count++;
		}

		myFirebaseRef.child("Themed Words Size").setValue(count);
	}


	public void createUsedWords() {
		Firebase themedWords = myFirebaseRef.child("Used Words");
		String[] themeWords = { 
				"helloWorld","hejsan","yo","niHao"
		};

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			themedWords.child("word" + i + "/attributes/text").setValue(themeWords[i]);
			themedWords.child("word" + i + "/attributes/active").setValue(false);
			themedWords.child("word" + i + "/attributes/owner").setValue("");
			themedWords.child("word" + i + "/attributes/xRel").setValue(0.5);
			themedWords.child("word" + i + "/attributes/yRel").setValue(0.5);
			int x=r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y=r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(themeWords[i], null,x,y,x,y));
			count++;
		}

		myFirebaseRef.child("Themed Words Size").setValue(count);
	}

	// Method to listen for updates in the words list
	private void wordListener() {
		// Creating a ref to a random child in the Regular Words tree on
		// firebase
		//Firebase fireBaseWords = myFirebaseRef.child("Regular Words");
		Firebase fireBaseWords = myFirebaseRef.child("Used Words");

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
				try{

					String s = snapshot.getRef().toString();
						

					//int index=Integer.parseInt(s.substring(63));// regular
					int index=Integer.parseInt(s.substring(60)); // used words
					
					//word = (String) snapshot.child("text").getValue().toString();

					if (snapshot.child("x").getValue() != null) {
						words.get(index).txPos=(int) (Float.parseFloat(snapshot.child("x").getValue().toString()) * Constants.screenWidth);
					}

					if (snapshot.child("y").getValue() != null) {
						words.get(index).tyPos=(int)  (Float.parseFloat(snapshot.child("y").getValue().toString()) * Constants.screenHeight);
					}

					if (snapshot.child("state").getValue() != null) {
						//System.out.println("State stuff");
						User u = null;
						if(words.get(index).getUser()!=null){ 
							u=words.get(index).getUser();
							switch(snapshot.child("State").getValue().toString()){
							case "placed":
								//	words.get(index).released();
								words.get(index).setState(Word.State.placed);
								u.release();
								System.out.println("placed");
								words.get(index).appear();
								break;

							case "draging":
								//words.get(index).respond();
								if(words.get(index).state!=Word.State.onTray){
									words.get(index).setState(Word.State.draging);
									u.xTar=words.get(index).txPos;
									u.yTar=words.get(index).tyPos;
									u.xPos=(int)words.get(index).txPos;
									u.yPos=(int)words.get(index).tyPos;
									//System.out.println("dragging");
								}else{
									words.get(index).xPos=(int)words.get(index).txPos;
									words.get(index).yPos=(int)words.get(index).tyPos;
									u.xTar=words.get(index).txPos;
									u.yTar=words.get(index).tyPos;
									u.xPos=(int)words.get(index).txPos;
									u.yPos=(int)words.get(index).tyPos;
									words.get(index).respond();
								}

								break;
							case "onTray":

								words.get(index).setState(Word.State.onTray);
								System.out.println("on tray");
								break;

							}
						}
						try{
							if (snapshot.child("plural").getValue() != null) {
								words.get(index).setType(snapshot.child("plural").getValue().toString());
							}
						} catch(Exception e){}

					}

					if(snapshot.child("owner").getValue().toString()!="") {
						words.get(index).setOwner(snapshot.child("Owner").getValue().toString());
						System.out.println(words.get(index).getOwner() + " owns the word " + words.get(index).getText());
					}

				} catch (NullPointerException npe){}
				 catch (NumberFormatException npe){}
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
		if(Constants.debug){
			g2.drawString("ID: " + Constants.screenNbr + " part:"+ particles.size() + " Overpart:"+ overParticles.size() + "  words: "+ words.size() + "  Users:" +userList.size() +"  FPS: "+FPS +"  Time:"+Constants.timeLeft, 30, 50);
			if(Constants.noCollision)g2.drawString("No water collision" , 30, 100);
			if(Constants.noTimer)g2.drawString("No time" , 30, 150);
			if(Constants.noUser)g2.drawString("No user" , 30, 200);
			if(Constants.simple)g2.drawString("simple Mode" , 30, 250);
		}else{
			g2.drawString("Screen ID: " + Constants.screenNbr , 30, 50);
		}
	}
	public static void checkCrowdedScreen(){
		int amount = 0;
		for(Word w: words){
			if(w.active)amount+= w.text.length()+2;
		}		
		if(Constants.spaceOnScreen<amount)clearScreen();
	}

public static void clearScreen(){
	//words.clear();
	overParticles.add(new EqualizerParticle((int)(Constants.screenWidth * 0.5), (int)(Constants.screenHeight * 0.5), 50));
}
}

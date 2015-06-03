package se.mah.k3;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
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

import se.mah.k3.Word.WordBuilder;
import se.mah.k3.Projectiles.Projectile;
import se.mah.k3.particles.EqualizerParticle;
import se.mah.k3.particles.Particle;
import se.mah.k3.particles.RippleParticle;
import se.mah.k3.particles.ScanParticle;
import se.mah.k3.particles.WaterParticle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.snapshot.IndexedNode;

public class DrawPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private int FPS, frames;
	static Firebase myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/"); // Root
	static Firebase regularWordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/Regular Words");
 	static Firebase themedWordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/Themed Words");
	static Firebase firebaseUsedWord=myFirebaseRef.child("/Used Words/");
	static Mouse mouse= new Mouse();
	//static Firebase myFirebaseRef, regularWordsRef, themedWordsRef;
	public static ArrayList<User> userList = new ArrayList<User>();
	private Random r = new Random(); // randomize numbers
	public static Graphics2D g2;
	public static BufferedImage bimage, mist, rust, cracks, moss, app;
	public static int myFrame;
	public String changedWord = "word";
	public static FontMetrics metrics;
	//static float offsetX ; // mouse
	//static float offsetY;
	//static float mouseY,mouseX;
	//private static float pMouseX, pMouseY;
	//boolean hold;
	//static Word selectedWord;
	static boolean collisionSent;

	User user;
	public static ArrayList<Particle> particles = new ArrayList<Particle>(),overParticles = new ArrayList<Particle>();
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public static ArrayList<Word> words = new ArrayList<Word>();

	protected static DataSnapshot choosen; // choosen word in respawn

	boolean onesRun = true;

	private GraphicsConfiguration config = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();

	// create a hardware accelerated image
	public final BufferedImage create(final int width, final int height,
			final boolean alpha) {
		return config.createCompatibleImage(width, height,
				alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}


	public void setup() {
		Constants.screenWidth = (int) getSize().width;
		Constants.screenHeight = (int) getSize().height;
		metrics = g2.getFontMetrics(Constants.font);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		//createStarterWords();
		onesRun = false;
		wordListener();

	}


	// image creation
	VolatileImage vImg = createVolatileImage(Constants.screenWidth,Constants.screenHeight);

	// rendering to the image
	void renderOffscreen() {
		do {
			if (vImg.validate(getGraphicsConfiguration()) ==
					VolatileImage.IMAGE_INCOMPATIBLE)
			{
				// old vImg doesn't work with new GraphicsConfig; re-create it
				vImg = createVolatileImage(Constants.screenWidth,Constants.screenHeight);
			}
			Graphics2D g = vImg.createGraphics();
			//
			// miscellaneous rendering commands...
			//
			g.dispose();
		} while (vImg.contentsLost());



		// copying from the image (here, gScreen is the Graphics
		// object for the onscreen window)
		do {
			int returnCode = vImg.validate(getGraphicsConfiguration());
			if (returnCode == VolatileImage.IMAGE_RESTORED) {
				// Contents need to be restored
				renderOffscreen();      // restore contents
			} else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				vImg = createVolatileImage(Constants.screenWidth,
						Constants.screenHeight);
				renderOffscreen();
			}
			g2.drawImage(vImg, 0, 0, this);
		} while (vImg.contentsLost());

	}

	public DrawPanel() {



		// rendering to the image

		// bimage = null;
		try {
			bimage = ImageIO.read(new File("images/background.bmp"));
			mist = ImageIO.read(new File("images/mist.png"));
			rust = ImageIO.read(new File("images/rust.png"));
			moss = ImageIO.read(new File("images/moss.png"));
			cracks = ImageIO.read(new File("images/cracks.png"));
			app = ImageIO.read(new File("images/app.png"));
		} catch (IOException e) {
			System.out.println("no");
		}

		 this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
	/*	this.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();

				if (e.getButton() == MouseEvent.NOBUTTON) {
					// System.out.println(" no button clicked");
				} else if (e.getButton() == MouseEvent.BUTTON1) {

					// System.out.println(" left button clicked");
					for (Word word : words) {
						if (word.active) {
							if (word.xPos + word.margin + (word.width * 0.5) > mouseX&& word.xPos - word.margin- (word.width * 0.5) < mouseX&& word.yPos + word.margin+ (word.height * 0.5) > mouseY&& word.yPos - word.margin- (word.height * 0.5) < mouseY) {
								selectedWord = word;
								selectedWord.selected();
								
								selectedWord.state = Word.State.draging;
								offsetX = word.xPos - mouseX;
								offsetY = word.yPos - mouseY;
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/occupied").setValue(true);
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/active").setValue(true);
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
								myFirebaseRef.child("Regular Words").child(selectedWord.getWordId()+ "/state").setValue("draging");
								 
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/text").setValue(selectedWord.text);
								try {
									myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/owner").setValue(selectedWord.owner.getId());
								} catch (Exception err) {
									myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/owner").setValue("");

								}

								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/occupied").setValue(true);
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/active").setValue(true);
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
								myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/state").setValue("draging");

							}
						} 
					}
					if(selectedWord!= null)selectedWord.toTop(selectedWord); 
					overParticles.add(new RippleParticle((int) mouseX,(int) mouseY, 30));

					// overParticles.add( new RippleParticle((int)mouseX,
					// (int)mouseY, 40));
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					// System.out.println(" middle button clicked");

					for (Word word : words) {
						word.respond();
					}

					overParticles.add(new RippleParticle((int) mouseX,
							(int) mouseY, 200));
				} else if (e.getButton() == MouseEvent.BUTTON3) {

					// System.out.println(" right button clicked");
					// overParticles.add(new SplashParticle((int)mouseX,
					// (int)mouseY));
					// overParticles.add(new RustParticle ((int) mouseX, (int)
					// mouseY, selectedWord.getText().length()));

					for (Word word : words) {
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
							myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/occupied").setValue(false); // false in regular temp
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/active").setValue(true);
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
						myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/state").setValue("placed");
						 
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/occupied").setValue(false);
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/active").setValue(true);
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/state").setValue("placed");
						System.out.println("id placed: "+ selectedWord.getWordId());
						selectedWord = null;

					}

					overParticles.add(new RippleParticle((int) mouseX,(int) mouseY));
				} else if (e.getButton() == MouseEvent.BUTTON2) {
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent ev) {
				mouseX = ev.getX();
				mouseY = ev.getY();
			}
			public void mouseDragged(MouseEvent ev) {
				mouseX = ev.getX();
				mouseY = ev.getY();

				if (SwingUtilities.isLeftMouseButton(ev)) {

					if (selectedWord != null) {
						selectedWord.state=Word.State.draging;
						selectedWord.xPos = (int) (mouseX + offsetX);
						selectedWord.yPos = (int) (mouseY + offsetY);
						selectedWord.txPos = (int) (mouseX + offsetX);
						selectedWord.tyPos = (int) (mouseY + offsetY);
						//	myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/xRel").setValue(selectedWord.getXPos()/ Constants.screenWidth);
						//	myFirebaseRef.child("Regular Words").child(selectedWord.getWordId() + "/yRel").setValue(selectedWord.getYPos()/ Constants.screenHeight);
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/xRel").setValue(((float) selectedWord.xPos / Constants.screenWidth));
						myFirebaseRef.child("Used Words").child(selectedWord.getWordId()+ "/attributes/yRel").setValue(((float) selectedWord.yPos / Constants.screenHeight));
					}

					overParticles.add(new RippleParticle((int) mouseX,
							(int) mouseY, 10));
				}

				if (SwingUtilities.isMiddleMouseButton(ev)) {

				}

				if (SwingUtilities.isRightMouseButton(ev)) {

				}
			}
		});
   */
		
		
		// createRegularWords();
		// createThemeWords();
		// createUsedWords() ;
		// createNewWords() ;

		// Run method that listens for change in word list (active words for
		// example).

		// use method getText from the word class to set text to "word1" in the
		// firebase db.
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr);
		myFirebaseRef.child("ScreenWidth").setValue(Constants.screenWidth);
		myFirebaseRef.child("ScreenHeight").setValue(Constants.screenHeight); 
		
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
				// System.out.println(arg0.getKey()+"  vem d�r?");
				if (arg0.getKey().equals("Users") && arg0.hasChildren()) {

					/*
					 * for (DataSnapshot dataSnapshot : dsList) { User u =new
					 * User
					 * (dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child
					 * ("xRel").getValue().toString()), Float.parseFloat(
					 * dataSnapshot.child("yRel").getValue().toString()));
					 * boolean match = false; //
					 * System.out.println("!!!!!!!!!!!!!!!!!!!!USER"); for(User
					 * ul:userList){
					 * 
					 * if( ul.getId().equals(u.getId())){ // check if it has the
					 * same ID String state=""; if(
					 * dataSnapshot.child("state").getValue()!=null)
					 * state=dataSnapshot.child("state").getValue().toString();
					 * //ul.xTar = u.xPos; //ul.yTar = u.yPos;
					 * ul.setId(u.getId()); ul.xTar = u.xTar; ul.yTar = u.yTar;
					 * switch (state){ case "offline":
					 * ul.state=User.State.offline;
					 * System.out.println("offline"); break; case "online":
					 * ul.state=User.State.online; System.out.println("online");
					 * 
					 * break; case "taping": ul.state=User.State.taping;
					 * //System.out.println("taping: "+ul.getId()+"state: "+
					 * state); break; default: }
					 * 
					 * match=true; }
					 * 
					 * } if (!match){ userList.add(u); u.setColor(new
					 * Color(r.nextInt(255), r.nextInt(255),r.nextInt(255)));
					 * System.out.println("Add user");
					 * System.out.println(dataSnapshot.getKey()); } }
					 */

					for (DataSnapshot dataSnapshot : dsList) {
						// User u =new
						// User(dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child("xRel").getValue().toString()),
						// Float.parseFloat(
						// dataSnapshot.child("yRel").getValue().toString()));
						boolean match = false;
						for (User ul : userList) {

							if (ul.getId().equals(dataSnapshot.getKey())) {				
								String state = "";
								if (dataSnapshot.child("state").getValue() != null)state = dataSnapshot.child("state").getValue().toString();
								// ul.xTar = u.xPos;
								// ul.yTar = u.yPos;
								ul.setId(dataSnapshot.getKey());
								try {
									ul.xTar = Float.parseFloat(dataSnapshot.child("xRel").getValue().toString())* Constants.screenWidth;
									ul.yTar = Float.parseFloat(dataSnapshot.child("yRel").getValue().toString())* Constants.screenHeight;
								} catch (Exception e) {
								}
								try {
									ul.moves = Integer.parseInt(dataSnapshot.child("moves").getValue().toString());
								} catch (Exception e) {
								}

								switch (state) {
								case "offline":
									ul.state = User.State.offline;
									// System.out.println("offline");
									break;
								case "online":
									ul.state = User.State.online;
									// System.out.println("online");

									break;
								case "taping":
									ul.state = User.State.taping;
									// System.out.println("taping: "+ul.getId()+"state: "+
									// state);
									break;
								default:
								}

								match = true;
							}

						}
						if (!match) {
							try {
								userList.add(new User(dataSnapshot.getKey(),Float.parseFloat(dataSnapshot.child("xRel").getValue().toString())*Constants.screenWidth, Float.parseFloat(dataSnapshot.child("yRel").getValue().toString())*Constants.screenHeight));
								userList.get(userList.size() - 1).setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
								System.out.println("Added user"+dataSnapshot.getKey());
							} catch (Exception e) {

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
		g2 = (Graphics2D) g; 
		if (onesRun)setup();
		// get the advance of my text in this font
		// and render context

		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		g2.drawImage(bimage, 0, 0, Constants.screenWidth,Constants.screenHeight, this);
		// g2.drawImage(app, Constants.screenWidth - 450, Constants.screenHeight- 200, this);
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

		/*for (int i = 0; i < 7; i++) { // spawn particles
			particles.add(new WaterParticle((int) r
					.nextInt(Constants.screenWidth), 0));
		}*/
		while (particles.size() > Constants.PARTICLE_LIMIT) { // run all particlesCap
			particles.remove(0);
		}
		while (overParticles.size() > Constants.HEAVY_PARTICLE_LIMIT) { // run all OverparticlesCap
			overParticles.remove(0);
		}
		while (projectiles.size() > Constants.PROJECTILE_LIMIT) { // run all OverparticlesCap
			projectiles.remove(0);
		}

		for (int i = particles.size() - 1; 0 < i; i--) { // run all particles
			particles.get(i).update();
			particles.get(i).display(g2);

			for (Word word : words) { // collision
				if (word.active) {
					// particles.get(i).collisionCircle(word.xPos, word.yPos,
					// word.margin);
					// particles.get(i).collisionCircle(word.xPos, word.yPos,
					// word.margin);
					if (!Constants.noCollision)particles.get(i).collisionRect(word.xPos, word.yPos,word.width, word.height);
				}
			}
			if (particles.get(i).dead)
				particles.remove(i);
		}

		//if (!Constants.noUser) {
			for (User user : userList) { // run all users
				user.update();
				user.display();
			}
		//}
		for (Projectile p : projectiles) { // run all projectiles

			for (Word w : words) {
				if (w.active) {
					p.collision(w);
				}
			}
			p.BoundCollision();
			p.update();
			p.display(g2);
		}

		for (Word word : words) { // run all words
			if (word.active) {
				word.update();
				word.display();
				if (word.state != Word.State.draging) {
					word.colliding = false;
					word.BoundCollision();
					for (Word word2 : words) { // word collision
						if (word != word2 && word2.active && word2.state != Word.State.draging) {
							word.collisionVSWord(word2);
						}
					}
				}
			}
		}

		for (int i = overParticles.size() - 1; 0 < i; i--) { // run all
			overParticles.get(i).update();
			if (!Constants.simple)
				overParticles.get(i).display(g2);
			for (Word w : words) {
				if (w.active)
					overParticles.get(i).collisionCircle(w.xPos, w.yPos,w.width, w);
			}
			for (Particle p : particles) {
				overParticles.get(i).collisionVSParticle(p);
			}

			if (overParticles.get(i).dead)overParticles.remove(i);

		}

		// g2.drawImage(app,(int)(Constants.screenWidth - app.getWidth()*0.5),
		// (int)(Constants.screenHeight - app.getHeight()*0.5), this); //
		// GooglePlay icon
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
				// System.out.println("Exception in thread: " +
				// iex.getMessage());
			}

			frames++;
			if (System.nanoTime() - lastTime >= 100000L) {
				FPS = (int) (frames * 0.3);
				frames = 0;
				lastTime = System.nanoTime();
				sendAfterCollision();
				checkCrowdedScreen();
				if (!Constants.noTimer) {
					Constants.cal = Calendar.getInstance();
					Constants.timeLeft = (long) (Constants.clearInterval - ((Constants.cal
							.getTimeInMillis() - Constants.startTime) * 0.001));
					if (Constants.timeLeft < 0) { // reset timer
						clearScreen();
						Constants.startTime = Constants.cal.getTimeInMillis();
					}
				}
			}
		}
	}
	
	public static void respawn (){
		System.out.println("reSpawn");
		final Random ra = new Random();
		 clearFirebase();
		myFirebaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
		
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		       
		    	DataSnapshot DS = snapshot.child("Regular Words");
		        occupiedFalse(snapshot);
		        for (int j=0; j< Constants.StartWordAmount;j++){
			        int index=ra.nextInt((int) DS.getChildrenCount());
			        int i=0;
			        for (DataSnapshot child:DS.getChildren()) {
			        	
			            System.out.println(child.getKey()+" child");
			            if(i==index){ 
			            	DrawPanel.choosen=child; // DONT TAKE A OCCUPIED WORD
			            	myFirebaseRef.child("/Regular Words/"+choosen.getKey()+"/occupied").setValue(true);
			            	System.out.println(child.child("/occupied").getValue());
			            	if(Boolean.parseBoolean(child.child("/occupied").getValue().toString())) System.out.println("OBS !!!!!! its already occupied");
	
			            	System.out.println("The choosen one is :"+choosen.getKey());
			            }
			            i++;
			        }
					System.out.println("chossen Text: "+choosen.child("text").getValue());
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("active").setValue(true);
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("state").setValue("placed");
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("owner").setValue("[startWords]");
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("occupied").setValue(false);
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("dropped").setValue(true);
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("text").setValue(choosen.child("text").getValue());
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("xRel").setValue( new Random().nextFloat()*1);
					firebaseUsedWord.child(choosen.getKey()).child("/attributes").child("yRel").setValue( new Random().nextFloat()*1);
		        }

		    }
			public void occupiedFalse(DataSnapshot snapshot){
				System.out.println("UPLOAD FIREBASE");
				DataSnapshot DsR = snapshot.child("Regular Words");
				System.out.println(DsR.getKey());
				for(DataSnapshot DSC:DsR.getChildren()){
					myFirebaseRef.child("Regular Words/"+DSC.getKey()+"/occupied").setValue(false);  // set all regular words to occupied false
					
				}
				System.out.println("all occupied is false");

			}
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}});
		}

	public static void clearFirebase(){
	myFirebaseRef.child("Used Words").removeValue();
	words.clear();				
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void createStarterWords(){
		Firebase wordList = myFirebaseRef.child("Starter Words");
		String[] starterWords = {"Our", "first", "sentence"};

		for (int i = 0; i < starterWords.length; i++) {
			String wordId = "word" + i;
			wordList.child(wordId + "/occupied").setValue(false);
			wordList.child(wordId + "/text").setValue(starterWords[i]);
			wordList.child(wordId + "/active").setValue(true);
			wordList.child(wordId + "/owner").setValue("");

			int x = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y = r.nextInt(Constants.screenHeight + 1); // skalad y pos
			//words.add(new Word(starterWords[i], null, x, y, x, y));
			words.add(new Word(new WordBuilder(starterWords[i], x, y)));

			//words.get(words.size() - 1).setWordId(wordId);
			
			System.out.println(words.get(words.size() - 1).getWordId());
		}
	}

	/*public void createRegularWords() {

		Firebase wordList = myFirebaseRef.child("Regular Words");

		String[] regularWords = { "When", "you", "use", "Taping", "calling",
				"draging", "mobile", "device", "should", "connect", "with",
				"internet", "Another", "important", "thing", "is", "you",
				"wrong", "unable", "to", "access", "Droping", "and", "some",
				"other", "emergency", "word", "via", "user", "rippeEffect",
				"If", "you", "want", "to", "make", "any", "not working",
				"login,", "logout", "animation", "make", "other",
				"communication", "arrangements", "How", "to", "Place", "your",
				"Whatsapp", "call?", "you", "can", "simply", "make", "a",
				"call", "through", "Whatsapp", "by", "the", "way", "of",
				"open", "the", "chat", "with", "who", "do", "you", "want",
				"call", "and", "on", "the", "top", "of", "the", "phone",
				"just", "tap", "the", "phone", "button", "Next", "we", "will",
				"see", "how", "to", "reveive", "whatsapp", "call?", "it's",
				"also", "quite", "simple,", "if", "someone", "calling", "you,",
				"while", "you", "will", "see", "the", "Whatsapp", "incoming",
				"call", "on", "your", "phone", "screen", "Afterward", "the",
				"ordinary", "phone", "call", "receving", "like", "process",
				"you", "want", "to", "do", "there", "For", "example", "green",
				"and", "red", "button", "will", "show,", "you", "want", "to",
				"attend", "the", "call", "just", "slide", "the", "green",
				"button", "or", "you", "dont", "want", "to", "like", "to",
				"answer", "that", "call", "just", "slide", "the", "red",
				"button", "then", "automatically", "call", "will", "declined",
				"and", "then", "substituly", "you", "can", "touch", "on",
				"the", "message", "icon", "on", "the", "whatsapp", "call",
				"screen", "to", "stop", "the", "call", "with", "rapid",
				"message" };

		int count = 0;
		for (int i = 0; i < regularWords.length; i++) {
			String wordId = "word" + i;
			wordList.child(wordId + "/occupied").setValue(false);
			wordList.child(wordId + "/text").setValue(regularWords[i]);
			wordList.child(wordId + "/active").setValue(false);
			wordList.child(wordId + "/owner").setValue("");

			if ("you".equals(regularWords[i])) {
				wordList.child("word" + i + "/plural").setValue("yes");
				System.out.println(wordList.child("word" + i + "/text")
						.child("word" + i).getKey());
			} else {
				wordList.child("word" + i + "/plural").setValue("");
			}

			int x = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y = r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(regularWords[i], null, x, y, x, y));
			words.get(words.size() - 1).setWordId(wordId);
			System.out.println(words.get(words.size() - 1).getWordId());
			count++;
		}

		myFirebaseRef.child("Regular Words Size").setValue(count);
	}
	 */
	/*
	public void createThemeWords() {
		Firebase themedWords = myFirebaseRef.child("Themed Words");
		String[] themeWords = { "too." };

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			String wordId = "word" + i;
			themedWords.child(wordId + "/text").setValue(themeWords[i]);
			themedWords.child(wordId + "/active").setValue(false);
			themedWords.child(wordId + "/owner").setValue("");
			int x = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y = r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(themeWords[i], null, x, y, x, y));
			words.get(words.size() - 1).setWordId(wordId);
			count++;
		}

		myFirebaseRef.child("Themed Words Size").setValue(count);
	}

	public void createNewWords() {
		Firebase themedWords = myFirebaseRef.child("New");
		String[] themeWords = { "10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "25", "30", "35", "40", "45", "50", "2000",
				"2001", "2002", "&", "?", "a", "a", "about", "above",
				"airplane", "all", "alphabet", "am", "and", "and", "and",
				"animal", "anime", "answer", "anyone", "anything", "apple",
				"applesauce", "are", "are", "asparagus", "astronomy", "at",
				"aunt", "autumn", "baby", "bad", "ball", "ballerina",
				"balloon", "barn", "base", "basket", "bathroom", "because",
				"begin", "believe", "best", "big", "bike", "bird", "birthday",
				"bite", "bite", "black", "blue", "body", "book", "bored",
				"born", "bottom", "box", "box", "boy", "breakfast", "bring",
				"Britney", "Spears", "Bronx", "brother", "brown", "bubblegum",
				"bug", "but", "butterfly", "by", "candy", "can't", "car",
				"castle", "cat", "cat", "chair", "child", "chocolate", "close",
				"cloud", "color", "Connecticut", "cool", "corn", "could",
				"couldn't", "cow", "cried", "d", "Dad", "dance", "dark", "day",
				"day", "did", "different", "dinner", "dinosaur", "do", "do",
				"does", "dog", "don't", "door", "down", "draw", "dream", "dug",
				"dull", "each", "ear", "easy", "eat", "ed", "eight",
				"elementary", "elephant", "er", "er", "exercise", "fall",
				"family", "far", "fast", "fat", "favorite", "favorite", "fed",
				"feet", "fight", "fish", "fish", "five", "flower", "fly",
				"fly", "for", "four", "France", "french", "fries", "friend",
				"from", "ful", "funny", "gentle", "geometry", "gerbil", "get",
				"girl", "give", "glow", "good", "graffiti", "Grandma",
				"Grandpa", "green", "grow", "hamburger", "hamster", "hand",
				"Harry", "Potter", "has", "hate", "have", "he", "her", "here",
				"hid", "hide", "high", "him", "hit", "hold", "home",
				"homework", "hot", "hug", "hungry", "I", "I", "I", "ice",
				"cream", "if", "imagine", "important", "in", "ing", "ing",
				"inside", "is", "is", "Japan", "JK", "Rowling", "jump",
				"junior", "kind", "king", "laptop", "laugh", "leave", "Lemony",
				"Snicket", "light", "like", "like", "little", "live", "live",
				"look", "looking", "loud", "lovelunch", "ly", "astronaut",
				"mad", "magic", "man", "many", "me", "midnight", "miss", "Mom",
				"monkey", "monkey", "monster", "more", "morning", "mountain",
				"movie", "mud", "music", "my", "name", "napkin", "near",
				"nest", "never", "New", "York", "newspaper", "next", "next",
				"night", "nine", "no", "noon", "nose", "not", "notebook", "of",
				"once", "once", "one", "one", "or", "or", "over", "over",
				"paint", "peace", "peanut", "butter", "photograph", "pink",
				"pizza", "poke", "Pokemon", "pool", "pool", "pretend",
				"pretty", "prince", "princes", "pumpkin", "purple", "queen",
				"Queens", "quiet", "r", "rain", "rainbow", "ran", "read",
				"real", "remember", "ride", "ring", "room", "round", "round",
				"rude", "s", "s", "sad", "said", "sail", "saw", "say",
				"school", "see", "seven", "share", "she", "should", "show",
				"silly", "sister", "six", "skateboard", "skin", "skinny",
				"smart", "snake", "sneakers", "so", "soccer", "something",
				"song", "speak", "special", "spring", "square", "start",
				"stop", "storm", "story", "strawberry", "stupid", "summer",
				"swim", "take", "teacher", "telephone", "television", "tell",
				"that", "that", "their", "them", "there", "think", "three",
				"iger", "time", "to", "to", "told", "too", "tool", "top",
				"town", "truth", "two", "TV", "U.S.", "uncle", "under", "used",
				"vacation", "vanilla", "very", "video", "game", "want", "warm",
				"was", "what", "whatever", "when", "where", "who", "why",
				"whisper", "white", "wild", "will", "will", "wind", "window",
				"wing", "winter", "wish", "with", "with", "woman", "won't",
				"world", "y", "yell", "yellow", "yes", "yesterday", "you",
				"you", "young", "your", "zebra" };

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			themedWords.child("word" + i + "/attributes/text").setValue(
					themeWords[i]);
			themedWords.child("word" + i + "/attributes/active")
					.setValue(false);
			themedWords.child("word" + i + "/attributes/owner").setValue("");
			themedWords.child("word" + i + "/attributes/xRel").setValue(0.5);
			themedWords.child("word" + i + "/attributes/yRel").setValue(0.5);
			int x = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y = r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(themeWords[i], null, x, y, x, y));
			count++;
		}

		myFirebaseRef.child("Themed Words Size").setValue(count);
	}*/
	/*
	public void createUsedWords() {
		Firebase themedWords = myFirebaseRef.child("Used Words");
		String[] themeWords = { "helloWorld", "hejsan", "yo", "niHao" };

		int count = 0;

		for (int i = 0; i < themeWords.length; i++) {
			themedWords.child("word" + i + "/attributes/text").setValue(
					themeWords[i]);
			themedWords.child("word" + i + "/attributes/active")
					.setValue(false);
			themedWords.child("word" + i + "/attributes/owner").setValue("");
			themedWords.child("word" + i + "/attributes/xRel").setValue(0.5);
			themedWords.child("word" + i + "/attributes/yRel").setValue(0.5);
			int x = r.nextInt(Constants.screenWidth + 1); // skalad x pos
			int y = r.nextInt(Constants.screenHeight + 1); // skalad y pos
			words.add(new Word(themeWords[i], null, x, y, x, y));
			count++;
		}

		myFirebaseRef.child("Themed Words Size").setValue(count);
	}*/

	// Method to listen for updates in the words list
	public void wordListener() {
		// Creating a ref to a random child in the Regular Words tree on
		// firebase
		Firebase fireBaseWords = myFirebaseRef.child("Starter Words");
		//Firebase fireBaseWords = myFirebaseRef.child("Used Words");

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


					int index=Integer.parseInt(s.substring(63));// regular
					//int index=Integer.parseInt(s.substring(60)); // used words
					//word = (String) snapshot.child("text").getValue().toString();

					if (snapshot.child("x").getValue() != null) {
						words.get(index).txPos=(int) (Float.parseFloat(snapshot.child("x").getValue().toString()) * Constants.screenWidth);
					}

					if (snapshot.child("y").getValue() != null) {
						words.get(index).tyPos=(int)  (Float.parseFloat(snapshot.child("y").getValue().toString()) * Constants.screenHeight);
					}

					if (snapshot.child("active").getValue() == "true") {
					words.get(index).display();
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
									u.yPos=(int)words.get(index).tyPos;
									u.xPos=(int)words.get(index).txPos;
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
						words.get(index).setOwner(snapshot.child("owner").getValue().toString());
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
		 

		Firebase fireBaseUsedWords = myFirebaseRef.child("Used Words");
		//Firebase fireBaseWords = myFirebaseRef.child("Used Words");
		fireBaseUsedWords.addListenerForSingleValueEvent(new ValueEventListener( ) {

			@Override
			public void onCancelled(FirebaseError arg0) {

			}

			@Override
			public void onDataChange(DataSnapshot snapshot) {
				System.out.println(snapshot.getChildrenCount()+"   children in usedWords"); 
				Iterable<DataSnapshot> childrens =snapshot.getChildren();
				//Word matchedWord;
				//boolean matched = false;
				for(DataSnapshot DSS: childrens){
					System.out.println(" DSS:" +DSS.getKey()); 

					words.add(new Word(DSS,DSS.child("/attributes/text").getValue().toString(), DSS.child("/attributes/owner").getValue().toString(),
							(int)(Math.round((double)(DSS.child("/attributes/xRel").getValue())*Constants.screenWidth)),
							(int)(Math.round((double)(DSS.child("/attributes/yRel").getValue())*Constants.screenHeight)),
							(int)(Math.round((double)(DSS.child("/attributes/xRel").getValue())*Constants.screenWidth)),
							(int)(Math.round((double)(DSS.child("/attributes/yRel").getValue())*Constants.screenHeight))));
					try{
					words.get(words.size()-1).width = metrics.stringWidth(words.get(words.size()-1).text);
					} catch (NullPointerException npe){
						System.err.println("NullPointerException in words.get(words.size()-1).width");
					}
					words.get(words.size()-1).height = metrics.getHeight();
					words.get(words.size()-1).active=Boolean.parseBoolean(DSS.child("/attributes/active").getValue().toString());
					System.out.println("assigning states");
					try{
						switch(DSS.child("/attributes/state").getValue().toString()){
						case "onTray": 
							words.get(words.size()-1).setState(Word.State.onTray);
							System.out.println("its done ,onTray assigned");
							break;
						case "draging": 
							words.get(words.size()-1).setState(Word.State.draging);
							System.out.println("its done ,onTray draging");
							break;
						case "placed": 
							words.get(words.size()-1).setState(Word.State.placed);
							break;
						case "locked": 
							words.get(words.size()-1).setState(Word.State.locked);
							break;
						}
					}catch(NullPointerException e){}

					System.out.println(" created some words:" +words.get(words.size()-1).getWordId()); 
				}
				System.out.println("arraylist of words is now:"+words.size());
			}});
		// Adding a child event listener to the firebasewords ref, to check for
		// active words

		fireBaseUsedWords.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildChanged(DataSnapshot snapshot, String arg1) {
				//	try{


				boolean match = false;
				Word matchingWord=null;
				for(Word w:words){
					//	System.out.println("datasnap:"+w.dataSnapshot.getKey() +"compareto:"+snapshot);
					try{
						if(w.dataSnapshot.getKey().equals(snapshot.getKey())){
							match=true;
							matchingWord=w;
						}
					}catch (NullPointerException npe){
						System.err.println("Word not found on Firebase");
					}
				}
				if(!match){ // create word
					try{
					words.add(new Word(snapshot,snapshot.child("/attributes/text").getValue().toString(), snapshot.child("/attributes/owner").getValue().toString(),
							(int)(Math.round((double)(snapshot.child("/attributes/xRel").getValue())*Constants.screenWidth)),
							(int)(Math.round((double)(snapshot.child("/attributes/yRel").getValue())*Constants.screenHeight)),
							(int)(Math.round((double)(snapshot.child("/attributes/xRel").getValue())*Constants.screenWidth)),
							(int)(Math.round((double)(snapshot.child("/attributes/yRel").getValue())*Constants.screenHeight))));
					words.get(words.size()-1).width = metrics.stringWidth(words.get(words.size()-1).text);
					words.get(words.size()-1).height = metrics.getHeight();
					}catch(NullPointerException e){}
				}
				//	System.out.println(" x:"+(int)Math.round((double)(snapshot.child("/attributes/xRel").getValue())*Constants.screenWidth));


				try{
					if (matchingWord != null) {
						matchingWord.txPos=(int)Math.round((double)(snapshot.child("/attributes/xRel").getValue())*Constants.screenWidth);
						System.out.println("xRel assigned");
					}
				}catch( NullPointerException ne){}
				try{
					if (matchingWord != null) {
						matchingWord.tyPos=(int)Math.round((double)(snapshot.child("/attributes/yRel").getValue())*Constants.screenHeight);
						System.out.println("yRel assigned");
					}	
				}catch( NullPointerException ne){}

				try{
					if (snapshot.child("attributes/state").getValue() != null) {
						//System.out.println("State stuff");
						User u = null;
						if(matchingWord.getUser()!=null){ 
							u=matchingWord.getUser();
							switch(snapshot.child("attributes/state").getValue().toString()){
							case "placed":
								//	words.get(index).released();
								if(u.state==User.State.online){
									matchingWord.setState(Word.State.placed);
									u.release();
									System.out.println("placed");
									matchingWord.appear();
								}
								break;

							case "draging":
								//words.get(index).respond();
								if(matchingWord.state!=Word.State.onTray){
									matchingWord.setState(Word.State.draging);
									matchingWord.toTop(matchingWord);
									u.xTar=matchingWord.txPos;
									u.yTar=matchingWord.tyPos;
									u.xPos=(int)matchingWord.txPos;
									u.yPos=(int)matchingWord.tyPos;
									//System.out.println("dragging");
								}else{
									matchingWord.xPos=(int)matchingWord.txPos;
									matchingWord.yPos=(int)matchingWord.tyPos;
									u.xTar=matchingWord.txPos;
									u.yTar=matchingWord.tyPos;
									u.yPos=(int)matchingWord.tyPos;
									u.xPos=(int)matchingWord.txPos;
									matchingWord.respond();
								}

								break;
							case "onTray":

								matchingWord.setState(Word.State.onTray);
								System.out.println("on tray");
								break;

							}
						}
					}	
				}catch( NullPointerException ne){}

				try{
					if (snapshot.child("attributes/plural").getValue() != null) {
						matchingWord.setType(snapshot.child("attributes/plural").getValue().toString());
					}
				} catch(Exception e){}

				try{
					if(snapshot.child("attributes/owner").getValue().toString()!="") {
						matchingWord.setOwner(snapshot.child("attributes/owner").getValue().toString());

						System.out.println(matchingWord.getOwner() + " owns the word " + matchingWord.getText());
					}
				} catch(Exception e){}
				//words.remove(matchingWord);

				//} catch (NullPointerException npe){}

			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});
	}

	public void displayDebugText() {
		g2.setColor(Color.white); // vit system color
		g2.setFont(Constants.boldFont); // init typsnitt
		if (Constants.debug) {g2.drawString("ID: " + Constants.screenNbr + " part:" + particles.size()+ " Overpart:" + overParticles.size() + "  words: "
				+ words.size() + "  Users:" + userList.size()
				+ "  FPS: " + FPS + "  Time:" + Constants.timeLeft,30, 50);
		if (Constants.noCollision)g2.drawString("No water collision", 30, 100);
		if (Constants.noTimer)g2.drawString("No time", 30, 150);
		if (Constants.noUser)g2.drawString("No user", 30, 200);
		if (Constants.simple)g2.drawString("simple Mode", 30, 250);
		} else {
			g2.drawString("Screen ID: " + Constants.screenNbr, 30, 50);
		}
	}

	public static void checkCrowdedScreen() {
		int amount = 0;
		for (Word w : words) {
			if (w.active)
				amount += w.text.length() + 2;
		}
		if (Constants.spaceOnScreen < amount)
			clearScreen();
	}

	public static void clearScreen() {
		// words.clear();
		overParticles.add(new EqualizerParticle(
				(int) (Constants.screenWidth * 0.5),
				(int) (Constants.screenHeight * 0.5), 50));
		respawn();

	}

	public static void sendAfterCollision() {

		if (!collisionSent) {
			Boolean allWordsNotColliding = true;
			for (Word w : DrawPanel.words) {
				if (w.active && w.colliding) {
					allWordsNotColliding = false;
				}
			}

			if (allWordsNotColliding) {
				for (Word w : DrawPanel.words) {
					if (w.active) {
						w.xVel=0;
						w.yVel=0;
						w.txPos=w.xPos;
						w.tyPos=w.yPos;
						DrawPanel.myFirebaseRef.child("Used Words").child(w.getWordId() + "/attributes/text").setValue(w.text);
						DrawPanel.myFirebaseRef.child("Used Words").child(w.getWordId() + "/attributes/xRel").setValue(((float) w.xPos / Constants.screenWidth));
						DrawPanel.myFirebaseRef.child("Used Words").child(w.getWordId() + "/attributes/yRel").setValue(((float) w.yPos / Constants.screenHeight));
						//w.respond();
					}
				}
				overParticles.add(new ScanParticle((int) (Constants.screenWidth*0.5),0));
				//overParticles.add(new RippleParticle((int) (Constants.screenWidth * 0.5),(int) (Constants.screenHeight * 0.5), 200));
				System.out.println("all words cood ,after collision update");
				DrawPanel.collisionSent = true;
			}

		}
	}

}
		
	

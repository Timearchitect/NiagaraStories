package se.mah.k3;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import se.mah.k3.Projectiles.PingBall;

public class FullScreen extends JFrame implements KeyEventDispatcher {

	public static DrawPanel panel;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private boolean inFullScreenMode = false;
	private int PrevX = 100 ,PrevY = 100 ,PrevWidth = 480,PrevHeight = 640; //Dummysize

	/**
	 * Launch the application.
	 */
	static {
		System.setProperty("sun.java2d.transaccel", "True");
	//	 System.setProperty("sun.java2d.trace", "timestamp,log,count");
		System.setProperty("sun.java2d.opengl", "True"); // GOPU ACCEL
		System.setProperty("sun.java2d.d3d", "True");
		System.setProperty("sun.java2d.ddforcevram", "True");
	//	System.setProperty("sun.java2d.ddoffscreen","true");
	//	System.setProperty("sun.java2d.accthreshold","0");
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FullScreen frame = new FullScreen();
					frame.setVisible(true);
					gameLoop(); // start the game loop
					frame.setIconImage(ImageIO.read(this.getClass().getResource("/icon/icontiny.png")));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



	/**
	 * Create the frame.
	 */
	public FullScreen() {
		Constants.startTime= Constants.cal.getTimeInMillis();
		try{
			//Not logical but normal means....
			Constants.font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf")).deriveFont(Constants.fontSize);
			Constants.lightFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/fonts/Roboto-Light.ttf")).deriveFont(Constants.fontSize);
			Constants.boldFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf")).deriveFont(Constants.fontSize);
			Constants.boldFontScreen =Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf")).deriveFont(32f);
		} catch (Exception e) {
			System.out.println("Oups didn't find the fonts....add default.....");
			Constants.font = new Font(Font.SERIF,Font.ITALIC,18);
			Constants.lightFont = new Font(Font.SERIF,Font.ITALIC,18);
			Constants.boldFont = new Font(Font.SERIF,Font.ITALIC,18);
			Constants.boldFontScreen = new Font(Font.SERIF,Font.ITALIC,18);
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		//DrawPanel panel = new DrawPanel();
		panel = new DrawPanel();
		panel.setFocusable(true);
		panel.requestFocus();
		contentPane.add(panel, BorderLayout.CENTER);
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager(); //Listen to keyboard
		manager.addKeyEventDispatcher(this);
	
		//   DrawPanel canvas = new DrawPanel();
		Thread t = new Thread(panel);
		t.start();
		setFullscreen(true);
	}
	static void gameLoop() {
		
	}

	public void setFullscreen(boolean fullscreen) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();    
		if(fullscreen){
			PrevX = getX();
			PrevY = getY();
			PrevWidth = getWidth();
			PrevHeight = getHeight();
			dispose(); 
			//Always on last screen!
			setUndecorated(true);
			gd[gd.length-1].setFullScreenWindow(this);
			setVisible(true);
			this.inFullScreenMode = true;
		}
		else{
		//	setVisible(true);
			setBounds(PrevX, PrevY, PrevWidth, PrevHeight);
			dispose();
			setUndecorated(false);
			setVisible(true);
			this.inFullScreenMode = false;
		}
	}


	//Toggle fullscreen with f
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_RELEASED) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				DrawPanel.sendAfterCollision();
			}
		}
		if (e.getID() == KeyEvent.KEY_TYPED) {
			char key=(String.valueOf(e.getKeyChar()).toLowerCase()).charAt(0);
			
			if(key=='f' ){     		 
				setFullscreen(!inFullScreenMode);	
			}
			if(key=='n' ){     		 
				Constants.spawnParticle=(Constants.spawnParticle==true)?(Constants.spawnParticle=false) :(Constants.spawnParticle=true);
			}
			if(key=='c' ){     		 
				Constants.noCollision=(Constants.noCollision==true)?(Constants.noCollision=false) :(Constants.noCollision=true);
			}
			if(key=='t' ){     		 
				Constants.noTimer=(Constants.noTimer==true)?(Constants.noTimer=false) :(Constants.noTimer=true);
			}
			if(key=='u'){     		 
				Constants.noUser=(Constants.noUser==true)?(Constants.noUser=false) :(Constants.noUser=true);
			}
			if(key=='s' ){     		 
				Constants.simple=(Constants.simple==true)?(Constants.simple=false) :(Constants.simple=true);
			}
			if(key=='#' ){     		 
				Constants.debug=(Constants.debug==true)?(Constants.debug=false) :(Constants.debug=true);
			}
			if(key=='d' ){     		 
				DrawPanel.projectiles.clear();		
			}
			if(key=='w' ){    
				//CustomDialog input = new CustomDialog(this, inFullScreenMode, "Write the text of the word.",(int)DrawPanel.mouseX,(int)DrawPanel.mouseY);
			 new CustomDialog(this, inFullScreenMode, "Write the text of the word.",(int)Mouse.mouseX,(int)Mouse.mouseY);

				//panel.setFocusable(true);
				//panel.setRequestFocusEnabled(true);

			}
			if(key == ' '){
				DrawPanel.clearScreen();
			}
			if(key == 'p'){
				DrawPanel.projectiles.add(new PingBall((int)(Constants.screenWidth*0.5),(int)(Constants.screenHeight*0.5),10,10));
			}

		}
		return false;	
	}



	

	public void generateAvalibleWordId(){
		
		
		
	}
	
	
}

package se.mah.k3;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import se.mah.k3.Word.WordBuilder;
import se.mah.k3.Projectiles.PingBall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class FullScreen extends JFrame implements KeyEventDispatcher,ActionListener {


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
		// System.setProperty("sun.java2d.trace", "timestamp,log,count");
		//System.setProperty("sun.java2d.opengl", "True"); // GPU ACCEL
		System.setProperty("sun.java2d.d3d", "True");
		System.setProperty("sun.java2d.ddforcevram", "True");
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FullScreen frame = new FullScreen();
					frame.setVisible(true);
					gameLoop(); // start the game loop
					frame.setIconImage(ImageIO.read(new File("assets/icon/icontiny.png")));

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
			Constants.font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Roboto-Regular.ttf")).deriveFont(Constants.fontSize);
			Constants.lightFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Roboto-Light.ttf")).deriveFont(Constants.fontSize);
			Constants.boldFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Roboto-Bold.ttf")).deriveFont(Constants.fontSize);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
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
	
		contentPane.add(panel, BorderLayout.CENTER);
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager(); //Listen to keyboard
		manager.addKeyEventDispatcher(this);
		setFullscreen(true);
		//   DrawPanel canvas = new DrawPanel();
		Thread t = new Thread(panel);
		t.start();

	}
	static void gameLoop() {
		// panel.run();
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
			setVisible(true);
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

			if(e.getKeyChar()=='f' || e.getKeyChar()=='F'){     		 
				setFullscreen(!inFullScreenMode);	
			}
			if(e.getKeyChar()=='c' || e.getKeyChar()=='C'){     		 
				Constants.noCollision=(Constants.noCollision==true)?(Constants.noCollision=false) :(Constants.noCollision=true);
			}
			if(e.getKeyChar()=='t' || e.getKeyChar()=='T'){     		 
				Constants.noTimer=(Constants.noTimer==true)?(Constants.noTimer=false) :(Constants.noTimer=true);
			}
			if(e.getKeyChar()=='u' || e.getKeyChar()=='U'){     		 
				Constants.noUser=(Constants.noUser==true)?(Constants.noUser=false) :(Constants.noUser=true);
			}
			if(e.getKeyChar()=='s' || e.getKeyChar()=='S'){     		 
				Constants.simple=(Constants.simple==true)?(Constants.simple=false) :(Constants.simple=true);
			}
			if(e.getKeyChar()=='#' ){     		 
				Constants.debug=(Constants.debug==true)?(Constants.debug=false) :(Constants.debug=true);
			}
			if(e.getKeyChar()=='d' ||e.getKeyChar()=='D' ){     		 
				DrawPanel.projectiles.clear();		
			}
			if(e.getKeyChar()=='w' ||e.getKeyChar()=='W' ){    
				CustomDialog input = new CustomDialog(this, inFullScreenMode, "Write the text of the word.",(int)DrawPanel.mouseX,(int)DrawPanel.mouseY);
				//panel.setFocusable(true);
				//panel.setRequestFocusEnabled(true);

			}
			if(e.getKeyChar() == ' '){
				clearScreen();
			}
			if(e.getKeyChar() == 'p' || e.getKeyChar() == 'P'){
				DrawPanel.projectiles.add(new PingBall((int)(Constants.screenWidth*0.5),(int)(Constants.screenHeight*0.5),10,10));
			}

		}
		return false;	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println(" action");
		
	}

	public void clearScreen(){ 
	if(Constants.simple){
		for(Word w:DrawPanel.words){
			w.active=false;	
		}
	}
		DrawPanel.clearScreen();
	}

}

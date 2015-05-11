package se.mah.k3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.KeyEvent;

public class FullScreen extends JFrame implements KeyEventDispatcher{

	/**
	 * 
	 */
	private static DrawPanel panel;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private boolean inFullScreenMode = false;
	private int PrevX = 100 ,PrevY = 100 ,PrevWidth = 480,PrevHeight = 640; //Dummysize

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FullScreen frame = new FullScreen();
					frame.setVisible(true);
				    gameLoop(); // start the game loop
				    
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		//DrawPanel panel = new DrawPanel();
		 panel = new DrawPanel();
		 
		//panel.setOpaque(false);
		//panel.setBackground( new Color(255, 0, 0, 20) );
		contentPane.add(panel, BorderLayout.CENTER);
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager(); //Listen to keyboard
	    manager.addKeyEventDispatcher(this);
		setFullscreen(true);
		
	}
	  static void gameLoop() {
		
		  panel.run();
		
		 
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
       if (e.getID() == KeyEvent.KEY_TYPED) {
       	 if(e.getKeyChar()=='f'){     		 
             	setFullscreen(!inFullScreenMode);	
     		}
        }
        return false;
	}
}

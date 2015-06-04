package se.mah.k3;


import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.TransferHandler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JTextField;

import se.mah.k3.Word.WordBuilder;

public class CustomDialog extends JDialog implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel myPanel = null;
    private JButton yesButton = null;
    private JButton noButton = null;
    private JTextField txtWord;

    public CustomDialog(JFrame frame, boolean modal, String myMessage,int xPos,int yPos) {
    super(frame, modal);
    myPanel = new JPanel();
    getContentPane().add(myPanel);
    
    myPanel.add(new JLabel(myMessage));
    txtWord = new JTextField();
    txtWord.setText("Text Word");
    yesButton = new JButton("Create");
    yesButton.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
			//DrawPanel.words.add(new Word(new WordBuilder(txtWord.getText() ,(int)DrawPanel.mouseX,(int)DrawPanel.mouseY).angle("random")));	
			DrawPanel.words.add(new Word(new WordBuilder(txtWord.getText() ,(int)Mouse.mouseX,(int)Mouse.mouseY).angle("random")));	

    		dispose();
    	}
    });
    
    myPanel.add(txtWord);
    txtWord.setColumns(10);
    yesButton.requestFocus();
    myPanel.add(yesButton);
    noButton = new JButton("Cancel");
    noButton.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		dispose();
    	}
    });
    myPanel.add(noButton);
    pack();
    //setLocationRelativeTo(frame);
    setLocation(xPos, yPos); // <--
    setVisible(true);
    }

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);
	}

	@Override
	protected JRootPane createRootPane() {
		return super.createRootPane();
	}

	@Override
	protected void dialogInit() {
		super.dialogInit();
	}

	@Override
	public AccessibleContext getAccessibleContext() {
		return super.getAccessibleContext();
	}

	@Override
	public Container getContentPane() {
		return super.getContentPane();
	}

	@Override
	public int getDefaultCloseOperation() {
		return super.getDefaultCloseOperation();
	}

	@Override
	public Component getGlassPane() {
		return super.getGlassPane();
	}

	@Override
	public Graphics getGraphics() {
		return super.getGraphics();
	}

	@Override
	public JMenuBar getJMenuBar() {
		return super.getJMenuBar();
	}

	@Override
	public JLayeredPane getLayeredPane() {
		return super.getLayeredPane();
	}

	@Override
	public JRootPane getRootPane() {
		return super.getRootPane();
	}

	@Override
	public TransferHandler getTransferHandler() {
		return super.getTransferHandler();
	}

	@Override
	protected boolean isRootPaneCheckingEnabled() {
		return super.isRootPaneCheckingEnabled();
	}

	@Override
	protected String paramString() {
		return super.paramString();
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
	}

	@Override
	public void remove(Component comp) {
		super.remove(comp);
	}

	@Override
	public void repaint(long time, int x, int y, int width, int height) {
		super.repaint(time, x, y, width, height);
	}

	@Override
	public void setContentPane(Container contentPane) {
		super.setContentPane(contentPane);
	}

	@Override
	public void setDefaultCloseOperation(int operation) {
		super.setDefaultCloseOperation(operation);
	}

	@Override
	public void setGlassPane(Component glassPane) {
		super.setGlassPane(glassPane);
	}

	@Override
	public void setJMenuBar(JMenuBar menu) {
		super.setJMenuBar(menu);
	}

	@Override
	public void setLayeredPane(JLayeredPane layeredPane) {
		// TODO Auto-generated method stub
		super.setLayeredPane(layeredPane);
	}

	@Override
	public void setLayout(LayoutManager manager) {
		super.setLayout(manager);
	}

	@Override
	protected void setRootPane(JRootPane root) {
		super.setRootPane(root);
	}

	@Override
	protected void setRootPaneCheckingEnabled(boolean enabled) {
		super.setRootPaneCheckingEnabled(enabled);
	}

	@Override
	public void setTransferHandler(TransferHandler newHandler) {
		super.setTransferHandler(newHandler);
	}

	@Override
	public void update(Graphics g) {
		super.update(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}


    
}


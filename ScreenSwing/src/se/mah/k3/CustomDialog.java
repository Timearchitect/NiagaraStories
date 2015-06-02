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
			DrawPanel.words.add(new Word(new WordBuilder(txtWord.getText() ,(int)DrawPanel.mouseX,(int)DrawPanel.mouseY).angle("random")));	
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
		// TODO Auto-generated method stub
		super.addImpl(comp, constraints, index);
	}

	@Override
	protected JRootPane createRootPane() {
		// TODO Auto-generated method stub
		return super.createRootPane();
	}

	@Override
	protected void dialogInit() {
		// TODO Auto-generated method stub
		super.dialogInit();
	}

	@Override
	public AccessibleContext getAccessibleContext() {
		// TODO Auto-generated method stub
		return super.getAccessibleContext();
	}

	@Override
	public Container getContentPane() {
		// TODO Auto-generated method stub
		return super.getContentPane();
	}

	@Override
	public int getDefaultCloseOperation() {
		// TODO Auto-generated method stub
		return super.getDefaultCloseOperation();
	}

	@Override
	public Component getGlassPane() {
		// TODO Auto-generated method stub
		return super.getGlassPane();
	}

	@Override
	public Graphics getGraphics() {
		// TODO Auto-generated method stub
		return super.getGraphics();
	}

	@Override
	public JMenuBar getJMenuBar() {
		// TODO Auto-generated method stub
		return super.getJMenuBar();
	}

	@Override
	public JLayeredPane getLayeredPane() {
		// TODO Auto-generated method stub
		return super.getLayeredPane();
	}

	@Override
	public JRootPane getRootPane() {
		// TODO Auto-generated method stub
		return super.getRootPane();
	}

	@Override
	public TransferHandler getTransferHandler() {
		// TODO Auto-generated method stub
		return super.getTransferHandler();
	}

	@Override
	protected boolean isRootPaneCheckingEnabled() {
		// TODO Auto-generated method stub
		return super.isRootPaneCheckingEnabled();
	}

	@Override
	protected String paramString() {
		// TODO Auto-generated method stub
		return super.paramString();
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		// TODO Auto-generated method stub
		super.processWindowEvent(e);
	}

	@Override
	public void remove(Component comp) {
		// TODO Auto-generated method stub
		super.remove(comp);
	}

	@Override
	public void repaint(long time, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		super.repaint(time, x, y, width, height);
	}

	@Override
	public void setContentPane(Container contentPane) {
		// TODO Auto-generated method stub
		super.setContentPane(contentPane);
	}

	@Override
	public void setDefaultCloseOperation(int operation) {
		// TODO Auto-generated method stub
		super.setDefaultCloseOperation(operation);
	}

	@Override
	public void setGlassPane(Component glassPane) {
		// TODO Auto-generated method stub
		super.setGlassPane(glassPane);
	}

	@Override
	public void setJMenuBar(JMenuBar menu) {
		// TODO Auto-generated method stub
		super.setJMenuBar(menu);
	}

	@Override
	public void setLayeredPane(JLayeredPane layeredPane) {
		// TODO Auto-generated method stub
		super.setLayeredPane(layeredPane);
	}

	@Override
	public void setLayout(LayoutManager manager) {
		// TODO Auto-generated method stub
		super.setLayout(manager);
	}

	@Override
	protected void setRootPane(JRootPane root) {
		// TODO Auto-generated method stub
		super.setRootPane(root);
	}

	@Override
	protected void setRootPaneCheckingEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setRootPaneCheckingEnabled(enabled);
	}

	@Override
	public void setTransferHandler(TransferHandler newHandler) {
		// TODO Auto-generated method stub
		super.setTransferHandler(newHandler);
	}

	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		super.update(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}


    
}


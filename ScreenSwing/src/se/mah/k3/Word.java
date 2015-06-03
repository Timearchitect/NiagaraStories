package se.mah.k3;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import se.mah.k3.particles.FrameParticle;
import se.mah.k3.particles.RippleParticle;
import se.mah.k3.particles.TextParticle;

//This is the class for the word object. It contains the words that
//the user will have displayed in their mobile app. 
//It also contains a boolean to check if the word is active or not.

public class Word implements Health ,RenderOrder{
	ArrayList<Word> linkedWords = new ArrayList<Word>();
	private final int MAX_OFFSET=20;
	public static final int MIN_ANGLE=-10, MAX_ANGLE=10;
	private final float FORCEFACTOR = 0.05f;
	public String type="",wordId="",bending[], ownerId = "",text = "";
	public boolean active = true, occupied,colliding, selected, plural;	
	public User owner;
	public Firebase firebase = new Firebase("https://scorching-fire-1846.firebaseio.com/Used Words/"); // Root;
	public DataSnapshot dataSnapshot;
	public enum State {onTray, draging, placed,locked};
	public State state=State.onTray;
	public int xPos, yPos, width, height, margin = 20, offsetX = -20, offsetY = 40;
	public float pxPos, pyPos,txPos,tyPos, xVel,yVel,txVel, tyVel;
	public float health,angle= (int)((new Random().nextInt(MAX_ANGLE))+MIN_ANGLE*0.5) ;

	//WordSkin skin = new WordSkin(this);
	Shadow shadow = new Shadow(this);
	private float tAngle;

	public Word(String _text, String _ownerId) {
		this.text = _text;
		this.ownerId = _ownerId;
		//this.active = true;
	}

	public Word(String _text, String _ownerId,int _x,int _y, int _tx ,int _ty) {
		xPos=_x;
		yPos=_y;
		txPos=_tx;
		tyPos=_ty;
		this.text = _text;
		this.ownerId = _ownerId;
		//this.active = false;
	}

	public Word(DataSnapshot _dataSnapshot,String _text, String _ownerId,int _x,int _y, int _tx ,int _ty) {
		dataSnapshot=_dataSnapshot;
		wordId=_dataSnapshot.getKey();
		xPos=_x;
		yPos=_y;
		txPos=_tx;
		tyPos=_ty;
		this.text = _text;
		this.ownerId = _ownerId;
		//this.active = false;
	}

	public Word(WordBuilder wordBuilder) {

		this.linkedWords =wordBuilder.linkedWords;
		this.type=wordBuilder.type;
		this.wordId=wordBuilder.wordId;
		this.ownerId=wordBuilder.ownerId;
		this.text=wordBuilder.text;
		this.active =wordBuilder.active;
		this.occupied =wordBuilder.occupied;
		this.plural=wordBuilder.plural;
		this.bending=wordBuilder.bending;
		this.owner=wordBuilder.owner;
		this.dataSnapshot =wordBuilder.dataSnapshot;
		this.state=wordBuilder.state;
		this.xPos =wordBuilder.xPos;
		this.yPos=wordBuilder.yPos;
		this.width =wordBuilder.width;
		this.height =wordBuilder.height;
		this.margin=wordBuilder.margin;
		this.txPos =wordBuilder.txPos;
		this.tyPos=wordBuilder.tyPos;
		this.health =wordBuilder.health;
		this.angle=wordBuilder.angle;
	}

	public void setUser(User _u){ 
		if(!owner.equals(_u)){
			owner=_u;
			ownerId=_u.getId();
		}
	}

	public User getUser(){
		return owner;

	}

	public String getText(){
		return text;
	}

	public void setXPos(int _xPos){
		this.xPos = _xPos;
	}

	public int getXPos(){
		return xPos;
	}

	public void setYPos(int _yPos){
		this.yPos = _yPos;
	}

	public int getYPos(){
		return yPos;
	}

	public void setWidth(int _width){
		this.width = _width;
	}

	public int getWidth(){
		return width;
	}

	public void setHeight(int _height){
		this.height = _height;
	}

	public int getHeight(){
		return height;
	}

	public void setText(String _text){
		this.text = _text;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public boolean isActive(){
		return active;
	}

	public void setOwner(String _ownerId){
		this.ownerId = _ownerId;
		for(User u: DrawPanel.userList){
			if(u.getId().equals(this.getOwner())){
				owner=u;
				System.out.println("owner setted");
			}
		}
	}

	public String getOwner(){
		return ownerId;
	}

	public void respond(){
		DrawPanel.overParticles.add(new FrameParticle(xPos, yPos, this, 1));
	}

	public void appear(){
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, margin, 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, -margin, 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, (int) (margin * 0.5), 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, (int) (- margin * 0.5), 0, text));
		active=true;
		firebase.child(wordId+"/attributes/active").setValue(true);

		pxPos=xPos;
		pyPos=yPos;
		setHealth(text.length());
	}

	public void disappear(){
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, margin, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (margin * 0.5), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (margin * 0.2), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, 0, text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (- margin * 0.2), text));
		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, (int) (- margin * 0.5), text));

		DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, -margin, text));


		
		active=false;
		firebase.child(wordId+"/attributes/active").setValue(false);
	}
	public void deathAnimation(){
		tAngle=(new Random().nextInt()*90)-45;
		tyPos = Constants.screenHeight-120;
	}
	public void selected(){
		DrawPanel.overParticles.add( new FrameParticle(xPos, yPos, this, 0));
	}

	public void released(){
		DrawPanel.overParticles.add( new FrameParticle(xPos, yPos, this));
	

	}

	public void display() {


		AffineTransform oldTransform = DrawPanel.g2.getTransform();
		DrawPanel.g2.translate((int) (xPos +offsetX),(int) (yPos +offsetY));
		if(!Constants.simple)DrawPanel.g2.rotate(Math.toRadians(angle));
		DrawPanel.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		shadow.display();

		if ( owner==null) {
			DrawPanel.g2.setColor(Constants.wordStroke);
		}else {
			DrawPanel.g2.setColor(owner.getColor());
		}

		//angle++;
		DrawPanel.g2.fillRect((int)(0 - margin-width*0.5),(int)(3- margin * 0.5-height*0.5) , width + margin * 2,(int) (height + 6));
		DrawPanel.g2.setColor(Color.white);
		DrawPanel.g2.setFont(Constants.lightFont);
		DrawPanel.g2.drawString(text,(int)(0 -width*0.5),(int) (0 + height* 0.25) );

		switch(state.ordinal()){
		case 0 ://onTray
			break;
		case 1 : // draging
			DrawPanel.g2.setStroke(Constants.wordOutline);
			DrawPanel.g2.drawRect((int)(0 - margin-width*0.5),(int)(3- margin * 0.5-height*0.5) , width + margin * 2,(int) (height + 6));
			break;
		case 2 : // placed
			break;
		case 3 : // locked
			break;
		default :
		}
		//if(!Constants.simple)skin.display(DrawPanel.g2);

		DrawPanel.g2.setTransform(oldTransform);

		/*DrawPanel.g2.fillRect((int) (xPos  - (width * 0.5)) - margin, (int) (yPos + 3 - (height * 0.5) - margin * 0.5), width + margin * 2, height + 6);
		DrawPanel.g2.setColor(Color.white);
		DrawPanel.g2.setFont(Constants.lightFont);
		DrawPanel.g2.drawString(text, (int) (xPos - width * 0.5),(int) (yPos + height* 0.25));
		//DrawPanel.overParticles.add(new RustParticle ((int) (xPos + 3 - (width * 0.5)) - margin, (int) (yPos + 3 - (height * 0.5) - margin * 0.5), width + margin * 2, height + 6, Integer.valueOf(text.length())));

		if(state==State.draging){
			DrawPanel.g2.setStroke(Constants.wordOutline);
			DrawPanel.g2.drawRect((int) (xPos + 3 - (width * 0.5)) - margin, (int) (yPos + 3 - (height * 0.5) - margin * 0.5), width + margin * 2, height + 6);
		}
		DrawPanel.g2.drawString(text, (int) (xPos - width * 0.5),(int) (yPos + height* 0.25));*/




	}


	public void collisionVSWord (Word w){
		if((xPos + margin + width * 0.5) > (w.xPos - margin - w.width*0.5)&&(xPos - margin - width*0.5) < (w.xPos + margin +w.width*0.5)&&(yPos + margin * 0.5 + height*0.5) > (w.yPos - margin * 0.5 - w.height*0.5)&&(yPos - margin * 0.5 - height*0.5) < (w.yPos + margin * 0.5 + w.height*0.5)){
			w.txVel=(w.xPos-xPos)*FORCEFACTOR;
			w.tyVel=(w.yPos-yPos)*FORCEFACTOR;
			txVel=(xPos-w.xPos)*FORCEFACTOR;
			tyVel=(yPos-w.yPos)*FORCEFACTOR;
			colliding=true;
			DrawPanel.collisionSent=false;
		}
	}

	public void BoundCollision(){
		if(xPos < margin + width * 0.5){											//LEFT
			txPos += 5;
			colliding=true;
			DrawPanel.collisionSent=false;
		}else if( xPos>Constants.screenWidth - margin - ( width * 0.5)){			//RIGHT
			txPos -= 5;
			colliding=true;
			DrawPanel.collisionSent=false;
		}if(yPos < margin * 0.5 + height * 0.5){									//TOP
			tyPos += 5;
			colliding=true;
			DrawPanel.collisionSent=false;
		}else if( yPos>Constants.screenHeight - (margin * 0.5) - (height * 0.5)){	//BOTTOM
			tyPos -= 5;
			colliding=true;
			DrawPanel.collisionSent=false;
		}
	}

	public void update() {
		float xDiff=txPos-xPos;
		float yDiff=tyPos-yPos;
		float AngleDiff=tAngle-angle;
		//txPos=Math.cos(angle);
		//tyPos=Math.sin(angle);
		xPos+=(int)(xDiff*0.2);
		yPos+=(int)(yDiff*0.2);

		xVel=xPos-pxPos;
		yVel=yPos-pyPos;
		txVel*=0.5;
		tyVel*=0.5;
		txPos+=txVel;
		tyPos+=tyVel;
		pxPos=xPos;
		pyPos=yPos;		
		if(MAX_ANGLE>tAngle && tAngle >MIN_ANGLE){}else{tAngle*=0.9;}
		//DrawPanel.g2.drawString(String.valueOf(tAngle)+"   "+String.valueOf(angle), (int) (xPos),(int) (yPos + height* 3));
		angle+=(float)(AngleDiff*0.2);
		//if(this.MAX_ANGLE<angle )angle*=0.9;
		
		if (yPos >= Constants.screenHeight - 50){
			active = false;
			Constants.noCollision=false;
			System.out.println(active);
		}

		switch(state.ordinal()){
		case 0 ://onTray

			break;
		case 1 : // draging
			if(offsetX<this.MAX_OFFSET)offsetX+=4;
			if(offsetY<this.MAX_OFFSET)offsetY+=4;
			tAngle=(float) (yVel*0.9+xVel*0.9);
			tAngle %= 360;
			angle %= 360;
			break;

		case 2 : // placed
			offsetX=0;
			offsetY=0;
			break;

		case 3 : // locked

			break;

		default :
		}
		//skin.update();
	}

	public void link(Word w){


	}

	public void displayLinked(){
		float xDiff,yDiff,dist,angle;

		for(Word w : DrawPanel.words){
			xDiff= w.xPos-xPos;
			yDiff= w.yPos-yPos;
			angle=(float) Math.atan2(xDiff, yDiff);
			dist=(float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));

			if(dist<500){
				DrawPanel.g2.setColor(Color.white);			
				DrawPanel.g2.drawLine((int)(xPos-width*0.5),(int)(yPos-height*0.5),(int)(w.xPos-w.width*0.5),(int)(w.yPos-w.height*0.5));
			}

		}

	}


	public void setState(State _state){
		this.state = _state;
	}

	public int getState(){
		return state.ordinal();
	}	

	public void setHealth(float _amount) {
		health=_amount;		
	}

	public void damage(float _amount) {
		if(state==State.draging)health-=_amount;
		else health-=_amount;
		if(health<=0)dead();
	}

	public void dead() {
		//DrawPanel.overParticles.add(new TextParticle(xPos, yPos, width, height, 0, 0, text));
		DrawPanel.overParticles.add(new RippleParticle((int)xPos, (int)yPos,30));	
		Constants.noCollision=true;
		//Constants.noCollision=(Constants.noCollision==true)?(Constants.noCollision=false) :(Constants.noCollision=true);
		yPos = Constants.screenHeight;
		//System.out.println(yPos);
deathAnimation();
		//active=false;
		//firebase.child(wordId+"/attributes/active").setValue(false);
		//Firebase fireBaseWords = DrawPanel.myFirebaseRef.child("Regular Words");
		//fireBaseWords.child(+wordId+"/Active").setValue(false);

	}

	public String getWordId() {
		return wordId;
	}	
	public void setWordId(String _s) {
		wordId=_s;
	}	

	public static class WordBuilder
	{
		ArrayList<Word> linkedWords = new ArrayList<Word>();
		private String type,wordId,ownerId,text;
		private boolean active = true, occupied, plural;
		private String bending[];
		private User owner;
		private Firebase firebase = new Firebase("https://scorching-fire-1846.firebaseio.com/Used Words/"); // Root;
		private DataSnapshot dataSnapshot;
		private State state=Word.State.placed;
		private int xPos, yPos, width, height, margin=20;
		private float txPos,tyPos;
		private float health,angle;



		public WordBuilder(String text, int  x,int y) {
			this.text = text;
			this.xPos = x;
			this.yPos = y;
			this.txPos = x;
			this.tyPos = y;
			//	DrawPanel.metrics = DrawPanel.g2.getFontMetrics(Constants.font);
			this.width = DrawPanel.metrics.stringWidth(text);
			this.height = DrawPanel.metrics.getHeight();
			this.firebase.setValue("word888");
			this.wordId="word888";
		}

		public WordBuilder plural(boolean plural) {
			this.plural = plural;
			return this;
		}
		public WordBuilder active(boolean active) {
			this.active = active;
			return this;
		}
		public WordBuilder occupied(boolean occupied) {
			this.occupied = occupied;
			return this;
		}
		public WordBuilder linkedWords(ArrayList<Word> linkedWords ) {
			this.linkedWords = linkedWords;
			return this;
		}

		public WordBuilder type(String type) {
			this.type = type;
			return this;
		}
		public WordBuilder wordId(String wordId) {
			this.wordId = wordId;
			return this;
		}
		public WordBuilder ownerId(String ownerId) {
			this.ownerId = ownerId;
			return this;
		}
		public WordBuilder bending(String[] bending) {
			this.bending = bending;
			return this;
		}
		public WordBuilder dataSnapshot(DataSnapshot dataSnapshot) {
			this.dataSnapshot = dataSnapshot;

			return this;
		}
		public WordBuilder state(State _state) {
			this.state = _state;
			return this;
		}

		public WordBuilder owner(User owner) {
			this.owner = owner;
			return this;
		}

		public WordBuilder xPos(int xPos) {
			this.xPos = xPos;
			return this;
		}

		public WordBuilder yPos(int yPos) {
			this.yPos = yPos;
			return this;
		}
		public WordBuilder txPos(int txPos) {
			this.txPos = txPos;
			return this;
		}

		public WordBuilder ytPos(int tyPos) {
			this.tyPos = tyPos;
			return this;
		}

		public WordBuilder health(float health) {
			this.health = health;
			return this;
		}
		public WordBuilder angle(float angle) {
			this.angle = angle;
			return this;
		}
		public WordBuilder angle(String command) {
			if(command.equals("random"))angle= (int)((new Random().nextInt(MAX_ANGLE))+MIN_ANGLE*0.5) ;
			return this;
		}
		public void addToFirebase(){


		}
		public Word build() {
			Word word =  new Word(this);
			//validateUserObject(user);
			return word;
		}
	}

	@Override
	public void toBottom(Object o) {
		int amountOfUnderlaying=0;
		for(Word w: DrawPanel.words){
			amountOfUnderlaying++;
		}
		if(DrawPanel.words.indexOf((Word)o) > amountOfUnderlaying){
			DrawPanel.words.remove(DrawPanel.words.indexOf(this));
			DrawPanel.words.add(0, this);
		}
	}

	@Override
	public void toTop(Object o) {
		int amountOfoverlaying=0;
		for(Word w: DrawPanel.words){
			if(w.state==state.draging)amountOfoverlaying++;
		}
		if(DrawPanel.words.indexOf((Word)o) < DrawPanel.words.size()-amountOfoverlaying){
		DrawPanel.words.remove(DrawPanel.words.indexOf(this));
		DrawPanel.words.add(DrawPanel.words.size(), this);
		}
	}

	@Override
	public void toIndex(int i) {
		// TODO Auto-generated method stub

	}
}

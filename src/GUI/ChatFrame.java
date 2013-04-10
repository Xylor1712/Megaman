package GUI;

import images.ImageLoader;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


import main.Main;

public class ChatFrame {
	
	public static final int LINES = 16;
	public static final float MAX_ALPHA = 0.6f;
	
	public static final int LINE_HEIGHT = 16;
	
	public static final float FADE_AMOUNT = 0.05f;
	public static final int FADE_IN = 1;
	public static final int FADE_OUT = -1;
	
	public int fade = FADE_OUT;
	
	private ArrayList<String> inhalt = new ArrayList<>();
	private Canvas root;
	
	public boolean writesText = false;
	public String inputText = "";
	
	public float alpha = 0f;
	
	private int charSize = 3*LINE_HEIGHT/4;
	private int amount_lines = 0;
	
	public static BufferedImage newMessageIcon;
	public boolean showNewMessageIcon = true;
	private int highestIndexShown = 0;
	public static final int ICON_HEIGHT = 26;

	
	public ChatFrame(Canvas root){
		this.root = root;
		
		initNewMessageImg();
		//DummyText
		//for(int i = 0; i < 100; i++) inhalt.add("TestLine No. " + i);
	}
	
	private void initNewMessageImg(){
		if(newMessageIcon == null){
			newMessageIcon = ImageLoader.getImage("newMessageIcon.png");
		}
	}
	
	public void paint(Graphics g, Rectangle pos){
		
		if(fade == FADE_IN && alpha < MAX_ALPHA){
			alpha += FADE_AMOUNT;
		}
		else if(fade == FADE_OUT && alpha > 0){
			alpha -= FADE_AMOUNT/5;
		}
		
		if(!Main.clock.isEnabled() || !Main.gameRuns) alpha = MAX_ALPHA;
		
		if(this.alpha <= 0){
			if(showNewMessageIcon && highestIndexShown + 1 < inhalt.size()) root.drawNewMessageIcon = true;
			return;
		}
		
		amount_lines = Math.min(pos.height / (LINE_HEIGHT + 2), inhalt.size());
		root.drawNewMessageIcon = false;
		
		
		paintBackground(g, pos);
		
		paintFrame(g, pos);
		
		paintText(g, pos);
		
		if(writesText) paintInputBox(g, pos);
		
		
		
		
	}
	
	private void paintBackground(Graphics g, Rectangle pos){
		g.setColor(new Color(0f, 0f, 0f, alpha));
		
		g.fillRect(pos.x, pos.y, pos.width, pos.height);
	}
	
	private void paintFrame(Graphics g, Rectangle pos){
		g.setColor(new Color(1f, 1f, 1f, alpha));

		g.drawRect(pos.x, pos.y, pos.width, pos.height);
		int y = pos.y + pos.height - 2*LINE_HEIGHT;
		g.drawLine(pos.x, y, pos.x + pos.width, y);
	}
	
	private void paintText(Graphics g, Rectangle pos){
		g.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, charSize));
		
	    FontMetrics fm = g.getFontMetrics();
	    
	    for(int i = 0; i < amount_lines; i++){
	    	String s = inhalt.get(inhalt.size() - i - 1);
	    	
		    Rectangle2D textsize = fm.getStringBounds(s, g);
		    int xPos = (int)(pos.x + textsize.getHeight()/2);
		    int yPos = (int)(pos.y + (amount_lines - i - 1) * LINE_HEIGHT + textsize.getHeight());

		    g.drawString(s, xPos, yPos);
	    }
	    highestIndexShown = inhalt.size() - 1;
	}
	
	private int focusCounter = 0;
	private boolean drawFocus = true;
	
	private void paintInputBox(Graphics g, Rectangle pos){
		g.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, charSize*2));
		
	    FontMetrics fm = g.getFontMetrics();
	    
	    Rectangle2D textsize = fm.getStringBounds(inputText, g);
	    int xPos = (int) (pos.x + textsize.getHeight()/2);
	    int yPos = (int) (pos.y + pos.height - LINE_HEIGHT + textsize.getHeight()/4);
	    
	    g.drawString(inputText, xPos, yPos);
	    
	    focusCounter++;
	    if(focusCounter > 20){
	    	focusCounter = 0;
	    	drawFocus = !drawFocus;
	    }
	    
	    if(drawFocus) g.drawLine((int)(xPos + textsize.getWidth() + 2),(int) ( yPos - textsize.getHeight()/2),(int) (xPos + textsize.getWidth() + 2), (int)(yPos + 2));
	    
	}
	
	public void fadeIn(){
		this.fade = FADE_IN;
		if(Main.debug) System.out.println("Fade in");
	}
	
	public void fadeOut(){
		this.fade = FADE_OUT;
		if(Main.debug) System.out.println("Fade out");
	}
	
	public void addText(String s){
		if(s.equals("")) return;
		inhalt.add(s);
	}
	
	public void charTyped(char c){
		inputText += c;
	}
	
	public void deleteLastChar(){
		if(inputText.length() > 0) inputText = inputText.substring(0, inputText.length()-1);
	}
	
	public void stopTyping(){
		inputText = "";
		writesText = false;
		fadeOut();
	}

}

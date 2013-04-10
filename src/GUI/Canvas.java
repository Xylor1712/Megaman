package GUI;

import images.ImageLoader;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.*;

import javax.swing.*;

import main.GameRules;
import main.Main;
import main.Util;

import objects.GameObject;
import objects.PlayerCharacter;

public class Canvas extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3013269106611132217L;
	
	private static final String line = System.getProperty("line.separator");
	
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
                         .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
	private BufferedImage img;
	
	private PlayerCharacter player = Main.player1;
	
	private static BufferedImage backgroundImage;
//	private static TexturePaint backgroundPaint;
	private BufferedImage background;
	
	public boolean displayStatFrame = false;
	
	public ChatFrame chat;
	public InterfaceFrame interfaceFrame;
	public boolean drawNewMessageIcon = false;
	
	public float infoMessageAlpha = 0f;
	public String infoMessageText = "";
	public float infoMessageFadeOutAmount = 0.01f;
	
	
	public Canvas(){
		initBackground();
		this.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent e){
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				background();				
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				if(gfxConf == null) return;
				background();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				background();
			}
		});
		
		this.chat = new ChatFrame(this);
		this.interfaceFrame = new InterfaceFrame();
	}
	
	public void initBackground(){
		/*
		if(backgroundImage == null){
			if(Main.debug) System.out.println("Load Image");
			try{
				backgroundImage = ImageIO.read(this.getClass().getResource("stars.gif"));
				backgroundPaint = new TexturePaint(backgroundImage, new Rectangle(0,0,backgroundImage.getWidth(), backgroundImage.getHeight()));
			}
			catch(IOException e){
				System.out.println("Failed to load File: stars.png");
			}
			catch(Exception e){
				System.out.println("Grass loading: failed");
				e.printStackTrace();
			}
		}
		*/
		if(backgroundImage == null){
			backgroundImage = ImageLoader.getImage("Background.png");
		}
	}
	
	public void background(){
		if(Main.mapSize == null) return;
		
		if(background == null || background.getWidth() != Main.mapSize.width || background.getHeight() != Main.mapSize.height){
			background = gfxConf.createCompatibleImage( Main.mapSize.width, Main.mapSize.height );
		}
		Graphics2D g2d = background.createGraphics();
		/*
		if(g2d.getClipBounds() == null){
			g2d.setClip(0,0,Main.mapSize.width,Main.mapSize.height);
		}
		g2d.setPaint(backgroundPaint);
		g2d.fillRect(0,0,Main.mapSize.width,Main.mapSize.height);
		*/
		
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(backgroundImage, 0, 0, Main.mapSize.width, Main.mapSize.height, this);
	}
	
	@Override
	protected void paintComponent(Graphics g){
		//Buffer-Zeugs
		PlayerCharacter curPlayer = Main.getCurrentPlayer();
		if(curPlayer == null) return; 
		if(player != curPlayer) player = curPlayer;
		
		
		if(img == null || img.getWidth() != Main.mapSize.width || img.getHeight() != Main.mapSize.height){
			img = gfxConf.createCompatibleImage( Main.mapSize.width, Main.mapSize.height );
		}
		Graphics g2 = img.createGraphics();
		if(g2.getClipBounds() == null){
			g2.setClip(0,0,Main.mapSize.width, Main.mapSize.height);
		}
		Point center = new Point((int)player.getxPos() + player.getWidth()/2 , (int)player.getyPos() + player.getHeight()/2);
		
		Dimension frameBounds = getSize();
		
		Rectangle drawBounds = new Rectangle(-center.x+frameBounds.width/2,-center.y+frameBounds.height/2, frameBounds.width, frameBounds.height);
		
		if(drawBounds.x > 0) drawBounds.x = 0;
		if(drawBounds.y > 0) drawBounds.y = 0;
		if(drawBounds.x < -(Main.mapSize.width-frameBounds.width)) drawBounds.x = -Main.mapSize.width+frameBounds.width;
		if(drawBounds.y < -(Main.mapSize.height-frameBounds.height)) drawBounds.y = -Main.mapSize.height+frameBounds.height;
		
		
		paintContent(g2, drawBounds);
	
		g.drawImage( img, drawBounds.x,drawBounds.y,this);
		
		/*
		if(Main.debug){
			System.out.println("Center: " + center);
			System.out.println("Frame: " + frameBounds);
			System.out.println("DrawBounds: " + drawBounds);
			System.out.println("MapSize: " + Main.mapSize);
		}
		*/
	}
	
	private void paintContent(Graphics g, Rectangle bounds){
		
		Rectangle newRect = new Rectangle(-bounds.x, -bounds.y, bounds.width, bounds.height);

		paintBackground(g);
		
		paintStatics(g, newRect);
		
		if(drawNewMessageIcon) g.drawImage(ChatFrame.newMessageIcon, newRect.x, newRect.y + bounds.height - ChatFrame.ICON_HEIGHT, null);
		
		PlayerCharacter pc = Main.getCurrentPlayer();
		if(pc != null) pc.paintBuffInfos(g, bounds);
		
		paintTeamScores(g, newRect); //sieht scheiße aus atm
		
		paintMissiles(g, newRect);
		
		paintCharacters(g, newRect);
		
		interfaceFrame.paint(g, newRect);
		
		if(displayStatFrame || !Main.clock.isEnabled() || !Main.gameRuns) StatFrame.paint(g, bounds);
		
		if(displaymsg) paintMessage(g, bounds);
		
		if(Main.displayFps) paintFps(g, bounds);
		
		paintChat(g, bounds);
		
		paintInfoMessage(g, bounds);
	}
	
	private void paintMessage(Graphics g, Rectangle bounds){
		g.setColor(Color.white);
		f = new Font(Font.DIALOG_INPUT, Font.PLAIN, (int)msgSize);
	    g.setFont(f);
	    FontMetrics fm = g.getFontMetrics();

	    Rectangle2D textsize = fm.getStringBounds(msg, g);
	    int xPos = (int)(msgX - textsize.getWidth()/2 - bounds.x);
	    int yPos = (int)(msgY - bounds.y);
	    g.drawString(msg, xPos, yPos);
	    increaseMsg();
	}
	
	private void paintInfoMessage(Graphics g, Rectangle bounds){
		if(infoMessageAlpha <= 0 || infoMessageAlpha > 1 || !Main.gameRuns) return;
		g.setColor(new Color(0f, 0f, 0f, infoMessageAlpha));
		infoMessageAlpha -= infoMessageFadeOutAmount;
		f = new Font(Font.DIALOG_INPUT, Font.BOLD, 20);
	    g.setFont(f);
	    FontMetrics fm = g.getFontMetrics();

	    Rectangle2D textsize = fm.getStringBounds(infoMessageText, g);
	    int xPos = (int)(bounds.width/2 - textsize.getWidth()/2 - bounds.x);
	    int yPos = (int)(bounds.height/4 - textsize.getHeight() - bounds.y);
	    g.drawString(infoMessageText, xPos, yPos);
	}
	
	private void paintBackground(Graphics g){
		if(background == null || background.getWidth() != Main.mapSize.width || background.getHeight() != Main.mapSize.height){
			background();
		}
		g.drawImage(background, 0, 0,null);
	}
	
	private void paintStatics(Graphics g, Rectangle bounds){
		for(GameObject obj : Main.getStaticList()){
			obj.paint(g, bounds);
		}
	}
	
	private void paintMissiles(Graphics g, Rectangle bounds){
		for(GameObject obj : Main.getMissileList()){
			obj.paint(g, bounds);
		}
	}
	
	private void paintCharacters(Graphics g, Rectangle bounds){
		for(GameObject obj : Main.getCharacterList()){
			obj.paint(g, bounds);
		}
	}
	
	private void paintChat(Graphics g, Rectangle bounds){
		int x = -bounds.x + bounds.width/40;
		int y = -bounds.y + 23* bounds.height /40;
		int width = 18*bounds.width/40;
		int height = 16*bounds.height/40;
		chat.paint(g, new Rectangle(x, y, width, height));
	}
	
	private void paintFps(Graphics g, Rectangle bounds){
		g.setColor(Color.WHITE);
		f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 12);
	    g.setFont(f);
	    FontMetrics fm = g.getFontMetrics();
	    String fps = "FPS: " + 1000 * 1000 * 1000 / Main.timeForFrame + line + " NanoSecs per MathOperation: " + Main.timeForMath;
	    Rectangle2D textsize = fm.getStringBounds(fps, g);
	    int xPos = (int)(bounds.width - textsize.getWidth() - textsize.getHeight() - bounds.x);
	    int yPos = (int)(textsize.getHeight() - bounds.y);
	    g.drawString(fps, xPos, yPos);
	}
	
	private void paintTeamScores(Graphics g, Rectangle bounds){
		if(!GameRules.isTeamGame()) return;
		
		int[] teams = GameRules.teams;
		int[] scoreOfTeam = GameRules.scoreOfTeam;
		
		if(teams.length != scoreOfTeam.length){
			System.err.println("Fail");
			return;
		}
		
		int size = this.getHeight()/15;
		int x = bounds.width + bounds.x - size;
		int y = bounds.height/2 + bounds.y - teams.length*(size/2+4);
		Graphics2D g2 = (Graphics2D)g;
		g2.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, size));
		new Arc2D.Double(x, y, size, size, 0, 360, Arc2D.OPEN);
		for(int i = 0; i < teams.length; i++){
			g2.setColor(GameRules.getTeamColor(teams[i], 1f));

			g2.fill(new Arc2D.Double(x, y, size, size, 0, 360, Arc2D.OPEN));
			
			g2.setColor(GameRules.getTeamColorComplement(teams[i], 1f));
			Util.drawStringCenteredAt(g2, ""+scoreOfTeam[i], x+size/2, y+size/4);
			
			y += size + 4;
		}
	}
	
	private String msg = "";
	private boolean displaymsg = false;
	private double msgSize = 10;
	private Font f = new Font(Font.DIALOG_INPUT, Font.BOLD, (int)msgSize);
	private double msgX = 0;
	private double msgY = 0;
	
	
	public void displayMessage(String s){
		this.msg=s;
		this.displaymsg = true;
	}
	public void increaseMsg(){
		double secsUntilMid = 3;
		
		if(msgX <= this.getWidth()/2) msgX += this.getWidth()/2d / Main.fps / secsUntilMid;
		if(msgY <= this.getHeight()/2) msgY += this.getHeight()/2d / Main.fps / secsUntilMid;
		if(msgSize <= this.getHeight()/6) msgSize += this.getHeight()/6d / Main.fps / secsUntilMid;
		/*
		if(msgX < this.getWidth()/6+10) msgX +=0.6 * konst;
		if(msgY < this.getHeight()/2-80/2) msgY += 1.25 * konst;
		if(msgSize < 80) msgSize += 0.25 * konst;
		*/
		
	}
	public void stopDisplayMessage(){
		this.displaymsg = false;
		this.msgX = 0;
		this.msgY = 0;
		this.msgSize = 10;
		this.msg = "";
	}
	
	public void startDisplayChat(){
		this.chat.fadeIn();
	}
	
	public void stopDisplayChat(){
		this.chat.fadeOut();
	}
	
	public void setInfoMessage(String s){
		this.infoMessageText = s;
		this.infoMessageAlpha = 1f;
	}
}
		
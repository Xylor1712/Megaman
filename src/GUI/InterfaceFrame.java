package GUI;

import images.ImageLoader;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GameRules;
import main.Main;
import main.Util;

import objects.PlayerCharacter;

public class InterfaceFrame {
	
	public static BufferedImage healthAndExpBars;
	public static BufferedImage currentHealthAndExpBar;
	public static int health = 0;
	public static int exp = 0;
	
	
	public static BufferedImage infoFrame;
	public static BufferedImage currentInfoFrame;
	public static int level = 0;
	public static String type = "";
	
	public InterfaceFrame(){
		initImg();
	}
	
	private void initImg(){
		initHealthAndExpBars();
		initInfoFrame();
	}
	
	private void initInfoFrame(){
		if(infoFrame == null){
			infoFrame = ImageLoader.getImage("infoField.png");
		}
	}

	private void initHealthAndExpBars() {
		if(healthAndExpBars == null){
			healthAndExpBars = ImageLoader.getImage("healthbar.png");
		}
	}

	public void paint(Graphics g, Rectangle bounds){
		paintHealthAndExp(g, bounds);
		paintInfoFrame(g, bounds);
	}
	
	private void paintHealthAndExp(Graphics g, Rectangle bounds){
		PlayerCharacter pc = Main.getCurrentPlayer();
		if(pc == null || !GameRules.playerCanLevel()) return;
		if(health != pc.getHealth() || exp != pc.getExp()) computeNewHPandExpBar(pc);
		if(currentHealthAndExpBar != null){
			
			int width =(int) (bounds.width/2.5);
			int height = (int) (0.15 * width);
			int x = bounds.x + bounds.width/2 - width/2;
			int y = bounds.y + bounds.height - height;
			
			g.drawImage(currentHealthAndExpBar, x, y, width, height, null);
			
		}
	}
	
	public void computeNewHPandExpBar(PlayerCharacter pc){
		if(pc == null) return;
		
		health = pc.getHealth();
		exp = pc.getExp();
		
		double percentage = 1d * health / pc.getMaxHealth();
		if(percentage > 1) percentage = 1;
		if(percentage <= 0) return;
		
		
		int lvl = pc.getLevel();
		int[] LEVEL_BREAKPOINTS = PlayerCharacter.LEVEL_BREAKPOINTS;
		int lastLevel = (lvl <= 1 ? 0 : LEVEL_BREAKPOINTS[lvl-2]);
		int nextLevel = (lvl >= 10 ? LEVEL_BREAKPOINTS[8] : LEVEL_BREAKPOINTS[lvl-1]);
		
		int diff = nextLevel - lastLevel;
		int earned = exp - lastLevel;
		
		BufferedImage background = healthAndExpBars.getSubimage(0, 0, 800, 120);
		BufferedImage healthBar = healthAndExpBars.getSubimage(25, 250,(int) (750*percentage), 80);
		BufferedImage overlay = healthAndExpBars.getSubimage(0, 360, 800, 120);
		
		currentHealthAndExpBar = Util.getTransparentImage(800, 120);
		Graphics2D g2 = (Graphics2D) currentHealthAndExpBar.getGraphics();
		g2.drawImage(background, 0, 0, 800, 120, null);
		g2.drawImage(healthBar, 25, 10, null);
		g2.setColor(new Color(0f, 1f, 1f, 1f));
		if(diff > 0){
			g2.fillRect(60, 90, 680 * earned / diff, 20);
		}
		else{
			g2.fillRect(60, 90, 680, 20);
		}
		g2.drawImage(overlay, 0, 0, 800, 120, null);
		
	}
	
	private void paintInfoFrame(Graphics g, Rectangle bounds){

		PlayerCharacter pc = Main.getCurrentPlayer();
		if(pc == null || !GameRules.playerCanLevel()) return;
		if(level != pc.getLevel() || type != pc.getType()) computeNewInfoFrame(pc);
		if(currentInfoFrame != null){
			
			int x = bounds.x;
			int y = bounds.y + 4*bounds.height/5;
			int height = bounds.height /5;
			int width = bounds.height / 4;
			
			g.drawImage(currentInfoFrame, x, y, width, height, null);
			
		}
		
	}
	
	private void computeNewInfoFrame(PlayerCharacter pc){
		if(pc == null) return;
		
		type = pc.getType();
		level = pc.getLevel();
		
		currentInfoFrame = Util.getTransparentImage(500, 400);
		Graphics2D g2 = (Graphics2D)currentInfoFrame.getGraphics();
		g2.drawImage(infoFrame.getSubimage(0, 400, 500, 400), 0, 0, null);
		g2.drawImage(pc.getPortrait(), 20, 20, 150, 150, null);
		g2.setColor(new Color(43, 17, 0, 255));
		g2.fillArc(140, 140, 70, 70, 0, 360);
		g2.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 60));
		g2.setColor(new Color(0xFFB100));
		Util.drawStringCenteredAt(g2, ""+level, 175, 155);
		g2.drawImage(infoFrame.getSubimage(0, 0, 500, 400), 0, 0, null);
	}
	
	
	
	
	
	
	
	
}

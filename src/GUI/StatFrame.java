package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import objects.PlayerCharacter;

import main.GameRules;
import main.Main;


public class StatFrame {
	
	private static String[] stats = GameRules.getDisplayStats();
	private static final Color color = new Color(1f, 1f, 1f, 0.5f);
	
	public static void paint(Graphics g, Rectangle bounds){
		
		Rectangle frameBounds = new Rectangle(
				(int) (-bounds.x + bounds.getWidth()/8),
				(int) (-bounds.y + bounds.getHeight()/8),
				(int) (3*bounds.getWidth()/4),
				(int) (3*bounds.getHeight()/4)
		);
		
		Graphics2D g2d = (Graphics2D)g;
		
		stats = GameRules.getDisplayStats();
		
		ArrayList<PlayerCharacter> playerList = Main.getPlayerList();
		
		paintBackground(frameBounds, g2d);
		
		paintGrid(frameBounds, g2d, playerList.size());
		
		paintText(frameBounds, g2d, playerList);
		
		paintStats(frameBounds, g2d, playerList);
		
	}



	private static void paintBackground(Rectangle bounds, Graphics2D g2d) {
		Color c = new Color(0f, 0f, 0f, 0.8f);
		g2d.setColor(c);
		g2d.fillRect(bounds.x, bounds.y,(int) bounds.getWidth(),(int) bounds.getHeight());
		g2d.setColor(color);
		g2d.drawRect(bounds.x, bounds.y,(int) bounds.getWidth(),(int) bounds.getHeight());
		
	}
	
	private static void paintGrid(Rectangle bounds, Graphics2D g2d, int anzPlayer) {
		g2d.setColor(color);
		
		int lineHeight = (int) bounds.height/9;
		
		g2d.drawLine(bounds.x, (int)(bounds.y + lineHeight),(int) (bounds.x + bounds.getWidth()), (int)(bounds.y + lineHeight));
		g2d.drawLine((int)(bounds.x + bounds.getWidth()/2), bounds.y, (int)(bounds.x + bounds.getWidth()/2) , (int) (bounds.y + bounds.getHeight()));
		
		for(int i = 1; i <= anzPlayer; i++){
			g2d.drawLine(bounds.x, bounds.y + (i+1)*lineHeight, (int) (bounds.x + bounds.getWidth()), bounds.y + (i+1)*lineHeight);
		}
		
		int distance = (int) ((bounds.width/2) / stats.length);
		for(int i = 1; i < stats.length; i++){
			g2d.drawLine(bounds.x + bounds.width / 2 + i*distance, bounds.y, bounds.x + bounds.width / 2 + i*distance, (int)(bounds.y + bounds.height));
		}
	}
	
	private static void paintText(Rectangle bounds, Graphics2D g2d, ArrayList<PlayerCharacter> list){
		g2d.setColor(color);
		
		int lineHeight = (int) bounds.height/9;
		int constant = (int) lineHeight/4;
		
	    g2d.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 3*lineHeight/4));

		g2d.drawString("Statistics", bounds.x + constant, bounds.y + lineHeight - constant);
		
	    FontMetrics fm = g2d.getFontMetrics();
	    
	    //Info - Zeile:
	    int distance = (int) ((bounds.width/2) / stats.length);
	    Rectangle2D textsize = fm.getStringBounds("W", g2d);
	    int xPos = (int)(bounds.x + bounds.width/2 + distance/2 - textsize.getWidth()/2);
	    int yPos = (int)(bounds.y + lineHeight - constant);
	    for(String s : stats){
	    	g2d.drawString(s, xPos, yPos);
	    	xPos += distance;
	    }
	}
	
	private static void paintStats(Rectangle bounds, Graphics2D g2d,
			ArrayList<PlayerCharacter> list) {
		g2d.setColor(color);
		
		int lineHeight = (int) bounds.height/9;
		int constant = (int) lineHeight/4;
		int distance = (int) ((bounds.width/2) / stats.length);
		

		
		g2d.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, lineHeight/2));
		FontMetrics fm = g2d.getFontMetrics();
		
		for(int i = 0; i < list.size(); i++){
			PlayerCharacter pc = list.get(i);
			
			g2d.setColor(GameRules.getTeamColor(pc.team, 0.5f));
			
			//Draw Name
			int x = (int) bounds.x + constant;
			int y = (int) bounds.y + lineHeight * (i+2) - constant;
			g2d.drawString(pc.getName(), x, y);
			
			
			x = (int)(bounds.x + bounds.width/2 + distance/2);
			for(String s : stats){
				switch(s){
				case "W":
					//Draw Wins
					String curString = "" + pc.getWins();
					Rectangle2D textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
					break;
				case "K":
					//Kills
				    curString = "" + pc.getKills();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				    break;
				case "D": 
					//Deaths
				    curString = "" + pc.getDeaths();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				    break;
				case "P": 
					//Ping
				    curString = "" + pc.getPing();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				    break;
				case "F": 
					//Flags
				    curString = "" + pc.getFlagsCaptured();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				    break;
				case "T": 
					//Tower
				    curString = "" + pc.getTowerKills();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				    break;
				case "M": 
					//Flags
				    curString = "" + pc.getMinionKills();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				    break;
				case "L":
					//Level
				    curString = "" + pc.getLevel();
				    textsize = fm.getStringBounds(curString, g2d);
				    g2d.drawString(curString,(int) (x - textsize.getWidth()/2), y);
				}

			    x += distance;
			}
		}
	}
	
	
	
}

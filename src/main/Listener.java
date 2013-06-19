package main;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.FocusManager;
import javax.swing.KeyStroke;

import objects.GameObject;
import objects.Missile;
import objects.PlayerCharacter;
import objects.Rect;
import objects.SimpleRangedMinion;

public class Listener implements KeyListener, MouseListener{
	
	private boolean currentlyTyping = false;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	Listener(){
		FocusManager fm = FocusManager.getCurrentManager();
		HashSet newKeys = new HashSet(fm.getDefaultFocusTraversalKeys(FocusManager.
	        FORWARD_TRAVERSAL_KEYS));
	    newKeys.remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0));
	    fm.setDefaultFocusTraversalKeys(FocusManager.FORWARD_TRAVERSAL_KEYS,
	                                newKeys);
	}
	
	public static final int RELOAD = 116;
	
	
	public void keyTyped(KeyEvent e){
		char c = e.getKeyChar();
		if(currentlyTyping && Util.isValidChar(c)){
			System.out.println("CharTyped: '" + e.getKeyChar() + "'");
			Main.canvas.chat.charTyped(e.getKeyChar());
		}
	}
	
	public void keyPressed(KeyEvent e){
//		if(Main.debug) System.out.println(e.getKeyCode());
		
		if(currentlyTyping){
			switch(e.getKeyCode()){
			case KeyEvent.VK_ESCAPE:
				Main.canvas.chat.stopTyping();
				break;
			case KeyEvent.VK_BACK_SPACE:
				Main.canvas.chat.deleteLastChar();
				break;
			case KeyEvent.VK_ENTER:
				typing();
				break;
			}
		}
		else{
			switch(e.getKeyCode()){
			case KeyEvent.VK_F1:
				testSaveInFile();
				break;
			case KeyEvent.VK_F2:
				//testLoad();
				break;
			case KeyEvent.VK_O:
				SimpleRangedMinion m = new SimpleRangedMinion(0,0,new Point(Main.mapSize.width/2, Main.mapSize.height/2));
				Main.addCompList(m);
				System.out.println("Added: " + m.transformToString());
				if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/spawn " + m.id + " " + m.transformToString());
				break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_T:
				typing();
				break;
			case KeyEvent.VK_TAB:
				Main.canvas.displayStatFrame = true;
				break;
			case KeyEvent.VK_P:
				if(Main.gameRuns) Main.switchMode();
				else{
					if(Main.mode == Main.NORMAL_MODE) Main.restartNormalGame();
					else if(Main.mode == Main.SERVER_MODE){
						Main.restartServerGame();
						if(Main.debug) System.out.println("RestartServerGame");
					}
				}
				break;
			case KeyEvent.VK_F5:
				if(Main.mode == Main.NORMAL_MODE) Main.restartNormalGame();
				else if(Main.mode == Main.SERVER_MODE){
					Main.restartServerGame();
					if(Main.debug) System.out.println("RestartServerGame");
				}
				break;
			case KeyEvent.VK_D:
					if(Main.mode == Main.CLIENT_MODE) Main.client.send("/turnRight");
					if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " turnRight");
						Main.player1.turnRight();
						//Main.debug = !Main.debug;
						break;
			case KeyEvent.VK_A:
					if(Main.mode== Main.CLIENT_MODE) Main.client.send("/turnLeft");
					if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " turnLeft");
					Main.player1.turnLeft();
					break;
			case KeyEvent.VK_F:
					Main.printStatus();
					break;
			case KeyEvent.VK_SPACE:
					if(Main.mode == Main.CLIENT_MODE) Main.client.send("/jump");
					if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " jump");
					Main.player1.jump();
					break;
			case KeyEvent.VK_RIGHT: 
					if(Main.mode != Main.NORMAL_MODE)return; 
					Main.player2.turnRight();
					break;
			case KeyEvent.VK_LEFT:
					if(Main.mode != Main.NORMAL_MODE)return; 
					Main.player2.turnLeft();
					break;
			case KeyEvent.VK_UP:
					if(Main.mode != Main.NORMAL_MODE)return; 
					Main.player2.up();
					break;
			case KeyEvent.VK_DOWN:
					if(Main.mode != Main.NORMAL_MODE)return; 
					Main.player2.down();
					break;
			case KeyEvent.VK_LESS:
					if(Main.mode == Main.CLIENT_MODE) Main.client.send("/attack");
					if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " attack");
					Main.player1.attack();
					break;
					
			case KeyEvent.VK_S:
					if(Main.mode == Main.CLIENT_MODE) Main.client.send("/down");
					if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " down");
					Main.player1.down();
					break;
			case KeyEvent.VK_W:
					if(Main.mode == Main.CLIENT_MODE) Main.client.send("/up");
					if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " up");
					Main.player1.up();
					break;
			case KeyEvent.VK_NUMPAD0:
					if(Main.mode != Main.NORMAL_MODE)return; 
					Main.player2.jump();
					break;
			case KeyEvent.VK_NUMPAD1:
					if(Main.mode != Main.NORMAL_MODE)return; 
					Main.player2.attack();
					break;
					
			}
		}
	}
	private void typing() {
		currentlyTyping = !currentlyTyping;
		
		if(Main.debug) System.out.println("Typing: " + currentlyTyping);
		
		if(currentlyTyping){
			Main.canvas.chat.writesText = true;
			Main.canvas.chat.fadeIn();
		}
		else{
			String text = Main.canvas.chat.inputText;
			if(text.length() > 3){
				switch(Main.mode){
				case Main.NORMAL_MODE:
					Main.canvas.chat.addText(text);
					break;
				case Main.SERVER_MODE:
					Main.server.sendMessage(text);
					break;
				case Main.CLIENT_MODE:
					Main.client.sendMessage(text);
					break;
				}
			}
			Main.canvas.chat.writesText = false;
			Main.canvas.chat.fadeOut();
			Main.canvas.chat.inputText = "";
		}
		
	}

	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_TAB:
			Main.canvas.displayStatFrame = false;
			break;
			case 39: 
				if(Main.mode != Main.NORMAL_MODE)return;
				Main.player2.stopTurnRight();
				break;
			case 37: 
				if(Main.mode != Main.NORMAL_MODE)return;
				Main.player2.stopTurnLeft();
				break;
			case 65:
				if(Main.mode == Main.CLIENT_MODE) Main.client.send("/stopTurnLeft");
				if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " stopTurnLeft");
				Main.player1.stopTurnLeft();
				break;
			case 68:
				if(Main.mode == Main.CLIENT_MODE) Main.client.send("/stopTurnRight");
				if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " stopTurnRight");
				Main.player1.stopTurnRight();
				break;
			case 38:
				if(Main.mode != Main.NORMAL_MODE)return; 
				Main.player2.stopUp();
				break;
			case 40:
				if(Main.mode != Main.NORMAL_MODE)return; 
				Main.player2.stopDown();
				break;
			case 83:
				if(Main.mode == Main.CLIENT_MODE) Main.client.send("/stopDown");
				if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " stopDown");
				Main.player1.stopDown();
				break;
			case 87:
				if(Main.mode == Main.CLIENT_MODE) Main.client.send("/stopUp");
				if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/action " + Main.player1.getName() + " stopUp");
				Main.player1.stopUp();
				break;
			case 122:
				Main.menubar.fullscreenMode();
				break;
			
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	Point keyPressed = null;
	@Override
	public void mousePressed(MouseEvent e) {	
		if(!Main.clock.isEnabled() && Main.mode != Main.CLIENT_MODE){
//			if(Main.debug) System.out.println("MousePressed: Button: " + e.getButton() + " Point: " + e.getPoint());
			if(e.getButton() == MouseEvent.BUTTON1){
				keyPressed = Util.frameToMapPoint(e.getPoint());
			}
			return;
		}
		if((e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) && (Main.mode == Main.SERVER_MODE || Main.mode == Main.CLIENT_MODE)){
			keyPressed = Util.frameToMapPoint(e.getPoint());
			PlayerCharacter pc = Main.getPlayer(Main.name);
			double xDif = keyPressed.x-pc.getxPos()-pc.getWidth()/2;
			double yDif = keyPressed.y-pc.getyPos()-pc.getHeight()/2;
			double distance = Math.sqrt(xDif*xDif + yDif*yDif);
			if(distance == 0) return; //BEHELFSMÄßIG; DO FIX!
			double factor = 1d * Missile.MISSILE_SPEED/distance;
			double xSpd = xDif*factor;
			double ySpd = yDif*factor;
			int attackMove = e.getButton() == MouseEvent.BUTTON1 ? PlayerCharacter.NORMAL_ATTACK : PlayerCharacter.BOMB_ATTACK;
			
//			if(Main.debug) System.out.println("shoot "+xSpd+ " " + ySpd);
			if(Main.mode == Main.SERVER_MODE){
				Main.player1.attack(xSpd, ySpd, attackMove);
				Main.server.sendToAll("/attack " + Main.name + " " + xSpd + " " + ySpd + " " + attackMove);
			}
			if(Main.mode == Main.CLIENT_MODE) Main.client.send("/attack2 " + xSpd + " " + ySpd + " " + attackMove);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(Main.clock.isEnabled() || Main.mode == Main.CLIENT_MODE) return;
//		if(Main.debug) System.out.println("MouseReleased: Button: " + e.getButton() + " Point: " + e.getPoint());

		if(e.getButton() == MouseEvent.BUTTON1){
			if(keyPressed == null) return;
			Point keyReleased = Util.frameToMapPoint(e.getPoint());
			if(keyReleased.x <= keyPressed.x || keyReleased.y <= keyPressed.y){
				keyPressed = null;
				return;
			}
			Rect r = new Rect(keyPressed.x, keyPressed.y, keyReleased.x-keyPressed.x, keyReleased.y-keyPressed.y);
			Main.newObj(""+Main.idCounter++, r.transformToString());
		}	
		
		else if(e.getButton() == MouseEvent.BUTTON3){
			if(Main.debug) System.out.println("MouseClicked: Button: " + e.getButton() + " Point: " + e.getPoint());
			
			Point clicked = Util.frameToMapPoint(e.getPoint());

			Main.deleteItemAt(clicked);
		}
	}
	
	public void testSaveInFile(){
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			File file = new File("standardSave.save");
			if(!file.exists()) file.createNewFile();
			fos = new FileOutputStream("standardSave.save");
			out = new ObjectOutputStream(fos);
			out.writeObject(Main.mapSize);
			for(GameObject go : Main.getCompList()){
				try{
				out.writeObject(go);
				}
				catch(NotSerializableException e){
					System.out.println(go);
				}
		}
			out.close();
			System.out.println("Object Persisted");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	/*
	public void testLoad(){
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream("standardSave.save");
			in = new ObjectInputStream(fis);
			Main.compList = new ArrayList<>();
			Main.mapSize = (Dimension) in.readObject();
			while(in.readObject() != null){
				Main.addCompList((GameObject)in.readObject());
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	*/
}
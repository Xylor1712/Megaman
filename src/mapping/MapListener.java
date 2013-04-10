package mapping;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.Main;

public class MapListener implements KeyListener, MouseMotionListener,
		MouseListener {
	
	private MapCreator root;
	
	private Point pressed = null;
	
	private int button = 0;
	

	
	public MapListener(MapCreator root){
		this.root = root;
	}

	
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Clicked: " + e.getPoint());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Entered: " + e.getPoint());

	}

	@Override
	public void mouseExited(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Exited: " + e.getPoint());

	}

	@Override
	public void mousePressed(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Pressed: " + e.getPoint());
		
		Point point = root.frameToMapPoint(e.getPoint());
		button = e.getButton();
		
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			pressed = point;
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Released: " + e.getPoint());
		
		Point point = root.frameToMapPoint(e.getPoint());
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			root.saveCur();
			break;
		case MouseEvent.BUTTON3:
			root.deleteItemAt(point);
			break;
		}
		
		button = 0;
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Dragged: " + e.getPoint() + " Button: " + e.getButton());
		
		Point point = root.frameToMapPoint(e.getPoint());
		switch(button){
		case MouseEvent.BUTTON1:
			root.setCurObject(pressed, point);
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//if(Main.debug) System.out.println("Mouse Moved: " + e.getPoint());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(Main.debug) System.out.println(e.getKeyCode());
		switch(e.getKeyCode()){
		case KeyEvent.VK_W:
			root.yChange = MapCreator.DECREASE;
			break;
		case KeyEvent.VK_S:
			root.yChange = MapCreator.INCREASE;
			break;
		case KeyEvent.VK_A:
			root.xChange = MapCreator.DECREASE;
			break;
		case KeyEvent.VK_D:
			root.xChange = MapCreator.INCREASE;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(Main.debug) System.out.println(e.getKeyCode());
		switch(e.getKeyCode()){
		case KeyEvent.VK_W:
		case KeyEvent.VK_S:
			root.yChange = MapCreator.UNCHANGED;
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_D:
			root.xChange = MapCreator.UNCHANGED;
			break;
			
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		//if(Main.debug) System.out.println(e.getKeyCode());

	}

}

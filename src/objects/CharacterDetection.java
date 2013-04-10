package objects;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import main.Main;

import interfaces.NeedsUpdate;

public abstract class CharacterDetection extends StaticObject implements NeedsUpdate {
	
	public static final int PULSE_MODE = 0;
	public static final int ASP_MODE = 1;
	public static final int ON_ENTER_MODE = 2;
	public static final int ON_LEAVE_MODE = 3;
	
	public static final int RECTANGLE_SHAPE = 0;
	public static final int OVAL_SHAPE = 1;
	
	public static final int DETECT_ALL = 0;
	public static final int DETECT_PLAYER = 1;
	public static final int DETECT_NPC = 2;
	
	protected Shape shape;
	
	protected double updateRate = 1;
	protected int updateCounter = 0;
	
	protected int mode = PULSE_MODE;
	
	protected int detectionShape = RECTANGLE_SHAPE;
	
	protected int detectionMode = DETECT_ALL;
	
	protected ArrayList<Character> curList = new ArrayList<>();
	

	protected CharacterDetection(int x, int y, int w, int h, int mode, int detectionShape) {
		super(x, y, w, h);
		this.mode = mode;
		changeShape(detectionShape);
	}
	
	public boolean contains(Rectangle r){
		return shape.intersects(r);
	}
	
	public void changeShape(int shape){
		switch(shape){
		case OVAL_SHAPE:
			this.shape = new Arc2D.Double(x, y, width, height, 0, 360,
			        Arc2D.OPEN);
			break;
		default:
			this.shape = this.getBounds();
		}
	}

	@Override
	public void update() {
		if(active()){
			switch(mode){
			case PULSE_MODE:
				updateCounter++;
				if(updateCounter >= updateRate * Main.actionsPerSecond){
					ArrayList<Character> list = new ArrayList<>();
					for(Character c : Main.getCharacterList()){
						switch(detectionMode){
						case DETECT_PLAYER:
							if(!(c instanceof PlayerCharacter)) continue;
							break;
						case DETECT_NPC:
							if(!(c instanceof NonPlayerCharacter)) continue;
							break;
						}
						if(contains(c.getBounds())) list.add(c);
					}
					if(list.size() > 0) event(list);
					curList = list;
					updateCounter = 0;
				}
				break;
			default:
				System.err.println("Unknown mode: " + this);
			}
		}
	}
	
	public abstract void event(ArrayList<Character> player);
	
	public abstract boolean active();

	public String transform() {
		return "PlayerDetection#"+ this.x + "#" + this.y + "#" + this.width + "#" + this.height + "#" + mode + "#" + updateRate + "#"+ updateCounter;
	}


}

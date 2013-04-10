package objects;

import interfaces.ICollision;
import interfaces.NeedsUpdate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import main.Main;

public abstract class SimpleMinion extends NonPlayerCharacter implements NeedsUpdate {
	
	private static final long serialVersionUID = -6579216687791840344L;
	
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	public static final int DETECTION_RANGE = 200;
	
	public static final double MINION_WALKSPEED = 2.5;
	public static final double MINION_JUMPSPEED = 6.5;
	
	public static final int DIRECTION_RIGHT = 0;
	public static final int DIRECTION_LEFT = 1;
	
	public static final int BASE_DAMAGE = 5;

	protected Arc2D.Double detectionShape;
	
	protected int lastDirection = DIRECTION_RIGHT;
	
	protected Point target = new Point();
	
	protected SimpleMinion(){
		this.height = HEIGHT;
		this.width = WIDTH;
		this.MAX_HEALTH = getMaxHealth();
		this.health = MAX_HEALTH;
		this.maxAttackCounter = Main.actionsPerSecond/2;
		this.detectionShape = new Arc2D.Double(xPos, yPos, 2*DETECTION_RANGE, 2*DETECTION_RANGE, 0, 360,
		        Arc2D.OPEN);
		team = -1;
	}
	

	@Override
	public void collide(int x, int y, ICollision obj){
		Rectangle bounds = obj.getBounds();
		
		//von Unten
		if(obj.inObject((int)(xPos+width/4),(int)yPos) || obj.inObject((int)xPos+3*width/4,(int) yPos) 
						&& !(obj.inObject((int)(xPos+width/4),(int)yPos+height/4) || obj.inObject((int)xPos+3*width/4,(int) yPos+height/4))){
			yPos = bounds.height;
			ySpeed = 0;
			return;
		}
		//von Oben
		if(obj.inObject((int)(xPos+width/4),(int)yPos+height) || obj.inObject((int)xPos+3*width/4,(int) yPos+height)
						&& !(obj.inObject((int)(xPos+width/4),(int)yPos) || obj.inObject((int)xPos+3*width/4,(int) yPos) )){
			yPos = bounds.y-height;
			ySpeed = 0;
			return;
		}
		
		//von der Seite
		//rechts
		if(xPos>bounds.x+bounds.width){
			xSpeed = 0;
			xPos = bounds.x+bounds.width;
		}
		//links
		if(xPos+width<bounds.x){
			xSpeed = 0;
			xPos = bounds.x-width;
		}
		
	}
	
	@Override
	public void move(Dimension bounds, ArrayList<ICollision> list) {
		
		double ySpeed = this.ySpeed;
		double xSpeed = this.xSpeed;
		
		
		if(Main.perspective == Main.WORM_PERSPECTIVE_MODE){
			this.ySpeed += Main.GRAVITATION;
		}
		ySpeed = this.ySpeed;
		
		
		if((xPos+width) > (bounds.width) || xPos < 0) xSpeed = this.xSpeed = 0;
		if((yPos+height)>= bounds.height) ySpeed = this.ySpeed = 0;
		if(yPos < 0)ySpeed = this.ySpeed = 0;
		

		double x = xPos + xSpeed;
				
		for(ICollision o : list){
			if(!this.canCollideWith(o) || o instanceof Missile)continue;
			
			if((o.inObject((int)x,(int)yPos) || o.inObject((int)x, (int)(yPos+height-0.1)))
					|| (o.inObject((int) x + width, (int)yPos) || o.inObject((int) x + width, (int) (yPos+height-0.1)))){
				x = xPos;
				if(this.jumpCounter == 0 && this.target.y < this.yPos + height){
					if(Main.perspective == Main.WORM_PERSPECTIVE_MODE){
						this.ySpeed = ySpeed = -MINION_JUMPSPEED;
						this.jumpCounter = 1;
					}
				}
			}
		}
		
		xPos = x;
		
		if(this.xPos < 0) xPos = 0;
		if(this.xPos+this.width > bounds.width) xPos = bounds.width-this.width;
		
		
		
		double y = yPos + ySpeed;
		
		for(ICollision o : list){
			if(!this.canCollideWith(o) || o instanceof Missile)continue;
			if(o.inObject((int)xPos,(int)y) || o.inObject((int)xPos+width, (int)(y))){
				y = o.getBounds().y + o.getBounds().height;
				if(Main.perspective == Main.WORM_PERSPECTIVE_MODE){
					ySpeed = Math.min(2, Math.abs(0.5*ySpeed)+1.0);
				}
				else ySpeed = 0;
				this.ySpeed = ySpeed;
			}
			else if(o.inObject((int) xPos, (int)y + height) || o.inObject((int) xPos+width,(int) y + height)){
				y = o.getBounds().y - height;
				ySpeed = 0;
				this.ySpeed = ySpeed;
				
				jumpCounter = 0;
				if((o.inObject((int) xPos, (int)y + height) && !o.inObject((int) xPos+width,(int) y + height) && xSpeed > 0) ||
						!o.inObject((int) xPos, (int)y + height) && o.inObject((int) xPos+width,(int) y + height) && xSpeed < 0	){
					if(Main.perspective == Main.WORM_PERSPECTIVE_MODE && this.jumpCounter <= 0 && y+height > target.y){
						this.ySpeed = ySpeed = -MINION_JUMPSPEED;
						y += ySpeed;
						jumpCounter = 1;
					}
				}
			}
		}
		this.yPos = y;
		if(this.yPos < 0){
			yPos = 0;
			if(Main.perspective == Main.WORM_PERSPECTIVE_MODE){
				this.ySpeed = Math.min(2, Math.abs(0.5*ySpeed)+1.0);
			}
			else this.ySpeed = 0;
		}
		if(this.yPos+this.height > bounds.height && ySpeed > 0){
			yPos = bounds.height - this.height;
			ySpeed = 0;
			this.ySpeed = ySpeed;
			jumpCounter = 0;
		}
		
		detectionShape.x = this.xPos+this.width/2 - DETECTION_RANGE;
		detectionShape.y = this.yPos+this.height/2 - DETECTION_RANGE;

	}

	@Override
	public void damaged(int dmg, GameObject o) {
		this.health -= dmg;
		if(this.health <= 0 && Main.mode != Main.CLIENT_MODE){
			died = true;
			if(o instanceof PlayerCharacter){
				((PlayerCharacter)o).increaseMinionKills();
			}
			else if(o instanceof Missile && ((Missile)o).getOwner() instanceof PlayerCharacter){
				((PlayerCharacter) ((Missile)o).getOwner()).increaseMinionKills();
			}

			computeEXP();
		}
	}

	
	@Override
	public void die() {
		this.deleteThisToken = true;
		if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/deleteObject " + this.id);
	}


	@Override
	protected void appTransform(String trans) {
		if(trans.equals("StandardValues")){
			return;
		}
		try{
			String[] t = trans.split("#");
			
			this.target.x = Integer.parseInt(t[0]);
			this.target.y = Integer.parseInt(t[1]);
			
		}
		catch(Exception e){
			System.err.println("failed to apply SimpleMeleeMinion-Tranform " + trans);
		}
	}
	
	@Override
	public String transform() {
		String trans =  target.x + "#" +
						target.y;
		trans += "_" + super.transform();
		return trans;
	}


	protected void paintHealth(Graphics g){
		if(!isAlive()) return;
		g.setColor(Color.RED);
		int barsHeight = 4;
		int healthWidth = width*this.health/(MAX_HEALTH);
		g.fillRect((int)xPos,(int)yPos-6, healthWidth, barsHeight);
	}
	

	protected int imgCountHelper = 0;

	
	protected void paintExplode(Graphics g){
		imgCountHelper++;
		if(imgCountHelper >= nextImageCounterWhen/2){
			imgCountHelper = 0;
			imgCounter++;
		}
		
		if(imgCounter >= expImg.length){
			die();
			return;
		}

		g.drawImage(expImg[imgCounter],(int)xPos,(int)yPos, null);
		
		nextImageCounterWhen = Main.getNextImageCounterWhen();
	}
	
	protected ArrayList<Character> getEnemyCharactersInRange() {
		ArrayList<Character> res = new ArrayList<>();
		for(Character c : Main.getCharacterList()){
			if(this.detectionShape.intersects(c.getBounds()) && c.isAlive() && c.team != this.team) res.add(c);
		}
		return res;
	}
	
	protected Character getNearest(ArrayList<Character> list){
		Character pc = null;
		for(Character p : list){
			if(!p.isAlive()) continue;
			if(pc == null) pc = p;
			else{
				Point p1 = pc.getCenter();
				Point p2 = p.getCenter();
				if(p1.distance(this.getCenter()) > p2.distance(this.getCenter())) pc = p;
			}
		}
		
		return pc;
	}
	protected ArrayList<Tower> towerInRange(){
		ArrayList<Tower> res = new ArrayList<>();
		for(Tower t : Main.getTowerList()){
			if(this.detectionShape.intersects(t.getBounds()) && t.isAlive() && t.team != this.team) res.add(t);
		}
		return res;
	}
	protected Tower getNearestTower(ArrayList<Tower> list){
		Tower tc = null;
		double distance = DETECTION_RANGE;
		for(Tower t : list){
			if(!t.isAlive()) continue;
			else if(tc == null){
				tc = t;
			}
			else{
				double newDis = t.getCenter().distance(this.getCenter());
				if(distance > newDis){
					tc = t;
					distance = newDis;
				}
			}
		}
		
		return tc;
	}
	
	
	@Override
	public void checkForLevelUp(){
		//doNothing
	}

}

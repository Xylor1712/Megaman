package objects;

import java.awt.Rectangle;

import main.GameRules;
import main.Main;

import interfaces.Damageable;
import interfaces.ICollision;
import interfaces.Moveable;

public abstract class Missile extends GameObject implements ICollision, Moveable, Damageable{

	protected int x;
	protected int y;
	
	protected int width;
	protected int height;
	
	protected double xSpeed;
	protected double ySpeed;
	
	public static final int MISSILE_SPEED = 8;
	
	protected GameObject owner;
	protected boolean alive = true;
	
	protected int damage;
	

	protected static int nextImageCounterWhen = Main.getNextImageCounterWhen();
	
	
	public Missile(int x, int y, double xSpeed, double ySpeed, GameObject owner, int damage){
		this.x = x;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.owner = owner;
		this.damage = damage;
		initImg();
	}
	
	public abstract void initImg();
	
	public Object getOwner(){
		return owner;
	}
	

	
	public void die(){
		deleteThisToken = true;
	}
	
	public boolean isAlive(){
		return alive;
	}
	

	
	
	protected void explode(){
		this.alive = false;
		this.xSpeed = this.ySpeed = 0;
		this.y -= 15;
	}
	
	
	
	@Override
	public boolean inObject(int x, int y) {
		return (x>=this.x && x<=this.x+this.width && y>=this.y && y<= this.y + this.height);
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
		if(canCollideWith(obj)) return;
		
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.alive = false;
		
		
	}
	
	@Override
	public boolean canCollideWith(ICollision obj){
		if(obj instanceof Character){
			Character pc = (Character)obj;
			return obj != this.owner && pc.isAlive() && 
					!(GameRules.isTeamGame() && pc.isAlive() && owner instanceof Character  && pc.team == ((Character)owner).team);
		}
		if(obj instanceof Tower){
			Tower t = (Tower)obj;
			return t.isAlive() && (owner instanceof Character  && t.team != ((Character)owner).team);
		}
		return obj != this.owner && !(obj instanceof Missile) &&  !(obj instanceof PickUpItem);
				
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x,y,width,height);
	}
	
	public static String type;
	
	public String toString(){
		return "Pos(" + x + "," + y + "), Speed("+ xSpeed + "," + ySpeed + "), Owner: " + owner.toString();
	}

	@Override
	public void damaged(int dmg, GameObject o) {
		explode();
	}

	@Override
	public int getHealth() {
		return 0;
	}

	@Override
	public void setHealth(int h) {

	}

	@Override
	public String transformToString() {
		return null;
	}
	
	@Override
	public int getMaxHealth(){
		return 0;
	}
	
	public void damageAOE(Rectangle r, int damage){
		for(ICollision o : Main.getCollisionList()){
			if(o instanceof Damageable && this.canCollideWith(o) && o.getBounds().intersects(r)) ((Damageable)o).damaged(damage, owner);
		}
	}

}

package objects;

import interfaces.*;

import java.awt.Point;
import java.awt.Rectangle;

import main.Main;

public abstract class Character extends GameObject implements ICollision, Moveable, Damageable, Resetable, NeedsSync {
	
	private static final long serialVersionUID = -4490682587406618015L;


	public static final int[] LEVEL_BREAKPOINTS = {200, 500, 1000, 1750, 2750, 4000, 5500, 7500, 10000};
	
	
	protected String name;

	
	protected double xPos = 0;
	protected double yPos = 0;
	protected int width;
	protected int height;
	
	protected int imgCounter = 0;
	protected int nextImageCounter = 0;
	
	protected int jumpCounter = 0;
	
	protected double xSpeed = 0;
	protected double ySpeed = 0;

	protected int MAX_HEALTH = 100;
	protected int MAX_SHIELD = 50;
	

	protected int health = MAX_HEALTH;
	protected int shieldAmount = 0;
	protected boolean died = false;
	protected int respawnCounter = 0;
	
	protected boolean canSpawn = true;
	
	protected int level = 1;
	protected int exp = 0;
	

	public int team = 0;
	

	public static int nextImageCounterWhen = Main.getNextImageCounterWhen();

	protected int attackCounter = 0;
	protected boolean recentlyAttacked = false;
	protected final static int MAX_ATTACK_COUNTER = 20;
	protected int maxAttackCounter = MAX_ATTACK_COUNTER;
	protected double amplifier = 1;
	protected int durationAtkMod = -1;
	protected int durationAtkCounter = 0;

	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public void expEarned(int amount){
		this.exp += amount;
		checkForLevelUp();
	}
	
	public void checkForLevelUp(){
		if(level > LEVEL_BREAKPOINTS.length) return; 
		int lvl = 1;
		for(int i = 0; i < LEVEL_BREAKPOINTS.length; i++){
			if(LEVEL_BREAKPOINTS[i] <= this.exp) lvl++;
		}
		if(lvl > level) levelup();
	}
	
	public void levelup(){
		this.level++;
		this.MAX_HEALTH *= 1.1;
		if(isAlive()) this.health += 10;
	}

	public abstract String getType();
	

	public Point getCenter(){
		return new Point((int) this.xPos+this.width/2,(int) this.yPos+this.height/2);
	}
	
	

	public double getxPos() {
		return xPos;
	}

	public void setxPos(double xPos) {
		System.out.println("xPos = " + xPos);
		this.xPos = xPos;
	}

	public double getyPos() {
		
		return yPos;
	}

	public void setyPos(double yPos) {
		this.yPos = yPos;
	}
	

	public int getWidth() {
		return width;
	}
	public int getHeight(){
		return height;
	}


	public void setDied(boolean b){
		this.died = b;
	}
	public boolean getDied(){
		return died;
	}
	
	public void setCanSpawn(boolean b){
		this.canSpawn = b;
	}
	
	public boolean getCanSpawn(){
		return canSpawn;
	}
	

	public int getHealth(){
		return health;
	}
	
	public void setHealth(int h){
		this.health = h;
	}
	
	public boolean isAlive(){
		return !died && canSpawn;
	}
	
	@Override
	public boolean canCollideWith(ICollision o) {
		if(o instanceof Character) return false;
		if(o instanceof Missile) if(((Missile)o).getOwner() == this) return false;
		if(o instanceof PickUpItem){
			if(this.getBounds().intersects(o.getBounds())) o.collide((int) xPos, (int) yPos, this);
			return false;
		}
		if(o instanceof Tower){
			Tower t = (Tower)o;
			if(t.team == this.team){
				t.testForRepairkit(this);
				return false; 
			}
		}
		return true;
	}
	
	

	public void getShield(){
		this.shieldAmount += MAX_SHIELD/2;
		if(this.shieldAmount > MAX_SHIELD) this.shieldAmount = MAX_SHIELD;
	}
	
	public boolean isShieldable(){
		return this.shieldAmount < MAX_SHIELD; 
	}


	public boolean heal(int amount){
		if(!this.isAlive() || this.health >= this.MAX_HEALTH) return false;
		
		this.health += amount;
		if(this.health >= this.MAX_HEALTH) this.health = MAX_HEALTH;
		return true;
	}

	public abstract void attack();
	
	public abstract void attack(double xSpeed, double ySpeed, int attack);
	
	

	public Rectangle getBounds(){
		return new Rectangle((int)xPos, (int)yPos,width,height);
	}
	public boolean inObject(int x, int y){
		if(health <= 0) return false;
		return (x >= xPos+width/4 && x <= (xPos+3*width/4) && y >= yPos && y <= (yPos+height));
	}
	
	
	
	@Override
	public void reset(){
		this.health = MAX_HEALTH;
		this.died = false;
		this.imgCounter = 0;
		this.nextImageCounter = 0;

		this.respawnCounter = 0;
		this.canSpawn = true;

		this.attackCounter = 0;
		this.recentlyAttacked = false;
		
		if(!Main.gameRuns){
			exp = 0;
			level = 1;
		}
	}
	
	public static Character createFromTransform(String trans){
		Character res = null;
		try{
			String[] s = trans.split("_");
			switch(s[0]){
			case "Megaman":
				res = new Megaman();
				break;
			case "Sakuyamon":
				res = new Sakuyamon();
				break;
			case "Zero":
				res = new Zero();
				break;
			case "Wizzy":
				res = new Wizzy();
				break;
			case "SimpleMeleeMinion":
				res = new SimpleMeleeMinion();
				break;
			case "SimpleRangedMinion":
				res = new SimpleRangedMinion();
				break;
			}
			if(s.length == 5) res.applyString(trans);
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Return null");
			return null;
		}
		
		return res;
	}
	
	protected abstract void appTransform(String trans);

	protected String transform(){
		String trans = "Character#" + xPos+"#"+yPos+"#"+xSpeed+"#"+ySpeed+"#"+health+"#"+
				died+"#"+imgCounter+"#"+nextImageCounter+"#"+jumpCounter+"#"+attackCounter+"#"+
				recentlyAttacked+"#"+shieldAmount+"#"+durationAtkCounter+
				"#"+durationAtkMod+	"#" + team + "#" + respawnCounter + "#" + name + "#" + level
				+ "#" + exp;
		
		return trans;
	
	}
	
	protected void applyTransform(String trans){
		String[] t;
		try{
			t = trans.split("#");
		}
		catch(Exception e){
			System.err.println("Failed to read String");
			return;
		}
		
		if(!t[0].equals("Character")){
			System.err.println("Tryed to apply wrong PlayerCharacter-Transform to " + this);
			System.err.println(trans);
			System.err.println("t[0] = \""+t[0]+"\"");
			return;
		}
		if(t.length != 20){
			System.err.println("Tryed to apply wrong Character-Transform to " + this);
			System.err.println("Differ in Length: " + t.length + " instead of " + 20);
			return;
		}
		
		try{
			this.xPos = Double.parseDouble(t[1]);
			this.yPos = Double.parseDouble(t[2]);
			this.xSpeed = Double.parseDouble(t[3]);
			this.ySpeed = Double.parseDouble(t[4]);
			this.health = Integer.parseInt(t[5]);
			this.died = Boolean.parseBoolean(t[6]);
			this.imgCounter = Integer.parseInt(t[7]);
			this.nextImageCounter = Integer.parseInt(t[8]);
			this.jumpCounter = Integer.parseInt(t[9]);
			this.attackCounter = Integer.parseInt(t[10]);
			this.recentlyAttacked = Boolean.parseBoolean(t[11]);
			this.shieldAmount = Integer.parseInt(t[12]);
			this.durationAtkCounter = Integer.parseInt(t[13]);
			this.durationAtkMod = Integer.parseInt(t[14]);
			this.team = Integer.parseInt(t[15]);
			this.respawnCounter = Integer.parseInt(t[16]);
			this.name = t[17];
			this.level = Integer.parseInt(t[18]);
			this.exp = Integer.parseInt(t[19]);
			
		}
		catch(Exception e){
			System.err.println("Failed to apply new values");
			e.printStackTrace();
		}
	}
	
	public abstract void computeEXP();
	
	

}

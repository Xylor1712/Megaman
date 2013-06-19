package objects;

import images.ImageLoader;
import interfaces.Damageable;
import interfaces.ICollision;
import interfaces.NeedsSync;
import interfaces.NeedsUpdate;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.GameRules;
import main.Main;

public class Tower extends StaticObject implements Damageable, NeedsUpdate, ICollision, NeedsSync{
	
	private static final long serialVersionUID = 7541455195165541808L;
	
	public static final int WIDTH = 80;
	public static final int HEIGHT = 100;
	
	private int MAX_HEALTH = 750;
	
	public static BufferedImage img;
	public static BufferedImage[] blue = new BufferedImage[2];
	public static BufferedImage[] red = new BufferedImage[2];
	
	
	
	
	public int team = GameRules.ENVIROMENT_TEAM;
	
	private int health = MAX_HEALTH;
	
	private CharacterDetection detection;
	
	private int attackRange = 300;
	
	private int damage = 30;
	
	private Point center = new Point(this.x + this.width/2, this.y + this.height/2);
	
	private Character target = null;

	public Tower(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
		
		initImg();
		initDetection();

	}
	
	public Tower(int x, int y, int damage, int attackRange){
		super(x, y, WIDTH, HEIGHT);
		
		this.damage = damage;
		this.attackRange = attackRange;
		
		initImg();
		initDetection();
	}
	
	private void initDetection(){
		this.detection = new CharacterDetection(
				this.x - attackRange, 
				this.y - attackRange, 
				attackRange * 2, 
				attackRange * 2,
				CharacterDetection.PULSE_MODE,
				CharacterDetection.OVAL_SHAPE){

					private static final long serialVersionUID = 514973164112497793L;

					@Override
					public void event(ArrayList<Character> player) {

						refreshHP();
						
						if(target != null && player.contains(target)){
							fireAt(target);
							return;
						}
						
						Character pc = null;
						for(Character p : player){
							if(!p.isAlive()) continue;
							if(p.team == team){
								continue;
							}
							if(pc == null) pc = p;
							else{
								Rectangle r1 = pc.getBounds();
								Rectangle r2 = p.getBounds();
								Point p1 = new Point(r1.x + r1.width/2, r1.y + r2.height/2);
								Point p2 = new Point(r2.x + r2.width/2, r2.y + r2.height/2);
								if(p1.distance(center) > p2.distance(center)) pc = p;
							}
						}
						
						if(pc != null){
							target = pc;
							fireAt(pc);
						}
					}

					@Override
					public boolean active() {
						return isAlive();
					}

					@Override
					public String transformToString() {
						return null;
					}

					@Override
					public void paint(Graphics g) {
						if(Main.debug){
							Graphics2D g2 = (Graphics2D)g;
							g2.draw(shape);
						}
					}
		};
		this.detection.detectionMode = CharacterDetection.DETECT_ALL;
	}
	
	private void initImg() {
		if(img == null){
			if(Main.debug) System.out.println("Load Image: tower.png");

			img = ImageLoader.getImage("tower.png");

			blue[0] = img.getSubimage(0, 0, WIDTH, HEIGHT);
			blue[1] = img.getSubimage(WIDTH, 0, WIDTH, HEIGHT);
			red[0] = img.getSubimage(0, HEIGHT, WIDTH, HEIGHT);
			red[1] = img.getSubimage(WIDTH, HEIGHT, WIDTH, HEIGHT);
			
		}
	}

	private void fireAt(Character pc) {
		
		Point pcCenter = pc.getCenter();
		
		double xDif = pcCenter.x-center.x;
		double yDif = pcCenter.y-center.y;
		double distance = Math.sqrt(xDif*xDif + yDif*yDif);
		if(distance == 0) return; //BEHELFSMÄßIG; DO FIX!
		double factor = 3*Missile.MISSILE_SPEED/distance/4;
		double xSpeed = xDif*factor;
		double ySpeed = yDif*factor;
		
		
		Main.addCompList(new TowerMissile(center.x, this.y, xSpeed, ySpeed,
			this, pc, getDamage()));
	}

	@Override
	public void paint(Graphics g) {
		if(img != null){
			int img = isAlive() ? 0 : 1;
			g.drawImage(team == 2 ? blue[img] : red[img], x, y, null);
			
			paintHealth(g);
		}
		if(!isAlive()){
			paintExplode(g);
		}
	}
	
	public void testForRepairkit(Character p){
		if(p instanceof PlayerCharacter){
			PlayerCharacter pchar = (PlayerCharacter)p;
			if(pchar.isAlive() 
					&& isAlive() 
					&& pchar.team == team 
					&& pchar.hasTowerRepairKit() 
					&& pchar.getBounds().intersects(getBounds()) 
					&& health < getMaxHealth()){
				
				increaseHealth(TowerRepairItem.HEAL_AMOUNT);
				pchar.setTowerRepairKit(false);
			}
		}
	}
	

	protected int imgCounter = 0;
	protected int nextImageCounter = 0;
	
	private void paintExplode(Graphics g){
		if(imgCounter >= expImg.length){
			return;
		}
		
		Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(expImg[imgCounter], center.x-height, center.y-height, 2*this.height, 2*this.height, null);
		
		

		nextImageCounter++;	
		if(nextImageCounter >= Main.getNextImageCounterWhen()*3){
			nextImageCounter = 0;
			imgCounter++;
		}
	}
	
	private void paintHealth(Graphics g){
		if(health <= 0) return;
		g.setColor(Color.RED);
		int barsHeight = 8;
		int healthWidth = width*this.health/(MAX_HEALTH);
		g.fillRect(x,y-10, (int)healthWidth, barsHeight);
	}

	@Override
	public void damaged(int dmg, GameObject o) {
		this.health -= dmg;
		if(this.health <= 0){

			GameRules.towerDestroyed(this, o);
			
			die();
			
			return;
		}
		this.refreshHP();
	}

	@Override
	public int getHealth() {
		return this.health;
	}

	@Override
	public void setHealth(int h) {
		this.health = h;
	}
	
	public void increaseHealth(int h){
		this.health += h;
		if(this.health > getMaxHealth()){
			this.health = getMaxHealth();
		}
	}

	@Override
	public int getMaxHealth() {
		double res = 1d + 0.1 * (GameRules.getNPCLevel()-1);
		
		return (int) (res * 750);
	}
	
	public void refreshHP(){
		int oldMaxHP = MAX_HEALTH;
		int newMaxHp = getMaxHealth();
		if(oldMaxHP == newMaxHp) return;
		double factor = 1d * newMaxHp / oldMaxHP; 
		this.health *= factor;
		this.MAX_HEALTH = newMaxHp;
	}

	@Override
	public void die() {
	}

	@Override
	public boolean isAlive() {
		return this.health > 0;
	}
	
	public void enraged(){
		detection.updateRate /= 2;
	}
	public void stopEnrage(){
		detection.updateRate *= 2;
	}

	public int getDamage() {
		double res = 1d + 0.1 * (GameRules.getNPCLevel()-1);
		return (int) (res * damage);
	}
	public void setDamage(int damage){
		this.damage = damage;
	}

	@Override
	public void update() {
		this.detection.update();
	}

	@Override
	public boolean inObject(int x, int y) {
		return this.getBounds().contains(x,y) && isAlive();
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
	}
	
	@Override
	public void reset(){
		this.health = MAX_HEALTH;
		this.imgCounter = 0;
		this.nextImageCounter = 0;
		this.target = null;
	}

	@Override
	public void applyString(String transform) {
		try{
			String[] s = transform.split("#");
			
			if(!s[0].equals("Tower")) throw new Exception("No SpawnPoint-Transform");
			
			if(s.length != 7) throw new Exception("Differ in Length");
			
			this.x = Integer.parseInt(s[1]);
			this.y = Integer.parseInt(s[2]);
			this.team = Integer.parseInt(s[3]);
			this.health = Integer.parseInt(s[4]);
			this.attackRange = Integer.parseInt(s[5]);
			this.damage = Integer.parseInt(s[6]);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public String transformToString() {
		String trans = "Tower#" 
				+ x + "#" 
				+ y + "#" 
				+ team + "#"
				+ health + "#" 
				+ attackRange + "#" 
				+ damage;
		return trans;
	}
	
	public static StaticObject newTowerFromString(String line) {
		try{
			String[] s = line.split("#");
			
			if(!s[0].equals("Tower")) throw new Exception("No SpawnPoint-Transform");
			
			if(s.length != 7) throw new Exception("Differ in Length");
			
			int x = Integer.parseInt(s[1]);
			int y = Integer.parseInt(s[2]);
			int damage = Integer.parseInt(s[6]);
			int attackRange = Integer.parseInt(s[5]);
			
			Tower t = new Tower(x, y, damage, attackRange);
			t.team = Integer.parseInt(s[3]);
			return t;
			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
}

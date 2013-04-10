package objects;

import images.ImageLoader;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import main.GameRules;
import main.Main;
import main.Util;

public class SimpleRangedMinion extends SimpleMinion {

	private static final long serialVersionUID = 6010283294884292736L;
	
	
	private static BufferedImage[] blue = new BufferedImage[12];
	private static BufferedImage[] red = new BufferedImage[12];
	private static BufferedImage img;
	

	private Point attackPoint = null;

	
	public SimpleRangedMinion(){
		name = "MeleeMinion";
	}
	
	public SimpleRangedMinion(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
	}
	

	public SimpleRangedMinion(int x, int y, Point target){
		this(x, y);
		this.target = target;
	}

	public SimpleRangedMinion(int x, int y, Point target, int level){
		this(x, y, target);
		this.level = level;

		this.MAX_HEALTH = getMaxHealth();
		this.health = MAX_HEALTH;
	}


	@Override
	public int getMaxHealth(){
		return (int) (40 * (1d + level * 0.05));
	}

	
	@Override
	public String getType() {
		return "SimpleRangedMinion";
	}


	@Override
	public void attack() {
		if(died || recentlyAttacked) return;
		
		attackCounter = 0;
		recentlyAttacked = true;
		
		Point center = this.getCenter();
		
		attackPoint.y += 20;
		
		double xDif = attackPoint.x -center.x;
		double yDif = attackPoint.y - center.y;
		double distance = Math.sqrt(xDif*xDif + yDif*yDif);
		if(distance == 0) return; //BEHELFSMÄßIG; DO FIX!
		double factor = 1d * MinionMissile.MISSILE_SPEED/distance;
		double xSpd = xDif*factor;
		double ySpd = yDif*factor;
		 
		
		recentlyAttacked = true;
		attackCounter = 0;
		
		int damage = getDamage();
		int w = xSpd > 0 ? 0 : 20;
		
		Main.addCompList(new MinionMissile(center.x - w , center.y-20,xSpd,ySpd, this, damage));
	}

	@Override
	public void attack(double xSpeed, double ySpeed, int attack) {
	}


	
	@Override
	public void paint(Graphics g) {
		if(isAlive()){
			int imageNumber =(int) (6d*attackCounter / maxAttackCounter) + lastDirection * 6;
			if(imageNumber >= blue.length) imageNumber = 11;
			g.drawImage(team == 2 ? blue[imageNumber] : red[imageNumber],(int)xPos,(int)yPos, null);
			if(Main.debug){
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(GameRules.getTeamColor(team, 1f));
				g2.draw(detectionShape);
			}
		}
		else{
			paintExplode(g);
		}
		paintHealth(g);
	}
	
	

	@Override
	public void initImg() {
		if(img == null){
			img = ImageLoader.getImage("RangedMinion.png");
			int imgHeight = 50;
			for(int i = 0; i < blue.length/2; i++){
				blue[i] = img.getSubimage(i*WIDTH, 0, WIDTH, imgHeight);
				blue[i+6] = Util.mirror(blue[i], Util.X_AXIS);
				red[i] = img.getSubimage(i*WIDTH, imgHeight, WIDTH, imgHeight);
				red[i+6] = Util.mirror(red[i], Util.X_AXIS);
			}

		}
	}

	@Override
	public void update() {
		if(!isAlive()) return;

		if(recentlyAttacked){
			attackCounter++;
	
			if(attackCounter >= amplifier*maxAttackCounter){
				recentlyAttacked = false;
				attackCounter = 0;
			}
		}
		
		Point goTo = this.target;
		
		Character nearest = getNearest(getEnemyCharactersInRange());
		Tower nearestT = getNearestTower(towerInRange());
		
		if(nearest != null){
			goTo = this.getCenter();
			
			if(!this.recentlyAttacked){
				attackPoint = nearest.getCenter();
				attack();
			}
		}
		else if(nearestT != null){

			goTo = this.getCenter();
			
			if(!this.recentlyAttacked){
				attackPoint = nearestT.getCenter();
				attack();
			}
		}
		
		if(Math.abs(this.getCenter().x - goTo.x) > this.width/2){
			this.xSpeed = this.getCenter().x < goTo.x ? MINION_WALKSPEED : -MINION_WALKSPEED;
			if(this.xSpeed > 0){
				lastDirection = DIRECTION_RIGHT;
			}
			else{
				lastDirection = DIRECTION_LEFT;
			}
		}
		else this.xSpeed = 0;
		if(Main.perspective == Main.BIRD_PERSPECTIVE_MODE){
			if(Math.abs(this.getCenter().y - goTo.y) > this.height/2){
				this.ySpeed = this.getCenter().x < goTo.x ? MINION_WALKSPEED : -MINION_WALKSPEED;
			}
			else this.ySpeed = 0;
		}
	}

	
	
	public int getDamage(){
		double res = 1d + 0.07 * (level-1);
		return (int) (res * BASE_DAMAGE);
	}


	@Override
	public String transformToString() {
		return "SimpleRangedMinion_" + super.transform();
	}
	
	public BufferedImage getMissileImg(){
		int y = team == 2 ? 30 : 80;
		return img.getSubimage(300, y, 20, 20);
	}

}

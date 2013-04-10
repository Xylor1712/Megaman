package objects;

import images.ImageLoader;
import interfaces.NeedsUpdate;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GameRules;
import main.Main;
import main.Util;

public class SimpleMeleeMinion extends SimpleMinion implements NeedsUpdate{

	private static final long serialVersionUID = -6040883154843033367L;
	
	private static BufferedImage[] blue = new BufferedImage[12];
	private static BufferedImage[] red = new BufferedImage[12];
	private static BufferedImage img;
	

	
	public SimpleMeleeMinion(){
		name = "MeleeMinion";
	}
	
	public SimpleMeleeMinion(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
	}
	

	public SimpleMeleeMinion(int x, int y, Point target){
		this(x, y);
		this.target = target;
	}

	public SimpleMeleeMinion(int x, int y, Point target, int level){
		this(x, y, target);
		this.level = level;

		this.MAX_HEALTH = getMaxHealth();
		this.health = MAX_HEALTH;
	}


	@Override
	public int getMaxHealth(){
		return (int) (50 * (1d + level * 0.07));
	}

	
	@Override
	public String getType() {
		return "SimpleMeleeMinion";
	}

	
	@Override
	public void attack() {
		if(died || recentlyAttacked) return;
		
		attackCounter = 0;
		recentlyAttacked = true;
		
		Rectangle attackArea = this.getBounds();
		attackArea.x += lastDirection == DIRECTION_RIGHT ? 2*MINION_WALKSPEED : -2 * MINION_WALKSPEED;
		
		for(Character c : Main.getCharacterList()){
			if(c.team != this.team && c.isAlive() && c.getBounds().intersects(attackArea)){
				c.damaged(getDamage(), this);
			}
		}
		for(Tower t : Main.getTowerList()){
			if(t.team != this.team && t.isAlive() && t.getBounds().intersects(attackArea)){
				t.damaged(getDamage(), this);
			}
		}
	}

	@Override
	public void attack(double xSpeed, double ySpeed, int attack) {
	}


	
	@Override
	public void paint(Graphics g) {
		if(isAlive()){
			int imageNumber =(int) (6d*attackCounter / maxAttackCounter) + lastDirection * 6;
			if(imageNumber >= blue.length) imageNumber = 11;
			g.drawImage(team == 2 ? blue[imageNumber] : red[imageNumber],(int)xPos,(int)yPos-10, null);
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
			img = ImageLoader.getImage("Minion.png");
			int imgHeight = 60;
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
		
		Rectangle attackArea = this.getBounds();
		attackArea.x += lastDirection == DIRECTION_RIGHT ? 2*MINION_WALKSPEED : -2* MINION_WALKSPEED;
		
		if(nearest != null){
			goTo = nearest.getCenter();
			
			if(!this.recentlyAttacked && nearest.getBounds().intersects(attackArea)) attack();
		}
		if(nearestT != null){
			double dis1 = this.getCenter().distance(goTo);
			double dis2 = this.getCenter().distance(nearestT.getCenter());
			if(dis1 > dis2) goTo = nearestT.getCenter();
			
			if(!this.recentlyAttacked && nearestT.getBounds().intersects(attackArea)) attack();
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
		return "SimpleMeleeMinion_" + super.transform();
	}
}

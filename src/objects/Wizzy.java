package objects;

import images.ImageLoader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Main;
import main.Util;

public class Wizzy extends PlayerCharacter {
	
	private static final long serialVersionUID = 1044067900669906115L;
	
	public static final int WIDTH = 50;
	public static final int HEIGHT = 75;
	
	
	private static BufferedImage all;
	private static BufferedImage portrait;
	private static BufferedImage[] walkRight = new BufferedImage[16];
	private static BufferedImage[] walkLeft = new BufferedImage[walkRight.length];
	private static BufferedImage[] jumpRight = new BufferedImage[8];
	private static BufferedImage[] jumpLeft = new BufferedImage[jumpRight.length];
	private static BufferedImage[] attackRight = new BufferedImage[8];
	private static BufferedImage[] attackLeft = new BufferedImage[walkRight.length];
	
	public Wizzy(){
		width = WIDTH;
		height = HEIGHT;
		initImages();
		name = this.getType();
	}
	
	public Wizzy(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
	}

	private void initImages() {
		if(all == null){
			all = ImageLoader.getImage("Wizzy.png");
			
			if(all == null) return;
			int picSize = 75;
			for(int i = 0; i < walkRight.length; i++){
				int x = (i % 4) * picSize;
				int y = (i / 4) * picSize;
				walkRight[i] = all.getSubimage(x, y, picSize, picSize);
				walkLeft[i] = Util.mirror(walkRight[i], Util.X_AXIS);
				
				if(i < attackRight.length){
					y += 300;
					attackRight[i] = all.getSubimage(x, y, picSize, picSize);
					attackLeft[i] = Util.mirror(attackRight[i], Util.X_AXIS);
					
					jumpRight[i] = all.getSubimage(x, 450 + 85 * (i/4), picSize, 85);
					jumpLeft[i] = Util.mirror(jumpRight[i], Util.X_AXIS);
				}
			}
		}
		if(portrait == null){
			portrait = ImageLoader.getImage("wizzyPortrait.png");
		}
	}

	@Override
	protected void appTransform(String trans) {
		
	}

	@Override
	public String transformToString() {
		return "Wizzy_" + super.transform();
	}

	@Override
	protected boolean paintSpecial(Graphics g) {
		if(!recentlyAttacked) return false;
		
		int imgNr = (attackRight.length*attackCounter / maxAttackCounter);
		if(imgNr >= attackRight.length) imgNr = attackRight.length - 1;
		if(lastDirection == CharacterMode.right){
			g.drawImage(attackRight[imgNr],(int) xPos,(int) yPos, null);
		}
		else{
			g.drawImage(attackLeft[imgNr],(int) xPos,(int) yPos, null);
		}
		return true;
	}

	@Override
	protected void paintStand(Graphics g) {
		int xPos = (int)this.xPos  + (this.lastDirection == CharacterMode.left ? -20 : 0);
		int yPos = (int)this.yPos;

		imgCounter = 0;
		
		if(lastDirection == CharacterMode.right){
			g.drawImage(attackRight[7], xPos,yPos, null);
		}
		else{
			g.drawImage(attackLeft[7], xPos,yPos, null);
		}
	}

	@Override
	protected void paintWalk(Graphics g) {
		int xPos = (int)this.xPos  + (this.lastDirection == CharacterMode.left ? -20 : 0);
		int yPos = (int)this.yPos;

		if(lastDirection == CharacterMode.right){
			nextImageCounter++;
			if(nextImageCounter >= nextImageCounterWhen){
				imgCounter++;
				nextImageCounter = 0;
			}
			if(imgCounter >= walkRight.length) imgCounter = 2;
			g.drawImage(walkRight[imgCounter], xPos, yPos, Main.canvas);
		}
		else if(lastDirection == CharacterMode.left){
			nextImageCounter++;
			if(nextImageCounter >= nextImageCounterWhen){
				imgCounter++;
				nextImageCounter = 0;
			}
			if(imgCounter >= walkLeft.length) imgCounter = 2;
			g.drawImage(walkLeft[imgCounter], xPos, yPos, Main.canvas);
		}
	}

	@Override
	protected void paintJump(Graphics g) {
		int xPos = (int)this.xPos  + (this.lastDirection == CharacterMode.left ? -20 : 0);
		int yPos = (int)this.yPos;
		
		nextImageCounter++;
		if(nextImageCounter >= nextImageCounterWhen){
			imgCounter++;
			nextImageCounter = 0;
		}
		if(imgCounter >= 5) imgCounter = 4;
		
		if(lastDirection == CharacterMode.right){

			g.drawImage(jumpRight[imgCounter], xPos,yPos-10,Main.canvas);
		}
		else if(lastDirection == CharacterMode.left){

			g.drawImage(jumpLeft[imgCounter], xPos,yPos-10,Main.canvas);
		}
	}

	@Override
	protected void paintFall(Graphics g){
		int xPos = (int)this.xPos  + (this.lastDirection == CharacterMode.left ? -20 : 0);
		int yPos = (int)this.yPos;
		imgCounter = 5;
		if(lastDirection == CharacterMode.right) g.drawImage(jumpRight[imgCounter], xPos,yPos-10,Main.canvas);
		else if(lastDirection == CharacterMode.left) g.drawImage(jumpLeft[imgCounter], xPos,yPos-10, Main.canvas);
	}

	@Override
	protected void paintLand(Graphics g) {
		int xPos = (int)this.xPos  + (this.lastDirection == CharacterMode.left ? -20 : 0);
		int yPos = (int)this.yPos;
		
		nextImageCounter++;		
		if(nextImageCounter >= nextImageCounterWhen){
			imgCounter++;
			nextImageCounter = 0;
		}
		
		if(imgCounter >= jumpRight.length){
			imgCounter = 0;
			if(xSpeed == 0){
				mode = CharacterMode.stand;
				paintStand(g);
			}
			else{
				mode = CharacterMode.walk;
				paintWalk(g);
			}
			return;
		}

		if(lastDirection == CharacterMode.right){
			g.drawImage(jumpRight[imgCounter], xPos,yPos-10,Main.canvas);
		}
		else if(lastDirection == CharacterMode.left){
			g.drawImage(jumpLeft[imgCounter], xPos,yPos-10,Main.canvas);
		}
	}

	@Override
	public String getType() {
		return "Wizzy";
	}

	@Override
	public void attack(){
		if(died) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		
		if(recentlyAttacked) return;
		recentlyAttacked = true;
		attackCounter = 0;
	
		int missileSpeed = WizzyMissile.MISSILE_SPEED;
		int diagonalWizzyMissileSpeed = 6;
		
		
		int xSpd = 0;
		int ySpd = 0;
		int x = (int)this.xPos + width/2;
		int y = (int)this.yPos+height/2-WizzyMissile.HEIGHT;
		
		switch(this.lastDirection){
		case right:
			switch(this.vDirection){
			case normal:
				xSpd = missileSpeed;
				ySpd = 0;
				break;
			case up:
				xSpd = diagonalWizzyMissileSpeed;
				ySpd = -diagonalWizzyMissileSpeed;
				break;
			case down:
				xSpd = diagonalWizzyMissileSpeed;
				ySpd = diagonalWizzyMissileSpeed;
				break;
			default:
			}
			break;
		case left:
			x -= WizzyMissile.WIDTH;
			switch(this.vDirection){
			case normal:
				xSpd = -missileSpeed;
				ySpd = 0;
				break;
			case up:
				xSpd = -diagonalWizzyMissileSpeed;
				ySpd = -diagonalWizzyMissileSpeed;
				break;
			case down:
				xSpd = -diagonalWizzyMissileSpeed;
				ySpd = diagonalWizzyMissileSpeed;
				break;
			default:
			}
			break;
		default:
		}
		Main.addCompList(new WizzyMissile(x, y, xSpd, ySpd, this, getDamage(NORMAL_ATTACK)));
	}

	
	@Override
	public void attack(double xSpd, double ySpd, int attack){
		if(died || recentlyAttacked || (attack == BOMB_ATTACK && bombAmmo <= 0)) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		int w = 0;
		int h = 0;
		
		if(attack == NORMAL_ATTACK){
			w = WizzyMissile.WIDTH;
			h = WizzyMissile.HEIGHT;
		}
		if(attack == BOMB_ATTACK){
			w = BombMissile.WIDTH;
			h = BombMissile.HEIGHT;
		}
		
		
		
		double x = this.xPos + this.width/2 + (attack == NORMAL_ATTACK ? 0 : ((this.width/2) / Missile.MISSILE_SPEED)*xSpd * 3/2);
		double y = (int)this.yPos+height/2-h + (attack == NORMAL_ATTACK ? 0 : ((this.height/2) / Missile.MISSILE_SPEED)*ySpd * 2); 
		if(xSpd < 0) x -= w;
		
		recentlyAttacked = true;
		attackCounter = 0;
		
		int damage = getDamage(attack);
		
		if(attack == NORMAL_ATTACK) Main.addCompList(new WizzyMissile((int)x,(int)y,xSpd,ySpd,this, damage));
		if(attack == BOMB_ATTACK){
			Main.addCompList(new BombMissile((int)x,(int)y,xSpd,ySpd,this, damage));
			bombAmmo--;
		}
	}
	
	public int getDamage(int attack){
		double res = 1d + 0.1 * (level-1);
		switch(attack){
		case NORMAL_ATTACK:
			res *= WizzyMissile.BASE_DAMAGE;
			break;
		case BOMB_ATTACK:
			res += BombMissile.BASE_DAMAGE;
			break;
		}
		return (int) res;
	}
	
	public BufferedImage getPortrait(){
		return portrait;
	}

}

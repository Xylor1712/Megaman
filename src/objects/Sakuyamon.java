package objects;

import images.ImageLoader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Main;
import main.Util;

public class Sakuyamon extends PlayerCharacter {
	

	private static final long serialVersionUID = 3751142030390616692L;
	
	public static final int WIDTH = 35;
	public static final int HEIGHT = 60;
	
	public static final int walkWidth = 50;
	public static final int jumpWidth = 35;
	
	
	
	private static BufferedImage all;
	private static BufferedImage portrait;
	private static BufferedImage[] walkRight = new BufferedImage[8];
	private static BufferedImage[] walkLeft = new BufferedImage[walkRight.length];
	private static BufferedImage[] jumpRight = new BufferedImage[12];
	private static BufferedImage[] jumpLeft = new BufferedImage[jumpRight.length];
	
	public Sakuyamon(){
		width = WIDTH;
		height = HEIGHT;
		initImages();
		name = "Sakuyamon";
	}
	
	public void initImages(){
		if(Main.debug) System.out.println("Init Sakuyamon-Images");
		
		
		initAllImg();
		initPortrait();
		initWalkRightImg();
		initWalkLeftImg();
		initJumpRightImg();
		initJumpLeftImg();
		
	}
	
	public void initAllImg(){
		if(all == null){

			all = ImageLoader.getImage("sakuyamon.png");

		}
	}
	
	private void initPortrait(){
		if(portrait == null) {
			portrait = ImageLoader.getImage("sakuyamonPortrait.png");
		}
	}
	
	public void initWalkRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkRight-Images");
			
			for(int i = 0; i<walkRight.length; i++){
				if(walkRight[i] == null) walkRight[i] = all.getSubimage(i*(walkWidth+5),64,walkWidth, height);
			}
		}
	}
	public void initWalkLeftImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkLeft-Images");
			
			for(int i = 0; i<walkLeft.length; i++){
				if(walkLeft[i] == null)walkLeft[i] = Util.mirror(walkRight[i], Util.X_AXIS);
			}
		}
	}
	
	public void initJumpRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init JumpRight-Images");
			
			for(int i = 0; i<6; i++){
				if(jumpRight[i] == null)jumpRight[i] = all.getSubimage(i*jumpWidth,128,jumpWidth, height);
			}
			for(int i = 0; i<6; i++){
				if(jumpRight[i+6] == null)jumpRight[i+6] = all.getSubimage(225 + i*45, 128, 45, height);
			}
		}
	}
	public void initJumpLeftImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init JumpLeft-Images");
			
			for(int i = 0; i<jumpLeft.length; i++){
				if(jumpLeft[i] == null)jumpLeft[i] = Util.mirror(jumpRight[i], Util.X_AXIS);
			}
		}
	}
	
	public Sakuyamon(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
	}

	@Override
	public void attack() {
		if(died) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		
		if(recentlyAttacked) return;
		recentlyAttacked = true;
		attackCounter = 0;
	
		int missileSpeed = MegamanMissile.MISSILE_SPEED;
		int diagonalMegamanMissileSpeed = 6;
		
		
		int xSpd = 0;
		int ySpd = 0;
		int x = 0;
		int y = (int)this.yPos+height/2-MegamanMissile.HEIGHT;
		
		switch(this.lastDirection){
		case right:
			x = (int) this.xPos + width;
			switch(this.vDirection){
			case normal:
				xSpd = missileSpeed;
				ySpd = 0;
				break;
			case up:
				xSpd = diagonalMegamanMissileSpeed;
				ySpd = -diagonalMegamanMissileSpeed;
				break;
			case down:
				xSpd = diagonalMegamanMissileSpeed;
				ySpd = diagonalMegamanMissileSpeed;
				break;
			default:
			}
			break;
		case left:
			x = (int) this.xPos - MegamanMissile.WIDTH;
			switch(this.vDirection){
			case normal:
				xSpd = -missileSpeed;
				ySpd = 0;
				break;
			case up:
				xSpd = -diagonalMegamanMissileSpeed;
				ySpd = -diagonalMegamanMissileSpeed;
				break;
			case down:
				xSpd = -diagonalMegamanMissileSpeed;
				ySpd = diagonalMegamanMissileSpeed;
				break;
			default:
			}
			break;
		default:
		}
		Main.addCompList(new SakuyamonMissile(x, y, xSpd, ySpd, this, getDamage(NORMAL_ATTACK)));
	}

	@Override
	public void attack(double xSpd, double ySpd, int attack) {
		if(died || recentlyAttacked || (attack == BOMB_ATTACK && bombAmmo <= 0)) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		int w = 0;
		int h = 0;
		
		if(attack == NORMAL_ATTACK){
			w = SakuyamonMissile.WIDTH;
			h = SakuyamonMissile.HEIGHT;
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
		
		if(attack == NORMAL_ATTACK) Main.addCompList(new SakuyamonMissile((int)x,(int)y,xSpd,ySpd,this, damage));
		if(attack == BOMB_ATTACK){
			Main.addCompList(new BombMissile((int)x,(int)y,xSpd,ySpd,this, damage));
			bombAmmo--;
		}
	}
	
	public void appTransform(String t){
		
	}
	
	@Override
	public String transformToString() {
		String res = "";
		res = "Sakuyamon_" + super.transform();
		return res;
	}

	@Override
	protected void paintStand(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
			default:
				if(lastDirection == CharacterMode.right){
					g.drawImage(jumpRight[0], xPos,yPos, null);
				}
				else{
					g.drawImage(jumpLeft[0], xPos,yPos, null);
				}
				break;
		}
	}

	@Override
	protected void paintWalk(Graphics g) {

		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
			default:
				if(lastDirection == CharacterMode.right){
					xPos -= walkWidth-jumpWidth;
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
				break;
		}
	}

	@Override
	protected void paintJump(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
		default:
				if(lastDirection == CharacterMode.right){
					nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= 9) imgCounter = 8;
					g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
				}
				else if(lastDirection == CharacterMode.left){
						nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= 9) imgCounter = 8;
					g.drawImage(jumpLeft[imgCounter], xPos,yPos,Main.canvas);
				}
				break;
		}
	}

	@Override
	protected void paintFall(Graphics g) {
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		imgCounter = 9;
		switch(combat){
		default:
			if(lastDirection == CharacterMode.right) g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
			else if(lastDirection == CharacterMode.left) g.drawImage(jumpLeft[imgCounter], xPos,yPos, Main.canvas);
			break;
		}
	}

	@Override
	protected void paintLand(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
		default:	
			if(lastDirection == CharacterMode.right){
				nextImageCounter++;		
				g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
				if(nextImageCounter >= nextImageCounterWhen/2){
					imgCounter++;
					nextImageCounter = 0;
				}
				if(imgCounter >= jumpRight.length){
					imgCounter = 0;
					mode = xSpeed == 0 ? CharacterMode.stand : CharacterMode.walk;
				}
			}
			else if(lastDirection == CharacterMode.left){
				nextImageCounter++;		
				g.drawImage(jumpLeft[imgCounter], xPos,yPos,Main.canvas);
				if(nextImageCounter >= nextImageCounterWhen/2){
					imgCounter++;
					nextImageCounter = 0;
				}		
				if(imgCounter >= jumpLeft.length){
					imgCounter = 0;
					mode = xSpeed == 0 ? CharacterMode.stand : CharacterMode.walk;
				}
			}
			break;
		}
	}
	
	protected boolean paintSpecial(Graphics g){
		return false;
	}
	
	public String getType(){
		return "Sakuyamon";
	}
	
	public int getDamage(int attack){
		double res = 1d + 0.1 * (level-1);
		switch(attack){
		case NORMAL_ATTACK:
			res *= SakuyamonMissile.BASE_DAMAGE;
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

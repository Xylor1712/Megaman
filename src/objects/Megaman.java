package objects;

import main.*;

import images.ImageLoader;

import java.awt.image.BufferedImage;
import java.awt.*;


public class Megaman extends PlayerCharacter {


	public static final int WIDTH = 50;
	public static final int HEIGHT = 55;
	
	
	private static BufferedImage all;
	private static BufferedImage portrait;
	private static BufferedImage[] walkRight = new BufferedImage[16];
	private static BufferedImage[] walkLeft = new BufferedImage[walkRight.length];
	private static BufferedImage[] jumpRight = new BufferedImage[19];
	private static BufferedImage[] jumpLeft = new BufferedImage[jumpRight.length];
	private static BufferedImage[] walkFightRight = new BufferedImage[16];
	private static BufferedImage[] walkFightLeft = new BufferedImage[walkFightRight.length];
	private static BufferedImage[] jumpFightRight = new BufferedImage[19];
	private static BufferedImage[] jumpFightLeft = new BufferedImage[jumpFightRight.length];
	
	
	public Megaman(){
		width = WIDTH;
		height = HEIGHT;
		initImages();
		name = "Megaman";
	}
	
	public Megaman(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
	}
	
	
	public void initImages(){
		if(Main.debug) System.out.println("Init Megaman-Images");
		
		
		initAllImg();
		initPortrait();
		initWalkRightImg();
		initWalkLeftImg();
		initJumpRightImg();
		initJumpLeftImg();
		initFightWalkRightImg();
		initFightWalkLeftImg();
		initFightJumpRightImg();
		initFightJumpLeftImg();
		
	}
	
	public void initAllImg(){
		if(all == null){
				all = ImageLoader.getImage("megamanNeu.png");
		}
		
	}
	
	public void initPortrait(){
		if(portrait == null){
			portrait = ImageLoader.getImage("megamanPortrait.png");
		}
	}
	public BufferedImage getPortrait(){
		return portrait;
	}
	
		
	public void initWalkRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkRight-Images");
			
			for(int i = 0; i<walkRight.length; i++){
				walkRight[i] = all.getSubimage(i*width,168,width, height);
			}
		}
	}
	public void initWalkLeftImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkLeft-Images");
			
			for(int i = 0; i<walkLeft.length; i++){
				walkLeft[i] = Util.mirror(walkRight[i], Util.X_AXIS);
			}
		}
	}
	
	public void initJumpRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init JumpRight-Images");
			
			for(int i = 0; i<jumpRight.length; i++){
				jumpRight[i] = all.getSubimage(i*42,330,38, height);
			}
		}
	}
	public void initJumpLeftImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init JumpLeft-Images");
			
			for(int i = 0; i<jumpLeft.length; i++){
				jumpLeft[i] = Util.mirror(jumpRight[i], Util.X_AXIS);
			}
		}
	}
	
	public void initFightWalkRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkFightRight-Images");
			
			for(int i = 0; i<walkFightRight.length; i++){
				 walkFightRight[i] = all.getSubimage(i*width,225,width, height);
			}
		}
	}
	public void initFightWalkLeftImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkFightLeft-Images");
			
			for(int i = 0; i<walkFightLeft.length; i++){
				walkFightLeft[i] = Util.mirror(walkFightRight[i], Util.X_AXIS);
			}
		}
	}
	
	public void initFightJumpRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init JumpFightRight-Images");
			
			for(int i = 0; i<jumpFightRight.length; i++){
				jumpFightRight[i] = all.getSubimage((int)(i*42),397,37, height);
			}
		}
	}
	public void initFightJumpLeftImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init JumpFightLeft-Images");
			
			for(int i = 0; i<jumpFightLeft.length; i++){
				jumpFightLeft[i] = Util.mirror(jumpFightRight[i], Util.X_AXIS);
			}
		}
	}

	
	
	public void attack(){
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
		Main.addCompList(new MegamanMissile(x, y, xSpd, ySpd, this, getDamage(NORMAL_ATTACK)));
	}
	
	public void attack(double xSpd, double ySpd, int attack){
		if(died || recentlyAttacked || (attack == BOMB_ATTACK && bombAmmo <= 0)) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		int w = 0;
		int h = 0;
		
		if(attack == NORMAL_ATTACK){
			w = MegamanMissile.WIDTH;
			h = MegamanMissile.HEIGHT;
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
		
		if(attack == NORMAL_ATTACK) Main.addCompList(new MegamanMissile((int)x,(int)y,xSpd,ySpd,this, damage));
		if(attack == BOMB_ATTACK){
			Main.addCompList(new BombMissile((int)x,(int)y,xSpd,ySpd,this, damage));
			bombAmmo--;
		}
	}
	
	protected void paintStand(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
			case normal:
				if(lastDirection == CharacterMode.right){
					g.drawImage(walkRight[0], xPos,yPos, null);
				}
				else{
					g.drawImage(walkLeft[0], xPos,yPos, null);
				}
				break;
			case fight:
				if(lastDirection == CharacterMode.right){
					g.drawImage(walkFightRight[0], xPos,yPos, null);
				}
				else{
					g.drawImage(walkFightLeft[0], xPos,yPos, null);
				}
				break;
			default:
		}
	}
	
	protected void paintWalk(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
			case normal:
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
				break;
			case fight:
				if(lastDirection == CharacterMode.right){
					nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= walkFightRight.length) imgCounter = 2;
					g.drawImage(walkFightRight[imgCounter], xPos, yPos, Main.canvas);
				}
				else if(lastDirection == CharacterMode.left){
					nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= walkFightLeft.length) imgCounter = 2;
					g.drawImage(walkFightLeft[imgCounter], xPos, yPos, Main.canvas);
				}
				break;
			default:
		}
	}
	
	protected void paintJump(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
		case normal:
				if(lastDirection == CharacterMode.right){
					nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= 8) imgCounter = 7;
					g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
				}
				else if(lastDirection == CharacterMode.left){
						nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= 8) imgCounter = 7;
					g.drawImage(jumpLeft[imgCounter], xPos,yPos,Main.canvas);
				}
				break;
		case fight:
			if(lastDirection == CharacterMode.right){
				nextImageCounter++;
				if(nextImageCounter >= nextImageCounterWhen){
					imgCounter++;
					nextImageCounter = 0;
				}
				if(imgCounter >= 8) imgCounter = 7;
				g.drawImage(jumpFightRight[imgCounter], xPos,yPos,Main.canvas);
			}
			else if(lastDirection == CharacterMode.left){
					nextImageCounter++;
				if(nextImageCounter >= nextImageCounterWhen){
					imgCounter++;
					nextImageCounter = 0;
				}
				if(imgCounter >= 8) imgCounter = 7;
				g.drawImage(jumpFightLeft[imgCounter], xPos,yPos,Main.canvas);
			}
			break;
		default:
		}
	}
	
	protected void paintFall(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		imgCounter = 8;
		switch(combat){
		case normal:
			if(lastDirection == CharacterMode.right) g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
			else if(lastDirection == CharacterMode.left) g.drawImage(jumpLeft[imgCounter], xPos,yPos, Main.canvas);
			break;
		case fight:
			if(lastDirection == CharacterMode.right) g.drawImage(jumpFightRight[imgCounter], xPos,yPos,Main.canvas);
			else if(lastDirection == CharacterMode.left) g.drawImage(jumpFightLeft[imgCounter], xPos,yPos, Main.canvas);
			break;
		default:
		}
	}
	
	protected void paintLand(Graphics g){
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		switch(combat){
		case normal:	
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
		case fight:
			if(lastDirection == CharacterMode.right){
				nextImageCounter++;		
				g.drawImage(jumpFightRight[imgCounter], xPos,yPos,Main.canvas);
				if(nextImageCounter >= nextImageCounterWhen/2){
					imgCounter++;
					nextImageCounter = 0;
				}		
				if(imgCounter >= jumpFightRight.length){
					imgCounter = 0;
					mode = xSpeed == 0 ? CharacterMode.stand : CharacterMode.walk;
				}
			}
			else if(lastDirection == CharacterMode.left){
				nextImageCounter++;		
				g.drawImage(jumpFightLeft[imgCounter], xPos,yPos,Main.canvas);
				if(nextImageCounter >= nextImageCounterWhen/2){
					imgCounter++;
					nextImageCounter = 0;
				}		
				if(imgCounter >= jumpFightLeft.length){
					imgCounter = 0;
					mode = xSpeed == 0 ? CharacterMode.stand : CharacterMode.walk;
				}
			}
			break;
		default:
		}
	}
	
	protected boolean paintSpecial(Graphics g){
		return false;
	}


	@Override
	public String transformToString() {
		return "Megaman_" + super.transform();
	}
	
	public void appTransform(String t){
		
	}
	
	public String getType(){
		return "Megaman";
	}
	
	public int getDamage(int attack){
		double res = 1d + 0.1 * (level-1);
		switch(attack){
		case NORMAL_ATTACK:
			res *= MegamanMissile.BASE_DAMAGE;
			break;
		case BOMB_ATTACK:
			res += BombMissile.BASE_DAMAGE;
			break;
		}
		return (int) res;
	}

			
}
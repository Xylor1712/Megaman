package objects;

import images.ImageLoader;
import interfaces.Damageable;
import interfaces.NeedsUpdate;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.GameRules;
import main.Main;
import main.Util;

public class Zero extends PlayerCharacter implements NeedsUpdate{
	

	private static final long serialVersionUID = -7838884012244264708L;
	
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	
	public static final int DASH_NODASH = 0;
	public static final int DASH_ATTACK1 = 1;
	public static final int DASH_ATTACK2 = 2;
	
	public static final double DASH_SPEED_CONSTANT = 13;
	public static final int DASH_ATTACKCOUNTER_AMPLIFIER = 2;
	public static final int DASH_DAMAGE = 15;
	
	private int dash_mode = DASH_NODASH;
	
	
	private static BufferedImage all;
	private static BufferedImage portrait;
	private static BufferedImage[] walkRight = new BufferedImage[16];
	private static BufferedImage[] walkLeft = new BufferedImage[walkRight.length];
	private static BufferedImage[] jumpRight = new BufferedImage[15];
	private static BufferedImage[] jumpLeft = new BufferedImage[jumpRight.length];
	private static BufferedImage[] attack1Right = new BufferedImage[8];
	private static BufferedImage[] attack1Left = new BufferedImage[attack1Right.length];
	
	private ArrayList<GameObject> alreadyHit = new ArrayList<>();
	
	
	public Zero(){
		width = WIDTH;
		height = HEIGHT;
		initImages();
		name = "Megaman";
	}
	
	public void initImages(){
		if(Main.debug) System.out.println("Init Zero-Images");
		
		
		initAllImg();
		initPortrait();
		initWalkRightImg();
		initWalkLeftImg();
		initJumpRightImg();
		initJumpLeftImg();
		initAttackImages();
		
	}
	
	public void initAllImg(){
		if(all == null){
			all = ImageLoader.getImage("zero.png");
		}
	}
	
	private void initPortrait(){
		if(portrait == null){ 
			portrait = ImageLoader.getImage("zeroPortrait.png");
		}
	}
	
	public BufferedImage getPortrait(){
		return portrait;
	}
	
		
	public void initWalkRightImg(){
		if(all != null){
			if(Main.debug) System.out.println("Init WalkRight-Images");
			
			walkRight[0] = all.getSubimage(0, 50, 40, 50);
			walkRight[1] = all.getSubimage(40, 50, 40, 50);
			walkRight[2] = all.getSubimage(80, 50, 50, 50);
			walkRight[3] = all.getSubimage(130, 50, 49, 50);
			walkRight[4] = all.getSubimage(179, 50, 43, 50);
			walkRight[5] = all.getSubimage(222, 50, 50, 50);
			walkRight[6] = all.getSubimage(271, 50, 53, 50);
			walkRight[7] = all.getSubimage(323, 50, 50, 50);
			walkRight[8] = all.getSubimage(0, 100, 55, 50);
			walkRight[9] = all.getSubimage(55, 100, 50, 50);
			walkRight[10] = all.getSubimage(105, 100, 45, 50);
			walkRight[11] = all.getSubimage(150, 100, 45, 50);
			walkRight[12] = all.getSubimage(195, 100, 50, 50);
			walkRight[13] = all.getSubimage(245, 100, 53, 50);
			walkRight[14] = all.getSubimage(298, 100, 51, 50);
			walkRight[15] = all.getSubimage(350, 100, 52, 50);
			
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
			
			jumpRight[0] = all.getSubimage(0, 330, 42, 50);
			jumpRight[1] = all.getSubimage(42, 320, 45, 60);
			jumpRight[2] = all.getSubimage(88, 320, 45, 60);
			jumpRight[3] = all.getSubimage(133, 320, 45, 60);
			jumpRight[4] = all.getSubimage(179, 320, 47, 60);
			jumpRight[5] = all.getSubimage(228, 320, 50, 60);
			jumpRight[6] = all.getSubimage(278, 310, 50, 70);
			jumpRight[7] = all.getSubimage(0, 380, 37, 80);
			jumpRight[8] = all.getSubimage(38, 380, 37, 80);
			jumpRight[9] = all.getSubimage(77, 380, 37, 80);
			jumpRight[10] = all.getSubimage(117, 380, 37, 80);
			jumpRight[11] = all.getSubimage(154, 400, 42, 60);
			jumpRight[12] = all.getSubimage(197, 410, 45, 50);
			jumpRight[13] = all.getSubimage(243, 410, 45, 50);
			jumpRight[14] = all.getSubimage(291, 410, 45, 50);
			
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
	
	public void initAttackImages(){
		initAttack1Right();
		initAttack1Left();
	}
	
	public void initAttack1Right(){
		if(all != null){
			if(Main.debug) System.out.println("Init attack1Right-Images");
			
			attack1Right[0] = all.getSubimage(0, 150, 50, 50);
			attack1Right[1] = all.getSubimage(50, 150, 50, 50);
			attack1Right[2] = all.getSubimage(105, 150, 80,50);
			attack1Right[3] = all.getSubimage(185, 150, 80,50);
			attack1Right[4] = all.getSubimage(264, 150, 80,50);
			attack1Right[5] = all.getSubimage(342, 150, 55,50);
			attack1Right[6] = all.getSubimage(400, 150, 50,50);
			attack1Right[7] = all.getSubimage(451, 150, 50,50);
			
		}
	}
	
	public void initAttack1Left(){
		if(all != null){
			if(Main.debug) System.out.println("Init attack1Left-Images");
			
			for(int i = 0; i<attack1Left.length; i++){
				attack1Left[i] = Util.mirror(attack1Right[i], Util.X_AXIS);
			}
		}
	}

	public Zero(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
	}

	@Override
	public void attack() {
		if(died) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		
		if(recentlyAttacked) return;
		
		startDashing();
		
		double diagonalCons = 3*DASH_SPEED_CONSTANT/4;
		
		specialXSpeed = DASH_SPEED_CONSTANT;
		specialYSpeed = DASH_SPEED_CONSTANT;
		
		switch(this.lastDirection){
		case right:
			switch(this.vDirection){
			case normal:
				specialXSpeed = DASH_SPEED_CONSTANT;
				specialYSpeed = 0;
				break;
			case up:
				specialXSpeed = diagonalCons;
				specialYSpeed = -diagonalCons;
				break;
			case down:
				specialXSpeed = diagonalCons;
				specialYSpeed = diagonalCons;
				break;
			default:
			}
			break;
		case left:
			switch(this.vDirection){
			case normal:
				specialXSpeed = -DASH_SPEED_CONSTANT;
				specialYSpeed = 0;
				break;
			case up:
				specialXSpeed = -diagonalCons;
				specialYSpeed = -diagonalCons;
				break;
			case down:
				specialXSpeed = -diagonalCons;
				specialYSpeed = diagonalCons;
				break;
			default:
			}
			break;
		default:
		}
	}

	@Override
	public void attack(double xSpeed, double ySpeed, int attack) {
		if(died || recentlyAttacked || (attack == BOMB_ATTACK && bombAmmo <= 0)) return;
		if(combat == CharacterMode.normal)combat = CharacterMode.fight;
		
		if(attack == NORMAL_ATTACK){
			
			if(specialMove) return;

			startDashing();
			
			double factor = DASH_SPEED_CONSTANT / Missile.MISSILE_SPEED;
			
			if(xSpeed < 0) lastDirection = CharacterMode.left;
			else lastDirection = CharacterMode.right;
			
			specialXSpeed = xSpeed * factor;
			specialYSpeed = ySpeed * factor;
			
		}
		if(attack == BOMB_ATTACK){
			amplifier = 1;
			int w = BombMissile.WIDTH;
			int h = BombMissile.HEIGHT;
			double x = this.xPos + this.width/2 + ((this.width/2) / Missile.MISSILE_SPEED)*xSpeed * 3/2;
			double y = (int)this.yPos+height/2-h + ((this.height/2) / Missile.MISSILE_SPEED)*ySpeed * 2; 
			if(xSpeed < 0) x -= w;

			Main.addCompList(new BombMissile((int)x,(int)y,xSpeed,ySpeed,this, getDamage(attack)));
			bombAmmo--;
		}

		recentlyAttacked = true;
		attackCounter = 0;
		
	}
	
	public void appTransform(String trans){
		if(trans.equals("StandardValues")){
			this.dash_mode = DASH_NODASH;
			return;
		}
		try{
			//t = trans.split("#");
			this.dash_mode = Integer.parseInt(trans);
		}
		catch(Exception e){
			System.err.println("failed to apply Zero-Tranform " + trans);
		}
	}

	@Override
	public String transformToString() {
		return "Zero_" + dash_mode + super.transform();
	}
	
	

	@Override
	protected void paintStand(Graphics g) {
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		imgCounter = 0;
		switch(combat){
			default:
				if(lastDirection == CharacterMode.right){
					g.drawImage(walkRight[0], xPos,yPos, null);
				}
				else{
					g.drawImage(walkLeft[0], xPos,yPos, null);
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
	protected void paintJump(Graphics g) {
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
					if(imgCounter >= 5) imgCounter = 4;
					g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
				}
				else if(lastDirection == CharacterMode.left){
						nextImageCounter++;
					if(nextImageCounter >= nextImageCounterWhen){
						imgCounter++;
						nextImageCounter = 0;
					}
					if(imgCounter >= 5) imgCounter = 4;
					g.drawImage(jumpLeft[imgCounter], xPos,yPos,Main.canvas);
				}
				break;
		}
	}

	@Override
	protected void paintFall(Graphics g) {
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos-30;
		if(imgCounter < 4 || imgCounter > 9) imgCounter = 7;
		switch(combat){
		default:
			if(lastDirection == CharacterMode.right){
				nextImageCounter++;
				if(nextImageCounter >= 2*nextImageCounterWhen){
					imgCounter++;
					nextImageCounter = 0;
				}
				g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
			}
			else if(lastDirection == CharacterMode.left){
					nextImageCounter++;
				if(nextImageCounter >= 2*nextImageCounterWhen){
					imgCounter++;
					nextImageCounter = 0;
				}
				g.drawImage(jumpLeft[imgCounter], xPos,yPos,Main.canvas);
			}
			break;
		}

	}

	@Override
	protected void paintLand(Graphics g) {
		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		if(imgCounter < 10) imgCounter = 10;
		if(imgCounter >= jumpRight.length) imgCounter = jumpRight.length -1;
		switch(imgCounter){
		case 10:
			yPos -= 30;
			break;
		case 11:
			yPos -= 10;
		}
		switch(combat){
		default:	
			if(lastDirection == CharacterMode.right){
				nextImageCounter++;		
				g.drawImage(jumpRight[imgCounter], xPos,yPos,Main.canvas);
				if(nextImageCounter >= nextImageCounterWhen){
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
				
				if(nextImageCounter >= nextImageCounterWhen){
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
		
		if(this.dash_mode == DASH_NODASH) return false;
		
		//painting Dash/Attack-Animation

		int xPos = (int)this.xPos;
		int yPos = (int)this.yPos;
		
		if(imgCounter > 2 && imgCounter < 5 && lastDirection == CharacterMode.left){
			xPos -= 30;
		}
		else if(imgCounter == 5 && lastDirection == CharacterMode.left) xPos -= 5;
		
		switch(dash_mode){
		default:
			if(lastDirection == CharacterMode.right){
				try{
					g.drawImage(attack1Right[imgCounter], xPos,yPos,Main.canvas);
				}
				catch(IndexOutOfBoundsException e){
					stopDashing();
					return false;
				}
			}
			else if(lastDirection == CharacterMode.left){
				try{
					g.drawImage(attack1Left[imgCounter], xPos,yPos,Main.canvas);
				}
				catch(IndexOutOfBoundsException e){
					stopDashing();
					return false;
				}
			}
			break;
		}
		
		if(imgCounter >= attack1Right.length) stopDashing();
		
		return true;
	}
	
	private void startDashing(){
		
		if(this.yPos >= Main.mapSize.height - this.height) this.yPos = Main.mapSize.height - this.height - 0.25;
		
		imgCounter = 0;
		dash_mode = DASH_ATTACK1;
		specialMove = true;
		recentlyAttacked = true;
		attackCounter = -1000;
		amplifier = DASH_ATTACKCOUNTER_AMPLIFIER;
	}
	
	private void stopDashing(){
		imgCounter = 0;
		dash_mode = DASH_NODASH;
		specialMove = false;
		recentlyAttacked = true;
		attackCounter = 0;
		alreadyHit = new ArrayList<>();
	}
	
	

	@Override
	public String getType() {
		return "Zero";
	}

	@Override
	public void update() {
		if(dash_mode != DASH_NODASH){
			nextImageCounter++;
			if(nextImageCounter >= Main.actionsPerSecond / 5d / 8){
				imgCounter++;
				nextImageCounter = 0;
			}
		}
	}
	
	@Override
	protected void specialMoveEffect(){
		if(!specialMove) return;
		Rectangle damageBounds = this.getBounds();
		damageBounds.x += (int)this.specialXSpeed;
		damageBounds.y += (int)this.specialYSpeed;
//		System.out.println(specialXSpeed + " " + specialYSpeed);
		for(GameObject g : Main.getCompList()){
			if(g instanceof Damageable && g != this && g.getBounds().intersects(damageBounds) && !alreadyHit.contains(g)){
				alreadyHit.add(g);
				if(GameRules.isTeamGame()){
					if(g instanceof Character){
						if(((Character)g).team == this.team) continue;
					}
					if(g instanceof Tower){
						if(((Tower)g).team == this.team) continue;
					}
				}
				int damage = getDamage(NORMAL_ATTACK);
				((Damageable)g).damaged(damage, this);
				if(!(g instanceof Tower)) this.heal(damage/6);
			}
		}
	}
	
	
	public int getDamage(int attack){
		double res = 1d + 0.1 * (level-1);
		switch(attack){
		case NORMAL_ATTACK:
			res *= DASH_DAMAGE;
			break;
		case BOMB_ATTACK:
			res += BombMissile.BASE_DAMAGE;
			break;
		}
		return (int) res;
	}
	
//	public void damaged(int dmg, GameObject o){
//		int damage = dmg;
//		if(dash_mode == DASH_ATTACK1){
//			damage /= 2;
//		}
//		super.damaged(damage, o);
//	}

}

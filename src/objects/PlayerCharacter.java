package objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import main.GameRules;
import main.Main;
import main.Util;
import interfaces.*;

public abstract class PlayerCharacter extends Character{
	
	
	protected int wins = 0;
	protected int kills = 0;
	protected int deaths = 0;
	protected int ping = 0;
	protected int towerKills = 0;
	protected int minionKills = 0;
	protected int flagsCaptured = 0;

	
	public static final int NORMAL_ATTACK = 1;
	public static final int BOMB_ATTACK = 2;
	
	protected int bombAmmo = 0;
	public static final int MAX_BOMBAMMO = 10;

	public static final int DURATION_RAPIDFIRE = 15; //secs
	
	
	protected CharacterMode mode = CharacterMode.stand;
	protected CharacterMode lastDirection = CharacterMode.right;
	protected CharacterMode combat = CharacterMode.normal;
	protected CharacterMode vDirection = CharacterMode.normal;
	
	protected double specialXSpeed = 0;
	protected double specialYSpeed = 0;
	protected boolean specialMove = false;
	
	protected boolean hasTowerRepairKit = false;
	
	protected PlayerCharacter(){
		super();
	}
	
	
	public void increaseBombAmmo(int amount){
		bombAmmo += amount;
		if(bombAmmo > MAX_BOMBAMMO) bombAmmo = MAX_BOMBAMMO;
	}
	
	public int getBombAmmo(){
		return bombAmmo;
	}
	
	public boolean atMaxAmmo(){
		return bombAmmo >= MAX_BOMBAMMO;
	}
		
	public int getFlagsCaptured(){
		return flagsCaptured;
	}
	public void setFlagsCaptured(int s){
		flagsCaptured = s;
	}
	public void increaseFlagsCaptured(){
		flagsCaptured++;
	}
	public int getMinionKills(){
		return minionKills;
	}
	public void setMinionKills(int s){
		minionKills = s;
	}
	public void increaseMinionKills(){
		minionKills++;
	}
	public int getTowerKills(){
		return towerKills;
	}
	public void setTowerKills(int s){
		towerKills = s;
	}
	public void increaseTowerKills(){
		towerKills++;
	}


	public void reset(){
		this.jumpCounter = 0;
		this.mode = CharacterMode.stand;
		this.lastDirection = CharacterMode.right;
		this.combat = CharacterMode.normal;
		this.vDirection = CharacterMode.normal;
		this.bombAmmo = 0;
		this.shieldAmount = 0;
		this.turnRapidFireOff();
		this.specialMove = false;
		super.reset();
		GameRules.statReset(this);
	}

	public void applyString(String transform){
		String[] t;
		try{
			t = transform.split("_");
		}
		catch(Exception e){
			System.err.println("Failed to read String");
			return;
		}
		
		if(!t[0].equals(this.getType())){
			System.err.println("Tryed to apply wrong PlayerCharacter-Transform to " + this);
			System.err.println("t[0] = \""+t[0]+"\"");
			return;
		}
		if(t.length != 5){
			System.err.println("Tryed to apply wrong PlayerCharacter-Transform to " + this);
			System.err.println(transform);
			System.err.println("Differ in Length: " + t.length + " instead of 5");
			return;
		}
		super.applyTransform(t[4]);
		
		applyTransform(t[2]);
		
		appTransform(t[1]);
		
		GameRules.applyStatistics(this, t[3]);
		
	}
	
	protected abstract void appTransform(String trans);

	protected String transform(){
		String trans = "_PlayerCharacter#"
				+mode+"#"
				+lastDirection+"#"
				+vDirection+"#"
				+combat+"#"
				+ping+"#"
				+bombAmmo+"#"
				+specialMove+ "#" 
				+specialXSpeed + "#" 
				+ specialYSpeed + "#"
				+ hasTowerRepairKit;
		
		trans += "_" + GameRules.transformStatistics(this);
		
		trans += "_" + super.transform();
		
		return trans;
	
	}
	
	public abstract String transformToString();
	
	protected void applyTransform(String trans){
		String[] t;
		try{
			t = trans.split("#");
		}
		catch(Exception e){
			System.err.println("Failed to read String");
			return;
		}
		
		if(!t[0].equals("PlayerCharacter")){
			System.err.println("Tryed to apply wrong PlayerCharacter-Transform to " + this);
			System.err.println(trans);
			System.err.println("t[0] = \""+t[0]+"\"");
			return;
		}
		if(t.length != 11){
			System.err.println("Tryed to apply wrong PlayerCharacter-Transform to " + this);
			System.err.println("Differ in Length: " + t.length + " instead of 10");
			return;
		}
		
		try{
			this.mode = CharacterMode.valueOf(t[1]);
			this.lastDirection = CharacterMode.valueOf(t[2]);
			this.vDirection = CharacterMode.valueOf(t[3]);
			this.combat = CharacterMode.valueOf(t[4]);
			this.ping = Integer.parseInt(t[5]);
			this.bombAmmo = Integer.parseInt(t[6]);
			this.specialMove = Boolean.parseBoolean(t[7]);
			this.specialXSpeed = Double.parseDouble(t[8]);
			this.specialYSpeed = Double.parseDouble(t[9]);
			this.hasTowerRepairKit = Boolean.parseBoolean(t[10]);
		}
		catch(Exception e){
			System.err.println("Failed to apply new values");
			e.printStackTrace();
		}
	}
	
	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public void increaseWins(){
		this.wins++;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}
	
	public void increaseKills(){
		this.kills++;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
	public void increaseDeaths(){
		this.deaths++;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}
	public void setTowerRepairKit(boolean b){
		this.hasTowerRepairKit = b;
	}
	public boolean hasTowerRepairKit(){
		return hasTowerRepairKit;
	}
	
	
	public boolean canCollideWith(ICollision o){
		boolean res = super.canCollideWith(o);
		
		if(o instanceof TransparentRect){
			if(this.ySpeed < 0 || this.vDirection == CharacterMode.down) return false;;
		}

		return res;
	}
	
	public int getMaxHealth(){
		return MAX_HEALTH;
	}
	
	
	
	public void move(Dimension bounds, ArrayList<ICollision> objects){
		if(!canSpawn) return;
		if(died){
			if(GameRules.respawnTime() != GameRules.NO_RESPAWN){
				respawnCounter++;
				if(respawnCounter > GameRules.respawnTime() * Main.actionsPerSecond  && Main.mode != Main.CLIENT_MODE){
					GameRules.respawn(this);
				}
			}
			return;
		}
		
		attackCounter++;
		if(isRapidFireMode()){
			durationAtkCounter++;
			if(durationAtkCounter > durationAtkMod){
				turnRapidFireOff();
			}
		}

		if(attackCounter >= amplifier*maxAttackCounter){
			recentlyAttacked = false;
			//attackCounter = 0;
		}
		if(attackCounter >= 300) combat = CharacterMode.normal;
		
		
		double ySpeed = this.ySpeed;
		double xSpeed = this.xSpeed;
		
		if(specialMove){
			ySpeed = this.specialYSpeed;
			xSpeed = this.specialXSpeed;
			specialMoveEffect();
		}
		else{
			if(Main.perspective == Main.WORM_PERSPECTIVE_MODE){
				this.ySpeed += Main.GRAVITATION;
			}
			
			ySpeed = this.ySpeed;
		}
		
		if((xPos+width) > (bounds.width) || xPos < 0) xSpeed = this.xSpeed = 0;
//		if((yPos+height)>= bounds.height) ySpeed = this.ySpeed = 0;
		if(yPos < 0)ySpeed = this.ySpeed = 0;
		

		double x = xPos + xSpeed;
				
		for(ICollision o : objects){
			if(!this.canCollideWith(o) || o instanceof Missile)continue;
			
			if((o.inObject((int)x,(int)yPos) || o.inObject((int)x, (int)(yPos+height-0.1)))
					|| (o.inObject((int) x + width, (int)yPos) || o.inObject((int) x + width, (int) (yPos+height-0.1)))){
				x = xPos;
				//xSpeed = 0;
			}
		}
		
		xPos = x;
		
		if(this.xPos < 0) xPos = 0;
		if(this.xPos+this.width > bounds.width) xPos = bounds.width-this.width;
		
		
		
		double y = yPos + ySpeed;
		
		for(ICollision o : objects){
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
		
		
		if(this.ySpeed == 0){
			if(mode == CharacterMode.fall || mode == CharacterMode.jump){
				mode = CharacterMode.land;
			}
			
		}
		if(this.ySpeed > 0){
			mode = CharacterMode.fall;
		}
		if(this.ySpeed < 0){
			mode = CharacterMode.jump;
		}
	}
	
	protected void specialMoveEffect() {
	}

	
	public void jump(){
		if(Main.perspective == Main.WORM_PERSPECTIVE_MODE){
			jumpCounter++;
			if(jumpCounter > 2) return;
			this.ySpeed = -10;
			if(this.yPos + this.height >= Main.mapSize.height-1) yPos = Main.mapSize.height - this.height -2;
		}
	}
	public void turnRight(){
		this.xSpeed = 5;
		lastDirection = CharacterMode.right;
		if(mode == CharacterMode.stand) mode = CharacterMode.walk;
	}
	public void turnLeft(){
		this.xSpeed = -5;
		lastDirection = CharacterMode.left;
		if(mode == CharacterMode.stand) mode = CharacterMode.walk;
	}
	public void stopTurnRight(){
		if(xSpeed == 5){
			xSpeed = 0;
		}
		if(mode == CharacterMode.walk && lastDirection == CharacterMode.right) mode = CharacterMode.stand;
	}
	public void stopTurnLeft(){
		if(xSpeed == -5){
			xSpeed = 0;
		}
		if(mode == CharacterMode.walk && lastDirection == CharacterMode.left) mode = CharacterMode.stand;
	}
	
	public void up(){
		this.vDirection = CharacterMode.up;
		if(Main.perspective == Main.BIRD_PERSPECTIVE_MODE){
			ySpeed = -5;
			if(this.yPos + this.height >= Main.mapSize.height) yPos = Main.mapSize.height - this.height -1;
		}
	}
	public void stopUp(){
		if(this.vDirection == CharacterMode.up) this.vDirection = CharacterMode.normal;
		if(Main.perspective == Main.BIRD_PERSPECTIVE_MODE){
			if(this.ySpeed == -5){
				ySpeed = 0;
			}
		}
	}
	public void down(){
		this.vDirection = CharacterMode.down;
		if(Main.perspective == Main.BIRD_PERSPECTIVE_MODE){
			ySpeed = 5;
		}
	}
	public void stopDown(){
		if(this.vDirection == CharacterMode.down)this.vDirection = CharacterMode.normal; 
		if(Main.perspective == Main.BIRD_PERSPECTIVE_MODE){
			if(this.ySpeed == 5){
				ySpeed = 0;
			}
		}
	}
	
	public void collide(int x, int y, ICollision obj){
		Rectangle bounds = obj.getBounds();
		
		//von Unten
		if(obj.inObject((int)(xPos+width/4),(int)yPos) || obj.inObject((int)xPos+3*width/4,(int) yPos) 
						&& !(obj.inObject((int)(xPos+width/4),(int)yPos+height/4) || obj.inObject((int)xPos+3*width/4,(int) yPos+height/4))){
			yPos = bounds.height;
			ySpeed = 0;
			mode = CharacterMode.fall;
			return;
		}
		//von Oben
		if(obj.inObject((int)(xPos+width/4),(int)yPos+height) || obj.inObject((int)xPos+3*width/4,(int) yPos+height)
						&& !(obj.inObject((int)(xPos+width/4),(int)yPos) || obj.inObject((int)xPos+3*width/4,(int) yPos) )){
			yPos = bounds.y-height;
			ySpeed = 0;
			mode = CharacterMode.land;
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
	
	
	//Damegable
	public void damaged(int dmg, GameObject o){
		if(died) return;
		PlayerCharacter pc = null;
		if(o instanceof Missile){
			if (((Missile)o).getOwner() == this && !(o instanceof BombMissile)) return;
			
			Object owner = ((Missile)o).getOwner();
			if(owner instanceof PlayerCharacter && !(o instanceof BombMissile)) pc = (PlayerCharacter)owner;
		}
		if(o instanceof PlayerCharacter) pc = (PlayerCharacter)o;
		if(pc!= null && GameRules.isTeamGame() && pc.team == this.team)return; 
		
		int remainingDamage = dmg;
		if(shieldAmount > 0){
			shieldAmount -= dmg;
			remainingDamage = -shieldAmount;
		}
		
		if(remainingDamage > 0) health -= remainingDamage;

		if(health <= 0){
			GameRules.killed(pc, this);
			imgCounter = 0;
			nextImageCounter = 0;
			computeEXP();
		}
	}
	
	public void die(){
		System.out.println("You died");
		this.deleteThisToken = true;
	}
	
	@Override
	public void paint(Graphics g){
		if(!canSpawn) return;
		if(died){
			paintExplode(g);
			return;
		}
		
		nextImageCounterWhen = Main.getNextImageCounterWhen();
		if(!paintSpecial(g)){
			switch(mode){
			case stand:
				paintStand(g);
				break;
				
			case walk:
				paintWalk(g);
				break;
				
			case jump:
				paintJump(g);
				break;

			case fall:
				paintFall(g);
				break;
				
			case land:
				paintLand(g);
				break;
			
			
			default:
			break;
			}
		}

		
		paintNameAndHealth(g);
	}

	protected abstract boolean paintSpecial(Graphics g);

	protected abstract void paintStand(Graphics g);
	protected abstract void paintWalk(Graphics g);
	protected abstract void paintJump(Graphics g);
	protected abstract void paintFall(Graphics g);
	protected abstract void paintLand(Graphics g);
	
	private void paintExplode(Graphics g){
		if(imgCounter >= expImg.length){
			return;
		}
		
		g.drawImage(expImg[imgCounter], (int)xPos, (int)yPos, null);
		

		nextImageCounter++;	
		if(nextImageCounter >= nextImageCounterWhen*3){
			nextImageCounter = 0;
			imgCounter++;
		}
	}
	
	private void paintNameAndHealth(Graphics g){
		int width = 50;
		if(name != null && name != ""){
			  
			g.setColor(GameRules.getTeamColor(team, 1f));

			g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
			
		    FontMetrics fm = g.getFontMetrics();
		    Font f = g.getFont();
		    g.setFont(f.deriveFont(Font.BOLD));

		    Rectangle2D textsize = fm.getStringBounds(name, g);
		    int xPos = (int)(this.xPos + width/2 - textsize.getWidth()/2);
		    int yPos = (int)(this.yPos - textsize.getHeight());
		    g.drawString(name, xPos, yPos < textsize.getHeight() ? (int)textsize.getHeight() : yPos);
		}
		//healthbar:
		int barsHeight = 6;
		if(shieldAmount > 0){
			g.setColor(Color.yellow);
			barsHeight /= 2;
			int shieldWidth = width*this.shieldAmount/(MAX_SHIELD);
			g.fillRect((int)xPos,(int) yPos-barsHeight, (int)shieldWidth, barsHeight);
		}
		g.setColor(Color.RED);
		int healthWidth = width*this.health/(MAX_HEALTH);
		g.fillRect((int)xPos,(int) yPos-6, (int)healthWidth, barsHeight);
		

		g.setColor(Color.WHITE);
	}
	
	boolean rapidfiremode = false;
	
	public void turnRapidFireOn(){
		rapidfiremode = true;
		this.maxAttackCounter /= 2;
		this.durationAtkMod = DURATION_RAPIDFIRE * Main.actionsPerSecond;
		this.durationAtkCounter = 0;

		if(Main.debug)System.out.println("TurnRapidFireOn (AtkModDuration = " + durationAtkMod + "): " + this); 
	}
	
	public void turnRapidFireOff(){
		this.maxAttackCounter = MAX_ATTACK_COUNTER;
		rapidfiremode = false;
		if(Main.debug)System.out.println("TurnRapidFireOff (AtkModDuration = " + durationAtkMod + "): " + this); 
	}
	
	public boolean isRapidFireMode(){
		return rapidfiremode;
	}
	
	
	public void paintBuffInfos(Graphics g, Rectangle bounds){
		int x = -bounds.x + 5;
		int y = -bounds.y + 5;
		
		//ExpBar:
//		if(GameRules.playerCanLevel() && this.level < LEVEL_BREAKPOINTS.length && level != 0) {
//			int lastLevel = (level <= 1 ? 0 : LEVEL_BREAKPOINTS[level-2]);
//			int nextLevel = (level >= 10 ? LEVEL_BREAKPOINTS[8] : LEVEL_BREAKPOINTS[level-1]);
//			
//			int diff = nextLevel - lastLevel;
//			int earned = this.exp - lastLevel;
//			
//			if(earned > 0 && diff > 0){
//				g.setColor(Color.GREEN);
//				
//				g.fillRect(-bounds.x, -bounds.y + bounds.height -20,(int) (1d * bounds.width * earned / diff), 20);
//			}
//
//			g.setColor(Color.black);
//			Util.drawStringCenteredAt(g, this.exp + "/" + nextLevel, -bounds.x + bounds.width/2, -bounds.y + bounds.height -10);
//			
//		}
		
		if(hasTowerRepairKit){
			g.drawImage(TowerRepairItem.imgFull, x, y, null);
			x += 5 + TowerRepairItem.WIDTH;
		}
		
		
		if(bombAmmo > 0){
			g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, BombItem.HEIGHT/2));
			g.drawImage(BombItem.imgFull, x, y, null);
			int newX = x+BombItem.WIDTH/2;
			int newY = y+BombItem.HEIGHT/2;
			g.setColor(Color.WHITE);
			Util.drawStringCenteredAt(g, ""+bombAmmo, newX, newY);
			x += 5 + BombItem.WIDTH;
		}
		
		if(isRapidFireMode()){
			g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, RapidFireItem.HEIGHT/2));
			g.drawImage(RapidFireItem.imgFull, x, y, null);
			int newX = x+RapidFireItem.WIDTH/2;
			int newY = y+RapidFireItem.HEIGHT/2;
			g.setColor(Color.WHITE);
			double remainingSecs = DURATION_RAPIDFIRE * (1 - 1.0d * durationAtkCounter/durationAtkMod); 
			DecimalFormat f = new DecimalFormat("#0.0"); 
			Util.drawStringCenteredAt(g, f.format(remainingSecs), newX, newY);
			x += 5;
			y += 5;
		}
		
		if(this.died && GameRules.respawnTime() != GameRules.NO_RESPAWN){
			int secsRemaining = GameRules.respawnTime() - respawnCounter / Main.actionsPerSecond;
			Main.canvas.setInfoMessage("Respawn in " + secsRemaining + " secs...");
		}
	}
	
	
	public String toString(){
		return name + "(" + getType() + "): " + transformToString();
	}
	
	public void computeEXP(){
		if(!GameRules.playerCanLevel()) return;
		int expForAll = this.level * 10 + 180;
		
		for(Character c : Main.getCharacterList()){
			if(c.isAlive() && c.getCenter().distance(this.getCenter()) <= 300 && c != this && c.team != this.team){
				c.expEarned(expForAll);
			}
		}
	}
	
	public abstract BufferedImage getPortrait();
	
	
}

package objects;

import interfaces.Damageable;
import interfaces.ICollision;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;

public class MinionMissile extends Missile {
	
	private static final long serialVersionUID = -1601301641847889045L;
	
	public static int WIDTH = 16;
	public static int HEIGHT = 16;
	
	private BufferedImage img;
	private int imgCounter = 0;
	private int imgCountHelper = -2;
	
	public static final int BASE_DAMAGE = 4;
	
	
	public static String type = "MinionMissile";
	
	private int lifetime = 0;
	private static final double MAX_LIFETIME = 0.4;
	

	public MinionMissile(int x, int y, double xSpeed, double ySpeed,
			SimpleRangedMinion owner, int damage) {
		super(x, y, xSpeed, ySpeed, owner, damage);
		this.width = WIDTH;
		this.height = HEIGHT;
	}

	@Override
	public void move(Dimension g, ArrayList<ICollision> list) {
		if(!alive) return;
		this.x += xSpeed;
		this.y += ySpeed;
		
		this.lifetime++;
		if(this.lifetime > MAX_LIFETIME * Main.actionsPerSecond){
			this.explode();
			return;
		}
		
		for(ICollision o : list){
			if(o.inObject(x, y) || o.inObject(x + width, y) || o.inObject(x,y+height) || o.inObject(x+width, y+height)){
				//Missiles des gleichen Spielers treffen sich nicht gegenseitig, von anderen Spielern jedoch sehr wohl
				if(!canCollideWith(o)) continue;
				
				explode();
				
				if(o instanceof Damageable){
					((Damageable)o).damaged(damage, owner);
				}
				
			}
		}
		if(this.y+height >= g.height || this.y <= 0|| this.x+this.width>=g.width || this.x<= 0) explode();
	}

	@Override
	public void initImg() {
		img = ((SimpleRangedMinion)owner).getMissileImg();
	}

	@Override
	public void paint(Graphics g) {

		if(alive){
			g.drawImage(img,x,y,null);
		}
		else{
			imgCountHelper++;
			if(imgCountHelper >= nextImageCounterWhen / 2){
				imgCountHelper = 0;
				imgCounter++;
			}
			if(imgCounter >= expImg.length){
				die();
				return;
			}
			g.drawImage(expImg[imgCounter], x, y, null);
			
			nextImageCounterWhen = Main.getNextImageCounterWhen();
		}
	}

}

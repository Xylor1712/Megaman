package objects;

import images.ImageLoader;
import interfaces.Damageable;
import interfaces.ICollision;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;
import main.Util;

public class MegamanMissile extends Missile {
	
	public static final int BASE_DAMAGE = 10;
	
	public static int WIDTH = 42;
	public static int HEIGHT = 12;
	
	private static final double MAX_LIFETIME = 1;
	private int lifetime = 0;
	
	private static BufferedImage missile;
	private BufferedImage img;
	private int imgCounter = 0;
	private int imgCountHelper = -2;
	
	protected static int damage = 10;
	

	public static String type = "MegamanMissile";
	
	
	
	public MegamanMissile(int x, int y, double xSpeed, double ySpeed,
			GameObject owner, int damage) {
		super(x, y, xSpeed, ySpeed, owner, damage);
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	public void initImg(){
		if(missile == null){
			missile = ImageLoader.getImage("missile.png");
		}
		
		if(this.xSpeed > 0){
			this.img = missile;
		}
		else{
			this.img = Util.mirror(missile, Util.X_AXIS);
		}
	}

	
	@Override
	public void paint(Graphics g) {
		if(alive){
			g.drawImage(img,x,y,null);
		}
		else{
			imgCountHelper++;
			if(imgCountHelper >= nextImageCounterWhen/2){
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
	
	
	public String toString(){
		return "MegamanMissile: " + super.toString();
	}
	
}

package objects;

import images.ImageLoader;
import interfaces.ICollision;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;

public class WizzyMissile extends Missile {
	

	private static final double MAX_LIFETIME = 0.66;
	private int lifetime = 0;
	
	public static int WIDTH = 25;
	public static int HEIGHT = 25;
	
	private static BufferedImage missile;
	
	private int imgCounter = 0;
	private int imgCountHelper = -2;
	
	public static final int BASE_DAMAGE = 13;
	

	public static String type = "WizzyMissile";

	public WizzyMissile(int x, int y, double xSpeed, double ySpeed,
			GameObject owner, int damage) {
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
			if(o.getBounds().intersects(this.getBounds())){
				//Missiles des gleichen Spielers treffen sich nicht gegenseitig, von anderen Spielern jedoch sehr wohl
				if(!canCollideWith(o)) continue;
				
				explode();
				
				damageAOE(getDamageBounds(), damage);
				
			}
		}
		if(this.y+height >= g.height || this.y <= 0|| this.x+this.width>=g.width || this.x<= 0) explode();
	}

	@Override
	public void initImg() {
		if(missile == null){
			missile = ImageLoader.getImage("WizzyMissile.png");
		}
	}

	@Override
	public void paint(Graphics g) {

		if(alive){
			g.drawImage(missile,x,y,null);
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
	
	public String toString(){
		return "WizzyMissile: " + super.toString();
	}
	
	private Rectangle getDamageBounds(){
		Rectangle res = this.getBounds();
		res.x -= res.width/2;
		res.y += res.height/2;
		res.width *= 2;
		res.height *= 2;
		return res;
	}

}

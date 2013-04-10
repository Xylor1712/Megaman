package objects;

import images.ImageLoader;
import interfaces.ICollision;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;
import main.Util;

public class BombMissile extends Missile{
	
	private static final long serialVersionUID = 6515657362076348665L;
	
	
	public static int WIDTH = 16;
	public static int HEIGHT = 16;
	
	private static BufferedImage bomb;
	private BufferedImage img;
	private int imgCounter = 0;
	private int imgCountHelper = -2;
	
	public static final int BASE_DAMAGE = 20;
	
	
	public static String type = "BombMissile";
	
	
	public BombMissile(int x, int y, double xSpeed, double ySpeed,
			GameObject owner, int damage) {
		super(x, y, xSpeed, ySpeed, owner, damage);
		
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	public void initImg(){
		if(bomb == null){

			bomb = ImageLoader.getImage("bomb.png");
		}
		
		if(this.xSpeed > 0){
			this.img = bomb;
		}
		else{
			this.img = Util.mirror(bomb, Util.X_AXIS);
		}
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
	
	public void move(Dimension g, ArrayList<ICollision> list) {
		if(!alive) return;
		ySpeed += Main.GRAVITATION/2;
		this.x += xSpeed;
		this.y += ySpeed;
		
		for(ICollision o : list){
			if(o == this) continue;
			if(o.inObject(x, y) || o.inObject(x + width, y) || o.inObject(x,y+height) || o.inObject(x+width, y+height)){
				//Missiles des gleichen Spielers treffen sich nicht gegenseitig, von anderen Spielern jedoch sehr wohl
				if(canCollideWith(o) || ((o instanceof PlayerCharacter && ((PlayerCharacter)o) == this.getOwner()))){				
					explode();
					damageAOE(getDamageBounds(), damage);
				}
			}
		}
		if(this.y+height >= g.height || this.y <= 0 || this.x+this.width>=g.width || this.x<= 0) explode();
	}
	
	public String toString(){
		return type + ": " + super.toString();
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

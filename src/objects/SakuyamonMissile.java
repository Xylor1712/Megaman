package objects;

import images.ImageLoader;
import interfaces.Damageable;
import interfaces.ICollision;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;

public class SakuyamonMissile extends Missile {

	private static final double MAX_LIFETIME = 1.33;
	public static final int BASE_DAMAGE = 6;

	public static int WIDTH = 24;
	public static int HEIGHT = 24;
	
	private static BufferedImage missile;
	private static BufferedImage[] img = new BufferedImage[3];
	private int imgCounter = 0;
	private int imgCountHelper = -2;
	
	private int size = 1;
	private int sizeCounter = 0;
	
	

	public static String type = "SakuyamonMissile";

	public SakuyamonMissile(int x, int y, double xSpeed, double ySpeed,
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
		
		sizeCounter++;
		if(sizeCounter > MAX_LIFETIME*Main.actionsPerSecond/3){
			size++;
			if(size == 4) explode();
			sizeCounter = 0;
		}
		
		for(ICollision o : list){
			if(o.inObject(x, y) || o.inObject(x + width, y) || o.inObject(x,y+height) || o.inObject(x+width, y+height)){
				
				if(!canCollideWith(o)) continue;
				
				explode();
				
				if(o instanceof Damageable){
					damage = getDamage();
					((Damageable)o).damaged(damage, owner);
				}
				
			}
		}
		if(this.y+height >= g.height || this.y <= 0|| this.x+this.width>=g.width || this.x<= 0) explode();
	}

	@Override
	public void initImg(){
		if(missile == null){
			missile = ImageLoader.getImage("sakuyamonMissile.png");
		}
		if(missile == null) return;
		for(int i = 0; i < 3; i++)
		{
			if(img[i] == null) img[i] = missile.getSubimage(i * WIDTH, 0, WIDTH, HEIGHT);
		}
	}
	
	public static int nextImageCounterWhen = Main.getNextImageCounterWhen();

	@Override
	public void paint(Graphics g) {
		if(alive){
			g.drawImage(img[size-1],x,y,null);
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
	
	private int getDamage(){
		return (int)(damage * size);
	}

}

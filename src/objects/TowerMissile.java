package objects;

import images.ImageLoader;
import interfaces.ICollision;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;
import main.Util;

public class TowerMissile extends Missile {
	
	private static final int MAX_LIFETIME = 3;
	private int lifetime = 0;
	
	public static int WIDTH = 30;
	public static int HEIGHT = 15;

	private Character destination;
	
	private static BufferedImage imgRight;
	private static BufferedImage imgLeft;
	
	

	public TowerMissile(int x, int y, double xSpeed, double ySpeed,
			Tower owner, Character pc, int damage) {
		super(x, y, xSpeed, ySpeed, owner, damage);
		this.destination = pc;
		
		this.width = WIDTH;
		this.height = HEIGHT;
	}

	@Override
	public void move(Dimension g, ArrayList<ICollision> list) {
		if(!isAlive()) return;
		
		if(this.getBounds().intersects(destination.getBounds())){
			destination.damaged(((Tower)owner).getDamage(), owner);
			this.explode();
			return;
		}
		
		this.lifetime++;
		if(this.lifetime > MAX_LIFETIME * Main.actionsPerSecond){
			this.explode();
			return;
		}
		
		Point aimAt = destination.getCenter();
		Point origin = getCenter();
		
		double xDif = aimAt.x-origin.x;
		double yDif = aimAt.y-origin.y;
		double distance = Math.sqrt(xDif*xDif + yDif*yDif);
		if(distance == 0) return; //BEHELFSMÄßIG; DO FIX!
		double factor = 3*Missile.MISSILE_SPEED/distance/4;
		this.xSpeed = xDif*factor;
		this.ySpeed = yDif*factor;
		
		this.x += xSpeed;
		this.y += ySpeed;
	}
	
	public Point getCenter(){
		return new Point(x+width/2, y+height/2);
	}

	@Override
	public void initImg() {
		if(imgRight == null){
			imgRight = ImageLoader.getImage("towerMissile.png");
		}
		if(imgLeft == null){
			imgLeft = Util.mirror(imgRight, Util.X_AXIS);
		}
	}
	

	private int imgCounter = 0;
	private int imgCountHelper = -2;

	@Override
	public void paint(Graphics g) {
		if(alive){
			g.drawImage(xSpeed >= 0 ? imgRight : imgLeft, x, y, null);
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


}

package objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.Main;
import images.ImageLoader;
import interfaces.Paintable;

public abstract class GameObject implements Paintable{
	
	public boolean deleteThisToken = false;
	
	public int id;
	
	private static BufferedImage explode;
	public static BufferedImage[] expImg = new BufferedImage[4];
	
	private void initExplodeImages(){
		if(explode == null || expImg == null){
			if(Main.debug) System.out.println("Load Image: missile.png");
			try{
				explode = ImageLoader.getImage("explode.png");
				
				for(int i = 0; i < expImg.length; i++){
					expImg[i] = explode.getSubimage(i*50, 0, 50, 52);
				}
			}
			catch(Exception e){
				System.out.println("explode loading: failed");
				return;
			}
		}
	}
	
	protected GameObject(){
		this.id = Main.idCounter;
		Main.idCounter++;
		initExplodeImages();
	}
	
	public abstract String transformToString();
	
	public abstract Rectangle getBounds();
	
	public void paint(Graphics g, Rectangle frameBounds){
		/*if(frameBounds.intersects(getBounds()) || Main.mode == Main.SERVER_MODE) */
		paint(g);
	}
	
	public abstract void paint(Graphics g);
	
	

}

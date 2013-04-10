package objects;

import images.ImageLoader;
import interfaces.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;


public class Rect extends StaticObject implements ICollision{
	
	private static BufferedImage brick;
	private static TexturePaint brickPaint;
	private BufferedImage img;
	
	public Rect(int x, int y, int width, int height){
		super(x, y, width, height);

		initBrick();
		initImg();
	}
	
	public void initBrick(){
		if(brick == null){
			brick = ImageLoader.getImage("BlockoG.png");
			brickPaint = new TexturePaint(brick, new Rectangle(0,0,brick.getWidth(), brick.getHeight()));
		}
	}
	
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	private void initImg() {
		if(img == null || img.getWidth() != width || img.getHeight() != height){
			img = gfxConf.createCompatibleImage( width, height );
		}
		Graphics2D g2d = img.createGraphics();
		if(g2d.getClipBounds() == null){
			g2d.setClip(0,0,width,height);
		}
		g2d.setPaint(brickPaint);
		g2d.fillRect(0,0,this.width,this.height);
		g2d.setColor(Color.WHITE);
		g2d.drawRect(0,0,this.width,this.height);
	}

	public void paint(Graphics g){
		g.drawImage(img, x, y, null);
	}
	
	//ICollsion
	
	@Override
	public boolean inObject(int x, int y){
		return this.getBounds().contains(x, y);
	}
	
	@Override
	public void collide(int x, int y, ICollision obj){
	}

	@Override
	public String transformToString() {
		
		return "Rect#"+ x+ "#" + y + "#" + width + "#" + height;
	}
	
	public static Rect newRectFromString(String transform){
		Rect res;
		try{
			String[] t = transform.split("#");
			if(t.length != 5 || !t[0].equals("Rect")) return null;
			res = new Rect(Integer.parseInt(t[1]), Integer.parseInt(t[2]), Integer.parseInt(t[3]), Integer.parseInt(t[4]));
		}
		catch (Exception e){
			return null;
		}
		return res;
	}
	
	
}
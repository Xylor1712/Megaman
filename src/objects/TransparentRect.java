package objects;

import images.ImageLoader;
import interfaces.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.Util;


public class TransparentRect extends StaticObject implements ICollision{
	
	public final static int HEIGHT = 10;
	
	private static BufferedImage trapdoor;
	private BufferedImage img;
	
	public TransparentRect(int x, int y, int width){
		super(x, y, width, HEIGHT);

		initTrapdoor();
		initImg();
	}
	
	public void initTrapdoor(){
		if(trapdoor == null){
			trapdoor = ImageLoader.getImage("Trapdoor.png");
		}
	}
	
	private void initImg() {
		if(img == null || img.getWidth() != width || img.getHeight() != height){
			img = Util.getTransparentImage( width, height );
		}
		Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(trapdoor, 0, 0, width, height, null);
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
		
		return "TransparentRect#"+ x+ "#" + y + "#" + width;
	}
	
	public static TransparentRect newRectFromString(String transform){
		TransparentRect res;
		try{
			String[] t = transform.split("#");
			if(t.length != 4 || !t[0].equals("TransparentRect")) return null;
			res = new TransparentRect(Integer.parseInt(t[1]), Integer.parseInt(t[2]), Integer.parseInt(t[3]));
		}
		catch (Exception e){
			return null;
		}
		return res;
	}
	
	
}
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import objects.PlayerCharacter;

public class Util {
	
	//Hilfsmethoden!
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	
	private final static GraphicsConfiguration gfxConf = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	
	public static BufferedImage mirror(BufferedImage org, int axis){
		
		int width = org.getWidth();
		int height = org.getHeight();
		BufferedImage dest = new BufferedImage(width, height, org.getType());
		
		if(axis == X_AXIS){
			for(int i = 0; i < width; i++)
				for(int j = 0; j < height; j++){
					dest.setRGB(width-1-i, j, org.getRGB(i,j));
					//r1.setDataElements(width-1-i, j, raster.getDataElements(i, j, null));
				}
					
		}
		else if(axis == Y_AXIS){
			for(int i = 0; i< width; i++)
				for(int j = 0; j < height; j++)
					dest.setRGB(i, height-1-j, org.getRGB(i,j));
		}
		//dest.setData(r1);
		
		return imageToBufferedImage(makeColorTransparent(dest, new Color(0)));
	}
	
	
	   public static BufferedImage imageToBufferedImage(Image image) {

	    	BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    	Graphics2D g2 = bufferedImage.createGraphics();
	    	g2.drawImage(image, 0, 0, null);
	    	g2.dispose();

	    	return bufferedImage;

	    }

	    public static Image makeColorTransparent(BufferedImage im, final Color color) {
	    	ImageFilter filter = new RGBImageFilter() {

	    		// the color we are looking for... Alpha bits are set to opaque
	    		public int markerRGB = color.getRGB() | 0xFF000000;

	    		public final int filterRGB(int x, int y, int rgb) {
	    			if ((rgb | 0xFF000000) == markerRGB) {
	    				// Mark the alpha bits as zero - transparent
	    				return 0x00FFFFFF & rgb;
	    			} else {
	    				// nothing to do
	    				return rgb;
	    			}
	    		}
	    	};

	    	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	    	return Toolkit.getDefaultToolkit().createImage(ip);
	    }
	    
	    
	    public static Point frameToMapPoint(Point p){
	    	PlayerCharacter player = Main.getCurrentPlayer();
			Point center = new Point((int)player.getxPos() + player.getWidth()/2 , (int)player.getyPos() + player.getHeight()/2);
			
			Dimension frameBounds = Main.canvas.getSize();
			
			Rectangle drawBounds = new Rectangle(-center.x+frameBounds.width/2,-center.y+frameBounds.height/2, frameBounds.width, frameBounds.height);
			
			if(drawBounds.x > 0) drawBounds.x = 0;
			if(drawBounds.y > 0) drawBounds.y = 0;
			if(drawBounds.x < -(Main.mapSize.width-frameBounds.width)) drawBounds.x = -Main.mapSize.width+frameBounds.width;
			if(drawBounds.y < -(Main.mapSize.height-frameBounds.height)) drawBounds.y = -Main.mapSize.height+frameBounds.height;
			
			return new Point(p.x-drawBounds.x, p.y-drawBounds.y);
	    }
	    
	    public static void drawStringCenteredAt(Graphics g, String s, int x, int y){
		    FontMetrics fm = g.getFontMetrics();

		    Rectangle2D textsize = fm.getStringBounds(s, g);
		    int xPos = (int)(x-textsize.getWidth()/2);
		    int yPos = (int)(y+textsize.getHeight()/2);
		    g.drawString(s, xPos, yPos);
	    }
	    
	    public static boolean isValidChar(char c){
	    	return (Character.isLetterOrDigit(c) || c == ' ' || c == '.' || c == '?');
	    }
	    
	    public static BufferedImage getTransparentImage(int width, int height){
	    	BufferedImage img = gfxConf.createCompatibleImage( width, height );
	    	return imageToBufferedImage(makeColorTransparent(img, new Color(img.getRGB(0, 0))));
	    }
	    
	  
	

}

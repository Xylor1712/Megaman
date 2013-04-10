package GUI;

import images.ImageLoader;
import interfaces.Paintable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import mapping.MapCreator;
import objects.*;

public class MapCanvas extends JPanel {

	private static final long serialVersionUID = 2925168494263984413L;
	
	private static final int crossSize = 5;
	
	public MapCreator root;
	
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	private BufferedImage img;
	
	private static BufferedImage backgroundImage;
//	private static TexturePaint backgroundPaint;
	private BufferedImage background;
	
	public MapCanvas(MapCreator root){
		this.root = root;
		initBackground();
		this.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent e){
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				background();				
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				if(gfxConf == null) return;
				background();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				background();
			}
		});
	}
	
	public void initBackground(){
		if(backgroundImage == null){
			backgroundImage = ImageLoader.getImage("Background.png");
		}
	}
	
	public void background(){
		if(root.mapSize == null) return;
		if(background == null || background.getWidth() != root.mapSize.width || background.getHeight() != root.mapSize.height){
			background = gfxConf.createCompatibleImage( root.mapSize.width, root.mapSize.height );
		}
		Graphics2D g2d = background.createGraphics();
		/*
		if(g2d.getClipBounds() == null){
			g2d.setClip(0,0,root.mapSize.width,root.mapSize.height);
		}
		g2d.setPaint(backgroundPaint);
		g2d.fillRect(0,0,root.mapSize.width,root.mapSize.height);
		*/
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(backgroundImage, 0, 0, root.mapSize.width, root.mapSize.height, this);
	}
	
	@Override
	protected void paintComponent(Graphics g){
		if(img == null || img.getWidth() != root.mapSize.width || img.getHeight() != root.mapSize.height){
			img = gfxConf.createCompatibleImage( root.mapSize.width, root.mapSize.height );
		}
		Graphics2D g2d = img.createGraphics();
		if(g2d.getClipBounds() == null){
			g2d.setClip(0,0,root.mapSize.width,root.mapSize.height);
		}
		Point center = root.viewPoint;
		
		Dimension frameBounds = getSize();
		
		Rectangle drawBounds = new Rectangle(-center.x+frameBounds.width/2,-center.y+frameBounds.height/2, frameBounds.width, frameBounds.height);
		
		if(drawBounds.x > 0) drawBounds.x = 0;
		if(drawBounds.y > 0) drawBounds.y = 0;
		if(drawBounds.x < -(root.mapSize.width-frameBounds.width)) drawBounds.x = -root.mapSize.width+frameBounds.width;
		if(drawBounds.y < -(root.mapSize.height-frameBounds.height)) drawBounds.y = -root.mapSize.height+frameBounds.height;
		
		paintContent(g2d, drawBounds);
		
		paintCross(g2d, center);
		
		g.drawImage( img, drawBounds.x,drawBounds.y,this);
		
	}
	
	public void paintContent(Graphics2D g, Rectangle bounds){
		if(background == null || background.getWidth() != root.mapSize.width || background.getHeight() != root.mapSize.height){
			background();
		}
		g.drawImage(background, 0, 0,null);

		@SuppressWarnings("unchecked")
		ArrayList<GameObject> list = (ArrayList<GameObject>) (root.compList.clone());
		
		Rectangle newRect = new Rectangle(-bounds.x, -bounds.y, bounds.width, bounds.height);
		for(GameObject obj : list){
			if(obj instanceof Paintable){
				((Paintable)obj).paint(g, newRect);
			}
		}
		if( root.cur != null) root.cur.paint(g, newRect);
		
		paintInfo(g, bounds);
	}
	
	public void paintCross(Graphics2D g, Point p){
		g.setColor(Color.white);
		g.drawLine(p.x-crossSize, p.y, p.x+crossSize, p.y);
		g.drawLine(p.x, p.y-crossSize, p.x, p.y+crossSize);
	}
	
	private void paintInfo(Graphics2D g, Rectangle bounds){
		g.setColor(Color.white);
		Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 20);
	    g.setFont(f);
	    FontMetrics fm = g.getFontMetrics();
	    String text = "Current Object: " + root.type;
	    Rectangle2D textsize = fm.getStringBounds(text, g);
	    int xPos = (int)(textsize.getHeight()/2 - bounds.x);
	    int yPos = (int)(textsize.getHeight() - bounds.y);
	    g.drawString(text, xPos, yPos);
	}


}

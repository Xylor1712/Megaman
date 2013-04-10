package images;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import main.Main;

public class ImageLoader {
/*
	public BufferedImage getImage(String path){
		try{
			if(Main.debug) System.out.println("Load " + path);
			
			return ImageIO.read(this.getClass().getResource(path));
		}
		catch(IOException e){
			System.out.println("Failed to load File: " + path);
			return null;
		}
	}
	*/
	public static BufferedImage getImage(String path){
		try{
			if(Main.debug) System.out.println("Load " + path);
			
			return ImageIO.read(ImageLoader.class.getResource(path));
		}
		catch(IOException e){
			System.out.println("Failed to load File: " + path);
			return null;
		}
		catch(Exception e){
			System.out.println("Image loading: failed (Path: " + path + ")");
			return null;
		}
	}
	
	public static ImageIcon getIcon(String path){
		try{
			if(Main.debug) System.out.println("Load " + path);
			ImageIcon res = new ImageIcon(path);
			return res;
		}
		catch(Exception e){
			System.out.println("ImageIcon loading: failed (Path: " + path + ")");
			return null;
		}
	}
}

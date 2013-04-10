package objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Main;
import images.ImageLoader;
import interfaces.ICollision;

public class HeartItem extends PickUpItem{
	
	public static final int HEAL_AMOUNT = 25;
	
	public static final String fileName = "heart";
	
	public static BufferedImage imgFull;
	public static BufferedImage imgEmpty;

	public HeartItem(int x, int y){
		super(x, y);
	}
	
	@Override
	public String transformToString() {
		return "Heart#"+ super.transform();
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
		if(obj instanceof PlayerCharacter && enabled){
			PlayerCharacter d = (PlayerCharacter)obj;
			if(d.heal(HEAL_AMOUNT)) this.enabled = false;
		}
	}

	public static HeartItem newHeartFromString(String line) {
		HeartItem res;
		try{
			String[] s = line.split("#");
			if(!s[0].equals("Heart") || s.length != 5) return null;
			res = new HeartItem(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			res.setRefreshCounter(Integer.parseInt(s[3]));
			res.setEnabled(Boolean.parseBoolean(s[3]));
		}
		catch(Exception e){
			return null;
		}
		return res;
	}
	
	public void initImg(){
		if(imgFull == null){

			imgFull = ImageLoader.getImage(fileName + "Full.png");
		}
		if(imgEmpty == null){

			imgEmpty = ImageLoader.getImage(fileName + "Empty.png");

		}
	}
	
	@Override
	public void paint(Graphics g) {
		if(enabled){
			g.drawImage(imgFull, x, y, Main.canvas);
		}
		else{
			g.drawImage(imgEmpty, x, y, Main.canvas);
		}
	}

	@Override
	protected int respawnTime() {
		return 30;
	}

	@Override
	public void applyString(String transform) {
		super.appTransform(transform);
	}

}

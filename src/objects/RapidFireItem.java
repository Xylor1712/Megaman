package objects;

import images.ImageLoader;
import interfaces.ICollision;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Main;

public class RapidFireItem extends PickUpItem {
	
	private static final long serialVersionUID = 4050382842649916482L;

	public static final String fileName = "rapidfire";
	
	public static BufferedImage imgFull;
	public static BufferedImage imgEmpty;


	public RapidFireItem(int x, int y) {
		super(x, y);
	}
	
	@Override
	public String transformToString() {
		return "RapidFireItem#"+ super.transform();
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
		if(obj instanceof PlayerCharacter && enabled){
			PlayerCharacter pc = ((PlayerCharacter) obj);
			if(!pc.isAlive()) return;
			pc.turnRapidFireOn();
			this.enabled = false;
		}
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
	
	public static RapidFireItem newRapidFireItemFromString(String line) {
		RapidFireItem res;
		try{
			String[] s = line.split("#");
			if(!s[0].equals("RapidFireItem") || s.length != 5) return null;
			res = new RapidFireItem(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			res.setRefreshCounter(Integer.parseInt(s[3]));
			res.setEnabled(Boolean.parseBoolean(s[4]));
		}
		catch(Exception e){
			return null;
		}
		return res;
	}

	@Override
	protected int respawnTime() {
		return 60;
	}
	
	@Override
	public void applyString(String transform) {
		super.appTransform(transform);
	}

}

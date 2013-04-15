package objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Main;
import images.ImageLoader;
import interfaces.ICollision;

public class BombItem extends PickUpItem {
	
	private static final long serialVersionUID = -2971580873342772041L;
	
	public static final int AMMO_AMOUNT = 5;
	public static final String fileName = "bombs";
	
	public static BufferedImage imgFull;
	public static BufferedImage imgEmpty;

	public BombItem(int x, int y){
		super(x, y);
	}
	
	@Override
	public String transformToString() {
		return "BombItem#"+ super.transform();
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
		if(obj instanceof PlayerCharacter && enabled){
			PlayerCharacter d = (PlayerCharacter)obj;
			if(d.atMaxAmmo() || !d.isAlive()) return;
			d.increaseBombAmmo(AMMO_AMOUNT);
			this.enabled = false;
		}
	}

	public static BombItem newBombItemFromString(String line) {
		BombItem res;
		try{
			String[] s = line.split("#");
			if(!s[0].equals("BombItem") || s.length != 5) return null;
			res = new BombItem(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			res.setRefreshCounter(Integer.parseInt(s[3]));
			res.setEnabled(Boolean.parseBoolean(s[4]));
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

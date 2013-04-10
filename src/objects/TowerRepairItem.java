package objects;

import images.ImageLoader;
import interfaces.ICollision;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Main;

public class TowerRepairItem extends PickUpItem {


	private static final long serialVersionUID = 6258498744329683257L;
	
	public static final int HEAL_AMOUNT = 150;
	public static BufferedImage imgFull;
	public static BufferedImage imgEmpty;
	
	public static final String fileName = "wrench";

	public TowerRepairItem(int x, int y) {
		super(x, y);
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
		if(obj instanceof PlayerCharacter && enabled){
			PlayerCharacter d = (PlayerCharacter)obj;
			if(!d.hasTowerRepairKit() && d.isAlive()){
				d.setTowerRepairKit(true);
				this.enabled = false;
			}
			
		}
	}

	@Override
	public void initImg() {
		if(imgFull == null){

			imgFull = ImageLoader.getImage(fileName + "Full.png");
		}
		if(imgEmpty == null){

			imgEmpty = ImageLoader.getImage(fileName + "Empty.png");
		}
	}

	@Override
	protected int respawnTime() {
		return 60;
	}

	@Override
	public void applyString(String transform) {
		super.appTransform(transform);
	}

	@Override
	public String transformToString() {
		return "TowerRepairItem#"+ super.transform();
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

	public static TowerRepairItem newTowerRepairFromString(String line) {
		TowerRepairItem res;
		try{
			String[] s = line.split("#");
			if(!s[0].equals("TowerRepairItem") || s.length != 5) return null;
			res = new TowerRepairItem(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			res.setRefreshCounter(Integer.parseInt(s[3]));
			res.setEnabled(Boolean.parseBoolean(s[4]));
		}
		catch(Exception e){
			return null;
		}
		return res;
	}

}

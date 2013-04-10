package objects;

import interfaces.ICollision;
import interfaces.NeedsSync;
import interfaces.NeedsUpdate;
import interfaces.Resetable;

import java.awt.Rectangle;

import main.Main;

public abstract class PickUpItem extends StaticObject implements ICollision, NeedsUpdate, NeedsSync, Resetable  {

	private static final long serialVersionUID = 1137608780139672894L;

	protected boolean enabled = true;
	
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	
	public PickUpItem(int x, int y){
		super(x, y, WIDTH, HEIGHT);
		initImg();
	}
	
	public abstract void initImg();
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}


	@Override
	public boolean inObject(int x, int y) {
		return getBounds().contains(x, y);
	}
	
	private int refreshCounter = 0;
	
	public void setRefreshCounter(int i){
		refreshCounter = i;
	}
	
	@Override
	public void update() {
		if(!enabled){
			refreshCounter++;
			if(refreshCounter > Main.actionsPerSecond * respawnTime()){
				enabled = true;
				refreshCounter = 0;
			}
		}
	}
	
	
	
	protected abstract int respawnTime();
	
	@Override
	public void reset(){
		enabled = true;
		refreshCounter = 0;
	}

	public abstract void applyString(String transform);
	
	public void appTransform(String transform){
		try{
			String[] s = transform.split("#");
			
			if(s.length != 5) throw new Exception("Wrong length");
			
			this.x = Integer.parseInt(s[1]);
			this.y = Integer.parseInt(s[2]);
			this.refreshCounter = Integer.parseInt(s[3]);
			this.enabled = Boolean.parseBoolean(s[4]);
		}
		catch(Exception e){
			System.err.println("Failed to apply transform");
			e.printStackTrace();
		}
	}
	
	public String transform(){
		return x+"#"+y+"#"+ refreshCounter + "#" +enabled;
	}
	
}

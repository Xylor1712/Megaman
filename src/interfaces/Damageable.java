package interfaces;

import objects.GameObject;

public interface Damageable {
	
	public void damaged(int dmg, GameObject o);
	
	public int getHealth();
	
	public void setHealth(int h);
	
	public int getMaxHealth();
	
	public void die();
	
	public boolean isAlive();
	
	
}

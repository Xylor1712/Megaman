package interfaces;

import java.awt.Rectangle;

public interface ICollision{
	
	public boolean inObject(int x, int y);
	
	public boolean canCollideWith(ICollision o);
	
	public void collide(int x, int y, ICollision obj);
	
	public Rectangle getBounds();
	
}
package objects;

import java.awt.Point;
import java.awt.Rectangle;

import interfaces.ICollision;
import interfaces.Resetable;

public abstract class StaticObject extends GameObject implements Resetable{
	
	protected int x = 200;
	protected int y = 100;
	protected int width = 50;
	protected int height = 50;
	
	protected StaticObject(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	
	
	public int getX() {
		return x;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}



	public static StaticObject newObjFromString(String line){
		String objName = line.split("#")[0];
		StaticObject obj = null;
		switch(objName){
		case "Rect":
			obj = Rect.newRectFromString(line);
			break;
		case "Heart":
			obj = HeartItem.newHeartFromString(line);
			break;
		case "BombItem":
			obj = BombItem.newBombItemFromString(line);
			break;
		case "ShieldItem":
			obj = ShieldItem.newShieldItemFromString(line);
			break;
		case "RapidFireItem":
			obj = RapidFireItem.newRapidFireItemFromString(line);
			break;
		case "Flag":
			obj = Flag.newFlagFromString(line);
			break;
		case "SpawnPoint":
			obj = SpawnPoint.newSpawnPointFromString(line);
			break;
		case "DominationPoint":
			obj = DominationPoint.newDominationPointFromString(line);
			break;
		case "Tower":
			obj = Tower.newTowerFromString(line);
			break;
		case "MeleeMinionSpawner":
			obj = MeleeMinionSpawner.newMMSFromLine(line);
			break;
		case "TransparentRect":
			obj = TransparentRect.newRectFromString(line);
			break;
		case "TowerRepairItem":
			obj = TowerRepairItem.newTowerRepairFromString(line);
			break;
		case "MinionSpawner":
			obj = MinionSpawner.newMSFromLine(line);
			break;
		}
		return obj;
	}
	
	public boolean canCollideWith(ICollision obj){
		return false;
	}
	
	public void reset(){
		
	}
	
	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public Point getCenter(){
		return new Point(x + width/2, y + height/2);
	}
	
	
}

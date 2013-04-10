package mapping;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import objects.*;

import main.Clock;
import main.Main;

import GUI.MapCanvas;

public class MapCreator extends JFrame {

	private static final long serialVersionUID = 4249926514863795400L;
	
	public boolean fullscreen = false;
	
	public MapCanvas canvas;
	public MapMenuBar menubar;
	public MapListener listener;
	
	public MapLoading io;
	public boolean recentlyChanged = false;
	
	public Clock renderer;
	public int fps = 60;
	
	public Dimension mapSize;
	public Point viewPoint;
	
	public ObjType type = ObjType.Rect;
	public StaticObject cur;
	
	public ArrayList<GameObject> compList = new ArrayList<GameObject>();
	
	public MapCreator(){
		this.setTitle("Megaman: MapCreator");
		this.setSize(1000,850);
		
		canvas = new MapCanvas(this);
		menubar = new MapMenuBar(this);
		listener = new MapListener(this);
		io = new MapLoading(this);
		
		renderer = new Clock(1000/fps){
			public void event(){
				canvas.repaint();
				moveViewPoint();
			}
			public void interruptedExc(InterruptedException e){
				System.out.println("Renderer Interrupted");
			}
		};
		
		
		mapSize = new Dimension(Main.mapSize.getSize());
		PlayerCharacter pc = Main.getCurrentPlayer();
		viewPoint = new Point((int) pc.getxPos(), (int)pc.getyPos());
		
		
		this.add(canvas);
		this.setJMenuBar(menubar);
		
		this.addKeyListener(listener);
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);
		
		this.setVisible(true);
		this.renderer.start();
	}
	
	public void deleteItemAt(Point clicked){

		for(GameObject go : getCompList()){
			if(go.getBounds().contains(clicked.x, clicked.y)){
				if(Main.debug)System.out.println("Rect fount");
				go.deleteThisToken = true;
				deleteUnusedObjects();
				canvas.repaint();
				recentlyChanged = true;
				return;
			}
		}
		if(Main.debug)System.out.println("No rect found");
	}
	
	public ArrayList<GameObject> getCompList(){
		@SuppressWarnings("unchecked")
		ArrayList<GameObject> res =(ArrayList<GameObject>) this.compList.clone();
		return res;
	}
	
	
	public Point frameToMapPoint(Point p){
		if(p == null) return null;
		Point center = this.viewPoint;
		
		Dimension frameBounds = canvas.getSize();
		
		Rectangle drawBounds = new Rectangle(-center.x+frameBounds.width/2,-center.y+frameBounds.height/2, frameBounds.width, frameBounds.height);
		
		if(drawBounds.x > 0) drawBounds.x = 0;
		if(drawBounds.y > 0) drawBounds.y = 0;
		if(drawBounds.x < -(mapSize.width-frameBounds.width)) drawBounds.x = -mapSize.width+frameBounds.width;
		if(drawBounds.y < -(mapSize.height-frameBounds.height)) drawBounds.y = -mapSize.height+frameBounds.height;
		
		return new Point(p.x-drawBounds.x, p.y-drawBounds.y);
    }
	
	public void deleteUnusedObjects(){
		for(Iterator<GameObject> it = compList.iterator(); it.hasNext();){
			GameObject o = it.next();
			if(o.deleteThisToken){				
				it.remove();
			}
		}
	}
	
	public void setCurObject(Point p1, Point p2){
		
		if(p1 == null || p2 == null) return;
		
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int width = Math.abs(p1.x-p2.x);
		int height = Math.abs(p1.y - p2.y);
		
		if(width == 0 || height == 0){
			cur = null;
			return;
		}
		switch(type){
		case Rect:
			cur = new Rect(x, y, width, height);
			break;
		case Heart:
			cur = new HeartItem(p2.x, p2.y);
			break;
		case BombItem:
			cur = new BombItem(p2.x, p2.y);
			break;
		case ShieldItem:
			cur = new ShieldItem(p2.x, p2.y);
			break;
		case RapidFire:
			cur = new RapidFireItem(p2.x, p2.y);
			break;
		case towerRepairItem:
			cur = new TowerRepairItem(p2.x, p2.y);
			break;
		case blueFlag:
			cur = new Flag(p2.x, p2.y, 2);
			break;
		case greenFlag:
			cur = new Flag(p2.x, p2.y, 3);
			break;
		case redFlag:
			cur = new Flag(p2.x, p2.y, 1);
			break;
		case whiteFlag:
			cur = new Flag(p2.x, p2.y, 0);
			break;
		case blueSpawn:
			cur = new SpawnPoint(p2.x, p2.y, 2);
			break;
		case greenSpawn:
			cur = new SpawnPoint(p2.x, p2.y, 3);
			break;
		case redSpawn:
			cur = new SpawnPoint(p2.x, p2.y, 1);
			break;
		case whiteSpawn:
			cur = new SpawnPoint(p2.x, p2.y, 0);
			break;
		case dominationPoint:
			cur = new DominationPoint(x, y, width, height);
			break;
		case tower:
			cur = new Tower(p2.x, p2.y);
			break;
		case meleeMinionSpawner:
			cur = new MeleeMinionSpawner(p1.x, p1.y, p2);
			break;
		case transparentRect:
			cur = new TransparentRect(x, y, width);
			break;
		}
	}
	
	public static final int INCREASE = 1;
	public static final int DECREASE = -1;
	public static final int UNCHANGED = 0;
	public static final int CAMERA_SPEED = 5;
	
	public int xChange = UNCHANGED;
	public int yChange = UNCHANGED;
	
	
	
	public void moveViewPoint(){
		viewPoint.x += xChange * CAMERA_SPEED;
		viewPoint.y += yChange * CAMERA_SPEED;
	}
	
	
	
	public void reloadMap(int w, int h, ArrayList<GameObject> list) {
		this.compList = new ArrayList<>();
		
		for(GameObject r : list){
			compList.add(r);
		}
		
		mapSize.width = w;
		mapSize.height = h;
		
		canvas.repaint();

	}
	
	public void saveCur(){
		if(cur == null) return;
		compList.add(cur);
		cur = null;
		recentlyChanged = true;
	}
	
	
	
	
}

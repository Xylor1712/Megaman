package mapping;

import java.awt.Dimension;
import java.util.ArrayList;

import objects.GameObject;

public class Map {
	
	private int height;
	private int width;
	private ArrayList<GameObject> mapContent;
	
	public Map(int w, int h){
		height = h;
		width = w;
		mapContent = new ArrayList<>();
	}
	
	public Map(int w, int h, ArrayList<GameObject> content){
		this(w, h);
		mapContent = content;
	}
	
	public void addObject(GameObject obj){
		mapContent.add(obj);
	}
	
	public void load(ArrayList<GameObject> list){
		for(GameObject o : mapContent){
			list.add(o);
		}
	}
	
	public Dimension getDimension(){
		return new Dimension(width, height);
	}
	
}

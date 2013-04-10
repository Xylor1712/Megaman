package objects;

import java.awt.Point;

import main.Main;

import interfaces.NeedsSync;
import interfaces.NeedsUpdate;

public abstract class Spawner extends StaticObject implements NeedsUpdate, NeedsSync {
	
	
	protected boolean enabled = true;
	protected int frequenzy = 5;
	
	protected int amountOfSpawns = 1;
	protected double spawnDelay = 0.25;
	protected int spawnsToDo = 0;
	
	protected int spawnCounter = 0;
	protected int spawnHelper = 0;
	
	
	protected Spawner(int x, int y, int width, int height){
		super(x, y, width, height);
	}
	
	public abstract Point getSpawnPoint();
	
	

	@Override
	public void update() {
		if(this.enabled && Main.mode != Main.CLIENT_MODE){
			spawnCounter++;
			if(spawnCounter >= frequenzy * Main.actionsPerSecond){
				spawnsToDo = amountOfSpawns;
				spawnCounter = 0;
				spawnHelper = 0;
			}
			if(spawnsToDo >= 1){
				spawnHelper++;
				if(spawnHelper >= spawnDelay * Main.actionsPerSecond){
					spawn();
					spawnsToDo--;
					spawnHelper = 0;
				}
			}
		}
	}
	
	public abstract void spawn();

	protected String transform(){
		return x+"#"+y+"#"+ frequenzy + "#" + amountOfSpawns + "#" + spawnDelay +"#"
				+ spawnsToDo +"#" + spawnCounter + "#" + spawnHelper + "#" +enabled;
	}
	
	public void appTransform(String transform){
		try{
			String[] s = transform.split("#");
			
			if(s.length != 9) throw new Exception("Wrong length");
			
			this.x = Integer.parseInt(s[0]);
			this.y = Integer.parseInt(s[1]);
			this.frequenzy = Integer.parseInt(s[2]);
			this.amountOfSpawns = Integer.parseInt(s[3]);
			this.spawnDelay = Double.parseDouble(s[4]);
			this.spawnsToDo = Integer.parseInt(s[5]);
			this.spawnCounter = Integer.parseInt(s[6]);
			this.spawnHelper = Integer.parseInt(s[7]);
			this.enabled = Boolean.parseBoolean(s[8]);
		}
		catch(Exception e){
			System.err.println("Failed to apply transform to Spawner");
			e.printStackTrace();
		}
	}
}

package objects;

import images.ImageLoader;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import main.GameRules;
import main.Main;

public class MinionSpawner extends Spawner {
	public static final int WIDTH = 60;
	public static final int HEIGHT = 75;
	
	protected Point spawnPoint = new Point(this.x+this.width/2-SimpleMinion.WIDTH/2,
											this.y + this.height - SimpleMinion.HEIGHT);
	
	protected Point target;
	protected int team = GameRules.ENVIROMENT_TEAM;
	
	public static BufferedImage img;
	public static BufferedImage[] blue = new BufferedImage[6];
	public static BufferedImage[] red = new BufferedImage[6];
	
	public static final int MELEE = 0;
	public static final int RANGED = 1;
	
	private int[] spawnWave = {0, 0, 0, 1, 1, 1};
	private int counter = 0;
	
	
	
	
	private int imgCounter = 0;
	private int imgCounterHelper = 0;

	public MinionSpawner(int x, int y, Point target) {
		super(x, y, WIDTH, HEIGHT);
		this.target = target;
		initImg();
	}

	private void initImg() {
		if(img == null){
			img = ImageLoader.getImage("Portals.png");
			
			for(int i = 0; i < blue.length; i++){
				blue[i] = img.getSubimage(i * WIDTH, 0, WIDTH, HEIGHT);
				red[i] = img.getSubimage(i * WIDTH, HEIGHT, WIDTH, HEIGHT);
			}
		}
	}

	@Override
	public void spawn() {
		SimpleMinion m;
		if(spawnWave[counter] == MELEE)
			m = new SimpleMeleeMinion(spawnPoint.x, spawnPoint.y, target, GameRules.getNPCLevel());
		else
			m = new SimpleRangedMinion(spawnPoint.x, spawnPoint.y, target, GameRules.getNPCLevel());
		
		m.team = this.team;
		Main.addCompList(m);
		if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/spawn " + m.id + " " + m.transformToString());
		
		counter = (counter+1)%spawnWave.length;
	}

	@Override
	public void paint(Graphics g) {
		if(!this.enabled){
			g.drawImage(team == 2 ? blue[0] : red[0], x, y, null);
		}
		else{
			imgCounterHelper++;
			if(imgCounterHelper >= Main.getNextImageCounterWhen()){
				if(imgCounter+1 < blue.length){
					imgCounter++;
					imgCounterHelper = 0;
				}
			}
			g.drawImage(team == 2 ? blue[imgCounter] : red[imgCounter], x, y, null);
		}
		if(Main.mapCreator != null){
			g.drawLine(spawnPoint.x, spawnPoint.y, target.x, target.y);
		}
	}

	@Override
	public void applyString(String transform) {
		try{
			String[] t = transform.split("_");
			
			String[] s = t[0].split("#");
			
			if(!s[0].equals("MinionSpawner")) throw new Exception("No MinionSpawner-Transform");
		
			if(s.length != 6) throw new Exception("Differ in Length, != 6");
			
			this.team = Integer.parseInt(s[1]);
			this.target.x = Integer.parseInt(s[2]);
			this.target.y = Integer.parseInt(s[3]);
			this.counter = Integer.parseInt(s[4]);
			
			String[] spawn = s[5].split("x");
			int[] newSpawnList = new int[spawn.length];
			for(int i = 0; i < spawn.length; i++){
				newSpawnList[i] = Integer.parseInt(spawn[i]);
			}
			
			this.spawnWave = newSpawnList;
			
			super.appTransform(t[1]);
		
		}
		catch(Exception e){
			System.err.println("Failed to apply transform");
			e.printStackTrace();
		}
	}

	@Override
	public String transformToString() {
		String trans = "MinionSpawner#" +
				team + "#" +
				target.x + "#" +
				target.y + "#" +
				counter + "#";
		for(int i = 0; i<spawnWave.length; i++){
			trans += i == 0 ? ""+spawnWave[i] : "x"+spawnWave[i];
		}
		
		trans += "_" + super.transform();
		return trans;
	}

	public static StaticObject newMSFromLine(String line) {
		try{
			String[] t = line.split("_");
			
			String[] s1 = t[0].split("#");
			String[] s2 = t[1].split("#");
			
			
			if(!s1[0].equals("MinionSpawner")) throw new Exception("No MinionSpawner-Transform");
		
			if(s1.length != 6) throw new Exception("Differ in Length, != 2");
			
			int targetX = Integer.parseInt(s1[2]);
			int targetY = Integer.parseInt(s1[3]);
			int x = Integer.parseInt(s2[0]);
			int y = Integer.parseInt(s2[1]);
			
			MinionSpawner res = new MinionSpawner(x, y, new Point(targetX, targetY));
			res.applyString(line);
			
			return res;
		
		}
		catch(Exception e){
			System.err.println("Failed to apply transform");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Point getSpawnPoint() {
		return spawnPoint;
	}
}

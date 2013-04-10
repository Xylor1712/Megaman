package objects;

import java.awt.Graphics;

import main.GameRules;

public class SpawnPoint extends StaticObject {
	
	private static final long serialVersionUID = -4548554036959274129L;
	
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	
	public int team;
	
	public SpawnPoint(int x, int y, int team) {
		super(x, y, WIDTH, HEIGHT);
		this.team = team;
	}


	@Override
	public String transformToString() {
		return "SpawnPoint#" + x + "#"+y+"#"+team;
	}
	
	public static SpawnPoint newSpawnPointFromString(String line){
		try{
			String[] s = line.split("#");
			
			if(!s[0].equals("SpawnPoint")) throw new Exception("No SpawnPoint-Transform");
			
			int x = Integer.parseInt(s[1]);
			int y = Integer.parseInt(s[2]);
			int team = Integer.parseInt(s[3]);
			
			return new SpawnPoint(x, y, team);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(GameRules.getTeamColor(team, 0.3f));
		g.drawRect(x, y, WIDTH, HEIGHT);
	}
	
	public int getTeam(){
		return team;
	}
	
	public void setTeam(int t){
		this.team = t;
	}

}

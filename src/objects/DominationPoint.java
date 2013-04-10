package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import main.GameRules;
import main.Main;

public class DominationPoint extends CharacterDetection {
	
	private static final long serialVersionUID = -1478125883822386685L;
	
	private static final Color STANDARD_COLOR = Color.YELLOW;
	private Color color = STANDARD_COLOR;

	public DominationPoint(int x, int y, int w, int h) {
		super(x, y, w, h, CharacterDetection.PULSE_MODE, CharacterDetection.OVAL_SHAPE);
		this.detectionMode = DETECT_PLAYER;
	}
	
	

	@Override
	public void event(ArrayList<Character> player) {
		ArrayList<Integer> teams = new ArrayList<>();
		for(Character pc : player){
			if(pc.isAlive() && !teams.contains(pc.team))teams.add(pc.team); 
		}
		if(teams.size() == 1){
			int scoringTeam = teams.get(0);
			this.color = GameRules.getTeamColor(scoringTeam, 1f);
			if(Main.mode != Main.CLIENT_MODE) GameRules.teamScored(scoringTeam);
			GameRules.updateRules();
		}
		else{
			this.color = STANDARD_COLOR;
		}
	}
	
	@Override
	public void paint(Graphics g){
		if(GameRules.currentGameMode == GameRules.DOMINATION || Main.mapCreator != null){
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(color);
			g2.draw(shape);
		}
	}


	@Override
	public boolean active() {
		return GameRules.currentGameMode == GameRules.DOMINATION;
	}



	@Override
	public String transformToString() {
		return "DominationPoint#" + super.transform();
	}



	public static StaticObject newDominationPointFromString(String line) {
		try{
			String[] s = line.split("#");
			
			if(!s[0].equals("DominationPoint")) throw new Exception("Wrong Transform");
			int x = Integer.parseInt(s[2]);
			int y = Integer.parseInt(s[3]);
			int w = Integer.parseInt(s[4]);
			int h = Integer.parseInt(s[5]);
			return new DominationPoint(x, y, w, h);
		}
		catch(Exception e){
			System.err.println("Failed to apply Transform (DominationPoint)");
			return null;
		}
	}
	
	

}

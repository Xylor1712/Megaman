package objects;

import interfaces.ICollision;
import interfaces.NeedsUpdate;

import java.awt.Graphics;

import main.GameRules;
import main.Main;

public class Flag extends PickUpItem implements NeedsUpdate{
	
	public final int team;
	public final int START_X;
	public final int START_Y;
	

	int[] xPoints = new int[3];
	int[] yPoints = new int[3];
	

	public PlayerCharacter owner = null;
	
	

	public Flag(int x, int y, int team) {
		super(x, y);
		START_X = x;
		START_Y = y;
		
		System.out.println("Flag at " + x + "|" + y);
		
		this.team = team;
		
		xPoints[0] = START_X;
		xPoints[1] = START_X + WIDTH;
		xPoints[2] = START_X + WIDTH/4;
		yPoints[0] = START_Y;
		yPoints[1] = START_Y;
		yPoints[2] = START_Y+HEIGHT/2;
	}

	@Override
	public void collide(int x, int y, ICollision obj) {
		if(!GameRules.paintFlag(team)) return;
		
		if(enabled && (this.owner == null || !this.owner.isAlive())){
			if(obj instanceof PlayerCharacter){
				PlayerCharacter pc = (PlayerCharacter) obj;
				if(!pc.isAlive()) return;
				if(pc.team != this.team){
					for(Flag f : Main.getFlagList()) if(f.owner == pc && f!=this) return;
					this.owner = pc;
				}
				else if(!isAtSpawn()){
					this.x = START_X;
					this.y = START_Y;
					this.owner = null;
				}
			}
		}
	}

	@Override
	public void initImg() {
	}

	@Override
	public void paint(Graphics g) {
		if(!GameRules.paintFlag(team) && !(Main.mapCreator != null && Main.mapCreator.compList.contains(this))) return;
		
		float alpha = enabled ? 1f : 0.5f;
		g.setColor(GameRules.getTeamColor(team, alpha));
		//spawn:
		g.drawLine(START_X, START_Y, START_X+WIDTH/2, START_Y+HEIGHT);
		if(isAtSpawn()){
			g.fillPolygon(xPoints , yPoints, 3);
		}
		else{
			g.drawPolygon(xPoints , yPoints, 3);
			
			g.drawLine(this.x, this.y-HEIGHT/2, this.x+WIDTH/2, this.y+HEIGHT/2);
			int[] xpoints = { x, x + WIDTH, x + WIDTH/4};
			int[] ypoints = { y-HEIGHT/2, y-HEIGHT/2, y };
			g.fillPolygon(xpoints , ypoints, 3);
		}
	}
	
	@Override
	public void update(){

		if(!GameRules.paintFlag(team)) return;
		
		if(owner != null && !this.owner.isAlive()) this.owner = null;
		if(owner != null && owner.isAlive() && !this.isAtSpawn()){
			this.x = (int) owner.getxPos();
			this.y = (int) owner.getyPos();
			
			int capturedBy = owner.team;
			for(Flag f : Main.getFlagList()){
				if(this.getBounds().contains(f.START_X+WIDTH/2, f.START_Y+HEIGHT/2) && f.isAtSpawn() && f.team == capturedBy){
					GameRules.flagCaptured(this, owner);
					return;
				}
			}
		}
		else super.update();
	}
	
	public boolean isAtSpawn(){
		return this.x == this.START_X && this.y == this.START_Y && this.owner == null;
	}
	
	public void reset(){
		this.x = START_X;
		this.y = START_Y;
		this.enabled = false;
		this.owner = null;
	}

	@Override
	public String transformToString() {
		String name ="";
		if(owner != null) name = owner.getName();
		return "Flag#" + team + "#" + name + "#" + super.transform();
	}
	
	public static Flag newFlagFromString(String line){
		Flag res;
		try{
			String[] s = line.split("#");
			if(!s[0].equals("Flag") || s.length != 7) return null;
			res = new Flag(Integer.parseInt(s[3]), Integer.parseInt(s[4]),Integer.parseInt(s[1]));
			res.setRefreshCounter(Integer.parseInt(s[5]));
			res.setEnabled(Boolean.parseBoolean(s[6]));
			res.owner = Main.getPlayer(s[2]);
		}
		catch(Exception e){
			return null;
		}
		return res;
		
	}
	
	public void applyString(String trans){
		try{
			String[] s = trans.split("#");
			
			if(!s[0].equals("Flag")) return;

			this.owner = Main.getPlayer(s[2]);
			
			super.appTransform("Flag#"+s[3]+"#"+s[4]+"#"+s[5]+"#"+s[6]);
		}
		catch(Exception e){
			System.err.println("Cant match Flag: " + trans);
		}
	}

	@Override
	protected int respawnTime() {
		return 10;
	}

}

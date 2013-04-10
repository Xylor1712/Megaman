package objects;

import main.GameRules;
import main.Main;


public abstract class NonPlayerCharacter extends Character {
	
	private static final long serialVersionUID = 4250078535970383203L;

	protected NonPlayerCharacter(){
		super();
		initImg();
	}

	protected abstract void initImg();
	
	public void applyString(String transform){
		String[] t;
		try{
			t = transform.split("_");
		}
		catch(Exception e){
			System.err.println("Failed to read String");
			return;
		}
		
		if(!t[0].equals(this.getType())){
			System.err.println("Tryed to apply wrong NonPlayerCharacter-Transform to " + this);
			System.err.println("t[0] = \""+t[0]+"\"");
			return;
		}
		if(t.length != 4){
			System.err.println("Tryed to apply wrong NonPlayerCharacter-Transform to " + this);
			System.err.println(transform);
			System.err.println("Differ in Length: " + t.length + " instead of 4");
			return;
		}
		super.applyTransform(t[3]);
		
		applyTransform(t[2]);
		
		appTransform(t[1]);
	}
	
	protected String transform(){
		return "_" + super.transform();
	}
	
	protected void applyTransform(String trans){
		
	}
	
	public void computeEXP(){

		if(!GameRules.playerCanLevel()) return;
		
		int expForAll = this.level * 2 + 18;
		
		for(Character c : Main.getCharacterList()){
			if(c.isAlive() && c.getCenter().distance(this.getCenter()) <= 200 && c != this && c.team != this.team){
				c.expEarned(expForAll);
			}
		}
	}
	
}

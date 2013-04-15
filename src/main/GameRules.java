package main;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import objects.*;
import objects.Character;

public class GameRules {
	
	public static final int WHITE = 0;
	public static final int RED = 1;
	public static final int BLUE = 2;
	public static final int GREEN = 3;
	
	public static final int LAST_MAN_STANDING = 1;
	public static final int CAPTURE_THE_FLAG = 2;
	public static final int KILL_COUNT = 3;
	public static final int DOMINATION = 4;
	public static final int TEAM_DEATHMATCH = 5;
	public static final int BASE_VS_BASE = 6;
	public static final int SURVIVAL = 7;
	
	public static final int NO_RESPAWN = -1;
	
	public static int KILL_COUNT_GOAL = 10;
	public static final int CAPTURE_THE_FLAG_GOAL = 5;
	public static final int TEAM_DEATHMATCH_GOAL = 20;
	public static final int DOMINATION_GOAL = 120;
	
	public static final int ENVIROMENT_TEAM = -1;
	
	public static int currentGameMode = LAST_MAN_STANDING;
	
	
	public static int teams[] = {0, 1, 2, 3};
	public static int scoreOfTeam[] = {0, 0, 0, 0};
	public static int lastSpawnPoint[];
	

	public static long currentGameTime = 0;
	
	
	
	public static void updateRules(){
		
		if(Main.mode == Main.CLIENT_MODE) return;
		
		switch(currentGameMode){
		case LAST_MAN_STANDING:
			lastManStandingRuleset();
			break;
		case KILL_COUNT:
			killCountRuleset();
			break;
		case CAPTURE_THE_FLAG:
			captureTheFlagRuleset();
			break;
		case TEAM_DEATHMATCH:
			teamDeathmatchRuleset();
			break;
		case DOMINATION:
			dominationRuleset();
			break;
		case BASE_VS_BASE:
			baseVsBaseRuleset();
			break;
		case SURVIVAL:
			survivalRuleset();
			break;
		}
	}
	
	private static void survivalRuleset() {
		boolean anySurvivor = false;
		for(PlayerCharacter pc : Main.getPlayerList()){
			if(pc.isAlive()) anySurvivor = true;
		}
		
		if(anySurvivor){
			return;
		}
		Main.canvas.displayMessage("Your survived " + currentGameTime/Main.actionsPerSecond + " Seconds!");
		Main.endGame();
	}

	private static void baseVsBaseRuleset() {
		boolean red = false;
		boolean blue = false;
		for(Tower t : Main.getTowerList()){
			if(t.isAlive()){
				if(t.team == BLUE){
					blue = true;
				}
				if(t.team == RED){
					red = true;
				}
			}
		}
		if(red && blue){
			return;
		}

		for(int f = 0; f < scoreOfTeam.length; f++) scoreOfTeam[f] = 0;
		
		messageWinner(blue ? BLUE : RED);
		Main.endGame();
	}

	private static void dominationRuleset(){
		int winner = -1;
		
		int i = 0;
		while(i < scoreOfTeam.length){
			if(scoreOfTeam[i] >= DOMINATION_GOAL){
				winner = i;
			}
			i++;
		}
		
		if(winner < 0) return;
		
		for(int f = 0; f < scoreOfTeam.length; f++) scoreOfTeam[f] = 0;
		messageWinner(teams[winner]);
		Main.endGame();
	}
	

	private static void teamDeathmatchRuleset() {
		int winner = -1;

		int i = 0;
		while(i < scoreOfTeam.length){
			if(scoreOfTeam[i] >= TEAM_DEATHMATCH_GOAL){
				winner = i;
			}
			i++;
		}
		
		if(winner < 0) return;
		
		for(int f = 0; f < scoreOfTeam.length; f++) scoreOfTeam[f] = 0;
		messageWinner(teams[winner]);
		Main.endGame();
	}

	private static void killCountRuleset(){
		PlayerCharacter winner = null;
		for(PlayerCharacter pc : Main.getPlayerList()){
			if(pc.getKills() >= KILL_COUNT_GOAL){
				winner = pc;
			}
		}
		
		if(winner == null) return;
		
		messageWinner(winner.getName());
		Main.endGame();
	}
	
	private static void captureTheFlagRuleset(){
		int winner = -1;

		int i = 0;
		while(i < scoreOfTeam.length){
			if(scoreOfTeam[i] >= CAPTURE_THE_FLAG_GOAL){
				winner = i;
			}
			i++;
		}
		
		if(winner < 0) return;
		
		for(int f = 0; f < scoreOfTeam.length; f++) scoreOfTeam[f] = 0;
		messageWinner(teams[winner]);
		Main.endGame();
	}
	
	
	private static void lastManStandingRuleset(){
		int playersAlive = 0;
		PlayerCharacter lastAlivePlayer = null;
		for(PlayerCharacter pc : Main.getPlayerList()){
			if(pc.isAlive()){
				playersAlive++;
				lastAlivePlayer = pc;
			}
		}
		if(playersAlive <= 1){
			if(lastAlivePlayer != null){
				messageWinner(lastAlivePlayer.getName());
				Main.endGame();
			}
		}
	}
	
	public static int getAmountTeams(){
		switch(currentGameMode){
		case LAST_MAN_STANDING:
		case SURVIVAL:
		case KILL_COUNT:
			return 1;
		case CAPTURE_THE_FLAG:
		case TEAM_DEATHMATCH:
			return teams.length;
		default:
			return 2;
		}
	}

	
	public static boolean isTeamGame(){
		switch(currentGameMode){
		case LAST_MAN_STANDING:
		case KILL_COUNT:
			return false;
		default:
			return true;
		}
	}
	
	public static boolean playerCanLevel(){
		switch(currentGameMode){
		case BASE_VS_BASE:
		case SURVIVAL:
			return true;
		default:
			return false;
		}
	}
	
	public static int getNPCLevel(){
		int level = 1;
		
		if(!playerCanLevel()) return level;
		
		for(PlayerCharacter pc : Main.getPlayerList()){
			if(pc.getLevel() > level) level = pc.getLevel();
		}
		return level;
	}
	
	public static int respawnTime(){
		switch(currentGameMode){
		case LAST_MAN_STANDING:
		case SURVIVAL:
			return NO_RESPAWN;
		default:
			return 3;
		}
	}
	
	public static void messageWinner(String winner){
		if(isTeamGame()) return;
		
		Main.canvas.displayMessage(winner + " won!");
		
		Main.getPlayer(winner).increaseWins();
		
		if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/win "+ winner);
	}
	
	public static void messageWinner(int team){
		if(!isTeamGame()) return;
		
		Main.canvas.displayMessage(getTeamName(team) + " won!");
		
		for(PlayerCharacter pc : Main.getPlayerList()){
			if(pc.team == team) pc.increaseWins();
		}
	
		if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/win "+ team);
	}
	
	public static boolean killReset(){
		return currentGameMode == KILL_COUNT 
				|| currentGameMode == TEAM_DEATHMATCH;
	}
	public static boolean deathReset(){
		return currentGameMode == KILL_COUNT 
				|| currentGameMode == TEAM_DEATHMATCH;
	}
	public static boolean flagReset(){
		return currentGameMode == CAPTURE_THE_FLAG;
	}
	public static boolean towerReset(){
		return currentGameMode == BASE_VS_BASE
				|| currentGameMode == SURVIVAL;
	}
	public static boolean minionReset(){
		return currentGameMode == BASE_VS_BASE
				|| currentGameMode == SURVIVAL;
	}

	
	public static void statReset(PlayerCharacter pc){

		if(Main.gameRuns) return;
		
		if(killReset()) pc.setKills(0);
		if(deathReset()) pc.setDeaths(0);
		if(flagReset()) pc.setFlagsCaptured(0);
		if(towerReset()) pc.setTowerKills(0);
		if(minionReset()) pc.setMinionKills(0);
		if(playerCanLevel()){
			pc.setLevel(1);
			pc.setExp(0);
		}
	}
	
	public static void applyStatistics(PlayerCharacter pc, String trans){
		String[] t;
		try{
			t = trans.split("#");
			pc.setWins(Integer.parseInt(t[0]));
			pc.setFlagsCaptured(Integer.parseInt(t[1]));
			pc.setKills(Integer.parseInt(t[2]));
			pc.setDeaths(Integer.parseInt(t[3]));
			pc.setPing(Integer.parseInt(t[4]));
			pc.setTowerKills(Integer.parseInt(t[5]));
			pc.setMinionKills(Integer.parseInt(t[6]));
		}
		catch(Exception e){
			System.err.println("failed to apply Statistic-Tranform " + trans);
		}
	}
	
	public static String transformStatistics(PlayerCharacter pc){
		return pc.getWins() + "#" + 
				pc.getFlagsCaptured() + "#" + 
				pc.getKills() + "#" + 
				pc.getDeaths() + "#" + 
				pc.getPing() + "#" + 
				pc.getTowerKills() + "#" + 
				pc.getMinionKills();
	}
	
	public static boolean changeMode(int newRuleset){
		if(Main.clock.isEnabled() && Main.gameRuns && Main.mode != Main.CLIENT_MODE){
			JOptionPane.showMessageDialog(null,"You have to pause the Game befor changing the GameMode.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(newRuleset == DOMINATION){
			boolean isOK = false;
			for(GameObject o : Main.getCompList())
				if(o instanceof DominationPoint)
					isOK = true;
			if(!isOK){
				JOptionPane.showMessageDialog(null,"This map does not support Domination-Mode.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if(newRuleset == CAPTURE_THE_FLAG){
			if(Main.getFlagList().size() < 2){
				JOptionPane.showMessageDialog(null,"This map does not support CaptureTheFlag-Mode.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if(newRuleset == BASE_VS_BASE){
			if(Main.getTowerList().size() < 2){
				JOptionPane.showMessageDialog(null,"This map does not support Base-Vs-Base-Mode.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if(newRuleset == SURVIVAL){
			if(Main.getSpawnerList().size() < 1){
				JOptionPane.showMessageDialog(null,"This map does not support Survival-Mode.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		
		
		currentGameMode = newRuleset;
		
		if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/gamemode " + currentGameMode);
		Main.menubar.refreshGameModeItems();
		rearrange();
		
		return true;
	}
	
	public static void calcTeamSize(){
		ArrayList<Integer> list = new ArrayList<>();
		for(Flag f : Main.getFlagList()){
			if(!list.contains(f.team)) list.add(f.team);
		}
		int i = 0;
		int anzTeams = Math.min(Main.getPlayerList().size(), list.size());
		
		
		teams = new int[anzTeams];
		scoreOfTeam = new int[anzTeams];
		lastSpawnPoint = new int[anzTeams];
		for(Integer integ : list){
			if(i >= anzTeams) break;
			teams[i] = integ;
			scoreOfTeam[i] = 0;
			lastSpawnPoint[i] = 0;
			i++;
		}
		if(teams.length == 0 || !isTeamGame()){
			teams = scoreOfTeam = lastSpawnPoint = new int[1];
			scoreOfTeam[0] = teams[0] = lastSpawnPoint[0] = 0;
		}
		if(getAmountTeams() == 1){
			teams = new int[1];
			scoreOfTeam = new int[1];
			lastSpawnPoint = new int[1];
			teams[0] = BLUE;
			scoreOfTeam[0]= lastSpawnPoint[0] = 0;
			return;
		}
		if(getAmountTeams() == 2){
				System.out.println("Team Blue vs Team Red");
				teams = new int[2];
				scoreOfTeam = new int[2];
				lastSpawnPoint = new int[2];
				teams[0] = BLUE;
				teams[1] = RED;
				scoreOfTeam[0] = scoreOfTeam[1] = lastSpawnPoint[0] = lastSpawnPoint[1] = 0;
				return;
		}
	}
	
	public static boolean paintFlag(int team){
		if(currentGameMode != CAPTURE_THE_FLAG) return false;
		for(int i = 0; i < teams.length; i++){
			if(team == teams[i]) return true;
		}
		return false;
	}
	
	public static void rearrange(){
		if(Main.debug) System.out.println("Rearrange.");

		calcTeamSize();
		
		ArrayList<PlayerCharacter> list = Main.getPlayerList();
		ArrayList<SpawnPoint> spawnPoints = Main.getSpawnPointList();
		int anzSpawnPoints = spawnPoints.size();
		int length = list.size();
		int width = Main.mapSize.width;
	
		
		if(length == 0) return;
		else{
			int anzTeams = teams.length;
			//Setze teams
			for(int i = 0; i < length; i++){
				list.get(i).team = teams[i%anzTeams];
				System.out.println("team set to " + getTeamName(teams[i%anzTeams]));
			}
			
			if(isTeamGame() && anzSpawnPoints > 0){
				if(Main.debug) System.out.println("TeamGame + SpawnPoints vorhanden");
				ArrayList<PlayerCharacter> unarranged = new ArrayList<>();
				for(int i = 0; i < teams.length; i++){
					if(rearrangeTeam(teams[i], i)) continue;
					for(PlayerCharacter pc : getTeamPlayerList(i)) unarranged.add(pc);
				}
				for(int i = 0; i < unarranged.size(); i++){
					PlayerCharacter pc = unarranged.get(i);
					pc.setxPos(i*(width-pc.getWidth())/(unarranged.size()-1));
					pc.setyPos(0);
				}
			}
			else if(!isTeamGame() && anzSpawnPoints >= length){
				
				for(int i = 0; i < length; i++){
					PlayerCharacter pc = list.get(i);
					SpawnPoint sp = spawnPoints.get(i);
					pc.setxPos(sp.getX());
					pc.setyPos(sp.getY());
					lastSpawnPoint[0] = i;
				}
			}		
			else if(length == 1){
				list.get(0).setxPos(0);
				list.get(0).team = teams[0];
			}
			else{
				for(int i = 0; i < length; i++){
					PlayerCharacter pc = list.get(i);
					pc.setxPos(i*(width-pc.getWidth())/(length-1));
					pc.setyPos(0);
				}
			}
		}
		if(Main.mode == Main.SERVER_MODE) Main.matchObjectsServerNew();
		Main.canvas.repaint();
	}
	
	public static ArrayList<SpawnPoint> getTeamSpawnList(int team){
		ArrayList<SpawnPoint> res = new ArrayList<>();
		for(SpawnPoint s : Main.getSpawnPointList()){
			if(s.team == team) res.add(s);
		}
		return  	res;
	}
	public static ArrayList<PlayerCharacter> getTeamPlayerList(int team){
		ArrayList<PlayerCharacter> res = new ArrayList<>();
		for(PlayerCharacter s : Main.getPlayerList()){
			if(s.team == team) res.add(s);
		}
		return res;
	}
	
	public static boolean rearrangeTeam(int team, int index){
		ArrayList<PlayerCharacter> pList = getTeamPlayerList(team);
		ArrayList<SpawnPoint> sList = getTeamSpawnList(team);
		int anzSpawnPoints = sList.size();
		if(anzSpawnPoints < 1) return false;
		for(int i = 0; i < pList.size(); i++){
			PlayerCharacter pc = pList.get(i);
			SpawnPoint sp = sList.get(i%anzSpawnPoints);
			pc.setxPos(sp.getX());
			pc.setyPos(sp.getY());
			lastSpawnPoint[index] = i%anzSpawnPoints;
			System.out.println("Spieler " + pc.getName() + " an SpawnPoint " + i + " von Team " + team);
		}
		return true;
	}
	
	public static int getTeamIndex(int team){
		for(int i = 0; i < teams.length; i++){
			if(teams[i] == team) return i;
		}
		return -1;
	}

	
	public static void respawn(PlayerCharacter pc){
		if(!Main.clock.isEnabled()) rearrange();
		else{
			pc.reset();
			int team = pc.team;
			int index = getTeamIndex(team);
			

			ArrayList<SpawnPoint> listSpawnsTeam = getTeamSpawnList(team);
			
			if(isTeamGame() && index != -1 && listSpawnsTeam.size() != 0){
				int lsp = lastSpawnPoint[index];
				lsp = (lsp+1)%listSpawnsTeam.size();
				SpawnPoint sp = listSpawnsTeam.get(lsp);
				pc.setxPos(sp.getX());
				pc.setyPos(sp.getY());
				lastSpawnPoint[index] = lsp;
			}
			else {
				ArrayList<SpawnPoint> listSpawns = Main.getSpawnPointList();
				if(listSpawns.size() >= Main.getPlayerList().size()){
					int lsp = (lastSpawnPoint[0]+1)%listSpawns.size();
					SpawnPoint s = listSpawns.get(lsp);
					pc.setxPos(s.getX());
					pc.setyPos(s.getY());
					lastSpawnPoint[0] = lsp; 
				}
				else{
					pc.setxPos((int)(Math.random()*(Main.mapSize.getWidth()-pc.getWidth())));
					pc.setyPos(0);
				}
			}
		}
		System.out.println("Respawn: " + pc.getName() + " at " + pc.getBounds());
	}

	public static Color getTeamColor(int team, float alpha) {
		switch(team){
		case 0:
			//White
			return new Color(1f, 1f, 1f, alpha);
		case 1:
			//Red
			return new Color(1f, 0f, 0f, alpha);
		case 2:
			//Blue
			return new Color(0f, 0f, 1f, alpha);
		case 3:
			//Green
			return new Color(0f, 1f, 0f, alpha);
		default:
			//White
			return new Color(1f, 1f, 1f, alpha);
		}
	}
	
	public static Color getTeamColorComplement(int team, float alpha) {
		switch(team){
		case 0:
			//White -> Black
			return new Color(0f, 0f, 0f, alpha);
		case 1:
			//Red -> Yellow
			return new Color(0f, 1f, 1f, alpha);
		case 2:
			//Blue
			return new Color(1f, 1f, 0f, alpha);
		case 3:
			//Green
			return new Color(1f, 0f, 1f, alpha);
		default:
			//White
			return new Color(0f, 0f, 0f, alpha);
		}
	}
	
	public static String getTeamName(int team){
		switch(team){
		case 0:
			//White
			return "WHITE";
		case 1:
			//Red
			return "RED";
		case 2:
			//Blue
			return "BLUE";
		case 3:
			//Green
			return "GREEN";
		default:
			return "NOT FOUND";
		}
	}
	
	public static void flagCaptured(Flag flag, PlayerCharacter pc){
		if(currentGameMode != CAPTURE_THE_FLAG) return;
		
		if(pc.team == flag.team){
			System.err.println("Blame the Programmer, he failed");
			return;
		}
		String s = pc.getName() + " captured the " + getTeamName(flag.team)+ " Flag!";;
		if(Main.mode == Main.SERVER_MODE){
			Main.server.sendToAll("/infoMessage "+ s);
		}
		if(Main.mode != Main.CLIENT_MODE){
			Main.canvas.setInfoMessage(s);
		}
			
		pc.increaseFlagsCaptured();
		

		if(Main.mode != Main.CLIENT_MODE) teamScored(pc.team);
		
		flag.reset();
		
		updateRules();
	}
	
	public static String[] getDisplayStats(){
		switch(currentGameMode){
		case CAPTURE_THE_FLAG:
			String[] ctf = {"W", "F", "K", "D", "P"};
			return ctf;
		case BASE_VS_BASE:
			String[] bvb = {"W", "L", "T", "K", "D", "M", "P"};
			return bvb;
		case SURVIVAL:
			String[] surv = {"L", "T", "M", "P"};
			return surv;
		default:
			String[] def ={"W", "K", "D", "P"};
			return def;
			
		}
	}

	
	public static void printStatus(){
		System.out.println("GameRules:");
		System.out.println("CurrentGameMode: " + currentGameMode);
		System.out.println("Is TeamMode: " + isTeamGame());
		if(isTeamGame()){
			System.out.println("TeamSize: " + teams.length);
			String s = "Teams:";
			for(int i : teams){
				s += " " + i;
			}
			System.out.println(s);
			s = "Team Flags::";
			for(int i : scoreOfTeam){
				s += " " + i;
			}
			System.out.println(s);
		}
	}
	
	public static boolean hotJoin(){
		switch(currentGameMode){
		case KILL_COUNT:
		case TEAM_DEATHMATCH:
		case DOMINATION:
			return true;
		default: return false;
		}
	}
	
	public static void killed(PlayerCharacter killer, PlayerCharacter killed){
		killed.setDied(true);
		killed.increaseDeaths();
		if(killer != null){
			killer.increaseKills();
			
			if(currentGameMode == TEAM_DEATHMATCH && Main.mode != Main.CLIENT_MODE){
				teamScored(killer.team);
			}
		}
		
		GameRules.updateRules();
	}
	
	public static void teamScored(int team){
		
		scoreOfTeam[GameRules.getTeamIndex(team)] += 1;
		if(Main.mode == Main.SERVER_MODE) Main.server.sendToAll("/teamScored " + team);

	}
	
	public static void towerDestroyed(Tower t, GameObject killer){
		Character c = null;
		
		if(killer instanceof PlayerCharacter){
			PlayerCharacter pc = ((PlayerCharacter)killer);
			c = pc;
			pc.increaseTowerKills();
			Main.canvas.setInfoMessage(pc.getName() + " destroyed Team " + getTeamName(t.team)+ "'s Tower!" );
		}
		else if(killer instanceof Missile && ((Missile)killer).getOwner() instanceof PlayerCharacter){
			PlayerCharacter pc = (PlayerCharacter) ((Missile)killer).getOwner();
			c = pc;
			pc.increaseTowerKills();
			Main.canvas.setInfoMessage(pc.getName() + " destroyed Team " + getTeamName(t.team)+ "'s Tower!" );
		}
		else if(killer instanceof NonPlayerCharacter){
			Main.canvas.setInfoMessage("An minion destroyed Team " + getTeamName(((NonPlayerCharacter)killer).team) + "s Tower!");
			c = (Character)killer;
		}
		
		if(c != null && playerCanLevel()){
			for(Character chr : Main.getCharacterList()){
				if(chr.team == c.team){
					chr.expEarned(150 + 50 * getNPCLevel());
				}
			}
			c.expEarned(150 + 50 * getNPCLevel());
		}
		if(currentGameMode != BASE_VS_BASE) return;
		
		updateRules();
	}
	
	
	
}

package main;

import interfaces.*;
import mapping.IOMap;
import mapping.MapCreator;
import objects.*;
import objects.Character;

import java.awt.*;

import javax.swing.*;

import ClientServer.CGui;
import ClientServer.SGui;
import GUI.Canvas;

import java.util.ArrayList;
import java.util.Iterator;

public class Main{
	
	public static boolean debug = true;
	public static final int SERVER_MODE = 1;
	public static final int CLIENT_MODE = 2;
	public static final int NORMAL_MODE = 0;
	
	public static int mode = NORMAL_MODE;
	
	public static String name = "Player1";
	
	public static final double GRAVITATION = 0.25;
	
	public static JFrame ui;
	public static GraphicsDevice device;
	public static boolean fullscreen = false;
	
	public static Clock clock;
	public static int actionsPerSecond = 70;
	public static Clock renderClock;
	public static int fps = 60;
	public static Clock serverSyncClock;
	public static int syncsPerSec = 30;
	public static IOMap iomap = new IOMap();
	private static ArrayList<GameObject> compList = new ArrayList<GameObject>();
	public static PlayerCharacter player2;
	private static PlayerCharacter player1;
	

	public static boolean displayFps = true;
	public static long timeForFrame = 1; //ns!!!
	public static long timeForMath = 1; //ns!!!
	
	public static ArrayList<String> map = new ArrayList<>();
	
	public static Dimension mapSize;
	
	
	
	public static Canvas canvas;
	
	public static MenueBar menubar;
	
	public static Listener listener = new Listener();
	
	public static SGui server = null;
	public static CGui client = null;
	
	public static boolean gameRuns = true;
	
	
	public static MapCreator mapCreator = null;
	
	
	public static final int BIRD_PERSPECTIVE_MODE = 0;
	public static final int WORM_PERSPECTIVE_MODE = 1;
	public static int perspective = WORM_PERSPECTIVE_MODE;
	
	public static int idCounter = 0;
	

	
	public static void main(String[] args){
		
		for(String arg : args){
			try{
				String[] s = arg.split("=");
				if(s.length != 2) continue;
				
				switch(s[0]){
				case "debug":
					debug = Boolean.parseBoolean(s[1]);
					break;
				case "name":
					if(nameOK(s[1])) name = s[1];
					break;
				}
			}
			catch(Exception e){
				System.err.println("Invalid argument: " + arg);
				e.printStackTrace();
			}
		}
		
		if(debug){
			controlls();
			System.out.println();
			System.out.println("Start Programm.");
			System.out.println();
			System.out.println("Init UI");
		}
		
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { 
        	e.printStackTrace(); 
        }
        

		if(debug) System.out.println("Init Clock");
		initClock();
		
		initUI();
		
		mapSize = new Dimension(canvas.getSize());
		
		if(debug) System.out.println("Init Megaman.");
		initPlayerCharacter(compList);
		
		if(debug) System.out.println("Init Rects");
		initStatics(compList);
		refreshLists();
		if(debug) System.out.println("Start Clock.");
		renderClock.start();
		clock.start();
	}
	
	private static long oldFPSTime = 0;
	
	private static void initClock() {
		clock = new Clock(1000 / actionsPerSecond){
			public void event(){
				long t1 = 0;
				long t2 = 0;
				if(displayFps) t1 = System.nanoTime();
				refreshLists();
				deleteUnusedObjects();
				if(movementAllowed && gameRuns){
					Main.move();
					updateList();
					GameRules.currentGameTime++;
				}
				
				
				if(displayFps){
					t2 = System.nanoTime();
					long timePerFrame = (t2-t1);
					//if(debug) System.out.println("Time per Math: " + timePerFrame);
					if(timePerFrame < 1) return;
					else timeForMath = timePerFrame;
				}
			}

			public void interruptedExc(InterruptedException e){
				System.out.println("Clock interrupted");
			}
		};
		
		renderClock = new Clock((int) getMillisPerFrame()){
			public void event(){
				
				canvas.repaint();
				
				if(displayFps){
					long t = System.nanoTime();
					long timePerFrame = (t-oldFPSTime);
					//if(debug) System.out.println("Time per Frame: " + timePerFrame);
					if(timePerFrame < 1) return;
					timeForFrame = timePerFrame;
					oldFPSTime = t;
				}
			}
			public void interruptedExc(InterruptedException e){
				System.out.println("Renderer Interrupted");
			}
		};
		System.out.println("ms per Frame: " + getMillisPerFrame());
	}
	
	public static int getMillisPerFrame(){
		return 1000/fps;
	}
	
	public static int getNextImageCounterWhen(){
		int counter = fps/20;
		if (counter == 0) counter = 1;
		return counter;
	}
	
	public static int eventCounter = 0;
	
	public static long oldTime = 0;
	
	public static void event(){
		if(mode == CLIENT_MODE) client.send("/event");
//		long newTime = System.nanoTime();
//		double speed = Math.abs((oldTime-newTime)/1000000000);
//		if(speed > 2 || speed < 0.5){
//			oldTime = newTime;
//			return;
//		}
//		clock.setSpeed(speed);
//		oldTime = newTime;
		//if(debug)System.out.println("Speed = " + speed);
		
	}
	
	public static boolean movementAllowed = true;
	
	private static void currentlySwitched() {
		switchModeCounter++;
		switch(switchModeCounter){
		case 1:
			server.sendToAll("/displayMessage 3!");
			canvas.displayMessage("3!");
			break;
		case 2:
			server.sendToAll("/displayMessage 2!");
			canvas.displayMessage("2!");
			break;
		case 3:
			server.sendToAll("/displayMessage 1!");
			canvas.displayMessage("1!");
			break;
		case 4:
			server.sendToAll("/displayMessage GO!");
			server.sendToAll("/movementAllowed true");
			canvas.displayMessage("GO!");
			movementAllowed = true;
			
			break;
		case 5:
			currentlySwitched = false;
			switchModeCounter = 0;
			canvas.stopDisplayMessage();
			server.sendToAll("/stopDisplayMessage");
			break;
		}
		
	}

	private static void initPlayerCharacter(ArrayList<GameObject> newCompList) {
		String type = "";
		if(player1 != null) type = player1.getType();
		switch(type){
		case "Sakuyamon":
			player1 = new Sakuyamon();
			break;
		case "Zero":
			player1 = new Zero();
			break;
		case "Megaman":
			player1 = new Megaman();
			break;
		case "Wizzy":
			player1 = new Wizzy();
			break;
		default:
			if(debug && !type.equals("")) System.err.println("Unknown type: " + type);
			player1 = new Megaman();
		}
		switch(mode){
		case NORMAL_MODE:
			player1.setName(name);
			player2 = new Sakuyamon(canvas.getX()+canvas.getWidth()-Megaman.WIDTH, canvas.getY());
			player2.setName("Player2");
			newCompList.add(player2);
			break;
		case SERVER_MODE:
			player1.setName(name);
		case CLIENT_MODE:
			//doNothing
		}
		
		newCompList.add(player1);
		
	}

	private static void controlls(){
		System.out.println("Controlls:");
		System.out.println("DebugInfo: f");
		System.out.println("Pause Game: p");
		System.out.println("Restart Game: F5");
		System.out.println("Fullscreen: F11");
		System.out.println();
		System.out.println("Contolls Player 1 & Online:");
		System.out.println("Walk: WASD");
		System.out.println("Jump: SPACE");
		System.out.println("Attack: < (Online right and left MouseKeys");
		System.out.println();
		System.out.println("Player 2 (Offline):");
		System.out.println("Walk: ArrowKeys");
		System.out.println("Jump: NUMPAD_0");
		System.out.println("Attack: NUMPAD_1");
	}
	
	private static void initUI(){
		ui = new JFrame("Megaman"){
			private static final long serialVersionUID = 8620900696432559397L;
			{
				this.setSize(1000,850);
				this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				this.setResizable(true);
				
		        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		        
		        if (fullscreen && device.isFullScreenSupported()){
		            this.setUndecorated(true);
		            device.setFullScreenWindow(this);
		        }
		        
				canvas = new Canvas();
				menubar = new MenueBar();
				this.add(canvas);
				this.setJMenuBar(menubar);
				
				this.setVisible(true);		
			}
		};
		ui.addKeyListener(listener);
		canvas.addMouseListener(listener);	
	}
	
	private static void initStatics(ArrayList<GameObject> newCompList){
		ArrayList<StaticObject> oldList = getStaticList();
		if(oldList.size() <= 0){
			map = new ArrayList<>();
			compList.add(new Rect(150,650,700,50));
			compList.add(new Rect(200,500,200,50));
			compList.add(new Rect(600,500,200,50));
			compList.add(new Rect(0,275,200,50));
			compList.add(new Rect(800,275,200,50));
			compList.add(new Rect(300,100,400,50));
			compList.add(new Rect(475,275,50,200));
		}
		else for(StaticObject so : oldList) {
			so.reset();
			newCompList.add(so);
		}
		
		for(StaticObject s : getStaticList()){
			map.add("/newObj " + s.id + " " + s.transformToString());
		}
		refreshLists = true;
	}
	
	private static boolean refreshLists = true;
	
	public static void refreshLists(){
		if(refreshLists){
			setCollisionList();
			setMoveableList();
			setPlayerList();
			setCharacterList();
			setRectList();
			setMissileList();
			setStaticList();
			setUpdateList();
			setSpawnPointList();
			setPickUpItemList();
			setFlagList();
			setSpawnerList();
			setSyncList();
			setNPCList();
			setTowerList();
			setResetableList();
			refreshLists = false;
		}
	}
	
	private static ArrayList<ICollision> collisionList = new ArrayList<>();
	private static ArrayList<Moveable> moveableList = new ArrayList<>();
	private static ArrayList<PlayerCharacter> playerList = new ArrayList<>();
	private static ArrayList<Character> characterList = new ArrayList<>();
	private static ArrayList<Rect> rectList = new ArrayList<>();
	private static ArrayList<Missile> missileList = new ArrayList<>();
	private static ArrayList<StaticObject> staticList = new ArrayList<>();
	private static ArrayList<NeedsUpdate> updateList = new ArrayList<>();
	private static ArrayList<SpawnPoint> spawnPointList = new ArrayList<>();
	private static ArrayList<PickUpItem> pickUpItemList = new ArrayList<>();
	private static ArrayList<Flag> flagList = new ArrayList<>();
	private static ArrayList<Spawner> spawnerList = new ArrayList<>();
	private static ArrayList<NeedsSync> syncList = new ArrayList<>();
	private static ArrayList<NonPlayerCharacter> npcList = new ArrayList<>();
	private static ArrayList<Tower> towerList = new ArrayList<>();
	private static ArrayList<Resetable> resetableList = new ArrayList<>();
	

	
	public static ArrayList<GameObject> getCompList(){
		return new ArrayList<GameObject>(compList);
	}
	
	public static ArrayList<ICollision> getCollisionList() {
		return collisionList;
	}

	public static ArrayList<Moveable> getMoveableList() {
		return moveableList;
	}

	public static ArrayList<PlayerCharacter> getPlayerList() {
		return playerList;
	}

	public static ArrayList<Character> getCharacterList() {
		return characterList;
	}

	public static ArrayList<Rect> getRectList() {
		return rectList;
	}

	public static ArrayList<Missile> getMissileList() {
		return missileList;
	}

	public static ArrayList<StaticObject> getStaticList() {
		return staticList;
	}

	public static ArrayList<NeedsUpdate> getUpdateList() {
		return updateList;
	}

	public static ArrayList<SpawnPoint> getSpawnPointList() {
		return spawnPointList;
	}

	public static ArrayList<PickUpItem> getPickUpItemList() {
		return pickUpItemList;
	}

	public static ArrayList<Flag> getFlagList() {
		return flagList;
	}

	public static ArrayList<Spawner> getSpawnerList() {
		return spawnerList;
	}

	public static ArrayList<NeedsSync> getSyncList() {
		return syncList;
	}

	public static ArrayList<NonPlayerCharacter> getNPCList() {
		return npcList;
	}

	public static ArrayList<Tower> getTowerList() {
		return towerList;
	}

	public static ArrayList<Resetable> getResetableList() {
		return resetableList;
	}
	/*
	public static <E> ArrayList<E> refreshList(ArrayList<E> list){
		ArrayList<E> res = new ArrayList<E>();
		for(GameObject o : getCompList()){
			if(o instanceof E) res.add((E)o);
		}
		return res;
	}
*/
	
	public static void setCollisionList(){
		ArrayList<ICollision> res = new ArrayList<ICollision>();
		for(GameObject o : getCompList()){
			if(o instanceof ICollision){
				res.add((ICollision)o);
			}
		}
		collisionList = res;
	}
	
	public static void setMoveableList(){
		ArrayList<Moveable> res = new ArrayList<Moveable>();
		for(GameObject o : getCompList()){
			if(o instanceof Moveable){
				res.add((Moveable)o);
			}
		}
		moveableList = res;

	}

	
	public static void setPlayerList(){
		ArrayList<PlayerCharacter> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof PlayerCharacter)
				res.add((PlayerCharacter)o);
		}
		playerList = res;
	}
	
	public static void setCharacterList(){
		ArrayList<Character> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Character)
				res.add((Character)o);
		}
		characterList = res;
	}
	
	public static void setRectList(){
		ArrayList<Rect> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Rect){
				res.add((Rect)o);
			}
		}
		rectList = res;
	}
	
	public static void setStaticList(){
		ArrayList<StaticObject> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof StaticObject){
				res.add((StaticObject)o);
			}
		}
		staticList = res;
	}
	
	public static void setMissileList(){
		ArrayList<Missile> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Missile){
				res.add((Missile)o);
			}
		}
		missileList = res;
	}
	
	public static void setUpdateList(){
		ArrayList<NeedsUpdate> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof NeedsUpdate){
				res.add((NeedsUpdate)o);
			}
		}
		updateList = res;
	}
	
	public static void setSpawnPointList(){
		ArrayList<SpawnPoint> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof SpawnPoint){
				res.add((SpawnPoint)o);
			}
		}
		spawnPointList = res;
	}
	
	public static void setPickUpItemList(){
		ArrayList<PickUpItem> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof PickUpItem){
				res.add((PickUpItem)o);
			}
		}
		pickUpItemList = res;
	}
	
	public static void setFlagList(){
		ArrayList<Flag> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Flag){
				res.add((Flag)o);
			}
		}
		flagList = res;
	}
	public static void setSpawnerList(){
		ArrayList<Spawner> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Spawner){
				res.add((Spawner)o);
			}
		}
		spawnerList = res;
	}
	public static void setSyncList(){
		ArrayList<NeedsSync> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof NeedsSync){
				res.add((NeedsSync)o);
			}
		}
		syncList = res;
	}
	public static void setNPCList(){
		ArrayList<NonPlayerCharacter> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof NonPlayerCharacter){
				res.add((NonPlayerCharacter)o);
			}
		}
		npcList = res;
	}
	public static void setTowerList(){
		ArrayList<Tower> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Tower){
				res.add((Tower)o);
			}
		}
		towerList = res;
	}
	public static void setResetableList(){
		ArrayList<Resetable> res = new ArrayList<>();
		for(GameObject o : getCompList()){
			if(o instanceof Resetable){
				res.add((Resetable)o);
			}
		}
		resetableList = res;
	}
	
	public static void updateList(){
		for(NeedsUpdate nu : getUpdateList()){
			nu.update();
		}
	}
	
	
	public static void addCompList(GameObject o){
		compList.add(o);
		refreshLists = true;
		refreshLists();
	}

	public static void deleteUnusedObjects(){
		try{
			for(Iterator<GameObject> it = compList.iterator(); it.hasNext();){
				GameObject o = it.next();
				if(o.deleteThisToken){				
					it.remove();
				}
			}
			refreshLists = true;
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	

	public synchronized static void move(){
		ArrayList<ICollision> colList = getCollisionList();
		for(Moveable m : getMoveableList()){
			m.move(mapSize, colList);
		}
	}
	
	
	public static void reload(){
		clock.pause();
		movementAllowed = false;
		canvas.stopDisplayMessage();
		ArrayList<GameObject> newCompList = new ArrayList<GameObject>();
		map = new ArrayList<>();
		if(mode != CLIENT_MODE){
			initPlayerCharacter(newCompList);
			initStatics(newCompList);
		}
		
		compList = newCompList;
		
		refreshLists = true;
		refreshLists();
		
		if(mode == NORMAL_MODE){
			clock.restart();
			movementAllowed = true;
			gameRuns = true;
		}
		canvas.repaint();
	}
	
	public static void restartNormalGame(){
		System.out.println("Restart Normal Game");
		clock.pause();
		movementAllowed = false;
		canvas.stopDisplayMessage();
		resetObjects();
		ArrayList<PlayerCharacter> oldPlayer = getPlayerList();
		ArrayList<GameObject> newCompList = new ArrayList<>();
		for(PlayerCharacter pc : oldPlayer){
			newCompList.add(pc);
		}
		initStatics(newCompList);
		compList = newCompList;
		
		refreshLists = true;
		refreshLists();
		
		GameRules.currentGameTime = 0;
		GameRules.rearrange();
		canvas.repaint();
		switchMode();
		
		movementAllowed = true;
		gameRuns = true;
	}
	
	public static void restartServerGame(){
		clock.pause();
		movementAllowed = false;
		server.sendToAll("/restartGame");
		canvas.stopDisplayMessage();
		resetObjects();
		ArrayList<PlayerCharacter> oldPlayer = getPlayerList();
		ArrayList<GameObject> newCompList = new ArrayList<>();
		for(PlayerCharacter pc : oldPlayer){
			newCompList.add(pc);
		}
		initStatics(newCompList);
		compList = newCompList;
		
		refreshLists = true;
		refreshLists();
		
		GameRules.currentGameTime = 0;
		GameRules.rearrange();
		canvas.repaint();
		syncIDsNew();
		matchObjectsServerNew();
		switchMode();

		gameRuns = true;
	}
	
	public static void restartClientGame(){
		clock.pause();
		movementAllowed = false;
		canvas.stopDisplayMessage();
		resetObjects();
		ArrayList<PlayerCharacter> oldPlayer = getPlayerList();
		ArrayList<GameObject> newCompList = new ArrayList<>();
		for(PlayerCharacter pc : oldPlayer){
			newCompList.add(pc);
		}
		initStatics(newCompList);
		compList = newCompList;

		refreshLists = true;
		refreshLists();
		
		GameRules.currentGameTime = 0;
		canvas.repaint();

		gameRuns = true;
	}
	
	public static void startServer(){
		try{
			server = new SGui(55555);
			
			gameRuns = false;
			mode = SERVER_MODE;
			initServerClock();
			reload();
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null,"Failed to start Server.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}
	
	private static void initServerClock(){
		serverSyncClock = new Clock(1000 / syncsPerSec){
			public void event(){
				if(mode == SERVER_MODE){
					eventCounter++;
					if(eventCounter > syncsPerSec){
						server.sendToAll("/event");
						oldTime = System.nanoTime();
						eventCounter = 0;
						if(currentlySwitched) currentlySwitched();
					}
					
					matchObjectsServerNew();
				}
			}
			public void interruptedExc(InterruptedException e){
				System.err.println("ServerClock Interrupted");
				e.printStackTrace();
			}
		};
		serverSyncClock.start();
	}
	
	public static void enterNameDialog(){
		try{
			String newName = JOptionPane.showInputDialog("Geben Sie bitte ihren Namen an");
			if(!renamePlayer(name, newName)) enterNameDialog();
		}
		catch(Exception e){
			if(debug) System.err.println("Wrong name");
			enterNameDialog();
		}
	}
	
	public static void connect(){
		try{
			String ip = JOptionPane.showInputDialog("Geben sie bitte die IP an.");
			if(ip == null || ip.equals("")) return;
			client = new CGui(ip, 55555);
			clock.setEnabled(false);
			mode = CLIENT_MODE;
			reload();
			client.send("/getInfos");

			client.send("/setName "+ Main.name + " " + Main.menubar.character);
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Failed to connect to Server (" + e.getClass() + ").", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	public static void processAction(String action, String name) {
		PlayerCharacter pc = getPlayer(name);
		if(pc == null){
			System.err.println("Player unknown: "+name+ " with Action "+ action);
			return;
		}
		
		if(!pc.isAlive()) return;
		
		switch(action){
		case "jump":
			pc.jump();
			break;
		case "turnRight":
			pc.turnRight();
			break;
		case "turnLeft":
			pc.turnLeft();
			break;
		case "stopTurnRight":
			pc.stopTurnRight();
			break;
		case "stopTurnLeft":
			pc.stopTurnLeft();
			break;
		case "attack":
			pc.attack();
			break;
		case "up":
			pc.up();
			break;
		case "down":
			pc.down();
			break;
		case "stopUp":
			pc.stopUp();
			break;
		case "stopDown":
			pc.stopDown();
			break;
		default:
			System.out.println("Action unknown: " + action);
		}
		if(mode == SERVER_MODE){
			server.sendToAll("/action " + name + " " + action);
		}
	}
	
	public static void newPlayer(String name, String transform, String id){
		
		PlayerCharacter pc = (PlayerCharacter) PlayerCharacter.createFromTransform(transform);
		pc.setName(name);
		if(id.equals("NOID")){
			pc.id = idCounter;
			idCounter++;
		}
		else{
			pc.id = Integer.parseInt(id);
		}
		
		if(!GameRules.hotJoin() && gameRuns) pc.setCanSpawn(false);
		addCompList(pc);
		if(mode == SERVER_MODE && server != null){
			
			if(pc.getCanSpawn()) GameRules.respawn(pc);
			
			server.sendToAll("/newPlayer " + name + " " + pc.id + " " + transform);
			matchObjectsServerNew();
		}
		canvas.repaint();
	}


	public static boolean renamePlayer(String oldName, String newName) {
		

		if(debug) System.out.println("Rename Player \"" + oldName + "\" to \"" + newName+ "\".");
		
		if(oldName.equals(newName)) return true;

		PlayerCharacter pc = getPlayer(oldName);
		
		if(nameOK(newName)){
			if(oldName.equals(name)) name = newName;
			pc.setName(newName);
			if(mode == SERVER_MODE) server.sendToAll("/rename " + oldName + " "+ newName);
			if(debug) System.out.println("Renaming succesful.");
			canvas.repaint();
			return true;
		}
		if(debug) System.out.println("Renaming failed");
		return false;
	}

	public static void closeServer() {
		server.sendToAll("/close");
		server.ui.dispose();
		try{
			serverSyncClock.interrupt();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		serverSyncClock = null;
		server.close();
		server = null;
		mode = NORMAL_MODE;
		menubar.refreshOnlineItems();
		reload();
	}
	
	public static void closeClient(){
		client.close();
	}
	
//	public static void matchPlayerCharactersNew(){
//		if(mode != SERVER_MODE) return;
//		String listOfTransforms = "/matchPlayerCharactersNew";
//		for(PlayerCharacter pc : getPlayerList()){
//			listOfTransforms += " " + pc.getName() + " " + pc.transformToString();
//		}
//		server.sendToAll(listOfTransforms);
//	}
//	
//	public static void matchPlayerCharacter(String name, String transform){
//		if(mode != CLIENT_MODE) return;
//		for(PlayerCharacter pc : getPlayerList()){
//			if(pc.getName().equals(name)){
//				pc.applyString(transform);
//				return;
//			}
//		}
//	}
//	
//	public static void matchObjectsServer(){
//		if(mode != SERVER_MODE) return;
//		String listOfTransforms = "/matchObjects";
//		for(Character c : getCharacterList()){
//			listOfTransforms += " " + c.id + " " + c.transformToString();
//		}
//		for(PickUpItem p : getPickUpItemList()){
//			listOfTransforms += " " + p.id + " " + p.transformToString();
//		}
//		server.sendToAll(listOfTransforms);
//	}
	public static void matchObjectsServerNew(){
		if(mode != SERVER_MODE) return;
		String listOfTransforms = "/matchObjects";
		for(NeedsSync ns : getSyncList()){
			listOfTransforms += " " + ((GameObject)ns).id + " " + ns.transformToString();
		}
		server.sendToAll(listOfTransforms);
	}
	
//	public static void matchObjects(String ident, String transform){
//		if(mode != CLIENT_MODE) return;
//		int id = Integer.parseInt(ident);
//		
//		for(GameObject g : getCompList()){
//			if(g.id == id){
//				if(g instanceof PickUpItem){
//					((PickUpItem)g).applyString(transform);
//					return;
//				}
//				if(g instanceof Character){
//					((Character)g).applyString(transform);
//					return;
//				}
//				System.err.println("Failed to match ID " + ident +". Belongs to: " + transform);
//			}
//		}
//		System.err.println("ID " + ident + " not found: " + transform);
//	}
	public static void matchObjectsNew(String ident, String transform){
		if(mode != CLIENT_MODE) return;
		int id = Integer.parseInt(ident);
		
		for(NeedsSync g : getSyncList()){
			if(((GameObject)g).id == id){
				g.applyString(transform);
				return;
			}
		}
		if(debug) System.err.println("ID " + ident + " not found: " + transform);
	}
	
	
//	public static void matchObjects(String listoftransforms){
//		if(mode != CLIENT_MODE) return;
//		try{
//			String[] s = listoftransforms.split(" ");
//			if(s.length % 2 != 1)throw new Exception("wrong length: " + s.length);
//			int anzTransforms = s.length/2;
//			int anzObj = getCharacterList().size() + getPickUpItemList().size();
//			
//			if(anzTransforms != anzObj){
//				System.err.println("wrong length: anzTransforms = " + anzTransforms + " & anzObj: " + anzObj);
//			}
//			for(int i = 1; i+1 < s.length; i+=2){
//				matchObjects(s[i], s[i+1]);
//			}
//		}
//		catch(Exception e){
//			if(Main.debug) System.err.println("Failed to match Objects.");
//			e.printStackTrace();
//		}
//	}
	public static void matchObjectsNew(String listoftransforms){
		if(mode != CLIENT_MODE) return;
		try{
			String[] s = listoftransforms.split(" ");
			if(s.length % 2 != 1)throw new Exception("wrong length: " + s.length);
			int anzTransforms = s.length/2;
			int anzObj = getSyncList().size();
			
			if(anzTransforms != anzObj && debug){
				System.err.println("wrong length: anzTransforms = " + anzTransforms + " & anzObj: " + anzObj);
			}
			for(int i = 1; i+1 < s.length; i+=2){
				matchObjectsNew(s[i], s[i+1]);
			}
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to match Objects.");
			e.printStackTrace();
		}
	}
	
//	public static void matchPlayerCharacterNew(String listOfTransforms){
//		if(mode != CLIENT_MODE) return;
//		try{
//			String[] s = listOfTransforms.split(" ");
//			if(s.length % 2 != 1) throw new Exception("wrong length");
//			int anzTransforms = s.length / 2;
//			int anzPlayer = getPlayerList().size();
//			/*if(debug){
//				System.out.println("Anzahl zu matchender Spieler: " + anzTransforms);
//				System.out.println("Anzahl bekannter Spieler: " + anzPlayer);
//			}
//			*/
//			if(anzTransforms != anzPlayer) return;
//			for(int i = 1; i+1 < s.length; i+=2){
//				matchPlayerCharacter(s[i], s[i+1]);
//			}
//		}
//		catch(Exception e){
//			if(Main.debug) System.err.println("Failed to match Characters.");
//		}
//	}

	public static void sendCurrentState(String ip, int port) {
		if(mode != SERVER_MODE) return;
		for(PlayerCharacter pc : getPlayerList()){
			server.send(ip, port, "/newPlayer " + pc.getName() + " " + pc.id + " " + pc.transformToString());
		}
		for(NonPlayerCharacter npc : getNPCList()){
			server.send(ip, port, "/spawn " + npc.id + " " + npc.transformToString());
		}
	}

	public static int switchModeCounter = 0;
	public static boolean currentlySwitched = false;
	
	public static void switchMode() {
		if(mode == CLIENT_MODE) return;
		Main.clock.switchMode();
		if(Main.mode == Main.SERVER_MODE){
			Main.server.sendToAll("/switchMode " + clock.isEnabled());
			Main.serverSyncClock.setEnabled(clock.isEnabled());
		}
		currentlySwitched = true;
		if(!clock.isEnabled() && mode == SERVER_MODE){
			movementAllowed = false;
			server.sendToAll("/movementAllowed false");
		}
	}
	
	public static void resetObjects(){
		for(Resetable r : getResetableList()){
			r.reset();
//			GameRules.statReset(r);
		}
	}

	public static void deletePlayer(String name) {
		if(mode == NORMAL_MODE) return;
		PlayerCharacter pc = getPlayer(name);
		if(pc == null){
			System.out.println("Player " + name + " does not exist.");
			return;
		}
		pc.deleteThisToken = true;
		if(mode == SERVER_MODE) server.sendToAll("/deletePlayer " + name);
		deleteUnusedObjects();
		if(!clock.isEnabled()) canvas.repaint();
	}
	
	public static PlayerCharacter getPlayer(String name){
		for(PlayerCharacter pc : getPlayerList()){
			if(pc.getName().equals(name)) return pc;
		}
		return null;
	}



	public static void attackWithDirection(String name, String xspeed,
			String yspeed, String attack) {
		try{
			double xSpeed = Double.parseDouble(xspeed);
			double ySpeed = Double.parseDouble(yspeed);
			int a = Integer.parseInt(attack);
			getPlayer(name).attack(xSpeed, ySpeed, a);
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to Attack");
		}
	}



	public static void newObj(String id, String line) {
		StaticObject newObj;
		try{
			newObj = StaticObject.newObjFromString(line);
			if(newObj == null) throw new Exception("newObj == null");
			newObj.id = Integer.parseInt(id);
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to create new StaticObject");
			e.printStackTrace();
			return;
		}
		addCompList(newObj);
		map.add("/newObj " + id + " " + line);
		if(mode == SERVER_MODE) server.sendToAll("/newObj "+ id + " " + line);
		canvas.repaint();
	}
	
	public static void deleteObj(String line){
		StaticObject newObj;
		try{
			newObj = StaticObject.newObjFromString(line);
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to delete Object");
			return;
		}
		for(StaticObject so : getStaticList()){
			if(so.getBounds().equals(newObj.getBounds())){
				if(mode == SERVER_MODE) server.sendToAll("/deleteObj " + line);
				
				if(map.contains("/newObj " + so.id + " " + line)) map.remove("/newObj "+ so.id+ " " + line);
				
				so.deleteThisToken = true;
				deleteUnusedObjects();
				canvas.repaint();
				return;
			}
		}
	}
	
	public static void deleteObj(int id){
		for(GameObject o : getCompList()){
			if(o.id == id) o.deleteThisToken = true;
		}
		deleteUnusedObjects();
	}
	


	public static void sendMap(String pClientIP, int pClientPort) {
		if(mode != SERVER_MODE) return;
//		server.send(pClientIP, pClientPort, "/newMap");
		server.send(pClientIP, pClientPort, "/newResolution " + mapSize.width + " " + mapSize.height);
		for(String s : map){
			server.send(pClientIP, pClientPort, s);
		}
		server.send(pClientIP, pClientPort, "/gamemode " + GameRules.currentGameMode);
	}
	
	public static void sendMapToAll() {
		if(mode != SERVER_MODE) return;
		server.sendToAll("/newMap");
		server.sendToAll("/newResolution " + mapSize.width + " " + mapSize.height);
		for(String s : map){
			server.sendToAll(s);
		}
	}
	
	public static void normalMode(){
		if(mode == SERVER_MODE){
			closeServer();
		}
		else if(mode == CLIENT_MODE){
			closeClient();
		}

		movementAllowed = true;
	}
	
	public static void reloadMap(int w, int h, ArrayList<StaticObject> list) {

//		if(mode == SERVER_MODE){
//			server.sendToAll("/newMap");
//		}
		
		for(StaticObject so : getStaticList()) so.deleteThisToken = true;
		deleteUnusedObjects();
		
		map = new ArrayList<>();
		
		for(StaticObject r : list){
			addCompList(r);
			map.add("/newObj " + r.id + " " + r.transformToString());
		}
		
		mapSize.width = w;
		mapSize.height = h;
		
		if(mode == SERVER_MODE){
			sendMapToAll();
		}
		
		canvas.repaint();

	}


	public static PlayerCharacter getCurrentPlayer(){
		return getPlayer(name);
	}
	
	public static void computePing(String playerName){
		PlayerCharacter pc = getPlayer(playerName);
		if(pc == null) return;
		long oT = oldTime;
		long nT = System.nanoTime();
		
		long diff = nT - oT;
		
		int ping = (int) diff / 2000000; //diff in µs in ms & 1/2
		//if(debug) System.out.println(playerName + " hat Ping von: " + ping);
		pc.setPing(ping);
	}
	
	public static void changeCharacter(String type, String name){
		
		PlayerCharacter old = getPlayer(name);
		
		if(gameRuns && clock.isEnabled() && old.getCanSpawn()){
			JOptionPane.showMessageDialog(null, "You need to pause the game bevor changing your character!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String oldTrans = old.transformToString();
		String newTrans = type;
		try{
			String[] t = oldTrans.split("_");
			
			//CharacterSpecificTransform:
			newTrans += "_StandardValues";
			for(int i = 2; i < t.length; i++){
				newTrans += "_" + t[i];
			}
		}
		catch(Exception e){
			if(debug) System.err.println("Failed to change Character");
			return;
		}
		PlayerCharacter newPc = (PlayerCharacter) PlayerCharacter.createFromTransform(newTrans);
		if(newPc == null){
			if(debug) System.err.println("Failed to change Character: new PC == null");
			return;
		}
		newPc.setName(name);
		newPc.setCanSpawn(old.getCanSpawn());
		newPc.id = old.id;
		old.deleteThisToken = true;
		deleteUnusedObjects();
		addCompList(newPc);
		if(name.equals(Main.name)) player1 = newPc;
		if(mode == SERVER_MODE){
			server.sendToAll("/changeCharacter " + name + " " + type);
		}
		
	}
	
	public static void endGame(){
		gameRuns = false;
	}
	
	public static void win(String win){
		if(mode != CLIENT_MODE) return;
		
		if(GameRules.isTeamGame()){	
			GameRules.messageWinner(Integer.parseInt(win));
		}
		else{
			GameRules.messageWinner(win);
		}
		endGame();
	}
	
	public static void printStatus(){
		System.out.println("Status:");
		System.out.println("Debug = " + debug);
		System.out.println("Mode = " + mode);
		System.out.println("Name = " + name);
		System.out.println("Fullscreen = " + fullscreen);
		System.out.println("Clock: " + clock);
		System.out.println("Renderer: " + renderClock);
		System.out.println("Map: Mapsize = " + mapSize);
		System.out.println("GameRuns = " + gameRuns);
		System.out.println("Movement Allowed = " + movementAllowed);
		System.out.println("Currently Switched = " + currentlySwitched);
		System.out.println();
		System.out.println("All Player:");
		for(PlayerCharacter pc : getPlayerList()) System.out.println(pc);
		System.out.println();
		GameRules.printStatus();
		
		System.out.println("List of IDs:");
		for(GameObject g : getCompList()) System.out.println("ID *" + g.id + "*: " + g.transformToString());
	}

	
	public static void newMap(){
		if(mode != CLIENT_MODE) return;
		for(StaticObject s : getStaticList()) s.deleteThisToken = true;
		deleteUnusedObjects();
	}
	
	public static void deleteItemAt(Point clicked){
		if(mode == CLIENT_MODE) return;
		for(GameObject go : getCompList()){
			if(go.getBounds().contains(clicked.x, clicked.y)){
				deleteObj(go.transformToString());
				return;
			}
		}
		if(Main.debug)System.out.println("No Object found");
	}
	
	public static void changePerspective(int newOne){
		if(newOne == perspective) return;
		if(mode == CLIENT_MODE){
			JOptionPane.showMessageDialog(null, "Only the Host is able to change the perspective.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(gameRuns && clock.isEnabled()){
			JOptionPane.showMessageDialog(null, "You need to pause the game bevor changing the perspective!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		perspective = newOne;
		if(mode == SERVER_MODE) server.sendToAll("/perspective " + perspective);
	}
	
//	public static void syncIDs(){
//		if(mode==SERVER_MODE) server.sendToAll(""+idCounter);
//		syncPlayerIDs();
//		syncPickUpItemIDs();
//	}
	
	public static void syncIDsNew(){
		if(mode==SERVER_MODE) server.sendToAll("/syncIDCounter "+idCounter);
		syncPlayerIDs();
		syncStaticsIDs();
	}
	
	public static void syncPlayerIDs(){
		if(mode != SERVER_MODE) return;
		
		String s = "/syncPlayerIDs";
		for(PlayerCharacter p : getPlayerList()){
			s += " " + p.getName() + " " + p.id;
		}
		server.sendToAll( s);
	}
	
//	public static void syncPickUpItemIDs(){
//		if(mode != SERVER_MODE) return;
//		
//		String s = "/syncPickUpItemIDs";
//		for(PickUpItem p : getPickUpItemList()){
//			s += " " + p.getX() + " " + p.getY() + " " + p.id;
//		}
//		server.sendToAll(s);
//	}
	
	public static void syncStaticsIDs(){
		if(mode != SERVER_MODE) return;
		
		String s = "/syncStaticIDs";
		for(StaticObject p : getStaticList()){
			s += " " + p.getX() + " " + p.getY() + " " + p.id;
		}
		server.sendToAll(s);
	}
	
	
	public static void syncIDs(String pClientIP, int pClientPort){
		if(mode==SERVER_MODE) server.send(pClientIP, pClientPort, "/syncIDCounter "+idCounter);
		syncPlayerIDs(pClientIP, pClientPort);
		syncStaticsIDs(pClientIP, pClientPort);
	}
	
	public static void syncPlayerIDs(String pClientIP, int pClientPort){
		if(mode != SERVER_MODE) return;
		
		String s = "/syncPlayerIDs";
		for(PlayerCharacter p : getPlayerList()){
			s += " " + p.getName() + " " + p.id;
		}
		server.send(pClientIP, pClientPort, s);
	}
	
	public static void syncStaticsIDs(String pClientIP, int pClientPort){
		if(mode != SERVER_MODE) return;
		
		String s = "/syncStaticIDs";
		for(StaticObject p : getStaticList()){
			s += " " + p.getX() + " " + p.getY() + " " + p.id;
		}
		server.send(pClientIP, pClientPort, s);
	}
	
	public static void syncPlayerIDs(String list){
		try{
			String[] s = list.split(" ");
			if(s.length % 2 != 1) return;
			int anzTransforms = s.length / 2;
			int anzPlayer = getPlayerList().size();

			if(anzTransforms != anzPlayer) return;
			for(int i = 1; i+1 < s.length; i+=2){
				syncPlayerID(s[i], s[i+1]);
			}
			
		}
		catch(Exception e){
			System.err.println("Unable to sync IDs");
		}
	}
	
	public static void syncPlayerID(String name, String id){
		getPlayer(name).id = Integer.parseInt(id);

		System.out.println("ID *" + id + " synced");
	}
	
	public static void syncStaticsIDs(String list){
		try{
			String[] s = list.split(" ");
			if(s.length % 3 != 1) return;
			
			for(int i = 1; i+2 < s.length; i+=3){
				syncStatics_ID(s[i], s[i+1], s[i+2]);
			}
		}
		catch(Exception e){
			System.err.println("Unable to sync IDs");
		}
	}
	
	public static void syncStatics_ID(String x, String y, String ident){
		int xPos = Integer.parseInt(x);
		int yPos = Integer.parseInt(y);
		int id = Integer.parseInt(ident);
		for(StaticObject s : getStaticList()){
			if(s.getX() == xPos && s.getY() == yPos){
				s.id = id;
				System.out.println("ID *" + id + " synced");
				return;
			}
		}
	}
	
	public static void spawnNPC(int id, String trans){
		if(mode != CLIENT_MODE) return;
		
		Character spawned = NonPlayerCharacter.createFromTransform(trans);
		
		if(spawned == null){
			System.err.println("Unable to spawn " + trans);
			return;
		}
		
		spawned.id = id;
		
		addCompList(spawned);
		
	}
	
	public static boolean nameOK(String name){
		return !name.contains(" ") 
				&& !name.contains("#") 
				&& !name.contains("_") 
				&& getPlayer(name) == null 
				&& name.length() > 2 
				&& name.length() <= 16;
	}
	
}
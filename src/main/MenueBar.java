package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import mapping.MapCreator;

public class MenueBar extends JMenuBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2961621439033063260L;

	private JMenu general;
	private JMenuItem reload;
	private JMenuItem switchMode;
	private JCheckBoxMenuItem debug;
	private JCheckBoxMenuItem showFps;
	private JMenu perspective;
	private JRadioButton birdPerspective;
	private JRadioButton wormPerspective;
	private JMenu gameMode;
	private JRadioButton lastManStanding;
	private JRadioButton killCount;
	private JRadioButton teamDeathmatch;
	private JRadioButton captureTheFlag;
	private JRadioButton domination;
	private JRadioButton basevsbase;
	private JRadioButton survival;
	private JMenu chooseCharacter;
	private JRadioButton megaman;
	private JRadioButton sakuyamon;
	private JRadioButton zero;
	private JRadioButton wizzy;
	private JMenuItem exit;
	
	private JMenu online;
	private JMenuItem openServer;
	private JMenuItem serverSync;
	private JMenuItem connect;
	private JMenuItem rename;
	private JMenuItem offline;
	
	private JMenu map;
	private JMenuItem reloadMap;
	private JMenuItem loadMap;
	private JMenuItem saveMap;
	private JMenuItem mapCreator;
	
	private JMenu options;
	private JCheckBoxMenuItem fullscreen;
	private JMenuItem fps;
	
	
	
	public MenueBar(){
		initMenus();
	}
	
	private void initMenus(){
		initMegamanMenu();
		initOnlineMenu();
		initMapMenu();
		initOptionsMenu();
	}
	
	public String character = "Megaman";

	private void initMegamanMenu(){
		general = new JMenu("General");
		general.setMnemonic(KeyEvent.VK_M);
		general.getAccessibleContext().setAccessibleDescription(
		        "General Options");
		
		reload = new JMenuItem("Reload");
		reload.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.mode == Main.NORMAL_MODE) Main.restartNormalGame();
				else if(Main.mode == Main.SERVER_MODE){
					Main.restartServerGame();
					if(Main.debug) System.out.println("RestartServerGame");
				}
			}
		});
		
		general.add(reload);
		
		switchMode = new JMenuItem("Pause/Unpause");
		switchMode.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.switchMode();
			}
		});
		
		general.add(switchMode);
		
		debug = new JCheckBoxMenuItem("DebugMode");
		debug.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.debug = !Main.debug;
				debug.setState(Main.debug);
			}
		});
		debug.setState(Main.debug);
		general.add(debug);
		
		showFps = new JCheckBoxMenuItem("Show FPS");
		showFps.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.displayFps = !Main.displayFps;
				showFps.setState(Main.displayFps);
			}
		});
		showFps.setState(Main.displayFps);
		general.add(showFps);

		general.addSeparator();
		
		ButtonGroup group = new ButtonGroup();
		
		perspective = new JMenu("Game Perspective");
		
		wormPerspective = new JRadioButton("Worm Perspective");
		wormPerspective.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.changePerspective(Main.WORM_PERSPECTIVE_MODE);
			}
		});
		wormPerspective.setSelected(true);
		group.add(wormPerspective);
		perspective.add(wormPerspective);
		
		birdPerspective = new JRadioButton("Bird Perspective");
		birdPerspective.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.changePerspective(Main.BIRD_PERSPECTIVE_MODE);
			}
		});
		birdPerspective.setSelected(false);
		group.add(birdPerspective);
		perspective.add(birdPerspective);
		
		general.add(perspective);
		
		
		
		gameMode = new JMenu("Choose GameMode");
		
		group = new ButtonGroup();
		
		lastManStanding = new JRadioButton("Last Man Standing");
		lastManStanding.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.LAST_MAN_STANDING);
			}
		});
		lastManStanding.setSelected(true);
		group.add(lastManStanding);
		gameMode.add(lastManStanding);
		
		killCount = new JRadioButton("Kill Count");
		killCount.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.KILL_COUNT);
			}
		});
		killCount.setSelected(false);
		group.add(killCount);
		gameMode.add(killCount);
		
		teamDeathmatch = new JRadioButton("Team Deathmatch");
		teamDeathmatch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.TEAM_DEATHMATCH);
			}
		});
		teamDeathmatch.setSelected(false);
		group.add(teamDeathmatch);
		gameMode.add(teamDeathmatch);
		
		captureTheFlag = new JRadioButton("Capture The Flag");
		captureTheFlag.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.CAPTURE_THE_FLAG);
			}
		});
		captureTheFlag.setSelected(false);
		group.add(captureTheFlag);
		gameMode.add(captureTheFlag);
		
		domination = new JRadioButton("Domination");
		domination.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.DOMINATION);
			}
		});
		domination.setSelected(false);
		group.add(domination);
		gameMode.add(domination);
		
		basevsbase = new JRadioButton("Base Vs Base");
		basevsbase.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.BASE_VS_BASE);
			}
		});
		basevsbase.setSelected(false);
		group.add(basevsbase);
		gameMode.add(basevsbase);
		
		survival = new JRadioButton("Survival");
		survival.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GameRules.changeMode(GameRules.SURVIVAL);
			}
		});
		survival.setSelected(false);
		group.add(survival);
		gameMode.add(survival);
		
		general.add(gameMode);
		
		
		chooseCharacter = new JMenu("Choose Character");
		
		group = new ButtonGroup();
		
		
		megaman = new JRadioButton("Megaman");
		megaman.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.mode == Main.CLIENT_MODE){
					if(!Main.clock.isEnabled()){
						Main.client.send("/changeCharacter Megaman");
					}
				}
				else{
					Main.changeCharacter("Megaman", Main.name);
					character = "Megaman";
				}
			}
		});
		megaman.setSelected(true);
		group.add(megaman);
		chooseCharacter.add(megaman);
		
		sakuyamon = new JRadioButton("Sakuyamon");
		sakuyamon.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.mode == Main.CLIENT_MODE){
					if(!Main.clock.isEnabled()){
						Main.client.send("/changeCharacter Sakuyamon");
					}
				}
				else{
					Main.changeCharacter("Sakuyamon", Main.name);
					character = "Sakuyamon";
				}
			}
		});
		group.add(sakuyamon);
		chooseCharacter.add(sakuyamon);
		
		zero = new JRadioButton("Zero");
		zero.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.mode == Main.CLIENT_MODE){
					if(!Main.clock.isEnabled()){
						Main.client.send("/changeCharacter Zero");
					}
				}
				else{
					Main.changeCharacter("Zero", Main.name);
					character = "Zero";
				}
			}
		});
		group.add(zero);
		chooseCharacter.add(zero);
		
		wizzy = new JRadioButton("Wizzy");
		wizzy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.mode == Main.CLIENT_MODE){
					if(!Main.clock.isEnabled()){
						Main.client.send("/changeCharacter Wizzy");
					}
				}
				else{
					Main.changeCharacter("Wizzy", Main.name);
					character = "Wizzy";
				}
			}
		});
		group.add(wizzy);
		chooseCharacter.add(wizzy);
		
		general.add(chooseCharacter);
		
		general.addSeparator();
		
		exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				exit();
			}
		});
		
		general.add(exit);
		
		
		this.add(general);
	}
	
	private void initOnlineMenu(){
		online = new JMenu("Online");
		online.setMnemonic(KeyEvent.VK_O);
		online.getAccessibleContext().setAccessibleDescription(
		        "Online Options");
		
		openServer = new JMenuItem("Open new Server...");
		openServer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openServer();
				refreshOnlineItems();
			}
		});
		online.add(openServer);
		
		serverSync = new JMenuItem("Set ServerSyncsPerSecond");
		serverSync.setMnemonic(KeyEvent.VK_S);
		serverSync.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setServerSyncs();
			}
		});
		
		online.add(serverSync);
		
		online.addSeparator();
		
		connect = new JMenuItem("Connect to Server...");
		connect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connect();
				refreshOnlineItems();
			}
		});
		online.add(connect);
		
		online.addSeparator();
		
		rename = new JMenuItem("Change Name");
		rename.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				changeName();
			}
		});
		online.add(rename);
		
		offline = new JMenuItem("Go offline!");
		offline.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.normalMode();
			}
		});
		online.add(offline);
		
		this.add(online);
		
	}
	
	private void initMapMenu() {
		map = new JMenu("Map Options");
		map.setMnemonic(KeyEvent.VK_P);
		map.getAccessibleContext().setAccessibleDescription(
		        "Map Options, only useable if no Game is currently processing");
		
		reloadMap = new JMenuItem("Reload StartMap");
		reloadMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!(Main.mode == Main.CLIENT_MODE) && !Main.clock.isEnabled()){
					Main.iomap.loadStandardMap();
				}
			}
		});
		
		map.add(reloadMap);
		
		map.addSeparator();
		
		loadMap = new JMenuItem("Load Map from File");
		loadMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.mode != Main.CLIENT_MODE) Main.iomap.mapLoadDialog();
			}
		});
		
		map.add(loadMap);
		
		saveMap = new JMenuItem("Save Map");
		saveMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.iomap.mapSaveDialog();
			}
		});
		
		map.add(saveMap);
		
		map.addSeparator();
		
		mapCreator = new JMenuItem("Create new Map");
		mapCreator.setMnemonic(KeyEvent.VK_N);
		mapCreator.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.mapCreator = new MapCreator();
			}
		});
		
		map.add(mapCreator);
		
		this.add(map);
		
	}
	
	private void initOptionsMenu(){
		options = new JMenu("Options");
		options.setMnemonic(KeyEvent.VK_P);
		
		fullscreen = new JCheckBoxMenuItem("Fullscreen");
		fullscreen.setMnemonic(KeyEvent.VK_S);
		fullscreen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fullscreenMode();
			}
		});
		fullscreen.setState(Main.fullscreen);
		
		options.add(fullscreen);
		
		fps = new JMenuItem("Set FPS");
		fps.setMnemonic(KeyEvent.VK_F);
		fps.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setFps();
			}
		});
		
		options.add(fps);
		
		this.add(options);
	}
	
	public void refreshOnlineItems(){
		switch(Main.mode){
		case Main.NORMAL_MODE:
			connect.setEnabled(true);
			openServer.setEnabled(true);
			switchMode.setEnabled(true);
			reload.setEnabled(true);
			loadMap.setEnabled(true);
			reloadMap.setEnabled(true);
			serverSync.setEnabled(false);
			
			killCount.setEnabled(true);
			lastManStanding.setEnabled(true);
			captureTheFlag.setEnabled(true);
			teamDeathmatch.setEnabled(true);
			domination.setEnabled(true);
			basevsbase.setEnabled(true);
			survival.setEnabled(true);
			break;	
		case Main.CLIENT_MODE:
			switchMode.setEnabled(false);
			reload.setEnabled(false);
			connect.setEnabled(false);
			openServer.setEnabled(false);
			loadMap.setEnabled(false);
			reloadMap.setEnabled(false);
			serverSync.setEnabled(false);
			
			killCount.setEnabled(false);
			lastManStanding.setEnabled(false);
			captureTheFlag.setEnabled(false);
			teamDeathmatch.setEnabled(false);
			domination.setEnabled(false);
			basevsbase.setEnabled(false);
			survival.setEnabled(false);
			
			break;
		case Main.SERVER_MODE:
			loadMap.setEnabled(true);
			reloadMap.setEnabled(true);
			connect.setEnabled(false);
			openServer.setEnabled(false);
			reload.setEnabled(true);
			switchMode.setEnabled(true);
			serverSync.setEnabled(true);
			
			killCount.setEnabled(true);
			lastManStanding.setEnabled(true);
			captureTheFlag.setEnabled(true);
			teamDeathmatch.setEnabled(true);
			domination.setEnabled(true);
			basevsbase.setEnabled(true);
			survival.setEnabled(true);
			break;
		}
	}
	
	
	
	public void reload(){
		Main.reload();
	}
	
	public void exit(){
		System.exit(0);
	}
	
	public void openServer(){
		Main.startServer();
	}
	
	public void connect(){
		Main.connect();
	}
	
	public void fullscreenMode(){
		Main.fullscreen = !Main.fullscreen;
		fullscreen.setState(Main.fullscreen);
		
        if (Main.fullscreen && Main.device.isFullScreenSupported()){
        	if (Main.ui.isDisplayable()) {
                Main.ui.setVisible(false);
                Main.ui.dispose();
            }
            Main.ui.setUndecorated(true);
            if (!isVisible()) {
                setVisible(true);
            }
            Main.device.setFullScreenWindow(Main.ui);
        }
        else{
        	Main.device.setFullScreenWindow(null);
            Main.ui.setVisible(false);
            Main.ui.dispose();
            Main.ui.setUndecorated(false);
            Main.ui.setVisible(true);
		}
	}
	
	public void setFps(){
		try{
			String input = JOptionPane.showInputDialog("Enter new FPS (1-120) below:");
			int fps = Integer.parseInt(input);
			if(fps <= 0 || fps > 120) return;
			Main.fps = fps;
			Main.renderClock.setIntervall(1000/fps);
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to set new FPS");
		}
	}
	
	
	public void setServerSyncs(){
		if(Main.mode != Main.SERVER_MODE) return;
		try{
			String input = JOptionPane.showInputDialog("Enter new ServerSyncsPerSecond (1-60) below:");
			int serverSyncs = Integer.parseInt(input);
			if(serverSyncs <= 0 || serverSyncs > 60) return;
			Main.syncsPerSec = serverSyncs;
			Main.serverSyncClock.setIntervall(1000/serverSyncs);
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to set new ServerSyncsPerSecond");
		}
	}
	
	public void refreshGameModeItems(){
		switch(GameRules.currentGameMode){
		case GameRules.KILL_COUNT:
			killCount.setSelected(true);
			break;
		case GameRules.LAST_MAN_STANDING:
			lastManStanding.setSelected(true);
			break;
		case GameRules.CAPTURE_THE_FLAG:
			captureTheFlag.setSelected(true);
			break;
		case GameRules.TEAM_DEATHMATCH:
			teamDeathmatch.setSelected(true);
			break;
		case GameRules.DOMINATION:
			domination.setSelected(true);
			break;
		case GameRules.BASE_VS_BASE:
			basevsbase.setSelected(true);
			break;
		case GameRules.SURVIVAL:
			survival.setSelected(true);
			break;
		}
	}
	
	public void changeName(){
		if(Main.mode == Main.CLIENT_MODE){
			Main.client.sendName();
		}
		else{
			Main.enterNameDialog();
		}
	}
	
	
	
}

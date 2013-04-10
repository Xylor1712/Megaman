package mapping;

import java.awt.Dimension;
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

import main.Main;

public class MapMenuBar extends JMenuBar {

	private static final long serialVersionUID = 5171080756932206385L;
	
	private MapCreator root;
	
	private JMenu map;
	private JMenuItem loadMap;
	private JMenuItem saveMap;
	private JMenuItem changeSize;
	private JMenuItem exit;
	
	private JMenu options;
	private JCheckBoxMenuItem fullscreen;
	private JMenuItem fps;
	
	private JMenu edit;
	private JRadioButton rects;
	private JRadioButton hearts;
	private JRadioButton bombItems;
	private JRadioButton shieldItem;
	private JRadioButton rapidfireItem;
	private JRadioButton towerRepairItem;
	private JMenu flags;
	private JRadioButton whiteFlag;
	private JRadioButton redFlag;
	private JRadioButton greenFlag;
	private JRadioButton blueFlag;	
	private JMenu spawnPoints;
	private JRadioButton whiteSpawn;
	private JRadioButton redSpawn;
	private JRadioButton greenSpawn;
	private JRadioButton blueSpawn;
	private JRadioButton dominationPoint;
	private JRadioButton tower;
	private JRadioButton meleeMinionSpawner;
	private JRadioButton transparentRect;
	
	public MapMenuBar(MapCreator root){
		this.root = root;
		initMenus();
	}

	private void initMenus(){
		initMapMenu();
		initOptionsMenu();
		initEditMenu();
	}
	
	private void initMapMenu() {
		map = new JMenu("Map Options");
		map.setMnemonic(KeyEvent.VK_M);
		
		loadMap = new JMenuItem("Load Map from File");
		loadMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.io.mapLoadDialog();
			}
		});
		
		map.add(loadMap);
		
		saveMap = new JMenuItem("Save Map");
		saveMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.io.mapSaveDialog();
			}
		});
		
		map.add(saveMap);
		
		map.addSeparator();
		
		changeSize = new JMenuItem("Change Size");
		changeSize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				changeSizeDialog();
			}
		});
		
		map.add(changeSize);
		
		map.addSeparator();
		
		exit = new JMenuItem("Close");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(root.recentlyChanged){
					
				}
				else{
					root.dispose();
				}
			}
		});
		map.add(exit);
		
		this.add(map);
		
	}
	
	private void initOptionsMenu(){
		options = new JMenu("Grafic Options");
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
	
	private void initEditMenu(){
		edit = new JMenu("Set CurrentItem");
		edit.setMnemonic(KeyEvent.VK_E);
		
		ButtonGroup group = new ButtonGroup();
		
		rects = new JRadioButton("Rect");
		rects.setSelected(true);
		rects.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.Rect;
			}
		});
		group.add(rects);
		edit.add(rects);
		
		
		transparentRect = new JRadioButton("TransparentRect");
		transparentRect.setSelected(false);
		transparentRect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.transparentRect;
			}
		});
		group.add(transparentRect);
		edit.add(transparentRect);
		
		hearts = new JRadioButton("Heart");
		hearts.setSelected(false);
		hearts.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.Heart;
			}
		});
		group.add(hearts);
		edit.add(hearts);
		
		bombItems = new JRadioButton("BombPack");
		bombItems.setSelected(false);
		bombItems.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.BombItem;
			}
		});
		group.add(bombItems);
		edit.add(bombItems);
		
		shieldItem = new JRadioButton("Shield(BuffItem)");
		shieldItem.setSelected(false);
		shieldItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.ShieldItem;
			}
		});
		group.add(shieldItem);
		edit.add(shieldItem);
		
		rapidfireItem = new JRadioButton("RapidFire(BuffItem)");
		rapidfireItem.setSelected(false);
		rapidfireItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.RapidFire;
			}
		});
		group.add(rapidfireItem);
		edit.add(rapidfireItem);
		
		
		towerRepairItem = new JRadioButton("TowerRepairKit");
		towerRepairItem.setSelected(false);
		towerRepairItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.towerRepairItem;
			}
		});
		group.add(towerRepairItem);
		edit.add(towerRepairItem);
		
		flags = new JMenu("Flags:");
		
		whiteFlag = new JRadioButton("White Flag (CTF Only)");
		whiteFlag.setSelected(false);
		whiteFlag.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.whiteFlag;
			}
		});
		group.add(whiteFlag);
		flags.add(whiteFlag);
		
		redFlag = new JRadioButton("Red Flag (CTF Only)");
		redFlag.setSelected(false);
		redFlag.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.redFlag;
			}
		});
		group.add(redFlag);
		flags.add(redFlag);
		
		greenFlag = new JRadioButton("Green Flag (CTF Only)");
		greenFlag.setSelected(false);
		greenFlag.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.greenFlag;
			}
		});
		group.add(greenFlag);
		flags.add(greenFlag);
		
		blueFlag = new JRadioButton("Blue Flag (CTF Only)");
		blueFlag.setSelected(false);
		blueFlag.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.blueFlag;
			}
		});
		group.add(blueFlag);
		flags.add(blueFlag);
		
		edit.add(flags);
		
		
		spawnPoints = new JMenu("SpawnPoints");
		
		whiteSpawn = new JRadioButton("White Spawnpoint");
		whiteSpawn.setSelected(false);
		whiteSpawn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.whiteSpawn;
			}
		});
		group.add(whiteSpawn);
		spawnPoints.add(whiteSpawn);
		
		redSpawn = new JRadioButton("Red Spawnpoint");
		redSpawn.setSelected(false);
		redSpawn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.redSpawn;
			}
		});
		group.add(redSpawn);
		spawnPoints.add(redSpawn);
		
		greenSpawn = new JRadioButton("Green Spawnpoint");
		greenSpawn.setSelected(false);
		greenSpawn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.greenSpawn;
			}
		});
		group.add(greenSpawn);
		spawnPoints.add(greenSpawn);
		
		blueSpawn = new JRadioButton("Blue Spawnpoint");
		blueSpawn.setSelected(false);
		blueSpawn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.blueSpawn;
			}
		});
		group.add(blueSpawn);
		spawnPoints.add(blueSpawn);
		
		edit.add(spawnPoints);
		
		dominationPoint = new JRadioButton("DominationPoint");
		dominationPoint.setSelected(false);
		dominationPoint.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.dominationPoint;
			}
		});
		group.add(dominationPoint);
		edit.add(dominationPoint);
		
		tower = new JRadioButton("Tower");
		tower.setSelected(false);
		tower.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.tower;
			}
		});
		group.add(tower);
		edit.add(tower);
		
		meleeMinionSpawner = new JRadioButton("MeleeMinionSpawner");
		meleeMinionSpawner.setSelected(false);
		meleeMinionSpawner.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.type = ObjType.meleeMinionSpawner;
			}
		});
		group.add(meleeMinionSpawner);
		edit.add(meleeMinionSpawner);
		

		
		
		this.add(edit);
	}
	
	
	
	
	
	public void fullscreenMode(){
		root.fullscreen = !root.fullscreen;
		fullscreen.setState(root.fullscreen);
		
        if (root.fullscreen && Main.device.isFullScreenSupported()){
        	if (root.isDisplayable()) {
                root.setVisible(false);
                root.dispose();
            }
            root.setUndecorated(true);
            if (!isVisible()) {
                setVisible(true);
            }
            Main.device.setFullScreenWindow(root);
            if(Main.fullscreen) Main.menubar.fullscreenMode();
        }
        else{
        	Main.device.setFullScreenWindow(null);
            root.setVisible(false);
            root.dispose();
            root.setUndecorated(false);
            root.setVisible(true);
		}
	}
	
	public void setFps(){
		try{
			String input = JOptionPane.showInputDialog("Enter new FPS (1-120) below:");
			int fps = Integer.parseInt(input);
			if(fps <= 0 || fps > 120) return;
			root.fps = fps;
			root.renderer.setIntervall(1000/fps);
		}
		catch(Exception e){
			if(Main.debug) System.err.println("Failed to set new FPS");
		}
	}
	
	public void changeSizeDialog(){
		try{
			String widthString = JOptionPane.showInputDialog("New width:");
			String heightString = JOptionPane.showInputDialog("New height:");
			root.mapSize = new Dimension(Integer.parseInt(widthString), Integer.parseInt(heightString));
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(this, "Failed to update new map size.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}

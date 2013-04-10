package mapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import objects.GameObject;
import objects.Rect;
import objects.StaticObject;

import main.Main;

public class MapLoading {
	
	public String sep = File.separator;
	
	public MapCreator root;
	
	public MapLoading(MapCreator root){
		this.root = root;
	}
	
	public void mapSaveDialog(){
	    JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        ".map files", "map");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       if(Main.debug)System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       
		    try{
		    	String filepath = chooser.getSelectedFile().getPath();
		    	if(!filepath.substring(filepath.length()-4).equals(".map")) filepath += ".map";
		    	File mapFile = new File(filepath);
		    	if(Main.debug) System.out.println(filepath);
		    	if(!mapFile.exists()){
		    		mapFile.createNewFile();
		    	}
		    	BufferedWriter out = new BufferedWriter(new FileWriter(mapFile));
			    out.write("resolution#" + root.mapSize.width + "#" + root.mapSize.height);
			    out.newLine();
			    for(GameObject o : root.getCompList()){
			    	out.write(o.transformToString());
			    	out.newLine();
			    }
			    out.close();
			    if(Main.debug)System.out.println("File saved succesfully");
			    root.recentlyChanged = false;
		    }
		    catch(IOException e){
		    	System.out.println("Failed to save file");
		    	e.printStackTrace();
		    }
	    }
	}
	
	public void mapLoadDialog(){
	    JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        ".map files", "map");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       if(Main.debug)System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       
		    try{
		    	BufferedReader in = new BufferedReader(new FileReader(chooser.getSelectedFile()));
			    String[] resolution = in.readLine().split("#");
			    if(resolution.length != 3 || !resolution[0].equals("resolution")){
			    	in.close();
			    	throw new Exception("Wrong file");
			    }
			    ArrayList<GameObject> objects = new ArrayList<>();
		    	do{
		    		String line = in.readLine();
		    		if(line == null || line.equals("")) break;
		    		
		    		GameObject obj = StaticObject.newObjFromString(line);

		    		if(obj == null){
		    			in.close();
		    			throw new Exception("Object == null");
		    		}
		    		else{
		    			objects.add(obj);
		    		}
		    	}
		    	while(true);
		    	
		    	in.close();
		    	
		    	root.reloadMap(Integer.parseInt(resolution[1]), Integer.parseInt(resolution[2]), objects);
		    	
			    if(Main.debug)System.out.println("File loaded succesfully");
			    root.recentlyChanged = false;
		    }
		    catch(Exception e){
		    	System.err.println("Failed to load file");
		    	if(Main.debug) e.printStackTrace();
		    }
	    }
	}
	
	public static void createNewStandardMapFile(){
		    try{
		    	File mapFile = new File(IOMap.class.getClassLoader().getResource("standardMap.map").getFile());
		    	if(Main.debug) System.out.println(mapFile.getAbsolutePath());
		    	if(!mapFile.exists()){
		    		mapFile.createNewFile();
		    	}
		    	BufferedWriter out = new BufferedWriter(new FileWriter(mapFile));
			    out.write("resolution#1000#850");
			    out.newLine();
			    
			    ArrayList<Rect> rList = new ArrayList<>();
			    rList.add(new Rect(150,650,700,50));
			    rList.add(new Rect(200,500,200,50));
			    rList.add(new Rect(600,500,200,50));
			    rList.add(new Rect(0,275,200,50));
			    rList.add(new Rect(800,275,200,50));
			    rList.add(new Rect(300,100,400,50));
			    rList.add(new Rect(475,275,50,200));
				
			    for(Rect r : rList){
			    	out.write(r.transformToString());
			    	out.newLine();
			    }
			    out.close();
			    if(Main.debug)System.out.println("File saved succesfully");
		    }
		    catch(IOException e){
		    	System.out.println("Failed to save file");
		    	e.printStackTrace();
		    }
	    
	}
	
}

package mapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import objects.GameObject;
import objects.Rect;
import objects.StaticObject;

import main.Main;

public class IOMap {
	
	public String sep = File.separator;
	
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
			    out.write("resolution#" + Main.mapSize.width + "#" + Main.mapSize.height);
			    out.newLine();
			    for(GameObject o : Main.getStaticList()){
			    	out.write(o.transformToString());
			    	out.newLine();
			    }
			    out.close();
			    JOptionPane.showMessageDialog(null, "File saved succesfully", "Message", JOptionPane.INFORMATION_MESSAGE);
		    }
		    catch(IOException e){
			    JOptionPane.showMessageDialog(null, "Failed to save file.", "Error", JOptionPane.ERROR_MESSAGE);
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
			    ArrayList<StaticObject> objects = new ArrayList<>();
		    	do{
		    		String line = in.readLine();
		    		if(line == null || line.equals("")) break;
		    		
		    		StaticObject obj = StaticObject.newObjFromString(line);

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
		    	
		    	Main.reloadMap(Integer.parseInt(resolution[1]), Integer.parseInt(resolution[2]), objects);

			    JOptionPane.showMessageDialog(null, "File loaded succesfully", "Message", JOptionPane.INFORMATION_MESSAGE);
		    }
		    catch(Exception e){
			    JOptionPane.showMessageDialog(null, "Failed to load file.", "Error", JOptionPane.ERROR_MESSAGE);
		    }
	    }
	}
	
	
	public void loadStandardMap(){
		    try{
		    	File mapFile = new File(IOMap.class.getClassLoader().getResource("standardMap.map").getFile());
		    	if(Main.debug) System.out.println(mapFile.getPath());
		    	if(!mapFile.exists()){
		    		createNewStandardMapFile();
		    		loadStandardMap();
		    		return;
		    	}
		    	BufferedReader in = new BufferedReader(new FileReader(mapFile));
			    String[] resolution = in.readLine().split("#");
			    if(resolution.length != 3 || !resolution[0].equals("resolution")){
			    	in.close();
			    	throw new Exception("Wrong file");
			    }
			    ArrayList<StaticObject> rects = new ArrayList<>();
		    	do{
		    		String line = in.readLine();
		    		if(line == null || line.equals("")) break;
		    		Rect r = Rect.newRectFromString(line);
		    		if(r == null){
		    			in.close();
		    			throw new Exception("Rect == null");
		    		}
		    		else{
		    			rects.add(r);
		    		}
		    	}
		    	while(true);
		    	
		    	in.close();
		    	
		    	Main.reloadMap(Integer.parseInt(resolution[1]), Integer.parseInt(resolution[2]), rects);
		    	
			    if(Main.debug)System.out.println("File loaded succesfully");
		    }
		    catch(Exception e){
		    	System.err.println("Failed to load file");
		    	e.printStackTrace();
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

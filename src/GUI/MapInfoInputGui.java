package GUI;

import javax.swing.JFrame;

public class MapInfoInputGui extends JFrame {

	private static final long serialVersionUID = -7121311447685641494L;

	private static MapInfoInputGui info;
	
	private static boolean infoComplete = false;
	
	public MapInfoInputGui(){
		this.setSize(200, 400);
		this.setAlwaysOnTop(true);
	}
	
	
	public static int[] getRules(){
		info = new MapInfoInputGui();
		
		
		return null;
	}

}

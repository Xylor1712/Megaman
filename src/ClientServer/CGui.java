package ClientServer;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.GameRules;
import main.Main;

public class CGui extends Client {
	
	public JFrame ui = new JFrame("Client ChatLog");
	private JTextField txt = new JTextField("");
	private JTextArea area = new JTextArea("");
	private JScrollPane scrollPane = new JScrollPane(area);
	
	private int charsPerLine = 55;
	
	private String line = System.getProperty("line.separator");
	
	
	
	public CGui(String ip, int port) throws UnknownHostException, IOException{
		super(ip, port);
		initFrame();
	}
	
	public void initFrame(){
		ui.setSize(400,400);
		ui.setLayout(new BorderLayout(5,5));
		ui.addWindowListener(new WindowListener(){
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
			}
			@Override
			public void windowClosing(WindowEvent arg0) {	
				Main.closeClient();
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {	
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
			}			
		});
		ui.setResizable(false);
		
		area.setEditable(false);
		area.setText("** "+ super.toString() +" **");
		
		txt.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					sendMessage(txt.getText());
					txt.requestFocus();
				}
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
		});
		
		
		ui.add(scrollPane, BorderLayout.CENTER);
		ui.add(txt, BorderLayout.PAGE_END);
		
		ui.setVisible(true);

		txt.requestFocus();
	}
	
	
	@Override
	public void processMessage(String pMessage) {
//		
//		System.out.println("Message from Server:");
//		System.out.println(pMessage);
//		
		String[] msg;
		try{
			msg = pMessage.split(" ");
		}
		catch(Exception e){
			return;
		}
		if(pMessage.length() < 1){
			System.err.println("Leere Nachricht");
			return;
		}
		try{
			if(pMessage.charAt(0) == '/'){
				switch(msg[0]){
				case "/getName":
					sendName();
					break;
				case "/say":
					say(msg);
					break;
				case "/gamemode":
					if(msg.length != 2) return;
					GameRules.changeMode(Integer.parseInt(msg[1]));
					break;
				case "/action":
					if(msg.length != 3) return;
					Main.processAction(msg[2], msg[1]);
					break;
				case "/attack":
					if(msg.length != 5) return;
					Main.attackWithDirection(msg[1], msg[2], msg[3], msg[4]);
					break;
				case "/matchObjects":
					Main.matchObjectsNew(pMessage);
					break;
				case "/syncPlayerIDs":
					Main.syncPlayerIDs(pMessage);
					break;
				case "/syncStaticIDs":
					Main.syncStaticsIDs(pMessage);
					break;
				case "/syncIDCounter":
					Main.idCounter = Integer.parseInt(msg[1]);
					break;
				case "/rename":
					if(msg.length != 3) return;
					Main.renamePlayer(msg[1], msg[2]);
					break;
				case "/newPlayer":
					if(msg.length != 4) return;
					Main.newPlayer(msg[1], msg[3], msg[2]);
					break;
				case "/deletePlayer":
					if(msg.length != 2) return;
					Main.deletePlayer(msg[1]);
					break;
				case "/newObj":
					if(msg.length != 3) return;
					Main.newObj(msg[1], msg[2]);
					break;
				case "/spawn":
					if(msg.length != 3) return;
					Main.spawnNPC(Integer.parseInt(msg[1]), msg[2]);
					break;
				case "/gameRuns":
					Main.gameRuns = Boolean.parseBoolean(msg[1]);
					break;
				case "/deleteObj":
					if(msg.length != 2) return;
					Main.deleteObj(msg[1]);
					break;
				case "/deleteObject":
					if(msg.length != 2) return;
					Main.deleteObj(Integer.parseInt(msg[1]));
					break;
				case "/switchMode":
					if(msg.length != 2){
						return;
					}
					Main.clock.setEnabled(Boolean.parseBoolean(msg[1]));
					break;
				case "/restartGame":
					Main.restartClientGame();
					break;
				case "/win":
					if(msg.length != 2) return;
					Main.win(msg[1]);
					break;
				case "/displayMessage":
					Main.canvas.displayMessage(pMessage.substring(16));
					break;
				case "/stopDisplayMessage":
					Main.canvas.stopDisplayMessage();
					break;
				case "/infoMessage":
					Main.canvas.setInfoMessage(pMessage.substring(13));
					break;
				case "/movementAllowed":
					if(msg.length != 2) return;
					Main.movementAllowed = Boolean.parseBoolean(msg[1]);
					break;
				case "/teamScored":
					if(msg.length != 2 || !GameRules.isTeamGame()) return;
					GameRules.teamScored(Integer.parseInt(msg[1]));
					break;
				case "/event":
					Main.event();
					break;
				case "/newResolution":
					if(msg.length != 3) return;
					Main.mapSize.width = Integer.parseInt(msg[1]);
					Main.mapSize.height = Integer.parseInt(msg[2]);
					if(Main.debug) System.out.println("New MapSize: " + Main.mapSize);
					break;
				case "/changeCharacter":
					if(msg.length != 3) return;
					Main.changeCharacter(msg[2], msg[1]);
					break;
				case "/newMap":
					Main.newMap();
					break;
				case "/perspective":
					Main.perspective = Integer.parseInt(msg[1]);
					break;
				case "/close":
					JOptionPane.showMessageDialog(null, "Server closed.", "Error", JOptionPane.ERROR_MESSAGE);
					Main.normalMode();
					break;
				default:
					print("Unknown command: " + pMessage);
				}
			}
			else{
				print(pMessage);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendName(){
		String name = "";
		do{
			try{
				name = JOptionPane.showInputDialog("Geben Sie ihren Namen ein");
			}
			catch(Exception e){
				
			}
		}
		while(name == null || name.equals(""));
		send("/setName "+ name + " " + Main.menubar.character);
		Main.name = name;
	}
	
 
	
	public void sendMessage(String text){
		if(text.equals("") || text == null) return;
		
		String s = "/say " + text;
		send(s);
		txt.setText("");
		if(Main.debug) System.out.println("SendToServer: " + s);
	}
	
	public void print(String s){
		String out = "";
		for(int i = 0; i*charsPerLine < s.length(); i++){
			out += line;
			if((i+1)*charsPerLine > s.length()) out += s.substring(i*charsPerLine);
			else out += s.substring(i*charsPerLine, (i+1)*charsPerLine);
		}
		while(this.area == null){System.out.println("area == null");}
		area.append(out);
		Main.canvas.chat.addText(s);
	}
	
	private void say(String[] msg){
		String message = "";
		for(int i = 1; i < msg.length; i++) message += (i == 1 ? "" : " ") + msg[i];
		print(message);
	}
	
	public void connectionClosed(){
		ui.dispose();
		Main.client = null;
		Main.mode = Main.NORMAL_MODE;
		Main.menubar.refreshOnlineItems();
		Main.reload();
	}

}

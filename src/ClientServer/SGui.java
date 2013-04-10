package ClientServer;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.Main;

public class SGui extends Server {
	
	
	public JFrame ui = new JFrame("Server Log");
	private JTextField txt = new JTextField("");
	private JTextArea area = new JTextArea("");
	private JScrollPane scrollPane = new JScrollPane(area);
	
	private String line = System.getProperty("line.separator");
	
	private boolean gameStarted = false;
	
	public SGui(int pPortNr) throws IOException {
		super(pPortNr);
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
				//Main.closeServer();
			}

			@Override
			public void windowClosing(WindowEvent arg0) {	
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
		area.setText("** Start Server on Port: " + zPort + " **");
		
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
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
	
	public void sendMessage(String text){
		if(text.equals("") || text == null || text.length() < 1) return;
		
		String s = Main.name + "(Host): " + text;
		sendToAll(s);
		print(s);
		txt.setText("");
	}
	
	public void print(String s){
		int charsPerLine = 55;
		String out = "";
		for(int i = 0; i*charsPerLine < s.length(); i++){
			out += line;
			if((i+1)*charsPerLine > s.length()) out += s.substring(i*charsPerLine);
			else out += s.substring(i*charsPerLine, (i+1)*charsPerLine);
		}
		area.append(out);
		Main.canvas.chat.addText(s);
	}
	
	
	@Override
	public void processNewConnection(String pClientIP, int pClientPort) {

	}

	@Override
	public void processMessage(String pClientIP, int pClientPort,
			String pMessage) {
		String[] msg;
		ServerConnection sc = this.SerververbindungVonIPUndPort(pClientIP, pClientPort);
		if (sc == null) return;
		
		try{
			msg = pMessage.split(" ");
		}
		catch(Exception e){
			wrongMsg(pClientIP, pClientPort, pMessage);
			return;
		}
		
		switch(msg[0]){
		case "/setName":
			setName(sc, msg);
			break;
		case "/say":
			say(sc,msg);
			break;
		case "/jump":
			Main.processAction("jump", sc.getN());
			break;
		case "/turnLeft":
			Main.processAction("turnLeft", sc.getN());
			break;
		case "/turnRight":
			Main.processAction("turnRight", sc.getN());
			break;
		case "/attack":
			Main.processAction("attack", sc.getN());
			break;
		case "/attack2":
			if(msg.length != 4) return;
			Main.attackWithDirection(sc.getN(), msg[1], msg[2], msg[3]);
			sendToAll("/attack " + sc.getN() + " " + msg[1] + " " + msg[2] + " " + msg[3]);
			break;
		case "/stopTurnRight":
			Main.processAction("stopTurnRight", sc.getN());
			break;
		case "/stopTurnLeft":
			Main.processAction("stopTurnLeft", sc.getN());
			break;
		case "/up":
			Main.processAction("up", sc.getN());
			break;
		case "/down":
			Main.processAction("down", sc.getN());
			break;
		case "/stopUp":
			Main.processAction("stopUp", sc.getN());
			break;
		case "/stopDown":
			Main.processAction("stopDown", sc.getN());
			break;
		case "/event":
			Main.computePing(sc.getN());
			break;
		case "/changeCharacter":
			if(msg.length != 2) return;
			Main.changeCharacter(msg[1], sc.getN());
			break;
		case "/getInfos":
			sendInfos(pClientIP, pClientPort);
			break;
		default:
			wrongMsg(pClientIP, pClientPort, pMessage);
		}
	}
	
	private void setName(ServerConnection sc, String[] msg){
		if(msg.length != 3 || !Main.nameOK(msg[1]) ){
			sc.send("/getName");
			return;
		}
	
		if(sc.getN() == null || sc.getN().equals("")){
			Main.newPlayer(msg[1], msg[2], "NOID");

			sta("/say " + msg[1] + " logged in.");
		}
		else{
			String oldName = sc.getN();
			if(!(Main.renamePlayer(oldName, msg[1]))){
				System.out.println("Unable to rename " + oldName + " zu " + msg[1] + ".");
				return;
			}
		}
		sc.setN(msg[1]);
	}
	
	private void sta(String s) {
		sendToAll(s);
		print(s.substring(5));
	}

	private void say(ServerConnection sc, String[] msg){
		String name = sc.getN();
		if(name == null || name.equals("")){
			sc.send("/getName");
			return;
		}
		String message = "";
		for(int i = 1; i < msg.length; i++) message += " " + msg[i];
		String m = "/say " + name + ":" + message;
		sta(m);
	}

	private void wrongMsg(String pClientIP, int pClientPort, String pMessage) {
		print("Wrong Message Received (will be ignored): ");
		String n = this.SerververbindungVonIPUndPort(pClientIP, pClientPort).getN();
		if(n != null){
			print(n + " schrieb: " + pMessage);
		}
		else{
			print("" + pClientIP + ":"+pClientPort+" : "+ pMessage);
		}
	}

	@Override
	public void processClosedConnection(String pClientIP, int pClientPort) {
		String name = this.SerververbindungVonIPUndPort(pClientIP, pClientPort).getN();
		sta("/say " + name + " logged off.");
		Main.deletePlayer(name);
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}
	
	public void sendInfos(String pClientIP, int pClientPort){
		this.send(pClientIP, pClientPort, "/gameRuns " + Main.gameRuns);
		Main.sendMap(pClientIP, pClientPort);
		Main.sendCurrentState(pClientIP, pClientPort);
		Main.syncIDs(pClientIP, pClientPort);
		this.send(pClientIP,  pClientPort, "/movementAllowed " + Main.movementAllowed);
		this.send(pClientIP, pClientPort, "/perspective " + Main.perspective);
		if(Main.clock.isEnabled()) send(pClientIP, pClientPort, "/switchMode true");
		Main.matchObjectsServerNew();
	}
	

}

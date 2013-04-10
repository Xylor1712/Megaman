package GUI;

import images.ImageLoader;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MenuPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = -6374418949813550261L;
	
	private ImageIcon buttonIcon = ImageLoader.getIcon("BlockoG.png");
	
	private BoxLayout layout;
	
	private JButton singleplayer;
	private JButton multiplayer;
	private JButton options;
	private JButton exit;
	
	
	public MenuPanel(){
		layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		

		this.setLayout(layout);
		
		initButtons();
		initLayout();
		

		
		
	}
	
	private void initLayout() {
		
		this.add(Box.createVerticalGlue());
		this.add(singleplayer);
		this.add(Box.createVerticalGlue());
		this.add(multiplayer);
		this.add(Box.createVerticalGlue());
		this.add(options);
		this.add(Box.createVerticalGlue());
		this.add(exit);
		this.add(Box.createVerticalGlue());
	}

	private void initButtons(){
		singleplayer = new JButton("Singleplayer");
		singleplayer.setMnemonic('S');
		singleplayer.setActionCommand("singleplayer");
		singleplayer.addActionListener(this);
		singleplayer.setIcon(buttonIcon);
		singleplayer.setAlignmentX(CENTER_ALIGNMENT);
		singleplayer.setAlignmentY(CENTER_ALIGNMENT);

		multiplayer = new JButton("Multiplayer");
		multiplayer.setMnemonic('M');
		multiplayer.setActionCommand("multiplayer");
		multiplayer.addActionListener(this);
		multiplayer.setAlignmentX(CENTER_ALIGNMENT);
		multiplayer.setAlignmentY(CENTER_ALIGNMENT);
		

		options = new JButton("Options");
		options.setMnemonic('O');
		options.setActionCommand("options");
		options.addActionListener(this);
		options.setAlignmentX(CENTER_ALIGNMENT);
		options.setAlignmentY(CENTER_ALIGNMENT);

		exit = new JButton("Exit");
		exit.setMnemonic('E');
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		exit.setAlignmentX(CENTER_ALIGNMENT);
		exit.setAlignmentY(CENTER_ALIGNMENT);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "singleplayer":
			
			break;
		case "multiplayer":
			
			break;
		case "options":
			
			break;
		case "exit":
			
			break;
		}
	}

	public static void main(String[] args){
		new JFrame("Test"){
			private static final long serialVersionUID = 8620900696432559397L;
			{
				this.add(new MenuPanel());
				this.setSize(400, 400);
				this.setDefaultCloseOperation(EXIT_ON_CLOSE);
				this.setVisible(true);
				
			}
		};
	}

}

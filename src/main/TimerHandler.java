package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public abstract class TimerHandler {
	
private boolean enabled = false;
	
//	private boolean doingSmth = false;
	
	private int intervall;
	
	private double speed = 1.0d;
	
	public TimerHandler(int intervall){
		this.intervall = intervall;
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(enabled){
//					if(!doingSmth){
//						doingSmth = true;
						event();
//						doingSmth = false;
//					}
				}
			}
		};
		new Timer(this.intervall, taskPerformer).start();
	}
	
	public void run() {

	}
	
	public void start(){
		this.enabled = true;
	}
	
	public void switchMode(){
		enabled = !enabled;
	}
	
	public void pause(){
		enabled = false;
	}
	public void restart(){
		enabled = true;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	
	public double getSpeed(){
		return speed;
	}
	
	public void setSpeed(double s){
		if(s<0.025) this.speed = 0.025;
		else if(s>10) this.speed = 10;
		else this.speed = s;
	}
	
	public abstract void event();
}

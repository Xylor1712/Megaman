package main;

public abstract class Clock extends Thread{
	
	private boolean enabled = false;
	
//	private boolean doingSmth = false;
	
	private int intervall;
	
	private double speed = 1.0d;
	
	public Clock(int intervall){
		this.intervall = intervall;
	}
	
	public void run() {
		while(true){
			if(enabled){
//				if(!doingSmth){
//					doingSmth = true;
					event();
//					doingSmth = false;
//				}
			}
			try{
				sleep((int)((1/speed)*intervall));
			}
			catch(InterruptedException e){
				interruptedExc(e);
			}
			
		}
	}
	
	public abstract void event();
	
	public abstract void interruptedExc(InterruptedException e);
	
	public void start(){
		this.enabled = true;
		super.start();
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
	
	public void setEnabled(boolean b){
		enabled = b;
	}
	
	
	public double getSpeed(){
		return speed;
	}
	
	public void setSpeed(double s){
		if(s<0.025) this.speed = 0.025;
		else if(s>10) this.speed = 10;
		else this.speed = s;
	}
	
	public void setIntervall(int intervall){
		this.intervall = intervall;
	}	
	
	public String toString(){
		return "Clock: enabled = " + enabled +" intervall = " + intervall + " speed = " + speed;
	}
	
}
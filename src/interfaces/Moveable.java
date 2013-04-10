package interfaces;

import java.awt.Dimension;
import java.util.ArrayList;

public interface Moveable extends ICollision{
	public void move(Dimension g, ArrayList<ICollision> list);
}
import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class GameWindow extends GameGrid {

	
	public GameWindow() {
	  
	    super(10, 10, 60, java.awt.Color.black);
	    Apple food = new Apple();
	    addActor(food, new Location(2, 4));
	    show();
	  }
	
	public static void main(String[] args) {
		
		new GameGrid();
	}

}

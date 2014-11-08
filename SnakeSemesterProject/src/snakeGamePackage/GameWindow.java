package snakeGamePackage;
import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Scanner;

public class GameWindow extends GameGrid {
	
	public GameWindow() {
	    super(40, 30, 18, java.awt.Color.gray);
	    Apple food = new Apple();
	    Apple imitateSnake = new Apple();
	    
	    addActor(food, new Location(2, 4));
	    addActor(imitateSnake, new Location(0,0));
	    Snake1 snake = new Snake1(); 
	    addActor(snake, new Location (5,7));
	    
	    Bit bit = new Bit();
	    addActor(bit, new Location (4,7));
	    
	    Bit bit2 = new Bit();
	    addActor(bit2, new Location (3,7));
	    
	    Bit bit3 = new Bit();
	    addActor(bit3, new Location (2,7));
	    
	    food.addCollisionActor(imitateSnake);
	    imitateSnake.addCollisionActor(food);
	    
	    show();
	  }
	
}
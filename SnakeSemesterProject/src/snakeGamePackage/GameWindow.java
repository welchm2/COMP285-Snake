package snakeGamePackage;
import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameWindow extends GameGrid {
	
	public GameWindow() {
	    super(40, 30, 18, java.awt.Color.gray);
	    Apple food = new Apple();
	    Apple imitateSnake = new Apple();
	    
	    addActor(food, new Location(2, 4));
	    addActor(imitateSnake, new Location(0,0));
	    
	    food.addCollisionActor(imitateSnake);
	    imitateSnake.addCollisionActor(food);
	    
	    show();
	  }
	
}

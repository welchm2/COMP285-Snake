package SnakeGame;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;

public class Test0 extends GameWindow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Snake snake = new Snake();
	private ArrayList<Apple> apples = new ArrayList<Apple>();
	
	
	public Test0() {
		super();
		addActor(snake, new Location(10, 10));
		snake.setDirection(Location.EAST);
		addApples();
		show();
	}
	
	public static void main(String[] args){
		GameWindow test0 = new GameWindow();
		test0.setTitle("Test to create GameWindow");
	}
	
	public void addApples(){
		Apple a = new Apple();
		apples.add(a);
		addActor(a, new Location(12, 16));
	}
}
package snakeGamePackage;
import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;


public class Apple extends Actor {
	
	// fields definitions
	private Random randomGenerator;
	private static final int GAMEWIDTH = 40;
	private static final int GAMEHEIGHT = 30;
	
	/*
	 * Constructor
	 * initializes Parent Constructor and Random Generator
	 */
	public Apple() {
		super("sprites/apple_small.png");
		randomGenerator = new Random();
	}
	
	/*
	 * the apple moves from its current location to a new random one
	 */
	public void act() {
		Location newLocation = new Location();
		newLocation.x = randomGenerator.nextInt(GAMEWIDTH);
		newLocation.y = randomGenerator.nextInt(GAMEHEIGHT);
		setLocation(newLocation);
	}
	
	/*
	 * Registers a partner actor that becomes a collision candidate, e.g. that is checked for collisions in every simulation cycle
	 * The collisions are reported by a collision listener that must be registered with addActorCollisionListener()
	 * 
	 * Paramenter: a partner of type Actor
	 */
	public void addCollisionActor(Actor snake) {
		
	}
	
	/*
	 * returns true if the snake could eat the apple
	 * (if the two actors collide)
	 */
	private boolean appleGotEaten() {
		
		// if ( ) 		// collides with snake
		return true;
		// else { return false; }
	}
}

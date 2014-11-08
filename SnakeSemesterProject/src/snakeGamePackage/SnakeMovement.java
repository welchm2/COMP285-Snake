package snakeGamePackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ch.aplu.jgamegrid.Actor;

public class SnakeMovement extends Apple implements KeyListener{
	private boolean left = false;
	private boolean right = true;
	private boolean up = false;
	private boolean down = false;
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_UP:
			if(!down){
				up = true;
				left = false;
				right = false;
			}
			break;
		case KeyEvent.VK_DOWN:
			if(!up){
				down = true;
				left = false;
				right = false;
			}
			break;
		case KeyEvent.VK_LEFT:
			if(!right){
				left = true;
				up = false;
				down = false;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(!left){
				right = true;
				up = false;
				down = false;
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
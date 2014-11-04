package game;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class WinMain extends JFrame{
	
	public gPanel pG;						//Panel für den Spielablauf
	public JPanel pE;						//Panel für das Ende des Spiels

	public WinMain(String title){
		//Die Vrbereitungen für das Hauptfenster treffen
		Game.log.write("Hauptfenster erstellen...");
		this.setTitle(title);
		this.setSize(230,270);		//Eigentlich müsste es 200,200 sein aber leider passen diese Angaben bei Windows nciht pixel genau und es wird ein Teil abgeschnitten!
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Panels initialisieren
		Game.log.write("Spielpanel erstellen...");
		this.pG = new gPanel();
		this.pG.setBounds(0, 0, 250, 250);
		this.pG.setBackground(Color.black);
		//Tasten-Events abfangen
		this.addKeyListener(new KeyListener(){

			//@Override
			public void keyPressed(KeyEvent e) {}

			//@Override
			public void keyReleased(KeyEvent e) {
				//Auf Tastendruck reagieren
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					Game.snake.setDir("right");
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					Game.snake.setDir("left");
				}else if(e.getKeyCode() == KeyEvent.VK_UP){
					Game.snake.setDir("top");
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					Game.snake.setDir("bottom");
				}
			}

			//@Override
			public void keyTyped(KeyEvent e) {}
			
		});
		this.add(pG);
		
		this.setVisible(true);
	}
	
	public class gPanel extends JPanel{
		@Override public void paintComponent(Graphics gr){
			//Game.log.write("Spielpanel zeichnen...");
			Graphics2D g2d  = (Graphics2D) gr;
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, 250, 250);
			
			g2d.setColor(Color.black);
			g2d.drawRect(10, 10, 195, 195);
			
			g2d.setColor(Color.green);
			g2d.fillRect((Game.aim.x+1)*5, (Game.aim.y+1)*5, 5, 5);
			
			g2d.setColor(Color.black);
			g2d.drawString("Punkte: "+Game.punkte+" - Zeit: "+Game.playtime, 10, 220);
			
			Game.snake.paintSneak(g2d);
			
		}
	}
}

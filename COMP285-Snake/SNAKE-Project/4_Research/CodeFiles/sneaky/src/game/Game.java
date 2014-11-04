/*
 * Tool name   : Sneaky
 * Description : This is a Demo of a sneak Game in Java
 * Author      : Sebastian Gross
 * Webpage     : http://blog.bigbasti.com / bigbasti.com
 * Version     : 0.5
 * OS          : Tested on Microsoft Windows XP; Mac OS X 10.5.8; Mac OS X 10.6
 * Todo        : -
 *
 * Changes     : Finished fully working version
 * 
 *
 *
 *
 * License     :
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package game;

import java.awt.*;

public class Game {

	public static Logwriter log = new Logwriter();
	public static Snake snake;
	public static WinMain frmMain;
	public static Point aim;
	
	public static double playtime = 0;
	
	public static int punkte = 0;
	
	public static void main(String[] args){
		log.write("Spiel gestartet...");
		
		snake = new Snake();
		
		frmMain = new WinMain("Sneaky by Sebastian Gross");
		log.write("Hauptfenster erstellt...");
		
		newAim();
		
		runGame(); 
	}
	
	public static void newAim(){
		Point p;
		//Einen neuen Punkt auf dem Spielfeld erzeugen
		while(true){
			p = new Point((int)((Math.random()*20)+0.5),(int)((Math.random()*20)+0.5));
			//Darauf achten, dass das Zielfeld innerhalb des Spielfeldes liegt!
			//x & y muss größer als 0 sein!
			if(p.x >= 1 && p.y >= 1){
				break;
			}
		}
		punkte += 100; //Für das erreichen des Zielfelds 100 Punkte geben
		aim = p;
	}
	
	public static void runGame(){
		int secs = 0;								//Variable um Zeit zu messen
		while(true){
			//Eine eunendliche Schleife starten
			//die alle 100 Millisekunden (Standard) das Bild Aktualisiert und 
			//Die Schlange um ein Kästchen weiter bewegt
			
			//frmMain.setTitle("S:"+snake.pos.x+snake.pos.y+" - "+aim.x+aim.y);
			if(snake.moveSnake() == false){
				//Wenn Speil zu Ende ist Programm anhalten
				break;
				
			}
			
			frmMain.pG.paintComponent(frmMain.pG.getGraphics());
			
			//Spielzeit aktualisieren (optnal)
			//Muss angepasst werden wenn, die untere sleep() Einstellung geändert wird!
			secs++;
			if (secs == 10){
				//Eine Sekunde vergangen
				playtime++;
				//Zähler zurücksetzen
				secs = 0;
				punkte++;
			}
			try{
				Thread.sleep(100);	//Standardeinstellung: 100
			}catch(Exception ex){log.write(ex.getMessage());}

			
		}
	}
}

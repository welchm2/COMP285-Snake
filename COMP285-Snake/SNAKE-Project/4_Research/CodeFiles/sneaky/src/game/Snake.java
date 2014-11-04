package game;

import java.awt.*;
import java.util.*;

public class Snake {

	public Point pos;			//Aktuelle Position des Schlangen Kopfes
	public String dir;			//Die Richtung in die die schlage sich momentan bewegt (left, right, top, bottom)
	
	public boolean newPoint = false;
	
	public LinkedList<PointA> points = new LinkedList<PointA>();	//Liste mit allen Schlangenpunkten
	
	private Logwriter log = new Logwriter();
	
	public Snake(){
		//Wenn eine Schlange neu erstellt wird hat sie ncoh keine Points, d.h. points ist leer
		//Damit der benutzer �berhaupt was sieht werden gleich 2 Punkte erzeugt
		//wobei einer von den 2 Punkten als Kopf markiert wird!
		//Die Schlange startet in der linken oberen ecke an Position 2,1 => wobei 2,1 der kopf und 1,1 der K�rper ist
		//0,0 - 0,[spielfeldl�nge] ist f�r den Rand reserviert!
		//Die anfangs laufrichtung ist dementsprechend rechts
		pos = new Point(3,1);			//Anfangsposition des Schlangenkopfes
		dir = "right";					//Anfangsrichtung
		
		//Den anfags-schlangen-k�rper erstellen, bestehend aus 3 Punkten Kopf-[nichtkopf & nichtschwanz]-Schwanz
		points.add(new PointA(new Point(3,1),true,false));	//Schlangenkopf erstellen
		points.add(new PointA(new Point(2,1),false,false));	//Schlangenkoerper erstellen
		points.add(new PointA(new Point(1,1),false,true));	//Schlangenschwanz erstellen
		
		log.write("Schlange erstellt...");
	}
	
	public void addNewPoint(PointA p){
		points.add(p);
	}
	
	public void paintSneak(Graphics g){
		//log.write("Punkte neu zeichnen...");
		for(PointA p : points){							//Alle Points der Schlange neu zeichnen
			p.paintPoint(g);
		}
	}
	
	public void setDir(String dir){
		//Pr�fen ob in die Entgegengesetzte Richtung gewechselt werden soll -> geht ja nit
		//Diese IF-Kette auskommentieren um das spiel um eine modi zu erweitern: instant-wenden
		if(this.dir.equals("right") && dir.equals("left")){
			log.write("Richtung �nder ist nicht m�glich...");
			return;
		}else if(this.dir.equals("left") && dir.equals("right")){
			log.write("Richtung �nder ist nicht m�glich...");
			return;
		}else if(this.dir.equals("top") && dir.equals("bottom")){
			log.write("Richtung �nder ist nicht m�glich...");
			return;
		}else if(this.dir.equals("bottom") && dir.equals("top")){
			log.write("Richtung �nder ist nicht m�glich...");
			return;
		}
		log.write("Laufrichtung auf "+dir+" �ndern...");
		this.dir = dir;									//Laufrichtung der Schlage �ndern
	}
	
	public boolean moveSnake(){
		//Diese Funktion gibt true zur�ck wenn die Schlange weiterr�cken konnte
		//Oder false wenn die schlange gegen die Wand oder gegen Sich selbst gefahren ist!
		
		/*
		 * Mir sind f�r diesen Vorgang 2 Vorgehensweisen eingefallen:
		 * 1:
		 * Wenn die Schlange weiterfahren soll nimmt man einfach jeden Punkt und 
		 * ersetzt dessen koordinaten mit denen des Vordermanns
		 *    O--X--X--X--X--X--X			<= Das soll die schlange sein
		 * 	^<|^<|^<|^<|^<|^<|^<|		=> Alles r�ckt einen Schritt weiter
		 * 
		 * 2:
		 * Oder man nimmt den Letzten point und setzt ihn an den Anfang der Schlange:
		 * 	  O--X--X--X--X--X--X			<= Das soll die schlange sein
		 * 	^------------------<|		=> Nur der Letzte point wird bewegt!
		 * 
		 * Ich habe mich an dieser Stelle mal f�r die zweite Variante entschieden,
		 * da ich denke dass diese fl�ssiger ist und Recourcensparender!
		 */

		
		//Pr�fen, ob die Schlange sich in die angegebene Richtung bewegen kann		
		Point nPos = null;
		if(this.dir.equals("left")){
			nPos = new Point(this.pos.x-1, this.pos.y);
		}else if(this.dir.equals("top")){
			nPos = new Point(this.pos.x, this.pos.y-1);
		}else if(this.dir.equals("bottom")){
			nPos = new Point(this.pos.x, this.pos.y+1);
		}else if(this.dir.equals("right")){
			nPos = new Point(this.pos.x+1, this.pos.y);
		}
		
		if(nPos.x <= 0 || nPos.x >= 40 || nPos.y <= 0 || nPos.y >= 40){			//Wenn Spielfeldrand erreicht ist
			return false;
		}else{
			for(PointA p : points){
				if(p.pos.equals(nPos)){	//Wenn Schlange sich selbst ber�hrt	
					log.write("Wand ber�hrt, Speil zu Ende...");
					return false;
				}
			}
			//Wenn noch Platz ist:
			//Den aktuellen Kopf als "nicht-kopf" markieren
			for(PointA p : points){
				if(p.head == true){
					p.head = false;
				}
			}
			//und den letzten Punkt vor den Kopf setzen
			for(PointA p : points){
				if(p.tail == true){
					p.tail = false;
					p.head = true;
					p.setPoint(nPos);
					PointA nP = p;
					points.remove(p);
					points.add(0, nP);
				}
			}
			this.pos = nPos;
			points.get(points.size()-1).tail = true;
		}
		
		//Pr�fen, ob das "Futter" gefunden wurde und die schlange erweitern
		if(nPos.equals(Game.aim)){
			log.write("Futter gefunden, wachsen...");
			//Gucken in welche Richtung die Schlange grade l�uft
			if(this.dir.equals("left")){
				for(PointA p : points){
					//Den Kopf der Schlange um eine Stelle in Laufrichtung verschieben
					if(p.head == true){
						p.setPoint(new Point(this.pos.x-1,this.pos.y));
					}
				}
				//Und den neuen Punkt hinter den Kopf h�ngen
				this.pos = new Point(this.pos.x-1,this.pos.y);
				points.add(1,new PointA(new Point(0,0),false,false));
			}else if(this.dir.equals("top")){
				for(PointA p : points){
					if(p.head == true){
						p.setPoint(new Point(this.pos.x,this.pos.y-1));
					}
				}
				this.pos = new Point(this.pos.x,this.pos.y-1);
				points.add(1,new PointA(new Point(0,0),false,false));
			}else if(this.dir.equals("bottom")){
				for(PointA p : points){
					if(p.head == true){
						p.setPoint(new Point(this.pos.x,this.pos.y+1));
					}
				}
				this.pos = new Point(this.pos.x,this.pos.y+1);
				points.add(1,new PointA(new Point(0,0),false,false));
			}else if(this.dir.equals("right")){
				for(PointA p : points){
					if(p.head == true){
						p.setPoint(new Point(this.pos.x+1,this.pos.y));
					}
				}
				this.pos = new Point(this.pos.x+1,this.pos.y);
				points.add(1,new PointA(new Point(0,0),false,false));
			}
			//Nenen Zielpunkt setzen
			Game.newAim();
		}
		return true;
	}
}

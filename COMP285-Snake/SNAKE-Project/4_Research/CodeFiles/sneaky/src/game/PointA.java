package game;

import java.awt.*;

//Diese Klasse hat ein "A" am Ende, da der name "Point" reserviert ist!

public class PointA {

	public Point pos;
	public boolean head;					//Gibt an, ob dieser Punkt der Kopf der Schlange ist
	public boolean tail;					//Gibt an, ob dieser Punkt das Ende der schlange ist
	private Logwriter log = new Logwriter();
	
	public PointA(Point p, boolean head, boolean tail){
		this.pos = p;
		this.head = head;
		this.tail = tail;
		log.write("Neuen Punkt erstellt...");
	}
	
	public Point getPos(){
		return pos;
	}
	
	public void setPoint(Point p){
		this.pos = p;
	}
	
	public void paintPoint(Graphics g){
		if (head == true){
			g.setColor(Color.red);			//Den Kopf der Schlage Rot färben
		}else{
			g.setColor(Color.black);
		}
		g.fillRect((pos.x+1)*5, (pos.y+1)*5, 5, 5);
		//log.write("Punkt zeichnen...");
	}
}

package Nibbles;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  This is the applet class for the Nibbles game.
 */
public class Nibbles extends JApplet implements KeyListener {
  private SnakePanel gameBoard;
  private final boolean show_keynums = true; //display the numbers of pressed keys

  /**
   * Semi-generic applet to application adapter.
   * @param args String[]
   */
  public static void main(String[] args) {
    final Nibbles applet = new Nibbles();
    JFrame frame = new JFrame();
    applet.init();
    applet.start();
    frame.addKeyListener(applet);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing (WindowEvent event) {
        applet.stop();
        applet.destroy();
        System.exit(0);
      }
    });
    frame.setContentPane(applet.getContentPane());
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Initialize the applet.
   */
  public void init() {
    gameBoard = new SnakePanel();
    gameBoard.setBackground(Color.black);
    gameBoard.setPreferredSize(new Dimension(600,400)); //600x400 is the size of the entire applet
    addKeyListener(this);
    Container c = getContentPane();
    c.add(gameBoard, BorderLayout.CENTER);
  }

  /**
   * Process Keyboard events. Simply calls the appropiate method of gameBoard.
   * @param e the event
   */
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case 38: //up
        gameBoard.changeDirection(8); break;
      case 40: //down
        gameBoard.changeDirection(2); break;
      case 37: //left
        gameBoard.changeDirection(4); break;
      case 39: //right
        gameBoard.changeDirection(6); break;
      case 32: //space bar
        gameBoard.startSnake(); break;
      case 67: //"c"
        gameBoard.ChooseColors(); break;
      case 80: //"p"
        gameBoard.togglePause(); break;
      case 61: //"+/=" (plus)
        gameBoard.setLevel(+1); break;
      case 45: //"_/-" (minus)
        gameBoard.setLevel(-1); break;
      default:
    }
    if (show_keynums) System.out.println(e.getKeyCode());//used for debugging
  }

  public void keyReleased(KeyEvent e) {}//needed to implement KeyListener
  public void keyTyped(KeyEvent e) {}//needed to implement KeyListener
}

package Nibbles;

import java.awt.*;
import java.util.Random;

/**
 *  Implements a Nibble for the Nibbles game
 */
public class Nibble {
  private final int NIBSIZE = 14;   //each nibble will be 14 pixels square
  private final int ROWSIZE = 16;   //each row will take up this much space
  private Color NIBC = Color.green; //Nibble color
  private int row,col;              //row and col position of nibble
  private Random random;            //random number generator

  /**
   *Constructor. Creates a new Random variable, and makes the first Nibble.
   * @param s the current snake, will not place a nibble on top of the snake
   */
  public Nibble(Snake s) {
    random = new Random(System.currentTimeMillis());
    random.nextFloat();
    random.nextFloat();
    newNibble(s);
  }

  /**
   *Returns the row of the Nibble;
   * @return the row of the nibble
   */
  public int getRow() { return row; }
  /**
   *Returns the column of the Nibble
   * @return the column of the nibble
   */
  public int getCol() { return col; }
  /**
   *Returns the color of the nibble
   * @return the nibble color (NIbble Background Color)
   */
  public Color getNIBC() { return NIBC; }
  /**
   *Sets the color of the nibble
   * @param newColor the new nibble color
   */
  public void setNIBC(Color newColor) { NIBC = newColor; }

  /**
   *Makes a new Nibble or moves the Nibble to a new random location.
   * @param s the current snake, needed to know where not to place the nibble
   */
  public void newNibble(Snake s) {
    boolean okay = false;
    while (!okay) {
      row = (int)((random.nextFloat())*24);
      col = (int)((random.nextFloat())*24);
      okay = true;
      for (int i = s.getLen(); i >= 0; i--) {
        if (row == s.getRow(i) && col == s.getCol(i)) {
          okay = false;
          break;
        }
      }
    }
  }

  /**
   * Draws the Nibble onto the given Graphics context
   * @param g the current graphics
   */
  public void draw(Graphics g) {
    int x = col * ROWSIZE + 1;
    int y = row * ROWSIZE + 1;
    g.setColor(NIBC);
    g.fillRoundRect(x,y,NIBSIZE,NIBSIZE,15,15);
  }
}

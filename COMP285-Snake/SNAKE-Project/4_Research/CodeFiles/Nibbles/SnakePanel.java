package Nibbles;

/**
 *  Implements a panel on which "snake" moves
 *  in the Nibbles applet.. all the action takes
 *  place in this class
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class SnakePanel extends JPanel implements ActionListener {
  //game objects
  private Snake snake;
  private Nibble nib;

  //timer
  private Timer t;
  private int delay = 75;//number of milliseconds between snake moves
  private double timer; //number of seconds on the timer
  private int starttimer = 5; //where to start the timer
  private int addtimer = 3; //how much to add to timer every time

  //in-game variables
  private int level = 1; //the user-choosed level to play at
  private ArrayList directions;//queue of keyStrokes
  private int score;//holds the value of the current score

  //game info for method control
  private int played = 0; //if user has played yet
  private boolean playing = false; //if user is currently playing
  private boolean paused = false; //Is game paused?

  //colors
  private Color infoBG = Color.blue; //color of the background to the text area
  private Color infoText = Color.black; //color of the text
  private Color snakeBG = Color.black; //color of snake's background

  /**
   *Creates a new area for the game
   */
  public SnakePanel() {
    snake = new Snake();
    nib = new Nibble(snake);
    t = new Timer(delay, this);
    directions = new ArrayList();
    setLevel(0);
  }

  /**
   *Starts the snake moving
   */
  public void startSnake() {
    t.start();
    snake.reset();
    if (played > 0)
    	nib.newNibble(snake);
    score = 0;
    timer = starttimer;
    paused = false;
    playing = true;
  }

  /**
   *Moves the snake
   */
  public void moveSnake() {
    snake.move();
    repaint();
  }

  /**
   * Add another direction to the direction queue
   * @param dir the new direction to go
   */
  public void changeDirection(int dir) {
    if (t.isRunning())
      directions.add( new Integer(dir) );
  }

  /**
   *Makes the game end
   */
  public void gameOver() {
    t.stop();
    Graphics g = getGraphics();
    g.setColor(infoText);
    g.drawString("GAME OVER", 450, 25);
    played++;
    playing = false;
  }

  /**
   * pauses game
   */
  public void pause() {
    if (playing) {
      t.stop();
      paused = true;
      repaint();
    }
  }

  /**
   * unpauses game
   */
  public void unpause() {
    if (playing) {
      t.start();
      paused = false;
      repaint();
    }
  }

  /**
   * Toggle the paused state of the game
   */
  public void togglePause() {
    if (paused)
      unpause();
    else
      pause();
  }


  /**
   * Every time the timer fires make the snake move and determine the state of
   * the game.
   * @param e the action event
   */
  public void actionPerformed(ActionEvent e) {
    timer -= (double)delay / 1000.0;
    while (!directions.isEmpty()) {
      if (snake.changeDirection(((Integer)directions.remove(0)).intValue()))
        break;
    }
    if (snake.goOutOfBounds() || snake.willEatSelf())
      gameOver();
    else
      moveSnake();
    if (snake.eatNibble(nib.getRow(),nib.getCol())) {
      score += (int)timer; //my special scoring method
      timer += addtimer; //increase the timer
      nib.newNibble(snake);
    }
  }

  /**
   * Allows user to select the colors
   */
  public void ChooseColors() {
    pause(); //so timer stops while dialogs are open
    snake.setLINEC(JColorChooser.showDialog(this, "Snake Line Color", snake.getLINEC()));
    snake.setBODYC(JColorChooser.showDialog(this, "Snake Body Color", snake.getBODYC()));
    nib.setNIBC(JColorChooser.showDialog(this, "Nibble Color", nib.getNIBC()));
    snakeBG = JColorChooser.showDialog(this, "Snake's background", snakeBG);
    infoBG = JColorChooser.showDialog(this, "Information center background", infoBG);
    infoText = JColorChooser.showDialog(this, "Text Color", infoText);
    unpause(); //get the game going again
    repaint(); //show the new colors
  }

  /**
   * Allows user to set the level (called when +/- key is pressed)
   * The value passed is added to level, so if value is negative, level goes down
   * Minumum level is 1, max is 10. The level cannot go down while playing.
   * @param change the amount the level should change by
   */
  public void setLevel(int change) {
    //checks for invalid and changes level
    if (change < 0 && playing)
      return;
    level += change;
    if (level > 10)
      level = 10;
    if (level < 1)
      level = 1;
    //set new delay
    delay = 80 - 5 * level;
    t.setDelay(delay);
    //set new growth
    snake.setGrowth((level/2) + 8);
    //set new timer add
    addtimer = (level/3) + 3;
    //refresh everything
    repaint();
  }

  /**
   * Paints the SnakePanel
   * @param g the graphics context to paint on
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g); // call JPanel's paintComponent
    g.setColor(snakeBG);
    g.fillRect(0,0,400,400);
    snake.draw(g);//draw the snake
    nib.draw(g);//draw the nibble
    g.setColor(infoBG); //background color
    g.fillRect(400,0,200,400);
    g.setColor(infoText); //text color
    //display all the text
    if (paused)
      g.drawString("PAUSED", 450, 25);
    g.drawString("SCORE",450,50);
    g.drawString(score + " points",460,65);
    g.drawString("TIMER",450,95);
    g.drawString((int)timer + " secs",460,110);
    g.drawString("LEVEL " + level,450,160);
    g.drawString("MOVEMENT DELAY",450,175);
    g.drawString(delay + " milliseconds",460,185);
    g.drawString("TIMER ADD",450,200);
    g.drawString(addtimer + " secs",460,210);
    g.drawString("SNAKE GROWTH",450,225);
    g.drawString(snake.getGrowth() + " pieces",460,235);
  }
}

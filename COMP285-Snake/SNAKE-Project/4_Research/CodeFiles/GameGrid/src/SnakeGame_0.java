// SnakeGame_0.java

import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class SnakeGame_0 extends GameGrid
{
  private Snake snake = new Snake();

  public SnakeGame_0()
  {
    super(20, 20, 20, null, false);
    addActor(snake, new Location(10, 10));
    snake.setDirection(Location.NORTH);
    show();
    doRun();
  }

  public static void main(String[] args)
  {
    new SnakeGame_0();
  }
}

// --------------------- class Snake ---------------------------
class Snake extends Actor
{
  private ArrayList<Tail> tailList = new ArrayList<Tail>();
  private boolean start = true;
  private final int nMax = 4;
  private int n = 0;

  public Snake()
  {
    super(true, "sprites/snakeHead.gif");
  }

  public void act()
  {
    if (start)
    {
      start = false;
      for (int i = 0; i < 3; i++)
      {
        Tail tail = new Tail();
        gameGrid.addActor(tail, new Location(getX(), getY() + i + 1));
        tailList.add(tail);
      }
    }

    // ---------------------------
    int lastIndex = tailList.size() - 1;
    Location lastLocation = tailList.get(lastIndex).getLocation();
    for (int i = lastIndex; i > 0; i--)
      tailList.get(i).setLocation(tailList.get(i-1).getLocation());
    tailList.get(0).setLocation(getLocation());
    // ---------------------------


    move();
    n++;
    if (n % nMax == 0)
      setDirection(getDirection() + 90);
    if (n % (4 * nMax) == 0)
    {
      Tail newTail = new Tail();
      gameGrid.addActor(newTail, lastLocation);
      tailList.add(newTail);
    }
  }
}

// --------------------- class Food ---------------------------
class Food extends Actor
{
  public Food()
  {
    super("sprites/sMouse.gif");
  }
}

// --------------------- class Tail ---------------------------
class Tail extends Actor
{
  public Tail()
  {
    super("sprites/snakeTail.gif");
  }
}

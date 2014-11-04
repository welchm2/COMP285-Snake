// Fish.java

import ch.aplu.jgamegrid.*;

public class Fish extends Actor
{
  private int ds = 1;

  public Fish()
  {
    super("sprites/nemo.gif");
  }

  public void act()
  {
    System.out.println("Calling Fish.act()");
    if (getX() == 9)
      ds = -1;
    if (getX() == 0)
      ds = 1;
    setX(getX() + ds);
  }

  public void reset()
  {
      ds = 1;
  }
}

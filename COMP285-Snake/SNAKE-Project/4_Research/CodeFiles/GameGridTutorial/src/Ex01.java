// Ex01.java

import ch.aplu.jgamegrid.*;
import java.awt.Color;

public class Ex01 extends GameGrid
{
  public Ex01()
  {
    super(10, 10, 60, Color.red);
    Fish fish = new Fish();
    addActor(fish, new Location(2, 4));
    show();
  }

  public static void main(String[] args)
  {
    new Ex01();
  }
}

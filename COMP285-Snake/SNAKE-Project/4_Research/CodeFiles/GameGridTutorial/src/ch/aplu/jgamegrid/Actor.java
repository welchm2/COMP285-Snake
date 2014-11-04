// Actor.java

/******************************************************************
 This is a basic version of the JGameGrid library to demonstrate
 some few features of the distributed library and how they 
 are implemented.
 It is Open Source Free Software, so you may
 - run the code for any purpose
 - study how the code works and adapt it to your needs
 - integrate all or parts of the code in your own programs
 - redistribute copies of the code
 - improve the code and release your improvements to the public
 However the use of the code is entirely your responsibility.

 Author: Aegidius Pluess, www.aplu.ch
 */

package ch.aplu.jgamegrid;

import java.awt.image.*;
import java.util.ArrayList;
import java.awt.*;

/**
 * Abstract class for a sprite icon that plays the role of an actor in the
 * game's playground. The actor's image may only be positioned in the center of cells.
 * (for odd pixel unit cells/and or odd pixel unit images, some rounding is necessary.)
 * For one pixel unit cells, the actor's image is positioned at the pixel coordinates.
 */
public class Actor
{
  /* Number of sprites for this actor */
  private int nbSprites;

  /* My GameGrid, null if actor is not added to GameGrid */
  private GameGrid gameGrid = null;

  /* Current cell indices */
  private Location location = new Location(0, 0);

  /* Start cell indices */
  private Location startLocation = new Location(0, 0);

  /* Sprites representing this actor */
  private GGSprite[] sprites;

  /* Current direction (in degrees clockwise, 0 to east) */
  private double direction = 0;

  /* Current rotation index */
  private int rotationIndex = 0;

  /* Starting direction to east */
  private double directionStart = 0;

  /* Simulation period (in usec) */
  private int simulationPeriod;

  /* Size of the sprite's image (in pixels) */
  private int[] imageWidths;
  private int[] imageHeights;

  /* Set of rotated images */
  private BufferedImage[][] bufferedImages;

  /* Set if image is rotated when direction changes */
  private boolean isRotatable;

  /* Factor how much delay act() */
  private int stepCountStart = 0;
  private int stepCount;

  /* is visible or hidden */
//  private boolean[] isVisible;
  private int idVisible;
  /* List of actors to check for collision */
  private ArrayList<Actor> collisionCandidates = new ArrayList<Actor>();

  /* Number of simulation cycles to wait until collision detection is rearmed */
  private int simCount;

  /* Flag to indicate image mirroring */
  private boolean isHorzMirror = false;
  private boolean isVertMirror = false;

  /* Flat to indicate if act() is enabled */
  private boolean isActEnabled = true;

  /* Flat to indicate if collisions are enabled */
  private boolean isCollisionEnabled = true;

  /**
   * Constructs an unrotatable actor based on the specified sprite image.
   * @param filename the fully qualified path to the image files displayed for this actor.
   */
  public Actor(String filename)
  {
    String[] imagePathes = new String[1];
    imagePathes[0] = filename;
    init(false, imagePathes, 1);
  }

  /**
   * Constructs an unrotatable actor based on several sprite images.
   * The sequence of the sprites (the sprite id) is given by the specified sequence of filenames.
   * @param filenames the fully qualified pathes (one or more) to the image files displayed for this actor.
   */
  public Actor(String... filenames)
  {
    init(false, filenames, filenames.length);
  }

  /**
   * Constructs an actor based on the specified sprite image.
   * If isRotatable is true, the actor is rotated when the direction changes.
   * @param isRotatable if true, the actor's image may be rotated (in 360 / 64 = 5.625 degrees angle steps)
   * when the direction changes.
   * @param filename the fully qualified path to the image file displayed for this actor.
   */
  public Actor(boolean isRotatable, String filename)
  {
    String[] imagePathes = new String[1];
    imagePathes[0] = filename;
    init(isRotatable, imagePathes, 1);
  }

  /**
   * Constructs an actor based on one several sprite images.
   * If isRotatable is true, the actor is rotated when the direction changes.
   * The sequence of the sprites (the sprite id) is given by the specified sequence of filenames.
   * @param isRotatable if true, the actor's image may be rotated (in 360 / 64 = 5.625 degrees angle steps)
   * when the direction changes.
   * @param filenames the fully qualified pathes to the image files displayed for this actor.
   */
  public Actor(boolean isRotatable, String... filenames)
  {
    init(isRotatable, filenames, filenames.length);
  }

  /**
   * Constructs an unrotatable actor based on one or more sprite images.
   * The actor may contain more than one sprite images, if nbSprites > 1 
   * the filenames of these images are automatically generated in a sequence
   * filename_0.ext, filename_1.ext, ...
   * @param filename the fully qualified path to the image file displayed for this actor
   * @param nbSprites the number of sprite images for the same actor
   */
  public Actor(String filename, int nbSprites)
  {
    String[] imagePathes = new String[1];
    imagePathes[0] = filename;
    init(false, imagePathes, nbSprites);
  }

  /**
   * Constructs an actor based on one or more sprite images.
   * If isRotatable is true, the actor is rotated when the direction changes.
   * The actor may contain more than one sprite images, if nbSprites > 1
   * the filenames of these images are automatically generated in a sequence
   * filename_0.ext, filename_1.ext, ...
   * @param isRotatable if true, the actor's image may be rotated (in 360 / 64 = 5.625 degrees angle steps)
   * when the direction changes.
   * @param filename the fully qualified path to the image file displayed for this actor
   * @param nbSprites the number of sprite images for the same actor
   */
  public Actor(boolean isRotatable, String filename, int nbSprites)
  {
    String[] imagePathes = new String[1];
    imagePathes[0] = filename;
    init(isRotatable, imagePathes, nbSprites);
  }

  private void init(boolean isRotatable, String[] imagePathes, int nbSprites)
  {
    if (nbSprites < 1)
      nbSprites = 1;
    this.nbSprites = nbSprites;
    sprites = new GGSprite[nbSprites];
    bufferedImages = new BufferedImage[nbSprites][];
    imageHeights = new int[nbSprites];
    imageWidths = new int[nbSprites];

    for (int i = 0; i < nbSprites; i++)
    {
      String path = "";
      if (nbSprites == 1)
        path = imagePathes[0];
      else
      {
        if (imagePathes.length == 1)  // automatic generation of filenames
        {
          int index = imagePathes[0].indexOf('.');
          path = imagePathes[0].substring(0, index) + "_" + i + imagePathes[0].substring(index);
        }
        else
          path = imagePathes[i];
      }
      sprites[i] = GGSpriteStore.get().getSprite(path, isRotatable);
      bufferedImages[i] = sprites[i].getImages();
      imageHeights[i] = sprites[i].getHeight();
      imageWidths[i] = sprites[i].getWidth();
    }

    this.isRotatable = isRotatable;
    initStepCount();

    // Empty implementations of callback methods
  }

  protected void setGameGrid(GameGrid gameGrid)
  {
    this.gameGrid = gameGrid;
  }

  /**
   * Returns the actor's GameGrid reference. 
   * @return the GameGrid reference of the actor
   */
  public GameGrid getGameGrid()
  {
    return gameGrid;
  }

  /**
   * Returns the actor's GGBackground reference.
   * @return the GameGrid's GGBackground reference of the actor, null if the the actor is not yet added to the GameGrid
   */
  public GGBackground getBackground()
  {
    if (gameGrid == null)
      return null;
    return gameGrid.getBackground();
  }

  /**
   * Assigns an new current horizontal coordinate.
   * If x is beyound the range of the cell indices, put to 0 or maximal
   * cell index respectively.<br><br>
   * Triggers a border event, if the location is near the border.
   * @param x the x-coordinate (cell index)
   */
  public void setX(int x)
  {
    location.x = x;
  }

  /**
   * Assigns an new current vertical coordinate.
   * If y is beyound the range of the cell indices, put to 0 or maximal
   * cell index respectively.<br><br>
   * Triggers a border event, if the location is near the border.
   * @param y the y-coordinate (cell index)
   */
  public void setY(int y)
  {
    location.y = y;
  }

  /**
   * Assigns an new current location to the given location.
   * Triggers a border event, if the location is near the border.
   * If x or y of the given location is beyound the range of the cell indices, put to 0 or maximal
   * cell index respectively.<br><br>
   * @param location the current location
   */
  public void setLocation(Location location)
  {
    this.location.x = location.x;
    this.location.y = location.y;
  }


  /**
   * Returns the current horizontal coordinate.
   * @return the x-coordinate (cell index)
   */
  public int getX()
  {
    return location.x;
  }

  /**
   * Returns the current vertical coordinate.
   * @return the y-coordinate (cell index)
   */
  public int getY()
  {
    return location.y;
  }

  /**
   * Returns the current location (horizontal and vertical coordinates).
   * @return the current location (cell indices)
   */
  public Location getLocation()
  {
    return location.clone();
  }

  protected void initStart()
  {
    startLocation = location.clone();
    directionStart = direction;
  }

  /**
   * Returns the start location (horizontal and vertical coordinates).
   * @return the location when the actor was added to the GameGrid
   */
  public Location getLocationStart()
  {
    return startLocation.clone();
  }

  /**
   * Returns the x-coordinate of the start location.
   * @return the x-coordinate when the actor was added to the GameGrid
   */
  public int getXStart()
  {
    return startLocation.x;
  }

  /**
   * Returns the y-coordinate of the start location.
   * @return the y-coordinate when the actor was added to the GameGrid
   */
  public int getYStart()
  {
    return startLocation.y;
  }

  /**
   * Returns the start direction.
   * @return the start direction when the actor was added to the GameGrid
   */
  public double getDirectionStart()
  {
    return directionStart;
  }

  /**
   * Sets the moving direction.
   * @param direction the angle for the next movement (in degrees clockwise, 0 to east)
   */
  public void setDirection(double direction)
  {
    direction = direction % 360;
    if (direction < 0)
      direction = 360 + direction;
    this.direction = direction;
    if (isRotatable)
      rotationIndex = ((int)(1000 * direction)) / 5625;
  }

  /**
   * Returns the current rotation index (0..63). This index selelects one
   * of 64 rotated images (in 360 / 64 degrees steps)
   * @return the rotation index
   */
  public int getRotationIndex()
  {
    return rotationIndex;
  }

  /**
   * Gets the current direction.
   * @return the direction for the next movement (in degrees clockwise, 0 to east)
   */
  public double getDirection()
  {
    return direction;
  }

  protected void draw(Graphics2D g2D)
  {
    if (GameGrid.debug)
      System.out.println("calling Actor.draw()");
    if (gameGrid == null)
      return;
    if (idVisible == -1) // no sprite visible
      return;

    int cellSize = gameGrid.getCellSize();
    if (cellSize > 0)
    {
      int ulx = cellSize / 2 + location.x * cellSize - imageWidths[idVisible] / 2;
      int uly = cellSize / 2 + location.y * cellSize - imageHeights[idVisible] / 2;
      sprites[idVisible].draw(g2D, ulx, uly, rotationIndex, isHorzMirror, isVertMirror);
    }
  }

  /**
   * For a small grid (total number of cells <= 900 = 30 * 30)
   * moves to one of 8 neighbour cells in the current direction (compass directions 45 degrees wide).
   * otherwise moves to a cell about 5 cell sizes away in the current direction.
   */
  public void move()
  {
    if (getNbHorzCells() * getNbVertCells() <= 900)   // Small grid
      setLocation(location.getNeighbourLocation(direction));
    else
      setLocation(location.getAdjacentLocation(direction));
  }

  /**
   * Returns number of the GameGrid's cells in horizontal direction.
   * @return the number of cells in x-direction or -1 if the actor is not yet added to the GameGrid.
   */
  public int getNbHorzCells()
  {
    if (gameGrid == null)
      return -1;
    return gameGrid.getNbHorzCells();
  }

  /**
   * Returns number of the GameGrid's cells in vertical direction.
   * @return the number of cells in y-direction or -1 if the actor is not yet added to the GameGrid.
   */
  public int getNbVertCells()
  {
    if (gameGrid == null)
      return -1;
    return gameGrid.getNbVertCells();
  }

  /**
   * Turns the moving direction by the given angle.
   * @param angle the angle to turn in degrees
   */
  public void turn(double angle)
  {
    direction = (direction + angle) % 360;
    if (isRotatable)
      rotationIndex = ((int)(1000 * direction)) / 5625;
  }

  protected void setSimulationPeriod(int period)
  {
    simulationPeriod = period;
  }

  /*
   Returns x-coordinate of vector x, y with constant magnitude, rotated counter-
   clockwise by angle increment da
   */
  protected static double xrot(double x, double y, double da)
  {
    return (x * Math.cos(da) - y * Math.sin(da));
  }

  /*
   Same for y-coordinate
   */
  protected static double yrot(double x, double y, double da)
  {
    return (y * Math.cos(da) + x * Math.sin(da));
  }

  /**
   * Empty method called in every simulation iteration. Override it to
   * implement your own notification.
   */
  public void act()
  {
  }

  /*
   * Empty method called when the reset button is hit or doReset() is called.
   * Override to get your own notification.
   */
  public void reset(Actor actor)
  {
  }

  /**
   * Empty implementation of a BorderListener called when the actor is set into a border cell.
   * Override to get your own notification.
   * @param actor the current actor
   * @param location the border location
   */
  public void nearBorder(Actor actor, Location location)
  {
  }

  /**
   * Returns true, if the actor's location is inside the grid.
   * @return true, if the current actor's location is inside the grid; null if
   * the actor has not yet been added to the GameGrid
   */
  public boolean isInGrid()
  {
    if (gameGrid == null)
      return false;
    return (gameGrid.isAtBorder(location));
  }

  /**
   * Returns true, if the current location is on a border row or column.
   * @return true, if the current location is on a border row or column; false, otherwise or if
   * the actor has not yet been added to the GameGrid
   */
  public boolean isNearBorder()
  {
    if (gameGrid == null)
      return false;
    return (gameGrid.isAtBorder(location));
  }

  /**
   * Returns true, if the next call of move() will put the actor in a cell
   * inside the game grid.
   * @return true, if the actor remains inside the grid on the next move()
   */
  public boolean isMoveValid()
  {
    if (gameGrid == null)
      return false;
    if (getNbHorzCells() * getNbVertCells() <= 900)   // Small grid
      return gameGrid.isInGrid(location.getNeighbourLocation(direction));
    else
      return gameGrid.isInGrid(location.getAdjacentLocation(direction));
  }

  /**
   * Removes the actor from the GameGrid's scene.
   * Does nothing if the actor has not yet been added to the GameGrid.
   */
  public void removeSelf()
  {
    if (gameGrid == null)
      return;
    setVisible(false);
    gameGrid.removeActor(this);
  }

  protected int getStepCount()
  {
    return stepCount;
  }

  protected void initStepCount()
  {
    stepCount = stepCountStart;
  }

  protected void decreaseStepCount()
  {
    stepCount--;
  }

  /**
   * Slows down the calling of act() by the given factor. This may be used to
   * individually adapt the speed of actors. Sets a counter to the given value.
   * Instead of calling act() in every simulation cycle, the counter is decremented.
   * When it reaches zero, act() is called.
   * @param factor the factor for delaying the invocation of act()
   */
  public void setSlowDown(int factor)
  {
    if (factor < 0)
      factor = 0;
    stepCountStart = factor;
    stepCount = factor;
  }

  /**
   * If isVisible = true, the visibility of the sprite with id = 0 is turned on,
   * otherwise the visibility of the currently visible sprite is turned off.
   * @param isVisible if true the actor is visible; otherwise it is hidden
   */
  public void setVisible(boolean isVisible)
  {
    if (isVisible)
      setVisible(0, true);
    else if (idVisible != -1)
      setVisible(idVisible, false);
  }

  /**
   * Determines if the actor (with specified sprite id) is drawn the next time act() is called.
   * @param spriteId the sprite id that will become visible or not. The visibility of all other
   * sprites is turned off
   * @param isVisible if true the actor is visible; otherwise it is hidden
   */
  public void setVisible(int spriteId, boolean isVisible)
  {
    if (spriteId < 0 || spriteId >= nbSprites)
      spriteId = 0;
    if (isVisible)
      idVisible = spriteId;
    else
      idVisible = -1;
  }

  /**
   * Returns the id of the visible sprite.
   * @return the id of the visible sprite, -1 if no sprite is visible
   */
  public int getIdVisible()
  {
    return idVisible;
  }

  /**
   * Returns the information, if the actor (with sprite id = 0) is visible or not.
   * @return true, if the actor is visible; otherwise false
   */
  public boolean isVisible()
  {
    return isVisible(0);
  }

  /**
   * Returns the information, if the actor with specified sprite id is visible or not.
   * @param spriteId the id of the sprite
   * @return true, if the actor is visible; otherwise false
   */
  public boolean isVisible(int spriteId)
  {
    return idVisible == spriteId ? true : false;
  }

  /**
   * Register a partner actor that becomes a collision candidate, e.t. that
   * is checked for collisions in every simulation cycle.
   * The collisions are reported by a collision listener that must be
   * registered with addCollisionListener().
   * Collisions are  detected by checking if the bounding boxes
   * of the visible sprites intersects.
   * @param partner the partner that is checked for collision
   */
  public void addCollisionCandidate(Actor partner)
  {
    collisionCandidates.add(partner);
  }

  /**
   * Register all actors in a list as collision candidates.
   * The collisions are reported by a collision listener that must be
   * registered with addCollisionListener().
   * @param partnerList a list of actors that are checked for collision
   */
  public void addCollisionCandidates(ArrayList<Actor> partnerList)
  {
    for (Actor a : partnerList)
      collisionCandidates.add(a);
  }

  protected void decreaseSimCount()
  {
    if (simCount > 0)
      simCount--;
  }

  protected boolean isCollisionRearmed()
  {
    if (simCount == 0)
      return true;
    return false;
  }

  /**
   * If set, the sprite image shown is mirrored horizontally.
   * @param enable if true, horizontal mirroring is enabled
   */
  public void setHorzMirror(boolean enable)
  {
    isHorzMirror = enable;
  }

  /**
   * If set, the sprite image shown is mirrored vertically.
   * @param enable if true, vertical mirroring is enabled
   */
  public void setVertMirror(boolean enable)
  {
    isVertMirror = enable;
  }

  /**
   * Enable/disable the invocation of act() in every simulation cycle.
   * @param enable if true, act() is invoked; otherwise act() is not invoked
   */
  public void setActEnabled(boolean enable)
  {
    isActEnabled = enable;
  }

  /**
   * Returns true, if act() is invoked in every simulation cycle.
   * @return true, if act() is called; false if the invokation of act() is disabled
   */
  public boolean isActEnabled()
  {
    return isActEnabled;
  }

  /**
   * Enable/disable the detection of collisions with the collision candidates.
   * @param enable if true, collisions will be notified
   */
  public void setCollisionEnabled(boolean enable)
  {
    isCollisionEnabled = enable;
  }

  /**
   * Returns true, if collision notification is enabled.
   * @return true, if collision detection is enabled
   */
  public boolean isCollisionEnabled()
  {
    return isCollisionEnabled;
  }

  /**
   * Returns simulation period.
   * @return the simulation period (in ms) (-1 if the actor is not yet added to the GameGrid)
   */
  public int getSimulationPeriod()
  {
    if (gameGrid == null)
      return -1;
    return simulationPeriod;
  }

  /**
   * Returns all actors of specified class in a specified distance.
   * The distance defines a circle around the current cell center. All actors in cells that intersects
   * with this circle are returned. Also cells outside the visible grid are considered.
   * To restrict this list to actors inside the grid, use isInGrid().
   * To get the 8 nearest neighbours, use distance = 1, to exclude diagonal
   * locations, use distance = 0.5;
   * Actors at the current location are not considered.
   * @param distance the distance in (fractional) cell units  public
   * @param clazz the class of the actors to look for; if null actors of all classes are included
   */
  public ArrayList<Actor> getNeighbours(double distance, Class clazz)
  {
    if (gameGrid == null)
      return null;
    ArrayList<Actor> a = new ArrayList<Actor>();
    for (Location loc : location.getNeighbourLocations(distance))
    {
      ArrayList<Actor> actors = gameGrid.getActorsAt(loc, clazz);
      for (Actor actor : actors)
        a.add(actor);
    }
    return a;
  }

  /**
   * Returns all actors in a specified distance.
   * The distance defines a circle around the current cell center. All actors in cells that intersects
   * with this circle are returned. Also cells outside the visible grid are considered.
   * To restrict this list to actors inside the grid, use isInGrid().
   * To get the 6 nearest neighbours, use distance = 1, to exlude diagonal
   * locations, use distance = 0.5;
   * Actors at the current location are not considered.
   * @param distance the distance in (fractional) cell units  public
   */
  public ArrayList<Actor> getNeighbours(double distance)
  {
    return getNeighbours(distance, null);
  }

  /**
   * Returns whether the actor is rotatable or not.
   * @return true, if rotatable, e.g. image rotates when direction changes
   */
  public boolean isRotatable()
  {
    return isRotatable;
  }
}

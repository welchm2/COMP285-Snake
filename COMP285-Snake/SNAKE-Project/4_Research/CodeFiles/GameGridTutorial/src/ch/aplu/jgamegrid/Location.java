// Location.java

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

import java.util.ArrayList;

/**
 * Class to represent the position of a cell (in cell coordinates).
 * Directions are measured in degrees clockwise from the positive x-axis.
 */
public class Location implements Comparable, Cloneable
{
  /**
   * The public horizontal coordinate (cell index) of the location.
   */
  public int x;

  /**
   * The public vertical coordinate (cell index) of the location.
   */
  public int y;

  /**
   * The turn angle for turning 90 degrees to the left.
   */
  public static final int LEFT = -90;

  /**
   * The turn angle for turning 90 degrees to the right.
   */
  public static final int RIGHT = 90;

  /**
   * The turn angle for turning 45 degrees to the left.
   */

  public static final int HALF_LEFT = -45;

  /**
   * The turn angle for turning 45 degrees to the right.
   */
  public static final int HALF_RIGHT = 45;

  /**
   * The turn angle for turning a full circle.
   */
  public static final int FULL_CIRCLE = 360;

  /**
   * The turn angle for turning a half circle.
   */
  public static final int HALF_CIRCLE = 180;

  /**
   * The turn angle for making no turn.
   */
  public static final int AHEAD = 0;

  /**
   * The compass direction for east.
   */
  public static final int EAST = 0;

  /**
   * The compass direction for southeast.
   */
  public static final int SOUTHEAST = 45;

  /**
   * The compass direction for south.
   */
  public static final int SOUTH = 90;

  /**
   * The compass direction for southwest.
   */
  public static final int SOUTHWEST = 135;

  /**
   * The compass direction for west.
   */
  public static final int WEST = 180;

  /**
   * The compass direction for northwest.
   */
  public static final int NORTHWEST = 225;

  /**
   * The compass direction for north.
   */
  public static final int NORTH = 270;

  /**
   * The compass direction for northeast.
   */
  public static final int NORTHEAST = 315;

  /**
   * Constructs a location at (0, 0).
   */
  public Location()
  {
    this.x = 0;
    this.y = 0;
  }

  /**
   * Constructs a location with given horizontal and vertical cell coordinates.
   * @param x the horizontal cell coordinate
   * @param y the vertical cell coordinate
   */
  public Location(int x, int y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructs a location with the coordinates of the given location.
   * @param location the location where to take the horizontal and vertical cell coordinates
   */
  public Location(Location location)
  {
    x = location.x;
    y = location.y;
  }

  /**
   * Gets the horizontal cell coordinate (index).
   * @return the horizontal coordinate
   */
  public int getX()
  {
    return x;
  }

  /**
   * Gets the vertical cell coordinate (index).
   * @return the vertical coordinate
   */
  public int getY()
  {
    return y;
  }

  /**
   * Gets the adjacent location of a cell where an displacement arrow from the current center of the
   * current cell with given direction and length = (distance + epsilon) * cellSize ends up.
   * For distance == 1, epsilon is 0.3. This will give the neighbour cells in the 8 compass
   * directions 45 degrees wide. For distance > 1, epsilon is -0.2.
   * @param direction the direction in which to find a adjacent location
   * @return the adjacent location in the direction where the displacement arrow ends up
   */
  public Location getAdjacentLocation(double direction, int distance)
  {
    if (distance == 1)
      return getAdjacentLocation(direction, distance, 0.3);
    else
      return getAdjacentLocation(direction, distance, -0.2);
  }

  /**
   * Same as getAdjacentLocation(double direction, 5).
   * @param direction the direction in which to find a adjacent location
   * @return the adjacent location in the direction where the displacement arrow ends up
   */
  public Location getAdjacentLocation(double direction)
  {
    return getAdjacentLocation(direction, 5, -0.2);
  }

  private Location getAdjacentLocation(double direction, int distance, double epsilon)
  {
    int xNew = (int)Math.floor(x + 0.5 +
      (distance + epsilon) * Math.cos(direction / 180 * Math.PI));
    int yNew = (int)Math.floor(y + 0.5 +
      (distance + epsilon) * Math.sin(direction / 180 * Math.PI));
    return (new Location(xNew, yNew));
  }

  /**
   * Gets one of the 8 surrounding cells in the compass directions 45 degrees wide.
   * @param direction the direction in which to find a neighbour location
   * @return one of the 8 neighbour cell locations
   */
  public Location getNeighbourLocation(double direction)
  {
    return getAdjacentLocation(direction, 1, 0.3);
  }

  /**
   * Returns the direction from this location toward another location. The
   * direction is rounded to the nearest compass direction.
   * @param target a location that is different from this location
   * @return the closest compass direction from this location toward
   * <code>target</code>
   */
  public int getDirectionToward(Location target)
  {
    int dx = target.getX() - getX();
    int dy = target.getY() - getY();
    // y axis points opposite to mathematical orientation
    int angle = (int)Math.toDegrees(Math.atan2(-dy, dx));

    // mathematical angle is counterclockwise from x-axis,
    // compass angle is clockwise from y-axis
    int compassAngle = RIGHT - angle;
    // prepare for truncating division by 45 degrees
    compassAngle += HALF_RIGHT / 2;
    // wrap negative angles
    if (compassAngle < 0)
      compassAngle += FULL_CIRCLE;
    // round to nearest multiple of 45
    return (compassAngle / HALF_RIGHT) * HALF_RIGHT;
  }

  /**
   * Indicates whether some other <code>Location</code> object is "equal to"
   * this one.
   * @param other the other location to test
   * @return <code>true</code> if <code>other</code> is a
   * <code>Location</code> with the same row and column as this location;
   * <code>false</code> otherwise
   */
  public boolean equals(Object other)
  {
    if (!(other instanceof Location))
      return false;

    Location otherLoc = (Location)other;
    return getX() == otherLoc.getX() && getY() == otherLoc.getY();
  }

  /**
   * Compares this location to <code>other</code> for ordering. Returns a
   * negative integer, zero, or a positive integer as this location is less
   * than, equal to, or greater than <code>other</code>. Locations are
   * ordered in row-major order. <br />
   * (Precondition: <code>other</code> is a <code>Location</code> object.)
   * @param other the other location to test
   * @return a negative integer if this location is less than
   * <code>other</code>, zero if the two locations are equal, or a positive
   * integer if this location is greater than <code>other</code>
   */
  public int compareTo(Object other)
  {
    Location otherLoc = (Location)other;
    if (getX() < otherLoc.getX())
      return -1;
    if (getX() > otherLoc.getX())
      return 1;
    if (getY() < otherLoc.getY())
      return -1;
    if (getY() > otherLoc.getY())
      return 1;
    return 0;
  }

  /**
   * Returns all locations in a specified distance.
   * The distance defines a circle around the current cell center. All cells that intersects
   * with this circle are returned. Also cells outside the visible grid are considered.
   * To restrict this list to cells inside the grid, use GameGrid.isInGrid().
   * To get the 6 nearest neighbours, use distance = 1, to exlude diagonal
   * locations, use distance = 0.5;
   * The current location is not included.
   *
   * @param distance the distance in (fractional) cell units
   */
  public ArrayList<Location> getNeighbourLocations(double distance)
  {
    ArrayList<Location> a = new ArrayList<Location>();
    int ymax = (int)(distance + 0.5);
    for (int dy = 1; dy <= ymax; dy += 1)
    {
      double y1 = dy - 0.5;
      double x1 = Math.sqrt(distance * distance - y1 * y1);
      int xmax = (int)(x1 + 0.5);
      for (int dx = 0; dx <= xmax; dx++)
      {
        a.add(new Location(x + dx, y + dy));
        a.add(new Location(x + dy, y - dx));
        a.add(new Location(x - dy, y + dx));
        a.add(new Location(x - dx, y - dy));
      }
    }
    return a;
  }
  
  /**
   * Returns a string that represents this location.
   * @return a string with horizontal and vertical coordinates of this location, in the format
   * (x, y)
   */
  public String toString()
  {
    return "(" + getX() + ", " + getY() + ")";
  }

  /**
   * Returns a new location with the duplicated coordinates.
   * @return a clone of the current location
   */
  public Location clone()
  {
    return new Location(this);
  }
}

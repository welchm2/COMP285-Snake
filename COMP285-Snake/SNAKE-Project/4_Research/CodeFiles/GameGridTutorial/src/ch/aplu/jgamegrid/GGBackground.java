// GGBackground.java

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

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.font.*;

/**
 * Class for drawing into the GameGrid background using standard Java2D graphics.
 * The size of the drawing area is determined by the number and size of the cells
 * when constructing the GameGrid: In pixel units, e.g. distance between adjacent pixel,
 * the width of the background is nbHorzCells * cellSize horizontally and
 * the height is nbVertCells * cellSize vertically. Thus the background contains
 * width + 1 pixels horizontally and height + 1 pixels vertically,
 * and the pixel coordinates are 0 <= i <= width (inclusive), 0 <= k <= height
 * (inclusive) respectively.<br>
 * Example: Constructing new GameGrid(600, 400, 1) will give a background with 601x401
 * pixels. x-pixel coordinates are in the range 0 <= x <= 600, y-pixel coordinates
 * in the range 0 <=y <= 400. The center is exactly at coordinate (300, 200).<br><br>
 *
 * Defaults:<br>
 * - paint color: white<br>
 * - line width 1 pixel<br>
 * - background color: black<br>
 * - font: SansSerif, Font.PLAIN, 24 pixel<br><br>
 *
 * GGBackground uses a offscreen buffer that is automatically rerendered to the screen
 * in every simulation cycle before the actor's act() methods are called. Eventually
 * it also contains the background image (if defined in the GameGrid constructor) and
 * a grid lines (defined by specifing the grid color in the GameGrid constructor). The current
 * content of the offscreen buffer may be saved and restored in an extra buffer using
 * save() and restore().
 */
public class GGBackground
{
  private GameGrid gameGrid;
  private BufferedImage bi;
  private Graphics2D g2D;
  private BufferedImage bgImage;
  private BufferedImage saveBuffer;
  private Graphics2D saveG2D;
  private int lineWidth = 1;
  private Color paintColor = Color.white;
  private int width;
  private int height;
  private int nbHorzCells;
  private int nbVertCells;
  private int nbHorzPix;
  private int nbVertPix;
  private int cellSize;
  private Color gridColor;
  private Font font = new Font("SansSerif", Font.PLAIN, 24);

  protected GGBackground(int nbHorzCells, int nbVertCells, int cellSize,
    Color gridColor, String bgImagePath, GameGrid gameGrid)
  {
    this.gameGrid = gameGrid;
    this.nbHorzCells = nbHorzCells;
    this.nbVertCells = nbVertCells;
    this.cellSize = cellSize;
    this.gridColor = gridColor;
    width = nbHorzCells * cellSize;  // in pixel units
    height = nbVertCells * cellSize;
    nbHorzPix = width + 1;
    nbVertPix = height + 1;

    // Create background buffer
    bi = new BufferedImage(nbHorzPix, nbVertPix, BufferedImage.TYPE_INT_RGB);
    g2D = bi.createGraphics();
    g2D.setColor(Color.black);
    g2D.fillRect(0, 0, nbHorzPix, nbVertPix);
    if (bgImagePath != null)
    {
      setBackgroundImage(bgImagePath);
      g2D.drawImage(bgImage, 0, 0, null);
    }
    if (gridColor != null)
      drawGridLines(gridColor);
  }

  protected BufferedImage getBufferedImage()
  {
    return bi;
  }

  /**
   * Saves the current background to an extra buffer.
   */
  public void save()
  {
    if (saveBuffer == null)
    {
      saveBuffer = new BufferedImage(nbHorzPix, nbVertPix, BufferedImage.TYPE_INT_RGB);
      saveG2D = saveBuffer.createGraphics();
    }
    saveG2D.drawImage(bi, 0, 0, null);
  }

  /**
   * Restores a previously saved background. Returns immediately if no save operation
   * was done yet.
   */
  public void restore()
  {
    if (saveBuffer == null)
      return;
    g2D.drawImage(saveBuffer, 0, 0, null);
  }

  /**
   * Clears the background buffer by painting it with the given color.
   * If necessary, draws an available background image and the grid lines into
   * the background buffer. The paint color remains unchanged.
   * @param color the color of the background
   */
  public void clear(Color color)
  {
    g2D.setColor(color);
    g2D.fillRect(0, 0, width, height);
    if (bgImage != null)
      g2D.drawImage(bgImage, 0, 0, null);
    if (gridColor != null)
      drawGridLines(gridColor);
    g2D.setColor(paintColor);
    gameGrid.repaint();
  }

  /**
   * Clears the background buffer by painting it with black.
   * If necessary, draw an available background image and the grid lines into
   * the background buffer. The paint color remains unchanged.
   */
  public void clear()
  {
    clear(Color.black);
  }

  /**
   * Draws the grid lines using the given color. The current paint color is unchanged.
   * @param color the color of the grid lines
   */
  public void drawGridLines(Color color)
  {
    Color oldColor = g2D.getColor();
    g2D.setColor(color);
    for (int i = 0; i <= nbHorzCells; i++)
      g2D.drawLine(i * cellSize, 0, i * cellSize, height);
    for (int k = 0; k <= nbVertCells; k++)
      g2D.drawLine(0, k * cellSize, width, k * cellSize);
    g2D.setColor(oldColor);
  }

  private void setBackgroundImage(String imagePath)
  {
    BufferedImage sourceImage = null;

    try
    {
      URL url = getClass().getClassLoader().getResource(imagePath);

      if (url == null)
      {
        GameGrid.fail("Error when constructing GameGrid.\nCan't find background image path: " + imagePath);
      }

      sourceImage = ImageIO.read(url);
    }
    catch (IOException e)
    {
      GameGrid.fail("Error when constructing GameGrid.\nFailed to load background image from path: " + imagePath);
    }

    GraphicsConfiguration gc =
      GraphicsEnvironment.getLocalGraphicsEnvironment().
      getDefaultScreenDevice().getDefaultConfiguration();
    bgImage =
      gc.createCompatibleImage(sourceImage.getWidth(),
      sourceImage.getHeight(),
      Transparency.BITMASK);

    bgImage.getGraphics().drawImage(sourceImage, 0, 0, null);
  }

  /**
   * Sets the current line width in pixels and return the previous line width.
   * @param width the new line width
   * @return the previous line width (in pixels)
   */
  public int setLineWidth(int width)
  {
    int w = lineWidth;
    lineWidth = width;
    return w;
  }

  /**
   * Sets the given new paint color (for drawing and filling) and return the previous color.
   * @param color the new line color
   * @return the previous line color
   */
  public Color setPaintColor(Color color)
  {
    Color c = paintColor;
    paintColor = color;
    return c;
  }

  /**
   * Draws a line from one coordinate pair to another coordinate pair.
   * The line width and color is determined by setLineWidth() and setPaintColor().
   * @param x1 the x-coordinate of the start point
   * @param y1 the y-coordinate of the start point
   * @param x2 the x-coordinate of the endpoint
   * @param y2 the y-coordinate of the endpoint
   */
  public void drawLine(int x1, int y1, int x2, int y2)
  {
    BasicStroke stroke = new BasicStroke(lineWidth);
    g2D.setStroke(stroke);
    g2D.setPaint(paintColor);
    Line2D line = new Line2D.Double(x1, y1, x2, y2);
    g2D.drawLine(x1, y1, x2, y2);
  }

  /**
   * Draws a line from one coordinate pair to another coordinate pair.
   * The line width and color is determined by setLineWidth() and setPaintColor().
   * @param pt1 the start point
   * @param pt2 the endpoint
   */
  public void drawLine(Point pt1, Point pt2)
  {
    drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
  }

  private void drawCircle(int xCenter, int yCenter, int radius, boolean fill)
  {
    if (lineWidth > 1)
    {
      BasicStroke stroke = new BasicStroke(lineWidth);
      g2D.setStroke(stroke);
    }
    g2D.setPaint(paintColor);

    int ulx = xCenter - radius;
    int uly = yCenter - radius;

    Ellipse2D.Double ellipse = new Ellipse2D.Double(ulx, uly, 2 * radius, 2 * radius);
    if (fill)
      g2D.fill(ellipse);
    else
    {
      g2D.draw(ellipse);
      System.out.println(ulx);
      System.out.println(uly);
    }
  }

  /**
   * Draws a circle with given center and given radius.
   * @param radius the radius of the circle
   */
  public void drawCircle(Point center, int radius)
  {
    drawCircle(center.x, center.y, radius, false);
  }

  /**
   * Draws a filled circle with given center and given radius.
   * @param center the center of the circle
   * @param radius the radius of the circle
   */
  public void fillCircle(Point center, int radius)
  {
    drawCircle(center.x, center.y, radius, true);
  }

  private void _drawRectangle(int x1, int y1, int x2, int y2, boolean fill)
  {
    if (lineWidth > 1)
    {
      BasicStroke stroke = new BasicStroke(lineWidth);
      g2D.setStroke(stroke);
    }
    g2D.setPaint(paintColor);
    Rectangle.Double rectangle = new Rectangle.Double(x1, y1, x2 - x1, y2 - y1);
    if (fill)
      g2D.fill(rectangle);
    else
      g2D.draw(rectangle);
  }

  /**
   * Draws a rectangle with given opposite corners.
   * @param pt1 upper left vertex of the rectangle
   * @param pt2 lower right vertex of the rectangle
   */
  public void drawRectangle(Point pt1, Point pt2)
  {
    _drawRectangle(pt1.x, pt1.y, pt2.x, pt2.y, false);
  }

  /**
   * Draws a filled rectangle with given opposite corners.
   * @param pt1 upper left vertex of the rectangle
   * @param pt2 lower right vertex of the rectangle
   */
  private void fillRectangle(Point pt1, Point pt2)
  {
    _drawRectangle(pt1.x, pt1.y, pt2.x, pt2.y, true);
  }

  private void drawArc(int xCenter, int yCenter, int radius, double startAngle, double extendAngle,
    boolean fill)
  {
    if (lineWidth > 1)
    {
      BasicStroke stroke = new BasicStroke(lineWidth);
      g2D.setStroke(stroke);
    }
    g2D.setPaint(paintColor);

    int ulx = xCenter - radius;
    int uly = yCenter - radius;

    Arc2D.Double arc =
      new Arc2D.Double(ulx, uly, 2 * radius, 2 * radius, startAngle, extendAngle,
      Arc2D.OPEN);
    if (fill)
      g2D.fill(arc);
    else
      g2D.draw(arc);
  }

  /**
   * Draws an arc with given center, radius, start and end angle.
   * @param pt the center of the arc
   * @param radius the radius of the arc
   * @param startAngle the start angle in degrees (zero to east, positive counterclockwise)
   * @param extendAngle the extend angle in degrees (zero to east, positive counterclockwise)
   */
  public void drawArc(Point pt, int radius, double startAngle, double extendAngle)
  {
    drawArc(pt.x, pt.y, radius, startAngle, extendAngle, false);
  }

  /**
   * Fills an arc with given center, radius, start and end angle.
   * @param pt the center of the arc
   * @param radius the radius of the arc
   * @param startAngle the start angle in degrees (zero to east, positive counterclockwise)
   * @param extendAngle the extendAngle in degrees (zero to east, positive counterclockwise)
   */
  public void fillArc(Point pt, int radius, double startAngle, double extendAngle)
  {
    drawArc(pt.x, pt.y, radius, startAngle, extendAngle, true);
  }

  private void drawPolygon(Point[] vertexes, boolean fill)
  {
    if (lineWidth > 1)
    {
      BasicStroke stroke = new BasicStroke(lineWidth);
      g2D.setStroke(stroke);
    }
    g2D.setPaint(paintColor);

    int size = vertexes.length;
    int[] x = new int[size];
    int[] y = new int[size];
    for (int i = 0; i < size; i++)
    {
      x[i] = vertexes[i].x;
      y[i] = vertexes[i].y;
    }

    Polygon polygon = new Polygon(x, y, size);
    if (fill)
      g2D.fill(polygon);
    else
      g2D.draw(polygon);
  }

  /**
   * Draws a polygon with given vertexes.
   * @param vertexes the vertexes of the polygon
   */
  public void drawPolygon(Point[] vertexes)
  {
    drawPolygon(vertexes, false);
  }

  /**
   * Draws a filled polygon with given vertexes.
   * @param vertexes the vertexes of the polygon
   */
  public void fillPolygon(Point[] vertexes)
  {
    drawPolygon(vertexes, true);
  }

  private void drawGeneralPath(GeneralPath gp, boolean fill)
  {
    if (lineWidth > 1)
    {
      BasicStroke stroke = new BasicStroke(lineWidth);
      g2D.setStroke(stroke);
    }
    g2D.setPaint(paintColor);

    if (fill)
      g2D.fill(gp);
    else
      g2D.draw(gp);
  }

  /**
   * Draws a figure defined by the given GeneralPath.
   * @param gp the GeneralPath that defines the shape
   */
  public void drawGeneralPath(GeneralPath gp)
  {
    drawGeneralPath(gp, false);
  }

  /**
   * Fills a figure defined by the given GeneralPath.
   * @param gp the GeneralPath that defines the shape
   */
  public void fillGeneralPath(GeneralPath gp)
  {
    drawGeneralPath(gp, true);
  }

  /**
   * Draws a single point. If the lineWidth is greater than 1,
   * more than one pixel may be învolved.
   * @param pt the point to draw
   */
  public void drawPoint(Point pt)
  {
    if (lineWidth > 1)
    {
      BasicStroke stroke = new BasicStroke(lineWidth);
      g2D.setStroke(stroke);
      g2D.setPaint(paintColor);
      Line2D line = new Line2D.Double(pt.x, pt.y, pt.x, pt.y);
      g2D.draw(line);
    }
    else
    {
      Graphics g = (Graphics)g2D;
      g.setColor(paintColor);
      g.drawLine(pt.x, pt.y, pt.x, pt.y);
    }
  }

  /**
   * Fills a cell (inner part, without grid boundary) with given color.
   * The paint color remains unchanged.
   * @param location the cell's location (cell indices).
   * @param fillColor the filling color of the cell
   */
  public void fillCell(Location location, Color fillColor)
  {
    int ulx = location.x * cellSize;
    int uly = location.y * cellSize;
    g2D.setPaint(fillColor);
    Rectangle.Double rectangle = new Rectangle.Double(ulx, uly, cellSize, cellSize);
    g2D.fill(rectangle);
    g2D.setPaint(paintColor);
  }

  /**
   * Returns the color of the pixel at given point.
   * @param pt point, where to pick the color
   * @return the color at the selected point
   */
  public Color getColor(Point pt)
  {
    return new Color(bi.getRGB(pt.x, pt.y));
  }

  /**
   * Returns the color of the pixel at given cell's center.
   * @param location cell's location where to pick the color
   * @return the color at the selected cell's center
   */
  public Color getColor(Location location)
  {
    Point pt = gameGrid.toPoint(location);
    return getColor(pt);
  }

  /**
   * Sets the font for displaying text with setText()
   * @param font
   */
  public void setFont(Font font)
  {
    this.font = font;
  }

  /**
   * Displays the given text at the given position using the current font.
   * @param text the text to display
   * @param pt the start point of the text baseline
   */
  public void drawText(String text, Point pt)
  {
    FontRenderContext frc = g2D.getFontRenderContext();
    TextLayout textLayout = new TextLayout(text, font, frc);
    g2D.setColor(paintColor);
    textLayout.draw(g2D, pt.x, pt.y);
  }

  /**
   * Returns the available font families for the current platform.
   * @return a string that describes the available font families
   */
  public static String[] getAvailableFontFamilies()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String s[] = ge.getAvailableFontFamilyNames();
    return s;
  }

  /**
   * Return the graphics device context of the background.
   * @return the Graphics2D of the background
   */
  public Graphics2D getContext()
  {
    return g2D;
  }
}

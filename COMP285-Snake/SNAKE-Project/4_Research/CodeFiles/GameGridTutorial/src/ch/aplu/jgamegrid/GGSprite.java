// GGSprite.java

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
import java.awt.image.*;
import java.awt.geom.*;

/**
 * A sprite to be displayed on the screen. Note that a sprite
 * contains no state information, i.e. its just the image and 
 * not the location. This allows us to use a single sprite in
 * lots of different places without having to store multiple 
 * copies of the image.
 */
class GGSprite
{
  // The images to be drawn for this sprite

  private BufferedImage[] images = new BufferedImage[64];
  // The standard collision areas (when direction is 0)

  protected GGSprite(BufferedImage[] images)
  {
    for (int i = 0; i < 64; i++)
    {
      this.images[i] = images[i];
    }
  }


  /**
   * Returns the width of the sprite with given id.
   * @param spriteId the id of the sprite
   * @return The width in pixels of this sprite
   */
  public int getWidth(int spriteId)
  {
    return images[spriteId].getWidth(null);
  }

  /**
   * Returns the width of the sprite with id = 0.
   * @return The width in pixels of this sprite
   */
  public int getWidth()
  {
    return getWidth(0);
  }

  /**
   * Returns the height of the sprite with given id.
   * @param spriteId the id of the sprite
   * @return The height in pixels of this sprite
   */
  public int getHeight(int spriteId)
  {
    return images[spriteId].getHeight(null);
  }

  /**
   * Returns the height of the sprite with id = 0.
   * @return The height in pixels of this sprite
   */
  public int getHeight()
  {
    return getHeight(0);
  }

  public BufferedImage[] getImages()
  {
    return images;
  }

  /**
   * Draws the sprite onto the graphics context provided.
   * @param g2D The graphics context on which to draw the sprite
   * @param x The x location in pixel coordinates at which to draw the sprite
   * @param y The y location in pixel coordinates at which to draw the sprite
   * @param rotationIndex the index of the rotated images (0..15)
   */
  public void draw(Graphics2D g2D, int x, int y, int rotationIndex, boolean isHorzMirror, boolean isVertMirror)
  {
    // Somewhat optimized for speed and not for pretty code
    if (!isHorzMirror && !isVertMirror)
    {
      g2D.drawImage(images[rotationIndex], x, y, null);
      return;
    }

    AffineTransform at = new AffineTransform();
    if (isHorzMirror && !isVertMirror)
    {
      at.scale(-1, 1);
      at.translate(-x - getWidth(), y);
    }
    if (!isHorzMirror && isVertMirror)
    {
      at.scale(1, -1);
      at.translate(x, -y - getHeight());
    }
    if (isHorzMirror && isVertMirror)
    {
      at.scale(-1, -1);
      at.translate(-x - getWidth(), -y - getHeight());
    }
    g2D.drawImage(images[rotationIndex], at, null);
  }
}

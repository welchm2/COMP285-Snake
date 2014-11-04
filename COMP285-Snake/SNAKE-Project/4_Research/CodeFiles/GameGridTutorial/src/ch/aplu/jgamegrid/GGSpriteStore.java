// GGSpriteStore.java

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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * A resource manager for sprites in the game. The sprite is identified via
 * its image path. When it is requested the first time, the image is loaded and
 * the image path/instance reference pair store in a Hash map. When request again,
 * the instance reference is retrieved from the Hash map (idea from Kevin Glass).
 */
class GGSpriteStore
{
  /* The single instance of this class */
  private static GGSpriteStore single = new GGSpriteStore();

  /* The cached sprite map, from reference to sprite instance */
  private HashMap sprites = new HashMap();

  /**
   * Returns the single instance of this class.
   * @return The single instance of this class
   */
  protected static GGSpriteStore get()
  {
    return single;
  }

  /**
   * Retrieves a rotatable sprite from the store.
   * If the sprite is not yet in the store, it is loaded from the given disk file.
   * @param imagePath the fully qualified path to the image to use for the sprite
   * @return the sprite instance reference containing the accelerated image
   */
  protected GGSprite getSprite(String imagePath)
  {
    return getSprite(imagePath, false);
  }

  /**
   * Retrieves a sprite from the store.
   * If isRotatable is false, only the default sprite image is stored.
   * This saves a lot of memory compared to a rotatable sprite.
   * If the sprite is not yet in the store, it is loaded from the given disk file.
   * @param imagePath the fully qualified path to the image to use for the sprite
   * @param isRotatable if true, a rotated actor's image is stored for every 360 / 64 = 5.625 degrees
   * @return the sprite instance reference containing the accelerated image
   */
  protected GGSprite getSprite(String imagePath, boolean isRotatable)
  {
    // if we've already got the sprite in the cache
    // then just return the existing version
    if (sprites.get(imagePath) != null)
    {
      return (GGSprite)sprites.get(imagePath);
    }

    // otherwise, grab the sprite from the resource loader
    BufferedImage sourceImage = null;

    try
    {
      URL url = getClass().getClassLoader().getResource(imagePath);

      if (url == null)
      {
        fail("Error in SpriteStore.getSprite().\nCan't find image path: " + imagePath);
      }

      sourceImage = ImageIO.read(url);
    }
    catch (IOException e)
    {
      fail("Error in SpriteStore.getSprite().\nFailed to load: " + imagePath);
    }

    int w = sourceImage.getWidth();
    int h = sourceImage.getHeight();

    // create an accelerated image of the right size to store our sprite in
    GraphicsConfiguration gc =
      GraphicsEnvironment.getLocalGraphicsEnvironment().
      getDefaultScreenDevice().getDefaultConfiguration();

    BufferedImage[] bi = new BufferedImage[64];

    if (isRotatable)
    {
      int s = (int)Math.ceil(Math.sqrt(w * w + h * h));
      BufferedImage biTmp = gc.createCompatibleImage(s, s, Transparency.BITMASK);
      Graphics2D gTmp = biTmp.createGraphics();
      gTmp.drawImage(sourceImage, (s - w) / 2, (s - h) / 2, null);

      for (int i = 0; i < 64; i++)
      {
        bi[i] = gc.createCompatibleImage(s, s, Transparency.BITMASK);
        Graphics2D g2D = bi[i].createGraphics();

        g2D.translate(s / 2, s / 2); // Translate the coordinate system (zero a image's center)
        g2D.rotate(Math.toRadians(360.0 / 64 * i));  // Rotate the image
        g2D.translate(-s / 2, -s / 2); // Translate the coordinate system (zero a image's center)
        g2D.drawImage(biTmp, 0, 0, null);
      }
      gTmp.dispose();
    }
    else
    {
      bi[0] = gc.createCompatibleImage(
        sourceImage.getWidth(),
        sourceImage.getHeight(),
        Transparency.BITMASK);

      Graphics2D g2D = bi[0].createGraphics();
      g2D.drawImage(sourceImage, 0, 0, null);
    }

    // create a sprite, add it to the cache then return it
    GGSprite sprite = new GGSprite(bi);
    sprites.put(imagePath, sprite);

    return sprite;
  }

  private void fail(String message)
  {
    System.err.println(message);
    System.exit(0);
  }
}

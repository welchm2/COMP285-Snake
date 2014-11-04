// GameGrid.java

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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import ch.aplu.util.*;

/**
 * Class to create a container where the actors live in. It is a two dimensional
 * grid of cells. The size of cells can be specified (in pixel units = distance
 * between two adjacent pixels) at GrameGrid's creation time,
 * and is constant after creation. Simple scenarios may use large cells that
 * entirely contain the representations of objects in a single cell.
 * More elaborate scenarios may use smaller cells (down to a single pixel unit)
 * to achieve fine-grained placement and smoother animation.
 * The GameGrid background can be decorated with drawings or a background
 * image.<br><br>
 *
 * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
 * The size in pixel units of the usable playground is width = nbHorzCells * cellSize horizontally and
 * height = nbVertCells * cellSize vertically. Thus the playground contains nbHorzPixels = width + 1 pixels horizontally and
 * nbVertPixels = height + 1 pixels vertically, with pixel indexes 0 <= i <= width (inclusive),
 * 0 <= k <= height (inclusive) respectively.<br><br>
 *
 * For pixel accurate positioning be aware that an image that has the size m x n in pixel units
 * contains m+1 pixels horizontally and n+1 pixels vertically.
 * So the background image must have nbVertPixels horizontally and
 * nbVerticalPixels vertically to fit exactly into the playground.
 * (E.g.  GameGrid(60, 50, 10) will need a background image with 601 x 501 pixels.) <br><br>
 *
 * The x- and y-coordinates for positioning actors are actually the cell index ranging from
 * 0 <= x < nbHorzCells (exclusive) horizontally and 0 <= y < nbVertCells (exclusive) vertically.
 * There image is automatically centered in the cell as accurate as possible.<br><br>
 *
 * The GameGrid implements and registers the GGKeyListener interface. The notification method keyHit()
 * has an empty body and returns false to indicate that other registered KeyListeners will get the notification.
 * It may be overridden to get your own notification.<br><br>
 *
 * The class design is inspired by the great Greenfoot programming environment
 * (see www.greenfoot.org) and by an article about game programming in Java
 * at www.cokeandcode.com/tutorials with thanks to the authors. The code
 * is entirely rewritten and in our responsability.
 *
 * A extended sound library is included that supports MP3.<br><br>
 * 
 * Using a sound converter integrated in the native DLL soundtouch.dll
 * (source from www.surina.net/soundtouch)
 * you may change pitch and tempo independently.
 * For the compilation you need  the package ch.aplu.jaw and the
 * native DLL soundtouch.dll must be found in the system path. It only  works
 * on Windows machines.
 */
public class GameGrid implements GGKeyListener
{
// --------------- Inner class MyMouseAdapter ----------------

  private class MyMouseAdapter implements MouseListener, MouseMotionListener
  {

    public void mousePressed(MouseEvent evt)
    {
      int modifiers = evt.getModifiers();
      if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
        notifyMouseEvent(evt, GGMouse.lPress);
      if ((modifiers & InputEvent.BUTTON2_MASK) != 0 ||
        (modifiers & InputEvent.BUTTON3_MASK) != 0)
        notifyMouseEvent(evt, GGMouse.rPress);
    }

    public void mouseReleased(MouseEvent evt)
    {
      int modifiers = evt.getModifiers();
      if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
        notifyMouseEvent(evt, GGMouse.lRelease);
      if ((modifiers & InputEvent.BUTTON2_MASK) != 0 ||
        (modifiers & InputEvent.BUTTON3_MASK) != 0)
        notifyMouseEvent(evt, GGMouse.rRelease);
    }

    public void mouseDragged(MouseEvent evt)
    {
      int modifiers = evt.getModifiers();
      if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
        notifyMouseEvent(evt, GGMouse.lDrag);
      if ((modifiers & InputEvent.BUTTON2_MASK) != 0 ||
        (modifiers & InputEvent.BUTTON3_MASK) != 0)
        notifyMouseEvent(evt, GGMouse.rDrag);
    }

    public void mouseClicked(MouseEvent evt)
    {
      int modifiers = evt.getModifiers();
      if (evt.getClickCount() == 1)
      {
        if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
          notifyMouseEvent(evt, GGMouse.lClick);
        if ((modifiers & InputEvent.BUTTON2_MASK) != 0 ||
          (modifiers & InputEvent.BUTTON3_MASK) != 0)
          notifyMouseEvent(evt, GGMouse.rClick);
      }
      if (evt.getClickCount() == 2)
      {
        if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
          notifyMouseEvent(evt, GGMouse.lDClick);
        if ((modifiers & InputEvent.BUTTON2_MASK) != 0 ||
          (modifiers & InputEvent.BUTTON3_MASK) != 0)
          notifyMouseEvent(evt, GGMouse.rDClick);
      }
    }

    public void mouseEntered(MouseEvent evt)
    {
      notifyMouseEvent(evt, GGMouse.enter);
    }

    public void mouseExited(MouseEvent evt)
    {
      notifyMouseEvent(evt, GGMouse.leave);
    }

    public void mouseMoved(MouseEvent evt)
    {
      notifyMouseEvent(evt, GGMouse.move);
    }

    private void notifyMouseEvent(MouseEvent evt, int mask)
    {
      if (!isMouseEnabled)
        return;
      synchronized (monitor)
      {
        for (int i = 0; i < mouseListeners.size(); i++)
        {
          GGMouseListener listener = mouseListeners.get(i);
          int mouseEventMask = mouseEventMasks.get(i);
          if ((mouseEventMask & mask) != 0)
          {
            GGMouse mouse = GGMouse.create(listener, mask, evt.getX(), evt.getY());
            if (listener.mouseEvent(mouse))
              return;
          }
        }
      }
    }
  }

  // --------------- Inner class MyKeyAdapter ----------------
  private class MyKeyAdapter implements KeyListener
  {

    public void keyPressed(KeyEvent evt)
    {
      synchronized (monitor)
      {
        keyCode = evt.getKeyCode();
        keyChar = evt.getKeyChar();
        keyModifiers = evt.getModifiers();
        keyModifiersText = KeyEvent.getKeyModifiersText(keyModifiers);
        gotKey = true;
        for (GGKeyListener kal : keyListeners)
          if (kal.keyHit(keyCode))
            return;
      }
    }

    public void keyReleased(KeyEvent evt)
    {
    }

    public void keyTyped(KeyEvent evt)
    {
    }
  }

  // --------------- Inner class GameThread ------------------
  private class GameThread extends Thread
  {

    public void run()
    {
      while (isThreadRunning)
      {
        if (isRunning)
        {
          timer.start();
          actAll();
          while (!isSingleStep && timer.isRunning())
            delay(1);

          if (isSingleStep)
          {
            isRunning = false;
            isSingleStep = false;
          }
        }
        else  // Paused
        {
          isPaused = true;
          GameGrid.delay(10);
        }
      }
    }
  }
  // --------------- End of inner classes --------------------
  //
  protected static final boolean debug = true;  // show debug information
  private static GGBackground background;
  private static final int MIN_VALUE = 0, MAX_VALUE = 3000;
  private volatile boolean isThreadRunning = true;
  private volatile boolean isRunning = false;
  private volatile boolean isPaused = false;
  private volatile boolean isSingleStep = false;
  private LinkedList<Actor> scene = new LinkedList<Actor>();
  private BufferStrategy strategy;
  private JPanel contentPane;
  private JFrame frame;
  private Canvas canvas;
  private GameThread gameThread;
  private static int nbHorzCells; // number of horizonal
  private static int nbVertCells; //  and vertical cells
  private static int cellSize;  // size of cell in pixel units
  private static int width; // width of playground in pixel units
  private static int height;  // width of playground in pixel units
  private static int nbHorzPix; // number of horizontal pixels involved
  private static int nbVertPix; // number of vertical pixels involved
  private int simulationPeriod = 200000;  // default simulatin period (200 ms)
  private HiResAlarmTimer timer = new HiResAlarmTimer(simulationPeriod);
  private JSlider speedSlider;
  private JButton stepBtn = new JButton("Step");
  private JButton runBtn = new JButton("Run");
  private JButton resetBtn = new JButton("Reset");
  private char keyChar;
  private int keyCode;
  private int keyModifiers;
  private String keyModifiersText;
  private ArrayList<GGKeyListener> keyListeners = new ArrayList<GGKeyListener>();
  private ArrayList<GGMouseListener> mouseListeners = new ArrayList<GGMouseListener>();
  private ArrayList<Integer> mouseEventMasks = new ArrayList<Integer>();
  private volatile boolean gotKey = false;
  private Object monitor = new Object();
  private boolean isMouseListenerAdded = false;
  private boolean isMouseMotionListenerAdded = false;
  private boolean isMouseEnabled;
  private GGActionListener actionListener = null;
  private final String[] sounds =
  {
    "res/cow.wav", "res/frog.wav", "res/bell.wav", "res/bird.wav",
    "res/bong.wav", "res/boing.wav", "res/cock.wav", "res/dog.wav",
    "res/explode.wav", "res/fade.wav", "res/giggle.wav", "res/gun.wav",
    "res/horse.wav", "res/pig.wav", "res/mmmm.wav", "res/smack.wav",
    "res/buzz.wav", "res/telephone.wav", "res/tick.wav", "res/ping.wav"
  };
  private int nbSounds = sounds.length;
  /**
   * Constant for playing the sound of a cow.
   */
  public static final int COW = 0;
  /**
   * Constant for playing the sound of a frog.
   */
  public static final int FROG = 1;
  /**
   * Constant for playing the sound of a bell.
   */
  public static final int BELL = 2;
  /**
   * Constant for playing the sound of a bird.
   */
  public static final int BIRD = 3;
  /**
   * Constant for playing a bong sound.
   */
  public static final int BONG = 4;
  /**
   * Constant for playing a boing sound.
   */
  public static final int BOING = 5;
  /**
   * Constant for playing the sound of a cock.
   */
  public static final int COCK = 6;
  /**
   * Constant for playing the sound of a dog.
   */
  public static final int DOG = 7;
  /**
   * Constant for playing the sound of an explosion.
   */
  public static final int EXPLOSION = 8;
  /**
   * Constant for playing a fading sound.
   */
  public static final int FADE = 9;
  /**
   * Constant for playing a giggle sound.
   */
  public static final int GIGGLE = 10;
  /**
   * Constant for playing the sound of a gun.
   */
  public static final int GUN = 11;
  /**
   * Constant for playing the sound of a horse.
   */
  public static final int HORSE = 12;
  /**
   * Constant for playing the sound of a pig.
   */
  public static final int PIG = 13;
  /**
   * Constant for playing the sound of mmmm.
   */
  public static final int MMMM = 14;
  /**
   * Constant for playing the sound of a smack.
   */
  public static final int SMACK = 15;
  /**
   * Constant for playing the sound of a buzzer.
   */
  public static final int BUZZ = 16;
  /**
   * Constant for playing the sound of a telephone.
   */
  public static final int TELEPHONE = 17;
  /**
   * Constant for playing the sound of a tick.
   */
  public static final int TICK = 18;
  /**
   * Constant for playing the sound of a ping.
   */
  public static final int PING = 19;

  /**
   * Constructs the game's playground of 10 by 10 cells (60 pixels wide)
   * with a navigation bar and a visible red grid but no background image.
   */
  public GameGrid()
  {
    this(10, 10, 60, Color.red);
  }

  /**
   * Constructs the game's playground of 10 by 10 cells (60 pixels wide)
   * with possibly a navigation bar and a visible red grid but no background image.
   * @param isNavigation if true, a navigation bar is shown
   */
  public GameGrid(boolean isNavigation)
  {
    this(10, 10, 60, Color.red, null, isNavigation);
  }

  /**
   * Constructs the game's playground with a navigation bar and no visible grid
   * and no background image.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixelUnits) of the cell
   */
  public GameGrid(int nbHorzCells, int nbVertCells, int cellSize)
  {
    this(nbHorzCells, nbVertCells, cellSize, null, null, true);
  }

  /**
   * Constructs the game's playground with no visible grid
   * and no background image, but possibly a navigation bar.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixel units) of the cell
   * @param isNavigation if true, a navigation bar is shown
   */
  public GameGrid(int nbHorzCells, int nbVertCells, int cellSize, boolean isNavigation)
  {
    this(nbHorzCells, nbVertCells, cellSize, null, null, isNavigation);
  }

  /**
   * Constructs the game's playground with a navigation bar and a possibly a background image,
   * but no visible grid.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixel units) of the cell
   * @param bgImagePath the path to a background image (if null, no background image)
   */
  public GameGrid(int nbHorzCells, int nbVertCells, int cellSize, String bgImagePath)
  {
    this(nbHorzCells, nbVertCells, cellSize, null, bgImagePath, true);
  }

  /**
   * Constructs the game's playground with a navigation bar and a possibly a visible grid,
   * but no background image.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixel units) of the cell
   * @param gridColor the color of the grid (if null, no grid is shown)
   */
  public GameGrid(int nbHorzCells, int nbVertCells, int cellSize, Color gridColor)
  {
    this(nbHorzCells, nbVertCells, cellSize, gridColor, null, true);
  }

  /**
   * Constructs the game's playground with a navigation bar, possibly a visible grid
   * and possibly a background image.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixel units) of the cell
   * @param gridColor the color of the grid (if null, no grid is shown)
   * @param bgImagePath the path to a background image (if null, no background image)
   */
  public GameGrid(int nbHorzCells, int nbVertCells, int cellSize, Color gridColor, String bgImagePath)
  {
    this(nbHorzCells, nbVertCells, cellSize, gridColor, bgImagePath, true);
  }

  /**
   * Constructs the game's playground with possibly a navigation bar, possibly a visible grid
   * and no background image.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixel units) of the cell
   * @param gridColor the color of the grid (if null, no grid is shown)
   * @param isNavigation if true, a navigation bar is shown
   */
  public GameGrid(int nbHorzCells, int nbVertCells, int cellSize, Color gridColor, boolean isNavigation)
  {
    this(nbHorzCells, nbVertCells, cellSize, gridColor, null, isNavigation);
  }

  /**
   * Constructs the game's playground with possibly a navigation bar, possibly a visible grid
   * and possibly a background image.
   * The cellSize is given in pixel units, e.g. the distance between adjacent pixels.
   * @param nbHorzCells the number of horizontal cells
   * @param nbVertCells the number of vertical cells
   * @param cellSize the side length (in pixel units) of the cell
   * @param gridColor the color of the grid (if null, no grid is shown)
   * @param bgImagePath the path to a background image (if null, no background image)
   * @param isNavigation if true, a navigation bar is shown
   */
  public GameGrid(final int nbHorzCells, final int nbVertCells, final int cellSize,
    final Color gridColor, final String bgImagePath, final boolean isNavigation)
  {
    if (SwingUtilities.isEventDispatchThread())
      init(nbHorzCells, nbVertCells, cellSize, gridColor, bgImagePath, isNavigation);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            init(nbHorzCells, nbVertCells, cellSize, gridColor,
              bgImagePath, isNavigation);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  private void init(int nbHorzCells, int nbVertCells, int cellSize, Color gridColor,
    String bgImagePath, boolean isNavigation)
  {

    this.cellSize = cellSize;
    this.nbHorzCells = nbHorzCells;
    this.nbVertCells = nbVertCells;
    width = nbHorzCells * cellSize;  // in pixel units
    height = nbVertCells * cellSize;
    nbHorzPix = width + 1;
    nbVertPix = height + 1;

    canvas = new Canvas();
    frame = new JFrame("JGameGrid");
    contentPane = (JPanel)frame.getContentPane();
    if (isNavigation)
      contentPane.setPreferredSize(new Dimension(nbHorzPix, nbVertPix + 40));
    else
      contentPane.setPreferredSize(new Dimension(nbHorzPix, nbVertPix));

    speedSlider = new JSlider(MIN_VALUE, MAX_VALUE, (int)(434.0 * Math.log(simulationPeriod / 1000)));
    speedSlider.setPreferredSize(new Dimension(100, speedSlider.getPreferredSize().height));
    speedSlider.setMaximumSize(speedSlider.getPreferredSize());
    speedSlider.setInverted(true);

    JPanel dlgPanel = new JPanel();
    dlgPanel.setPreferredSize(new Dimension(100, 40));
    stepBtn.setPreferredSize(new Dimension(70, 25));
    runBtn.setPreferredSize(new Dimension(70, 25));
    resetBtn.setPreferredSize(new Dimension(70, 25));
    dlgPanel.add(stepBtn);
    dlgPanel.add(runBtn);
    dlgPanel.add(resetBtn);
    dlgPanel.add(new JLabel("     "));
    dlgPanel.add(new JLabel("Slow"));
    dlgPanel.add(speedSlider);
    dlgPanel.add(new JLabel("Fast"));
    canvas.addKeyListener(new MyKeyAdapter());
    canvas.setFocusable(true);  // Needed to get the key events

    if (isNavigation)
    {
      contentPane.add(canvas, BorderLayout.CENTER);
      contentPane.add(dlgPanel, BorderLayout.SOUTH);
    }
    else
      contentPane.add(canvas);

    // Tell AWT not to bother repainting our canvas since we will
    // do that ourself in accelerated mode
    canvas.setIgnoreRepaint(true);

    frame.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension size = frame.getSize();
    screenSize.height = screenSize.height / 2;
    screenSize.width = screenSize.width / 2;
    size.height = size.height / 2;
    size.width = size.width / 2;
    int y = screenSize.height - size.height;
    int x = screenSize.width - size.width;
    frame.setLocation(x, y);

    frame.addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent evt)
      {
        isThreadRunning = false;
        try
        {
          gameThread.join(5000);
        }
        catch (InterruptedException ex)
        {
        }
        System.exit(0);
      }

      // Because automatic repaint is disabled, we must repaint the
      // graphics ourself
      public void windowActivated(WindowEvent evt)
      {
        repaint();
        canvas.requestFocus();  // Needed to get the key events
        if (debug)
          System.out.println("window activated");
      }

      public void windowGainedFocus(WindowEvent evt)
      {
        repaint();
        canvas.requestFocus();  // Needed to get the key events
        if (debug)
          System.out.println("window focus gained");
      }

      public void windowDeactivated(WindowEvent e)
      {
        if (debug)
          System.out.println("window deactivated");
      }

      public void windowLostFocus(WindowEvent e)
      {
        if (debug)
          System.out.println("window focus lost");
      }

      public void windowStateChanged(WindowEvent e)
      {
        if (debug)
          System.out.println("window state changed");
      }
    });

    frame.addComponentListener(new ComponentAdapter()
    {

      public void componentMoved(ComponentEvent evt)
      {
        repaint();
        canvas.requestFocus();  // Needed to get the key events
        if (debug)
          System.out.println("window component moved");
      }

      public void componentHidden(ComponentEvent e)
      {
        if (debug)
          System.out.println("window component hidden");
      }

      public void componentResized(ComponentEvent e)
      {
        repaint();
        canvas.requestFocus();  // Needed to get the key events
        if (debug)
          System.out.println("window component resized");
      }

      public void componentShown(ComponentEvent e)
      {
        repaint();
        canvas.requestFocus();  // Needed to get the key events
        if (debug)
          System.out.println("window component shown");
      }
    });


    // Create the buffering strategy which will allow AWT
    // to manage our accelerated graphics
    canvas.createBufferStrategy(2);
    strategy = canvas.getBufferStrategy();

    runBtn.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent evt)
      {
        if (isRunning)
          doPause();
        else
          doRun();
        canvas.requestFocus();  // Needed to get the key events
      }
    });


    resetBtn.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent evt)
      {
        runBtn.setText("Run");
        doReset();
        canvas.requestFocus();  // Needed to get the key events
      }
    });

    stepBtn.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent evt)
      {
        doStep();
      }
    });

    speedSlider.addChangeListener(new ChangeListener()
    {

      public void stateChanged(ChangeEvent evt)
      {
        int value = ((JSlider)evt.getSource()).getValue();
        int time = (int)Math.exp(value / 434.0);
        setSimulationPeriod(time, false);
        canvas.requestFocus();  // Needed to get the key events
      }
    });

    background = new GGBackground(nbHorzCells, nbVertCells, cellSize,
      gridColor, bgImagePath, this);

    addKeyListener(this);

    gameThread = new GameThread();
    gameThread.setPriority(Thread.MAX_PRIORITY);
    gameThread.start();
  }

  /**
   * Returns the width (horizontal size) in pixel units. The number of pixels in the
   * horizontal direction is width + 1.
   * @return the horizotal size in pixel units.
   */
  public static int getWidth()
  {
    return width;
  }

  /**
   * Returns the height (vertical size) in pixel units. The number of pixels in the
   * vertical direction is height + 1.
   * @return the vertical size in pixel units.
   */
  public static int getHeight()
  {
    return height;
  }

  /**
   * Returns the horizontal number of cells.
   * @return the number of cells in horizontal direction
   */
  public static int getNbHorzCells()
  {
    return nbHorzCells;
  }

  /**
   * Returns the vertical number of cells.
   * @return the number of cells in vertical direction
   */
  public static int getNbVertCells()
  {
    return nbVertCells;
  }

  /**
   * Returns a reference to the background of the GameGrid.
   * @return a reference to the background object
   */
  public static GGBackground getBackground()
  {
    return background;
  }

  /**
   * Add an actor at given starting position to the scene. If the actor
   * is already in the scene, the existing actor will be removed. This garantees
   * that an actor is only contained once in the scene.
   * The starting location is saved and may be retrieved later. The actor
   * reset notification is called in order an initialisation may be done.
   * The actor's initial direction is set to 0 (to the east).
   * If the position is outside the grid, returns immediately. More than one
   * actor may be added at the same location.
   * @param actor the actor to be added at the end of the linked list
   * @param location the location of the actor (cell indices)
   */
  public void addActor(final Actor actor, Location location)
  {
    addActor(actor, location, 0);
  }

  /**
   * Add an actor at given starting position to the scene. If the actor
   * is already in the scene, the existing actor will be removed. This garantees
   * that an actor is only contained once in the scene.
   * The starting location is saved and may be retrieved later. The actor
   * reset notification is called in order an initialisation may be done.
   * If the position is outside the grid, returns immediately. More than one
   * actor may be added at the same location.
   * The image center is positioned at the given location. For even image pixel
   * width or height, the center is half pixel width to the left or resp. to the top.
   * @param actor the actor to be added at the end of the linked list
   * @param location the location of the actor (cell indices)
   * @param direction the direction (clockwise in degrees, 0 to east)
   */
  public void addActor(final Actor actor, Location location, int direction)
  {
    scene.remove(actor); // Remove if already there, actors should not be duplicated in scene
    actor.setGameGrid(this);
    actor.setX(location.x);
    actor.setY(location.y);
    actor.setDirection(direction);
    actor.initStart();
    actor.setSimulationPeriod(simulationPeriod);
    scene.add(actor);
  }

  private void act(boolean doAct)
  {
    if (debug)
       System.out.println("calling JGameGrid.act() with doAct = " + doAct);
    synchronized (monitor)  // prevent conflicts with key events
    {
      // Must use a copy of scene, because act() act may modify scene
      LinkedList<Actor> tmp = (LinkedList<Actor>)scene.clone();
      for (Actor a : tmp)
      {
        if (doAct && a.isActEnabled())
          a.act();
      }
      if (doAct && actionListener != null)
      {
        actionListener.act(null);
      }

      Graphics2D g2D = (Graphics2D)strategy.getDrawGraphics();
      g2D.drawImage(background.getBufferedImage(), 0, 0, null);
      g2D.setClip(0, 0, nbHorzPix, nbVertPix);

      for (Actor a : tmp)
        a.draw(g2D);

      g2D.dispose();
      strategy.show();
    }
  }

  /**
   * Redraws the current game situation.
   */
  public void repaint()
  {
    if (debug)
      System.out.println("calling repaint()");
    act(false);
  }

  /**
   * Shows the GameGrid after initialisation or when hided.
   */
  public void show()
  {
    try
    {
      EventQueue.invokeAndWait(new Runnable()
      {

        public void run()
        {
          frame.setVisible(true);
          frame.setUndecorated(true);
          frame.setResizable(false);
          frame.setUndecorated(false);

        }
      });
    }
    catch (Exception ex)
    {
    }
  }

  /**
   * Hides the GameGrid, but does not destroy it.
   */
  public void hide()
  {
    try
    {
      EventQueue.invokeAndWait(new Runnable()
      {

        public void run()
        {
          frame.setVisible(false);
        }
      });
    }
    catch (Exception ex)
    {
    }
  }

  /**
   * Invokes all actor's act() methods in the order of the scene
   * and draws the new game situation.
   * Corresponds to the next simulation act.
   */
  public void actAll()
  {
    act(true);
  }

  /**
   * Returns the size of a cell (in pixels).
   * @return the size of a cell
   */
  public static int getCellSize()
  {
    return cellSize;
  }

  /**
   * Set the act order of objects in the world.
   * Act order is specified by class: objects of one class will always act
   * before objects of some other class. The order of objects of the same class
   * cannot be specified. Objects of classes listed first in the parameter
   * list will act before any objects of classes listed later.<br><br>
   *
   * Objects of a class not explicitly specified inherit the act order from their superclass.<br><br>
   *
   * Objects of classes not listed will act after all objects whose classes have been specified.
   * @param classes the classes in desired act order
   */
  public void setActOrder(Class... classes)
  {
  }

  /**
   * Set the paint order of objects in the world.
   * Paint order is specified by class: objects of one class will always be
   * painted on top of objects of some other class.
   * The order of objects of the same class cannot be specified.
   * Objects of classes listed first in the parameter list will appear on top
   * of all objects of classes listed later.<br><br>
   *
   * Objects of a class not explicitly specified effectively inherit the paint order from their superclass.<br><br>
   *
   * Objects of classes not listed will appear below the objects whose classes have been specified.
   * @param classes the classes in desired paint order
   */
  public void setPaintOrder(Class... classes)
  {
  }

  protected void addComponentListener(ComponentListener l)
  {
    contentPane.addComponentListener(l);
  }

  protected void addFocusListener(FocusListener l)
  {
    contentPane.addFocusListener(l);
  }

  protected void addKeyListenerListener(KeyListener l)
  {
    contentPane.addKeyListener(l);
  }

  protected void addMouseListener(MouseListener l)
  {
    contentPane.addMouseListener(l);
  }

  protected void addMouseMotionListener(MouseMotionListener l)
  {
    contentPane.addMouseMotionListener(l);
  }

  protected void addWindowFocusListener(WindowFocusListener l)
  {
    frame.addWindowFocusListener(l);
  }

  protected void addWindowListener(WindowListener l)
  {
    frame.addWindowListener(l);
  }

  /**
   * Delay execution for the given amount of time.
   * @param time the delay time (in ms)
   */
  public static void delay(int time)
  {
    try
    {
      Thread.sleep(time);
    }
    catch (InterruptedException ex)
    {
    }
  }

  /**
   * Starts the simulation cycling. Same as if the 'Run' button is pressed.
   */
  public void doRun()
  {
    if (isRunning)
      return;
    isPaused = false;
    isSingleStep = false;
    isRunning = true;
    if (SwingUtilities.isEventDispatchThread())
      runBtn.setText("Pause");
    else
    {
      EventQueue.invokeLater(new Runnable()
      {

        public void run()
        {
          runBtn.setText("Pause");
        }
      });
    }
  }

  /**
   * Pause the simulation cycling. Same as if the 'Pause' button is pressed.
   */
  public void doPause()
  {
    if (!isRunning)
      return;
    isRunning = false;
    if (SwingUtilities.isEventDispatchThread())
      runBtn.setText("Run");
    else
    {
      EventQueue.invokeLater(new Runnable()
      {

        public void run()
        {
          runBtn.setText("Run");
        }
      });
    }
  }

  /**
   * Single step of the simulaton loop. Same as if the 'Step' button is pressed.
   */
  public void doStep()
  {
    doPause();
    isSingleStep = true;
    isRunning = true;
    canvas.requestFocus();  // Needed to get the key events
  }

  /**
   * If running execute doPause() and
   * put all actors automatically at starting location/direction and call
   * all registrated ResetListeners. Then repaint the GameGrid.<br><br>
   * Same as if the 'Reset' button is pressed..
   */
  public void doReset()
  {
    if (isRunning)
      doPause();
    for (Actor actor : scene)
    {
      actor.setLocation(actor.getLocationStart());
      actor.setDirection(actor.getDirectionStart());
      actor.setVisible(true);
      actor.setCollisionEnabled(true);
    }
    repaint();
  }

  /**
   * Returns the content pane of the GameGrid window.
   * @return the GameGrid's content pane
   */
  public JPanel getContentPane()
  {
    return contentPane;
  }

  /**
   * Returns the canvas of the GameGrid window.
   * @return the GameGrid's canvas (drawing area)
   */
  public Canvas getCanvas()
  {
    return canvas;
  }

  /**
   * Returns the JFrame of the GameGrid window.
   * @return the GameGrid's JFrame
   */
  public JFrame getFrame()
  {
    return frame;
  }

  protected static void fail(String message)
  {
    System.err.println(message);
    System.exit(0);
  }

  /**
   * Sets the period of the simulation loop (>10 ms).
   * If there is too much to do in one period, the period may be
   * exceeded.
   * @param millisec the period of the simulation loop (in milliseconds)
   */
  public void setSimulationPeriod(int millisec)
  {
    setSimulationPeriod(millisec, true);
  }

  /**
   * Returns simulation period.
   * @return the simulation period (in ms)
   */
  public int getSimulationPeriod()
  {
    return simulationPeriod;
  }

  private void setSimulationPeriod(int millisec, boolean setSlider)
  {
    if (millisec < 1)
      millisec = 1;
    simulationPeriod = millisec;
    timer = new HiResAlarmTimer(simulationPeriod * 1000);
    for (int i = 0; i < scene.size(); i++)
    {
      Actor actor = scene.get(i);
      actor.setSimulationPeriod(simulationPeriod);
    }
    if (setSlider)
      speedSlider.setValue((int)(434.0 * Math.log(millisec)));
  }

  /**
   * Returns true, if a key was press since the last call to getKey(), getKeyCode().
   * The key is not removed from the one-key buffer.
   * @return true, if a key was pressed and not yet consumed
   */
  public boolean kbhit()
  {
    delay(1);
    return gotKey;
  }

  /**
   * Returns the last key pressed and removes it from the one-key buffer.
   * @return the charactor corresponding to the last key pressed, KeyEvent.CHAR_UNDEFINED if no key was pressed
   */
  public char getKey()
  {
    synchronized (monitor)
    {
      if (gotKey)
      {
        gotKey = false;
        return keyChar;
      }
      else
        return KeyEvent.CHAR_UNDEFINED;
    }
  }

  /**
   * Returns the last key pressed and removes it from the one-key buffer.
   * @return the key code corresponding to the last key pressed, KeyEvent.CHAR_UNDEFINED if no key was pressed
   */
  public int getKeyCode()
  {
    synchronized (monitor)
    {
      if (gotKey)
      {
        gotKey = false;
        return keyCode;
      }
      else
        return KeyEvent.CHAR_UNDEFINED;
    }
  }

  /**
   * Returns the key modifier of the last key pressed.
   * The key is not removed from the one-key buffer.
   * @return the key modifier
   */
  public int getKeyModifiers()
  {
    return keyModifiers;
  }

  /**
   * Returns the key modifier as text of the last key pressed.
   * The key is not removed from the one-key buffer.
   * @return the key modifier as string
   */
  public String getKeyModifiersText()
  {
    return keyModifiersText;
  }

  /**
   * Add a GGKeyListener to get events when a key is pressed. More than
   * one KeyListener may be registered. They are called in the order they
   * are added.
   * @param listener the keyListenerer to register
   */
  public void addKeyListener(GGKeyListener listener)
  {
    keyListeners.add(listener);
  }

  /*
   * Empty method called when a key is hit.
   * Override to get your own notification.
   * @param keyCode the key code of the key that was pressed
   */
  public boolean keyHit(int keyCode)
  {
    return false;
  }

  /**
   * Returns true, if the given cell location is within the grid.
   * @return true, if the cell coordinates are within the grid; otherwise false
   */
  public boolean isInGrid(Location location)
  {
    boolean isHorz = (location.x >= 0 && location.x < nbHorzCells);
    boolean isVert = (location.y >= 0 && location.y < nbVertCells);
    return isHorz && isVert;
  }

  /**
   * Returns true, if the given cell location is at the grid border.
   * @return true, if the cell coordinates are at the grid border
   */
  public boolean isAtBorder(Location location)
  {
    boolean isHorz = (location.x == 0 || location.x == nbHorzCells - 1);
    boolean isVert = (location.y == 0 || location.y == nbVertCells - 1);
    return isHorz || isVert;
  }

  /**
   * Returns a list of all occupied locations.
   * @return an ArrayList of all occupied locations
   */
  public ArrayList<Location> getOccupiedLocations()
  {
    ArrayList<Location> locations = new ArrayList<Location>();

    for (int x = 0; x < nbHorzCells; x++)
    {
      for (int y = 0; y < nbVertCells; y++)
      {
        Location location = new Location(x, y);
        if (!getActorsAt(location).isEmpty())
          locations.add(location);
      }
    }
    return locations;
  }

  /**
   * Returns a list of all empty locations.
   * @return an ArrayList of all occupied locations
   */
  public ArrayList<Location> getEmptyLocations()
  {
    ArrayList<Location> locations = new ArrayList<Location>();

    for (int x = 0; x < nbHorzCells; x++)
    {
      for (int y = 0; y < nbVertCells; y++)
      {
        Location location = new Location(x, y);
        if (getActorsAt(location).isEmpty())
          locations.add(location);
      }
    }
    return locations;
  }

  /**
   * Returns all actors of given type in given cell.
   * The list is empty if the location is unoccupied.
   * @param location the location of the cell
   * @param clazz class type of the actors to be returned (e.g. Fish.class),
   * if clazz is null, all actors are returned
   * @return an ArrayList of actors
   */
  public ArrayList<Actor> getActorsAt(Location location, Class clazz)
  {
    if (clazz == null)
      return getActorsAt(location);

    ArrayList<Actor> list = new ArrayList<Actor>();
    ArrayList<Actor> actors = getActorsAt(location);
    for (Actor a : actors)
    {
      if (a.getClass() == clazz)
        list.add(a);
    }
    return list;
  }

  /**
   * Returns all actors at the given location. T
   * he list is empty if the location is unoccupied.
   * @return an ArrayList of all actors at the given location
   */
  public ArrayList<Actor> getActorsAt(Location location)
  {
    ArrayList<Actor> list = new ArrayList<Actor>();
    for (Actor a : scene)
    {
      if (a.getLocation().equals(location))
        list.add(a);
    }
    return list;
  }

  /**
   * Returns all actors.
   * @return an ArrayList that contains all actors
   */
  public ArrayList<Actor> getActors()
  {
    ArrayList<Actor> list = new ArrayList<Actor>();
    for (Actor a : scene)
      list.add(a);
    return list;
  }

  /**
   * Returns all actors of the specified class.
   * @param clazz the class of the actors to look for, if null all actors are returned
   * @return an ArrayList that contains actors of the given class
   */
  public ArrayList<Actor> getActors(Class clazz)
  {
    ArrayList<Actor> list = new ArrayList<Actor>();
    for (Actor a : scene)
      if (a.getClass() == clazz)
        list.add(a);
    return list;
  }

  /**
   * Returns the first actor of specified class in the actor's list at the specific location.
   * @param location the location of the cell
   * @param clazz the class of the actors to look for, if null all actors are considered
   * @return the first actor in the actor's list or null, if no actor is found
   */
  public Actor getOneActorAt(Location location, Class clazz)
  {
    ArrayList<Actor> list = getActorsAt(location, clazz);
    if (list.isEmpty())
      return null;
    return list.get(0);
  }

  /**
   * Returns the first actor in the actor's list at the specific location.
   * @param location the location of the cell
   * @return the first actor in the actor's list or null, if no actor is found
   */
  public Actor getOneActorAt(Location location)
  {
    return getOneActorAt(location, null);
  }

  /**
   * Returns total number of actors in the scene.
   * @return the total number of actors
   */
  public int getNumberOfActors()
  {
    return scene.size();
  }

  /**
   * Returns number of actors at specified location.
   * @return the number of actors
   */
  public int getNumberOfActorsAt(Location location)
  {
    return getActorsAt(location).size();
  }

  /**
   * Returns number of actors of specified class at specified location.
   * @return the number of actors
   */
  public int getNumberOfActorsAt(Location location, Class clazz)
  {
    return getActorsAt(location, clazz).size();
  }

  /**
   * Returns number of actors of specified class.
   * @return the number of actors
   */
  public int getNumberOfActors(Class clazz)
  {
    return getActors(clazz).size();
  }

  public boolean isEmpty(Location location)
  {
    return (getNumberOfActorsAt(location) == 0) ? true : false;
  }

  /**
   * Removes the given actor from the scene.
   * @param actor the actor to be removed
   * @return true, if the specified actor is found and removed
   */
  public boolean removeActor(Actor actor)
  {
    actor.setVisible(false);
    return scene.remove(actor);
  }

  /**
   * Removes all actors from the scene.
   * @return the number of removed actors
   */
  public int removeAllActors()
  {
    for (Actor actor : scene)
      actor.setVisible(false);
    int nb = scene.size();
    scene.clear();
    return nb;
  }

  /**
   * Removes all actors from the specified class.
   * @param clazz class of the actors to be removed, if null all actors are removed
   * @return the number of removed actors
   */
  public int removeActors(Class clazz)
  {
    if (clazz == null)
      return removeAllActors();

    ArrayList<Actor> list = getActors(clazz);
    int nb = 0;
    for (Actor a : list)
      if (removeActor(a))
        nb++;
    return nb;
  }

  /**
   * Removes all actors from the specified class at the specified location.
   * @param location the location of the cell
   * @param clazz class of the actors to be removed, if null all actors are removed
   * @return the number of removed actors
   */
  public int removeActorsAt(Location location, Class clazz)
  {
    ArrayList<Actor> list = getActorsAt(location, clazz);
    int nb = 0;
    for (Actor a : list)
      if (removeActor(a))
        nb++;
    return nb;
  }

  /**
   * Removes all actors at the specified location
   * @param location the location of the cell
   * @return the number of removed actors
   */
  public int removeActorsAt(Location location)
  {
    return removeActorsAt(location, null);
  }

  /**
   * Returns a random location within the GameGrid.
   * @return a random location
   */
  public Location getRandomLocation()
  {
    int x = (int)(nbHorzCells * Math.random());
    int y = (int)(nbVertCells * Math.random());
    return new Location(x, y);
  }

  /**
   * Returns an empty random location within the GameGrid.
   * An empty location is where are no actors.
   * @return an empty random location, if no empty cells are available, returns null
   */
  public Location getRandomEmptyLocation()
  {
    if (getEmptyLocations().size() == 0)
      return null;

    while (true) // Must terminate
    {
      Location location = getRandomLocation();
      if (isEmpty(location))
        return location;
    }
  }

  /**
   * Returns true, if the game is running.
   * @return true, if the game runs; false, if not yet started or paused
   */
  public boolean isRunning()
  {
    return isRunning;
  }

  /**
   * Returns true, if the game is paused
   * @return true, if the game was started and then paused; false, if not yet started or running
   */
  public boolean isPaused()
  {
    return isPaused;
  }


  /**
   * Set the title in the window's title bar.
   * @param text the text to display
   */
  public void setTitle(final String text)
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      frame.setTitle(text);
    }
    else
    {
      EventQueue.invokeLater(new Runnable()
      {

        public void run()
        {
          frame.setTitle(text);
        }
      });
    }
  }

  /**
   * Add a GGMouseListener to get notifications from mouse events. More than
   * one GGMouseListener may be registered. They are called in the order they
   * are added. Only the events defined as OR-combination in the specified mask
   * are notified. If the mouse was disabled, it is enabled now.
   * @param listener the MouseActionlistener to register
   * @param mouseEventMask an OR-combinaton of constants defined in class GGMouse
   */
  public void addMouseListener(GGMouseListener listener, int mouseEventMask)
  {
    mouseListeners.add(listener);
    mouseEventMasks.add(mouseEventMask);
    MyMouseAdapter mouseAdapter = new MyMouseAdapter();
    if (!isMouseListenerAdded &&
      (mouseEventMask & GGMouse.lPress) != 0 |
      (mouseEventMask & GGMouse.rPress) != 0 |
      (mouseEventMask & GGMouse.lRelease) != 0 |
      (mouseEventMask & GGMouse.rRelease) != 0 |
      (mouseEventMask & GGMouse.lClick) != 0 |
      (mouseEventMask & GGMouse.rClick) != 0 |
      (mouseEventMask & GGMouse.lDClick) != 0 |
      (mouseEventMask & GGMouse.rDClick) != 0)
    {
      isMouseListenerAdded = true;
      canvas.addMouseListener(mouseAdapter);
      isMouseEnabled = true;
    }
    if (!isMouseMotionListenerAdded &&
      (mouseEventMask & GGMouse.lDrag) != 0 |
      (mouseEventMask & GGMouse.rDrag) != 0 |
      (mouseEventMask & GGMouse.enter) != 0 |
      (mouseEventMask & GGMouse.leave) != 0 |
      (mouseEventMask & GGMouse.move) != 0)
    {
      isMouseListenerAdded = true;
      canvas.addMouseListener(mouseAdapter);   // Necessary for enter/leave
      isMouseMotionListenerAdded = true;
      canvas.addMouseMotionListener(mouseAdapter);
      isMouseEnabled = true;
    }
  }

  /**
   * Enable/disable all mouse event callbacks.
   * @param enabled if true, the registered callbacks are enabled; otherwise disabled
   */
  public void setMouseEnabled(boolean enabled)
  {
    isMouseEnabled = enabled;
  }

  /**
   * Returns the x-y-coordinates of the center of the cell with given
   * location (cell indices).
   * @param location the indices of the cell   * @return the x-y-coordinates (as point) of the cell's center.
   */
  public Point toPoint(Location location)
  {
    int x = cellSize / 2 + location.x * cellSize;
    int y = cellSize / 2 + location.y * cellSize;
    return new Point(x, y);
  }

  /**
   * Returns the location (cell indices) of the cell where the given point
   * resides. If the point is outside the grid, returns the location with
   * cell indices outside the valid range.
   * @param pt a point of pixels coordinates
   * @return the location of the cell (cell indices) where the point resides
   */
  public Location toLocation(Point pt)
  {
    return toLocation(pt.x, pt.y);
  }

  /**
   * Returns the location (cell indices) of the cell where the point
   * with given coordinates resides.
   * If the point is outside the grid, returns the location with
   * cell indices outside the valid range.
   * @param x x-coordinate (in pixels)
   * @param y y-coordinate (in pixels)
   * @return the location of the cell (cell indices) where the point resides
   */
  public Location toLocation(int x, int y)
  {
    int xCell = (int)(x / cellSize);
    int yCell = (int)(y / cellSize);
    return new Location(xCell, yCell);
  }

  /**
   * Returns the location (cell indices) of the cell where the given point
   * resides. If the point is outside the grid, returns the closes location within the grid.
   * @param pt a point of pixels coordinates
   * @return the location of the cell (cell indices) where the point resides
   */
  public Location toLocationInGrid(Point pt)
  {
    return toLocationInGrid(pt.x, pt.y);
  }

  /**
   * Returns the location (cell indices) of the cell where the point
   * with given coordinates resides.
   * Despite in a strict sense the pixels of the right and bottom border belongs to cells outside
   * the visible cells, the location of the cooresponding visible border cell is returned.
   * If the point is outside the grid, the closest location within the grid is returned.
   * @param x x-coordinate (in pixels)
   * @param y y-coordinate (in pixels)
   * @return the location of the cell (cell indices) where the point resides
   */
  public Location toLocationInGrid(int x, int y)
  {
    if (x < 0)
      x = 0;
    if (x > width - 1)
      x = width - 1;
    if (y < 0)
      y = 0;
    if (y > height - 1)
      y = height - 1;
    return toLocation(x, y);
  }

  /**
   * Register an GGActionListener for the GameGrid, so that the callback method act()
   * is called in the simulation loop at the end of all actor's act() method. act()
   * will be called with null for its actor's parameter.
   * @param listener the GGActionListener that receives the act() events
   */
  public void addActionListener(GGActionListener listener)
  {
    actionListener = listener;
  }

}

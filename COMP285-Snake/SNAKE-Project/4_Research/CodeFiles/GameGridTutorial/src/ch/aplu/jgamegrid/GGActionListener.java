// GGActionListener.java

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

/**
 * Declarations of the notification method called in every simulation cycle.
*/
public interface GGActionListener
{
  /**
   * Event callback method called in every simulation cycle.
   * @param actor the actor that got the act event
   */
  public void act(Actor actor);

}

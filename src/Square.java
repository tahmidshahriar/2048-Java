/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;

/**
 * A basic game object displayed as a black square, starting in the upper left
 * corner of the game court.
 * 
 */
public class Square  {

  // Collection of variables required for the square

  // Helps store the value of the square in order to draw it
  public int worth = 0;

  // Width of the square
  public int width;

  // Height of the square
  public int height;

  // Stores the x position of where we start drawing
  public int pos_x;

  // Stores the y position of where we start drawing
  public int pos_y; 

  // Stores the boolean to check if the block has already combined (controlled mainly by the movement commands)
  public boolean combinedAlready = false; 

  // Side is used to differentiate between different squares- its similar to a name 
  public int side;

  // Point displays the high score
  public int point;

  // Counting displays the timer
  public int counting;

  // Help displays the number of "help" left 
  public int help;

  // Missed displays the number of turns missed
  public int missed;

  // allTimeMax displays the total high score
  public int allTimeMax;


  // creates the square
  public Square(int courtWidth, int courtHeight) {

    // width and height calculated
    width = courtWidth/9;
    height = courtHeight/4;
  }

  // drawing of the square
  public void draw(Graphics g) {

    // Set font and draw rectangle
    g.setFont(new Font("ComicSans", Font.PLAIN, 22)); 
    g.setColor(Color.BLACK);
    g.drawRect(pos_x, pos_y, width, height);

    // draw the value
    int val = (int) Math.pow(2, worth);
    if (worth != 0) {
      g.drawString(val + "", pos_x + width/4, pos_y + height/2);

    }
   
    // Draw the instructions panel
    else if (side == -1) {
      g.setColor(Color.BLUE);

      g.drawString("How to play classic 2048!", pos_x + 5, pos_y + 25);

      g.drawString("Use your arrow keys to move the tiles.", pos_x + 5, pos_y + 75);
      g.drawString("When two tiles with the same number ,", pos_x + 5, pos_y + 100);
      g.drawString("touch they merge into one!", pos_x + 5, pos_y + 125);
      g.drawString("Scores are awarded according to the", pos_x + 5, pos_y + 175);
      g.drawString("power of 2 of new block created.", pos_x + 5, pos_y + 200);
      g.drawString("Creating a 4 gives 2 points.", pos_x + 5, pos_y + 225);
      g.drawString("Similarly, 8 gives 3 points and so on!", pos_x + 5, pos_y + 250);
    }

    // Draw the high score panel
    else if (side == -2) {
      g.setColor(Color.RED);

      g.drawString("Your current points: " + point, pos_x + 5, pos_y + 40);
      g.drawString("Your all time best: " + allTimeMax, pos_x + 5, pos_y + 80);

    }
    
    // draw the Timer, Help and Miss panel
    else if (side == -3) {
      g.setColor(Color.MAGENTA);
      g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
      g.drawString("Timer:" + counting, pos_x + 10, pos_y + 27);
      g.drawString("Help:" + help, pos_x + 10, pos_y + 60);
      g.drawString("Miss:" + missed, pos_x + 10, pos_y + 90);
    }

    // draw the middle gray space
    else if (side == -4) {
      g.setColor(Color.GRAY);
      g.fillRect(pos_x, pos_y, width, height);
    }


  }
}

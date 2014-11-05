/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

  // the state of the game logic
  public boolean playing = false; // whether the game is running
  private JLabel status; // Current status text (i.e. Procrastinating...)

  // Game constants
  public static final int COURT_WIDTH = 900;
  public static final int COURT_HEIGHT = 400;
  
  // Update interval for timer, in milliseconds
  public static final int INTERVAL = 1000;
  
  // Timer helps keep track of how has passed
  private int timer;
  
  // the main squares which display the numbers
  private Square[][] grid;
  
  //the square responsible for instruction display
  private Square cellSide;
  
  // the square responsible for high score display
  private Square point;
  
  // keeps track of high score
  private int highScore;
  
  // square responsible for timer, help and missed display
  private Square forTime;
  
  // keeps track of "help". Initializes with a 3
  private int help = 3;
  
  // boolean to store if a key caused movement
  private boolean didMove;
  
  // square responsible for gray block in middle
  private Square mid;

  // keeps track of turns missed
  private int timesMissed = 0;

  // initialize preference to store high scores indefinitely
  Preferences pref = Preferences.userRoot();

  // initializes the square at the start
  public void initializeGrid() {
    // Creates the grid and states its values
    grid = new Square[4][4];
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        Square cell = new Square(COURT_WIDTH, COURT_HEIGHT);
        grid[x][y] = cell;
        grid[x][y].pos_x = x * COURT_WIDTH/9;
        grid[x][y].pos_y = y * COURT_HEIGHT/4;
      } 
    }

    // creates the instruction panel square
    cellSide = new Square(4 * COURT_WIDTH, 3 * COURT_HEIGHT);
    cellSide.pos_x = (COURT_WIDTH * 5) / 9;
    cellSide.side = -1;
    
    // creates the high score panel square
    point = new Square(3 * COURT_WIDTH , COURT_HEIGHT);
    point.pos_x = (COURT_WIDTH * 5) / 9;
    point.pos_y = (COURT_HEIGHT / 4) * 3;
    point.side = -2;
    
    // creates the timer help and miss square
    forTime = new Square(COURT_WIDTH , COURT_HEIGHT);
    forTime.pos_x = (COURT_WIDTH * 8) / 9;
    forTime.pos_y = (COURT_HEIGHT / 4) * 3;
    forTime.side = -3;
    forTime.help = help;
    
    // creates the mid square
    mid = new Square (COURT_WIDTH - 36, 4* COURT_HEIGHT);
    mid.side = -4;
    mid.pos_x = ((COURT_WIDTH * 4) / 9) + 2;
    
    // gives a random block in grid a 2 value
    grid[(int)(Math.random() * 4)][(int)(Math.random() * 4)].worth = 1;
  }

  public void getRand() {
    // initialize array-list to store the values of the empty blocks in grid
    ArrayList<int[]> alist = new ArrayList<int[]>();
    
    // locates the empties
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 4; j++) {
        if (grid[i][j].worth == 0) {
          
          // creates an integer array with size 2 (store x and y value)
          int[] temp = new int[2];
          temp[0] = i;
          temp[1] = j;
          
          // adds the random value to the array-list once
          alist.add(temp);

          // adds the random value 3 more times if its a block on the sides (to increase chance of a side block to 80%)
          if ((i == 0 || i == 3) || (j == 0 || j == 3)){
            alist.add(temp);
            alist.add(temp);
            alist.add(temp);
          }
        }
      }
    }
    
    // finds size of the array list and locates a random one
    int index = alist.size();
    int random = (int)(Math.random() * (index) );
    
    // if no empty was found, check if game is over
    if (index == 0) {
      isItOver();
    }

    // if game isn't over, give the randomly located box a 2 or a 4
    else {
      
      // decide using a 90% of getting a 2 whether the new block is a 2 or 4
      int[] use = alist.get(random);
      int a = (int) (100 * Math.random());
      if (a > 89) {
        grid[use[0]][use[1]].worth = 2;
      }
      else {
        grid[use[0]][use[1]].worth = 1;
      }
    }
    
    // reset the timer after random in order to make sure we have new time after every move
    if (timer > 0) {
      timer = 0;
    }
  }

  // linked with the alm button. Used to bring the game to near end, it has no real function other than to help test game
  public void almostLose() {
    // fills the entire board except the corner block with numbers starting from 4
    int t = 2;
    boolean so = false;
    boolean kk = false;
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 4; j++) {
        if (i == 3) so = true;
        if ((so) && j == 3) kk = true;
        if (!kk) {
          grid[i][j].worth = t;
          t = t + 1;
        }
      }
    }
    // refocuses window
    requestFocusInWindow(); 
  }


  // linked with the surrender button.
  public void surrender() {
    // similar to almostLost, but it fills the entire board with 2s and 4s in order to force a defeat
    boolean t = false;
    for (int i = 0; i < 4; i++){
      t = !t;
      for (int j = 0; j < 4; j++) {
        if (t ) grid[i][j].worth = 1;
        else grid[i][j].worth = 2;
        t = !t;
      }
    }
    // after filling a board, tries to get random and forces a game end
    getRand();
  }

  // creates and stores a high score in preference
  public void high() {
    // if no high score yet, give 0
    int a = pref.getInt("a", 0);
    
    // if current high score is bigger than high score, replace it
    if (highScore > a) {
      pref.putInt("a", highScore);
    }
    // update the high score panel
    point.allTimeMax = pref.getInt("a", 0);
  }

  // locates a random unused block and stores a 0 in it
  public void randomPain() {
    // the process is same as randomizing, except all blocks have equal chance
    ArrayList<int[]> alist = new ArrayList<int[]>();
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 4; j++) {
        if (grid[i][j].worth == 0) {
          int[] temp = new int[2];
          temp[0] = i;
          temp[1] = j;
          alist.add(temp);
        }
      }
    }

    int index = alist.size();
    // if the box isn't full, put a 0
    if (index != -1){
      int random = (int)(Math.random() * (index) );
      int[] use = alist.get(random);
      grid[use[0]][use[1]].worth = -1;
    }
  }

  // linked to the button which gives more time
  public void time() {
    // checks if enough help is remaining, if so, adds time and decreases help
    if (help > 0) {
      timer = -30;
      help = help - 1;
    }
    //refocuses window
    requestFocusInWindow();
    return;
  }

  // linked with button that doubles all the block value
  public void more() {
    // checks if help left and if so doubles all blocks and decreases help
    if (help > 0) {
      // doesn't halve value of 2^0 block
      for (int x = 0; x < 4; x++) {
        for (int y = 0; y < 4; y++) {
          if (grid[x][y].worth != 0) {
            grid[x][y].worth = grid[x][y].worth + 1;
          }
        }
      }
      help = help - 1;
    }
    // sets timer to 0 for using a help block
    timer = 0;
    // refocuses window
    requestFocusInWindow();
    return;
  }


  // linked with button that halves all block value, similar to more()
  public void half() {
    boolean empty = true;
    if (help > 0) {
      // doesn't halve value of 2^0 block
      for (int x = 0; x < 4; x++) {
        for (int y = 0; y < 4; y++) {
          if (grid[x][y].worth != 0) {
            grid[x][y].worth = grid[x][y].worth - 1;
          }
        }
      }
      
      // if it reduces the grid to all 2^0s, it randomizes a new value
      for (int x = 0; x < 4; x++) {
        for (int y = 0; y < 4; y++) {
          if (grid[x][y].worth != 0) empty = false;
        }
      }
      if (empty) getRand();
      help = help - 1;
    }
    timer = 0;
    requestFocusInWindow();
    return;
  }

  // linked with button related to more helps
  public void getHelp() {
    // trades 15 points for a help
    if (highScore > 15) {
      highScore = highScore - 15;
      help = help + 1;
    }
    timer = 0;
    requestFocusInWindow();
  }

  // linked with button "what"
  public void what() {
    // pauses the game and displays message
    playing = false;
    JOptionPane.showMessageDialog(null, "In this game, we have a timer. Every time you fail to make a move"
        + "\n" + "by every 3rd second, we take away half of your points. Making a move resets it."
        + "\n" + "You are initially given 5 'helps'. You can use these to get special help!"
        + "\n" + "Using the more button, all your blocks get double value (also resets time)"
        + "\n" + "Using the half button, all your blocks can gave half value (also resets time)"
        + "\n" + "Using the time button, you get an extra 30 seconds (also resets time)."
        + "\n" + "If you have more than 10 miss, we will give you a fun unremobavle block."
        + "\n" + "You can also use the remove button to remove these blocks and all misses" 
        + "\n" + "Each of these consume a help. Use help button to purchase more with 15 points (also resets time)."
        + "\n" + "Finally, Test End button is present for the ease of those testing this code (not for gamers)"
        + "\n" + "\n" + "Good luck! You will need it."
        + "\n" + "\n" + "Press OK or close window to resume your game."

        );
    requestFocusInWindow();
    // resumes the game
    playing = true;

  }
  
  // related to the fix button of the game
  public void fix () {
    if (help > 0) {
      // removes a help and all 0 blocks along with missed moves
      help = help - 1;
      timesMissed = 0;
      for (int i = 0; i < 4; i++){
        for (int j = 0; j < 4; j++) {
          if (grid[i][j].worth == -1) {
            grid[i][j].worth = 0;
          }
        }
      }
    }    
    requestFocusInWindow();
  }


  // restarts the already combined boolean of the blocks in a grid
  public void restartCombStatus () {
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 4; j++) {
        grid[i][j].combinedAlready = false;
      }
    }
  }


  // checks if game is over
  public void isItOver() {
    // pauses the game
    playing = false;
    // creates a variable with true in it
    boolean isIt = true;
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 4; j++) {
        // if any of the block is empty or there is a move possible, turns tha varibale to a false
        if (grid[i][j].worth == 0) isIt = false;
        if (j < 3 && grid[i][j].worth == grid[i][j+1].worth) isIt = false; 
        if (i < 3 && grid[i][j].worth == grid[i+1][j].worth) isIt = false; 
      } 
    }

    // if variable is still true, ends game
    if (isIt) {
      // changes status and gives popup option to restart game
      status.setText("Better luck next time!");
      int dialogButton = JOptionPane.OK_OPTION;
      JOptionPane.showMessageDialog (null, "Press OK or close the message to restart!","Who cares about finals?",dialogButton);
      if(dialogButton == JOptionPane.OK_OPTION){ 
        reset();
      }
    }
    // if game didn't end, resumes timer
    else playing = true;
  }

  // initializes the game court
  public GameCourt(JLabel status) {
    // creates border around the court area, JComponent method
    setBorder(BorderFactory.createLineBorder(Color.BLACK));
    initializeGrid();
    // The timer is an object which triggers an action periodically
    // with the given INTERVAL. One registers an ActionListener with
    // this timer, whose actionPerformed() method will be called
    // each time the timer triggers. We define a helper method
    // called tick() that actually does everything that should
    // be done in a single time step.
    Timer timer = new Timer(INTERVAL, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tick();
      }
    });
    timer.start(); // MAKE SURE TO START THE TIMER!

    // Enable keyboard focus on the court area.
    // When this component has the keyboard focus, key
    // events will be handled by its key listener.
    setFocusable(true);

    // This key listener allows the square to move when the key is pressed
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        
        // Moves the blocks to left
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          // variable to store if block did in fact move
          didMove = false;
          
          //goes through every block
          for (int y = 0; y < 4; y++){
            for (int x = 0; x < 4; x++) {
              // if the block is moved to an empty, it stores that fact
              int checkIfOcc = 0;
              // moves blocks to the empty
              if (x != 0 && grid[x][y].worth != 0) {
                if (grid[x - 1][y].worth == 0) {
                  grid[x - 1][y].worth = grid[x][y].worth;
                  grid[x][y].worth = 0;
                  checkIfOcc = 1;
                  didMove = true;
                }
                
                // moves block to combine is same one is beside it. Only does it block didn't combine already
                if (grid[x - 1][y].worth == grid[x][y].worth &&   grid[x][y].combinedAlready == false) {
                  grid[x - 1][y].worth = grid[x - 1][y].worth + 1; 
                  grid[x][y].worth = 0;
                  grid[x][y].combinedAlready = true;
                  highScore = highScore + grid[x - 1][y].worth;
                  didMove = true;
                }  
                // if it moved to an empty, go through the line again
                if (checkIfOcc == 1) {
                  x = x - 2;
                }
              }
            }
          }
          
          // if there was movement, get a random value
          if (didMove) {
            getRand(); 
            
            // restart the combination status
            restartCombStatus();  
          }
        }

        // similar to left key except slightly different logic
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
          didMove = false;
          for (int y = 0; y < 4; y++){
            for (int x = 3; x >= 0; x--) {
              int checkIfOcc = 0;
              if (x != 3 && grid[x][y].worth != 0) {
                if (grid[x + 1][y].worth == 0) {
                  grid[x + 1][y].worth = grid[x][y].worth;
                  grid[x][y].worth = 0;
                  checkIfOcc = 1;
                  didMove = true;
                }
                if (grid[x + 1][y].worth == grid[x][y].worth &&   grid[x][y].combinedAlready == false) {
                  grid[x + 1][y].worth = grid[x + 1][y].worth + 1; 
                  grid[x][y].worth = 0;
                  grid[x][y].combinedAlready = true;
                  highScore = highScore + grid[x + 1][y].worth;
                  didMove = true;
                }  
                if (checkIfOcc == 1) {
                  x = x + 2;
                }
              }
            }
          }
          if (didMove){
            getRand(); 
            restartCombStatus();  
          }
        }

        // similar to left key except slightly different logic
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
          didMove = false;
          for (int i = 0; i < 4; i++){
            for (int j = 3; j >= 0; j--) {
              int checkIfOcc = 0;
              if (j != 3 && grid[i][j].worth != 0) {
                if (grid[i][j + 1].worth == 0) {
                  grid[i][j + 1].worth = grid[i][j].worth;
                  grid[i][j].worth = 0;
                  checkIfOcc = 1;
                  didMove = true;
                }
                if (grid[i][j + 1].worth == grid[i][j].worth && grid[i][j].combinedAlready == false) {
                  grid[i][j + 1].worth = grid[i][j + 1].worth + 1; 
                  grid[i][j].worth = 0;
                  grid[i][j].combinedAlready = true;
                  highScore = highScore + grid[i][j + 1].worth;
                  didMove = true;
                }  
                if (checkIfOcc == 1) {
                  j = j + 2;
                }
              }
            }
          } 
          if (didMove){
            getRand(); 
            restartCombStatus();  
          }
        }

        // similar to left key except slightly different logic
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
          didMove = false;
          for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++) {
              int checkIfOcc = 0;
              if (j != 0 && grid[i][j].worth != 0) {
                if (grid[i][j - 1].worth == 0) {
                  grid[i][j - 1].worth = grid[i][j].worth;
                  grid[i][j].worth = 0;
                  checkIfOcc = 1;
                  didMove = true;
                }
                if (grid[i][j - 1].worth == grid[i][j].worth && grid[i][j].combinedAlready == false) {
                  grid[i][j - 1].worth = grid[i][j - 1].worth + 1; 
                  grid[i][j].worth = 0;
                  grid[i][j].combinedAlready = true;
                  highScore = highScore + grid[i][j - 1].worth;
                  didMove = true;
                }  
                if (checkIfOcc == 1) {
                  j = j-2;
                }
              }
            }
          }
          if (didMove) {
            getRand(); 
            restartCombStatus();  
          }
        }
      }
    });
    this.status = status;
  }

  /**
   * (Re-)set the game to its initial state.
   */
  public void reset() {
    timesMissed = 0;
    timer = 0;
    highScore = 0;
    help = 3;    
    initializeGrid();
    playing = true;
    status.setText("Procrastinating...");

    // Make sure that this component has the keyboard focus
    requestFocusInWindow();
  }

  /**
   * This method is called every time the timer defined in the constructor
   * triggers.
   */
  void tick() {
    // update timer every second
    if (playing) {
      timer = timer + 1;
      repaint();
    }
    // if 3rd second is here, update times missed and halve the score
    if (timer > 2) {
      timesMissed = timesMissed + 1;
      isItOver();
      highScore = (int) (highScore / 2);
      point.point = highScore;
      timer = 0;
    }
    
    // if times missed is 10, create a 0 block
    if (timesMissed == 10) {
      randomPain();
      timesMissed = 0;
    }
  }

  // draws the board
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // updates the grid
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 4; j++) {
        grid[i][j].draw(g);
      }
    }
    // draws the instruction panel and mid panel
    cellSide.draw(g);
    mid.draw(g);
    // updates the score
    point.point = highScore;
    //check if new high score achieved and if so update it
    high();
    // draw the point panel
    point.draw(g);
    // update and draw the timer help missed panel
    forTime.counting = timer;
    forTime.help = help;
    forTime.missed = timesMissed;
    forTime.draw(g);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(COURT_WIDTH, COURT_HEIGHT);
  }
}

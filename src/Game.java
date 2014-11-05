/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	public void run() {
	  
	  
	  // Top-level frame in which game components live
	  // Name of the game has been set here
	  final JFrame frame = new JFrame("Not really fun");
		frame.setLocation(100, 100);
		
		
		
		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);
		final JLabel status = new JLabel("Procrastinating...");
		status_panel.add(status);

		// Main playing area
		final GameCourt court = new GameCourt(status);
		frame.add(court, BorderLayout.CENTER);

		// Control panel
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);

		//In game button creations (all link to GameCourt)
	  final JButton what = new JButton("WHAT?");
    what.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        court.what();
      }
    });
    control_panel.add(what);
		
		final JButton surrender = new JButton("I give up!");
    surrender.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        court.surrender();
      }
    });
    control_panel.add(surrender);
    

    final JButton time = new JButton("Time Please!");
    time.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        court.time();
      }
    });
    control_panel.add(time);
    
    final JButton more = new JButton("Bigger Please!");
    more.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        court.more();
      }
    });
    control_panel.add(more);
    
    final JButton half = new JButton("Halve Please!");
    half.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        court.half();
      }
    });
    control_panel.add(half);
    
    final JButton getHelp = new JButton("Help more!");
    getHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        court.getHelp();
      }
    });
    control_panel.add(getHelp);
    

    final JButton fix = new JButton("Remove");
    fix.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        court.fix();
      }
    });
    control_panel.add(fix);
    
    final JButton alm = new JButton("Test End");
    alm.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        court.almostLose();
      }
    });
    control_panel.add(alm);
    
		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start game
		court.reset();
	}

	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it IMPORTANT.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}

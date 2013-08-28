package core;

/**
 * The purpose of this class is to be the main driving
 * system for the game.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */

// Import statements
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class GameClient extends JFrame implements WindowListener {
	
	private static final int TILE_SIZE = 30;
	private static final int FPS = 80;
	
	private GameClientPanel gp;	
	private static ArrayList<Box> boxes;
	
	/**
	 * The main of jBomber that takes in a map to play on.
	 * 
	 * @param argv A .cbm file to be parsed into a map to play.
	 */
	public static void main(String[] argv) {
		char[][] map = new char[10][10];
		long period = (long) 1000.0/FPS;
		
		// Trying to parse the map
		try {
			MapParser mp = new MapParser();
			map = mp.parseMap(argv[0]);
		}
		catch (Exception e) {
			System.err.print(e.getMessage());
		}
		
		// Getting an ArrayList of the boxes in the map
		boxes = findBoxes(map);
		
		// Creating a new game with the boxes and period
		new GameClient(/*map,*/ boxes, period*1000000L);
	} // End main
	
	/**
	 * This method finds all boxes in the map and creates
	 * an ArrayList of their positions.
	 * 
	 * @param m A map to find boxes on.
	 * @return An ArrayList of the positions of all boxes.
	 */
	private static ArrayList<Box> findBoxes(char[][] m) {
		ArrayList<Box> boxes = new ArrayList<Box>();
		int x = 0, y = 0;
		
		// Checking the map for boxes and assigning positions
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				if (m[i][j] == 'x') {
					switch (j) {
						case 0:
							x = TILE_SIZE * j;
							break;
						case 1:
							x = TILE_SIZE * j;
							break;
						case 2:
							x = TILE_SIZE * j;
							break;
						case 3:
							x = TILE_SIZE * j;
							break;
						case 4:
							x = TILE_SIZE * j;
							break;
						case 5:
							x = TILE_SIZE * j;
							break;
						case 6:
							x = TILE_SIZE * j;
							break;
						case 7:
							x = TILE_SIZE * j;
							break;
						case 8:
							x = TILE_SIZE * j;
							break;
						case 9:
							x = TILE_SIZE * j;
							break;
						default:
							// Do nothing
							break;
					} // End switch i
					
					switch (i) {
						case 0:
							y = TILE_SIZE * i;
							break;
						case 1:
							y = TILE_SIZE * i;
							break;
						case 2:
							y = TILE_SIZE * i;
							break;
						case 3:
							y = TILE_SIZE * i;
							break;
						case 4:
							y = TILE_SIZE * i;
							break;
						case 5:
							y = TILE_SIZE * i;
							break;
						case 6:
							y = TILE_SIZE * i;
							break;
						case 7:
							y = TILE_SIZE * i;
							break;
						case 8:
							y = TILE_SIZE * i;
							break;
						case 9:
							y = TILE_SIZE * i;
							break;
						default:
							// Do nothing
							break;
					} // End switch j
					
					boxes.add(new Box(new Position(x, y)));
					
				} // End if
		
		return boxes;					
	} // End findBoxes
	
	/**
	 * Creating the game for jBomber.
	 * 
	 * @param boxes An ArrayList of all the boxes found.
	 * @param period A long of the FPS.
	 */
	public GameClient(/*char[][] map,*/ ArrayList<Box> boxes, long period) {
		super("jBomber - By CircleBear");
		setUpGUI(/*map,*/ boxes, period);
		
		addWindowListener(this);
		pack();
		setResizable(false);
		setVisible(true);
	} // End Game(map)
	
	/**
	 * Setting up the GUI for jBomber.
	 * 
	 * @param boxes An ArrayList of all the boxes found.
	 * @param period A long of the FPS.
	 */
	private void setUpGUI(/*char[][] map,*/ ArrayList<Box> boxes, long period) {
		Container c = getContentPane();
		
		// Creating a new GamePanel for jBomber
		gp = new GameClientPanel(this, /*map,*/ boxes, period);
		c.add("Center", gp);
	} // End setUpGUI

	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		// Do nothing
	} // End windowActivated

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		// Do nothing
	} // End windowClosed

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		// Do nothing
	} // End windowClosing

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
		// Do nothing
	} // End windowDeactivated

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
		// Do nothing
	} // End windowDeiconified

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {
		// Do nothing
	} // End windowIconified

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {
		// Do nothing
	} // End windowOpened
} // End Game class

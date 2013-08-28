package editor;

// Import statements
import core.Bomb;
import core.Bomber;
import core.Box;
import core.Explosion;
import core.Game;
import core.GamePanel;
import core.Position;

import javax.swing.JFrame;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

/**
 * This is the main program that initializes and runs
 * the Map Editor.
 * 
 * @author Dan Wiechert
 * @since 1.1
 * @version 1.1
 */
public class EditorGame extends JFrame implements WindowListener {
	private static final int FPS = 80;
	private EditorGamePanel gp;
	
	public static void main(String[] argv) {
		char[][] map = new char[10][10];
		long period = (long) 1000.0/FPS;
		
		// Initialized the map to all '-'s
		map = initializeMap(map);
		
		// Creating a new game with the boxes and period
		new EditorGame(map, period*1000000L);
	} // End main
	
	private static char[][] initializeMap(char[][] m) {
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				m[i][j] = '-';
		
		return m;
	} // End initializeMap
	
	/**
	 * Creating the game for jBomber.
	 * 
	 * @param boxes An ArrayList of all the boxes found.
	 * @param period A long of the FPS.
	 */
	public EditorGame(char[][] map, long period) {
		super("jBomber - By CircleBear");
		setUpGUI(map, period);
		
		addWindowListener(this);
		pack();
		setResizable(false);
		setVisible(true);
	} // End EditorGame(map)
	
	/**
	 * Setting up the GUI for jBomber.
	 * 
	 * @param boxes An ArrayList of all the boxes found.
	 * @param period A long of the FPS.
	 */
	private void setUpGUI(char[][] map, long period) {
		Container c = getContentPane();
		
		// Creating a new GamePanel for jBomber
		gp = new EditorGamePanel(this, map, period);
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
} // End EditorGame

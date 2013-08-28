package editor;

// Import statements
import core.Bomb;
import core.Bomber;
import core.Box;
import core.Explosion;
import core.Game;
import core.Position;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This is the main panel that controls
 * the Map Editor.
 * 
 * @author Dan Wiechert
 * @since 1.1
 * @version 1.1
 */
public class EditorGamePanel extends JPanel implements Runnable, KeyListener {
	// Dimensions of game and tile sizes
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final int TILE_SIZE = 30;
	
	// Numbers used in FPS calculations
	private static final int NO_DELAYS_PER_YIELD = 16;
	private static int MAX_FRAME_SKIPS = 5;
	
	// The game that created this GamePanel
	private EditorGame gameTop;
	
	// The current map
	private char[][] map;
	
	// A bomber which represents the objects moving on screen
	private Bomber mapObject;
	
	// An int to represent what object is selected in the editor
	private int mapObjectInt = 0;
	
	// ArrayLists of the boxes, bombs, and explosions on the map
	private ArrayList<Box> boxes = new ArrayList<Box>();
	
	// Image files
	private Image dbImage = null;
	private BufferedImage floorTile;
	private BufferedImage boxTile;
	private BufferedImage steelTile;
	private Graphics dbg = null;
	
	// New global variables (temp comment)
	private long period;
	private Thread animator;           // the thread that performs the animation
	private boolean running = false;   // used to stop the animation thread
	private boolean isPaused = false;
	private long gameStartTime;
	
	// Start Constructor(s)
	/**
	 * This is the main constructor of the EditorGamePanel and initializes all variables.
	 * 
	 * @param game The game that created this GamePanel.
	 * @param boxes An ArrayList of the boxes on the map.
	 * @param period A long to help with FPS calculations.
	 */
	public EditorGamePanel(EditorGame game, char[][] map, long period) {
		this.gameTop = game;
		this.map = map;
		this.boxes = boxes;
		this.period = period;
		
		mapObject = new Bomber(new Position(TILE_SIZE * 0, TILE_SIZE * 0));
		
		readInImages();
		
		setBackground(Color.white);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		setFocusable(true);
	    requestFocus();    // the JPanel now has focus, so receives key events
		//readyForTermination(); Don't need this for now.
		
		addKeyListener(this);
	} // End GamePanel(Game, map)
	// End Constructor(s)
	
	// =================================================================================
	// Start section of running game
	// =================================================================================

	/**
	 * Wait for the JPanel to be added to the JFrame before starting.
	 */
	public void addNotify() { 
		super.addNotify();   // creates the peer
	    startGame();         // start the thread
	} // End addNotify()
	
	/**
	 * Initializes and starts the thread
	 */
	private void startGame() { 
		if (animator == null || !running) {
			animator = new Thread(this);
		    animator.start();
	    }
	} // End startGame()

	/**
	 * Called when the JFrame is actived / deiconified.
	 */
	public void resumeGame() {
		isPaused = false;  
	} // End resumeGame()

	/**
	 * Called when the JFram is deactivated / iconified.
	 */
	public void pauseGame() {
		isPaused = true;
	} // End pauseGame()

	/**
	 * Called when the JFrame is closing.
	 */
	public void stopGame() {
		running = false;
	} // End stopGame()
	
	/**
	 * This method runs, updates, and renders the map editor.
	 */
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		gameStartTime = System.nanoTime();
		beforeTime = gameStartTime;
		  
		running = true;
		  
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen();
			  
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
			  
			if(sleepTime > 0) {
				try {
					Thread.sleep(sleepTime/1000000L);
				}
				catch(InterruptedException e) {
					System.err.println(e.getMessage());
				}
				  
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}
			else {
				excess -= sleepTime;
				overSleepTime = 0L;
				  
				if(++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();
					noDelays = 0;
				}
			}
			  
			beforeTime = System.nanoTime();
			  
			int skips = 0;
			while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate();
				skips++;
			}
		} // End running 
	} // End run()
	
	// =================================================================================
	// End section for running game
	// =================================================================================
	
	// =================================================================================
	// Start section for working with images
	// =================================================================================
	  
	/**
	 * This method re-draws all the images once the
	 * logic is updated.
	 */
	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(WIDTH, HEIGHT);
			
			if (dbImage == null) {
				System.out.println("dbImage is null.");
				return;
			}
			else
				dbg = dbImage.getGraphics();
		} // End if
		
		// clear the background
	    dbg.setColor(Color.white);
	    dbg.fillRect (0, 0, WIDTH, HEIGHT);
	    
	    // Drawing in the floor, boxes, bombs, bomber, and explosions
	    this.fillInFloor(dbg);
	    this.fillInBoxes(dbg);
	    this.fillInMapObject(dbg);
	} // End gameRender()

	/**
	 * This method reads in all the images that will be used
	 * during the game. It reads them once and never has
	 * to locate them again during game play.
	 */
	private void readInImages() {
		// Load the explosion image(s)
		try {
			this.boxTile = ImageIO.read(new File("src/core/Images/box_1.jpg"));
			this.steelTile = ImageIO.read(new File("src/core/Images/steel_box_1.png"));
			this.floorTile = ImageIO.read(new File("src/core/Images/floor_tile_1.jpg"));
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
	} // End readInImages()

	/**
	 * This method draws the map object on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInMapObject(Graphics dbg) {
		switch (mapObjectInt) {
			case 0:
				break;
			case 1:
				dbg.drawImage(boxTile, mapObject.getPosition().getXCoord(), mapObject.getPosition().getYCoord(), this);
				break;
			// TODO: Integrate all box images
			/*case 2:
				break;
			case 3:
				break;*/
			case 4:
				dbg.drawImage(steelTile, mapObject.getPosition().getXCoord(), mapObject.getPosition().getYCoord(), this);
				break;
			default:
				// Do nothing
				break;
		} // End switch
	} // End fillInMapObject()
	
	/**
	 * This method draws the box graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInBoxes(Graphics dbg) {
		// Draws all boxes in the boxes' ArrayList.
		for (int i = 0; i < boxes.size(); i++) {
			try {
				if(boxes.get(i).getType() == 0)					
					dbg.drawImage(boxTile, boxes.get(i).getPosition().getXCoord(), boxes.get(i).getPosition().getYCoord(), this);
				else
					dbg.drawImage(steelTile, boxes.get(i).getPosition().getXCoord(), boxes.get(i).getPosition().getYCoord(), this);
			}
			catch (NullPointerException e) {
				// Ignore exception because boxes wasn't full
			}
		} // End for
	} // End fillInBoxes()
	
	/**
	 * This method draws the floor graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInFloor(Graphics dbg) {
		// Draws the floor tile on the whole game screen
		for(int i = 0; i < 380; i+=30)
			for(int j = 0; j < 380; j+=30)
				dbg.drawImage(floorTile, i, j, this);
	} // End fillInFloor()
	
	/**
	 * Uses active rendering to put the buffered image on-screen.
	 */
	private void paintScreen() { 
		Graphics g;
      
	    try {
	    	g = this.getGraphics();
	      
	        if ((g != null) && (dbImage != null))
	        	g.drawImage(dbImage, 0, 0, null);
	      
	        g.dispose();
	    }
	    catch (Exception e) { 
	    	System.out.println("Graphics context error: " + e);  
	    }
	} // End paintScreen()
	
	// =================================================================================
	// End section for working with images
	// =================================================================================
	
	// =================================================================================
	// Start section for game logic
	// =================================================================================
	
	/**
	 * This updates the logic of the game by checking if
	 * bombs have blown up or explosions have timed out.
	 */
	private void gameUpdate() {
		// No logic needs to be updated
	} // End gameUpdate()
	
	/**
	 * This method makes sure that the position the bomber
	 * wants to lay a bomb is in now currently occupied by
	 * another bomb.
	 * 
	 * @param posn The position the bomber wants to lay a bomb in.
	 * @return A boolean saying whether the lay is 'OK' or not.
	 */
	private boolean checkLayOK(int code, Position posn) {
		boolean moveOK = true;
		
		for (int e = 0; e < boxes.size(); e++)
			if (boxes.get(e).getPosition().compare(posn))
				moveOK = false;
		
		return moveOK;
	} // End checkLayOK()
	
	/**
	 * This checks to make sure the map object is always within bounds.
	 * @param x The movement in the x direction.
	 * @param y The movement in the y direction.
	 * @return A boolean if the move is legal.
	 */
	private boolean checkWithinBounds(int x, int y) {
		int newXCoord = mapObject.getPosition().getXCoord() + x;
		int newYCoord = mapObject.getPosition().getYCoord() + y;
		
		if (newXCoord > (WIDTH - TILE_SIZE) || newYCoord > (HEIGHT - TILE_SIZE))
			return false;
		else if (newXCoord < 0 || newYCoord < 0)
			return false;
		
		return true;
	} // End checkWithinBounds()
	
	/**
	 * This method sees if a map object is in the position to be removed,
	 * if so, it is removed from its list.
	 * @param posn The position to remove a map object from.
	 * @return A boolean of if a map object was removed.
	 */
	private boolean removeMapObject(Position posn) {
		boolean wasRemoved = false;
		
		for (int z = boxes.size() - 1; z > -1; z--)
			if (boxes.get(z).getPosition().compare(posn)) {
				boxes.remove(z);
				wasRemoved = true;
			}
			
		return wasRemoved;
	} // End removeMapObject
	
	/**
	 * This updates the char[][] with the new map object
	 * @param code The type of map object laid.
	 * @param posn The position the map object was laid in.
	 */
	private void updateMap(int code, Position posn) {
		// Translating the x and y into char[][] indexes
		int x = posn.getXCoord() / TILE_SIZE;
		int y = posn.getYCoord() / TILE_SIZE;
		
		// TODO: Integrate all type of boxes
		// Updating the char[][] map based on the code
		if (code == 0)
			this.map[y][x] = '-';
		else if (code == 1)
			this.map[y][x] = 'x';
		else if (code == 4)
			this.map[y][x] = 's';
	} // End updateMap()
	
	// =================================================================================
	// End section for game logic
	// =================================================================================
	
	// =================================================================================
	// Start section for saving map
	// =================================================================================
	
	private void saveMap() {
		String fileName = JOptionPane.showInputDialog(null, "Map Name:");
		
		/*
		 * Trying to save to src/core/Maps/fileName.cbm.
		 */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("src/core/Maps/" + fileName + ".cbm"));
			
			for (int a = 0; a < map.length; a++) {
				for (int b = 0; b < map[a].length; b++)
						out.write(map[a][b]);
				
				out.newLine();
			} // End for
			
			// Closing the buffer
			out.close();
			
			// File was saved correctly
			JOptionPane.showMessageDialog(null, "File saved correctly: src/core/Maps/" + fileName + ".cbm");
		} 
		catch (IOException e) {
			System.err.println("Error writing to file.");
		}
	} // End saveMap()
	
	// =================================================================================
	// End section for saving map
	// =================================================================================
	
	// =================================================================================
	// Start section for keyboard input
	// =================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent ke) {
		// Do nothing
	} // End keyReleased()
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent ke) {
		// Do nothing
	} // End keyTyped()
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent ke) {		
		int keyCode = ke.getKeyCode(); // Getting key code pressed
		
		/*
		 * This is a switch on the keys we want to look out for
		 * that help with the movement of Bombers, pausing and 
		 * un-pausing the game, exiting the game, menus, and 
		 * dropping each Bomber's bombs.
		 */
		switch (keyCode) {
			case 8:		// (BACKSPACE) Remove map object
				if (removeMapObject(mapObject.getPosition()))
					updateMap(0, mapObject.getPosition());
				break;
			case 27:	// ESCAPE
				running = false;
				System.exit(0);
				break;
			// TODO: Integrate all types of boxes
			case 32:	// (SPACE) Lay map object
				if (checkLayOK(mapObjectInt, mapObject.getPosition())) {
					if (mapObjectInt == 1) {
						boxes.add(new Box(new Position(mapObject.getPosition().getXCoord(), mapObject.getPosition().getYCoord())));
						updateMap(1, mapObject.getPosition());
					}
					else if (mapObjectInt == 4) {
						boxes.add(new Box(new Position(mapObject.getPosition().getXCoord(), mapObject.getPosition().getYCoord()), 1));
						updateMap(4, mapObject.getPosition());
					}
				} // End if
				else {
					removeMapObject(mapObject.getPosition());
					
					if (mapObjectInt == 1) {
						boxes.add(new Box(new Position(mapObject.getPosition().getXCoord(), mapObject.getPosition().getYCoord())));
						updateMap(1, mapObject.getPosition());
					}
					else if (mapObjectInt == 4) {
						boxes.add(new Box(new Position(mapObject.getPosition().getXCoord(), mapObject.getPosition().getYCoord()), 1));
						updateMap(4, mapObject.getPosition());
					}
				}
				break;
			case 37:	// (LEFT) map object
				if (checkWithinBounds(-TILE_SIZE, 0))
					mapObject.moveLeft();
				break;
			case 38:	// (UP) map object
				if (checkWithinBounds(0, -TILE_SIZE))
					mapObject.moveUp();
				break;
			case 39:	// (RIGHT) map object
				if (checkWithinBounds(TILE_SIZE, 0))
					mapObject.moveRight();
				break;
			case 40:	// (DOWN) map object
				if (checkWithinBounds(0, TILE_SIZE))
					mapObject.moveDown();
				break;
			case 48: // (0) Bomber
				break;
			case 49: // (1) Box_1
				mapObjectInt = 1;
				break;
			// TODO: Integrate all box images
			/*case 50: // (2) Box_2
				mapObjectInt = 2;
				break;
			case 51: // (3) Box_3
				mapObjectInt = 3;
				break;*/
			case 52: // (4) Steel_Box_1
				mapObjectInt = 4;
				break;
			case 83:	// (S) Save
				saveMap();
				break;
			case 127: 	// (DELETE) Remove map object
				if (removeMapObject(mapObject.getPosition()))
					updateMap(0, mapObject.getPosition());
				break;
			default:	// Any other key
				// Ignore other keystrokes
				break;
		} // End switch
	} // End keyPressed()
	
	// =================================================================================
	// End section for keyboard input
	// =================================================================================
} // End EditorGamePanel

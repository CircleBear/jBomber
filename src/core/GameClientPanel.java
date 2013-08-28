package core;

/**
 * This is the class that does all logic checking,
 * drawing of images, gets user input, and runs
 * the game. 
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.1
 */

//Import statements
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/*
 * TODO: List of things that need to be done:
 * - Consider how pausing affects fuse time with our implementation (System.nanoTime())
 * - Comment run()
 * - Fix checkValidMove()
 * - See if pausing is possible
 */
public class GameClientPanel extends JPanel implements Runnable, KeyListener {
	// Dimensions of game and tile sizes
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final int TILE_SIZE = 30;
	
	// Numbers used in FPS calculations
	private static final int NO_DELAYS_PER_YIELD = 16;
	private static int MAX_FRAME_SKIPS = 5;
	
	// The game that created this GamePanel
	private GameClient gameTop;
	
	// The current map (commented out)
	//private char[][] map;
	
	// ArrayLists of the boxes, bombs, and explosions on the map
	private ArrayList<Box> boxes = new ArrayList<Box>();
	private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	
	// Bomber 1 and 2 (commented out)
	private Bomber player1;//, player2;
	
	// Image files
	private Image dbImage = null;
	private BufferedImage floorTile;
	private BufferedImage boxTile;
	private BufferedImage bombTile;
	private BufferedImage bomberTile;
	private BufferedImage fireTileCenter;
	private BufferedImage fireTileUp;
	private BufferedImage fireTileDown;
	private BufferedImage fireTileLeft;
	private BufferedImage fireTileRight;
	private Graphics dbg = null;
	
	// New global variables (temp comment)
	private long period;
	private Thread animator;           // the thread that performs the animation
	private boolean running = false;   // used to stop the animation thread
	private boolean isPaused = false;
	private long gameStartTime;
	
	// Networking variables
	Socket echoSocket;
	PrintWriter output;
	BufferedReader input;
	
	// Start Constructor(s)
	/**
	 * This is the main constructor of the GamePanel and initializes all variables.
	 * 
	 * @param game The game that created this GamePanel.
	 * @param boxes An ArrayList of the boxes on the map.
	 * @param period A long to help with FPS calculations.
	 */
	public GameClientPanel(GameClient game, /*char[][] map,*/ ArrayList<Box> boxes, long period) {
		this.gameTop = game;
		//this.map = map;
		this.boxes = boxes;
		this.period = period;
		
		 /*
		  * Connecting to the GameServer
		  */
		try {
			// 'localhost' impliest that the server is hosted on this machine
			// If we wanted to change this, we would provide another machine
			// name that is on the current network.
			echoSocket = new Socket("localhost", 2488);
			output = new PrintWriter(echoSocket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} // End try
		catch (UnknownHostException e) {
			System.err.println("Don't know about host: MyServer");
			System.exit(1);
		} // End catch UnknowHostException
		catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: MyServer");
			System.exit(1);
		} // End catch IOException
		
		readInImages();
		
		setBackground(Color.white);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		setFocusable(true);
	    requestFocus();    // the JPanel now has focus, so receives key events
		//readyForTermination(); Don't need this for now.

		player1 = new Bomber(new Position(TILE_SIZE * 0, TILE_SIZE * 0));
		//player2 = new Bomber(new Position(TILE_SIZE * 9, TILE_SIZE * 9));
		
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
	   * TODO: Comment...
	   */
	public void run() {
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String clientInput;
		
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		  
		// FIXME: May need to remove global on gameStartTime
		gameStartTime = System.nanoTime();
		beforeTime = gameStartTime;
		  
		running = true;
		gameUpdate();
		gameRender();
		paintScreen();
		  
		while(running) {
			try {
				// TODO: If client receives certain message from server, it quits
				// TODO: If certain text is sent to server, it quits
				while ((clientInput = stdIn.readLine()) != null) {
					System.out.println("Client: " + clientInput);
					output.println(clientInput);
					System.out.println("Server: " + input.readLine());
					
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
					} // End while
				} // End while
			} // End try
			catch (IOException e) {
				System.out.println("Error reading from server.");
			}
		} // End running 
		
		try {
			output.close();
			input.close();
			stdIn.close();
			echoSocket.close();
		}
		catch (IOException e) {
			System.out.println("Error closing sockets.");
		}
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
	    this.fillInBombs(dbg);
	    this.fillInBomber(dbg);
	    this.fillInExplosion(dbg);
	} // End gameRender()
	
	/**
	 * This method reads in all the images that will be used
	 * during the game. It reads them once and never has
	 * to locate them again during game play.
	 */
	private void readInImages() {
		// Load the explosion image(s)
		try {
			this.fireTileCenter = ImageIO.read(new File("src/core/Images/fire_center_1.png"));
			this.fireTileUp = ImageIO.read(new File("src/core/Images/fire_up_1.png"));
			this.fireTileDown = ImageIO.read(new File("src/core/Images/fire_down_1.png"));
			this.fireTileLeft = ImageIO.read(new File("src/core/Images/fire_left_1.png"));
			this.fireTileRight = ImageIO.read(new File("src/core/Images/fire_right_1.png"));
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		
		// Load the bomber image
		try {
			this.bomberTile = ImageIO.read(new File("src/core/Images/bomber_1.png"));
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		// Load the bomb image
		try {
			this.bombTile = ImageIO.read(new File("src/core/Images/bomb_1.png"));
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		// Load the box image
		try {
			this.boxTile = ImageIO.read(new File("src/core/Images/box_1.jpg"));
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		// Load the floor image
		try {
			this.floorTile = ImageIO.read(new File("src/core/Images/floor_tile_1.jpg"));
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	} // End readInImages()
	
	/**
	 * This method draws the explosion graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInExplosion(Graphics dbg) {	
		// Draws the explosions
		for(int i = 0; i < this.explosions.size(); i++) {
			try {
				int x = this.explosions.get(i).getPosn().getXCoord();
				int y = this.explosions.get(i).getPosn().getYCoord();
				dbg.drawImage(this.fireTileCenter, x, y, this);
				dbg.drawImage(this.fireTileUp, x, (y - TILE_SIZE), this);
				dbg.drawImage(this.fireTileDown, x, (y + TILE_SIZE), this);
				dbg.drawImage(this.fireTileLeft, (x - TILE_SIZE), y, this);
				dbg.drawImage(this.fireTileRight, (x + TILE_SIZE), y, this);
			}
			catch (NullPointerException e) {
				// Ignore exception because bomb is gone
			}
		} // End for
	} // End fillInExplosion()
	
	/**
	 * This method draws the bomber graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInBomber(Graphics dbg) {
		// Draws the bomber
		dbg.drawImage(bomberTile, player1.getPosition().getXCoord(), player1.getPosition().getYCoord(), this);
	} // End fillInBomber()
	
	/**
	 * This method draws the bomb graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInBombs(Graphics dbg) {
		// Draws all bombs in the bombs' ArrayList.
		for (int a = 0; a < bombs.size(); a++) {
			try {
				dbg.drawImage(bombTile, bombs.get(a).getPosition().getXCoord(), bombs.get(a).getPosition().getYCoord(), this);
			}
			catch (NullPointerException e) {
				// Ignore exception because bomb is gone
			}
		} // End for
	} // end fillInBombs()
	
	/**
	 * This method draws the box graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInBoxes(Graphics dbg) {
		// Draws all boxes in the boxes' ArrayList.
		for (int i = 0; i < boxes.size(); i++) {
			try {
				dbg.drawImage(boxTile, boxes.get(i).getPosition().getXCoord(), boxes.get(i).getPosition().getYCoord(), this);
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
		/*
		 * Checking to see if a bomb has blown up, if
		 * so, it removes neighboring objects.
		 */
		for (int i = 0; i < bombs.size(); i++) 
			if (bombs.get(i).checkExplode()) {
				this.explosions.add(new Explosion(bombs.get(i).getPosition()));
				removeShitAroundBomb(bombs.get(i).getPosition(), i);
			}
		
		/*
		 * Checking to see if an explosion has timed out,
		 * if so, it removes the explosion from the game.
		 */
		for(int j = 0; j < explosions.size(); j++)
			if(!this.explosions.get(j).checkBurn())
				this.explosions.remove(j);
	} // End gameUpdate()
	
	/**
	 * This method removes any neighboring bombers, 
	 * bombs, or boxes from the bomb that has exploded. 
	 * 
	 * @param posn The position of the bomb that exploded.
	 * @param index The index of the bomb from the ArrayList.
	 */
	private void removeShitAroundBomb(Position posn, int index) {
		/*
		 * This checks to see if player1 is in the bomb position,
		 * bomb x + 1 and y, x - 1 and y, x and y + 1, and x and y - 1.
		 * If there is a neighboring bomber, it is killed.
		 */
		if (player1.getPosition().compare(posn))
			System.out.println("You Died Dummy....");
		else if (player1.getPosition().compare(new Position(posn.getXCoord() + TILE_SIZE, posn.getYCoord())))
			System.out.println("You Died Dummy....");
		else if (player1.getPosition().compare(new Position(posn.getXCoord() - TILE_SIZE, posn.getYCoord())))
			System.out.println("You Died Dummy....");
		else if (player1.getPosition().compare(new Position(posn.getXCoord(), posn.getYCoord() + TILE_SIZE)))
			System.out.println("You Died Dummy....");
		else if (player1.getPosition().compare(new Position(posn.getXCoord(), posn.getYCoord() - TILE_SIZE)))
			System.out.println("You Died Dummy....");
		
		/*
		 * This checks to see if any box is in the bomb position,
		 * bomb x + 1 and y, x - 1 and y, x and y + 1, and x and y - 1.
		 * If there is a neighboring box, it gets destroyed.
		 */
		for(int b = (boxes.size() - 1); b > -1; b--) {			
			if(boxes.get(b).getPosition().compare(new Position(posn.getXCoord() + TILE_SIZE, posn.getYCoord())))
				boxes.remove(b);
			else if(boxes.get(b).getPosition().compare(new Position(posn.getXCoord() - TILE_SIZE, posn.getYCoord())))
				boxes.remove(b);
			else if(boxes.get(b).getPosition().compare(new Position(posn.getXCoord(), posn.getYCoord() + TILE_SIZE)))
				boxes.remove(b);
			else if(boxes.get(b).getPosition().compare(new Position(posn.getXCoord(), posn.getYCoord() - TILE_SIZE)))
				boxes.remove(b);
		}

		/*
		 * This checks to see if any bomb is in the bomb position,
		 * bomb x + 1 and y, x - 1 and y, x and y + 1, and x and y - 1.
		 * If there is a neighboring bomb, it is triggered to explode.
		 */
		for (int c = (bombs.size() - 1); c > -1; c--) {
			if (bombs.get(c).getPosition().compare(new Position(posn.getXCoord() + TILE_SIZE, posn.getYCoord())))
				bombs.get(c).setExplode(true);
			else if (bombs.get(c).getPosition().compare(new Position(posn.getXCoord() - TILE_SIZE, posn.getYCoord())))
				bombs.get(c).setExplode(true);
			else if (bombs.get(c).getPosition().compare(new Position(posn.getXCoord(), posn.getYCoord() + TILE_SIZE)))
				bombs.get(c).setExplode(true);
			else if (bombs.get(c).getPosition().compare(new Position(posn.getXCoord(), posn.getYCoord() - TILE_SIZE)))
				bombs.get(c).setExplode(true);
		} // End for
		
		// Removing this bomb
		bombs.remove(index);
	} // End removeShitAroundBomb()
	
	//TODO: Change x and y to compare with positions.
	/**
	 * This function is called from positionMover() to check against the game
	 * map and other players to see if a move is valid.
	 * 
	 * @param map The current map of the game.
	 * @param dPosition The desired position the player wants to go, supplied by positionMover().
	 * @return True if the move is valid, false if invalid.
	 */
	private boolean checkValidMove(/*char[][] map,*/ Position dPosition) {
		int x = dPosition.getXCoord();
		int y = dPosition.getYCoord();

		/*
		 * This loops through all the bombs on the map and sees if they
		 * are in a position that the Bomber wants to move to. If it is
		 * in the way, it returns false.
		 */
		for (int a = 0; a < bombs.size(); a++)
			if (x == bombs.get(a).getPosition().getXCoord() && y == bombs.get(a).getPosition().getYCoord())
				return false;
		
		/*
		 * This loops through all the boxes on the map and sees if they
		 * are in a position that the Bomber wants to move to. If it is
		 * in the way, it returns false.
		 */
		for (int i = 0; i < boxes.size(); i++) {
			try {
				if (x == boxes.get(i).getPosition().getXCoord() && y == boxes.get(i).getPosition().getYCoord())
					return false;
			}
			catch (NullPointerException e) {
				// Ignore exception because boxes wasn't full
			}
		} // End for
		
		/*
		 * If all the boxes are passed, we check the bounds and where
		 * the other Bomber is to make sure it is still a legal move.
		 */	
		if (x > (WIDTH - TILE_SIZE) || y > (HEIGHT - TILE_SIZE))
			return false;
		else if (x < 0 || y < 0)
			return false;
		else if (x == player1.getPosition().getXCoord() && y == player1.getPosition().getYCoord())
			return false;
		//else if (x == player2.getPosition().getXCoord() && y == player2.getPosition().getYCoord())
			//moveLegal = false;
		
		return true;
	} // End checkValidMove()
	
	/**
	 * This function will generate a desired position for each bomber and 
	 * check to see if it is a legal move or not.
	 * 
	 * @param p The player's number, 1 or 2.
	 * @param x How many spaces in the x-axis the player wants to move.
	 * @param y How many spaces in the y-axis the player wants to move.
	 * @return True if the move is valid, false if invalid.
	 */
	private boolean positionMover(int p, int x, int y) {
		Position desiredPosition = null;
		
		if (p == 1) {
			desiredPosition = new Position(
					player1.getPosition().getXCoord() + x,
					player1.getPosition().getYCoord() + y);
		}
		//else if (p == 2) {
		//	desiredPosition = new Position(
		//			player2.getPosition().getXCoord() + x,
		//			player2.getPosition().getYCoord() + y);
		//}
		
		boolean move = checkValidMove(/*map,*/ desiredPosition);

		return move;
	} // End positionMover()
	
	/**
	 * This method makes sure that the position the bomber
	 * wants to lay a bomb is in now currently occupied by
	 * another bomb.
	 * 
	 * @param posn The position the bomber wants to lay a bomb in.
	 * @return A boolean saying whether the lay is 'OK' or not.
	 */
	private boolean checkLayOK(Position posn) {
		boolean moveOK = true;
		
		for (int e = 0; e < bombs.size(); e++)
			if (bombs.get(e).getPosition().compare(posn))
				moveOK = false;
		
		return moveOK;
	} // End checkLayOK()
	
	// =================================================================================
	// End section for game logic
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
			case 10:	// ENTER
				// TODO: Use for menus
				break;
			case 16:	// SHIFT
				break;
			case 27:	// ESCAPE
				running = false;
				System.exit(0);
				break;
			case 32:	// (SPACE) BOMB Player 1
				if (checkLayOK(player1.getPosition()))
					bombs.add(new Bomb(new Position(player1.getPosition().getXCoord(), player1.getPosition().getYCoord())));
				//numBombs++;
				break;
			case 37:	// (â†�) LEFT Player 1
				if (positionMover(1, -TILE_SIZE, 0))
					player1.moveLeft();
				break;
			case 38:	// (â†‘) UP Player 1
				if (positionMover(1, 0, -TILE_SIZE))
					player1.moveUp();
				break;
			case 39:	// (â†’) RIGHT Player 1
				System.out.println("Client: RIGHT");
				output.println("RIGHT");
				try {
					System.out.println("Server: " + input.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (positionMover(1, TILE_SIZE, 0))
					player1.moveRight();
				
				gameUpdate();
				gameRender();
				paintScreen();
				break;
			case 40:	// (â†“) DOWN Player 1
				if (positionMover(1, 0, TILE_SIZE))
					player1.moveDown();
				break;
			case 65:	// (A) LEFT Player 2
				//if (positionMover(2, -TILE_SIZE, 0))
				//	player2.moveLeft();
				break;
			case 68:	// (D) RIGHT Player 2
				//if (positionMover(2, TILE_SIZE, 0))
				//	player2.moveRight();
				break;
			case 80:	// P
				// TODO: Use to pause and un-pause game
				break;
			case 83:	// (S) DOWN Player 2
				//if (positionMover(2, 0, TILE_SIZE))
				//	player2.moveDown();
				break;
			case 87:	// (W) UP Player 2
				//if (positionMover(2, 0, -TILE_SIZE))
				//	player2.moveUp();
				break;
			default:	// Any other key
				// Ignore other keystrokes
				break;
		} // End switch
	} // End keyPressed()
	
	// =================================================================================
	// End section for keyboard input
	// =================================================================================
} // End GamePanel
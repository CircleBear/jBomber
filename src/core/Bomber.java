package core;

/**
 * The Bomber class represents the bomber object in the Bomberman
 * game. This is the object that can be moved about the game board
 * and lay bombs per user command.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */

//Import statements
import java.awt.Graphics;

public class Bomber {
	private Position position; // the position of this Bomber in the grid
	private int bombs; // the number of bombs that this bomber has
	private int lives; // the number of lives that this bomber has
	private final int TILE_SIZE = 30;
	
	// Constructor(s)
	/**
	 * This is an overloaded constructor and assigns the Bomber
	 * a given position.
	 * 
	 * @param posn The starting position of the bomber.
	 */
	public Bomber(Position posn) {
		this.position = posn;
		this.lives = 1;
	} // End Bomber(Position)
	
	/**
	 * This is a constructor that defines the position and the
	 * number of lives for this bomber.
	 */
	public Bomber(Position posn, int numLives) {
		this.lives = numLives;
		this.position = posn;
	} // End Bomber(Position, lives)
	// End of constructor(s)
	
	/**
	 * This method updates the Bomber's position by decreasing the
	 * current y-coordinate by 1.
	 */
	public void moveUp() {
		int newY = position.getYCoord() - TILE_SIZE;
		position.setYCoord(newY);
	} // End moveUp()
	
	/**
	 * This method updates the Bomber's position by increasing the
	 * current y-coordinate by 1.
	 */
	public void moveDown() {
		int newY = position.getYCoord() + TILE_SIZE;
		position.setYCoord(newY);
	} // End moveDown()
	
	/**
	 * This method updates the Bomber's position by decreasing the
	 * current x-coordinate by 1.
	 */
	public void moveLeft() {
		int newX = position.getXCoord() - TILE_SIZE;
		position.setXCoord(newX);
	} // End moveLeft()
	
	/**
	 * This method updates the Bomber's position by increasing the
	 * current x-coordinate by 1.
	 */
	public void moveRight() {
		int newX = position.getXCoord() + TILE_SIZE;
		position.setXCoord(newX);
	} // End moveRight()
	
	/**
	 * Returns the position of the bomber.
	 * 
	 * @return A Position of the bomber's current position.
	 */
	public Position getPosition() {
		return position;
	} // End getPosition()
	
	/**
	 * Sets the position of the bomber.
	 * 
	 * @param position A Position variable of the desired position.
	 */
	public void setPosition(Position position) {
		this.position = position;
	} // End setPosition()
} // End Bomber class

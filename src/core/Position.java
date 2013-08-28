package core;

/**
 * The Position class represents the x-y coordinate position of an
 * object in the grid of a jBomber game board.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */
public class Position {
	private int xCoord; // the x coordinate of this position
	private int yCoord; // the y coordinate of this position
	
	// Constructor(s)
	/**
	 * This constructor defines an empty, default position instance
	 * whose xCoord and yCoord will both be set to 0.
	 */
	public Position() {
		this.xCoord = 0;
		this.yCoord = 0;
	} // End Position()
	
	/**
	 * This constructor defines a position instance whose xCoord and
	 * yCoord have been defined.
	 */
	public Position(int xCoord, int yCoord) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	} // End Position(x, y)
	// End Constructor(s)

	/**
	 * getXCoord is the method that returns the value of the private
	 * variable that represents this position's xCoord.
	 * 
	 * @return An int of the x-coord.
	 */
	public int getXCoord() {
		return this.xCoord;
	} // End getXCoord()

	/**
	 * setXCoord is the method that sets the value of this position's
	 * xCoord to the given int value.
	 * 
	 * @param xCoord The x-coord to be set.
	 */
	public void setXCoord(int xCoord) {
		this.xCoord = xCoord;
	} // End setXCoord()

	/**
	 * getYCoord is the method that returns the value of the private
	 * variable that represents this position's yCoord.
	 * 
	 * @return An int of the y-coord.
	 */
	public int getYCoord() {
		return this.yCoord;
	} // End getYCoord()

	/**
	 * setYCoord is the method that sets the value of this position's
	 * yCoord to the given int value.
	 * 
	 * @param yCoord The y-coord to be set.
	 */
	public void setYCoord(int yCoord) {
		this.yCoord = yCoord;
	} // End setYCoord()
	
	/**
	 * this method compares this position with the given position to
	 * see if they are equal.
	 */
	public boolean compare(Position posn){
		boolean equal = false;
		
		if(this.xCoord == posn.getXCoord() && this.yCoord == posn.getYCoord())
			equal = true;
		
		return equal;
	} // End compare()
} // End Position class

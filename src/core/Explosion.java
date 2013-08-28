package core;

/**
 * The purpose of this class is to represent an explosion in the
 * jBomber game.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */
public class Explosion {
	private static final long DEFAULT_BURN_TIME = 300000000L;
	private static final int DEFAULT_LENGTH = 1;
	
	private Position posn; // the center position of the explosion
	private long burnTime; // display time of the flames
	private long burnStart; // time when bomb exploded
	private int length; // length of the explosion.
	
	// Constructor(s)
	/**
	 * An overloaded constructor that sets the explosion position.
	 * 
	 * @param posn A Position variable of the explosion.
	 */
	public Explosion(Position posn) {
		this.posn = posn;
		this.burnStart = System.nanoTime();
		this.burnTime = DEFAULT_BURN_TIME;
		this.length = DEFAULT_LENGTH;
	} // End Explosion(Position)
	
	/**
	 * An overloaded constructor that sets the position and length.
	 * 
	 * @param posn A Position variable of the explosion.
	 * @param length The length that the explosion should last.
	 */
	public Explosion(Position posn, int length) {
		this.posn = posn;
		this.burnStart = System.nanoTime();
		this.burnTime = DEFAULT_BURN_TIME;
		this.length = length;
	} // End Explosion(Position, length)
	// End of constructor(s)
	
	/**
	 * The purpose of this method is to check if explosion should
	 * still exist or if it has "burned out." If the explosion
	 * should still exist, then it should continue to be displayed
	 * in the GamePanel, but if it has burned out, then it needs
	 * to be removed from the ArrayList<Explosion> so that it is
	 * no longer displayed.
	 * 
	 * @return A boolean if it is still burning.
	 */
	public boolean checkBurn() {
		boolean burn = true;
		
		if(0 > this.burnStart - System.nanoTime() + this.burnTime)
			burn = false;
		
		return burn;
	} // End checkBurn()
	
	/**
	 * This method sets the burn time for the explosion.
	 * 
	 * @param burnTime The burn time to set.
	 */
	public void setBurnTime(long burnTime) {
		this.burnTime = burnTime;
	} // End setBurnTime()
	
	/**
	 * This method gets the burn time for the explosion.
	 * 
	 * @return A long of the burn time.
	 */
	public long getBurnTime() {
		return burnTime;
	} // End getBurnTime()
	
	/**
	 * This method sets the length of the explosion.
	 * 
	 * @param length The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	} // End setLength()
	
	/**
	 * This method returns the length of the explosion.
	 * 
	 * @return An int of the length.
	 */
	public int getLength() {
		return length;
	} // End getLength()

	/**
	 * Sets the burn start time.
	 * 
	 * @param burnStart The burn start to set.
	 */
	public void setBurnStart(long burnStart) {
		this.burnStart = burnStart;
	} // End setBurnStart()

	/**
	 * Returns the start time of the burn.
	 * 
	 * @return A long of the start time.
	 */
	public long getBurnStart() {
		return burnStart;
	} // End getBurnStart()

	/**
	 * Returns the current position of the explosion.
	 * 
	 * @param posn The position of the explosion.
	 */
	public void setPosn(Position posn) {
		this.posn = posn;
	} // End setPosn()

	/**
	 * Gets the current position of the explosion.
	 * 
	 * @return A Position variable of the current position.
	 */
	public Position getPosn() {
		return posn;
	} // End getPosn()
} // End Explosion class

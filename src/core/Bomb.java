package core;

/**
 * The purpose of this class is to represent a Bomb in the jBomber
 * world. Bomb is an item, so it extends item.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */

// Import statements
import java.awt.Graphics;

public class Bomb extends Item {
	// The default time it takes for this bomb to explode
	public static final long DEFAULT_FUSE_LENGTH = 2000000000L;
	
	// The position of this bomb
	private Position posn;
	
	// The fuse length of this bomb
	private long fuseLength;
	private long dropTime;
	
	// Value if bomb will explode or not
	private boolean explode;
	
	// Constructor(s)
	/**
	 * This is an overloaded constructor that takes in a Position.
	 * 
	 * @param posn The position of the bomb.
	 */
	public Bomb(Position posn) {
		this.posn = posn;
		this.fuseLength = Bomb.DEFAULT_FUSE_LENGTH;
		this.dropTime = System.nanoTime();
		this.explode = false;
	} // End Bomb(Position)

	/**
	 * This is an overloaded constructor that takes in a Position
	 * and a fuse length.
	 * 
	 * @param posn The position of the bomb.
	 * @param fuseLength The long fuse length of the bomb.
	 */
	public Bomb(Position posn, long fuseLength) {
		this.posn = posn;
		this.fuseLength = fuseLength;
	} // End Bomb(Position, long)
	// End Constructor(s)
	
	@Override
	public Position getPosition() {
		return this.posn;
	} // End getPosition()

	@Override
	public void setPosition(Position newPosn) {
		this.posn = newPosn;
	} // End setPosition()

	/**
	 * Returns the fuse length of the bomb.
	 * 
	 * @return A long of the fuse length.
	 */
	public long getFuseLength() {
		return this.fuseLength;
	} // End getFuseLength()
	
	/**
	 * Sets the fuse length of the bomb.
	 * 
	 * @param newFuseLength A long of the fuse length.
	 */
	public void setFuseLength(long newFuseLength) {
		this.fuseLength = newFuseLength;
	} // End setFuseLength()
	
	/**
	 * Updates the fuse length of the bomb.
	 * 
	 * @param newFuseLength A long of the fuse length to be added.
	 */
	public void addToFuseLength(long newFuseLength) {
		this.fuseLength = newFuseLength + this.fuseLength;
	} // End setFuseLength()
	
	/**
	 * Checks to see if the fuse is gone to explode.
	 * 
	 * @return A boolean if the bomb will explode.
	 */
	public boolean checkExplode() {
		long currentFuse = System.nanoTime() - this.getDropTime();
		
		if (currentFuse >= this.getFuseLength() || explode)
			return true;
		else
			return false;
	} // End checkExplode()
	
	/**
	 * Returns the drop time of the bomb.
	 * 
	 * @return A long of the drop time.
	 */
	public long getDropTime() {
		return dropTime;
	} // End getDropTime()

	/**
	 * Sets the drop time of the bomb.
	 * 
	 * @param dropTime The drop time to set.
	 */
	public void setDropTime(long dropTime) {
		this.dropTime = dropTime;
	} // End setDropTime()
	
	public void draw() {
		// Do nothing right now
	} // End draw

	/**
	 * Sets the explode variable to user input.
	 * 
	 * @param explode Boolean to set explode.
	 */
	public void setExplode(boolean explode) {
		this.explode = explode;
	} // End setExplode()

	/**
	 * Returns if a bomb will explode.
	 * 
	 * @return A boolean if bomb will explode.
	 */
	public boolean isExplode() {
		return explode;
	} // End isExplode()
} // End Bomb class

package core;

/**
 * The purpose of this class is to represent a Box in the jBomber
 * world. Box is an item, so it extends item.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */

//Import statements
import java.awt.Graphics;

public class Box extends Item {
	/*
	 * These are constant ints that represent different types of
	 * boxes that are in the world. For v1.0, only WOOD_CRATE is
	 * needed. Future implementations may advantage from using 
	 * different types and assigning that through the other
	 * constructor.
	 */
	private static final int WOOD_CRATE = 0;
	private static final int STEEL_BOX = 1;
	
	// The position of this Box in the game world
	private Position posn;
	
	// The type of this Box
	private int type;
	
	// Constructor(s)
	/**
	 * This is the default constructor that assigns a default type of
	 * wood crate and a position specified by the input parameter.
	 * 
	 * @param posn A Position variable of the position of this box.
	 */
	public Box(Position posn) {
		this.posn = posn;
		this.setType(Box.WOOD_CRATE);
	} // End Box(Position)
	
	/**
	 * This is an overloaded constructor that assigns a type and position
	 * based upon the input parameters.
	 * 
	 * @param posn A Position variable of the position of this box.
	 * @param type An integer representing what type of box this is.
	 */
	public Box(Position posn, int type) {
		this.posn = posn;
		//this.type = type;
		if (type == 1)
			this.setType(Box.STEEL_BOX);
		else
			this.setType(Box.WOOD_CRATE);
	} // End Box(Position, type)
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
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
} // End Item class

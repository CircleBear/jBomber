package core;

/**
 * The MapParser class is used for reading from text files that have
 * map information stored in them. It stores this information in a
 * double array.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */

// Imported Classes
import java.io.*;
import java.util.ArrayList;

public class MapParser 
{
	private static final int TILE_SIZE = 30;
	
	// Constructor(s)
	/**
	 * The main constructor of the MapParser class.
	 */
	public MapParser() {
		// Do nothing
	} // End MapParser()
	// End Constructor(s)
	
	/**
	 * This method is used to parse a CircleBear map (.cbm) to load into the
	 * jBomber game world.
	 * 
	 * @param filename A .cbm file to be parsed by the jBomber system.
	 * @return A double-char array of the map with all the objects in it.
	 */
	public char[][] parseMap(String filename) {
		BufferedReader br = null;
		char[][] map = new char[10][10];
		
		// Trying to read in the map file
		try {
			File mapFile = new File(filename);
			br = new BufferedReader(new FileReader(mapFile));
			char[][] mapArray = new char[10][10];
		} // End try
		catch(FileNotFoundException e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
		} // End catch
		finally {
			try {
				// Parsing the map into a char[][]
				if(br != null) {
					String line = br.readLine();
					int row = 0;
					
					while (line != null) {
						for (int i = 0; i < 10; i++) 
							map[row][i] = line.charAt(i);
						
						row++;
						line = br.readLine();
					} // End while
					
					// Closing the buffered reader
					br.close();
				} // End if
			} // End try
			catch(IOException e) {
				// Don't care about this exception.
			} // End catch
		} // End finally
		
		// Printing the map
		printMap(map);
		
		return map;
	} // End parseMap()
	
	/**
	 * This method prints out the map so the user can
	 * see how it was parsed in.
	 * 
	 * @param m A map to be printed.
	 */
	private void printMap(char[][] m) {
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				if (j == 9)
					System.out.print(m[i][j] + "\n");
				else
					System.out.print(m[i][j]);
	} // End printMap
	
	/**
	 * This method finds all boxes in the map and creates
	 * an ArrayList of their positions.
	 * 
	 * @param m A map to find boxes on.
	 * @return An ArrayList of the positions of all boxes.
	 */
	public static ArrayList<Box> findBoxes(char[][] m) {
		ArrayList<Box> boxes = new ArrayList<Box>();
		int x = 0, y = 0;
		
		// Checking the map for boxes and assigning positions
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				if (m[i][j] == 'x' || m[i][j] == 's') {
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
					
					if(m[i][j] == 'x')
					{
						boxes.add(new Box(new Position(x, y)));
					}
					else
					{
						boxes.add(new Box(new Position(x, y), 1));
					}
					
				} // End if
		
		return boxes;					
	} // End findBoxes
} // End MapParser class

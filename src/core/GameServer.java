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
import java.net.ServerSocket;
import java.net.Socket;
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
public class GameServer   {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		try {
			serverSocket = new ServerSocket(2488);
		} // End try
		catch (IOException e) {
			System.err.println("Could not listen on port: 2488");
			System.exit(1);
		} // End catch IOException
		
		try {
			clientSocket = serverSocket.accept();
		} // End try
		catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		} // End catch IOException
		
		PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String inputLine, outputLine;
		int worldCounter = 0;
		
		outputLine = "Hello World! - " + worldCounter;
		output.println(outputLine);
		worldCounter++;
		
		// TODO: If certain text is received from client, it quits
		// TODO: Send certain information when server quits
		while ((inputLine = input.readLine()) != null) {
			outputLine = "Hello World! - " + worldCounter;
			output.println(outputLine);
			worldCounter++;
			
			if (worldCounter == 2) {
				outputLine = "run";
				output.println(outputLine);
				worldCounter++;
			}
			
			if (worldCounter == 10)
				break;
		} // End while
		
		output.close();
		input.close();
		clientSocket.close();
		serverSocket.close();
	} // End main
} // End GamePanel
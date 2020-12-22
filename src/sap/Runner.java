package sap;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import visuals.View;

public class Runner {
	public static JFrame main_frame;
	
	public static void main(String[] args) {
		View view = new View();

		// Create a frame and add the view to it 
		main_frame = new JFrame();
		main_frame.setTitle("SAP-1 Simulator by Milen Patel");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.setContentPane(view);

		// Lock in dimensions 
		main_frame.setPreferredSize(new Dimension(250+2318/3, 50+1600/3));
		main_frame.setResizable(false);

		// Make the frame visible 
		main_frame.pack();
		main_frame.setVisible(true);
	}
}


// Add grammar for assembler
// Add RAM MAR selector feature
// Make constants public and use those for coloring
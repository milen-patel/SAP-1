package sap;

import java.awt.Dimension;

import javax.swing.JFrame;

import visuals.View;

public class Runner {
	
	public static void main(String[] args) {
		View view = new View();

		// Create a frame and add the view to it 
		JFrame main_frame = new JFrame();
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

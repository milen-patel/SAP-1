package Main;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Runner {
	
	public static void main(String[] args) {
		
		System.out.println("Start of main method execution");
		View view = new View();

		/* Create a frame and add the view to it */
		JFrame main_frame = new JFrame();
		main_frame.setTitle("SAP-1 Simulator by Milen Patel");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.setContentPane(view);

		/* Lock in dimensions */
		main_frame.setPreferredSize(new Dimension(1200, 960));
		main_frame.setResizable(false);

		/* Make the frame visible */
		main_frame.pack();
		main_frame.setVisible(true);
	}
}

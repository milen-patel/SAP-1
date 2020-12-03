package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class View extends JPanel {
	SAPModel model;
	JLabel welcome_label;

	public View() {
		this.model = new SAPModel();
		this.setBackground(Color.pink);
		this.setLayout(new BorderLayout());
		welcome_label = new JLabel("Welcome!");
		welcome_label.setHorizontalAlignment(SwingConstants.CENTER);
		welcome_label.setForeground(Color.BLUE);
		welcome_label.setBackground(Color.YELLOW);
		this.add(welcome_label, BorderLayout.CENTER);

	}
}

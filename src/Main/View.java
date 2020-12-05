package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class View extends JPanel {
	SAPModel model;
	JLabel welcome_label;
	private GridBagConstraints c;


	public View() {
		this.model = new SAPModel();
		
		/* Set the Layout */
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		this.setPreferredSize(new Dimension(1000, 1000));
		
		welcome_label = new JLabel("Welcome!");
		welcome_label.setForeground(Color.BLUE);
		welcome_label.setBackground(Color.YELLOW);
		c.gridx = 4;
		c.gridy = 9;
		c.gridheight = 1;
		//this.add(welcome_label, c);
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage icon;

		try {
			icon = ImageIO.read(getClass().getResource("/include/Background.png"));
			g.drawImage(icon, 0, 0, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
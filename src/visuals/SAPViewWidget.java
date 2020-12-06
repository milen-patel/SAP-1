package visuals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.*;

import sap.SAPModel;

public class SAPViewWidget extends JPanel {
	private sap.SAPModel model;
	private GridBagConstraints c;

	
	public SAPViewWidget(sap.SAPModel model) {
		this.model = model;
		
		/* Set our preferred size */
		this.setPreferredSize(new Dimension(525, 500));
		this.setBackground(Color.CYAN);
		
		/* Set the Layout */
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;
		this.add(new JLabel("SAP Status"), c);
		
		repaint();
	}

}

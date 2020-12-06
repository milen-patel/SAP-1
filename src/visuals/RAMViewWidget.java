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

public class RAMViewWidget extends JPanel {
	private sap.SAPModel model;
	private GridBagConstraints c;
	private JButton[][] butts;
	private static Dimension buttonSize = new Dimension(20,20);
	
	/*
	 *  Address: [0, 15]
	 *  bitPos: [0, 7]
	 */
	private int lookupRAM(int address, int bitPos) {
		int val = this.model.getRAM().getRAM()[address];
		return (val >> bitPos) & 0b1;
	}

	public RAMViewWidget(sap.SAPModel model) {
		this.model = model;
		
		butts = new JButton[16][8];
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 8; j++) {
				this.butts[i][j] = new JButton(""+lookupRAM(i, 7-j));
				this.butts[i][j].setPreferredSize(buttonSize);
			}
		}
		
		/* Set our preferred size */
		this.setPreferredSize(new Dimension(350, 500));
		this.setBackground(Color.PINK);
		
		/* Set the Layout */
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;
		this.add(new JLabel("Memory Content"), c);
		c.gridx = 1;
		c.gridy = 1;
		this.add(new JLabel("[0000]: "), c);
	   

		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		
		c.gridx = 1;
		c.gridy = 2;
		this.add(new JLabel("[0001]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 3;
		this.add(new JLabel("[0010]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 4;
		this.add(new JLabel("[0011]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 5;
		this.add(new JLabel("[0100]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 6;
		this.add(new JLabel("[0101]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 7;
		this.add(new JLabel("[0110]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 8;
		this.add(new JLabel("[0111]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 9;
		this.add(new JLabel("[1000]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 10;
		this.add(new JLabel("[1001]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 11;
		this.add(new JLabel("[1010]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 12;
		this.add(new JLabel("[1011]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 13;
		this.add(new JLabel("[1100]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 14;
		this.add(new JLabel("[1101]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 15;
		this.add(new JLabel("[1110]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		c.gridx = 1;
		c.gridy = 16;
		this.add(new JLabel("[1111]: "), c);
		for (int i = 2; i < 10; i++) {
			c.gridx = i;
			this.add(butts[c.gridy-1][i-2], c);
		}
		repaint();
	}

}

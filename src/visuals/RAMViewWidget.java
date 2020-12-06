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

public class RAMViewWidget extends JPanel implements sap.RAMObserver {
	private sap.SAPModel model;
	private GridBagConstraints c;
	private JButton[][] butts;
	private static Dimension buttonSize = new Dimension(20,20);
	
	/*
	 *  Address: [0, 15]
	 *  bitPos: [0, 7]
	 */
	private int lookupRAM(int address, int bitPos) {
		int val = 0b1111 & this.model.getRAM().getRAM()[address];
		return (val >> bitPos) & 0b1;
	}

	public RAMViewWidget(sap.SAPModel model) {
		this.model = model;
		// Add ourselves as a RAMObserver
		this.model.getRAM().addRAMObserver(this);
		
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
		
		for (int i = 1; i <= 16; i++) {
			c.gridx = 1;
			c.gridy = i;
			switch (i-1) {
			case 0:
				this.add(new JLabel("[0000]: "), c);
				break;
			case 1:
				this.add(new JLabel("[0001]: "), c);
				break;
			case 2:
				this.add(new JLabel("[0010]: "), c);
				break;
			case 3:
				this.add(new JLabel("[0011]: "), c);
				break;
			case 4:
				this.add(new JLabel("[0100]: "), c);
				break;
			case 5:
				this.add(new JLabel("[0101]: "), c);
				break;
			case 6:
				this.add(new JLabel("[0110]: "), c);
				break;
			case 7:
				this.add(new JLabel("[0111]: "), c);
				break;
			default:
				this.add(new JLabel("["+Integer.toBinaryString(i-1)+"]: "), c);

			}
			for (int j = 2; j < 10; j++) {
				c.gridx = j;
				this.add(butts[c.gridy-1][j-2], c);
			}
		}
		
		repaint();
	}

	@Override
	public void valChanged(int address) {
		for (int i = 0; i <= 7; i++) {
			this.butts[address][i].setText(""+lookupRAM(address,7-i));
		}
	}

}

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
import javax.swing.*;

import sap.SAPModel;

public class View extends JPanel implements sap.LogObserver, ActionListener, sap.ClockObserver {

	private SAPModel model;
	private JLabel welcome_label;
	private GridBagConstraints c;
	private JTextArea logLabel;
	private JButton resetButton;
	private JButton clockButton;
	private SAPViewWidget viewWidget;
	private RAMViewWidget ramWidget;

	public View() {
		this.model = new SAPModel();

		/* Set the Layout */
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		this.setPreferredSize(new Dimension(1000, 1000));

		this.viewWidget = new SAPViewWidget(this.model);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 6;

		this.add(viewWidget, c);

		this.ramWidget = new RAMViewWidget(this.model);
		c.gridx = 0;
		this.add(ramWidget, c);

		welcome_label = new JLabel("Clock: " + (sap.Clock.getClock().getStatus() ? "HIGH" : "LOW"));

		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		this.add(welcome_label, c);

		resetButton = new JButton("Reset");
		resetButton.setActionCommand("resetButtonClicked");
		resetButton.addActionListener(this);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		this.add(resetButton, c);

		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		this.clockButton = new JButton("Toggle Clock");
		this.clockButton.addActionListener(this);
		this.clockButton.setActionCommand("clockButton");
		this.add(clockButton, c);
		
		logLabel = new JTextArea(1, 1);
		logLabel.setEditable(false);
		c.gridx = 3;
		c.gridy = 3;
		c.ipadx = 220;
		c.ipady = 350;
		c.gridheight = 7;
		c.fill = GridBagConstraints.VERTICAL;

		this.add(new JScrollPane(logLabel), c);

		// Add the view as a log observer
		sap.EventLog.getEventLog().addObserver(this);
		// Add the view as a clock observer
		sap.Clock.getClock().addObserver(this);
	}

	// Implement log observer method
	@Override
	public void newLogEntry(String entry) {
		logLabel.append(entry + "\n");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("resetButtonClicked")) {
			this.model.reset();
		} else if (e.getActionCommand().contentEquals("clockButton")) {
			sap.Clock.getClock().toggleClock();
		}
	}

	@Override
	public void clockChange() {
		this.welcome_label.setText("Clock: " + (sap.Clock.getClock().getStatus() ? "HIGH" : "LOW"));
	}

}
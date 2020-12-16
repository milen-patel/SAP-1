package visuals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import sap.SAPModel;
import javax.swing.text.DefaultCaret;
public class View extends JPanel implements interfaces.LogObserver, ActionListener, interfaces.ClockObserver {

	private SAPModel model;
	private JLabel welcome_label;
	private GridBagConstraints c;
	private JTextArea logLabel;
	private JButton resetButton;
	private JButton clockButton;
	private JButton playButton;
	private SAPViewWidget viewWidget;
	private RAMViewWidget ramWidget;

	private boolean isAutoRunning;
	private BackgroundRunner bRunner;
	private static final int AUTOPLAY_SPEED_MS = 10;
	private static final Color VIEW_BACKGROUND_COLOR = new Color(225,246,203);

	public View() {
		this.model = new SAPModel();
		this.setBackground(VIEW_BACKGROUND_COLOR);
		

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
		c.gridy = 0;
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

		c.gridx = 3;
		c.gridy = 3;
		c.gridheight = 1;
		this.playButton = new JButton("Autoplay");
		this.playButton.setActionCommand("autoplay");
		this.playButton.addActionListener(this);
		this.add(playButton, c);

		// Add gap to the left of the log
		c.insets = new Insets(0,6,0,0);
		logLabel = new JTextArea(1, 1);
		logLabel.setMaximumSize(new Dimension(20,20));
		logLabel.setEditable(false);
		c.gridx = 3;
		c.gridy = 4;
		c.ipadx = 240;
		c.ipady = 350;
		c.gridheight = 7;
		c.fill = GridBagConstraints.VERTICAL;
		 DefaultCaret caret = (DefaultCaret)logLabel.getCaret();
		 caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
logLabel.setLineWrap(true);		
		JScrollPane sv = new JScrollPane(logLabel);
		sv.setAutoscrolls(true);
		sv.setPreferredSize(new Dimension(20,100));
		sv.setMaximumSize(new Dimension(20,100));

		this.add(sv, c);

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
		} else if (e.getActionCommand().contentEquals("autoplay")) {
			if (isAutoRunning) {
				isAutoRunning = false;
				bRunner.terminate();
				bRunner = null;
			} else {
				isAutoRunning = true;
				bRunner = new BackgroundRunner(AUTOPLAY_SPEED_MS);
				bRunner.start();
			}
		}
	}

	@Override
	public void clockChange() {
		this.welcome_label.setText("Clock: " + (sap.Clock.getClock().getStatus() ? "HIGH" : "LOW"));
	}

}
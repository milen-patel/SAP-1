package visuals;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import sap.SAPModel;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JButton;

public class Assembler extends JPanel implements ActionListener {

	// Define instance variables
	private GridBagConstraints c;
	private JTextArea inputField;
	private JLabel outputField;
	private JButton assembleButton;
	private JButton sendToSapButton;
	private SAPModel model;

	// Define constants
	private static final int SCREEN_X = 2318 / 3;
	private static final int SCREEN_Y = 1600 / 3;
	private static final int BUTTON_PANEL_HEIGHT = 50;
	private static final Dimension WIDGET_SIZE = new Dimension(SCREEN_X, SCREEN_Y);
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final char ASSEMBLER_COMMENT_SYMBOL = '#';

	public Assembler(SAPModel model) {
		// Encapsulate SAP Model
		this.model = model;

		// Set preferred size and color
		this.setPreferredSize(WIDGET_SIZE);
		this.setBackground(BACKGROUND_COLOR);

		// Set the Layout
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.fill = GridBagConstraints.NORTH;

		// Add the text input area
		this.inputField = new JTextArea();
		this.inputField.setPreferredSize(new Dimension(SCREEN_X / 2, SCREEN_Y - BUTTON_PANEL_HEIGHT));
		this.inputField.setMaximumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - BUTTON_PANEL_HEIGHT));
		this.inputField.setMinimumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - BUTTON_PANEL_HEIGHT));
		c.gridx = 0;
		c.gridy = 0;
		this.add(this.inputField, c);

		// Add the compiled program
		this.outputField = new JLabel("Assembled program here");
		this.outputField.setPreferredSize(new Dimension(SCREEN_X / 2, SCREEN_Y - BUTTON_PANEL_HEIGHT));
		this.outputField.setMaximumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - BUTTON_PANEL_HEIGHT));
		this.outputField.setMinimumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - BUTTON_PANEL_HEIGHT));
		c.gridx = 2;
		c.gridy = 0;
		this.add(this.outputField, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;

		// Add the assembler button
		c.gridx = 0;
		c.gridy = 1;
		this.assembleButton = new JButton("Assemble");
		this.assembleButton.setActionCommand("assemble");
		this.assembleButton.addActionListener(this);

		this.add(this.assembleButton, c);

		// Add the send to sap button
		c.gridx = 1;
		c.gridy = 1;
		this.sendToSapButton = new JButton("Send to SAP");
		this.sendToSapButton.setActionCommand("sendtosap");
		this.sendToSapButton.addActionListener(this);

		this.add(this.sendToSapButton, c);

		// Add the decompile button
		c.gridx = 2;
		c.gridy = 1;
		this.add(new JButton("Decompile current program"), c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().contentEquals("assemble")) {
			this.outputField.setText("Assembling...");
			this.outputField.setText(parseProgram(this.inputField.getText()));
			return;
		}
		if (e.getActionCommand().contentEquals("sendtosap")) {
			this.outputField.setText("Sending to sap...");
			return;
		}
	}

	private String parseProgram(String text) {
		String rVal = "<html>" + text.replaceAll("\n", "<br>") + "</html>";

		// Edge case
		if (text.contentEquals("") || text.isBlank()) {
			return "<html>[Assembler Failed] No Detectable Program.</html>";
		}

		// Parse input into String array
		String[] arr = text.split("\n");

		// Remove comments, force string to uppercase
		for (int i = 0; i < arr.length; i++) {
			// Grab the current string
			String s = arr[i];

			// If it has a comment in it, remove it
			if (s.indexOf(ASSEMBLER_COMMENT_SYMBOL) != -1) {
				s = s.substring(0, s.indexOf(ASSEMBLER_COMMENT_SYMBOL));
				arr[i] = s.toUpperCase();
			} else {
				arr[i] = arr[i].toUpperCase();
			}
		}

		// Remove blank lines
		List<String> result = new ArrayList<String>();

		for (String str : arr) {
			if (str != null && !str.isEmpty()) {
				result.add(str);
			}
		}

		// Remove Whitespace
		for (int i = 0; i < result.size(); i++) {
			result.set(i, result.get(i).replaceAll("\\s+", ""));
		}

		// Parse labels
		Map<String, Integer> labelLookup = new TreeMap<String, Integer>();

		int numLabels = 0;
		for (int i = 0; i < result.size(); i++) {
			String curr = result.get(i);

			if (curr.length() > 0 && curr.charAt(0) == '.') {
				numLabels++;

				// If the current label isn't define
				if (curr.length() == 1 || curr.substring(1).isBlank() || curr.substring(1).isEmpty()) {
					return "<html>[Assembler Failed] Label definition is incomplete.</html>";
				}

				// If the current label has already been used
				if (labelLookup.containsKey(curr.substring(1))) {
					return "<html>[Assembler Failed] Label '" + curr + "' has been defined more than once.</html>";
				}

				// Add label to our lookup table
				labelLookup.put(curr.substring(1), i + 1);
			}
		}
		
		// Remove all labels from the array (List<String>)
		for (String s : labelLookup.keySet()) {
			result.remove("."+s);
		}

		// Validate we have enough memory for the program
		if (result.size() > 16) {
			return "<html>[Assembler Failed] Cannot compile program into 16 bytes.</html>";
		}
/*
 * 
lda 5
.loop
out
j loop
 * 
 */
		// Grab all of the labels
		Set<String> labels =  labelLookup.keySet();
		
		// Replace labels with numerical locations
		for (int i = 0; i < result.size(); i++) {
			// Check all keys for a match
			for (String currLabel : labels) {
				if (result.get(i).indexOf(currLabel+"\n") != -1) {
					// Match!
					
				}
			}
		}

		rVal = "<html>";
		for (int i = 0; i < result.size(); i++) {
			rVal += result.get(i) + "<br>";
		}
		return rVal + "</html>";
	}
}

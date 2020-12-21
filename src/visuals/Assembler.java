package visuals;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import sap.Runner;
import sap.SAPModel;
import sap.SAPModel.InstructionTypes;

import java.util.*;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class Assembler extends JPanel implements ActionListener {

	// Define instance variables
	private GridBagConstraints c;
	private JTextArea inputField;
	private JLabel outputField;
	private JButton assembleButton;
	private JButton sendToSapButton;
	private JButton exitButton;
	private SAPModel model;
	private JPanel returnPanel;

	private static final int SCREEN_X = 225 + (2318 / 3);
	private static final int SCREEN_Y = 50 + (1600 / 3);
	private static final int BUTTON_PANEL_HEIGHT = 50;
	private static final Dimension WIDGET_SIZE = new Dimension(SCREEN_X, SCREEN_Y);
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final char ASSEMBLER_COMMENT_SYMBOL = '#';

	public Assembler(SAPModel model, JPanel returnPanel) {
		// Encapsulate SAP Model and return view
		this.model = model;
		this.returnPanel = returnPanel;

		// Set preferred size and color
		this.setPreferredSize(WIDGET_SIZE);
		this.setBackground(BACKGROUND_COLOR);

		// Set the Layout
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridwidth = 1;

		// Add the text input area
		this.inputField = new JTextArea();
		this.inputField.setPreferredSize(new Dimension(SCREEN_X / 2, SCREEN_Y - 50));
		this.inputField.setMaximumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - 50));
		this.inputField.setMinimumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - 50));
		this.inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK ));

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 6;
		this.add(this.inputField, c);
		c.insets = new Insets(0, 10, 0, 0);
		c.gridheight = 1;
		
		// Add the compiled program
		this.outputField = new JLabel("Assembled program here");
		this.outputField.setPreferredSize(new Dimension(SCREEN_X / 2, SCREEN_Y - 3*BUTTON_PANEL_HEIGHT - 20));
		this.outputField.setMaximumSize(new Dimension(SCREEN_X / 2, SCREEN_Y - 3*BUTTON_PANEL_HEIGHT - 20));
		this.outputField.setMinimumSize(new Dimension(SCREEN_X / 2, SCREEN_Y -3*BUTTON_PANEL_HEIGHT - 20));
		this.outputField.setBorder(BorderFactory.createLineBorder(Color.BLACK ));

		c.gridx = 2;
		c.gridy = 0;
		this.add(this.outputField, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;

		// Add the assembler button
		c.gridx = 2;
		c.gridy = 1;
		this.assembleButton = new JButton("Assemble");
		this.assembleButton.setActionCommand("assemble");
		this.assembleButton.addActionListener(this);

		this.add(this.assembleButton, c);

		// Add the send to sap button
		c.gridx = 2;
		c.gridy = 2;
		this.sendToSapButton = new JButton("Send to SAP");
		this.sendToSapButton.setActionCommand("sendtosap");
		this.sendToSapButton.addActionListener(this);

		this.add(this.sendToSapButton, c);

		// Add the decompile button
		c.gridx = 2;
		c.gridy = 3;
		this.add(new JButton("Decompile current program"), c);

		// Add the exit button
		c.gridx = 2;
		c.gridy = 4;
		this.exitButton = new JButton("Exit");
		this.exitButton.setActionCommand("exit");
		this.exitButton.addActionListener(this);
		this.add(this.exitButton, c);
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
		if (e.getActionCommand().contentEquals("exit")) {
			Runner.main_frame.setContentPane(this.returnPanel);
			Runner.main_frame.pack();
			Runner.main_frame.setVisible(true);

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

		// Parse variables
		Map<String, String> varValLookup = new TreeMap<String, String>();
		Map<String, Integer> varAddressLookup = new TreeMap<String, Integer>();
		Set<String> deletionIndices = new HashSet<String>();
		int nextAddress = 15;

		for (int i = 0; i < result.size(); i++) {
			// Grab the current instruction
			String curr = result.get(i);

			// Is the current instruction a variable declaration
			if (curr.indexOf("LET") == 0) {
				// Make sure it is a valid definition
				if (curr.indexOf("=") == -1 || curr.indexOf("=") == 3 || curr.length() == 4) {
					return "<html>[Assembler Failed] Invalid variable declaration: " + curr + ".</html>";
				}
				String varName = curr.substring(3, curr.indexOf("="));
				String varVal = curr.substring(curr.indexOf("=") + 1);

				// Make sure name is strictly alphabetical
				for (int j = 0; j < varName.length(); j++) {
					if (varName.charAt(j) >= '0' && varName.charAt(j) <= '9') {
						return "<html>[Assembler Failed] Variable names cannot contain numbers.</html>";
					}
				}

				// Make sure variable value is valid
				if (!isValidBinaryString(varVal)) {
					return "<html>[Assembler Failed] Invalid variable value '" + varVal + "'.</html>";
				}

				if (varVal.length() == 0) {
					return "<html>[Assembler Failed] All variables must be defined with a value</html>";
				}

				// Check for duplicate variable declaration
				for (String s : varValLookup.keySet()) {
					if (s.contentEquals(varName)) {
						return "<html>[Assembler Failed] Duplicate variable declaration of '" + s + "'.</html>";
					}
				}
				// Add to the map
				varValLookup.put(varName, varVal);
				varAddressLookup.put(varName, nextAddress--);
				deletionIndices.add(curr);
			}
		}

		// Remove variable declarations from program
		for (String i : deletionIndices) {
			result.remove(i);
		}

		// Parse labels
		Map<String, Integer> labelLookup = new TreeMap<String, Integer>();

		int pos = 0;
		for (int i = 0; i < result.size(); i++) {
			String curr = result.get(i);

			if (curr.length() > 0 && curr.charAt(0) == '.') {

				// If the current label isn't define
				if (curr.length() == 1 || curr.substring(1).isBlank() || curr.substring(1).isEmpty()) {
					return "<html>[Assembler Failed] Label definition is incomplete.</html>";
				}

				// If the current label has already been used
				if (labelLookup.containsKey(curr.substring(1))) {
					return "<html>[Assembler Failed] Label '" + curr + "' has been defined more than once.</html>";
				}

				// Add label to our lookup table
				labelLookup.put(curr.substring(1), pos);
			} else {
				pos++;
			}
		}

		// Remove all labels from the array (List<String>)
		for (String s : labelLookup.keySet()) {
			result.remove("." + s);
		}

		// Validate we have enough memory for the program
		if (result.size() + deletionIndices.size() > 16) {
			return "<html>[Assembler Failed] Cannot compile program into 16 bytes.</html>";
		}

		// Grab all of the labels
		Set<String> labelsSet = labelLookup.keySet();
		List<String> labels = new ArrayList<String>();
		labels.addAll(labelsSet);

		// Sort labels by descending length
		labels.sort(Comparator.comparingInt(String::length));

		// Replace labels with numerical locations
		for (int i = 0; i < result.size(); i++) {
			// Check all keys for a match
			for (int j = labels.size() - 1; j >= 0; j--) {
				String currLabel = labels.get(j);
				if (result.get(i).indexOf(currLabel) != -1
						&& result.get(i).substring((result.get(i).indexOf(currLabel))).contentEquals(currLabel)) {
					// Match!
					String s = result.get(i);

					s = s.substring(0, s.indexOf(currLabel)) + labelLookup.get(currLabel);
					// Replace value in the array
					result.set(i, s);
				}
			}
		}

		// Make sure that all JMP/JC/JZ instructions that used labels were replaced
		for (int i = 0; i < result.size(); i++) {
			// Grab the current instruction
			String curr = result.get(i);

			// Check if its a branch
			if (curr.indexOf("JMP") != -1) {
				if (curr.substring(3).matches("[a-zA-Z]+")) {
					return "<html>[Assembler Failed] Cannot compile the following instruction:." + curr + "</html>";
				}
			} else if (curr.indexOf("JC") != -1) {
				if (curr.substring(2).matches("[a-zA-Z]+")) {
					return "<html>[Assembler Failed] Cannot compile the following instruction:." + curr + "</html>";
				}
			} else if (curr.indexOf("JZ") != -1) {
				if (curr.substring(2).matches("[a-zA-Z]+")) {
					return "<html>[Assembler Failed] Cannot compile the following instruction:." + curr + "</html>";
				}
			}
		}

		String[] rArr = new String[16];

		// Add variables to memory
		for (String v : varAddressLookup.keySet()) {
			rArr[varAddressLookup.get(v)] = varValLookup.get(v);
		}

		// convert each instruction to machine code
		for (int i = 0; i < result.size(); i++) {
			// Grab the current instruction
			String curr = result.get(i);

			// Add binary value
			InstructionTypes iVal = parseInstruction(curr);
			switch (iVal) {
			case NOP:
				rArr[i] = "00000000";
				break;
			case LDA:
				rArr[i] = "0001";
				if (curr.length() == 3) {
					return "<html>[Assembler Failed] LDA Missing Arguement</html>";
				}
				if (argToBinary(curr.substring(3), varAddressLookup) == null) {
					System.out.println("A");
					return "<html>[Assembler Failed] Missing variable.</html>";
				}
				rArr[i] += argToBinary(curr.substring(3), varAddressLookup);
				break;
			case ADD:
				rArr[i] = "0010";
				if (curr.length() == 3) {
					return "<html>[Assembler Failed] ADD Missing Arguement</html>";
				}
				if (argToBinary(curr.substring(3), varAddressLookup) == null) {
					System.out.println("B");
					return "<html>[Assembler Failed] Missing variable.</html>";
				}
				rArr[i] += argToBinary(curr.substring(3), varAddressLookup);
				break;
			case SUB:
				rArr[i] = "0011";
				if (curr.length() == 3) {
					return "<html>[Assembler Failed] SUB Missing Arguement</html>";
				}
				if (argToBinary(curr.substring(3), varAddressLookup) == null) {
					System.out.println("C");
					return "<html>[Assembler Failed] Missing variable.</html>";
				}
				rArr[i] += argToBinary(curr.substring(3), varAddressLookup);
				break;
			case STA:
				rArr[i] = "0100";
				if (curr.length() == 3) {
					return "<html>[Assembler Failed] STA Missing Arguement</html>";
				}
				if (argToBinary(curr.substring(3), varAddressLookup) == null) {
					System.out.println("D");
					return "<html>[Assembler Failed] Missing variable.</html>";
				}
				rArr[i] += argToBinary(curr.substring(3), varAddressLookup);
				break;
			case LDI:
				rArr[i] = "0101";
				if (curr.length() != 7) {
					return "<html>[Assembler Failed] LDI Invalid Arguement</html>";
				}
				if (argToBinary(curr.substring(3), varAddressLookup) == null) {
					System.out.println("D");
					return "<html>[Assembler Failed] Missing variable.</html>";
				}
				rArr[i] += argToBinary(curr.substring(3), varAddressLookup);
				break;
			case JMP:
				rArr[i] = "0110";
				break;
			case JC:
				rArr[i] = "0111";
				break;
			case JZ:
				rArr[i] = "1000";
				break;
			case OUT:
				rArr[i] = "11100000";
				break;
			case HLT:
				rArr[i] = "11110000";
				break;
			default:
				rArr[i] = "N/A";
			}

		}

		rVal = "<html>";
		for (int i = 0; i < rArr.length; i++) {
			rVal += i + ": " + (rArr[i] == null ? "XXXXXXXX" : rArr[i]) + "<br>";
		}
		return rVal + "</html>";
	}

	private InstructionTypes parseInstruction(String curr) {
		if (curr.indexOf("ADD") != -1) {
			return InstructionTypes.ADD;
		} else if (curr.indexOf("SUB") != -1) {
			return InstructionTypes.SUB;
		} else if (curr.indexOf("NOP") != -1) {
			return InstructionTypes.NOP;
		} else if (curr.indexOf("LDA") != -1) {
			return InstructionTypes.LDA;
		} else if (curr.indexOf("STA") != -1) {
			return InstructionTypes.STA;
		} else if (curr.indexOf("LDI") != -1) {
			return InstructionTypes.LDI;
		} else if (curr.indexOf("JMP") != -1) {
			return InstructionTypes.JMP;
		} else if (curr.indexOf("JZ") != -1) {
			return InstructionTypes.JZ;
		} else if (curr.indexOf("JC") != -1) {
			return InstructionTypes.JC;
		} else if (curr.indexOf("OUT") != -1) {
			return InstructionTypes.OUT;
		} else if (curr.indexOf("HLT") != -1) {
			return InstructionTypes.HLT;
		}
		return InstructionTypes.INVALID;
	}

	private boolean isValidBinaryString(String value) {
		for (int i = 0; i < value.length(); i++) {
			int tempB = value.charAt(i);
			if (tempB != '0' && tempB != '1') {
				return false;
			}
		}
		// no failures, so
		return true;
	}

	private String argToBinary(String arg, Map<String, Integer> varAddressLookup) {
		if (arg == null || arg.length() == 0) {
			return "";
		}

		// Check for binary literal
		if (isValidBinaryString(arg)) {
			if (arg.length() == 0) {
				return "0000";
			} else if (arg.length() == 1) {
				return "000" + arg;
			} else if (arg.length() == 2) {
				return "00" + arg;
			} else if (arg.length() == 3) {
				return "0" + arg;
			} else {
				return arg;
			}
		}

		// Meaning we have a variable
		Integer val = varAddressLookup.get(arg);
		if (val == null) {
			return null;
		}

		String address = Integer.toBinaryString(val);
		if (address.length() == 0) {
			return "0000";
		} else if (address.length() == 1) {
			return "000" + address;
		} else if (address.length() == 2) {
			return "00" + address;
		} else if (address.length() == 3) {
			return "0" + address;
		} else {
			return address;
		}

		// See if we have variable for it
	}
}

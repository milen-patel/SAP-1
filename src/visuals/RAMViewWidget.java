package visuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import sap.EventLog;
import sap.Runner;

@SuppressWarnings("serial")
public class RAMViewWidget extends JPanel implements interfaces.RAMObserver, ActionListener {
	
	// Widget components
	private sap.SAPModel model;
	private View view;
	private GridBagConstraints c;
	private JButton[][] butts;
	private JButton clearMemButton;
	private JButton showOpcodeButton;
	private JButton countingProgramButton;
	private JButton analyzeProgramButton;
	private JButton assemblerButton;
	private JButton highlightMarButton;
	private JPanel parentPanel;
	private byte marVal;
	private boolean shouldHighlightMAR;

	// Constants
	private static final Dimension buttonSize = new Dimension(20, 20);
	private static final Dimension WIDGET_SIZE = new Dimension(220, 550);
	private static final Color COLOR_ON = new Color(246, 203, 225);
	private static final Color COLOR_OFF = new Color(246, 213, 203);
	private static final Color COLOR_MAR = Color.gray;
	private static final Border BOTTOM_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
	private static final Border RIGHT_BORDER = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
	private static final Border BOTTOM_RIGHT_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK);
	private static final Border TOP_LEFT_RIGHT_BORDER = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK);
	private static final Border LEFT_RIGHT_BORDER = BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK);
	private static final Border FULL_BORDER = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK);
	private static final String MAR_ON_LABEL = "[ON] Show MAR on RAM";
	private static final String MAR_OFF_LABEL = "[OFF] Show MAR on RAM";
	
	public RAMViewWidget(sap.SAPModel model, JPanel parentPanel) {
		// Store what we need to maintain
		this.marVal = 0;
		this.parentPanel = parentPanel;
		this.model = model;
		this.view = (View) parentPanel;
		this.shouldHighlightMAR = true;

		// Add ourselves as a RAMObserver
		this.model.getRAM().addRAMObserver(this);

		// Create the array of buttons representing each bit of memory
		butts = new JButton[16][8];
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 8; j++) {
				this.butts[i][j] = new JButton("" + lookupRAM(i, 7 - j));
				this.butts[i][j].setPreferredSize(buttonSize);
				this.butts[i][j].setActionCommand(i + "," + j);
				this.butts[i][j].addActionListener(this);
				this.butts[i][j].setBorder(null);
				this.butts[i][j].setBackground(butts[i][j].getText().equals("1") ? COLOR_ON : COLOR_OFF);
				this.butts[i][j].setOpaque(true);
			}
		}

		// Set our preferred size
		this.setPreferredSize(WIDGET_SIZE);
		this.setBackground(View.VIEW_BACKGROUND_COLOR);

		// Set the Layout
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		// Add the reset RAM button
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 9;
		this.clearMemButton = new JButton("Clear All Memory");
		this.clearMemButton.setActionCommand("clearmem");
		this.clearMemButton.addActionListener(this);
		this.add(this.clearMemButton, c);

		// Add the show OPCodes button
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 9;
		this.showOpcodeButton = new JButton("Show Operation Codes");
		this.showOpcodeButton.setActionCommand("showopcodes");
		this.showOpcodeButton.addActionListener(this);
		this.add(this.showOpcodeButton, c);

		// Add the sample program loader button
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 9;
		this.countingProgramButton = new JButton("Load Counting Program");
		this.countingProgramButton.setActionCommand("loadcountprogram");
		this.countingProgramButton.addActionListener(this);
		this.add(this.countingProgramButton, c);

		// Add the analyze program button
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 9;
		this.analyzeProgramButton = new JButton("Analyze Program");
		this.analyzeProgramButton.setActionCommand("analyzeProgram");
		this.analyzeProgramButton.addActionListener(this);
		this.add(this.analyzeProgramButton, c);

		// Add the assembler button
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 9;
		this.assemblerButton = new JButton("Assembler");
		this.assemblerButton.setActionCommand("openAssembler");
		this.assemblerButton.addActionListener(this);
		this.add(this.assemblerButton, c);

		// Add button to toggle MAR visualization
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 9;
		this.highlightMarButton = new JButton(this.shouldHighlightMAR ? MAR_ON_LABEL : MAR_OFF_LABEL);
		this.highlightMarButton.setActionCommand("toggleMAR");
		this.highlightMarButton.addActionListener(this);
		this.add(this.highlightMarButton, c);

		// Add label above RAM content
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 9;
		c.gridy = 6;
		JLabel tmp = new JLabel("              Memory Content");
		tmp.setBorder(FULL_BORDER);
		this.add(tmp, c);

		// Display the memory content
		c.gridx = 4;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		for (int i = 1; i <= 16; i++) {
			c.gridx = 1;
			c.gridy = i + 5 + 1;
			switch (i - 1) {
			case 0:
				JLabel tmp1 = new JLabel(" [0000]");
				tmp1.setBorder(LEFT_RIGHT_BORDER);
				this.add(tmp1, c);
				break;
			case 1:
				JLabel tmp2 = new JLabel(" [0001]");
				tmp2.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp2, c);
				break;
			case 2:
				JLabel tmp3 = new JLabel(" [0010]");
				tmp3.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp3, c);
				break;
			case 3:
				JLabel tmp4 = new JLabel(" [0011]");
				tmp4.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp4, c);
				break;
			case 4:
				JLabel tmp5 = new JLabel(" [0100]");
				tmp5.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp5, c);
				break;
			case 5:
				JLabel tmp6 = new JLabel(" [0101]");
				tmp6.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp6, c);
				break;
			case 6:
				JLabel tmp7 = new JLabel(" [0110]");
				tmp7.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp7, c);
				break;
			case 7:
				JLabel tmp8 = new JLabel(" [0111]");
				tmp8.setBorder(TOP_LEFT_RIGHT_BORDER);
				this.add(tmp8, c);
				break;
			default:
				if (Integer.toBinaryString(i - 1).contentEquals("1111")) {
					JLabel tmp9 = new JLabel(" [" + Integer.toBinaryString(i - 1) + "] ");
					tmp9.setBorder(FULL_BORDER);
					this.add(tmp9, c);
				} else {
					JLabel tmp9 = new JLabel(" [" + Integer.toBinaryString(i - 1) + "] ");
					tmp9.setBorder(TOP_LEFT_RIGHT_BORDER);
					this.add(tmp9, c);
				}

			}
			for (int j = 2; j < 10; j++) {
				c.gridx = j;
				this.add(butts[c.gridy - 1 - 5 - 1][j - 2], c);
			}
		}

		// Add the right borders to the RAM visualization
		for (int i = 0; i < this.butts.length; i++) {
			this.butts[i][this.butts[0].length - 1].setBorder(RIGHT_BORDER);
		}

		// Add the bottom border to the RAM visualization
		for (int i = 0; i < this.butts[0].length; i++) {

			// Bottom right piece gets special border
			if (i == 7) {
				this.butts[this.butts.length - 1][i]
						.setBorder(BOTTOM_RIGHT_BORDER);
			} else {
				this.butts[this.butts.length - 1][i]
						.setBorder(BOTTOM_BORDER);
			}

		}

		this.marChange(this.marVal);
		repaint();
	}

	// Helper function for accessing individual bits in memory; Address: [0, 15]
	// bitPos: [0, 7]
	private int lookupRAM(int address, int bitPos) {
		int val = 0b11111111 & this.model.getRAM().getRAM()[address];
		return (val >> bitPos) & 0b1;
	}

	@Override
	// If a value in memory is changed, repaint it
	public void valChanged(int address) {

		// Iterate over all bits in the current memory position
		for (int i = 0; i <= 7; i++) {
			this.butts[address][i].setText("" + lookupRAM(address, 7 - i));

			// Check if it is the MAR value, in which case special coloring is needed
			if (this.shouldHighlightMAR && address == this.marVal) {
				this.butts[address][i].setBackground(COLOR_MAR);
			} else {
				this.butts[address][i].setBackground(butts[address][i].getText().equals("1") ? COLOR_ON : COLOR_OFF);
			}

			// If we are on the rightmost position, keep the border
			if (i == 7) {
				this.butts[address][i].setBorder(RIGHT_BORDER);
			} else {
				this.butts[address][i].setBorder(null);
			}
			
			// If we are on the bottom row, keep the border
			if (address==15) {
				if (i == 7) {
					this.butts[address][i].setBorder(BOTTOM_RIGHT_BORDER);

				} else {
					this.butts[address][i].setBorder(BOTTOM_BORDER);
				}
			}
		}
	}

	public void marChange(byte newMarVal) {
		// If we aren't in highlighting mode, exit
		if (!this.shouldHighlightMAR) {
			valChanged(newMarVal);
			return;
		}
		
		// Grab the old MAR Value
		int oldVal = this.marVal;

		// Paint the new value with the correct color
		this.marVal = newMarVal;
		valChanged(this.marVal);

		// Remove special coloring from the old MAR value
		valChanged(oldVal);
	}

	@Override
	// Responds to button click indicating a bit change in memory
	public void actionPerformed(ActionEvent e) {
		// If the user wants to toggle MAR highlighting on the RAM portion of the widget
		if (e.getActionCommand().contentEquals("toggleMAR")) {
			// Toggle status value
			this.shouldHighlightMAR = !this.shouldHighlightMAR;
			
			// Update button label
			this.highlightMarButton.setText(this.shouldHighlightMAR ? MAR_ON_LABEL : MAR_OFF_LABEL);

			// Update visualization
			this.marChange(this.marVal);
			
			return;
		}
		
		// If the program is automatically playing , then stop it first
		if (this.view.getIsAutoRunning()) {
			ActionEvent x = new ActionEvent("", 5, "autoplay");
			this.view.actionPerformed(x);

			// Sleep so event log doesn't get clustered
			try {
				Thread.sleep(25);
			} catch (InterruptedException e1) {
				EventLog.getEventLog().addEntry("Failed to sleep for 100 ms");
			}
		}

		// If the user wants to open the assembler
		if (e.getActionCommand().contentEquals("openAssembler")) {
			// Create instance of the assembler
			Assembler view = new Assembler(this.model, this.parentPanel);

			// Set the view to the assembler (of the current window)
			Runner.main_frame.setContentPane(view);
			Runner.main_frame.pack();
			Runner.main_frame.setVisible(true);

			return;
		}
		
		// If the user clicks the analyze program button
		if (e.getActionCommand().contentEquals("analyzeProgram")) {
			EventLog.getEventLog().addEntry("=============");
			EventLog.getEventLog().addEntry("[ADDRESS]\t[INSTR]\t[DEC]");
			for (byte i = 0; i < 16; i++) {
				this.model.analyzeInstruction(i);
			}
			EventLog.getEventLog().addEntry("=============");
			return;
		}

		// If the user clicks the clear memory button
		if (e.getActionCommand().contentEquals("clearmem")) {
			// Get the contents of memory
			byte[] arr = this.model.getRAM().getRAM();

			for (int i = 0; i < 16; i++) {
				// Set the value to 0
				arr[i] = 0;
			}

			// Force the display to repaint twice, to handle visual delay
			for (int i = 0; i < 16; i++) {
				this.valChanged(i);
			}
			for (int i = 0; i < 16; i++) {
				this.valChanged(i);
			}
			return;
		}

		// If the user clicks the show opcodes button
		if (e.getActionCommand().contentEquals("showopcodes")) {
			EventLog.getEventLog().addEntry("=============");
			EventLog.getEventLog().addEntry("NOP\t0000");
			EventLog.getEventLog().addEntry("LDA\t0001");
			EventLog.getEventLog().addEntry("ADD\t0010");
			EventLog.getEventLog().addEntry("SUB\t0011");
			EventLog.getEventLog().addEntry("STA\t0100");
			EventLog.getEventLog().addEntry("LDI\t0101");
			EventLog.getEventLog().addEntry("JMP\t0110");
			EventLog.getEventLog().addEntry("JC\t0111");
			EventLog.getEventLog().addEntry("JZ\t1000");
			EventLog.getEventLog().addEntry("OUT\t1110");
			EventLog.getEventLog().addEntry("HLT\t1111");
			EventLog.getEventLog().addEntry("=============");
			return;
		}

		// If the user clicks the load demo program button
		if (e.getActionCommand().contentEquals("loadcountprogram")) {
			// Grab internal representation of RAM
			byte[] arr = this.model.getRAM().getRAM();

			// First clear the memory content
			for (int i = 0; i < 16; i++) {
				// Set the value to 0
				arr[i] = 0;
				// Tell the display to repaint
				this.valChanged(i);

			}

			// Add updated memory content for this program
			arr[0] = 0b01010000;
			this.valChanged(0);
			arr[1] = 0b00101110;
			this.valChanged(1);
			arr[2] = (byte) 0b11100000;
			this.valChanged(2);
			arr[3] = 0b01001010;
			this.valChanged(3);
			arr[4] = 0b01100001;
			this.valChanged(4);
			arr[14] = 0b00000001;
			this.valChanged(14);

			return;
		}

		// Otherwise, the user must have requested a bit change somewhere in memory
		// Parse the memory address
		byte address = Byte.parseByte(e.getActionCommand().substring(0, e.getActionCommand().indexOf(",")));
		// Parse the bit position change
		byte bitPos = Byte.parseByte(e.getActionCommand().substring(e.getActionCommand().indexOf(",") + 1));
		bitPos = (byte) (7 - bitPos);

		// Get the current value of the bit add the changed position
		int currVal = lookupRAM(address, bitPos);
		// Get the current value of the memory at the specified address
		byte memVal = this.model.getRAM().getRAM()[address];

		// Determine if we need to subtract or add
		byte newVal;
		if (currVal == 1) {
			// Subtract
			newVal = (byte) (memVal - Math.pow(2, bitPos));
		} else {
			// Add
			newVal = (byte) (memVal + Math.pow(2, bitPos));
		}
		this.model.getRAM().manualValueChange(address, newVal);

		// Inform the log
		sap.EventLog.getEventLog().addEntry("Memory address " + address + " changed to " + newVal);
	}
}
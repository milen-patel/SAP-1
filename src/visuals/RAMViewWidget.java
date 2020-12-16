package visuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sap.EventLog;

public class RAMViewWidget extends JPanel implements sap.RAMObserver, ActionListener {
	private sap.SAPModel model;
	private GridBagConstraints c;
	private JButton[][] butts;
	private JButton clearMemButton;
	private JButton showOpcodeButton;
	private JButton countingProgramButton;

	private static Dimension buttonSize = new Dimension(20, 20);
	private static Color COLOR_ON = new Color(124, 248, 42);
	private static Color COLOR_OFF = new Color(34, 82, 20);

	/*
	 * Address: [0, 15] bitPos: [0, 7]
	 */
	private int lookupRAM(int address, int bitPos) {
		int val = 0b11111111 & this.model.getRAM().getRAM()[address];
		return (val >> bitPos) & 0b1;
	}

	public RAMViewWidget(sap.SAPModel model) {
		this.model = model;
		// Add ourselves as a RAMObserver
		this.model.getRAM().addRAMObserver(this);

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

		/* Set our preferred size */
		this.setPreferredSize(new Dimension(220, 550));
		this.setBackground(Color.LIGHT_GRAY);

		/* Set the Layout */
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 9;
		this.clearMemButton = new JButton("Clear all Memory");
		this.clearMemButton.setActionCommand("clearmem");
		this.clearMemButton.addActionListener(this);
		this.add(this.clearMemButton, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 9;
		this.showOpcodeButton = new JButton("Show OPCodes");
		this.showOpcodeButton.setActionCommand("showopcodes");
		this.showOpcodeButton.addActionListener(this);
		this.add(this.showOpcodeButton, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 9;
		this.countingProgramButton = new JButton("Load Counting Program");
		this.countingProgramButton.setActionCommand("loadcountprogram");
		this.countingProgramButton.addActionListener(this);
		this.add(this.countingProgramButton, c);

		// Reset constraint parameters
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;

		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 7;
		c.gridy = 5;
		this.add(new JLabel("Memory Content"), c);
		c.gridx = 4;
		c.gridwidth = 1;
		for (int i = 1; i <= 16; i++) {
			c.gridx = 1;
			c.gridy = i + 5;
			switch (i - 1) {
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
				this.add(new JLabel("[" + Integer.toBinaryString(i - 1) + "]: "), c);

			}
			for (int j = 2; j < 10; j++) {
				c.gridx = j;
				this.add(butts[c.gridy - 1 - 5][j - 2], c);
			}
		}

		repaint();
	}

	@Override
	public void valChanged(int address) {
		for (int i = 0; i <= 7; i++) {
			this.butts[address][i].setText("" + lookupRAM(address, 7 - i));
			this.butts[address][i].setBackground(butts[address][i].getText().equals("1") ? COLOR_ON : COLOR_OFF);
			this.butts[address][i].setBorder(null);
		}
		EventLog.getEventLog().addEntry("Repainted RAM address " + address);
	}

	// Responds to button click indicating a bit change in memory
	@Override
	public void actionPerformed(ActionEvent e) {
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
			arr[0] = 0b01010001;
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

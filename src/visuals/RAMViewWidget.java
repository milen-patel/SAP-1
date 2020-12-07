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

public class RAMViewWidget extends JPanel implements sap.RAMObserver, ActionListener {
	private sap.SAPModel model;
	private GridBagConstraints c;
	private JButton[][] butts;
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
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 7;
		c.gridy = 0;
		this.add(new JLabel("Memory Content"), c);
		c.gridx = 4;
		c.gridwidth = 1;
		for (int i = 1; i <= 16; i++) {
			c.gridx = 1;
			c.gridy = i;
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
				this.add(butts[c.gridy - 1][j - 2], c);
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
	}

	// Responds to button click indicating a bit change in memory
	@Override
	public void actionPerformed(ActionEvent e) {
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

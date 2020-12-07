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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.*;

import sap.SAPModel;
import sap.SAPModel.RegisterType;
import sap.SAPObserver;

public class SAPViewWidget extends JPanel implements SAPObserver {
	private sap.SAPModel model;
	private GridBagConstraints c;

	private JLabel label_regA;
	private JLabel label_regB;
	private JLabel label_ALU;
	private JLabel label_IR;
	private JLabel label_out;
	private JLabel label_PC;
	private JLabel label_MAR;
	private JLabel label_controls;
	private JLabel label_stepCount;
	private JLabel label_bus;
	private JLabel stepCt;
	private JLabel label_flags;

	private JButton[] aBits;
	private JButton[] bBits;
	private JButton[] aluBits;
	private JButton[] irBits;
	private JButton[] outBits;
	private JButton[] pcBits;
	private JButton[] marBits;
	private JButton[] controlBits;
	private JButton[] busBits;
	private JButton cFlag;
	private JButton zFlag;

	private static Dimension buttonSize = new Dimension(20, 20);
	private static Color defaultButtonBackground = new Color(238, 238, 238);
	private static Color selectedButtonBackground = new Color(55, 55, 55);

	public SAPViewWidget(sap.SAPModel model) {
		// Encapsulate the model
		this.model = model;

		// Add ourselves as a model observer
		this.model.addObserver(this);

		// Set our preferred size
		this.setPreferredSize(new Dimension(525, 500));
		this.setBackground(Color.CYAN);

		// Set the Layout
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 10, 0);

		// Add header
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		this.label_bus = new JLabel("BUS");
		this.add(this.label_bus, c);

		c.gridy = 1;
		// this.add(new JLabel("========"), c);

		// Add component labels
		c.gridwidth = 1;
		c.gridy = 2;
		c.gridx = 0;
		this.label_regA = new JLabel("Register A");
		this.add(label_regA, c);

		c.gridy = 4;
		c.gridx = 0;
		this.label_regB = new JLabel("Register B");
		this.add(label_regB, c);

		c.gridy = 6;
		c.gridx = 0;
		this.label_ALU = new JLabel("ALU");
		this.add(label_ALU, c);

		c.gridy = 7;
		this.add(new JLabel("========"), c);

		c.gridy = 8;
		c.gridx = 0;
		this.label_PC = new JLabel("Instruction Register");
		this.add(label_PC, c);

		c.gridy = 10;
		c.gridx = 0;
		this.label_IR = new JLabel("Program Counter");
		this.add(label_IR, c);

		c.gridy = 11;
		this.add(new JLabel("========"), c);

		c.gridy = 12;
		c.gridx = 0;
		this.label_out = new JLabel("Output");
		this.add(label_out, c);

		c.gridy = 14;
		c.gridx = 0;
		this.label_MAR = new JLabel("MAR");
		this.add(label_MAR, c);

		c.gridy = 16;
		c.gridx = 0;
		this.label_stepCount = new JLabel("Step Count");
		this.add(this.label_stepCount, c);

		c.gridy = 16;
		c.gridx = 1;
		this.stepCt = new JLabel("" + this.model.getStepCount());
		this.add(this.stepCt, c);

		c.gridx = 0;
		c.gridwidth = 12;
		c.gridy = 17;
		this.add(new JLabel("=================================================="), c);

		c.gridwidth = 1;
		c.gridy = 18;
		c.gridx = 0;
		this.label_controls = new JLabel("Control Lines");
		this.add(this.label_controls, c);

		c.gridy = 20;
		c.gridx = 0;
		this.label_flags = new JLabel("Flags");
		this.add(this.label_flags, c);

		// Prepare to display register contents
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);

		// Display Flags
		this.controlBits = new JButton[16];
		this.controlBits[0] = new JButton("HLT");
		this.controlBits[1] = new JButton("MI");
		this.controlBits[2] = new JButton("RI");
		this.controlBits[3] = new JButton("RO");
		this.controlBits[4] = new JButton("IO");
		this.controlBits[5] = new JButton("II");
		this.controlBits[6] = new JButton("AI");
		this.controlBits[7] = new JButton("AO");
		this.controlBits[8] = new JButton("ΣO");
		this.controlBits[9] = new JButton("SU");
		this.controlBits[10] = new JButton("BI");
		this.controlBits[11] = new JButton("OI");
		this.controlBits[12] = new JButton("CE");
		this.controlBits[13] = new JButton("CO");
		this.controlBits[14] = new JButton("J");
		this.controlBits[15] = new JButton("FI");

		for (JButton b : controlBits) {
			b.setPreferredSize(buttonSize);
		}

		c.gridy = 18;
		for (int i = 0; i < 16; i++) {
			controlBits[i].setBorder(null);
			controlBits[i].setBackground(defaultButtonBackground);
			controlBits[i].setOpaque(true);
			if (i == 8) {
				c.gridy++;
				c.gridx = 1;
			} else if (i >= 8) {
				c.gridx = i - 7;
			} else {
				c.gridx = i + 1;
			}
			this.add(this.controlBits[i], c);
		}

		// Show BUS Value
		c.gridy = 0;
		c.gridx = 1;
		busBits = new JButton[8];
		for (int i = 0; i <= 7; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.BUS, 7 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			busBits[i] = b;
		}

		// Show Register A Values
		c.gridy = 2;
		c.gridx = 1;
		aBits = new JButton[8];
		for (int i = 0; i <= 7; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.A, 7 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			aBits[i] = b;
		}

		// Show Register B Values
		c.gridy = 4;
		c.gridx = 1;
		bBits = new JButton[8];
		for (int i = 0; i <= 7; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.B, 7 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			bBits[i] = b;
		}

		// Show ALU Value
		c.gridy = 6;
		c.gridx = 1;
		aluBits = new JButton[8];
		for (int i = 0; i <= 7; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.ALU, 7 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			aluBits[i] = b;
		}

		// Show IR Value
		c.gridy = 8;
		c.gridx = 1;
		irBits = new JButton[8];
		for (int i = 0; i <= 7; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.IR, 7 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			irBits[i] = b;
		}

		// Show out register
		c.gridy = 12;
		c.gridx = 1;
		outBits = new JButton[8];
		for (int i = 0; i <= 7; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.OUT, 7 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			outBits[i] = b;
		}

		// Show PC
		c.gridy = 10;
		c.gridx = 1;
		pcBits = new JButton[4];
		for (int i = 0; i <= 3; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.PC, 3 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			pcBits[i] = b;
		}

		// Show MAR
		c.gridy = 14;
		c.gridx = 1;
		marBits = new JButton[4];
		for (int i = 0; i <= 3; i++) {
			c.gridx = i + 1;
			JButton b = new JButton(decodeRegister(RegisterType.MAR, 3 - i));
			b.setPreferredSize(buttonSize);
			this.add(b, c);
			marBits[i] = b;
		}

		// Show Flags
		c.gridy = 20;
		c.gridx = 1;
		this.cFlag = new JButton(this.model.getFlags().getCF() ? "C" : "-C");
		this.cFlag.setPreferredSize(buttonSize);
		this.add(this.cFlag, c);
		c.gridx = 2;
		this.zFlag = new JButton(this.model.getFlags().getZF() ? "Z" : "-Z");
		this.zFlag.setPreferredSize(buttonSize);
		this.add(this.zFlag, c);
		// TODO del
		this.controlLineChange();
		repaint();
	}

	private String decodeRegister(RegisterType t, int bitPos) {
		byte val = 0;
		if (t == RegisterType.A) {
			val = this.model.getA().getVal();
		} else if (t == RegisterType.B) {
			val = this.model.getB().getVal();
		} else if (t == RegisterType.ALU) {
			val = this.model.getALU().ALUOut(this.model.getControlLines()[9]);
		} else if (t == RegisterType.IR) {
			val = this.model.getIR().getVal();
		} else if (t == RegisterType.OUT) {
			val = this.model.getOut().getVal();
		} else if (t == RegisterType.PC) {
			val = this.model.getPC().getVal();
		} else if (t == RegisterType.MAR) {
			val = this.model.getMAR().getVal();
		} else if (t == RegisterType.BUS) {
			val = this.model.getBus().getVal();
		}
		return "" + (0b1 & (val >> bitPos));

	}

	private void updateALU() {
		for (int i = 0; i <= 7; i++) {
			aluBits[i].setText(decodeRegister(RegisterType.ALU, 7 - i));
		}
	}

	@Override
	public void regAChange(byte newVal) {
		for (int i = 0; i <= 7; i++) {
			aBits[i].setText(decodeRegister(RegisterType.A, 7 - i));
		}
		updateALU();
	}

	@Override
	public void regBChange(byte newVal) {
		for (int i = 0; i <= 7; i++) {
			bBits[i].setText(decodeRegister(RegisterType.B, 7 - i));
		}
		updateALU();
	}

	@Override
	public void pcChange(byte newVal) {
		for (int i = 0; i <= 3; i++) {
			pcBits[i].setText(decodeRegister(RegisterType.PC, 3 - i));
		}
	}

	@Override
	public void marChange(byte newVal) {
		for (int i = 0; i <= 3; i++) {
			marBits[i].setText(decodeRegister(RegisterType.MAR, 3 - i));
		}
	}

	@Override
	public void outChange(byte newVal) {
		for (int i = 0; i <= 7; i++) {
			outBits[i].setText(decodeRegister(RegisterType.OUT, 7 - i));
		}
	}

	@Override
	public void irChange(byte newVal) {
		for (int i = 0; i <= 7; i++) {
			irBits[i].setText(decodeRegister(RegisterType.IR, 7 - i));
		}
	}

	@Override
	public void stepCycleChange(byte newVal) {
		this.stepCt.setText("" + newVal);
	}

	@Override
	public void flagChange() {
		// TODO Auto-generated method stub

		this.zFlag.setText(this.model.getFlags().getZF() ? "Z" : "-Z");
		this.cFlag.setText(this.model.getFlags().getCF() ? "C" : "-C");
	}

	@Override
	public void busChange(byte newVal) {
		for (int i = 0; i <= 7; i++) {
			busBits[i].setText(decodeRegister(RegisterType.BUS, 7 - i));
		}
	}

	@Override
	public void controlLineChange() {
		boolean[] newLines = this.model.getControlLines();
		for (int i = 0; i < newLines.length; i++) {
			if (newLines[i]) {
				this.controlBits[i].setBackground(selectedButtonBackground);
			} else {
				this.controlBits[i].setBackground(defaultButtonBackground);
			}
		}
	}

}

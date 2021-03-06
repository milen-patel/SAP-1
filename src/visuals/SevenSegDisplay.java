package visuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SevenSegDisplay extends JPanel implements ActionListener {

	private GridBagConstraints c;

	private JLabel aLabel;
	private JLabel bLabel;
	private JLabel cLabel;
	private JLabel dLabel;
	private JLabel[] arr;
	private boolean isSigned;
	private byte byteVal;

	private JButton modeToggle;

	private static ImageIcon iconZero;
	private static ImageIcon iconOne;
	private static ImageIcon iconTwo;
	private static ImageIcon iconThree;
	private static ImageIcon iconFour;
	private static ImageIcon iconFive;
	private static ImageIcon iconSix;
	private static ImageIcon iconSeven;
	private static ImageIcon iconEight;
	private static ImageIcon iconNine;
	private static ImageIcon iconNegative;

	private static final String TWOS_COMP_LABEL = "[Mode] 2's Complement";
	private static final String UNSIGNED_LABEL = "[Mode] Unsigned";

	public SevenSegDisplay(byte val) {
		// Load icons into memory
		try {
			BufferedImage picZero = ImageIO.read(getClass().getResource("/include/Zero.png"));
			iconZero = new ImageIcon(picZero.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picOne = ImageIO.read(getClass().getResource("/include/One.png"));
			iconOne = new ImageIcon(picOne.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picTwo = ImageIO.read(getClass().getResource("/include/Two.png"));
			iconTwo = new ImageIcon(picTwo.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picThree = ImageIO.read(getClass().getResource("/include/Three.png"));
			iconThree = new ImageIcon(picThree.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picFour = ImageIO.read(getClass().getResource("/include/Four.png"));
			iconFour = new ImageIcon(picFour.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picFive = ImageIO.read(getClass().getResource("/include/Five.png"));
			iconFive = new ImageIcon(picFive.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picSix = ImageIO.read(getClass().getResource("/include/Six.png"));
			iconSix = new ImageIcon(picSix.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picSeven = ImageIO.read(getClass().getResource("/include/Seven.png"));
			iconSeven = new ImageIcon(picSeven.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picEight = ImageIO.read(getClass().getResource("/include/Eight.png"));
			iconEight = new ImageIcon(picEight.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picNine = ImageIO.read(getClass().getResource("/include/Nine.png"));
			iconNine = new ImageIcon(picNine.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));

			BufferedImage picNegative = ImageIO.read(getClass().getResource("/include/NegativeSign.png"));
			iconNegative = new ImageIcon(picNegative.getScaledInstance(204 / 4, 286 / 4, java.awt.Image.SCALE_SMOOTH));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setPreferredSize(new Dimension(260, 105));

		// Set the Layout
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		this.setBackground(View.VIEW_BACKGROUND_COLOR);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.isSigned = false;
		this.byteVal = 0;

		// Add the status/toggle button
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,0,0,0);

		this.modeToggle = new JButton(UNSIGNED_LABEL);
		this.modeToggle.setActionCommand("toggle");

		this.modeToggle.addActionListener(this);
		this.add(modeToggle, c);
		
		c.insets = new Insets(0,0,10,0);

		// Add the display bits
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		this.aLabel = new JLabel(iconZero);

		this.add(this.aLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		this.bLabel = new JLabel(iconZero);

		this.add(this.bLabel, c);

		c.gridx = 2;
		c.gridy = 1;
		this.cLabel = new JLabel(iconZero);
		this.add(this.cLabel, c);

		c.gridx = 3;
		c.gridy = 1;
		this.dLabel = new JLabel(iconZero);
		this.add(this.dLabel, c);

		arr = new JLabel[5];
		arr[1] = dLabel;
		arr[2] = cLabel;
		arr[3] = bLabel;
		arr[4] = aLabel;
		loadVal(val, this.isSigned);
	}

	private void setImage(int position, int val) {
		if (val == 1) {
			arr[position].setIcon(iconOne);
		} else if (val == 2) {
			arr[position].setIcon(iconTwo);

		} else if (val == 3) {
			arr[position].setIcon(iconThree);

		} else if (val == 4) {
			arr[position].setIcon(iconFour);

		} else if (val == 5) {
			arr[position].setIcon(iconFive);

		} else if (val == 6) {
			arr[position].setIcon(iconSix);

		} else if (val == 7) {
			arr[position].setIcon(iconSeven);

		} else if (val == 8) {
			arr[position].setIcon(iconEight);

		} else if (val == 9) {
			arr[position].setIcon(iconNine);

		} else if (val == 0) {
			arr[position].setIcon(iconZero);

		} else if (val == -1) {
			arr[position].setIcon(iconNegative);

		}
	}

	public void loadVal(byte val) {
		loadVal(val, this.isSigned);
		this.byteVal = val;
	}

	private void loadVal(byte val, boolean twosComplement) {
		if (!twosComplement) {
			int unsignedVal = 0b11111111 & val;

			// Compute ones position
			int onesPos = unsignedVal % 10;
			setImage(1, onesPos);

			// Compute tens position
			int tensPos = (unsignedVal % 100) / 10;
			setImage(2, tensPos);

			// Compute hundreds position
			int hundredsPos = (unsignedVal % 1000) / 100;
			setImage(3, hundredsPos);

			// Unsigned, so leave the last image as a 0
			setImage(4, 0);
			return;
		}

		// If our number is non-negative, treat it as a unsigned value
		if (val >= 0) {
			loadVal(val, false);
			return;
		}

		// Else, we have a negative number
		loadVal((byte) (128 - (0b01111111 & val)), false);
		setImage(4, -1);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().contentEquals("toggle")) {
			this.isSigned = !this.isSigned;
			this.loadVal(this.byteVal);
			
			// Change label
			this.modeToggle.setText(this.isSigned ? TWOS_COMP_LABEL : UNSIGNED_LABEL);
		}
	}
}

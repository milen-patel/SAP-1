package sap;

import interfaces.Register;

public class PC implements Register {
	// Uses a byte to hold the 4-bit Program Counter Value. Only the 4 least
	// significant bits of the byte will be used, but the PC itself will maintain 8
	// bits.
	private byte content;

	public PC() {
		this.content = 0;
	}

	@Override
	// Loads a value into the Program Counter
	public void loadVal(byte newVal) {
		this.content = newVal;
	}

	@Override
	// Returns the value in the PC
	public byte getVal() {
		return this.content;
	}

	// Increments the program counter
	public void counterEnable() {
		if (this.content == 15) {
			// If PC is already at 15, reset it since that represents 4-bit overflow
			this.content = 0;
		} else {
			this.content++;
		}
	}

	@Override
	// Resets the value of the PC
	public void clear() {
		this.content = 0;
	}
}

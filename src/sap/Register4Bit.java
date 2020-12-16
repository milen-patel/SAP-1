package sap;

import interfaces.Register;

public class Register4Bit implements Register {

	private byte content;
	
	public Register4Bit() {
		this.content = 0;
	}

	@Override
	// Loads 4 bit value into the register
	public void loadVal(byte newVal) {
		// Make sure input is 4 bits
		if (newVal > 0b1111) {
			throw new RuntimeException();
		}
		this.content = newVal;
	}

	@Override
	// Getter
	public byte getVal() {
		return this.content;
	}
	
	@Override
	// Resets register contents
	public void clear() {
		this.content = 0;
	}

}

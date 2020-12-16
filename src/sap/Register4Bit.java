package sap;

import interfaces.Register;

public class Register4Bit implements Register {

	private byte content;
	
	public Register4Bit() {
		this.content = 0;
	}

	@Override
	public void loadVal(byte newVal) {
		if (newVal > 0b1111) {
			throw new RuntimeException();
		}
		this.content = newVal;
	}

	@Override
	public byte getVal() {
		return this.content;
	}
	
	@Override
	public void clear() {
		this.content = 0;
	}

}

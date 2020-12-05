package sap;

public class Register8Bit implements Register {
	private byte content;
	
	public Register8Bit() {
		this.content = 0;
	}

	@Override
	public void loadVal(byte newVal) {
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

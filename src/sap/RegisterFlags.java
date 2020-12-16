package sap;

public class RegisterFlags {
	// Uses booleans to store the contents of the register
	private boolean zeroFlag;
	private boolean carryFlag;

	public RegisterFlags() {
		this.zeroFlag = false;
		this.carryFlag = false;
	}

	public void clear() {
		this.zeroFlag = false;
		this.carryFlag = false;
	}

	public void FlagsIn(boolean zF, boolean cF) {
		this.zeroFlag = zF;
		this.carryFlag = cF;
	}

	public boolean getZF() {
		return this.zeroFlag;
	}

	public boolean getCF() {
		return this.carryFlag;
	}
}

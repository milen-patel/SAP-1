package sap;

public class RegisterFlags {
	boolean zeroFlag;
	boolean carryFlag;
	
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
}

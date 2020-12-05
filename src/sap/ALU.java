package sap;

public class ALU {
	Register regA;
	Register regB;
	RegisterFlags regFlags;

	public ALU(Register A, Register B) {
		this.regA = A;
		this.regB = B;
		this.regFlags = new RegisterFlags();
	}

	public void flagsIn(boolean sub) {
		int result;
		if (sub) {
			result = this.regA.getVal() - this.regB.getVal();
		} else {
			result = this.regA.getVal() + this.regB.getVal();
		}
		boolean zF = (result == 0);
		boolean cF = (result > 0b10000000);
		regFlags.FlagsIn(zF, cF);
	}

	public byte ALUOut(boolean sub) {
		if (sub) {
			return (byte) (this.regA.getVal() - this.regB.getVal());
		} else {
			return (byte) (this.regA.getVal() + this.regB.getVal());
		}
	}
	
	public boolean getZeroFlag() {
		return this.regFlags.zeroFlag;
	}
	
	public boolean getCarryFlag() {
		return this.regFlags.carryFlag;
	}

}

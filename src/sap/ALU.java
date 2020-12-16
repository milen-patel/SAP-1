package sap;

import interfaces.Register;

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
		/*
		 * int result; if (sub) { //result = this.regA.getVal() - this.regB.getVal();
		 * result = Byte.toUnsignedInt(this.regA.getVal()) -
		 * Byte.toUnsignedInt(this.regB.getVal());
		 * 
		 * } else { //result = this.regA.getVal() + this.regB.getVal(); result =
		 * Byte.toUnsignedInt(this.regA.getVal()) -
		 * Byte.toUnsignedInt(this.regB.getVal());
		 * 
		 * } if (result < 0) {
		 * 
		 * } boolean zF = (result == 0); boolean cF = ((result & 0b11100000000) != 0);
		 * System.out.println("FLAGS FUNC"+zF+cF+Integer.toBinaryString(result &
		 * 0b11100000000)); regFlags.FlagsIn(zF, cF);
		 */
		boolean zF, cF;
		int result = (0b00000000000000000000000011111111 & this.regA.getVal())
				+ (0b00000000000000000000000011111111 & this.regB.getVal());
		if ((result & 0b11111111) == 0) {
			zF = true;
		} else {
			zF = false;
		}

		if ((result & 0b100000000) == 0) {
			cF = false;
		} else {
			cF = true;
		}

		regFlags.FlagsIn(zF, cF);

		//System.out.println("FLAGS FUNC"+zF+cF+Integer.toBinaryString(result & 0b11100000000));
		// String a = Integer.toBinaryString(0b11111111 & this.regA.getVal());
		// System.out.println("FLAGS FUNC"+zF+cF+Integer.toBinaryString(result));
	}

	public byte ALUOut(boolean sub) {
		if (sub) {
			return (byte) (this.regA.getVal() - this.regB.getVal());
		} else {
			return (byte) (this.regA.getVal() + this.regB.getVal());
		}
	}

	public boolean getZeroFlag() {
		return this.regFlags.getZF();
	}

	public boolean getCarryFlag() {
		return this.regFlags.getCF();
	}

}

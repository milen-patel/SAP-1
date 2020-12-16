package sap;

import interfaces.Register;

public class ALU {
	Register regA;
	Register regB;
	RegisterFlags regFlags;

	public ALU(Register A, Register B) {
		// The ALU maintains references to the two operand registers in addition to a
		// register for storing flags
		this.regA = A;
		this.regB = B;
		this.regFlags = new RegisterFlags();
	}

	// Updates the flags register based on the current ALU value
	public void flagsIn(boolean sub) {
		boolean zF, cF;
		
		// Compute the zero flag
		int result = (0b00000000000000000000000011111111 & this.regA.getVal())
				+ (0b00000000000000000000000011111111 & this.regB.getVal());
		if ((result & 0b11111111) == 0) {
			zF = true;
		} else {
			zF = false;
		}

		// Compute the carry flag
		if ((result & 0b100000000) == 0) {
			cF = false;
		} else {
			cF = true;
		}

		// Use the flags register function to update values
		regFlags.FlagsIn(zF, cF);
	}

	// Returns the current value of the stateless ALU
	public byte ALUOut(boolean sub) {
		if (sub) {
			return (byte) (this.regA.getVal() - this.regB.getVal());
		} else {
			return (byte) (this.regA.getVal() + this.regB.getVal());
		}
	}

	// Getter functions for the ALU flags
	public boolean getZeroFlag() {
		return this.regFlags.getZF();
	}

	public boolean getCarryFlag() {
		return this.regFlags.getCF();
	}

}

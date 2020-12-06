package sap;

public class Memory {
	int[] data;
	Register MAR;
	
	public Memory() {
		this.data = new int[16];
		data[3] = 8;
		this.MAR = new Register4Bit();
	}
	
	public void memoryIn(int val) {
		this.data[this.MAR.getVal()] = val;
	}
	
	public int memoryOut() {
		return this.data[this.MAR.getVal()];
	}
	
	public int[] getRAM() {
		return this.data;
	}

}

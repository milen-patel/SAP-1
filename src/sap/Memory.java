package sap;
import java.util.List;
import java.util.ArrayList;

public class Memory {
	private int[] data;
	private Register MAR;
	private List<RAMObserver> observers;
	public Memory() {
		this.data = new int[16];
		this.MAR = new Register4Bit();
		this.observers = new ArrayList<RAMObserver>();
	}
	
	public void memoryIn(int val) {
		this.data[this.MAR.getVal()] = val;
		this.notifyObservers(this.MAR.getVal());
	}
	
	public int memoryOut() {
		return this.data[this.MAR.getVal()];
	}
	
	public int[] getRAM() {
		return this.data;
	}
	
	public void addRAMObserver(RAMObserver o) {
		if (o == null) {
			return;
		}
		this.observers.add(o);
	}
	
	public void removeRAMObserver(RAMObserver o) {
		if (o == null) {
			return;
		}
		this.observers.remove(o);
	}
	
	private void notifyObservers(int address) {
		for (RAMObserver o: observers) {
			o.valChanged(address);
		}
	}
	
	

}

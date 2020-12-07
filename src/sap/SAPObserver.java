package sap;

public interface SAPObserver {
	public void regAChange(byte newVal);
	public void regBChange(byte newVal);
	public void pcChange(byte newVal);
	public void marChange(byte newVal);
	public void outChange(byte newVal);
	public void irChange(byte newVal);
	public void stepCycleChange(byte newVal);
	public void flagChange();
	public void busChange(byte newVal);
}

package misat11.bw.api.special;

public interface ArrowBlocker extends SpecialItem {
	public int getProtectionTime();

	public int getUsedTime();
	
	public boolean isActivated();
	
	public void runTask();
}

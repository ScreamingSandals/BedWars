package misat11.bw.api.special;

public interface ArrowBlocker extends SpecialItem {
	public int getProtectionTime();
	
	public boolean isActivated();
	
	public boolean isProtecting();
	
	public void runTask();
}

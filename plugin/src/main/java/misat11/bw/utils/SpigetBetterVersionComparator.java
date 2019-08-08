package misat11.bw.utils;

import org.inventivetalent.update.spiget.comparator.VersionComparator;

public class SpigetBetterVersionComparator extends VersionComparator {

	public static final SpigetBetterVersionComparator instance = new SpigetBetterVersionComparator();

	@Override
	public boolean isNewer(String arg0, String arg1) {
		String currentVersion = arg0.split("-")[0];
		currentVersion += (arg0.toLowerCase().contains("pre") || arg0.toLowerCase().contains("snapshot")) ? ".0" : ".1";
		String checkVersion = arg1.split("-")[0];
		checkVersion += (arg1.toLowerCase().contains("pre") || arg1.toLowerCase().contains("snapshot")) ? ".0" : ".1";
		
		// TODO remake Semantic version comparator (This comparator should not work with
		// current version for example 1.10.0 and new version 2.0.0)
		return VersionComparator.SEM_VER.isNewer(currentVersion, checkVersion);
	}

}

package misat11.bw.special;

import org.bukkit.plugin.Plugin;

import misat11.bw.special.listener.ArrowBlockerListener;
import misat11.bw.special.listener.GolemListener;
import misat11.bw.special.listener.LuckyBlockAddonListener;
import misat11.bw.special.listener.MagnetShoesListener;
import misat11.bw.special.listener.ProtectionWallListener;
import misat11.bw.special.listener.RescuePlatformListener;
import misat11.bw.special.listener.TNTSheepListener;
import misat11.bw.special.listener.TrackerListener;
import misat11.bw.special.listener.TrapListener;
import misat11.bw.special.listener.WarpPowderListener;

public class SpecialRegister {

	public static void onEnable(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(new ArrowBlockerListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new GolemListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new LuckyBlockAddonListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new MagnetShoesListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ProtectionWallListener(), plugin); // TODO
		plugin.getServer().getPluginManager().registerEvents(new RescuePlatformListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new TNTSheepListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new TrackerListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new TrapListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new WarpPowderListener(), plugin);
	}

}

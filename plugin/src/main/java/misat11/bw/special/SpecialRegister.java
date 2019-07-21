package misat11.bw.special;

import org.bukkit.plugin.Plugin;

import misat11.bw.special.listener.LuckyBlockAddonListener;
import misat11.bw.special.listener.TrapListener;
import misat11.bw.special.listener.WarpPowderListener;

public class SpecialRegister {
	public static void onEnable(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(new WarpPowderListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new LuckyBlockAddonListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new TrapListener(), plugin);
	}
}

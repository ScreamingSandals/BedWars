package org.screamingsandals.bedwars.special;

import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.special.listener.*;

public class SpecialRegister {

    public static void onEnable(Plugin plugin) {
        Main.getInstance().registerBedwarsListener(new ArrowBlockerListener());
        Main.getInstance().registerBedwarsListener(new GolemListener());
        Main.getInstance().registerBedwarsListener(new LuckyBlockAddonListener());
        Main.getInstance().registerBedwarsListener(new MagnetShoesListener());
        Main.getInstance().registerBedwarsListener(new PermaItemListener());
        Main.getInstance().registerBedwarsListener(new ProtectionWallListener());
        Main.getInstance().registerBedwarsListener(new RescuePlatformListener());
        Main.getInstance().registerBedwarsListener(new TeamChestListener());
        Main.getInstance().registerBedwarsListener(new ThrowableFireballListener());
        Main.getInstance().registerBedwarsListener(new TNTSheepListener());
        Main.getInstance().registerBedwarsListener(new TrackerListener());
        Main.getInstance().registerBedwarsListener(new TrapListener());
        Main.getInstance().registerBedwarsListener(new WarpPowderListener());
        Main.getInstance().registerBedwarsListener(new AutoIgniteableTNTListener());
    }

}

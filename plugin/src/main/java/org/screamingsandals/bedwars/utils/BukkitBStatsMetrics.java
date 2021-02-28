package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.premium.PremiumBedwars;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
@UtilityClass
public class BukkitBStatsMetrics {
    public final int PLUGIN_ID = 7147;

    @OnPostEnable
    public void onPostEnable() {
        var metrics = new Metrics(Main.getInstance().as(JavaPlugin.class), PLUGIN_ID);
        metrics.addCustomChart(new Metrics.SimplePie("edition", () -> PremiumBedwars.isPremium() ? "Premium" : "Free"));
        metrics.addCustomChart(new Metrics.SimplePie("build_number", () -> VersionInfo.BUILD_NUMBER));
    }
}

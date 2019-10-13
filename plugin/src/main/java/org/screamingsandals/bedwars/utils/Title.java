package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.Main;
import misat11.lib.nms.NMSUtils;
import org.bukkit.entity.Player;

public class Title {
    public static void send(Player player, String title, String subtitle) {
        int fadeIn = Main.getConfigurator().config.getInt("title.fadeIn");
        int stay = Main.getConfigurator().config.getInt("title.stay");
        int fadeOut = Main.getConfigurator().config.getInt("title.fadeOut");

        NMSUtils.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }
}

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.Main;
import org.bukkit.entity.Player;

public class Title {
    public static void send(Player player, String title, String subtitle) {
        if (!Main.getConfigurator().node("title", "enabled").getBoolean()) {
            return;
        }

        int fadeIn = Main.getConfigurator().node("title", "fadeIn").getInt();
        int stay = Main.getConfigurator().node("title", "stay").getInt();
        int fadeOut = Main.getConfigurator().node("title", "fadeOut").getInt();

        org.screamingsandals.bedwars.lib.nms.title.Title.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }
}

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.title.Title;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;

import java.time.Duration;

@UtilityClass
public class TitleUtils {
    public void send(PlayerWrapper player, String title, String subtitle) {
        if (!Main.getConfigurator().node("title", "enabled").getBoolean()) {
            return;
        }

        int fadeIn = Main.getConfigurator().node("title", "fadeIn").getInt();
        int stay = Main.getConfigurator().node("title", "stay").getInt();
        int fadeOut = Main.getConfigurator().node("title", "fadeOut").getInt();

        var titleComponent = Title.title(
                AdventureHelper.toComponent(title),
                AdventureHelper.toComponent(subtitle),
                Title.Times.of(
                        Duration.ofMillis(fadeIn * 50L),
                        Duration.ofMillis(stay * 50L),
                        Duration.ofMillis(fadeOut * 50L)
                )
        );

        player.showTitle(titleComponent);
    }
}

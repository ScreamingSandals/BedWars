package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.events.GameStartedEventImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class LobbyInvisibilityListener {

    @OnEvent
    public void onGameStarted(GameStartedEventImpl event) {
        final var game = event.getGame();
        if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.INVISIBLE_LOBBY_ON_GAME_START, Boolean.class, true)) {
            if (game.getLobbyPos1() != null && game.getLobbyPos2() != null) {
                MiscUtils.getLocationsBetween(game.getLobbyPos1(), game.getLobbyPos2()).forEach(loc -> {
                    if (loc.getBlock().getType().isAir()) {
                        final var block = loc.getBlock();
                        final var blockState = block.getBlockState().orElseThrow();
                        game.getRegion().putOriginalBlock(loc, blockState);
                        block.setType(MaterialMapping.getAir());
                    }
                });
            }
        }
    }


}

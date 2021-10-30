package org.screamingsandals.bedwars.player;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.api.player.PlayerManager;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service(dependsOn = {
        GameManagerImpl.class
})
@RequiredArgsConstructor
public class PlayerManagerImpl implements PlayerManager<BedWarsPlayer, GameImpl> {
    private final List<BedWarsPlayer> players = new ArrayList<>();

    {
        PlayerMapper
                .UNSAFE_getPlayerConverter()
                .registerP2W(BedWarsPlayer.class, bwPlayer -> (PlayerWrapper) bwPlayer.raw())
                .registerW2P(BedWarsPlayer.class, playerWrapper -> getPlayer(playerWrapper).orElseThrow());
    }

    public static PlayerManagerImpl getInstance() {
        return ServiceManager.get(PlayerManagerImpl.class);
    }

    public BedWarsPlayer getPlayerOrCreate(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper)
                .orElseGet(() -> {
                    var p = new BedWarsPlayer(playerWrapper);
                    players.add(p);
                    return p;
                });
    }

    public Optional<BedWarsPlayer> getPlayer(UUID uuid) {
        return PlayerMapper.getPlayer(uuid).flatMap(this::getPlayer);
    }

    public Optional<BedWarsPlayer> getPlayer(PlayerWrapper playerWrapper) {
        return players.stream()
                .filter(bedWarsPlayer -> bedWarsPlayer.getUuid().equals(playerWrapper.getUuid()))
                .findFirst();
    }

    public boolean isPlayerInGame(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper).map(p -> p.getGame() != null).orElse(false);
    }

    public boolean isPlayerInGame(UUID uuid) {
        return getPlayer(uuid).map(p -> p.getGame() != null).orElse(false);
    }

    public void dropPlayer(BedWarsPlayer player) {
        player.changeGame(null);
        players.remove(player);
    }

    public boolean isPlayerRegistered(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper).isPresent();
    }

    public boolean isPlayerRegistered(UUID uuid) {
        return getPlayer(uuid).isPresent();
    }

    @Override
    public Optional<GameImpl> getGameOfPlayer(UUID uuid) {
        return getPlayer(uuid).map(BedWarsPlayer::getGame);
    }

    public Optional<GameImpl> getGameOfPlayer(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper).map(BedWarsPlayer::getGame);
    }
}

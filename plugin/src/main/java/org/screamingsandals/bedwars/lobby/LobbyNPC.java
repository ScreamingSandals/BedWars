package org.screamingsandals.bedwars.lobby;

import lombok.Data;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.SerializableLocation;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.npc.skin.NPCSkin;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.InteractType;
import org.screamingsandals.lib.world.LocationHolder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ConfigSerializable
public class LobbyNPC {
    private SerializableLocation location;
    @Nullable
    private NPCSkin skin;
    private Action action = Action.OPEN_GAMES_INVENTORY;
    private String value;
    private boolean shouldLookAtPlayer = true;
    private final List<Component> hologramAbove = new ArrayList<>();

    private transient NPC npc;

    public void spawn() {
        if (npc == null && location != null && location.isWorldLoaded()) {
            npc = NPC.of(location.as(LocationHolder.class))
                    .setDisplayName(hologramAbove)
                    .setTouchable(true)
                    .setShouldLookAtPlayer(shouldLookAtPlayer);

            if (skin != null) {
                npc.setSkin(skin);
            }

            npc.show();

            Server.getConnectedPlayers().forEach(npc::addViewer);
        }
    }

    public void destroy() {
        if (npc != null) {
            npc.destroy();
            npc = null;
        }
    }

    public void handleClick(PlayerWrapper player, InteractType type) {
        switch (action) {
            case JOIN_GAME:
                if (value != null && !value.isEmpty()) {
                    GameManagerImpl.getInstance().getGame(value).ifPresent(game1 ->
                            Server.runSynchronously(() -> game1.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)))
                    );
                }
                break;
            case OPEN_GAMES_INVENTORY:
                if (value != null && !value.isEmpty()) {
                    Server.runSynchronously(() -> GamesInventory.getInstance().openForPlayer(player, value));
                }
            break;
        }
    }

    public enum Action {
        OPEN_GAMES_INVENTORY,
        JOIN_GAME
    }

}

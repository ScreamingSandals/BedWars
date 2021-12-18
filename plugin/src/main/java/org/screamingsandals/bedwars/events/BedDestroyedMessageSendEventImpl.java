package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.BedDestroyedMessageSendEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.AdventureHelper;

@Data
public class BedDestroyedMessageSendEventImpl implements BedDestroyedMessageSendEvent<GameImpl, BedWarsPlayer, TeamImpl>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer victim;
    @Nullable
    private final BedWarsPlayer destroyer;
    private final TeamImpl team;
    @NonNull
    private Message message;
    private boolean cancelled;

    @Override
    public String getStringMessage() {
        return AdventureHelper.toLegacy(message.getForJoined(victim));
    }

    @Override
    public void setStringMessage(String message) {
        this.message = Message.ofPlainText(message);
    }
}

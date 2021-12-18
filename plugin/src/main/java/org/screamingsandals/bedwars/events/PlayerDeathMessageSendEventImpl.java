package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.NonNull;
import org.screamingsandals.bedwars.api.events.PlayerDeathMessageSendEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.AdventureHelper;

@Data
public class PlayerDeathMessageSendEventImpl implements PlayerDeathMessageSendEvent<GameImpl, BedWarsPlayer>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer victim;
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

package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.screamingsandals.bedwars.api.events.PlayerDeathMessageSendEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.AdventureHelper;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerDeathMessageSendEventImpl extends CancellableAbstractEvent implements PlayerDeathMessageSendEvent<Game, BedWarsPlayer> {
    private final Game game;
    private final BedWarsPlayer victim;
    @NonNull
    private Message message;

    @Override
    public String getStringMessage() {
        return AdventureHelper.toLegacy(message.getForJoined(victim));
    }

    @Override
    public void setStringMessage(String message) {
        this.message = Message.ofPlainText(message);
    }
}

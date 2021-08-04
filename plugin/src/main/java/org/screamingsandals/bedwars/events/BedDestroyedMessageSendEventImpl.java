package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.BedDestroyedMessageSendEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.AdventureHelper;

@EqualsAndHashCode(callSuper = true)
@Data
public class BedDestroyedMessageSendEventImpl extends CancellableAbstractEvent implements BedDestroyedMessageSendEvent<Game, BedWarsPlayer, CurrentTeam> {
    private final Game game;
    private final BedWarsPlayer victim;
    @Nullable
    private final BedWarsPlayer destroyer;
    private final CurrentTeam team;
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

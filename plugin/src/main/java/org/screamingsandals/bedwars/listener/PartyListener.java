package org.screamingsandals.bedwars.listener;

import com.alessiodp.parties.api.Parties;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PlayerJoinedEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;

@Service(dependsOn = {
    MainConfig.class
})
@UtilityClass
public class PartyListener {

    @ShouldRunControllable
    public boolean isEnabled() {
        return MainConfig.getInstance().node("party", "enabled").getBoolean() && PluginManager.isEnabled(PluginManager.createKey("Parties").orElseThrow());
    }

    @OnEvent
    public void onBedWarsPlayerJoined(PlayerJoinedEventImpl e) {
        if (!MainConfig.getInstance().node("party", "autojoin-members").getBoolean()) {
            return;
        }

        final var player = e.getPlayer();
        final var partyApi = Parties.getApi();
        final var partyPlayer = partyApi.getPartyPlayer(player.getUuid());

        final var game = e.getGame();

        if (partyPlayer.getPartyId() != null) {
            final var party = partyApi.getParty(partyPlayer.getPartyId());

            if (party != null) {
                final var leaderUUID = party.getLeader();

                if (leaderUUID != null) {
                    //Player who joined is party leader
                    if (leaderUUID.equals(player.getUuid())) {
                        final var players = MiscUtils.getOnlinePlayersW(party.getMembers());

                        if (players.size() > 1) {
                            players.forEach(partyMember -> {
                                if (partyMember != null) {
                                    if (partyMember.getUuid().equals(player.getUuid())) {
                                        return;
                                    }
                                    player.sendMessage(Message.of(LangKeys.PARTY_INFORM_GAME_JOIN).defaultPrefix());

                                    var gameOfPlayer = PlayerManagerImpl.getInstance().getGameOfPlayer(partyMember);
                                    if (gameOfPlayer.isPresent()) {
                                        if (gameOfPlayer.get().getName().equalsIgnoreCase(game.getName())) {
                                            return;
                                        }
                                        gameOfPlayer.get().leaveFromGame(partyMember.as(Player.class)); // TODO
                                    }
                                    game.joinToGame(partyMember.as(Player.class)); // TODO
                                }
                            });

                            if (MainConfig.getInstance().node("party", "notify-when-warped").getBoolean(true)) {
                                player.sendMessage(Message.of(LangKeys.PARTY_WARPED).defaultPrefix());
                            }
                        }
                    }
                }
            }
        }
    }
}

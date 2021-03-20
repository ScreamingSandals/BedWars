package org.screamingsandals.bedwars.listener;

import com.alessiodp.parties.api.Parties;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedEvent;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;

public class PartyListener implements Listener {

    @EventHandler
    public void onBedWarsPlayerJoined(BedwarsPlayerJoinedEvent e) {
        if (!MainConfig.getInstance().node("party", "autojoin-members").getBoolean()) {
            return;
        }

        final var player = e.getPlayer();
        final var partyApi = Parties.getApi();
        final var partyPlayer = partyApi.getPartyPlayer(player.getUniqueId());

        final var game = e.getGame();

        if (partyPlayer.getPartyId() != null) {
            final var party = partyApi.getParty(partyPlayer.getPartyId());

            if (party != null) {
                final var leaderUUID = party.getLeader();

                if (leaderUUID != null) {
                    //Player who joined is party leader
                    if (leaderUUID.equals(player.getUniqueId())) {
                        final var players = MiscUtils.getOnlinePlayers(party.getMembers());

                        if (players.size() > 1) {
                            players.forEach(partyMember -> {
                                if (partyMember != null) {
                                    if (partyMember.getUniqueId().equals(player.getUniqueId())) {
                                        return;
                                    }
                                    PlayerMapper.wrapPlayer(player).sendMessage(Message.of(LangKeys.PARTY_INFORM_GAME_JOIN).defaultPrefix());

                                    final var gameOfPlayer = Main.getPlayerGameProfile(partyMember).getGame();
                                    if (gameOfPlayer != null) {
                                        if (gameOfPlayer.getName().equalsIgnoreCase(game.getName())) {
                                            return;
                                        }
                                        gameOfPlayer.leaveFromGame(partyMember);
                                    }
                                    game.joinToGame(partyMember);
                                }
                            });

                            if (MainConfig.getInstance().node("party", "notify-when-warped").getBoolean(true)) {
                                PlayerMapper.wrapPlayer(player).sendMessage(Message.of(LangKeys.PARTY_WARPED).defaultPrefix());
                            }
                        }
                    }
                }
            }
        }
    }
}

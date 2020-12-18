package org.screamingsandals.bedwars.listener;

import com.alessiodp.parties.api.Parties;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedEvent;
import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class PartyListener implements Listener {

    @EventHandler
    public void onBedWarsPlayerJoined(BedwarsPlayerJoinedEvent e) {
        if (!Main.getConfigurator().config.getBoolean("party.autojoin-members", false)) {
            return;
        }

        final var player = e.getPlayer();
        final var PartyAPI = Parties.getApi();
        final var partyPlayer = PartyAPI.getPartyPlayer(player.getUniqueId());

        final var game = e.getGame();

        if (!partyPlayer.getPartyName().isEmpty()) {
            final var party = PartyAPI.getParty(partyPlayer.getPartyName());

            if (party != null) {
                final var leaderUUID = party.getLeader();

                if (leaderUUID != null) {
                    //Player who joined is party leader
                    if (leaderUUID.equals(player.getUniqueId())) {
                        final var players = party.getMembers();

                        if (!players.isEmpty()) {
                            players.forEach(uuid->{
                                final var partyMember = Bukkit.getPlayer(uuid);
                                if (partyMember != null) {
                                    partyMember.sendMessage(i18n("party_inform_game_join", true));

                                    final var gameOfPlayer = Main.getPlayerGameProfile(partyMember).getGame();
                                    if (gameOfPlayer != null) {
                                        if (gameOfPlayer.equals(game)) {
                                            return;
                                        }

                                        gameOfPlayer.leaveFromGame(partyMember);
                                    }

                                    game.joinToGame(player);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}

package org.screamingsandals.bedwars.listener;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.utils.MiscUtils;

import java.util.List;
import java.util.UUID;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class PartyListener implements Listener {

    @EventHandler
    public void onBedWarsPlayerJoined(BedwarsPlayerJoinedEvent e) {
        if (!Main.getConfigurator().config.getBoolean("party.autojoin-members")) {
            return;
        }

        final Player player = e.getPlayer();
        final PartiesAPI partyApi = Parties.getApi();
        final PartyPlayer partyPlayer = partyApi.getPartyPlayer(player.getUniqueId());

        final Game game = (Game) e.getGame();

        if (partyPlayer.getPartyId() != null) {
            final Party party = partyApi.getParty(partyPlayer.getPartyId());

            if (party != null) {
                final UUID leaderUUID = party.getLeader();

                if (leaderUUID != null) {
                    //Player who joined is party leader
                    if (leaderUUID.equals(player.getUniqueId())) {
                        final List<Player> players = MiscUtils.getOnlinePlayers(party.getMembers());

                        if (players.size() > 1) {
                            players.forEach(partyMember -> {
                                if (partyMember != null) {
                                    if (partyMember.getUniqueId().equals(player.getUniqueId())) {
                                        return;
                                    }
                                    player.sendMessage(i18n("party_inform_game_join"));

                                    final Game gameOfPlayer = Main.isPlayerInGame(partyMember) ? Main.getPlayerGameProfile(partyMember).getGame() : null;
                                    if (gameOfPlayer != null) {
                                        if (gameOfPlayer.getName().equalsIgnoreCase(game.getName())) {
                                            return;
                                        }

                                        gameOfPlayer.leaveFromGame(partyMember);
                                    }

                                    game.joinToGame(partyMember);
                                }
                            });

                            if (Main.getConfigurator().config.getBoolean("party.notify-when-warped")) {
                                player.sendMessage(i18n("party_command_warped"));
                            }
                        }
                    }
                }
            }
        }
    }
}

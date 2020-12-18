package org.screamingsandals.bedwars.commands;

import com.alessiodp.parties.api.Parties;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class PartyCommand extends BaseCommand {

    public PartyCommand() {
        super("party", null, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        final var player = (Player) sender;

        if (args.size() != 1) {
            player.sendMessage(i18n("party_command_invalid_arguments", true));
            return true;
        }

        if (args.get(0).equalsIgnoreCase("warp")) {
            final var PartyAPI = Parties.getApi();
            final var partyPlayer = PartyAPI.getPartyPlayer(player.getUniqueId());
            final var game = Main.getPlayerGameProfile(player).getGame();

            if (partyPlayer.getPartyName().isEmpty()) {
                player.sendMessage(i18n("party_command_not_in_party", true));
                return true;
            }

            final var party = PartyAPI.getParty(partyPlayer.getPartyName());

            if (party != null) {
                final var leaderUUID = party.getLeader();

                if (leaderUUID != null) {

                    if (!leaderUUID.equals(player.getUniqueId())) {
                        player.sendMessage(i18n("party_command_not_party_leader", true));
                        return true;
                    }

                    final var players = party.getMembers();

                    if (!players.isEmpty()) {
                        players.forEach(uuid -> {
                            final var partyMember = Bukkit.getPlayer(uuid);
                            if (partyMember != null) {

                                if (game == null) {
                                    partyMember.sendMessage(i18n("party_warped", true));
                                    PlayerUtils.teleportPlayer(partyMember, player.getLocation());
                                    return;
                                }

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
        } else if (args.get(0).equalsIgnoreCase("help")) {
            player.sendMessage(i18n("party_command_help", true));
        }

        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
            completion.add("warp");
    }
}

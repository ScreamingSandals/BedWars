package org.screamingsandals.bedwars.commands;

import com.alessiodp.parties.api.Parties;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;

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
            final var partyApi = Parties.getApi();
            final var partyPlayer = partyApi.getPartyPlayer(player.getUniqueId());
            final var game = Main.getPlayerGameProfile(player).getGame();

            if (partyPlayer.getPartyId() == null) {
                player.sendMessage(i18n("party_command_not_in_party", true));
                return true;
            }

            final var party = partyApi.getParty(partyPlayer.getPartyId());

            if (party != null) {
                final var leaderUUID = party.getLeader();

                if (leaderUUID != null) {

                    if (!leaderUUID.equals(player.getUniqueId())) {
                        player.sendMessage(i18n("party_command_not_party_leader", true));
                        return true;
                    }

                    final var players = MiscUtils.getOnlinePlayers(party.getMembers());

                    if (players.size() == 1) {
                        player.sendMessage(i18n("party_command_is_empty", true));
                        return true;
                    }

                    players.forEach(partyMember -> {
                        if (partyMember != null) {
                            if (partyMember.getUniqueId().equals(player.getUniqueId())) {
                                return;
                            }

                            final var gameOfPlayer = Main.getPlayerGameProfile(partyMember).getGame();

                            if (game == null) {
                                partyMember.sendMessage(i18n("party_warped", true));
                                if (gameOfPlayer != null) {
                                    gameOfPlayer.leaveFromGame(partyMember);
                                }
                                PlayerUtils.teleportPlayer(partyMember, player.getLocation());
                                return;
                            }

                            partyMember.sendMessage(i18n("party_inform_game_join", true));
                            if (gameOfPlayer != null) {
                                if (gameOfPlayer.getName().equalsIgnoreCase(game.getName())) {
                                    return;
                                }

                                gameOfPlayer.leaveFromGame(partyMember);
                            }

                            game.joinToGame(partyMember);
                        }
                    });
                    if (Main.getConfigurator().node("party", "notify-when-warped").getBoolean(true)) {
                        player.sendMessage(i18n("party_command_warped", true));
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

package org.screamingsandals.bedwars.commands;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.utils.MiscUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class PartyCommand extends BaseCommand {

    public PartyCommand() {
        super("party", PARTY_PERMISSION, false, Main.getConfigurator().config.getBoolean("default-permissions.party"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (args.size() != 1) {
            sender.sendMessage(i18n("party_inform_game_join"));
            return true;
        }
        Player player = (Player) sender;

        if ("warp".equalsIgnoreCase(args.get(0))) {
            final PartiesAPI api = Parties.getApi();
            final PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
            final Game game = Main.isPlayerInGame(player) ? Main.getPlayerGameProfile(player).getGame() : null;

            if (partyPlayer.getPartyId() == null) {
                player.sendMessage(i18n("party_command_not_in_party"));
                return true;
            }

            final Party party = api.getParty(partyPlayer.getPartyId());

            if (party != null) {
                final UUID leaderUUID = party.getLeader();

                if (leaderUUID != null) {
                    if (!leaderUUID.equals(player.getUniqueId())) {
                        player.sendMessage(i18n("party_command_not_party_leader"));
                        return true;
                    }

                    final List<Player> players = MiscUtils.getOnlinePlayers(party.getMembers());

                    players.forEach(partyMember -> {
                        if (partyMember != null) {
                            if (partyMember.getUniqueId().equals(player.getUniqueId())) {
                                return;
                            }

                            final Game gameOfPlayer = Main.isPlayerInGame(partyMember) ? Main.getPlayerGameProfile(partyMember).getGame() : null;

                            if (game == null) {
                                partyMember.sendMessage(i18n("party_warped"));
                                if (gameOfPlayer != null) {
                                    gameOfPlayer.leaveFromGame(partyMember);
                                }
                                partyMember.teleport(player.getLocation());
                                return;
                            }

                            partyMember.sendMessage(i18n("party_inform_game_join"));
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

        } else if ("help".equalsIgnoreCase(args.get(0))) {
            player.sendMessage(i18n("party_command_help"));
        } else {
            player.sendMessage(i18n("party_command_invalid_arguments"));
        }

        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Arrays.asList("warp", "help"));
        }
    }

}

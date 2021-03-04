package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import com.alessiodp.parties.api.Parties;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.Optional;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class PartyCommand extends BaseCommand {
    public PartyCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "party", BedWarsPermission.PARTY_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
            commandSenderWrapperBuilder
                    .argument(manager
                            .argumentBuilder(String.class, "action")
                            .withSuggestionsProvider((c, s) -> List.of("warp", "help"))
                            .asOptional()
                    )
            .handler(commandContext -> {
                var sender = commandContext.getSender();

                Optional<String> action = commandContext.getOptional("action");

                if (action.isEmpty()) {
                    sender.sendMessage(i18n("party_command_invalid_arguments", true));
                    return;
                }

                var player = sender.as(Player.class);

                if (action.get().equalsIgnoreCase("warp")) {
                    final var partyApi = Parties.getApi();
                    final var partyPlayer = partyApi.getPartyPlayer(player.getUniqueId());
                    final var game = Main.getPlayerGameProfile(player).getGame();

                    if (partyPlayer.getPartyId() == null) {
                        player.sendMessage(i18n("party_command_not_in_party", true));
                        return;
                    }

                    final var party = partyApi.getParty(partyPlayer.getPartyId());

                    if (party != null) {
                        final var leaderUUID = party.getLeader();

                        if (leaderUUID != null) {

                            if (!leaderUUID.equals(player.getUniqueId())) {
                                player.sendMessage(i18n("party_command_not_party_leader", true));
                                return;
                            }

                            final var players = MiscUtils.getOnlinePlayers(party.getMembers());

                            if (players.size() == 1) {
                                player.sendMessage(i18n("party_command_is_empty", true));
                                return;
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
                            if (MainConfig.getInstance().node("party", "notify-when-warped").getBoolean(true)) {
                                player.sendMessage(i18n("party_command_warped", true));
                            }
                        }

                    }
                } else if (action.get().equalsIgnoreCase("help")) {
                    player.sendMessage(i18n("party_command_help", true));
                }
            })
        );
    }
}

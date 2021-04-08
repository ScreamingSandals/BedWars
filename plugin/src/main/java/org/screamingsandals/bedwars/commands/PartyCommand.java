package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import com.alessiodp.parties.api.Parties;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.Optional;

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
                var sender = commandContext.getSender().as(PlayerWrapper.class);

                Optional<String> action = commandContext.getOptional("action");

                if (action.isEmpty()) {
                    sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_INVALID_ARGUMENTS).defaultPrefix());
                    return;
                }

                var playerManager = PlayerManager.getInstance();
                var player = sender.as(Player.class);

                if (action.get().equalsIgnoreCase("warp")) {
                    final var partyApi = Parties.getApi();
                    final var partyPlayer = partyApi.getPartyPlayer(player.getUniqueId());
                    final var game = playerManager.getGameOfPlayer(sender);

                    if (partyPlayer.getPartyId() == null) {
                        sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_NOT_IN_PARTY).defaultPrefix());
                        return;
                    }

                    final var party = partyApi.getParty(partyPlayer.getPartyId());

                    if (party != null) {
                        final var leaderUUID = party.getLeader();

                        if (leaderUUID != null) {

                            if (!leaderUUID.equals(player.getUniqueId())) {
                                sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_NOT_PARTY_LEADER).defaultPrefix());
                                return;
                            }

                            final var players = MiscUtils.getOnlinePlayers(party.getMembers());

                            if (players.size() == 1) {
                                sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_IS_EMPTY).defaultPrefix());
                                return;
                            }

                            players.forEach(partyMember -> {
                                if (partyMember != null) {
                                    if (partyMember.getUniqueId().equals(player.getUniqueId())) {
                                        return;
                                    }

                                    final var wrapper = PlayerMapper.wrapPlayer(partyMember);
                                    final var gameOfPlayer = playerManager.getGameOfPlayer(wrapper);

                                    if (game.isEmpty()) {
                                        PlayerMapper.wrapPlayer(partyMember).sendMessage(Message.of(LangKeys.PARTY_WARPED).defaultPrefix());
                                        gameOfPlayer.ifPresent(value -> value.leaveFromGame(partyMember));
                                        PlayerUtils.teleportPlayer(partyMember, player.getLocation());
                                        return;
                                    }

                                    PlayerMapper.wrapPlayer(partyMember).sendMessage(Message.of(LangKeys.PARTY_INFORM_GAME_JOIN).defaultPrefix());
                                    if (gameOfPlayer.isPresent()) {
                                        if (gameOfPlayer.get().getName().equalsIgnoreCase(game.get().getName())) {
                                            return;
                                        }

                                        gameOfPlayer.get().leaveFromGame(partyMember);
                                    }

                                    game.get().joinToGame(partyMember);
                                }
                            });
                            if (MainConfig.getInstance().node("party", "notify-when-warped").getBoolean(true)) {
                                sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_WARPED).defaultPrefix());
                            }
                        }

                    }
                } else if (action.get().equalsIgnoreCase("help")) {
                    sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_HELP).defaultPrefix());
                }
            })
        );
    }
}

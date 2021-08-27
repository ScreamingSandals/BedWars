package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import com.alessiodp.parties.api.Parties;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartyCommand extends BaseCommand {
    public PartyCommand() {
        super("party", BedWarsPermission.PARTY_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
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

                var playerManager = PlayerManagerImpl.getInstance();

                if (action.get().equalsIgnoreCase("warp")) {
                    final var partyApi = Parties.getApi();
                    final var partyPlayer = partyApi.getPartyPlayer(sender.getUuid());
                    final var game = playerManager.getGameOfPlayer(sender);

                    if (partyPlayer.getPartyId() == null) {
                        sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_NOT_IN_PARTY).defaultPrefix());
                        return;
                    }

                    final var party = partyApi.getParty(partyPlayer.getPartyId());

                    if (party != null) {
                        final var leaderUUID = party.getLeader();

                        if (leaderUUID != null) {

                            if (!leaderUUID.equals(sender.getUuid())) {
                                sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_NOT_PARTY_LEADER).defaultPrefix());
                                return;
                            }

                            final var players = MiscUtils.getOnlinePlayersW(party.getMembers());

                            if (players.size() == 1) {
                                sender.sendMessage(Message.of(LangKeys.PARTY_COMMAND_IS_EMPTY).defaultPrefix());
                                return;
                            }

                            players.forEach(partyMember -> {
                                if (partyMember != null) {
                                    if (partyMember.getUuid().equals(sender.getUuid())) {
                                        return;
                                    }

                                    final var wrapper = PlayerMapper.wrapPlayer(partyMember);
                                    final var gameOfPlayer = playerManager.getGameOfPlayer(wrapper);

                                    if (game.isEmpty()) {
                                        PlayerMapper.wrapPlayer(partyMember).sendMessage(Message.of(LangKeys.PARTY_WARPED).defaultPrefix());
                                        gameOfPlayer.ifPresent(value -> value.leaveFromGame(partyMember));
                                        partyMember.teleport(sender.getLocation());
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

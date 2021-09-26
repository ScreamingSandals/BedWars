package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JoinTeamCommand extends BaseAdminSubCommand {
    public static final Map<UUID, TeamImpl> TEAMS_IN_HAND = new HashMap<>();

    public JoinTeamCommand() {
        super("jointeam");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("team")
                                    .withSuggestionsProvider((c, s) -> {
                                        if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                            return AdminCommand.gc.get(c.<String>get("game")).getTeams()
                                                    .stream()
                                                    .map(TeamImpl::getName)
                                                    .collect(Collectors.toList());
                                        }
                                        return List.of();
                                    })
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String team = commandContext.get("team");

                            var player = sender.as(PlayerWrapper.class);

                            for (var t : game.getTeams()) {
                                if (t.getName().equals(team)) {
                                    TEAMS_IN_HAND.put(player.getUuid(), t);

                                    Tasker.build(() -> TEAMS_IN_HAND.remove(player.getUuid()))
                                            .delay(200, TaskerTime.TICKS)
                                            .start();
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_TEAM_JOIN_ENTITY_CLICK_RIGHT_ON_ENTITY).defaultPrefix().placeholder("team", t.getName()));
                                    return;
                                }
                            }
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );
    }
}

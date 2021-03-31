package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.boss.BarColor;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LobbyBossBarColorCommand extends BaseAdminSubCommand {
    public LobbyBossBarColorCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "lobbybossbarcolor");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("barColor")
                                .withSuggestionsProvider((c, s) ->
                                        Stream.concat(Arrays.stream(BarColor.values()).map(BarColor::name), Stream.of("default")).collect(Collectors.toList())
                                )
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String barColor = commandContext.get("barColor");
                            if (!barColor.equalsIgnoreCase("default")) {
                                try {
                                    var c = BarColor.valueOf(barColor.toUpperCase());
                                    game.setLobbyBossBarColor(c);

                                    sender.sendMessage(Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_BAR_COLOR_SET)
                                            .defaultPrefix()
                                            .placeholder("color", c.name())
                                            .placeholder("type", "LOBBY"));
                                } catch (Exception e) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_BAR_COLOR).defaultPrefix());
                                }
                            } else {
                                game.setLobbyBossBarColor(null);

                                sender.sendMessage(Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_BAR_COLOR_SET)
                                        .defaultPrefix()
                                        .placeholder("color", "default")
                                        .placeholder("type", "LOBBY"));
                            }
                        }))
        );
    }
}

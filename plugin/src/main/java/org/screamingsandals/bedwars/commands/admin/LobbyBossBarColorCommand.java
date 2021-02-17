package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.boss.BarColor;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

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

                                    sender.sendMessage(i18n("admin_command_bar_color_set").replace("%color%", c.name())
                                            .replace("%type%", "LOBBY"));
                                } catch (Exception e) {
                                    sender.sendMessage(i18n("admin_command_invalid_bar_color"));
                                }
                            } else {
                                game.setLobbyBossBarColor(null);

                                sender.sendMessage(i18n("admin_command_bar_color_set").replace("%color%", "default")
                                        .replace("%type%", "LOBBY"));
                            }
                        }))
        );
    }
}

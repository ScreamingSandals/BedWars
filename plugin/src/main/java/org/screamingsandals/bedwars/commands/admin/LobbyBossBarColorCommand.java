package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import net.kyori.adventure.bossbar.BossBar;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LobbyBossBarColorCommand extends BaseAdminSubCommand {
    public LobbyBossBarColorCommand() {
        super("lobbybossbarcolor");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("barColor")
                                .withSuggestionsProvider((c, s) ->
                                        Stream.concat(Arrays.stream(BossBar.Color.values()).map(BossBar.Color::name), Stream.of("default")).collect(Collectors.toList())
                                )
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String barColor = commandContext.get("barColor");
                            if (!barColor.equalsIgnoreCase("default")) {
                                try {
                                    var c = BossBar.Color.valueOf(barColor.toUpperCase());
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

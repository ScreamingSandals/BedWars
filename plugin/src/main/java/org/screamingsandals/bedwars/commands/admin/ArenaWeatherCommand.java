package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.WeatherType;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class ArenaWeatherCommand extends BaseAdminSubCommand {
    public ArenaWeatherCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "arenaweather");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("arenaWeather")
                                .withSuggestionsProvider((c, s) ->
                                        Stream.concat(Arrays.stream(WeatherType.values()).map(WeatherType::name), Stream.of("default")).collect(Collectors.toList())
                                )
                            )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String arenaWeather = commandContext.get("arenaWeather");

                            if (!arenaWeather.equalsIgnoreCase("default")) {
                                try {
                                    var weatherType = WeatherType.valueOf(arenaWeather.toUpperCase());
                                    game.setArenaWeather(weatherType);

                                    sender.sendMessage(i18n("admin_command_arena_weather_set").replace("%weather%", weatherType.name()));
                                } catch (Exception e) {
                                    sender.sendMessage(i18n("admin_command_invalid_arena_weather"));
                                }
                            } else {
                                game.setArenaWeather(null);

                                sender.sendMessage(i18n("admin_command_arena_weather_set").replace("%weather%", "default"));
                            }

                        }))
        );
    }
}

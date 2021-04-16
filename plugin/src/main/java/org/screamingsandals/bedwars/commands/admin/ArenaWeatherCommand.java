package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.WeatherType;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ArenaWeatherCommand extends BaseAdminSubCommand {
    public ArenaWeatherCommand() {
        super("arenaweather");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
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

                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_WEATHER_SET).defaultPrefix().placeholder("weather", weatherType.name()));
                                } catch (Exception e) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_ARENA_WEATHER).defaultPrefix());
                                }
                            } else {
                                game.setArenaWeather(null);

                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_WEATHER_SET).defaultPrefix().placeholder("weather", "default"));
                            }

                        }))
        );
    }
}

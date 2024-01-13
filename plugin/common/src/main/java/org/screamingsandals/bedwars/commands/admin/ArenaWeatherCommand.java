/*
 * Copyright (C) 2024 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.weather.WeatherType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ArenaWeatherCommand extends BaseAdminSubCommand {
    public ArenaWeatherCommand() {
        super("arenaweather");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSender>newBuilder("arenaWeather")
                                .withSuggestionsProvider((c, s) ->
                                        Stream.concat(WeatherType.all().javaStreamOfLocations().map(ResourceLocation::asString), Stream.of("default")).collect(Collectors.toList())
                                )
                            )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String arenaWeather = commandContext.get("arenaWeather");

                            if (!arenaWeather.equalsIgnoreCase("default")) {
                                try {
                                    var weatherType = WeatherType.of(arenaWeather);
                                    game.setArenaWeather(weatherType);

                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_WEATHER_SET).defaultPrefix().placeholder("weather", weatherType.location().asString()));
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

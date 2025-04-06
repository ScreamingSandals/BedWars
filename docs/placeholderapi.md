# PlaceholderAPI

This plugin registers placeholders to [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

These placeholders are intended for use in other plugins and may not function if used in the BedWars configuration. However, they can be utilized in `shop.yml` and other shop-related files if PlaceholderAPI is installed. Use the slightly modified syntax: `%papi.<placeholder_name>%` (e.g., `%papi.bedwars_all_games_players%`).

## Global placeholders

* `%bedwars_all_games_players%` - Returns the total number of players across all games.
* `%bedwars_all_games_maxplayers%` - Returns the total maximum number of players across all games.
* `%bedwars_all_games_anyrunning%` - Returns `true` if any game is currently in `running` or `game_end_celebrating` state; otherwise `false`.
* `%bedwars_all_games_anywaiting%` - Returns `true` if any game is currently in `waiting` state; otherwise `false`.

## Current information about players

The placeholders in this section are specific to a player, based on the context in which they are used, most commonly the viewer of a message, hologram, etc.

### Information about the current game

Some of these placeholders contain a parameter `<team_name>`. Replace it with the name of the team. For example, if the team name is `red`, you would use `%bedwars_current_game_team_red_bed%`.

* `%bedwars_current_game%` - Returns the name of the current game.
* `%bedwars_current_game_players%` - Returns the number of players in the game.
* `%bedwars_current_game_minplayers%` - Returns the minimum number of players required for the game to start.
* `%bedwars_current_game_maxplayers%` - Returns the maximum number of players that can join the game.
* `%bedwars_current_game_world%` - Returns the name of the world where the arena is located.
* `%bedwars_current_game_state%` - Returns the current state of the game. Possible values are: `waiting`, `running`, `game_end_celebrating`, `rebuilding`, and `disabled`.
* `%bedwars_current_game_time%` - Returns the remaining time in seconds.
* `%bedwars_current_game_timeformat%` - Returns the remaining time formatted as `MM:SS`.
* `%bedwars_current_game_elapsedtime%` - Returns the elapsed time in seconds.
* `%bedwars_current_game_elapsedtimeformat%` - Returns the elapsed time formatted as `MM:SS`.
* `%bedwars_current_game_team_<team_name>_colored%` - Returns the team name in color.
* `%bedwars_current_game_team_<team_name>_color%` - Returns the team color code as `&<legacy color code>`.
* `%bedwars_current_game_team_<team_name>_ingame%` - Returns whether the team is currently playing, as a string: `yes` or `no`.
* `%bedwars_current_game_team_<team_name>_players%` - Returns the number of players in the team.
* `%bedwars_current_game_team_<team_name>_maxplayers%` - Returns the maximum number of players in the team.
* `%bedwars_current_game_team_<team_name>_bed%` - Returns whether the team currently has a valid target block, as a string: `yes` or `no`.
* `%bedwars_current_game_team_<team_name>_bedsymbol%` - Returns the colored target block symbol used in SBW's in-game scoreboard.
* `%bedwars_current_game_team_<team_name>_teamchests%` - Returns the number of team chests.
* `%bedwars_current_game_running%` - Returns `true` if the game is currently in `running` or `game_end_celebrating` state; otherwise `false`.
* `%bedwars_current_game_waiting%` - Returns `true` if the game is currently in `waiting` state; otherwise `false`.
* `%bedwars_current_available_teams%` - Returns the number of existing teams.
* `%bedwars_current_connected_teams%` - Returns the number of teams currently playing.
* `%bedwars_current_teamchests%` - Returns the number of team chests across all teams.

### Information about the player's team

* `%bedwars_current_team%` - Returns the name of the player's team.
* `%bedwars_current_team_color%` - Returns the color of the player's team as `&<legacy color code>`.
* `%bedwars_current_team_colored%` - Returns the team name in color.
* `%bedwars_current_team_players%` - Returns the number of players in the team.
* `%bedwars_current_team_maxplayers%` - Returns the maximum number of players in the team.
* `%bedwars_current_team_bed%` - Returns whether the team currently has a valid target block, as a string: `yes` or `no`.
* `%bedwars_current_team_teamchests%` - Returns the number of team chests.
* `%bedwars_current_team_bedsymbol%` - Returns the colored target block symbol used in SBW's in-game scoreboard.

## Game information placeholders

When using these placeholders, replace `<game>` with the specific game identifier and `<team_name>` with the name of the team. For example, if the game identifier is `game1` and the team name is `red`, you would use `%bedwars_game_game1_team_red_colored%`.

* `%bedwars_game_<game>_name%` - Returns the name of the game.
* `%bedwars_game_<game>_players%` - Returns the number of players in the game.
* `%bedwars_game_<game>_minplayers%` - Returns the minimum number of players required for the game to start.
* `%bedwars_game_<game>_maxplayers%` - Returns the maximum number of players that can join the game.
* `%bedwars_game_<game>_world%` - Returns the name of the world where the arena is located.
* `%bedwars_game_<game>_state%` - Returns the current state of the game. Possible values are: `waiting`, `running`, `game_end_celebrating`, `rebuilding`, and `disabled`.
* `%bedwars_game_<game>_available_teams%` - Returns the number of existing teams.
* `%bedwars_game_<game>_connected_teams%` - Returns the number of teams currently playing.
* `%bedwars_game_<game>_teamchests%` - Returns the number of team chests in the game.
* `%bedwars_game_<game>_time%` - Returns the remaining time in seconds.
* `%bedwars_game_<game>_timeformat%` - Returns the remaining time formatted as `MM:SS`.
* `%bedwars_game_<game>_elapsedtime%` - Returns the elapsed time in seconds.
* `%bedwars_game_<game>_elapsedtimeformat%` - Returns the elapsed time formatted as `MM:SS`.
* `%bedwars_game_<game>_team_<team_name>_colored%` - Returns the team name in color.
* `%bedwars_game_<game>_team_<team_name>_color%` - Returns the team color code as `&<legacy color code>`.
* `%bedwars_game_<game>_team_<team_name>_ingame%` - Returns whether the team is currently playing, as a string: `yes` or `no`.
* `%bedwars_game_<game>_team_<team_name>_players%` - Returns the number of players in the team.
* `%bedwars_game_<game>_team_<team_name>_maxplayers%` - Returns the maximum number of players in the team.
* `%bedwars_game_<game>_team_<team_name>_bed%` - Returns whether the team currently has a valid target block, as a string: `yes` or `no`.
* `%bedwars_game_<game>_team_<team_name>_bedsymbol%` - Returns the colored target block symbol used in SBW's in-game scoreboard.
* `%bedwars_game_<game>_team_<team_name>_teamchests%` - Returns the number of team chests.
* `%bedwars_game_<game>_running%` - Returns `true` if the game is currently in `running` or `game_end_celebrating` state; otherwise `false`.
* `%bedwars_game_<game>_waiting%` - Returns `true` if the game is currently in `waiting` state; otherwise `false`.

## Statistics placeholders

### Player stats placeholders

!!! tip "Custom leaderboards"
    
    You can create custom leaderboards using placeholders to display player statistics in various ways. To achieve this, consider using the [ajLeaderboards](https://www.spigotmc.org/resources/ajleaderboards.85548/) plugin with the placeholders listed below. This allows for greater flexibility beyond what the BedWars plugin offers.

    Need help setting it up? See the [ajLeaderboards Setup Guide](https://wiki.ajg0702.us/ajLeaderboards/setup/) for detailed instructions.

    For example, after adding a new board for kills using:
    ```
    /ajlb add %bedwars_stats_kills%
    ```
    you can display the top player’s name and value using:
    ```
    %ajlb_lb_bedwars_stats_kills_1_alltime_name%
    %ajlb_lb_bedwars_stats_kills_1_alltime_value%
    ```

    If you want to use a leaderboard ordered by total score, you can use the [built-in placeholders](#score-leaderboard-placeholders) instead of ajLeaderboards.

The placeholders in this section are specific to a player, based on the context in which they are used, most commonly the viewer of a message, hologram, etc.

* `%bedwars_stats_deaths%` - Returns the number of deaths.
* `%bedwars_stats_destroyed_beds%` - Returns the number of destroyed beds.
* `%bedwars_stats_kills%` - Returns the number of kills.
* `%bedwars_stats_loses%` - Returns the number of losses.
* `%bedwars_stats_score%` - Returns the total score.
* `%bedwars_stats_wins%` - Returns the number of wins.
* `%bedwars_stats_games%` - Returns the total number of games played.
* `%bedwars_stats_kd%` - Returns the kill/death ratio.

### Any player stats placeholders

When using these placeholders, replace `<player>` with the specific player's name. For example, if the player's name is `Misat11`, you would use `%bedwars_otherstats_Misat11_deaths%`.

* `%bedwars_otherstats_<player>_deaths%` - Returns the number of deaths.
* `%bedwars_otherstats_<player>_destroyed_beds%` - Returns the number of destroyed beds.
* `%bedwars_otherstats_<player>_kills%` - Returns the number of kills.
* `%bedwars_otherstats_<player>_loses%` - Returns the number of losses.
* `%bedwars_otherstats_<player>_score%` - Returns the total score.
* `%bedwars_otherstats_<player>_wins%` - Returns the number of wins.
* `%bedwars_otherstats_<player>_games%` - Returns the total number of games played.
* `%bedwars_otherstats_<player>_kd%` - Returns the kill/death ratio.

### Score leaderboard placeholders

These placeholders are used to access the built-in score-based leaderboard. Replace `<position>` with a number indicating the ranking spot you want to access. 

For example, to get the name of the player in 3rd place:
`%bedwars_leaderboard_score_3_name%`

* `%bedwars_leaderboard_score_<position>_name%` - Returns the name of the player.
* `%bedwars_leaderboard_score_<position>_uuid%` - Returns the UUID of the player.
* `%bedwars_leaderboard_score_<position>_deaths%` - Returns the number of deaths.
* `%bedwars_leaderboard_score_<position>_destroyed_beds%` - Returns the number of destroyed beds.
* `%bedwars_leaderboard_score_<position>_kills%` - Returns the number of kills.
* `%bedwars_leaderboard_score_<position>_loses%` - Returns the number of losses.
* `%bedwars_leaderboard_score_<position>_score%` - Returns the total score.
* `%bedwars_leaderboard_score_<position>_wins%` - Returns the number of wins.
* `%bedwars_leaderboard_score_<position>_games%` - Returns the total number of games played.
* `%bedwars_leaderboard_score_<position>_kd%` - Returns the kill/death ratio.

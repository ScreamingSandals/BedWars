package org.screamingsandals.bedwars.commands;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import org.screamingsandals.lib.command.CloudConstructor;
import org.screamingsandals.lib.utils.Controllable;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class CommandGenerator {
    public static void init(Controllable controllable) {
        controllable.postEnable(() -> {
            try {
                var manager = CloudConstructor.construct(CommandExecutionCoordinator.simpleCoordinator());

                new AddholoCommand(manager).construct();
                new AdminCommand(manager).construct();
                new AlljoinCommand(manager).construct();
                new AutojoinCommand(manager).construct();
                new DumpCommand(manager).construct();
                new HelpCommand(manager).construct();
                new JoinCommand(manager).construct();
                new LanguageCommand(manager).construct();
                new LeaderboardCommand(manager).construct();
                new LeaveCommand(manager).construct();
                new ListCommand(manager).construct();
                new MainlobbyCommand(manager).construct();
                new PartyCommand(manager).construct();
                new RejoinCommand(manager).construct();
                new ReloadCommand(manager).construct();
                new RemoveholoCommand(manager).construct();
                new StatsCommand(manager).construct();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

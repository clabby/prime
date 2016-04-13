package me.vexil.prime.data;

import me.vexil.prime.annotations.CommandPermission;
import me.vexil.prime.handler.PrimeExecutor;
import org.bukkit.command.CommandExecutor;

public class CommandInformation {

    private String[] aliases;
    private String desc, usage;
    private int minArgs, maxArgs;
    private PrimeExecutor commandExecutor;
    private String permission;

    public CommandInformation(String[] aliases, String desc, int minArgs, int maxArgs, String usage, PrimeExecutor commandExecutor, CommandPermission permission) {
        this.aliases = aliases;
        this.desc = desc;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.usage = usage;
        this.commandExecutor = commandExecutor;
        this.permission = permission == null ? null : permission.value();
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDesc() {
        return desc;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public String getUsage() {
        return usage;
    }

    public PrimeExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public String getPermission() {
        return permission;
    }
}

package me.vexil.prime.handler;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class PrimePermissionHandler implements PermissionHandler {

    @Override
    public boolean hasPermission(CommandSender commandSender, String permission) {
        return commandSender instanceof ConsoleCommandSender || commandSender.isOp() || commandSender.hasPermission(permission);
    }
}

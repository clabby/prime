package me.vexil.prime.handler;

import org.bukkit.command.CommandSender;

public interface PermissionHandler {

    boolean hasPermission(CommandSender commandSender, String permission);
}
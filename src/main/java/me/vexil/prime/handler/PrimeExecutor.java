package me.vexil.prime.handler;

import me.vexil.prime.data.CommandContext;
import me.vexil.prime.exception.CommandException;
import me.vexil.prime.exception.CommandMethodInvocationException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrimeExecutor {

    private Method method;

    public PrimeExecutor(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void execute(CommandContext arguments, CommandSender sender) throws CommandException {
        try {
            method.invoke(null, arguments, sender);
        } catch (IllegalAccessException e) {
            throw new CommandMethodInvocationException(e);
        } catch (InvocationTargetException e) {
            throw e.getCause() != null ? e.getCause() instanceof CommandException ? (CommandException) e.getCause() : new CommandMethodInvocationException(e.getCause()) : new CommandMethodInvocationException(e);
        }
    }
}

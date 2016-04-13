package me.vexil.prime.exception;

public class CommandMethodInvocationException extends CommandException {

    public CommandMethodInvocationException(Throwable throwable) {
        super("Failed to invoke command!", throwable);
    }
}

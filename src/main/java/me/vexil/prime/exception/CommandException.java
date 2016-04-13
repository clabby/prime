package me.vexil.prime.exception;

public class CommandException extends Exception {

    public CommandException(String string) {
        super(string);
    }

    public CommandException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
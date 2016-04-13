package me.vexil.prime.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParentCommand {

    String[] aliases();

    String description();
}

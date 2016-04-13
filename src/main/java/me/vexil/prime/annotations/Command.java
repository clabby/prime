package me.vexil.prime.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String[] aliases();

    String description();

    int minArgs() default 0;

    int maxArgs() default -1;

    String usage() default "";
}
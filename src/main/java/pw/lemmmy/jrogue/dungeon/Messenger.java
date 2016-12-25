package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.utils.Utils;

public interface Messenger {
    void log(String s, Object... objects);

    default void logRandom(String... strings) {
        log(Utils.randomFrom(strings));
    }

    default void The(String s, Object... objects) {
        log("The " + s, objects);
    }

    default void redThe(String s, Object... objects) {
        log("[RED]The " + s, objects);
    }

    default void orangeThe(String s, Object... objects) {
        log("[ORANGE]The " + s, objects);
    }

    default void yellowThe(String s, Object... objects) {
        log("[YELLOW]The " + s, objects);
    }

    default void greenThe(String s, Object... objects) {
        log("[GREEN]The " + s, objects);
    }

    default void You(String s, Object... objects) {
        log("You " + s, objects);
    }

    default void redYou(String s, Object... objects) {
        log("[RED]You " + s, objects);
    }

    default void orangeYou(String s, Object... objects) {
        log("[ORANGE]You " + s, objects);
    }

    default void yellowYou(String s, Object... objects) {
        log("[YELLOW]You " + s, objects);
    }

    default void greenYou(String s, Object... objects) {
        log("[GREEN]You " + s, objects);
    }

    default void Your(String s, Object... objects) {
        log("Your " + s, objects);
    }

    default void redYour(String s, Object... objects) {
        log("[RED]Your " + s, objects);
    }

    default void orangeYour(String s, Object... objects) {
        log("[ORANGE]Your " + s, objects);
    }

    default void yellowYour(String s, Object... objects) {
        log("[YELLOW]Your " + s, objects);
    }

    default void greenYour(String s, Object... objects) {
        log("[GREEN]Your " + s, objects);
    }
}

package ru.ifmo.se.lab6;

import java.util.Optional;

public interface Command {
    default Optional<String> getDescription() {
        return Optional.empty();
    }
    default boolean isReversible() { return false; }
}

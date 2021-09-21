package com.gordonfromblumberg.games.core.common.event;

@FunctionalInterface
public interface EventHandler {
    boolean handle(Event event);
}

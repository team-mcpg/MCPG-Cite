package fr.milekat.MCPG_Cite.frozen;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class FrozenBank extends Event {
    private static final HandlerList handlers = new HandlerList();

    public FrozenBank() {

    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

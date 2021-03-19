package fr.milekat.MCPG_Cite.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class BankUpdate extends Event {
    private static final HandlerList handlers = new HandlerList();

    public BankUpdate() {

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

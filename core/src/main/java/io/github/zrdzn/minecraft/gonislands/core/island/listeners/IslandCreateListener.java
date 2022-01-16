package io.github.zrdzn.minecraft.gonislands.core.island.listeners;

import io.github.zrdzn.minecraft.gonislands.api.event.IslandCreateEvent;
import io.github.zrdzn.minecraft.gonislands.core.message.MessageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandCreateListener implements Listener {

    private final MessageService service;

    public IslandCreateListener(MessageService service) {
        this.service = service;
    }

    @EventHandler
    public void onIslandCreate(IslandCreateEvent event) {
        this.service.sendMessage(event.getExecutorId(), "island.created_successfully");
    }

}

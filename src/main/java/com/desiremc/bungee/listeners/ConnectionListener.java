package com.desiremc.bungee.listeners;

import com.desiremc.bungee.ping.StatusManager;

import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListener implements Listener
{

    @EventHandler
    public void onServerConnect(ServerConnectEvent event)
    {
        if (StatusManager.isOnline(event.getTarget().getName()))
        {
            event.setTarget(StatusManager.getLeastPopulous());
        }
    }

}

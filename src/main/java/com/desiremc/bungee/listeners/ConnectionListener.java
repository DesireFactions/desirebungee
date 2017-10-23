package com.desiremc.bungee.listeners;

import com.desiremc.bungee.DesireBungee;
import com.desiremc.bungee.ping.StatusManager;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
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

    @EventHandler
    public void onKick(ServerKickEvent event)
    {
        String reason = BaseComponent.toLegacyText(event.getKickReasonComponent());
        if (DesireBungee.DEBUG)
        {
            System.out.println("onKick(ServerKickEvent) called with " + reason + ".");
        }
        if (reason.contains("closed"))
        {
            if (DesireBungee.DEBUG)
            {
                System.out.println("onKick(ServerKickEvent) reason contains closed.");
            }
            ServerInfo info = StatusManager.getLeastPopulous();
            if (info.equals(event.getKickedFrom()))
            {
                StatusManager.removeServer(info.getName());
                info = StatusManager.getLeastPopulous();
                if (info == null)
                {
                    return;
                }
            }
            event.setCancelled(true);
            event.setCancelServer(StatusManager.getLeastPopulous());
        }
    }

}

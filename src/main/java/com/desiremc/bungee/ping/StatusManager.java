package com.desiremc.bungee.ping;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.desiremc.bungee.DesireBungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

public class StatusManager
{
    public static Map<String, ServerStatus> servers = new HashMap<>();

    private static ServerPingManager spm = new ServerPingManager();

    public static ServerPingManager getServerPingManager()
    {
        return spm;
    }

    public static boolean isOnline(String server)
    {
        if (servers.containsKey(server))
        {
            return ((ServerStatus) servers.get(server)).isOnline();
        }
        return false;
    }

    public static int getOnlinePlayers(String server)
    {
        if (servers.containsKey(server))
        {
            return ((ServerStatus) servers.get(server)).getOnlinePlayers();
        }
        return 0;
    }

    public static int getMaxPlayers(String server)
    {
        if (servers.containsKey(server))
        {
            return ((ServerStatus) servers.get(server)).getMaxPlayers();
        }
        return 0;
    }

    public static ServerInfo getLeastPopulous()
    {
        if (DesireBungee.DEBUG)
        {
            System.out.println("getLeastPopulous() called.");
        }
        ServerInfo leastPopulatedInfo = null;
        ServerStatus leastPopulatedStatus = null;
        ServerStatus it;
        if (DesireBungee.DEBUG)
        {
            System.out.println("getLeastPopulous() servers size is " + ProxyServer.getInstance().getServers().size());
        }
        for (Entry<String, ServerInfo> server : ProxyServer.getInstance().getServers().entrySet())
        {
            if (DesireBungee.DEBUG)
            {
                System.out.println("getLeastPopulous() checking if " + server.getKey() + " is a lobby.");
            }
            it = servers.get(server.getKey());
            if (it != null && it.isOnline())
            {
                if (DesireBungee.DEBUG)
                {
                    System.out.println("getLeastPopulous() it is a lobby.");
                }
                if (leastPopulatedInfo == null)
                {
                    if (DesireBungee.DEBUG)
                    {
                        System.out.println("getLeastPopulous() it is the least populous lobby.");
                    }
                    leastPopulatedInfo = server.getValue();
                    leastPopulatedStatus = it;
                }
                else if (it.getOnlinePlayers() < leastPopulatedStatus.getOnlinePlayers())
                {
                    if (DesireBungee.DEBUG)
                    {
                        System.out.println("getLeastPopulous() it is the least populous lobby.");
                    }
                    leastPopulatedInfo = server.getValue();
                    leastPopulatedStatus = it;
                }
            }

        }
        if (DesireBungee.DEBUG)
        {
            System.out.println("getLeastPopulous() returned server " + leastPopulatedInfo.getName());
        }
        return leastPopulatedInfo;
    }

    public static ServerStatus getServerStatus(String server)
    {
        if (servers.containsKey(server))
        {
            return (ServerStatus) servers.get(server);
        }
        return null;
    }

    public static void updateStatus(String server, boolean online, int onlinePlayers, int maxPlayers)
    {
        ServerStatus serverStatus = new ServerStatus(server);
        serverStatus.setMaxPlayers(maxPlayers);
        serverStatus.setOnline(online);
        serverStatus.setOnlinePlayers(onlinePlayers);
        servers.put(server, serverStatus);
    }

    public static void startPingTask()
    {
        ProxyServer.getInstance().getScheduler().schedule(DesireBungee.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                if (DesireBungee.DEBUG)
                {
                    System.out.println("Pinging servers in startPingTask()");
                }
                try
                {
                    Configuration cs = DesireBungee.getConfigHandler().getConfigurationSection("hubs");
                    String[] pieces;
                    String ip;
                    int port;
                    for (String hub : cs.getKeys())
                    {
                        pieces = cs.getString(hub).split(":");
                        ip = pieces[0];
                        port = pieces.length == 2 ? Integer.parseInt(pieces[1]) : 25565;
                        try
                        {
                            StatusResponse status = getServerPingManager().getServerInfo(ip, port);
                            StatusManager.updateStatus(hub, true, status.getOnlinePlayers(), status.getMaxPlayers());

                        }
                        catch (Exception localException2)
                        {
                            StatusManager.updateStatus(hub, false, 0, 0);
                        }
                    }
                }
                catch (Exception localException1)
                {
                    localException1.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }
}
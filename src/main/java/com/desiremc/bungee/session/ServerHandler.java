package com.desiremc.bungee.session;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.mongodb.morphia.dao.BasicDAO;

import com.desiremc.bungee.DesireBungee;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerHandler extends BasicDAO<Server, Long>
{
    private static ServerHandler instance;

    private ServerHandler()
    {
        super(Server.class, DesireBungee.getInstance().getMongoWrapper().getDatastore());

        ProxyServer.getInstance().getScheduler().schedule(DesireBungee.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                update();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void update()
    {
        List<Server> servers = find().asList();
        ServerInfo info;
        for (Server server : servers)
        {
            if ((info = ProxyServer.getInstance().getServerInfo(server.getName())) != null)
            {
                info.ping(new Callback<ServerPing>()
                {

                    @Override
                    public void done(ServerPing result, Throwable error)
                    {
                        server.setMaxCount(result.getPlayers().getMax());
                        server.setOnline(result.getPlayers().getOnline());
                        save(server);
                    }
                });
            }
        }
    }

    public static ServerHandler getInstance()
    {
        if (instance == null)
        {
            instance = new ServerHandler();
        }
        return instance;
    }

}

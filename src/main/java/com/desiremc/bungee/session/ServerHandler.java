package com.desiremc.bungee.session;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.mongodb.morphia.dao.BasicDAO;

import com.desiremc.bungee.DesireBungee;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;

public class ServerHandler extends BasicDAO<Server, Long>
{

    public static List<ScheduledTask> tasks = new LinkedList<>();
    private static ServerHandler instance;

    private ServerHandler()
    {
        super(Server.class, DesireBungee.getInstance().getMongoWrapper().getDatastore());

        tasks.add(ProxyServer.getInstance().getScheduler().schedule(DesireBungee.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                update();
                if (tasks.size() > 4)
                {
                    tasks.remove(0);
                }
            }
        }, 0, 2, TimeUnit.SECONDS));

        DesireBungee.getInstance().getMongoWrapper().getMorphia().map(Server.class);
    }

    private void update()
    {
        if (DesireBungee.shuttingDown)
        {
            return;
        }
        List<Server> servers = find().asList();
        ServerInfo info;
        Callback<ServerPing> callback;
        for (Server server : servers)
        {
            if ((info = ProxyServer.getInstance().getServerInfo(server.getName())) != null)
            {
                if (!BungeeCord.getInstance().isRunning)
                {
                    return;
                }
                callback = new Callback<ServerPing>()
                {

                    @Override
                    public void done(ServerPing result, Throwable error)
                    {
                        if (result != null)
                        {
                            server.setMaxCount(result.getPlayers().getMax());
                            server.setOnline(result.getPlayers().getOnline());
                            server.setStatus(true);
                        }
                        else
                        {
                            server.setMaxCount(0);
                            server.setOnline(0);
                            server.setStatus(false);
                        }
                        save(server);
                    }
                };
                ChannelFutureListener listener = new IJustWantToLoopThroughStuffOkay(callback, info);

                new Bootstrap()
                        .channel(PipelineUtils.getChannel())
                        .group(BungeeCord.getInstance().eventLoops)
                        .handler(PipelineUtils.BASE)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .remoteAddress(info.getAddress())
                        .connect()
                        .addListener(listener);
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

    private static class IJustWantToLoopThroughStuffOkay implements ChannelFutureListener
    {
        private Callback<ServerPing> callback;
        private ServerInfo info;

        public IJustWantToLoopThroughStuffOkay(Callback<ServerPing> callback, ServerInfo info)
        {
            this.callback = callback;
            this.info = info;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void operationComplete(ChannelFuture future) throws Exception
        {
            if (future.isSuccess())
            {
                future.channel().pipeline().get(HandlerBoss.class).setHandler(new PingHandler(info, callback, ProxyServer.getInstance().getProtocolVersion()));
            }
            else
            {
                callback.done(null, future.cause());
            }

        }
    }

}

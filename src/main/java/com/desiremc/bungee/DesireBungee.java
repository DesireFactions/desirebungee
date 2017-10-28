package com.desiremc.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.desiremc.bungee.connection.MongoWrapper;
import com.desiremc.bungee.listeners.ConnectionListener;
import com.desiremc.bungee.ping.StatusManager;
import com.desiremc.bungee.session.ServerHandler;
import com.desiremc.bungee.utils.FileHandler;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

public class DesireBungee extends Plugin implements Listener
{

    public static final boolean DEBUG = false;

    private static DesireBungee instance;

    private static FileHandler config;

    private static MongoWrapper mongoWrapper;

    public static boolean shuttingDown;

    @Override
    public void onEnable()
    {
        instance = this;
        config = new FileHandler(saveDefaultConfig());

        ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable()
        {

            @Override
            public void run()
            {
                mongoWrapper = new MongoWrapper();

                ServerHandler.getInstance();
                mongoWrapper.getDatastore().ensureIndexes();

            }
        });

        StatusManager.startPingTask();
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ConnectionListener());
        //ProxyServer.getInstance().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onDisable()
    {
        shuttingDown = true;
    }

    @EventHandler
    public void onCommand(ChatEvent event)
    {
        System.out.println(event.getMessage());
        if (event.isCommand() && !event.isCancelled() && event.getMessage().equals("/end"))
        {
            if ((event.getSender() instanceof UserConnection))
                ProxyServer.getInstance().getScheduler().cancel(this);
            for (ScheduledTask task : ServerHandler.tasks)
            {
                task.cancel();
            }
        }
    }

    public MongoWrapper getMongoWrapper()
    {
        return mongoWrapper;
    }

    private File saveDefaultConfig()
    {
        if (!getDataFolder().exists())
        {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
        {
            try
            {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                        OutputStream os = new FileOutputStream(configFile))
                {
                    ByteStreams.copy(is, os);
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
        return configFile;
    }

    public static FileHandler getConfigHandler()
    {
        return config;
    }

    public static DesireBungee getInstance()
    {
        return instance;
    }

}

package com.desiremc.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.desiremc.bungee.connection.MongoWrapper;
import com.desiremc.bungee.session.ServerHandler;
import com.desiremc.bungee.utils.FileHandler;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;

public class DesireBungee extends Plugin
{

    private static DesireBungee instance;

    private static FileHandler config;

    private static MongoWrapper mongoWrapper;

    @Override
    public void onEnable()
    {
        instance = this;
        config = new FileHandler(saveDefaultConfig());

        mongoWrapper = new MongoWrapper();
        
        ServerHandler.getInstance();
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

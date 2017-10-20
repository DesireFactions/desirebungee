package com.desiremc.bungee.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * @author Michael Ziluck
 */
public class FileHandler
{

    private static List<FileHandler> instances = new LinkedList<>();

    protected File file;

    protected Configuration fileConfig;

    protected HashMap<String, Object> history;

    /**
     * Construct a new optimized file handler.
     *
     * @param file
     */
    public FileHandler(File file)
    {
        instances.add(this);
        history = new HashMap<>();
        this.file = file;
        try
        {
            this.fileConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the {@link}. This resets the file and clears the history object.
     *
     * @param
     */
    public void reload()
    {
        try
        {
            this.fileConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        history.clear();
    }

    /**
     * Gets a formatted string from the config file. Replaces any color
     * placeholders as well. If the string does not exist in the config, returns
     * null.
     *
     * @param key
     * @return the formatted string.
     */
    public String getString(String key)
    {
        key = key.toLowerCase();
        String message = null;
        Object o = history.get(key);
        if (o != null && o instanceof String)
        {
            return (String) o;
        }
        message = fileConfig.getString(key);
        if (message != null)
        {
            message = ChatColor.translateAlternateColorCodes('&', fileConfig.getString(key));
            history.put(key, message);
            return message;
        }
        else
        {
            return key;
        }
    }

    /**
     * Gets a double value from history or the config. If it does not exist,
     * returns 0.
     *
     * @param key
     * @return the value.
     */
    public Double getDouble(String key)
    {
        key = key.toLowerCase();
        double value;
        Object o = history.get(key);
        if (o != null && o instanceof Double)
        {
            return (Double) o;
        }
        value = fileConfig.getDouble(key);
        history.put(key, value);
        return value;
    }

    /**
     * Gets a long value from history or the config. If it does not exist,
     * returns 0.
     *
     * @param key
     * @return the value.
     */
    public Long getLong(String key)
    {
        key = key.toLowerCase();
        long value;
        Object o = history.get(key);
        if (o != null && o instanceof Long)
        {
            return (Long) o;
        }
        value = fileConfig.getLong(key);
        history.put(key, value);
        return value;
    }

    /**
     * Gets a integer value from history or the config. If it does not exist,
     * returns 0.
     *
     * @param key
     * @return the value.
     */
    public Integer getInteger(String key)
    {
        key = key.toLowerCase();
        int value;
        Object o = history.get(key);
        if (o != null && o instanceof Integer)
        {
            return (Integer) o;
        }
        value = fileConfig.getInt(key);
        history.put(key, value);
        return value;
    }

    /**
     * Gets a boolean value from history or the config. If it does not exist,
     * returns 0.
     *
     * @param key
     * @return the value.
     */
    public Boolean getBoolean(String key)
    {
        key = key.toLowerCase();
        boolean value;
        Object o = history.get(key);
        if (o != null && o instanceof Integer)
        {
            return (Boolean) o;
        }
        value = fileConfig.getBoolean(key);
        history.put(key, value);
        return value;
    }

    /**
     * Gets a formatted string list from the config file. Replaces any color
     * placeholders as well. If the string list does not exist in the config,
     * returns null.
     *
     * @param key
     * @return the formatted string list.
     */
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key)
    {
        key = key.toLowerCase();
        Object o = history.get(key);
        if (o != null && o instanceof List<?>)
        {
            return (List<String>) o;
        }
        List<String> list = new LinkedList<>();
        for (String str : fileConfig.getStringList(key))
        {
            list.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        if (list.size() == 0)
        {
            list.add(getString(key));
        }
        history.put(key, list);
        return list;
    }
    
    public Configuration getConfigurationSection(String key)
    {
        key = key.toLowerCase();
        Object o = history.get(key);
        if (o != null && o instanceof Configuration)
        {
            return (Configuration) o;
        }
        Configuration config = fileConfig.getSection(key);
        if (config == null)
        {
            return null;
        }
        history.put(key, config);
        return config;
        
    }

    
}
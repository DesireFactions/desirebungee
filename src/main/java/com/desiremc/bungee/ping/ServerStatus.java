package com.desiremc.bungee.ping;

public class ServerStatus
{
    private String name;
    private int online;
    private int max;
    private boolean isonline;

    public ServerStatus(String paramString)
    {
        this.name = paramString;
    }

    public String getName()
    {
        return this.name;
    }

    public void setOnlinePlayers(int paramInt)
    {
        this.online = paramInt;
    }

    public int getOnlinePlayers()
    {
        return this.online;
    }

    public void setMaxPlayers(int paramInt)
    {
        this.max = paramInt;
    }

    public int getMaxPlayers()
    {
        return this.max;
    }

    public void setOnline(boolean paramBoolean)
    {
        this.isonline = paramBoolean;
    }

    public boolean isOnline()
    {
        return this.isonline;
    }
}
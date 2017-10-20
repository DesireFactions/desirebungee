package com.desiremc.bungee.ping;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoadData
{
    private static final Gson gson = new Gson();

    public int readVarInt(DataInputStream paramDataInputStream) throws IOException
    {
        int i = 0;
        int j = 0;
        int k;
        do
        {
            k = paramDataInputStream.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5)
            {
                throw new RuntimeException("VarInt too big");
            }
        } while ((k & 0x80) == 128);
        return i;
    }

    public void writeVarInt(DataOutputStream paramDataOutputStream, int paramInt) throws IOException
    {
        for (;;)
        {
            if ((paramInt & 0xFFFFFF80) == 0)
            {
                paramDataOutputStream.writeByte(paramInt);
                return;
            }
            paramDataOutputStream.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public StatusResponse fetchData(InetSocketAddress address, int timeout) throws IOException
    {
        Socket socket = new Socket();

        socket.setSoTimeout(timeout);
        socket.connect(address, timeout);

        OutputStream output = socket.getOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(output);

        InputStream input = socket.getInputStream();
        InputStreamReader inputReader = new InputStreamReader(input);

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutputTwo = new DataOutputStream(byteOutput);
        
        dataOutputTwo.writeByte(0);
        writeVarInt(dataOutputTwo, 4);
        writeVarInt(dataOutputTwo, address.getHostString().length());
        dataOutputTwo.writeBytes(address.getHostString());
        dataOutputTwo.writeShort(address.getPort());
        writeVarInt(dataOutputTwo, 1);

        writeVarInt(dataOutput, byteOutput.size());
        dataOutput.write(byteOutput.toByteArray());

        dataOutput.writeByte(1);
        dataOutput.writeByte(0);

        DataInputStream localDataInputStream = new DataInputStream(input);
        readVarInt(localDataInputStream);
        int i = readVarInt(localDataInputStream);
        if (i == -1)
        {
            socket.close();
            throw new IOException("Premature end of stream.");
        }
        if (i != 0)
        {
            socket.close();
            throw new IOException("Invalid packetID");
        }
        int j = readVarInt(localDataInputStream);
        if (j == -1)
        {
            socket.close();
            throw new IOException("Premature end of stream.");
        }
        if (j == 0)
        {
            socket.close();
            throw new IOException("Invalid string length.");
        }
        byte[] arrayOfByte = new byte[j];
        localDataInputStream.readFully(arrayOfByte);

        String str = new String(arrayOfByte, Charset.forName("utf-8"));

        long l = System.currentTimeMillis();
        dataOutput.writeByte(9);
        dataOutput.writeByte(1);
        dataOutput.writeLong(l);

        readVarInt(localDataInputStream);
        i = readVarInt(localDataInputStream);
        if (i == -1)
        {
            socket.close();
            throw new IOException("Premature end of stream.");
        }
        if (i != 1)
        {
            socket.close();
            throw new IOException("Invalid packetID");
        }
        dataOutput.close();
        output.close();
        inputReader.close();
        input.close();
        socket.close();
        try
        {
            return (StatusResponse) gson.fromJson(str, StatusResponse19.class);
        }
        catch (JsonSyntaxException localJsonSyntaxException1)
        {
            try
            {
                return (StatusResponse) gson.fromJson(str, StatusResponse17.class);
            }
            catch (JsonSyntaxException localJsonSyntaxException2)
            {
                return null;
            }
        }
    }
}
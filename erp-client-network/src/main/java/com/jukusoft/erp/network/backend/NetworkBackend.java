package com.jukusoft.erp.network.backend;

import com.jukusoft.erp.network.message.MessageReceiver;
import com.jukusoft.erp.network.utils.Callback;
import com.jukusoft.erp.network.utils.NetworkResult;

public interface NetworkBackend<T> {

    /**
    * try to connect to network server asynchronous
     *
     * @param server ip of server
     * @param port port of server
     * @param callback callback to execute, if connection has established or failed
    */
    public void connect (String server, int port, Callback<NetworkResult<Boolean>> callback);

    public void disconnect ();

    public boolean isConnected ();

    /**
    * send message to server
     *
     * @param msg instance of message
    */
    public void send (T msg);

    /**
    * set message receiver to listen to messages and handle them
     *
     * @param receiver instance of message receiver
    */
    public void setMessageReceiver (MessageReceiver<T> receiver);

    public void executeBlocking (Runnable runnable);

    /**
    * create and start new timer
     *
     * @param time delay time in ms
     * @param runnable runnable to execute
     *
     * @return timer ID
    */
    public long startPeriodicTimer (long time, Runnable runnable);

    /**
    * stop existing timer
     *
     * @param timerID timer ID
    */
    public void stopPeriodicTimer (long timerID);

    /**
    * execute runnable after an given time
     *
     * @param time delay time in ms
     * @param runnable runnable to execute
     *
     * @return timer ID
    */
    public long executeDelayed (long time, Runnable runnable);

    /**
    * shutdown network backend
    */
    public void shutdown ();

}

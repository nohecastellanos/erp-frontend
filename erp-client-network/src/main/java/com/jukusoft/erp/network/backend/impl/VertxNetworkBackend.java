package com.jukusoft.erp.network.backend.impl;

import com.jukusoft.erp.network.backend.NetworkBackend;
import com.jukusoft.erp.network.message.MessageReceiver;
import com.jukusoft.erp.network.utils.Callback;
import com.jukusoft.erp.network.utils.NetworkResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.atomic.AtomicBoolean;

public class VertxNetworkBackend implements NetworkBackend<String> {

    //vertx options
    protected VertxOptions options = null;

    //instance of vertx
    protected Vertx vertx = null;

    /**
     * network client options
     */
    protected NetClientOptions netClientOptions = new NetClientOptions();

    /**
     * network client
     */
    protected NetClient netClient = null;

    /**
     * network socket
     */
    protected NetSocket socket = null;

    //message receiver
    protected MessageReceiver<String> messageReceiver = null;

    protected AtomicBoolean connected = new AtomicBoolean(false);

    public VertxNetworkBackend () {
        this.options = new VertxOptions();

        //set connection timeout of 1 seconds
        this.netClientOptions.setConnectTimeout(1000);

        //if connection fails, try 5x
        this.netClientOptions.setReconnectAttempts(5)
                .setReconnectInterval(500);
    }

    @Override
    public void connect(String server, int port, Callback<NetworkResult<Boolean>> callback) {
        if (this.messageReceiver == null) {
            throw new IllegalStateException("You have to set an message receiver first.");
        }

        if (this.vertx == null) {
            //create new vertx.io instance
            this.vertx = Vertx.vertx(this.options);
        }

        //create new network client
        this.netClient = this.vertx.createNetClient(this.netClientOptions);

        System.out.println("try to connect to server: " + server + ":" + port);

        //connect to server
        this.netClient.connect(port, server, res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                this.socket = res.result();

                //initialize socket
                initSocket(socket);

                //set flag
                connected.set(true);

                callback.handle(NetworkResult.complete(true));
            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());

                //set flag
                connected.set(false);

                callback.handle(NetworkResult.fail(res.cause()));
            }
        });
    }

    @Override
    public void disconnect() {
        if (!this.connected.get()) {
            throw new IllegalStateException("Cannot disconnect, because client isnt connected.");
        }

        //close socket
        this.socket.close();
    }

    @Override
    public boolean isConnected() {
        return this.connected.get();
    }

    protected void initSocket (NetSocket socket) {
        //set connection close handler
        socket.closeHandler(res -> {
            System.err.println("Server connection was closed by server.");

            //reset flag
            this.connected.set(false);

            System.exit(0);
        });

        //add message handler
        /*socket.handler(buffer -> {
            //convert to string and json object
            String str = buffer.toString(StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(str);

            //System.out.println("message received: " + str);

            //convert to chat message
            ChatMessage msg = ChatMessage.create(json);

            //call message receiver
            messageReceiver.messageReceived(msg);
        });*/
    }

    @Override
    public void send(String msg) {

    }

    @Override
    public void setMessageReceiver(MessageReceiver<String> receiver) {
        this.messageReceiver = receiver;
    }

}
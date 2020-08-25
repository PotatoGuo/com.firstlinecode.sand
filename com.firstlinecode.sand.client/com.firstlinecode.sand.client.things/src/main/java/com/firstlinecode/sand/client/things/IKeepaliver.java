package com.firstlinecode.sand.client.things;

import com.firstlinecode.chalk.IParsingListener;
import com.firstlinecode.chalk.network.IConnectionListener;

public interface IKeepaliver extends IConnectionListener, IParsingListener {
    void start();
    void stop();
    boolean isAlive();
}

package com.github.sdnwiselab.sdnwise.cooja;

import com.sun.net.httpserver.HttpHandler;

/**
 * Created by Behnam Khazael on 2/13/2021.
 * Interface to implement by simulated sensors.
 * @author Behnam Khazael
 * @version 0.1
 */
public interface SensorInterface extends HttpHandler {
    public boolean simulator_turnOn();

    public boolean simulator_turnOff();

    public void simulator_sendLocation();
}

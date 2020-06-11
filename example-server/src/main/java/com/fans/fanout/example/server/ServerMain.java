package com.fans.fanout.example.server;

import com.fans.fanout.server.Server;
import com.fans.fanout.server.Starter;
import com.fans.fanout.server.XmlStarter;


/**
 * @author ：fsp
 * @date ：2020/6/8 15:45
 */
public class ServerMain {

    public static void main(String[] args) {
        try {
            Starter starter = new XmlStarter(ServerMain.class.getClassLoader().getResource("fanoutService.xml")
                    .openStream());
            new Server(starter).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

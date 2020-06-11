package com.fans.fanout.example.client;

import com.fans.fanout.client.Client;
import com.fans.fanout.client.EndPoints;
import com.fans.fanout.client.config.ClientConfig;
import com.fans.fanout.example.HelloRequest;
import com.fans.fanout.example.HelloService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * @author ：fsp
 * @date ：2020/6/9 15:42
 */
public class ClientMain {

    public static void main(String[] args) {

        List<EndPoints.EndPoint> endpointList = new ArrayList();
        endpointList.add(new EndPoints().new EndPoint("127.0.0.1", 18600));
        EndPoints endPoints = new EndPoints("HelloService", endpointList);
        Client client = new Client(new ClientConfig());

        HelloService proxy = client.getProxy(HelloService.class, endPoints);
        groupInvoke(proxy);
    }

    private static void groupInvoke(HelloService serviceProxy) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test1");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test2");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test3");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 3));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test4");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 4));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test5");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 5));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test6");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 6));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test7");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 7));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test8");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test9");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 9));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                HelloRequest request = new HelloRequest("test10");
                cyclicBarrier.await();
                System.out.println(serviceProxy.hello(request, 10));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}

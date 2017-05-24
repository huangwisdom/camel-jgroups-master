package it.redhat;


import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.locking.LockService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;

public class Main extends ReceiverAdapter {

    JChannel channel;

    public static void main(String[] args) throws Exception {
        new Main().start();
    }

    public void start() throws Exception {
        channel = new JChannel("locking.xml").setReceiver(this);
        LockService lockService = new LockService(channel);
        channel.connect("jgroups-master");

        Lock lock = lockService.getLock("mylock"); // gets a cluster-wide lock

        System.out.println("starting");
        //Thread.sleep(10000);
        eventLoop(lock);
        channel.close();
    }

    private void eventLoop(Lock lock) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;

                if (line.startsWith("lock")) {
                    lock.lock();
                    System.out.println("Acquired lock");
                    try {
                        Thread.sleep(10000);
                    } finally {
                        lock.unlock();
                        System.out.println("Released lock");
                    }
                } else {
                    line = ">" + line;
                    Message msg = new Message(null, line);
                    channel.send(msg);
                }
            } catch (Exception e) {
            }
        }
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }
}

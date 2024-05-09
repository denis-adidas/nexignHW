package ru.babybilling.generator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloRunnable implements Runnable {
    private boolean doStop = false;

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    @Override
    public void run() {
        System.out.println("Running");
        int i = 0;
        while (keepRunning()) {
            System.out.println(i);
            i++;
            if (i > 5) {
                this.doStop();
            }
        }

    }

    public static void main(String args[]) {

        ExecutorService service = Executors.newFixedThreadPool(5);
        service.submit(new HelloRunnable());
        service.submit(new HelloRunnable());
        service.submit(new HelloRunnable());
        service.submit(new HelloRunnable());
        service.submit(new HelloRunnable());
    }
}

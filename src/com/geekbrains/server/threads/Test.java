package com.geekbrains.server.threads;

/*1. Создать три потока, каждый из которых выводит
определенную букву (A, B и C) 5 раз (порядок – ABСABСABС).
 Используйте wait/notify/notifyAll.
2. На серверной стороне сетевого чата реализовать
управление потоками через ExecutorService.*/
public class Test {
    static Object lock = new Object();
    static volatile String c = "C";

    public void StrABC(String[] args) throws InterruptedException {

        Runnable rA = new Runnable() {
            @Override
            public void run() {
                try {
                    int i =0;
                    while (true) {
                        synchronized (lock) {
                            while (c != "C") lock.wait();
                            c = "A";
                            System.out.print(c);
                            i ++;
                            while ( i >= 5 ) lock.wait();
                            lock.notifyAll();
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " прерван.");
                }
                System.out.println(Thread.currentThread().getName() + " завершен.");
            }
        };

        Runnable rB = new Runnable(){
            @Override
            public void run() {
                try {
                    int i =0;
                    while (true) {
                        synchronized (lock) {
                            while (c != "A") lock.wait();
                            c = "B";
                            System.out.print(c);
                            i ++;
                            while ( i >= 5 ) lock.wait();
                            lock.notifyAll();
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " прерван.");
                }
                System.out.println(Thread.currentThread().getName() + " завершен.");
            }
        };

        Runnable rC = new Runnable(){
            @Override
            public void run() {
                try {
                    int i =0;
                    while (true) {
                        synchronized (lock) {
                            while (c != "B") lock.wait();
                            c = "C";
                            System.out.print(c);
                            i ++;
                            while ( i >= 5 ) lock.wait();
                            lock.notifyAll();
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " прерван.");
                }
                System.out.println(Thread.currentThread().getName() + " завершен.");
            }
        };

//        Thread trC = new Thread(rC);
//        Thread trB = new Thread(rB);
//        Thread trA = new Thread(rA);

//        trA.start();
//        trB.start();
//        trC.start();
//
//        Thread.sleep(1000);
//
//        trA.interrupt();
//        trB.interrupt();
//        trC.interrupt();
    }

}

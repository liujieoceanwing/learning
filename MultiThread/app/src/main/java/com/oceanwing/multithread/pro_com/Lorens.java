package com.oceanwing.multithread.pro_com;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LJ.Liu
 * @date 2018/11/14.
 */
public class Lorens {

    static final int MAX = 10;
    static int count = 0;
    static Lock lock = new ReentrantLock();
    static Condition full = lock.newCondition();
    static Condition empty = lock.newCondition();
    static List<String> dataList = new LinkedList<>();
    static Queue<String> stringQueue = new ArrayDeque<>();

    public static void main(String[] args) {
        CusThread cusThread1 = new CusThread("cus1");
        CusThread cusThread2 = new CusThread("cus2");
        CusThread cusThread3 = new CusThread("cus3");
        CusThread cusThread4 = new CusThread("cus4");
        ProThread proThread1 = new ProThread("pro1");
        ProThread proThread2 = new ProThread("pro2");
        ProThread proThread3 = new ProThread("pro3");
        ProThread proThread4 = new ProThread("pro4");
        cusThread1.start();
        proThread1.start();
//        proThread2.start();
//        cusThread2.start();
//        cusThread3.start();
//        proThread3.start();
//        proThread4.start();
//        cusThread4.start();
    }

    public static class CusThread extends Thread {

        private final String name;

        public CusThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    lock.lock();
                    while (stringQueue.size() == 0) {
                        empty.await();
                    }
//                    System.out.print("\n sleep " + name + ":" + count);
                    String str = stringQueue.poll();
                    count = stringQueue.size();
                    System.out.print("\nThreadName:" + name + ", currentCount: " + count + " take <== " + str);
                    full.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static class ProThread extends Thread {

        private final String name;

        public ProThread(String name) {
            this.name = name;
        }


        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    lock.lock();
                    while (stringQueue.size() == 10) {
                        full.await();
                    }
                    String str = name + ":" + count;
                    stringQueue.add(str);
                    count = stringQueue.size();
                    System.out.print("\nThreadName:" + name + " currentCount:" + count + " put ==> " + str);
                    empty.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}

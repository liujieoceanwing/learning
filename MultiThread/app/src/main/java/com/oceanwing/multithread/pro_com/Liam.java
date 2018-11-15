package com.oceanwing.multithread.pro_com;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LJ.Liu
 * @date 2018/11/14.
 */
public class Liam {

    static final int MAX = 10;
    static int count = 0;
    static Lock lock = new ReentrantLock();
    static Condition full = lock.newCondition();
    static Condition empty = lock.newCondition();
    static List<String> dataList = new LinkedList<>();

    public static void main(String[] args) {
        CusThread cusThread = new CusThread();
        ProThread proThread = new ProThread();
        cusThread.start();
        proThread.start();
    }

    public static class CusThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                try {
                    if (count == 0) {
                        empty.await();
                    }
                    count--;

                    System.out.print("\ncus:" + dataList.get(0)+ ",threadId:" + Thread.currentThread().getId());
                    dataList.remove(0);
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
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                try {

                    if (MAX == count) {
                        full.await();
                    }

                    count++;
                    String data = "a"+count;
                    System.out.print("\npro:" + data + ",threadId:" + Thread.currentThread().getId());
                    dataList.add(data);
                    empty.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}

package com.oceanwing.multithread.pro_com;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LJ.Liu
 * @date 2018/11/14.
 */
public class Boke {

    public static final void main(String... args) {
        BoundedBuffer boundedBuffer = new BoundedBuffer();
        Thread pro1 = new ProThread(boundedBuffer);

        Thread com1 = new ComThread(boundedBuffer);

        pro1.start();
        com1.start();
    }

    private static class ProThread extends Thread {

        private BoundedBuffer mBoundedBuffer;

        private int i;

        public ProThread(BoundedBuffer boundedBuffer) {
            mBoundedBuffer = boundedBuffer;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    mBoundedBuffer.put(i++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ComThread extends Thread {

        private BoundedBuffer mBoundedBuffer;

        public ComThread(BoundedBuffer boundedBuffer) {
            mBoundedBuffer = boundedBuffer;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    mBoundedBuffer.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class BoundedBuffer {
        final Lock lock = new ReentrantLock();
        final Condition notFull = lock.newCondition();
        final Condition notEmpty = lock.newCondition();

        final int[] items = new int[9];
        int putptr, takeptr, count;

        public void put(int x) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length)
                    notFull.await();
                items[putptr] = x;
                System.out.println("put:" + x);
//                Thread.sleep(1000);
                if (++putptr == items.length) putptr = 0;
                ++count;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public Object take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0)
                    notEmpty.await();
                int x = items[takeptr];
                System.out.println("take:" + x);
//                Thread.sleep(2000);
                if (++takeptr == items.length) takeptr = 0;
                --count;
                notFull.signal();
                return x;
            } finally {
                lock.unlock();
            }
        }
    }
}

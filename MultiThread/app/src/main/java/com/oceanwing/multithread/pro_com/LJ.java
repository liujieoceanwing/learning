package com.oceanwing.multithread.pro_com;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LJ.Liu
 * @date 2018/11/14.
 */
public class LJ {

    public static final void main(String... args) {
        Buffer buffer = new Buffer();
        BoundedBuffer boundedBuffer = new BoundedBuffer();
//        Thread pro1 = new ProThread(boundedBuffer);
        Thread pro1 = new ProThread(buffer);
//        Thread pro2 = new ProThread(buffer);
//        Thread pro3 = new ProThread(buffer);

//        Thread com1 = new ComThread(boundedBuffer);
        Thread com1 = new ComThread(buffer);
//        Thread com2 = new ComThread(buffer);
//        Thread com3 = new ComThread(buffer);

        pro1.start();
//        pro2.start();
//        pro3.start();
        com1.start();
//        com2.start();
//        com3.start();


    }

    private static class ProThread extends Thread {

        private BoundedBuffer mBoundedBuffer;

        private Buffer mBuffer;
        private int i;

        public ProThread(Buffer buffer) {
            mBuffer = buffer;
        }

        public ProThread(BoundedBuffer boundedBuffer) {
            mBoundedBuffer = boundedBuffer;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    mBuffer.put(i++);
//                    mBoundedBuffer.put(i++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ComThread extends Thread {

        private BoundedBuffer mBoundedBuffer;

        private Buffer mBuffer;

        public ComThread(Buffer buffer) {
            mBuffer = buffer;
        }

        public ComThread(BoundedBuffer boundedBuffer) {
            mBoundedBuffer = boundedBuffer;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    mBuffer.take();
//                    mBoundedBuffer.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class Buffer {

        private static final String TAG = "Buffer";

        private Lock takeLock = new ReentrantLock();
        private Lock putLock = new ReentrantLock();
        private Condition takeCondition = takeLock.newCondition();
        private Condition putCondition = putLock.newCondition();
        private int[] items = new int[10];
        private int putPtr, takePtr, count;


        public void put(int i) throws InterruptedException {
            try {
                putLock.lock();
                System.out.println("put lock");
                while (count == items.length) {
                    System.out.println("put await");
                    putCondition.await();
                }
                items[putPtr] = i;
                count++;
                processPut(i);
                putPtr++;
                if (putPtr == items.length) {
                    putPtr = 0;
                }
                if (count == 1) {
                    takeCondition.signal();
                }
            } finally {
                System.out.println("put unlock");
                putLock.unlock();
            }
        }

        public int take() throws InterruptedException {
            int i;
            try {
                takeLock.lock();
                System.out.println("take lock");
                while (count == 0) {
                    System.out.println("take await");
                    takeCondition.await();
                }

                i = items[takePtr];
                count--;
                processTake(i);
                takePtr++;
                if (takePtr == items.length) {
                    takePtr = 0;
                }
                if (count == items.length - 1) {
                    putCondition.signal();
                }
            } finally {
                System.out.println("take unlock");
                takeLock.unlock();
            }
            return i;
        }

        private void processTake(int i) {
            System.out.println("take:" + i);
        }

        private void processPut(int i) {
            System.out.println("put:" + i);

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

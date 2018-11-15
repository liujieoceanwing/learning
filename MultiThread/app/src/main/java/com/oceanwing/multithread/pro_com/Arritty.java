package com.oceanwing.multithread.pro_com;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LJ.Liu
 * @date 2018/11/14.
 */
public class Arritty {

    public static void main(String[] args){
        test();
    }


    private static Lock lock = new ReentrantLock();
    static Condition full = lock.newCondition();
    static int count =1;
    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName()+" run start");
                lock.lock();
                System.out.println(Thread.currentThread().getName()+" get1 lock");

                lock.lock();
                System.out.println(Thread.currentThread().getName()+" get2 lock");
//                Thread.sleep(10000);
                while (count ==1){
                    count =2;
                    full.await();


                }

                System.out.println(Thread.currentThread().getName()+" finished");
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName()+" interrupted");
            }finally {
                System.out.println(Thread.currentThread().getName()+" finally");
                lock.unlock();
                lock.unlock();
            }
        }
    };
    private  static void test(){
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
    }
}

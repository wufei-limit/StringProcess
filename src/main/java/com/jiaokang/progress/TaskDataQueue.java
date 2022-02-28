package com.jiaokang.progress;

import com.jiaokang.progress.entities.StringLocalData;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiaokang on 2022/2/25
 */
public class TaskDataQueue {
    private static final LinkedBlockingQueue<StringLocalData> queue = new LinkedBlockingQueue<>();
    public static void putData(StringLocalData data){
        queue.offer(data);
    }

    public static boolean isEmpty(){
        return queue.isEmpty();
    }

    public static StringLocalData getData() throws InterruptedException {
        return queue.poll(5, TimeUnit.SECONDS);
    }

    public static int getSize() {
        return queue.size();
    }
}

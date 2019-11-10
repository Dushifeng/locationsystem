package cn.lovezsm.locationsystem.base.data;


import cn.lovezsm.locationsystem.base.bean.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class DataCache {

    private static ScheduledExecutorService swapExpiredPool = new ScheduledThreadPoolExecutor(5);

    private ReentrantLock lock = new ReentrantLock();

//    private ConcurrentHashMap<Long,Message> cache = new ConcurrentHashMap<>(1024);

    /**
     * 让过期时间最小的数据排在队列前，在清除过期数据时
     * ，只需查看缓存最近的过期数据，而不用扫描全部缓存
     */
    private PriorityQueue<Node> expireQueue = new PriorityQueue<>(1024);

    public DataCache() {
        //使用默认的线程池，每5秒清除一次过期数据
        //线程池和调用频率 最好是交给调用者去设置。
        swapExpiredPool.scheduleWithFixedDelay(new SwapExpiredNodeWork(), 1, 1, TimeUnit.SECONDS);
    }

    /**
     *
     * @param value
     * @param ttl 毫秒数
     * @return
     */
    public void add(Message value, long ttl) {
        Assert.isTrue(ttl > 0, "ttl must greater than 0");
        long expireTime = System.currentTimeMillis() + ttl;
        Node newNode = new Node(value, expireTime);
        lock.lock();
        try {
            expireQueue.add(newNode);
        } finally {
            lock.unlock();
        }
    }

    public void addAll(Collection collection, long ttl){
        Assert.isTrue(ttl > 0, "ttl must greater than 0");
        long expireTime = System.currentTimeMillis() + ttl;
        Iterator iterator = collection.iterator();
        List<Node> nodeList = new ArrayList<>();
        while (iterator.hasNext()){
            Message value = (Message) iterator.next();
            Node newNode = new Node(value, expireTime);
            nodeList.add(newNode);
        }
        lock.lock();
        try {
            expireQueue.addAll(nodeList);
        }finally {
            lock.unlock();
        }
    }

    public List<Message> getAll(){
        List<Message> ans = new ArrayList<>();
        lock.lock();
        try {
            Iterator<Node> iterator = expireQueue.iterator();
            while (iterator.hasNext()){
                ans.add(iterator.next().value);
            }
        }finally {
            lock.unlock();
        }
        return ans;
    }


    private static class Node implements Comparable<Node> {
        private Message value;
        private long expireTime;

        public Node(Message value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        @Override
        public int compareTo(Node o) {
            long r = this.expireTime - o.expireTime;
            if (r > 0) {
                return 1;
            }
            if (r < 0) {
                return -1;
            }
            return 0;
        }

    }

    /**
     * 删除已经过期的数据
     */
    private class SwapExpiredNodeWork implements Runnable {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            while (true){
                lock.lock();
                try {
                    Node node = expireQueue.peek();
                    if (node == null || node.expireTime>now){
                        return;
                    }
                    expireQueue.poll();
                }finally {
                    lock.unlock();
                }
            }
        }
    }
}

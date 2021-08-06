package utils.api.eventbase;

import utils.api.SemaphoreBase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

// thread safe class
@SuppressWarnings("unused")
public class ThreadManager<K> implements SemaphoreBase<K> {
    private final Map<K, Semaphore> semaphoreMap;
    private final Map<K, Runnable> runnableMap;
    private final Map<K, Integer> runnableExecutionCount;
    private final ExecutorService executor;
    private final Object mutex;

    public ThreadManager(ExecutorService executor) {
        semaphoreMap = new HashMap<>();
        runnableMap = new HashMap<>();
        this.executor = executor;
        runnableExecutionCount = new HashMap<>();
        mutex = new Object();
    }

    public ThreadManager() {
        this(Executors.newFixedThreadPool(10));
    }

    public void execute(K key) {
        synchronized (mutex) {
            runnableExecutionCount.put(key, runnableExecutionCount.getOrDefault(key, 0) + 1);
            executor.execute(runnableMap.get(key));
        }
    }

    public void execute(K key, Runnable runnable) {
        synchronized (mutex) {
            runnableExecutionCount.put(key, runnableExecutionCount.getOrDefault(key, 0) + 1);
            runnableMap.put(key, runnable);
            executor.execute(runnable);
        }
    }

    public void addRunnable(K key, Runnable runnable) {
        synchronized (mutex) {
            runnableExecutionCount.put(key, runnableExecutionCount.getOrDefault(key, 0));
            runnableMap.put(key, runnable);
        }
    }

    public void removeRunnable(K key) {
        synchronized (mutex) {
            runnableMap.remove(key);
        }
    }

    public Class<? extends ExecutorService> getExecutorClass() {
        return executor.getClass();
    }

    public int getRunnableExecutionCount(K key) {
        synchronized (mutex) {
            return runnableExecutionCount.get(key);
        }
    }

    public Map<K, Runnable> getRunnableMap() {
        synchronized (mutex) {
            return Map.copyOf(runnableMap);
        }
    }

    public void terminate() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isTerminated() {
        return executor.isTerminated();
    }

    public boolean isNewRunnableAcceptable() {
        return executor.isShutdown();
    }

    @Override
    public Map<K, Semaphore> getSemaphoreMap() {
        return semaphoreMap;
    }
}

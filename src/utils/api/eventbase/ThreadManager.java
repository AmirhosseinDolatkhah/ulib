package utils.api.eventbase;

import utils.api.SemaphoreBase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

// thread safe class
@SuppressWarnings("unused")
public class ThreadManager<K> implements SemaphoreBase<K> {
    private final Map<K, Semaphore> semaphoreMap;
    private final Map<K, Callable<?>> callableMap;
    private final Map<K, Runnable> runnableMap;
    private final Map<K, Integer> taskExecutionCount;
    private final Map<K, Future<?>> results;
    private ExecutorService executor;
    private final Object mutex;

    public ThreadManager(ExecutorService executor) {
        semaphoreMap = new HashMap<>();
        callableMap = new HashMap<>();
        taskExecutionCount = new HashMap<>();
        results = new HashMap<>();
        runnableMap = new HashMap<>();
        mutex = new Object();
        this.executor = executor;
    }

    public ThreadManager(int poolSize) {
        this(Executors.newFixedThreadPool(poolSize));
    }

    public void execute(K key) {
        synchronized (mutex) {
            taskExecutionCount.put(key, taskExecutionCount.getOrDefault(key, 0) + 1);
            if (callableMap.containsKey(key)) {
                results.put(key, executor.submit(callableMap.get(key)));
            } else if (runnableMap.containsKey(key)) {
                results.put(key, executor.submit(runnableMap.get(key)));
            }
        }
    }

    public void execute(K key, Callable<?> callable) {
        //noinspection DuplicatedCode
        synchronized (mutex) {
            if (callableMap.containsKey(key) || runnableMap.containsKey(key))
                throw new IllegalArgumentException("Duplicated key. Cannot add or replace tasks with the same key.");
            taskExecutionCount.put(key, taskExecutionCount.get(key) + 1);
            callableMap.put(key, callable);
            results.put(key, executor.submit(callable));
        }
    }

    public void execute(K key, Runnable runnable) {
        //noinspection DuplicatedCode
        synchronized (mutex) {
            if (callableMap.containsKey(key) || runnableMap.containsKey(key))
                throw new IllegalArgumentException("Duplicated key. Cannot add or replace tasks with the same key.");
            taskExecutionCount.put(key, taskExecutionCount.get(key) + 1);
            runnableMap.put(key, runnable);
            results.put(key, executor.submit(runnable));
        }
    }

    public void addTask(K key, Callable<?> callable) {
        synchronized (mutex) {
            if (callableMap.containsKey(key) || runnableMap.containsKey(key))
                throw new IllegalArgumentException("Duplicated key. Cannot add or replace tasks with the same key.");
            taskExecutionCount.put(key, 0);
            callableMap.put(key, callable);
        }
    }

    public void addTask(K key, Runnable runnable) {
        synchronized (mutex) {
            if (callableMap.containsKey(key) || runnableMap.containsKey(key))
                throw new IllegalArgumentException("Duplicated key. Cannot add or replace tasks with the same key.");
            taskExecutionCount.put(key, 0);
            runnableMap.put(key, runnable);
        }
    }

    public void removeTask(K key) {
        synchronized (mutex) {
            callableMap.remove(key);
            runnableMap.remove(key);
        }
    }

    public Class<? extends ExecutorService> getExecutorClass() {
        synchronized (mutex) {
            return executor.getClass();
        }
    }

    public void setExecutor(ExecutorService executor) {
        synchronized (mutex) {
            terminate();
            this.executor = executor;
        }
    }

    public int getRunnableExecutionCount(K key) {
        synchronized (mutex) {
            return taskExecutionCount.get(key);
        }
    }

    public Map<K, Callable<?>> getCallableMap() {
        synchronized (mutex) {
            return Map.copyOf(callableMap);
        }
    }

    public void terminate() {
        synchronized (mutex) {
            if (executor.isTerminated())
                return;
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
    }

    public boolean isTerminated() {
        synchronized (mutex) {
            return executor.isTerminated();
        }
    }

    public boolean isNewRunnableAcceptable() {
        synchronized (mutex) {
            return executor.isShutdown();
        }
    }

    public Future<?> getResult(K key) {
        synchronized (mutex) {
            return results.get(key);
        }
    }

    public int unfinishedTaskCount() {
        synchronized (mutex) {
            return (int) results.values().stream().filter(Future::isDone).count();
        }
    }

    public boolean isAllTaskDone() {
        synchronized (mutex) {
            return results.values().stream().allMatch(Future::isDone);
        }
    }

    public boolean isTaskDone(K key) {
        synchronized (mutex) {
            return results.get(key).isDone();
        }
    }

    @Override
    public Map<K, Semaphore> getSemaphoreMap() {
        synchronized (mutex) {
            return semaphoreMap;
        }
    }
}

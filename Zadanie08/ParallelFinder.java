import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelFinder implements Finder {
    private ThreadsFactory myThreadFactory;
    private Array myArray;
    private final List<Integer> positions = Collections.synchronizedList(new ArrayList<>());
    private final BlockingQueue<Integer> writeQueue = new LinkedBlockingQueue<>();
    private volatile int readersDone = 0;
    private volatile boolean writerDone = false;
    private volatile int leftIndex = 0;
    private volatile int rightIndex;
    private final Object lock = new Object();

    @Override
    public void setThreadsFactory(ThreadsFactory factory) {
        this.myThreadFactory = factory;
    }

    @Override
    public void setArray(Array array) {
        this.myArray = array;
    }

    @Override
    public List<Integer> start(int value) {
        if (myArray == null || myThreadFactory == null || myArray.size() == 0) {
            return List.of();
        }

        // Reset state
        positions.clear();
        writeQueue.clear();
        readersDone = 0;
        writerDone = false;
        leftIndex = 0;
        rightIndex = myArray.size() - 1;

        // Create and start reader threads
        Thread leftReader = myThreadFactory.leftReadOnlyThread(() -> {
            while (true) {
                int currentIndex;
                synchronized (lock) {
                    if (leftIndex > rightIndex) break;
                    currentIndex = leftIndex++;
                }
                if (myArray.get(currentIndex) == value) {
                    positions.add(currentIndex);
                    try {
                        writeQueue.put(currentIndex);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            synchronized (lock) {
                readersDone++;
                lock.notifyAll();
            }
        });

        Thread rightReader = myThreadFactory.rightReadOnlyThread(() -> {
            while (true) {
                int currentIndex;
                synchronized (lock) {
                    if (leftIndex > rightIndex) break;
                    currentIndex = rightIndex--;
                }
                if (myArray.get(currentIndex) == value) {
                    positions.add(currentIndex);
                    try {
                        writeQueue.put(currentIndex);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            synchronized (lock) {
                readersDone++;
                lock.notifyAll();
            }
        });

        // Create and start writer thread
        Thread writer = myThreadFactory.writeOnlyThread(() -> {
            while (!writerDone) {
                try {
                    Integer pos = writeQueue.poll();
                    if (pos != null) {
                        myArray.set0(pos);
                    } else if (readersDone < 2) {
                        Thread.sleep(1);
                    } else {
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            synchronized (lock) {
                writerDone = true;
                lock.notifyAll();
            }
        });

        leftReader.start();
        rightReader.start();
        writer.start();

        // Wait for all threads to complete
        try {
            leftReader.join();
            rightReader.join();
            writer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        }

        return new ArrayList<>(positions);
    }
}

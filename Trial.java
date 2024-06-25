import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Trial {

    public static void main(String[] args) {
        AtomicInteger boothOne = new AtomicInteger(0);
        AtomicInteger boothTwo = new AtomicInteger(10);

        boothOne.getAndSet(boothTwo.get());
        boothTwo.set(0);

        // System.out.println(boothOne.get());
        // System.out.println(boothTwo.get());


        BlockingQueue<Integer> QueueOne = new ArrayBlockingQueue<Integer>(3);
        BlockingQueue<Integer> QueueTwo = new ArrayBlockingQueue<Integer>(3);
        QueueOne.add(1);
        QueueOne.add(2);
        QueueOne.add(3);
        QueueOne.drainTo(QueueTwo);
        System.out.println(QueueOne.size());
        for(int x: QueueTwo){
            System.out.println(x);
        }
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("The task is running!");
            }
        }, 2, TimeUnit.SECONDS);

        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            executorService.shutdown();
        } catch (Exception e) {
            // TODO: handle exception
        }

        {System.out.println("Hello kmmke");}
        

        
    }
    
}

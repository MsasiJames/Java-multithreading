import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BathRoomBreakTwo extends MainSimulation{
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public void scheduleTask(Runnable task, long delay,TimeUnit unit) {
        executorService.schedule(task, delay, unit);
    }

    public void bathRoomAction(){
                System.out.println("TICKET STAFF 1 WENT TO BATHROOM!!!");
                System.out.println("CUSTOMERS FROM BOOTH ONE GO TO BOOTH TWO");
                TicketStaffOneAvailable = false;
                boothOne.drainTo(boothTwo);
                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}  // simulate break
                System.out.println("TICKET STAFF 1 IS BACK!!!");
        }

    public void shutdown() {
        TicketStaffOneAvailable = true;
        try {executorService.awaitTermination(9, TimeUnit.SECONDS);} catch (InterruptedException e) {}
        executorService.shutdown();
    }
}

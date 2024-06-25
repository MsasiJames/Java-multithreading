import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BathRoomBreak extends MainSimulation{
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public void scheduleTask(Runnable task, long delay,TimeUnit unit) {
        executorService.schedule(task, delay, unit);
    }

    public void bathRoomAction(){
            int ticketStaffChoice = ThreadLocalRandom.current().nextInt(2);
            if(ticketStaffChoice == 0){
                System.out.println("TICKET STAFF 1 WENT TO BATHROOM!!!");
                System.out.println("CUSTOMERS FROM BOOTH ONE GO TO BOOTH TWO");
                TicketStaffOneAvailable = false;
                boothOne.drainTo(boothTwo);
                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}  // simulate break
                System.out.println("TICKET STAFF 1 IS BACK!!!");
            }else{
                System.out.println("TICKET STAFF 2 WENT TO BATHROOM!!!");
                System.out.println("CUSTOMERS FROM BOOTH TWO GO TO BOOTH ONE");
                TicketStaffTwoAvailable = false;
                boothTwo.drainTo(boothOne);
                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}  // simulate break
                System.out.println("TICKET STAFF 2 IS BACK!!!");
            }
        }

    public void shutdown() {
        try {executorService.awaitTermination(9, TimeUnit.SECONDS);} catch (InterruptedException e) {}
        TicketStaffOneAvailable = TicketStaffTwoAvailable = true;
        executorService.shutdown();
    }
}

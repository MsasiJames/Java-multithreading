package Workspace;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Schedulers extends MainSimulation{
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public void scheduleTask(Runnable task, long delay,TimeUnit unit) {
        executorService.schedule(task, delay, unit);
    }

    public void bathRoomAction(){
                System.out.println("TICKET STAFF 1 WENT TO BATHROOM!!!");
                //System.out.println("CUSTOMERS FROM BOOTH ONE GO TO BOOTH TWO");
                TicketStaffOneAvailable = false;
                //boothOne.drainTo(boothTwo);
                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}  // simulate break
                System.out.println("TICKET STAFF 1 IS BACK!!!");
                TicketStaffOneAvailable = true;
        }
        
    public static void executeMachineBreak(){
        ExecutorService machine = Executors.newSingleThreadExecutor();

        Future<Boolean> future = machine.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception{
                System.out.println("MACHINE IN BOOTH TWO BROKEN");
                TicketStaffTwoAvailable = false;
                System.out.println("CUSTOMERS FROM BOOTH TWO GO TO BOOTH ONE");
                boothTwo.drainTo(boothOne);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
                //System.out.println("Finished");
                return true;    // this value was used to break the machine in booth two
            }
        });

        try {machineBroken = future.get();} catch (InterruptedException e) {} catch (ExecutionException e) {}
        machine.shutdown();
        try{machine.awaitTermination(1, TimeUnit.DAYS);}catch(InterruptedException e){}
    }
    public static void executeMachineFix(){
        Thread fixMachine = new Thread(new Runnable() {
            public void run() {
                System.out.println("MACHINE IN BOOTH TWO FIXED");
                try {
                    Thread.sleep(2000);     // simulate fixing the machine
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
                System.out.println("CUSTOMERS CAN COME NOW");
                machineBroken = false;
                TicketStaffTwoAvailable = true;
            }
        });
        fixMachine.start();
    }

    public void shutdown() {
        executorService.shutdown();
        try {executorService.awaitTermination(1, TimeUnit.DAYS);} catch (InterruptedException e) {}
    }
}

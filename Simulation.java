import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Simulation {

    private static final int CUSTOMER_MAX = 80;

    private static final Semaphore boothSemaphore = new Semaphore(2, true);
    private static final Semaphore inspectorSemaphore = new Semaphore(1, true);
    private static final Semaphore waitingSemaphore = new Semaphore(3, true);
    private static final Semaphore busSemaphore = new Semaphore(30, true);
    private static final Semaphore westEntrance = new Semaphore(1, true);
    private static final Semaphore eastEntrance = new Semaphore(1, true);

    private static BlockingQueue<Integer> Buses = new ArrayBlockingQueue<Integer>(30);
    private static BlockingQueue<Integer> WaitingArea = new ArrayBlockingQueue<Integer>(30);
    private static BlockingQueue<Integer> Terminal = new ArrayBlockingQueue<Integer>(45);
    
    private static Random rand = new Random();

    private static class Customer extends Thread {
        private int CustomerId;
        private Semaphore entrancePermit;

        public Customer(int id, Semaphore entrancePermit) {
            this.CustomerId = id;
            this.entrancePermit = entrancePermit;
        }

        @Override
        public void run() {
            try {
                // Customer entering Gates
                entrancePermit.acquire();
                try{Thread.sleep(2000);} catch (InterruptedException e) {} // simulate entering gate
                getInTerminal();

                // Get a ticket from a booth
                boothSemaphore.acquire();
                try{Thread.sleep(0);} catch (InterruptedException e) {} // simulate buying ticket
                getTicket();
                boothSemaphore.release();

                // Go to the waiting area for the bus
                waitingSemaphore.acquire();
                try{Thread.sleep(0);} catch (InterruptedException e) {} 
                waitInWaitingArea();
                waitingSemaphore.release();

                // Get the ticket checked by the inspector
                inspectorSemaphore.acquire();
                try{Thread.sleep(0);} catch (InterruptedException e) {} // simulate ticket inspection
                checkTicket();
                inspectorSemaphore.release();

                // Passenger boarding the bus
                busSemaphore.acquire();
                BoardingBus();
                busSemaphore.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{entrancePermit.release();}
        }

        private void getInTerminal() throws InterruptedException{
            String gate;
            boolean gateFull = false;
            if(entrancePermit == westEntrance){ gate = "west";}else{ gate = "east";}
            if(Terminal.size() == 45){      // max number of customers in the terminal
                System.out.println("Passengers can't enter now, terminal full");
                Thread.sleep(1000);
                gateFull = true;
                while(gateFull){
                    Terminal.remove();
                    if(Terminal.size() < 36){  // 80% of max customers in terminal, MADE CHANGES HERE
                        System.out.println("Terminal freeing up, passengers can enter now");
                        gateFull = false;
                    }
                }
            }else{
                System.out.println("Passenger " + CustomerId + " has entered " + gate + " entrance");
                Terminal.put(CustomerId);   
            }
            
        }

        private void getTicket() {
            int TicketBoothId = rand.nextInt(2) + 1;
            System.out.println("Passenger " + CustomerId  + " got a ticket by ticket staff id: " + TicketBoothId);
        }

        private void checkTicket() {
            System.out.println("Passenger " + CustomerId  + "  ticket was checked.");
        }

        private void waitInWaitingArea() throws InterruptedException {

            WaitingArea.put(CustomerId);

            if(WaitingArea.size() == 30){
                System.out.println("Waiting area are full");
                Thread.sleep(1000);     // wait for all the wating area to free up
                for(int i = 0; i < WaitingArea.size(); i++){
                    WaitingArea.remove();   // removing passengers from the waiting area
                }
                System.out.println("Waiting area is freeing up you can sit now Passenger Id: " + CustomerId);
            }else{
                System.out.println("Passenger " + CustomerId + " is waiting in Waiting Area");
            }
        }

        private void BoardingBus() throws InterruptedException{     // assumption is that all the buses leave together
            Buses.put(CustomerId);

            if(Buses.size() == 30){
                System.out.println("Buses are full now they are leaving");
                Thread.sleep(1000);     // wait for all the buses to come back
                for(int i = 0; i < Buses.size(); i++){
                    Buses.remove();
                }
                System.out.println("Buses are back");
            }else{
                System.out.println("Passenger " + CustomerId + " is Boarding the Bus");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Creating customers
        long start = System.currentTimeMillis();
        ExecutorService passengersEast = Executors.newFixedThreadPool(CUSTOMER_MAX/2);
        ExecutorService passengersWest = Executors.newFixedThreadPool(CUSTOMER_MAX/2);

        for (int i = 0; i < CUSTOMER_MAX / 2; i++) {
            passengersEast.submit(new Customer(i, eastEntrance));
        }
        for (int i = CUSTOMER_MAX / 2; i < CUSTOMER_MAX; i++) {     // this was done so as no twp customers would have the same Id from east and west
            passengersEast.submit(new Customer(i, westEntrance));
        }
        passengersEast.shutdown();
        passengersWest.shutdown();
        passengersEast.awaitTermination(1, TimeUnit.DAYS);
        passengersWest.awaitTermination(1, TimeUnit.DAYS);


        long end = System.currentTimeMillis();

        System.out.println("Time taken: " + ((end - start) / 1000) + " seconds");
        
    }
}



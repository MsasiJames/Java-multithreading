import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class MainSimulation {

    public static final int CUSTOMER_MAX = 80;

    public static final Semaphore boothSemaphore = new Semaphore(2, true);
    public static final Semaphore inspectorSemaphore = new Semaphore(1, true);
    public static final Semaphore waitingSemaphore = new Semaphore(1, true);
    public static final Semaphore busSemaphore = new Semaphore(1, true);
    public static final Semaphore westEntrance = new Semaphore(1, true);
    public static final Semaphore eastEntrance = new Semaphore(1, true);

    public static BlockingQueue<Integer> BusesA = new ArrayBlockingQueue<Integer>(10);
    public static BlockingQueue<Integer> BusesB = new ArrayBlockingQueue<Integer>(10);
    public static BlockingQueue<Integer> BusesC = new ArrayBlockingQueue<Integer>(10);

    public static BlockingQueue<Integer> WaitingAreaA = new ArrayBlockingQueue<Integer>(10);
    public static BlockingQueue<Integer> WaitingAreaB = new ArrayBlockingQueue<Integer>(10);
    public static BlockingQueue<Integer> WaitingAreaC = new ArrayBlockingQueue<Integer>(10);

    public static BlockingQueue<Integer> boothOne = new ArrayBlockingQueue<>(20);
    public static BlockingQueue<Integer> boothTwo = new ArrayBlockingQueue<>(20);


    public static boolean TicketStaffOneAvailable = true;
    public static boolean TicketStaffTwoAvailable = true;

    public static BlockingQueue<Integer> Terminal = new ArrayBlockingQueue<Integer>(45);

    public static AtomicInteger TotalPassengers = new AtomicInteger(0);
    public static AtomicInteger PassengersDeparted = new AtomicInteger(0);
    

    public static Random rand = new Random();

    public static class Customer extends Thread {
        private int CustomerId;
        private Semaphore entrancePermit;
        public Customer(int id, Semaphore entrancePermit) {this.CustomerId = id;this.entrancePermit = entrancePermit;}
        @Override
        public void run() {
            try {
                // Customer entering Gates
                entrancePermit.acquire();
                try{Thread.sleep(1000);} catch (InterruptedException e) {} // simulate entering gate
                getInTerminal();
                entrancePermit.release();
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
                try{Thread.sleep(0);} catch (InterruptedException e) {} // simulate bus boarding
                BoardingBus();
                busSemaphore.release();
            } catch (InterruptedException e) {e.printStackTrace();}finally{entrancePermit.release();}
        }

        private synchronized void getInTerminal() throws InterruptedException{
            // GETTING TO THE TERMINAL
            TotalPassengers.incrementAndGet();
            String gate;
            boolean gateFull = false;
            if(entrancePermit == westEntrance){ gate = "west";}else{ gate = "east";}
            if(Terminal.size() == 45){      // max number of customers in the terminal
                System.out.println("PASSENGERS FULL NOW, PASSENGERS CAN'T ENTER MAX CAPACITY REACHED AT (" + Terminal.size() + " /45)");
                Thread.sleep(500);
                gateFull = true;
                while(gateFull){
                    Terminal.remove();      // IMPROVE THIS CODE, BY SIMULATING PEOPLE ARE LEAVING THE TERMINAL THROUGH THE BUSES
                    if(Terminal.size() < 36){  // 80% of max customers in terminal, MADE CHANGES HERE
                        System.out.println("TERMINAL FREED UP, PASSENGERS CAN ENTER NOW CURRENT COUNT AT (" + Terminal.size() + " /45)");
                        gateFull = false;
                    }
                }
            }else{
                System.out.println("Passenger " + CustomerId + " has entered " + gate + " entrance");
                Terminal.put(CustomerId);   
            }
            // GOING TO THE BOOTH
            if(CustomerId < 40 && CustomerId >= 0){  // passengers from west entrance
                if(boothOne.size() == 10){
                    boothOne.remove();
                }
                boothOne.put(CustomerId);
                Thread.sleep(500); // simulate waiting
                System.out.println("Passenger " + CustomerId + " is going to booth one current count at (" + boothOne.size() + " /20)");
            }else if(CustomerId >= 40 && CustomerId < 80){
                if(boothTwo.size() == 10){
                    boothTwo.remove();
                }
                boothTwo.put(CustomerId);
                Thread.sleep(500); // simulate waiting
                System.out.println("Passenger " + CustomerId + " is going to booth two current count at (" + boothTwo.size() + " /20)");
            }
        }

        private void getTicket() {
            boolean TicketStaffOneNotAvailable = !(TicketStaffOneAvailable);
            boolean TicketStaffTwoNotAvailable = !(TicketStaffTwoAvailable);
            // ticket staff id 1 is at booth one, ticket staff id 2 is at booth two
            if((CustomerId < 40 && CustomerId >= 0) && TicketStaffOneAvailable){  // passengers from west entrance
                System.out.println("Passenger " + CustomerId + " is buying a ticket from ticket staff id : 1");
                try {boothOne.remove();} catch (Exception e) {}
            }
            if((CustomerId < 40 && CustomerId >= 0) && TicketStaffOneNotAvailable){
                System.out.println("Passenger " + CustomerId + " is buying a ticket from ticket staff id : 2");
                try {boothTwo.remove();} catch (Exception e) {} // since the customers went to booth two now
            }

            if((CustomerId < 80 && CustomerId >= 40) && TicketStaffTwoAvailable){
                System.out.println("Passenger " + CustomerId + " is buying a ticket from ticket staff id : 2");
                try {boothTwo.remove();} catch (Exception e) {}
            }
            if((CustomerId < 80 && CustomerId >= 40) && TicketStaffTwoNotAvailable){
                System.out.println("Passenger " + CustomerId + " is buying a ticket from ticket staff id : 1");
                try {boothOne.remove();} catch (Exception e) {} // since the customers went to booth one now
            }
        }

        private void checkTicket() {
            System.out.println("Passenger " + CustomerId  + "  ticket was checked.");
        }

        private synchronized void waitInWaitingArea() throws InterruptedException {

            int waitingChoice = ThreadLocalRandom.current().nextInt(3);
            if(waitingChoice == 0){
                WaitingAreaA.put(CustomerId);
                System.out.println("Passenger " + CustomerId + " is waiting at A: current count (" + WaitingAreaA.size() + "/10)");
            }else if(waitingChoice == 1){
                WaitingAreaB.put(CustomerId);
                System.out.println("Passenger " + CustomerId + " is waiting at B: current count (" + WaitingAreaB.size() + "/10)");
            }else if(waitingChoice == 2){
                WaitingAreaC.put(CustomerId);
                System.out.println("Passenger " + CustomerId + " is waiting at C: current count (" + WaitingAreaC.size() + "/10)");
            }

            int duration = rand.nextInt(5000);

            if(WaitingAreaA.size() == 10){
                System.out.println("WAITING AREA A IS FULL");
                for(int i = 0; i < 5; i++){
                    try {WaitingAreaA.remove();} catch (Exception e) {}
                }
                Thread.sleep(duration);
                System.out.println("WAITING AREA A IS FREEING UP NOW");
            }
            if(WaitingAreaB.size() == 10){
                System.out.println("WAITING AREA B IS FULL");
                for(int i = 0; i < 5; i++){
                    try {WaitingAreaB.remove();} catch (Exception e) {}
                }
                Thread.sleep(duration);
                System.out.println("WAITING AREA B IS FREEING UP NOW");
            }
            if(WaitingAreaC.size() == 10){
                System.out.println("WAITING AREA C IS FULL");
                for(int i = 0; i < 5; i++){
                    try {WaitingAreaC.remove();} catch (Exception e) {}
                }
                Thread.sleep(duration);
                System.out.println("WAITING AREA C IS FREEING UP NOW");
            }

        }

        private synchronized void BoardingBus() throws InterruptedException{
                int busChoice = ThreadLocalRandom.current().nextInt(3);
                if(busChoice == 0){
                    BusesA.put(CustomerId);
                    System.out.println("Passenger " + CustomerId + " is boarding Bus A: current count (" + BusesA.size() + "/10)");
                }else if(busChoice == 1){
                    BusesB.put(CustomerId);
                    System.out.println("Passenger " + CustomerId + " is boarding Bus B: current count (" + BusesB.size() + "/10)");
                }else if(busChoice == 2){
                    BusesC.put(CustomerId);
                    System.out.println("Passenger " + CustomerId + " is boarding Bus C: current count (" + BusesC.size() + "/10)");
                }
                PassengersDeparted.incrementAndGet();
            
            int duration = rand.nextInt(2000);  // time for bus to come back

            if(BusesA.size() == 10){
                System.out.println("BUS A IS FULL, NOW LEAVING");
                Thread.sleep(duration); // simulate bus leaving
                BusesA.clear();         // remove all the passengers from the bus
                if(duration > 1000){
                    System.out.println("BUS A IS DELAYED, COMING LATE");
                }else{
                    System.out.println("BUS A IS HERE NOW");
                }
            }
            if(BusesB.size() == 10){
                System.out.println("BUS B IS FULL, NOW LEAVING");
                Thread.sleep(duration); // simulate bus leaving
                BusesB.clear();         // remove all the passengers from the bus
                if(duration > 1000){
                    System.out.println("BUS B IS DELAYED, COMING LATE");
                }else{
                    System.out.println("BUS B IS HERE NOW");
                }
            }
            if(BusesC.size() == 10){
                System.out.println("BUS C IS FULL, NOW LEAVING");
                Thread.sleep(duration); // simulate bus leaving
                BusesC.clear();         // remove all the passengers from the bus
                if(duration > 1000){
                    System.out.println("BUS C IS DELAYED, COMING LATE");
                }else{
                    System.out.println("BUS C IS HERE NOW");
                }
            }
        }

    }
    
    public static void main(String[] args) throws InterruptedException {
        BathRoomBreakTwo bathBreak = new BathRoomBreakTwo();  // this has to be in this position.
        bathBreak.scheduleTask(new Runnable() {
            @Override
            public void run() {
                bathBreak.bathRoomAction();
            }
        }, 7, TimeUnit.SECONDS);
        
        // Creating customers
        long start = System.currentTimeMillis();
        for (int i = 0; i < CUSTOMER_MAX / 2; i++) {
            Customer custWest = new Customer(i, westEntrance);
            custWest.start();
        }
        
        for (int i = CUSTOMER_MAX / 2; i < CUSTOMER_MAX; i++) {     // this was done so as no two customers would have the same Id from east and west
            Customer custEast = new Customer(i, eastEntrance);
            custEast.start();
            custEast.join();
        }

        try {bathBreak.shutdown();} catch (Exception e) {}
        
        
        Thread endOfSimulation = new Thread(){
            public void run(){
                System.out.println("--------------------------------------------------------------");
                if(BusesA.size() == 0){
                    System.out.println("BusA is parking for the day");
                    System.out.println("Bus B is leaving with " + BusesB.size() + " /10 passengers");
                    System.out.println("Bus C is leaving with " + BusesC.size() + " /10 passengers");
                }else if(BusesB.size() == 0){
                    System.out.println("Bus A is leaving with " + BusesA.size() + " /10 passengers");
                    System.out.println("Bus B is parking for the day");
                    System.out.println("Bus C is leaving with " + BusesC.size() + " /10 passengers");
                }else if(BusesC.size() == 0){
                    System.out.println("Bus A is leaving with " + BusesA.size() + " /10 passengers");
                    System.out.println("Bus B is leaving with " + BusesB.size() + " /10 passengers");
                    System.out.println("BusC is parking for the day");
                }else{
                    System.out.println("Bus A is leaving with " + BusesA.size() + " /10 passengers");
                    System.out.println("Bus B is leaving with " + BusesB.size() + " /10 passengers");
                    System.out.println("Bus C is leaving with " + BusesC.size() + " /10 passengers");
                }
                System.out.println("Terminal closing, all passengers without tickets must leave");
            }
        };
        endOfSimulation.start();
        endOfSimulation.join();

        long end = System.currentTimeMillis();

        System.out.println("Time taken: " + ((end - start) / 1000) + " seconds");
        System.out.println("Customers processed: " + TotalPassengers.get());
        System.out.println("Passengers departed with buses: " + PassengersDeparted.get());
    }
}



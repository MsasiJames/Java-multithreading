import java.util.concurrent.Semaphore;

public class BusTerminalSimulation {
    private static final int NUM_OF_BUSSES = 3; // Number of busses in the terminal
    private static final int NUM_OF_PASSENGERS = 10; // Number of passengers

    private static Semaphore busSemaphore = new Semaphore(NUM_OF_BUSSES);

    private static class Bus implements Runnable {
        private int busId;

        public Bus(int busId) {
            this.busId = busId;
        }

        @Override
        public void run() {
            try {
                System.out.println("Bus " + busId + " arrives at the terminal.");
                busSemaphore.acquire();
                System.out.println("Bus " + busId + " boards passengers.");
                Thread.sleep(2000); // Simulating boarding time
                System.out.println("Bus " + busId + " departs from the terminal.");
                busSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Passenger implements Runnable {
        private int passengerId;

        public Passenger(int passengerId) {
            this.passengerId = passengerId;
        }

        @Override
        public void run() {
            try {
                System.out.println("Passenger " + passengerId + " arrives at the terminal.");
                busSemaphore.acquire();
                System.out.println("Passenger " + passengerId + " boards the bus.");
                Thread.sleep(1000); // Simulating boarding time
                System.out.println("Passenger " + passengerId + " gets off the bus.");
                busSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Create and start the bus threads
        for (int i = 0; i < NUM_OF_BUSSES; i++) {
            Thread busThread = new Thread(new Bus(i));
            busThread.start();
        }

        // Create and start the passenger threads
        for (int i = 0; i < NUM_OF_PASSENGERS; i++) {
            Thread passengerThread = new Thread(new Passenger(i));
            passengerThread.start();
        }
    }
}

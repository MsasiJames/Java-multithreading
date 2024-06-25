import java.util.concurrent.Semaphore;

public class BusSimulationTwo {

    private static final int CUSTOMER_COUNT = 30;
    private static final Semaphore boothSemaphore = new Semaphore(2);
    private static final Semaphore inspectorSemaphore = new Semaphore(1);
    private static final Semaphore busSemaphore = new Semaphore(3);

    public static void main(String[] args) {
        // Create customers
        for (int i = 0; i < CUSTOMER_COUNT; i++) {
            new Customer(i).start();
        }
    }

    private static class Customer extends Thread {
        private int CustomerId;

        public Customer(int id) {
            this.CustomerId = id;
        }

        @Override
        public void run() {
            try {
                // Get a ticket from a booth
                boothSemaphore.acquire();
                getTicket();
                boothSemaphore.release();

                // Get the ticket checked by the inspector
                inspectorSemaphore.acquire();
                checkTicket();
                inspectorSemaphore.release();

                // Go to the waiting area for the bus
                busSemaphore.acquire();
                waitInWaitingArea();
                busSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void getTicket() {
            System.out.println("Customer " + CustomerId  + " got a ticket.");
        }

        private void checkTicket() {
            System.out.println("Customer " + CustomerId  + "  ticket was checked.");
        }

        private void waitInWaitingArea() {
            System.out.println("Customer " + CustomerId  + "  waiting for bus");
        }
    }
}


//@author: SpeedyNS


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class Theater
{
    public static int numCustomers = 15, numBoxOfficeAgents = 2;

    //Global variables
    static String[] movies;
    static int[] seats;
    static String[] food = {"Popcorn", "Soda", "Popcorn and Soda"};
    static Random rng = new Random();
    //Semaphores
    static Queue<Customer> queueBoxOfficeAgent = new LinkedList<>();
    static Queue<Customer> queueTicketTaker = new LinkedList<>();
    static Queue<Customer> queueConcessionStandWorker = new LinkedList<>();
    static Semaphore mutexQueueBoxOfficeAgent = new Semaphore(1);
    static Semaphore mutexQueueTicketTaker = new Semaphore(1);
    static Semaphore mutexQueueConcessionStandWorker = new Semaphore(1);
    static Semaphore mutexMovieArray = new Semaphore(1);
    static Semaphore customerRdyBoxOfficeAgent = new Semaphore(0);
    static Semaphore customerRdyTicketTaker = new Semaphore(0);
    static Semaphore customerRdyConcessionStandWorker = new Semaphore(0);

    public static void main(String[] args) throws FileNotFoundException
    {
        //Reading input file
        String filename = "movies.txt";
        if(args.length >= 1)
            filename = args[0];
        ArrayList<String> file = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            file.add(scanner.nextLine());
        }
        movies = new String[file.size()];
        seats = new int[file.size()];
        for (int i = 0; i < file.size(); i++) {
            String[] split = file.get(i).split("\t");
            movies[i] = split[0];
            seats[i] = Integer.parseInt(split[1]);
        }

        //Initialize all threads
        Thread[] boxOfficeAgents = new Thread[numBoxOfficeAgents];
        for (int i = 0; i < numBoxOfficeAgents; i++) {
            boxOfficeAgents[i] = new Thread(new BoxOfficeAgent(i));
        }
        Thread ticketTaker = new Thread(new TicketTaker(0));
        Thread concessionStandWorker = new Thread(new ConcessionStandWorker(0));
        Thread[] customers = new Thread[numCustomers];
        for (int i = 0; i < numCustomers; i++) {
            customers[i] = new Thread(new Customer(i));
        }

        //Run all threads
        for (int i = 0; i < numBoxOfficeAgents; i++) {
            boxOfficeAgents[i].start();
        }
        ticketTaker.start();
        concessionStandWorker.start();
        System.out.println("Theater is open");
        for (int i = 0; i < numCustomers; i++) {
            customers[i].start();
        }

        //Customers join theater
        for (int i = 0; i < numCustomers; i++) {
            try {
                customers[i].join();
                System.out.println("Customer " + i + " enjoys the movie.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Terminate threads
        boxOfficeAgents[0].interrupt();
        boxOfficeAgents[1].interrupt();
        ticketTaker.interrupt();
        concessionStandWorker.interrupt();

        System.exit(0);
    }
}

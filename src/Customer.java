import java.util.concurrent.Semaphore;

public class Customer extends Thread
{
    private final int id;
    private int movieID;
    private String foodOrder;
    private boolean movieAvailable;
    public Semaphore beingServed;
    public Customer(int id)
    {
        this.id = id;
        beingServed = new Semaphore(0);
        movieAvailable = false;
    }

    @Override
    public void run()
    {
        try {
            selectMovie();                                                                                              //randomly select film
            Theater.mutexQueueBoxOfficeAgent.acquire();
            queueBoxOfficeWorker();                                                                                     //Customer lines up for Box Office
            Theater.customerRdyBoxOfficeAgent.release();                                                                //Customer is rdy for box Office
            Theater.mutexQueueBoxOfficeAgent.release();
            beingServed.acquire();                                                                                      //Customer is being served by Box Office
            if (movieAvailable)                                                                                         //Check film availability
            {
                Theater.mutexQueueTicketTaker.acquire();
                queueTicketTaker();                                                                                     //Customer lines up for ticket taker
                Theater.customerRdyTicketTaker.release();                                                               //tell ticket taker customer is rdy
                Theater.mutexQueueTicketTaker.release();
                beingServed.acquire();                                                                                  //Customer is being served by ticket taker
                if (visitConcessionStand())                                                                             //decide whether to visit concession stand
                {
                    selectFoodOrder();                                                                                  //Select food
                    Theater.mutexQueueConcessionStandWorker.acquire();
                    queueConcessionStandWorker();                                                                       //Customer lines up for food
                    Theater.customerRdyConcessionStandWorker.release();                                                 //tell Concession stand customer is rdy
                    Theater.mutexQueueConcessionStandWorker.release();
                    beingServed.acquire();                                                                              //Customer is being served by Concession stand
                }

                enterTheater();                                                                                         //Enter the Theater
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getID()
    {
        return id;
    }

    public int getMovieID()
    {
        return movieID;
    }

    public boolean getMovieAvailable()
    {
        return movieAvailable;
    }

    public String getFoodOrder()
    {
        return foodOrder;
    }

    public void setMovieAvailable(boolean bool)
    {
        movieAvailable = bool;
    }

    private void enterTheater()
    {
        System.out.println("Customer " + id + " enters theater to watch " + Theater.movies[movieID]);
    }
    
    private void queueBoxOfficeWorker()
    {
        Theater.queueBoxOfficeAgent.add(this);
    }

    private void queueTicketTaker()
    {
        Theater.queueTicketTaker.add(this);
        System.out.println("Customer " + id + " in line to see ticket taker");
    }

    private void queueConcessionStandWorker()
    {
        Theater.queueConcessionStandWorker.add(this);
        System.out.println("Customer " + id + " in line to buy " + foodOrder);
    }

    private void selectFoodOrder()                                                                                      //33% chance for each order
    {
        int foodOrderID = Theater.rng.nextInt(3);
        if (foodOrderID == 0)
            foodOrder = Theater.food[0];
        else if (foodOrderID == 1)
            foodOrder = Theater.food[1];
        else
            foodOrder = Theater.food[2];
    }

    private void selectMovie()
    {
        movieID = Theater.rng.nextInt(Theater.movies.length);
        System.out.println("Customer " + id + " created, buying ticket to " + Theater.movies[movieID]);
    }

    private boolean visitConcessionStand()                                                                              //50% chance to visit Concession stand
    {
        return Theater.rng.nextInt(2) == 0;
    }
}

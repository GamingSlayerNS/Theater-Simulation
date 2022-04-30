import java.util.concurrent.Semaphore;

public class Customer extends Thread
{
    private int id, movieID;
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
            selectMovie();
            Theater.mutexQueueBoxOfficeAgent.acquire();
            queueBoxOfficeWorker();
            Theater.customerRdyBoxOfficeAgent.release();
            Theater.mutexQueueBoxOfficeAgent.release();
            beingServed.acquire();
            if (movieAvailable)
            {
                Theater.mutexQueueTicketTaker.acquire();
                queueTicketTaker();
                Theater.customerRdyTicketTaker.release();                                                               //TicketTaker takes customer
                Theater.mutexQueueTicketTaker.release();                                                                //Customer leaves queue
                beingServed.acquire();
                if (visitConcessionStand())
                {
                    selectFoodOrder();
                    Theater.mutexQueueConcessionStandWorker.acquire();
                    queueConcessionStandWorker();
                    Theater.customerRdyConcessionStandWorker.release();
                    Theater.mutexQueueConcessionStandWorker.release();
                    beingServed.acquire();
                }

                enterTheater();
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

    private void selectFoodOrder()
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
        if (Theater.rng.nextInt(2) == 0)
            return true;
        else
            return false;
    }
}

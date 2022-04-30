public class BoxOfficeAgent extends Thread
{
    private final int id;
    private Customer servingCustomer;
    public BoxOfficeAgent(int id)
    {
        this.id = id;
        System.out.println("Box office agent " + id + " created");
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                Theater.customerRdyBoxOfficeAgent.acquire();                                                            //Check queue
                Theater.mutexQueueBoxOfficeAgent.acquire();
                queueBoxOfficeAgent();                                                                                  //Accept Customer from queue
                Theater.mutexQueueBoxOfficeAgent.release();
                Theater.mutexMovieArray.acquire();
                checkMovieAvailability();                                                                               //Check film availability from seats
                Theater.mutexMovieArray.release();

                //process
                sleep(1500);                                                                                      //Wait 90/60s while ticket is processed.
                if (servingCustomer.getMovieAvailable())
                    System.out.println("Box office agent " + id + " sold ticket for " + Theater.movies[servingCustomer.getMovieID()] + " to customer " + servingCustomer.getID());
                else
                    System.out.println("Customer " + servingCustomer.getID() + " could not buy ticket, movie " + Theater.movies[servingCustomer.getMovieID()] + " is sold out");
                servingCustomer.beingServed.release();                                                                  //Finish serving customer
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkMovieAvailability()
    {
        if (Theater.seats[servingCustomer.getMovieID()] > 0)
        {
            Theater.seats[servingCustomer.getMovieID()]--;
            servingCustomer.setMovieAvailable(true);
        }
        else
            servingCustomer.setMovieAvailable(false);
    }

    private void queueBoxOfficeAgent()
    {
        servingCustomer = Theater.queueBoxOfficeAgent.remove();
        System.out.println("Box office agent " + id + " serving customer " + servingCustomer.getID());
    }
}

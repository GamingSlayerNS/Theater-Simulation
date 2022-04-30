public class ConcessionStandWorker extends Thread
{
    private final int id;
    private Customer servingCustomer;
    public ConcessionStandWorker(int id)
    {
        this.id = id;
        System.out.println("Concession stand worker created");
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                Theater.customerRdyConcessionStandWorker.acquire();                                                     //Check queue
                Theater.mutexQueueConcessionStandWorker.acquire();
                queueConcessionStandWorker();                                                                           //Accept Customer from queue
                Theater.mutexQueueConcessionStandWorker.release();

                //process
                System.out.println("Order for " + servingCustomer.getFoodOrder() + " taken from customer " + servingCustomer.getID());
                sleep(3000);                                                                                      //Wait 180/60s while buying food.
                System.out.println(servingCustomer.getFoodOrder() + " given to customer " + servingCustomer.getID());
                servingCustomer.beingServed.release();                                                                  //Finish serving customer
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void queueConcessionStandWorker()
    {
        servingCustomer = Theater.queueConcessionStandWorker.remove();
    }
}

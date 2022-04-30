public class TicketTaker extends Thread
{
    private final int id;
    private Customer servingCustomer;
    public TicketTaker(int id)
    {
        this.id = id;
        System.out.println("Ticket taker created");
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                Theater.customerRdyTicketTaker.acquire();                                                               //Check queue
                Theater.mutexQueueTicketTaker.acquire();
                queueTicketTaker();                                                                                     //Accept Customer from queue
                Theater.mutexQueueTicketTaker.release();

                //process
                sleep(250);                                                                                       //Wait 15/60s while ticket is taken.
                System.out.println("Ticket taken from customer " + servingCustomer.getID());
                servingCustomer.beingServed.release();                                                                  //Finish serving customer
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void queueTicketTaker()
    {
        servingCustomer = Theater.queueTicketTaker.remove();
    }
}

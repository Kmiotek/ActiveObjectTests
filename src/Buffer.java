import org.jcsp.lang.*;

/**
 * Buffer class: Manages communication between Producer2
 * and Consumer2 classes.
 */
public class Buffer implements CSProcess {
    private One2OneChannelInt[] consumers;
    private One2OneChannelInt[] producers;

    private final int capacity;

    private int id;

    public Buffer(One2OneChannelInt[] producers,
                  One2OneChannelInt[] consumers, int id) {
        this(producers, consumers, id,  5);
    } // constructor

    public Buffer(One2OneChannelInt[] producers,
                  One2OneChannelInt[] consumers, int id, int capacity) {
        this.capacity = capacity;
        this.consumers = consumers;
        this.producers = producers;
        this.id = id;

    } // constructor


    public void run() {
        final Guard[] guards;

        int used = 0;

        guards = new Guard[consumers.length + producers.length];
        boolean[] preCondition = new boolean[guards.length];
        for (int i = 0; i < producers.length; i++) {
            guards[i] = producers[i].in();
            preCondition[i] = (capacity - used > 0);
        }
        for (int i = 0; i < consumers.length; i++) {
            guards[i + producers.length] = consumers[i].in();
            preCondition[i + producers.length] = (used > 0);
        }


        final Alternative alt = new Alternative(guards);
        boolean running = true;
        while (running) {
            for (int i = 0; i < producers.length; i++) {
                preCondition[i] = (capacity - used > 0);
            }
            for (int i = 0; i < consumers.length; i++) {
                preCondition[i + producers.length] = (used > 0);
            }
            int index = alt.fairSelect(preCondition);

            if (index < producers.length){
                producers[index].in().read();
                used ++;
            } else {
                consumers[index - producers.length].in().read();
                consumers[index - producers.length].out().write(1);
                used--;
            }
            System.out.println("Buffer " + id + " used " + used);
        } // while
        System.out.println("Buffer " + id + " ended.");
    } // run
} // class Buffer
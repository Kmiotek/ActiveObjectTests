import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannelInt;

import java.util.Arrays;

public class Supervisor implements CSProcess {

    One2OneChannelInt[] consumers;
    One2OneChannelInt[] producers;

    int[] buffers;
    int[] bufferSizes;

    int[] bufferWasUsed;
    int[] consumerAsked;
    int[] producerAsked;

    public Supervisor(int[] bufferSizes, One2OneChannelInt[] producers, One2OneChannelInt[] consumers){
        this.consumers = consumers;
        this.producers = producers;
        buffers = bufferSizes.clone();
        this.bufferSizes = bufferSizes.clone();
        bufferWasUsed = new int[buffers.length];
        Arrays.fill(bufferWasUsed, 0);
        consumerAsked = new int[consumers.length];
        Arrays.fill(consumerAsked, 0);
        producerAsked = new int[producers.length];
        Arrays.fill(producerAsked, 0);
    }

    @Override
    public void run() {
        final Guard[] guards;

        int freeSpace = 0;
        for (int i : buffers) {
            freeSpace += i;
        }
        int allSpace = freeSpace;

        guards = new Guard[consumers.length + producers.length];
        boolean[] preCondition = new boolean[guards.length];
        for (int i = 0; i < producers.length; i++) {
            guards[i] = producers[i].in();
            preCondition[i] = (freeSpace > 0);
        }
        for (int i = 0; i < consumers.length; i++) {
            guards[i + producers.length] = consumers[i].in();
            preCondition[i + producers.length] = (allSpace > freeSpace);
        }



        final Alternative alt = new Alternative(guards);
        boolean running = true;
        int bufferIterator = 0;
        while (running) {
            for (int i = 0; i < producers.length; i++) {
                preCondition[i] = (freeSpace > 0);
            }
            for (int i = 0; i < consumers.length; i++) {
                preCondition[i + producers.length] = (allSpace > freeSpace);
            }
            int index = alt.fairSelect(preCondition);

            if (index < producers.length){
                System.out.println("Some producer " + preCondition[index] + " free space " + freeSpace + " " + (freeSpace > 0));
                producers[index].in().read();
                producerAsked[index] += 1;
                int newBufferIterator = getFreeIndex(bufferIterator);
                producers[index].out().write(newBufferIterator);
                if (newBufferIterator > -1) {
                    bufferIterator = newBufferIterator;
                    buffers[bufferIterator] -= 1;
                    bufferWasUsed[bufferIterator] += 1;
                    freeSpace -= 1;
                }
            } else {
                System.out.println("Some consumer " + preCondition[index]);
                consumers[index - producers.length].in().read();
                consumerAsked[index - producers.length] += 1;
                int newBufferIterator = getTakenIndex(bufferIterator);
                consumers[index - producers.length].out().write(newBufferIterator);
                if (newBufferIterator > -1) {
                    bufferIterator = newBufferIterator;
                    buffers[bufferIterator] += 1;
                    bufferWasUsed[bufferIterator] += 1;
                    freeSpace += 1;
                }
            }
            System.out.println("\n\nBuffers");
            for (int i: bufferWasUsed) {
                System.out.printf("%d ", i);
            }
            System.out.println("\nConsumers");
            for (int i: consumerAsked) {
                System.out.printf("%d ", i);
            }
            System.out.println("\nProducers");
            for (int i: producerAsked) {
                System.out.printf("%d ", i);
            }
        } // while
        System.out.println("\n\nBuffers");
        for (int i: bufferWasUsed) {
            System.out.printf("%d ", i);
        }
        System.out.println("\nConsumers");
        for (int i: consumerAsked) {
            System.out.printf("%d ", i);
        }
        System.out.println("\nProducers");
        for (int i: producerAsked) {
            System.out.printf("%d ", i);
        }
    }

    private int getFreeIndex(int bufferI){
        int bufferIterator = bufferI;
        for (int i = 0; i < buffers.length; i++) {
            bufferIterator = (bufferIterator + 1) % buffers.length;
            if (buffers[bufferIterator] > 0){
                return bufferIterator;
            }
        }
        return -1;
    }

    private int getTakenIndex(int bufferI){
        int bufferIterator = bufferI;
        for (int i = 0; i < buffers.length; i++) {
            bufferIterator = (bufferIterator + 1) % buffers.length;
            if (buffers[bufferIterator] < bufferSizes[bufferIterator]){
                return bufferIterator;
            }
        }
        return -1;
    }
}

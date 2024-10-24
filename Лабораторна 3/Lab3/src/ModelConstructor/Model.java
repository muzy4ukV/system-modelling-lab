package ModelConstructor;

import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    protected final ArrayList<Element> elements;
    protected double tCurr;
    protected double tNext;
    protected int nearestEvent;
    protected boolean isFirstIteration = true;

    public Model(Element... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
        tNext = 0.0;
        tCurr = tNext;
        nearestEvent = 0;
    }

    public void simulate(double time) {
        while (tCurr < time) {
            tNext = Double.MAX_VALUE;
            for (var element : elements) {
                if ((tCurr < element.getTNext() || isFirstIteration) && element.getTNext() < tNext) {
                    tNext = element.getTNext();
                    nearestEvent = element.getId();
                }
            }
            updateBlockedElements();
            System.out.printf("%n[Event] Element: %s | tNext: %.2f%n", elements.get(nearestEvent).getName(), tNext);

            var delta = tNext - tCurr;
            doModelStatistics(delta);

            for (Element element : elements) {
                element.doStatistics(delta);
            }

            tCurr = tNext;
            for (var element : elements) {
                element.setTCurr(tCurr);
            }

            elements.get(nearestEvent).outAct();

            for (var element : elements) {
                if (element.getTNext() == tCurr) {
                    element.outAct();
                }
            }

            isFirstIteration = false;
            printInfo();
        }

        // Adding resting elements in queue
        for (Element element : elements) {
            if (element instanceof Process) {
                Process p = (Process) element;
                p.addRestJobs(p.getQueueSize());
                if (element.getState() == 1) {
                    p.addRestJobs(p.getNumOfJobs());
                }
            }
        }

        printResult();
    }

    public void printInfo() {
        System.out.println("\n--- System Info ---");
        for (var element : elements) {
            element.printInfo();
        }
        System.out.println();
    }

    public void printResult() {
        System.out.println("\n------------- RESULTS -------------");
        for (var element : elements) {
            System.out.print("-> ");
            element.printResult();

            if (element instanceof Process p) {
                System.out.printf("   Mean Queue: %.2f%n", p.getMeanQueue() / tCurr);
                System.out.printf("   Mean Workload: %.2f%n", p.getWorkTime() / tCurr);
                System.out.printf("   Failure Probability: %.2f%n", p.getFailures() / (double) (p.getQuantity() + p.getFailures()));
            }
        }
        System.out.println("-----------------------------------\n");
    }

    protected void doModelStatistics(double delta) {
        // Implement this if needed
    }

    private void updateBlockedElements() {
        for (var element : elements) {
            if (element.getTNext() <= tCurr) {
                element.setTNext(tNext);
            }
        }
    }
}

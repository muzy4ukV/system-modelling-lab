package ModelConstructor;

import java.util.ArrayList;

public class Model {
    private ArrayList<Element> list;
    double tnext, tcurr;
    int event;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e.getId();
                }
            }
            System.out.println("\nIt's time for event in " +
                    list.get(event).getName() +
                    ", time = " + tnext);
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            list.get(event).outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
            printInfo();
        }
        // Adding resting elements in queues to failure
        for (Element e : list) {
            if (e instanceof Service s) {
                s.addRestFailure(s.getQueue());
            }
        }
        printResult(time);
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult(double simulationTime) {
        System.out.println("\n-------------RESULTS-------------");
        int sum_failure = 0;
        int create_elem = 0;
        for (Element e : list) {
            System.out.println();
            e.printResult();
            String className = e.getClass().getSimpleName();
            switch (className) {
                case "Service":
                    Service p = (Service) e;
                    sum_failure += p.getFailure();
                    System.out.println("number of failure = " + p.getFailure() +
                            "\nmean length of queue = " +
                            p.getMeanQueue() / tcurr +
                            "\naverage service load = " + p.getMeanLoad() / tcurr);
                    break; // Додаємо break, щоб уникнути виконання наступного case
                case "Create":
                    create_elem = e.getQuantity();
                    break; // Додаємо break, щоб уникнути виконання наступних case
            }
        }
        System.out.println("\nFailure probability = " + (double) sum_failure / create_elem);
    }
}

package core;

import java.util.ArrayList;

public class Model {
    private ArrayList<Element> list;
    double tnext, tcurr;
    Element currElement;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        currElement = null;
        tcurr = tnext;
    }

    public double simulate(boolean showLogs) {
        while (true) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext && e.getTnext() >= 0) {
                    tnext = e.getTnext();
                    currElement = e;
                }
            }
            if (tnext == Integer.MAX_VALUE) { //вихід з циклу після закінчення обробки всіх завдань
                break;
            }
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            currElement.outAct();
            if(showLogs) {
                System.out.println("\nIt's time for event in " +
                        currElement.getName() +
                        ", time = " + tnext);

                printInfo();
            }
        }
        // Adding resting elements in queues to failure
        for (Element e : list) {
            if (e instanceof Process s) {
                s.addRestFailure(s.getQueue());
                s.addRestFailure(s.getLoadedDevices());
            }
        }
        if(showLogs) {
            printResult(tcurr);
        }
        return tcurr;
    }

//    public void simulate(double time) {
//        while (tcurr < time) {
//            tnext = Double.MAX_VALUE;
//            for (Element e : list) {
//                if (e.getTnext() < tnext && e.getTnext() >= 0) {
//                    tnext = e.getTnext();
//                    event = e.getId();
//                }
//            }
//            System.out.println("\nIt's time for event in " +
//                    list.get(event).getName() +
//                    ", time = " + tnext);
//            for (Element e : list) {
//                e.doStatistics(tnext - tcurr);
//            }
//            tcurr = tnext;
//            for (Element e : list) {
//                e.setTcurr(tcurr);
//            }
//            list.get(event).outAct();
//
//            printInfo();
//        }
//        // Adding resting elements in queues to failure
//        for (Element e : list) {
//            if (e instanceof Process s) {
//                s.addRestFailure(s.getQueue());
//                s.addRestFailure(s.getLoadedDevices());
//            }
//        }
//        printResult(time);
//    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult(double simulationTime) {
        System.out.println("\n-------------RESULTS-------------");
        System.out.println("\nSIMULATION TIME: " + simulationTime);
        int sum_failure = 0;
        int created_tasks = 0;
        for (Element e : list) {
            String className = e.getClass().getSimpleName();
            switch (className) {
                case "Process":
                    Process p = (Process) e;
                    sum_failure += p.getFailure();
                case "Create":
                    created_tasks += e.getQuantity();
                    break;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Element e = list.get(i);
            String className = e.getClass().getSimpleName();
            System.out.println();
            e.printResult();
            if(className.equals("Process")) {
                Process p = (Process) e;
                System.out.println("number of failure = " + p.getFailure() +
                        "\nmean length of queue = " +
                        p.getMeanQueue() / simulationTime +
                        "\naverage num loaded devices = " + p.getMeanLoadedDevices() / simulationTime);

            }
            if (i == 5) {
                i = list.size() - 7;
            }
        }

        System.out.println("\nFailure probability = " + (double) sum_failure / created_tasks);
    }
}

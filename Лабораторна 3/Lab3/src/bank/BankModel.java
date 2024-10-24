package bank;

import ModelConstructor.Dispose;
import ModelConstructor.Element;
import ModelConstructor.Job;
import ModelConstructor.Process;

import java.util.ArrayList;

public class BankModel extends ModelConstructor.Model {
    private double meanClientsNum = 0;
    private int totalSwitchedJobs = 0;

    public BankModel(Element... elements) {
        super(elements);
    }

    @Override
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (var element : elements) {
            if (element instanceof Process p) {
                System.out.println("-> " + p.getName());
                System.out.println("   Average workload: " + p.getWorkTime() / tCurr);
                System.out.println("   Average queue size: " + p.getMeanQueue() / tCurr);
                System.out.println("   Mean leave interval: " + p.getMeanLeaveInterval());
            }
            if (element instanceof SwitchingProcess sp) {
                System.out.println("   Switched jobs: " + sp.getSwitchedJobs());
            }
        }
        System.out.println("Mean clients num: " + meanClientsNum / tCurr);
        System.out.println("Average client in bank time: " + getAverageJobInSystemTime());
        System.out.println("Mean leave interval: " + getGlobalMeanLeaveInterval());
        System.out.println("Failure percentage: " + getTotalFailureProbability() * 100 + "%");
        System.out.println("Total switched jobs: " + totalSwitchedJobs);
    }

    @Override
    protected void doModelStatistics(double delta) {
        super.doModelStatistics(delta);
        for (var element : elements) {
            if (element instanceof Process p) {
                meanClientsNum += p.getQueueSize() * delta + p.getState() * delta;
            }
        }
    }

    private double getTotalFailureProbability() {
        double totalFailures = 0;
        double totalQuantity = 0;
        for (var element : elements) {
            if (element instanceof Process p) {
                totalFailures += p.getFailures();
                totalQuantity += p.getQuantity();
            }
            if (element instanceof SwitchingProcess sp) {
                totalSwitchedJobs += sp.getSwitchedJobs();
            }
        }
        return totalFailures / totalQuantity;
    }

    private double getAverageJobInSystemTime() {
        var jobs = new ArrayList<Job>();
        for (var element : elements) {
            if (element instanceof Process p) {
                jobs.addAll(p.getUnprocessedJobs());
            }
            if (element instanceof Dispose d) {
                jobs.addAll(d.getProcessedJobs());
            }
        }
        double totalJobInSystemTime = 0;
        for (var job : jobs) {
            totalJobInSystemTime += job.getTimeOut() - job.getTimeIn();
        }
        return totalJobInSystemTime / jobs.size();
    }

    private double getGlobalMeanLeaveInterval() {
        double totalLeaveInterval = 0;
        double totalQuantity = 0;
        for (var element : elements) {
            if (element instanceof Process p) {
                totalLeaveInterval += p.getMeanLeaveInterval() * p.getQuantity();
                totalQuantity += p.getQuantity();
            }
        }
        return totalLeaveInterval / totalQuantity;
    }
}

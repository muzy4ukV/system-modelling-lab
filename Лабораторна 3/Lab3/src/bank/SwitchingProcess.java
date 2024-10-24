package bank;

import java.util.ArrayList;
import java.util.Arrays;

public class SwitchingProcess extends ModelConstructor.Process {
    private final ArrayList<ModelConstructor.Process> neighbors = new ArrayList<>();
    private final int deltaToSwitch;
    private int switchedJobs = 0;

    public SwitchingProcess(String name, double delayMean, int channelsNum, int deltaToSwitch) {
        super(name, delayMean, channelsNum);
        this.deltaToSwitch = deltaToSwitch;
    }

    public SwitchingProcess(String name, double delayMean, double delayDev, int channelsNum, int deltaToSwitch) {
        super(name, delayMean, delayDev, channelsNum);
        this.deltaToSwitch = deltaToSwitch;
    }

    public void setNeighbors(ModelConstructor.Process... neighbors) {
        this.neighbors.addAll(Arrays.asList(neighbors));
    }

    @Override
    public void outAct() {
        trySwitchProcess();
        super.outAct();
        for (var neighbor : neighbors) {
            if (neighbor instanceof SwitchingProcess) {
                ((SwitchingProcess) neighbor).trySwitchProcess();
            }
        }
    }

    @Override
    public void printResult() {
        super.printResult();
        System.out.println("   Switched jobs: " + switchedJobs);
    }

    public void trySwitchProcess() {
        for (var neighbor : neighbors) {
            while (this.getQueueSize() - neighbor.getQueueSize() >= deltaToSwitch) {
                var switchedJob = this.queue.pollLast();
                neighbor.inAct(switchedJob);
                switchedJobs++;
            }
        }
    }

    public int getSwitchedJobs() {
        return switchedJobs;
    }
}

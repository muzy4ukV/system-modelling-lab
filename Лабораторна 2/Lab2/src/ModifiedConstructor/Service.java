package ModifiedConstructor;

import java.util.Map;
import java.util.TreeMap;

public class Service extends Element {
    private int queue, maxqueue, failure;
    private double meanQueue;
    private double[] devicesTime;
    private double meanLoadedDevices;
    private int loadedDevices;
    private Map<Double, Element> propability;
    private int numExitElem;

    public Service(double delay, int numDeivce) {
        super(delay);
        super.setTnext(-1.0);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        failure = 0;
        devicesTime = new double[numDeivce];
        meanLoadedDevices = 0.0;
        loadedDevices = 0;
        propability = new TreeMap<>();
        numExitElem = 0;
    }

    @Override
    public void inAct() {
        // Якщо є вільні пристрої, займаєм будь-який
        if (loadedDevices < devicesTime.length) {
            takeFreeDevice();
            // Визначаємо наступну найближчу подію й встановлюємо її
            calculateAndSetTnext();
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        releaseDevice();
        if (getQueue() > 0) {
            setQueue(getQueue() - 1);
            takeFreeDevice();
        }
        calculateAndSetTnext();

        Element nextElement = getNextElement();  // Збереження результату виклику методу

        if (!propability.isEmpty() && nextElement != null) {
            nextElement.inAct();
        } else if (super.getNextElement() != null) {
            super.getNextElement().inAct();
        } else {
            numExitElem++;
        }

    }

    public void addProbBranch(Double prob, Element nextElement) {
        propability.put(prob, nextElement);
    }

    @Override
    public Element getNextElement() {
        double randomValue = Math.random();
        for (Double keyProb : propability.keySet()) {
            if (randomValue < keyProb) {
                return propability.get(keyProb);
            }
        }
        return null;
    }

    private void releaseDevice() {
        for(int i = 0; i < devicesTime.length; i++) {
            if (devicesTime[i] == super.getTcurr()) {
                devicesTime[i] = 0.0;
                loadedDevices--;
                break;
            }
        }
    }

    private void takeFreeDevice()  {
        for(int i = 0; i < devicesTime.length; i++) {
            if (devicesTime[i] == 0.0) {
                devicesTime[i] = super.getTcurr() + super.getDelay();
                loadedDevices++;
                break;
            }
        }
    }

    private void calculateAndSetTnext() {
        double Tnext = Double.MAX_VALUE;
        for(int i = 0; i < devicesTime.length; i++) {
            if (devicesTime[i] > 0 && devicesTime[i] < Tnext) {
                Tnext = devicesTime[i];
            }
        }
        super.setTnext(Tnext);
    }

    public int getFailure() {
        return failure;
    }

    public void addRestFailure(int restElements) {
        failure += restElements;
    }

    public int getLoadedDevices() {
        return loadedDevices;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public int getMaxqueue() {
        return maxqueue;
    }

    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }

    @Override
    public void printInfo() {
        System.out.println();
        super.printInfo();
        System.out.println("failure = " + this.getFailure());
    }

    @Override
    public void printResult() {
        super.printResult();
        System.out.println("number of exit elements = " + numExitElem);
    }

    @Override
    public void doStatistics(double delta) {
        meanQueue = getMeanQueue() + queue * delta;
        meanLoadedDevices = meanLoadedDevices + loadedDevices * delta;
    }

    public double getMeanLoadedDevices() {
        return meanLoadedDevices;
    }

    public double getMeanQueue() {
        return meanQueue;
    }
}

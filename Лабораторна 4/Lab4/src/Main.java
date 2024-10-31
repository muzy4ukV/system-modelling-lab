import core.*;
import core.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class Main {
    public static void main(String[] args) {
        int[] N_tasks = {60, 99, 201, 300, 402, 501, 600, 702, 801, 900};

//        Model MMO = createSimpleMMO(10);
//        MMO.simulate(true);

//        Model MMO = createBinaryTreeMMO(12);
//        MMO.simulate(true);

        //testMMO(N_tasks, "Simple", Main::createSimpleMMO);
        //testMMO(N_tasks, "Complex", Main::createComplexMMO);
        testMMO(new int[]{48, 96, 192, 384, 768, 1536, 3072, 6144}, "BinaryTree", Main::createBinaryTreeMMO);
    }

    public static void testMMO(int[] N_tasks, String name, Function<Integer, Model> func) {
        System.out.printf("\n-------------%s MMO-------------\n", name);
        for(int N: N_tasks) {
            Model MMO = func.apply(N);
            double simulationTime = MMO.simulate(false);
            System.out.printf("For N = %d simulation time is - %.2f%n", N, simulationTime);
        }
    }


    public static Model createSimpleMMO(int N) {
        Create creator = new Create(1.0, N + 1);
        creator.setDistribution("exp");
        creator.setName("CREATOR");

        ArrayList<Element> listModel = new ArrayList<>();
        listModel.add(creator);

        Process p_previous = getProcess(1.0, 1, "exp", Integer.MAX_VALUE);
        p_previous.setName("PROCESS 0");
        creator.setNextElement(p_previous);
        listModel.add(p_previous);

        Process p_current = null;
        for(int i = 1; i < N; i++) {
            p_current = getProcess(1.0, 1, "exp", Integer.MAX_VALUE);
            p_current.setName("PROCESS " + i);

            p_previous.addProbBranch(1.0, p_current);
            listModel.add(p_current);
            p_previous = p_current;
        }

        Despose despose = new Despose();
        p_current.addProbBranch(1.0, despose);
        listModel.add(despose);

        return new Model(listModel);
    }

    public static Process getProcess(double delay, int numDevice, String dist, int maxQueue) {
        Process p = new Process(delay, numDevice);
        p.setDistribution(dist);
        p.setMaxqueue(maxQueue);
        return p;
    }

    public static Model createComplexMMO(int N) {
        Create creator = new Create(1.0, N + 1);
        creator.setDistribution("exp");
        creator.setName("CREATOR");

        ArrayList<Element> listModel = new ArrayList<>();
        listModel.add(creator);

        Process headElement = getProcess(1.0, 1, "exp", Integer.MAX_VALUE);
        headElement.setName("PROCESS 1");
        Process previousLeft = getProcess(1.0, 2, "exp", Integer.MAX_VALUE);
        previousLeft.setName("PROCESS 2");
        Process previousRight = getProcess(1.0, 2, "exp", Integer.MAX_VALUE);
        previousRight.setName("PROCESS 3");
        listModel.add(headElement);
        listModel.add(previousLeft);
        listModel.add(previousRight);

        creator.setNextElement(headElement);
        headElement.addProbBranch(0.5, previousLeft);
        headElement.addProbBranch(1.0, previousRight);


        for(int i = 3; i < N; i += 3) {
            headElement = getProcess(1.0, 1, "exp", Integer.MAX_VALUE);
            headElement.setName("PROCESS " + (i+1));
            Process leftElement = getProcess(1.0, 2, "exp", Integer.MAX_VALUE);
            leftElement.setName("PROCESS " + (i+2));
            Process rightElement = getProcess(1.0, 2, "exp", Integer.MAX_VALUE);
            rightElement.setName("PROCESS " + (i+3));
            listModel.add(headElement);
            listModel.add(leftElement);
            listModel.add(rightElement);

            headElement.addProbBranch(0.5, leftElement);
            headElement.addProbBranch(1.0, rightElement);

            previousLeft.addProbBranch(1.0, headElement);
            previousRight.addProbBranch(1.0, headElement);

            previousLeft = leftElement;
            previousRight = rightElement;
        }

        Despose despose = new Despose();
        previousLeft.addProbBranch(1.0, despose);
        previousRight.addProbBranch(1.0, despose);
        listModel.add(despose);

        return new Model(listModel);
    }

    public static Model createBinaryTreeMMO(int N) {
        Create creator = new Create(1.0, N + 1);
        creator.setDistribution("exp");
        creator.setName("CREATOR");

        ArrayList<Element> listModel = new ArrayList<>();
        listModel.add(creator);

        ArrayList<Process> previousLayer = new ArrayList<>();
        previousLayer.add(getProcess(1.0, 1, "exp", Integer.MAX_VALUE));
        listModel.addAll(previousLayer);
        creator.setNextElement(previousLayer.getFirst());

        for(int i = 2; i < N; i *= 2) {

            ArrayList<Process> currLayer = new ArrayList<>();
            for(int j = 0; j < i; j++) {
                currLayer.add(getProcess(1.0, 1, "exp", Integer.MAX_VALUE));
            }

            int flag = 0;
            for(int k = 0; k < previousLayer.size(); k++) {
                previousLayer.get(k).addProbBranch(0.5, currLayer.get(k+flag));
                previousLayer.get(k).addProbBranch(1.0, currLayer.get(k+flag+1));
                flag++;
            }

            listModel.addAll(currLayer);
            previousLayer = currLayer;
        }

        Despose despose = new Despose();
        for(Process p: previousLayer) {
            p.addProbBranch(1.0, despose);
        }
        listModel.add(despose);

        return new Model(listModel);
    }
}
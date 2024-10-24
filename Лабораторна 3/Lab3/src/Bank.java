import ModelConstructor.*;
import bank.*;


public class Bank {
    public static void main(String[] args) {
        var create = new Create("Create #1", 0.5, 0.1);
        var cashierWindow1 = new SwitchingProcess("Cashier window #1", 1, 0.3, 1, 2);
        var cashierWindow2 = new SwitchingProcess("Cashier window #2", 1, 0.3, 1, 2);
        var dispose = new Dispose("Dispose #1");

        cashierWindow1.initializeChannelsWithJobs(1);
        cashierWindow1.initializeQueueWithJobs(2);
        cashierWindow1.setNeighbors(cashierWindow2);
        cashierWindow2.initializeChannelsWithJobs(1);
        cashierWindow2.initializeQueueWithJobs(2);
        cashierWindow2.setNeighbors(cashierWindow1);

        create.setDistribution("exp");
        cashierWindow1.setDistribution("exp");
        cashierWindow1.setDelayMean(0.3);
        cashierWindow2.setDistribution("exp");
        cashierWindow2.setDelayMean(0.3);

        cashierWindow1.setMaxQueueSize(3);
        cashierWindow2.setMaxQueueSize(3);

        create.setRouting(Routing.BY_PRIORITY);
        create.addRoutes(
                new Route(cashierWindow1, 0.5, 1, (Job job) -> cashierWindow2.getQueueSize() < cashierWindow1.getQueueSize()),
                new Route(cashierWindow2, 0.5, 0)
        );

        cashierWindow1.addRoutes(
                new Route(dispose)
        );

        cashierWindow2.addRoutes(
                new Route(dispose)
        );

        var model = new BankModel(create, cashierWindow1, cashierWindow2, dispose);
        model.simulate(1000);
    }
}

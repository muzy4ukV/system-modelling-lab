import bank.BankModel;
import bank.SwitchingProcess;
import clinic.*;
import core.Process;
import core.*;

public class Main {
    public static void main(String[] args) {
        // bank();
        clinic();
    }

    public static void bank() {
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

    public static void clinic() {
        final int[] patientTypes = {1, 2, 3};
        final double[] patientFrequencies = {0.5, 0.1, 0.4};
        final double[] patientDelays = {15, 40, 30};

        var create = new PatientCreate("Patient Creator", 15);
        var registration = new RegistrationProcess("Registration", 15, 2);
        var wardsTransfer = new Process("Wards Transfer", 3, 8, 3);
        var laboratoryTransfer = new Process("Laboratory Transfer", 2, 5, 100);
        var laboratoryRegistration = new Process("Laboratory Registration", 4.5, 3, 1);
        var laboratoryAnalysis = new TypeModifyingProcess("Laboratory Analysis", 4, 2, 2);
        var registrationTransfer = new Process("Registration Transfer", 2, 5, 100);

        var wardsDispose = new Dispose("Dispose [Type 1 & 2]");
        var laboratoryDispose = new Dispose("Dispose [Type 3]");


        create.setPatientTypedFrequencies(patientTypes, patientFrequencies);
        registration.setPatientTypedDelays(patientTypes, patientDelays);
        registration.setPrioritizedPatientType(1);
        laboratoryAnalysis.setTypeModifyingMap(
                new int[] {2},
                new int[] {1}
        );

        create.setDistribution("exp");
        registration.setDistribution("exp");
        wardsTransfer.setDistribution("uniform");
        laboratoryTransfer.setDistribution("uniform");
        laboratoryRegistration.setDistribution("erlang");
        laboratoryAnalysis.setDistribution("erlang");
        registrationTransfer.setDistribution("uniform");

        create.addRoutes(
                new Route(registration)
        );
        registration.addRoutes(
                new Route(wardsTransfer, 0.5, 1, (Job job) -> ((Patient) job).getType() != 1),
                new Route(laboratoryTransfer, 0.5, 0)
        );
        registration.setRouting(Routing.BY_PRIORITY);
        wardsTransfer.addRoutes(
                new Route(wardsDispose)
        );
        laboratoryTransfer.addRoutes(
                new Route(laboratoryRegistration)
        );
        laboratoryRegistration.addRoutes(
                new Route(laboratoryAnalysis)
        );
        laboratoryAnalysis.addRoutes(
                new Route(laboratoryDispose, 0.5, 1, (Job job) -> ((Patient) job).getType() != 3),
                new Route(registrationTransfer, 0.5, 0)
        );
        laboratoryAnalysis.setRouting(Routing.BY_PRIORITY);
        registrationTransfer.addRoutes(
                new Route(registration)
        );

        var model = new ClinicModel(create, registration, wardsTransfer, laboratoryTransfer, laboratoryRegistration,
                laboratoryAnalysis, registrationTransfer, wardsDispose, laboratoryDispose);
        model.simulate(1000);
    }
}
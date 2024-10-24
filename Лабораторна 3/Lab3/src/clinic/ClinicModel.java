package clinic;

import ModelConstructor.Dispose;
import ModelConstructor.Element;
import ModelConstructor.Job;

import java.util.ArrayList;

public class ClinicModel extends ModelConstructor.Model {
    public ClinicModel(Element... elements) {
        super(elements);
    }

    private double getLaboratoryArrivalInterval() {
        for (var element : elements) {
            if (element.getName().equals("Laboratory Transfer")) {
                return ((ModelConstructor.Process) element).getMeanLeaveInterval();
            }
        }
        return 0.0;
    }

    @Override
    public void printResult() {
        System.out.println("\n------------- RESULTS -------------");
        printPatientInfo();
        System.out.println("\n------------ STATISTICS -----------");
        System.out.printf("Mean time in system (processed): %.2f%n", getMeanTimeInSystem());
        System.out.printf("Mean laboratory arrival interval: %.2f%n", getLaboratoryArrivalInterval());
    }

    private void printPatientInfo() {
        System.out.println("\n------------ PATIENTS ------------");
        for (var element : elements) {
            if (element instanceof Dispose d) {
                var patients = d.getProcessedJobs();
                for (var patient : patients) {
                    System.out.printf("Patient %-4d | Type: %-2d | Time In: %-8.2f | Time Out: %-8.2f | Time in System: %-8.2f%n",
                            patient.getId(),
                            ((Patient) patient).getType(),
                            patient.getTimeIn(),
                            patient.getTimeOut(),
                            (patient.getTimeOut() - patient.getTimeIn())
                    );
                }
            }
        }
    }

    private double getMeanTimeInSystem() {
        var patients = new ArrayList<Job>();
        for (var element : elements) {
            if (element instanceof Dispose d) {
                patients.addAll(d.getProcessedJobs());
            }
        }
        var sum = 0.0;
        for (var patient : patients) {
            sum += patient.getTimeOut() - patient.getTimeIn();
        }
        return sum / patients.size();
    }
}

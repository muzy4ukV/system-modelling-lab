package clinic;

import ModelConstructor.Create;
import ModelConstructor.Job;

import java.util.HashMap;

public class PatientCreate extends Create {

    private HashMap<Integer, Double> patientTypedFrequencies;

    public PatientCreate(String name, double delay) {
        super(name, delay);
    }

    public void setPatientTypedFrequencies(int[] types, double[] frequencies) {
        this.patientTypedFrequencies = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            this.patientTypedFrequencies.put(types[i], frequencies[i]);
        }
    }

    @Override
    protected Job createJob() {
        var type = choosePatientType();
        return new Patient(super.getTCurr(), type);
    }

    private int choosePatientType() {
        var random = Math.random();
        var sum = 0.0;
        for (var entry : patientTypedFrequencies.entrySet()) {
            sum += entry.getValue();
            if (random < sum) {
                return entry.getKey();
            }
        };
        return 0;
    }
}

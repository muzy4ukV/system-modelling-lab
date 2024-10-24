package clinic;

import core.Job;

public class Patient extends Job {
    int type;

    public Patient(double timeIn, int type) {
        super(timeIn);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

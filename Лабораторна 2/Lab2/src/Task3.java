import ModelConstructor.*;

import java.util.ArrayList;

public class Task3 {
    public static void main(String[] args) {
        Create c = new Create(4.0);
        Service s1 = new Service(1.0);
        Service s2 = new Service(1.0);
        Service s3 = new Service(1.0);

        c.setNextElement(s1);
        s1.setNextElement(s2);
        s2.setNextElement(s3);

        c.setName("CREATOR");
        s1.setName("PROCESS 1");
        s2.setName("PROCESS 2");
        s3.setName("PROCESS 3");

        c.setDistribution("exp");
        s1.setDistribution("exp");
        s2.setDistribution("exp");
        s3.setDistribution("exp");

        s1.setMaxqueue(5);
        s2.setMaxqueue(5);
        s3.setMaxqueue(5);

        ArrayList<Element> list_model = new ArrayList<>();
        list_model.add(c);
        list_model.add(s1);
        list_model.add(s2);
        list_model.add(s3);

        Model model = new Model(list_model);
        model.simulate(1000.0);
    }
}

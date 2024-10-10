import ModifiedConstructor.*;

import java.util.ArrayList;


public class Task4 {
    public static void main(String[] args) {
        Create c = new Create(1.0);
        Service s1 = new Service(2.0, 2);
        Service s2 = new Service(2.0, 2);

        c.setNextElement(s1);
        s1.setNextElement(s2);
        c.setName("CREATOR");
        s1.setName("SMO 1");
        s2.setName("SMO 2");

        c.setDistribution("exp");
        s1.setDistribution("exp");
        s2.setDistribution("exp");
        s1.setMaxqueue(5);
        s2.setMaxqueue(5);

        ArrayList<Element> list_model = new ArrayList<>();
        list_model.add(c);
        list_model.add(s1);
        list_model.add(s2);

        Model model = new Model(list_model);
        model.simulate(1000.0);
    }

}

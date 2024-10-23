import ModelConstructor.*;

import java.util.ArrayList;


public class Task1_2 {
    public static void main(String[] args) {
        Create c = new Create(2.0);
        Service s = new Service(2.0);
        System.out.println("id0 = " + c.getId() + " id1 = " + s.getId());

        c.setNextElement(s);
        s.setMaxqueue(5);
        c.setName("CREATOR");
        s.setName("PROCESSOR");
        c.setDistribution("exp");
        s.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(s);

        Model model = new Model(list);
        model.simulate(1000.0);
    }
}
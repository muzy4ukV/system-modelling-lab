package core;

public class Create extends Element {
    private final int maxTasks; // максимальна кількість завдань створена класом
    public Create(double delay, int maxTasks) {
        super(delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create
        this.maxTasks = maxTasks;
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());
        if(getQuantity() >= maxTasks) {
            super.setTnext(Integer.MAX_VALUE);
        }
        super.getNextElement().inAct();
    }

    @Override
    public void printResult() {
        System.out.println("Number of created tasks by " + this.getName() + " = " + this.getQuantity());
    }
}

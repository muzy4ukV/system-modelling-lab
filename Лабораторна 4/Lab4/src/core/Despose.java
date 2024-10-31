package core;

public class Despose extends Element {

    public Despose() {
        super("DESPOSE", 0.0);
        setTnext(Integer.MAX_VALUE);

    }

    @Override
    public void inAct() {
        super.outAct();
    }

    @Override
    public void printInfo() {
        System.out.println("Number of done tasks by " + getName() + " = " + getQuantity());
    }
}

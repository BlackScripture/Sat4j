import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class main {

    public static void main(String[] args) throws ContradictionException, TimeoutException {


        // Erstelle Solver
        WeightedMaxSatDecorator solver = new WeightedMaxSatDecorator(SolverFactory.newDefault());

        solver.addHardClause(new VecInt(new int[]{1}));
        solver.addHardClause(new VecInt(new int[]{-1}));
        //solver.addHardClause(new VecInt(new int[]{1,2}));

        if (solver.isSatisfiable()) {
            int[] model = solver.model();
            if (model[1 - 1] > 0) {
                System.out.println(model.toString());
            }
        }


    }




}

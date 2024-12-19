import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.specs.IVecInt;

import java.util.*;

public class RowSolverHardHardSoft {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Input value n for Puzzle of n x n size: ");
        int dimension = scanner.nextInt(); // dimension x dimension puzzle gets generated

        System.out.print("Input value c for number of different colors appearing in the puzzle: ");
        int howManyDifferentColors = scanner.nextInt(); // howManyDifferentColors will be used in Puzzle

        scanner.close();

        System.out.println("Puzzle of size: " + dimension + "x" + dimension + " will be created");
        System.out.println(howManyDifferentColors + " different colors will be used in Puzzle");

        // create object of puzzle-manager class
        PuzzleManager puzzlemanager = new PuzzleManager();

        PuzzleAndPieces puzzleandpieces = puzzlemanager.createPuzzleAndPieces(dimension, howManyDifferentColors);

        PuzzleField[] fields = puzzleandpieces.puzzle;
        PuzzlePiece[] pieces = puzzleandpieces.pieces;


        // List with all the solutions with only horizontal adjacency taken into account
        List<List<List<List<String>>>> currentRows = new ArrayList<>(); // row , unique solution for row, different permutations for unique solution, placement in row

        // List of previous Placements for whole puzzle, to forbid the placement and search for new solution
        List<List<String>> previousRows = new ArrayList<>();

        long start = System.nanoTime();

        solveRows(pieces, fields, dimension, currentRows,previousRows, 1); //searches for a placement for the whole puzzle with only
                                                                                       // horizontal adjacency matching

        long end = System.nanoTime();
        long elapsedTime = end - start;
        double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
        System.out.println("Runtime:" + elapsedTimeInSecond + " seconds");


    }

    public static void solveRows(PuzzlePiece[] pieces,
                                 PuzzleField[] fields,
                                 int dimension,
                                 List<List<List<List<String>>>> currentRows,
                                 List<List<String>> previousRows,
                                 int numberOfCalls) {

        //PseudoOptDecorator solver = new PseudoOptDecorator(SolverFactory.newDefault());
        // Obergrenzen für Variablen und Klauseln festlegen
        //solver.newVar(10); // Maximal 10 Variablen
        //solver.setExpectedNumberOfClauses(5); // Maximal 5 Klauseln


        // create solver
        WeightedMaxSatDecorator solver = new WeightedMaxSatDecorator(SolverFactory.newDefault());

        var placement = new String[fields.length];
        var placementIndex = 0;
        var singleRowPlacement = new String[dimension];
        var singleRowPlacementIndex = 0;

        try {

            System.out.println("Searching for RowPlacements via hardClauses");

            // Erstelle Hashmap um später alle Teile in allen Rotationen auf die Felder zu mappen
            Map<String, Integer> varMap = new HashMap<>();
            int varCount = 1; // Unique Hasmap id

            // create variables for the solver
            // variables get created and accessed via a hashmap
            for (int feld_index = 0; feld_index < fields.length; feld_index++) {            // field_index iterates through every field
                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {     // piece_index iterates through every piece (without considerating rotation)
                    for ( int rotation = 0; rotation < 4; rotation++ ) {                    // iterating through all 4 rotations
                        // create variables for every field combined with every piece in all rotations
                        varMap.put("field_" + feld_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation, varCount++);
                        solver.newVar(varCount); // Create new variable for the solver
                    }
                }
            }


            //iterates through every previous placement i for whole puzzle, iterates through the every field_piece_rotation j in this specific placement j
            for ( int i = 0; i < previousRows.size(); i++){
                IVecInt clause = new VecInt();                               //clause forbids one of the previous placements for the whole puzzle, so that a new puzzle can be searched
                for ( int j = 0; j < previousRows.get(i).size(); j++) {
                    clause.push(-varMap.get(previousRows.get(i).get(j)));
                }
                solver.addHardClause(clause);
            }

            // every field needs at least 1 piece
            for (int field_index = 0; field_index < fields.length; field_index++) {
                IVecInt clause = new VecInt();
                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                    for ( int rotation = 0; rotation < 4; rotation++) {
                        // entnehme der Hashmap alle Zuweisungen, pushe diese als Klauseln. Schließlich erfolgt mindestens eine Puzzleteil Zuweisung
                        clause.push(varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation));
                    }
                }
                solver.addHardClause(clause);
            }

            // every piece can only be used once
            for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                for (int rotation_on_field_1 = 0; rotation_on_field_1 < 4; rotation_on_field_1++) {
                    for (int field_index_1 = 0; field_index_1 < fields.length; field_index_1++) {
                        for (int rotation_on_field_2 = 0; rotation_on_field_2 < 4; rotation_on_field_2++) {
                            for (int field_index_2 = 0; field_index_2 < fields.length; field_index_2++) {
                                // if piece is already placed on a field, forbid placement on any other field in any rotation
                                if ( field_index_1 != field_index_2 ) {
                                    solver.addHardClause(new VecInt(new int[]{
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_1),
                                            -varMap.get("field_" + field_index_2 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_2)
                                    }));
                                }
                                // if piece is already placed, forbid placement of same piece in different rotation on the same field
                                if (field_index_1 == field_index_2 && rotation_on_field_1 != rotation_on_field_2) {
                                    solver.addHardClause(new VecInt((new int[]{
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_1),
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_2)
                                    })));
                                }
                            }
                        }
                    }
                }
            }

            // righthand neighbour has to have same color
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int y = fields[field_index].y;

                    // neighbour on the right (x, y+1)
                    if (y + 1 < dimension) { // check if current field has a neighbour on the right side
                        for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                            for (int rotation_piece_1 = 0; rotation_piece_1 < 4; rotation_piece_1++) {
                                for (int piece_index_2 = 0; piece_index_2 < pieces.length; piece_index_2++) {
                                    for (int rotation_piece_2 = 0; rotation_piece_2 < 4; rotation_piece_2++) {
                                        // only when different pieces are neighbours
                                        if (piece_index_1 != piece_index_2) {
                                            // border colors of left and right piece have to be the same when adjacent, if not forbid placement of one of the two
                                            int leftVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_piece_1);
                                            int rightVar = varMap.get("field_" + (field_index + 1) + "_piece_" + pieces[piece_index_2].piece_id + "_rotation_" + rotation_piece_2);

                                            int countWeight = 0;
                                            if (pieces[piece_index_1].edges[rotation_piece_1][1] != pieces[piece_index_2].edges[rotation_piece_2][3]) {
                                                countWeight++;
                                            }
                                            if (pieces[piece_index_1].symbols[rotation_piece_1][1] == 1 && pieces[piece_index_2].symbols[rotation_piece_2][3] == 1) {
                                                countWeight++;
                                            }else if (pieces[piece_index_1].symbols[rotation_piece_1][1] == 2 && pieces[piece_index_2].symbols[rotation_piece_2][3] == 2){
                                                countWeight++;
                                            }
                                            if ( countWeight > 0 ){
                                                solver.addSoftClause(countWeight,new VecInt(new int[]{-leftVar, -rightVar}));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            }

            // border color 0 has to be placed on the border
            // forbid != 0 on border
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int x = fields[field_index].x;
                int y = fields[field_index].y;

                    for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                        for (int rotation = 0; rotation < 4; rotation++) {
                            int pieceVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);

                            // check if border is colored and would be placed on border

                            if (pieces[piece_index].edges[rotation][0] != 0 && x == 0) { // top border
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                            if (pieces[piece_index].edges[rotation][1] != 0 && y == ((int) Math.sqrt(fields.length)) - 1) { // right border
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                            if (pieces[piece_index].edges[rotation][2] != 0 && x == ((int) Math.sqrt(fields.length)) - 1) { // bottom border
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                            if (pieces[piece_index].edges[rotation][3] != 0 && y == 0) { // left border
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                        }
                    }
            }

            var index = 0;
            var subString1 = "";
            var subString2 = "";
            var subString3 = "";
            var subString4 = "";
            var subString5 = "";
            var subString6 = "";
            var subString7 = "";
            var outputstring = "";

            // Lösung finden und ausgeben
            if (solver.isSatisfiable()) {
                int[] model = solver.model();
                System.out.println("Zuweisung der Puzzleteile zu den Feldern:");
                for (int field_index = 0; field_index < fields.length; field_index++) {
                    for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                        for (int rotation = 0; rotation < 4; rotation++) {
                            int var = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);
                            if (model[var - 1] > 0) { // Wenn die Variable positiv ist, ist das Puzzleteil zugewiesen

                                //neu
                                if (index <= fields.length) {
                                    subString1 = subString1 + "------------------";
                                    subString2 = subString2 + "/       " + pieces[piece_index].edges[rotation][0] + "        /";
                                    subString3 = subString3 + "/       " + pieces[piece_index].symbols[rotation][0] + "        /";
                                    if ( pieces[piece_index].piece_id < 10) {
                                        subString4 = subString4 + "/  " + pieces[piece_index].edges[rotation][3] + " " + pieces[piece_index].symbols[rotation][3] + " (" + pieces[piece_index].piece_id + ") " + pieces[piece_index].symbols[rotation][1] + " " + pieces[piece_index].edges[rotation][1] + "   /";
                                    }else{
                                        subString4 = subString4 + "/  " + pieces[piece_index].edges[rotation][3] + " " + pieces[piece_index].symbols[rotation][3] + " (" + pieces[piece_index].piece_id + ") " + pieces[piece_index].symbols[rotation][1] + " " + pieces[piece_index].edges[rotation][1] + "  /";
                                    }
                                    subString5 = subString5 + "/       " + pieces[piece_index].symbols[rotation][2] + "        /";
                                    subString6 = subString6 + "/       " + pieces[piece_index].edges[rotation][2] + "        /";
                                    subString7 = subString7 + "------------------";
                                    index++;
                                    if (index % (int) Math.sqrt(fields.length) == 0){
                                        outputstring = outputstring + "\n" + subString1 + "\n" + subString2 + "\n" + subString3 + "\n" + subString4 + "\n" + subString5 + "\n" + subString6 + "\n" + subString7;
                                        subString1 = "";
                                        subString2 = "";
                                        subString3 = "";
                                        subString4 = "";
                                        subString5 = "";
                                        subString6 = "";
                                        subString7 = "";
                                    }
                                }
                                //neu

                                System.out.println("Feld (" + fields[field_index].x + ", " + fields[field_index].y + ") -> Puzzleteil " + pieces[piece_index].piece_id + " Rotation " + rotation);
                                placement[placementIndex] = ("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);
                                placementIndex++;
                            }
                        }
                    }
                }
            } else {
                System.out.println("Keine Lösung gefunden.");
            }
            //neu
            System.out.println(outputstring);
            //neu

            if (placement[0] != null) {
                PlacementManager placementManager = new PlacementManager();

                //Auswertung der gefundenen Belegung
                int violations = placementManager.countViolations(fields, pieces, dimension, placement);
                System.out.println("Anzahl falscher Paare und außen != grau: " + violations);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

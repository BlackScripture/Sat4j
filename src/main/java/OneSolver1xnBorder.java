import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.specs.IVecInt;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OneSolver1xnBorder {

    public static void main(String[] args) {

        //Gib Dimensionierung und Anzahl der Darben ein
        Scanner scanner = new Scanner(System.in);

        System.out.print("Gib einen Wert für die Dimensionierung ein: ");
        int ersterWert = scanner.nextInt();

        System.out.print("Gib einen Wert für die Anzahl möglicher auftretender Farben ein: ");
        int zweiterWert = scanner.nextInt();

        scanner.close();

        System.out.println("Dimensionierung: " + ersterWert + "x" + ersterWert);
        System.out.println("Anzahl möglicher verschiedener Farben: " + zweiterWert);

        // Erstelle ein Objekt der puzzleManager-Klasse
        PuzzleManager puzzlemanager = new PuzzleManager();

        PuzzleAndPieces puzzleandpieces = puzzlemanager.createPuzzleAndPieces_1xn(ersterWert, zweiterWert);

        /*
        //Testausgabe

        System.out.println(puzzleandpieces.puzzle.length);
        System.out.println(puzzleandpieces.pieces.length);

        for ( Puzzle field : puzzleandpieces.puzzle){
            System.out.println("(" + field.x + " " + field.y +")");
        }
        for ( PuzzlePiece piece : puzzleandpieces.pieces){
            System.out.println(piece.piece_id);
        }

         */

        PuzzleField[] fields = puzzleandpieces.puzzle;
        PuzzlePiece[] pieces = puzzleandpieces.pieces;

/*
        //Ersetze erstes Puzzleteil durch ein Puzzleteil, das in keinem Fall richtig platziert werden kann
        int[][] newedges = {{9,9,9,9},{9,9,9,9},{9,9,9,9},{9,9,9,9}};
        int[][] newsymbols = {{9,9,9,9},{9,9,9,9},{9,9,9,9},{9,9,9,9}};
        pieces[0] = new PuzzlePiece(pieces[0].piece_id, newedges, newsymbols);

 */





        long start = System.nanoTime();

        // Erstelle Solver
        WeightedMaxSatDecorator solver = new WeightedMaxSatDecorator(SolverFactory.newDefault());


        var placement = new String[fields.length];
        var placementIndex = 0;



        try {
            // Erstelle Hashmap um später alle Teile in allen Rotationen auf die Felder zu mappen
            Map<String, Integer> varMap = new HashMap<>();
            int varCount = 1; // Unique Hasmap id

            // Variablen definieren, um jedes Feld genau einem Puzzleteil zuzuweisen
            // füge sie anschließend der Hashmap hinzu
            for (int feld_index = 0; feld_index < fields.length; feld_index++) {            // field_index iteriert durch jedes Feld
                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {     // piece_index iteriert durch alle Puzzleteile OHNE Rotation
                    for ( int rotation = 0; rotation < 4; rotation++ ) {
                        // mappe nun auf jedes Feld jedes Puzzleteil in jeder Rotation
                        varMap.put("field_" + feld_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation, varCount++);
                        solver.newVar(varCount); // Hier die Variablen im Solver registrieren
                    }
                }
            }
            //      System.out.println(varMap.toString());

            // Klauseln hinzufügen, die sicherstellen, dass jedes Feld genau ein Puzzleteil erhält
            for (int field_index = 0; field_index < fields.length; field_index++) {
                IVecInt clause = new VecInt();
                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                    for ( int rotation = 0; rotation < 4; rotation++) {
                        // entnehme der Hashmap alle Zuweisungen, pushe diese als Klauseln. Schließlich erfolgt mindestens eine Puzzleteil Zuweisung
                        clause.push(varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation));
                    }
                }
                solver.addSoftClause(clause);

                // Ausschluss von Mehrfachzuweisungen pro Feld von verschiedenen Puzzleteilen
                for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                    for (int piece_index_2 = 0; piece_index_2 < pieces.length; piece_index_2++) {
                        for (int rotation_piece_1 = 0; rotation_piece_1 < 4; rotation_piece_1++) {
                            for (int rotation_piece_2 = 0; rotation_piece_2 < 4; rotation_piece_2++) {
                                if ( piece_index_1 != piece_index_2 ) {
                                    solver.addSoftClause(new VecInt(new int[]{
                                            // Mindestens eine Negation muss wahr sein, sprich wenn ein Puzzleteil auf einem Feld liegt, kann
                                            // auf dem gleichen Feld kein anderes Puzzleteil mehr platziert werden
                                            -varMap.get("field_" + field_index + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_piece_1),
                                            -varMap.get("field_" + field_index + "_piece_" + pieces[piece_index_2].piece_id + "_rotation_" + rotation_piece_2)
                                    }));
                                }
                            }
                        }
                    }
                }
            }

            // Jedes Puzzleteil darf nur genau einmal verwendet werden / Ausschluss von erneutem nutzen in irgendeiner Rotation auf anderem Feld
            for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                for (int rotation_on_field_1 = 0; rotation_on_field_1 < 4; rotation_on_field_1++) {
                    for (int field_index_1 = 0; field_index_1 < fields.length; field_index_1++) {
                        for (int rotation_on_field_2 = 0; rotation_on_field_2 < 4; rotation_on_field_2++) {
                            for (int field_index_2 = 0; field_index_2 < fields.length; field_index_2++) {
                                if ( field_index_1 != field_index_2 ) {
                                    solver.addSoftClause(50,new VecInt(new int[]{
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_1),
                                            -varMap.get("field_" + field_index_2 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_2)
                                    }));
                                }
                                //Ausschluss Mehrfachzuweisung gleiches Teil, gleiches Feld, verschiedene Rotation
                                if (field_index_1 == field_index_2 && rotation_on_field_1 != rotation_on_field_2) {
                                    solver.addSoftClause(50,new VecInt((new int[]{
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_1),
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_2)
                                    })));
                                }
                            }
                        }
                    }
                }
            }



            // Benachbarte Felder müssen gleiche Kantenfarben haben
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int y = fields[field_index].y;

                // Nachbar rechts (x, y+1)
                if (y + 1 < ersterWert) { // Prüfe, ob das rechte Nachbarfeld im Raster liegt
                    for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                        for (int rotation_piece_1 = 0; rotation_piece_1 < 4; rotation_piece_1++) {
                            for (int piece_index_2 = 0; piece_index_2 < pieces.length; piece_index_2++) {
                                for (int rotation_piece_2 = 0; rotation_piece_2 < 4; rotation_piece_2++) {
                                    // Nur wenn zwei verschiedene Puzzleteile an benachbarten Feldern liegen
                                    if (piece_index_1 != piece_index_2) {
                                        // Die Kantenfarben des rechten und linken Puzzleteils müssen übereinstimmen
                                        int leftVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_piece_1);
                                        int rightVar = varMap.get("field_" + (field_index + 1) + "_piece_" + pieces[piece_index_2].piece_id + "_rotation_" + rotation_piece_2);
                                        if (pieces[piece_index_1].edges[rotation_piece_1][1] != pieces[piece_index_2].edges[rotation_piece_2][3]) {
                                            solver.addSoftClause(new VecInt(new int[]{-leftVar, -rightVar}));
                                            if (pieces[piece_index_1].symbols[rotation_piece_1][1] == pieces[piece_index_2].symbols[rotation_piece_2][3]) {
                                            }
                                        } else if (pieces[piece_index_1].symbols[rotation_piece_1][1] == pieces[piece_index_2].symbols[rotation_piece_2][3]) {
                                            solver.addSoftClause(new VecInt(new int[]{-leftVar, -rightVar}));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Kantenfarbe 0 muss immer am Rand liegen
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int y = fields[field_index].y;
                if ( y == 0 ) {
                    IVecInt clause = new VecInt();
                    for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                        for (int rotation = 0; rotation < 4; rotation++) {
                            if (pieces[piece_index].edges[rotation][3] == 0 && pieces[piece_index].edges[rotation][0] == 0 && pieces[piece_index].edges[rotation][2] == 0) {
                                clause.push(varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation));
                            }
                        }
                    }
                    solver.addSoftClause(clause);
                }
            }






/*









//positive zuweisung
            // Benachbarte Felder müssen gleiche Kantenfarben haben
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int x = fields[field_index].x;
                int y = fields[field_index].y;

                // Nachbar rechts (x, y+1)
                if (y + 1 < ((int) Math.sqrt(fields.length))) { // Prüfe, ob das rechte Nachbarfeld im Raster liegt
                    for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                        for (int rotation_piece_1 = 0; rotation_piece_1 < 4; rotation_piece_1++) {
                            for (int piece_index_2 = 0; piece_index_2 < pieces.length; piece_index_2++) {
                                for (int rotation_piece_2 = 0; rotation_piece_2 < 4; rotation_piece_2++) {
                                    // Nur wenn zwei verschiedene Puzzleteile an benachbarten Feldern liegen
                                    if (piece_index_1 != piece_index_2) {
                                        // Die Kantenfarben des rechten und linken Puzzleteils müssen übereinstimmen
                                        int leftVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_piece_1);
                                        int rightVar = varMap.get("field_" + (field_index + 1) + "_piece_" + pieces[piece_index_2].piece_id + "_rotation_" + rotation_piece_2);
                                        if (pieces[piece_index_1].edges[rotation_piece_1][1] == pieces[piece_index_2].edges[rotation_piece_2][3]) {
                                            solver.addSoftClause(new VecInt(new int[]{leftVar, rightVar}));
                                        } if (pieces[piece_index_1].symbols[rotation_piece_1][1] != pieces[piece_index_2].symbols[rotation_piece_2][3]) {
                                            solver.addSoftClause(new VecInt(new int[]{leftVar, rightVar}));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Nachbar unten (x+1, y)
                if (x + 1 < ((int) Math.sqrt(fields.length))) { // Prüfe, ob das untere Nachbarfeld im Raster liegt
                    for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                        for (int rotation_piece_1 = 0; rotation_piece_1 < 4; rotation_piece_1++) {
                            for (int piexe_index_2 = 0; piexe_index_2 < pieces.length; piexe_index_2++) {
                                for (int rotation_piece_2 = 0; rotation_piece_2 < 4; rotation_piece_2++) {
                                    // Nur wenn zwei verschiedene Puzzleteile an benachbarten Feldern liegen
                                    if (piece_index_1 != piexe_index_2) {
                                        // Die Kantenfarben des oberen und unteren Puzzleteils müssen übereinstimmen
                                        int topVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_piece_1);
                                        int bottomVar = varMap.get("field_" + (field_index + ((int) Math.sqrt(fields.length))) + "_piece_" + pieces[piexe_index_2].piece_id + "_rotation_" + rotation_piece_2);
                                        if (pieces[piece_index_1].edges[rotation_piece_1][2] == pieces[piexe_index_2].edges[rotation_piece_2][0]) {
                                            solver.addSoftClause(new VecInt(new int[]{-topVar, bottomVar}));
                                            solver.addSoftClause(new VecInt(new int[]{topVar, -bottomVar}));
                                        } if (pieces[piece_index_1].symbols[rotation_piece_1][2] != pieces[piexe_index_2].symbols[rotation_piece_2][0]){
                                            solver.addSoftClause(new VecInt(new int[]{-topVar, bottomVar}));
                                            solver.addSoftClause(new VecInt(new int[]{topVar, -bottomVar}));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }







            // Kantenfarbe 0 muss immer am Rand liegen
            // verbieten von =0 in Mitte
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int x = fields[field_index].x;
                int y = fields[field_index].y;

                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                    for (int rotation = 0; rotation < 4; rotation++) {
                        int pieceVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);

                        // Prüfe, ob das Puzzleteil eine 0-Kante hat und an einem Randfeld liegt
                        if (pieces[piece_index].edges[rotation][0] == 0 && x > 0) { // Oberkante
                            solver.addSoftClause( new VecInt(new int[]{-pieceVar}));
                        }
                        if (pieces[piece_index].edges[rotation][1] == 0 && y < ( (int) Math.sqrt(fields.length)) -1 ) { // rechte Kante
                            solver.addSoftClause( new VecInt(new int[]{-pieceVar}));
                        }
                        if (pieces[piece_index].edges[rotation][2] == 0 && x < ( (int) Math.sqrt(fields.length)) -1 ) { // Unterkante
                            solver.addSoftClause(new VecInt(new int[]{-pieceVar}));
                        }
                        if (pieces[piece_index].edges[rotation][3] == 0 && y > 0) { // linke Kante
                            solver.addSoftClause( new VecInt(new int[]{-pieceVar}));
                        }
                    }
                }
            }






            // Kantenfarbe 0 muss immer am Rand liegen
            // verbiete Belegung von !=0 am Rand
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int x = fields[field_index].x;
                int y = fields[field_index].y;

                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                    for (int rotation = 0; rotation < 4; rotation++) {
                        int pieceVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);

                        // Prüfe, ob das Puzzleteil eine 0-Kante hat und an einem Randfeld liegt
                        if (pieces[piece_index].edges[rotation][0] != 0 && x == 0) { // Oberkante
                            solver.addSoftClause(new VecInt(new int[]{-pieceVar}));
                        }
                        if (pieces[piece_index].edges[rotation][1] != 0 && y == ((int) Math.sqrt(fields.length)) - 1) { // rechte Kante
                            solver.addSoftClause(new VecInt(new int[]{-pieceVar}));
                        }
                        if (pieces[piece_index].edges[rotation][2] != 0 && x == ((int) Math.sqrt(fields.length)) - 1) { // Unterkante
                            solver.addSoftClause(new VecInt(new int[]{-pieceVar}));
                        }
                        if (pieces[piece_index].edges[rotation][3] != 0 && y == 0) { // linke Kante
                            solver.addSoftClause(new VecInt(new int[]{-pieceVar}));
                        }
                    }
                }

            }

 */



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
                                    if ( index == fields.length ){
                                        outputstring = outputstring + "\n" + subString1 + "\n" + subString2 + "\n" + subString3 + "\n" + subString4 + "\n" + subString5 + "\n" + subString6 + "\n" + subString7;
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

        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        if (placement[0] != null) {
            PlacementManager placementManager = new PlacementManager();

            //Auswertung der gefundenen Belegung
            int dimension = (int) Math.sqrt(fields.length);
            int violations = placementManager.countViolations(fields, pieces, dimension, placement);
            System.out.println("Anzahl falscher Paare und außen != grau: " + violations);
        }


 */
        if (placement[0] != null) {
            PlacementManager placementManager = new PlacementManager();

            //Auswertung der gefundenen Belegung
            int dimension = ersterWert;
            int violations = placementManager.countViolations1xnBorder(fields, pieces, dimension, placement);
            System.out.println("Anzahl falscher Paare und außen != grau: " + violations);
        }


        long end = System.nanoTime();
        long elapsedTime = end - start;
        double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
        System.out.println("Runtime:" + elapsedTimeInSecond + " seconds");






    }

}

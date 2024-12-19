import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PieceByPiece {

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

        PuzzleAndPieces puzzleandpieces = puzzlemanager.createPuzzleAndPieces(ersterWert, zweiterWert);

        PuzzleField[] fields = puzzleandpieces.puzzle;
        PuzzlePiece[] pieces = puzzleandpieces.pieces;


        // Liste mit Belegung gelöster Zeilen
        List<String> currentPlacementOnField = new ArrayList<>(); // Alle gegenwärtigen Vorbelegungen

        // Liste für verbotene Belegungen in den Zeilen
        List<List<String>> forbiddenPlacementOnField = new ArrayList<>(); // Feld, verschiedene verbotene Belegungen für Feld
        for (int i = 0; i < ersterWert * ersterWert; i++) {
            forbiddenPlacementOnField.add(new ArrayList<>());
        }

        // Field Index Zwischenspeicher
        int currentField_index = 0;

        long start = System.nanoTime();

        solveField(pieces, fields, ersterWert, currentPlacementOnField, forbiddenPlacementOnField, currentField_index);

        long end = System.nanoTime();
        long elapsedTime = end - start;
        double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
        System.out.println("Runtime:" + elapsedTimeInSecond + " seconds");


    }

    public static void solveField(PuzzlePiece[] pieces, PuzzleField[] fields, int ersterWert,  List<String> currentPlacementOnField, List<List<String>> forbiddenPlacementOnField, int currentFieldIndex) {

        // Erstelle Solver
        WeightedMaxSatDecorator solver = new WeightedMaxSatDecorator(SolverFactory.newDefault());

        var placement = new String[fields.length];
        var placementIndex = 0;
        var singleRowPlacement = new String[ersterWert];
        var singleRowPlacementIndex = 0;

        try {
/*
            System.out.println("Suche Lösung für Zeile " + currentRow);

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


            //Iteriere durch die verbotenen Belegungen der einzelnen Reihen und füge die verbotenen Belegungen als verbotene Belegungen hinzu
            for ( int i = 0; i < forbiddenRows.get(currentRow).size(); i++){             //iteriert durch die Anzahl der verbotenen Gesamtbelegungen der gegenwärtigen Reihe
                List<String> currentList = forbiddenRows.get(currentRow).get(i);
                IVecInt rowClause = new VecInt();
                for ( int k = 0; k < currentList.size(); k++){
                    rowClause.push(-varMap.get(currentList.get(k)));
                }
                solver.addHardClause(rowClause);
                //System.out.println("test Forbidden");
            }

            //Berücksichtige Belegungen zuvor gefundener Reihen, sofern vorhanden
            for ( int i = 0; i < solvedRows.size(); i++){
                List<String> currentList = solvedRows.get(i);
                for ( int k = 0; k < currentList.size(); k++){
                    solver.addHardClause(new VecInt(new int[]{varMap.get(currentList.get(k))}));
                }
            }



            // Klauseln hinzufügen, die sicherstellen, dass jedes Feld genau ein Puzzleteil erhält
            for (int field_index = 0; field_index < fields.length; field_index++) {
                IVecInt clause = new VecInt();
                for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                    for ( int rotation = 0; rotation < 4; rotation++) {
                        // entnehme der Hashmap alle Zuweisungen, pushe diese als Klauseln. Schließlich erfolgt mindestens eine Puzzleteil Zuweisung
                        clause.push(varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation));
                    }
                }
                solver.addHardClause(clause);

                // Ausschluss von Mehrfachzuweisungen pro Feld von verschiedenen Puzzleteilen
                for (int piece_index_1 = 0; piece_index_1 < pieces.length; piece_index_1++) {
                    for (int piece_index_2 = 0; piece_index_2 < pieces.length; piece_index_2++) {
                        for (int rotation_piece_1 = 0; rotation_piece_1 < 4; rotation_piece_1++) {
                            for (int rotation_piece_2 = 0; rotation_piece_2 < 4; rotation_piece_2++) {
                                if ( piece_index_1 != piece_index_2 ) {
                                    solver.addHardClause(new VecInt(new int[]{
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
                                    solver.addHardClause(new VecInt(new int[]{
                                            -varMap.get("field_" + field_index_1 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_1),
                                            -varMap.get("field_" + field_index_2 + "_piece_" + pieces[piece_index_1].piece_id + "_rotation_" + rotation_on_field_2)
                                    }));
                                }
                                //Ausschluss Mehrfachzuweisung gleiches Teil, gleiches Feld, verschiedene Rotation
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

            // Benachbarte Felder müssen gleiche Kantenfarben haben
            for (int field_index = 0; field_index < fields.length; field_index++) {
                int x = fields[field_index].x;
                int y = fields[field_index].y;

                if ( fields[field_index].x == currentRow ) {

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
                                            if (pieces[piece_index_1].edges[rotation_piece_1][1] != pieces[piece_index_2].edges[rotation_piece_2][3]) {
                                                solver.addHardClause(new VecInt(new int[]{-leftVar, -rightVar}));
                                            } else if (pieces[piece_index_1].symbols[rotation_piece_1][1] == pieces[piece_index_2].symbols[rotation_piece_2][3]) {
                                                solver.addHardClause(new VecInt(new int[]{-leftVar, -rightVar}));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                if (fields[field_index].x == currentRow-1) {
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
                                            if (pieces[piece_index_1].edges[rotation_piece_1][2] != pieces[piexe_index_2].edges[rotation_piece_2][0]) {
                                                solver.addHardClause(new VecInt(new int[]{-topVar, -bottomVar}));
                                            } if (pieces[piece_index_1].symbols[rotation_piece_1][2] == pieces[piexe_index_2].symbols[rotation_piece_2][0]){
                                                solver.addHardClause(new VecInt(new int[]{-topVar, -bottomVar}));
                                            }
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

                if ( fields[field_index].x == currentRow) {

                    for (int piece_index = 0; piece_index < pieces.length; piece_index++) {
                        for (int rotation = 0; rotation < 4; rotation++) {
                            int pieceVar = varMap.get("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);

                            // Prüfe, ob das Puzzleteil eine 0-Kante hat und an einem Randfeld liegt
                            if (pieces[piece_index].edges[rotation][0] == 0 && x > 0) { // Oberkante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            } else if (pieces[piece_index].edges[rotation][0] != 0 && x == 0) { // Oberkante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                            if (pieces[piece_index].edges[rotation][1] == 0 && y < ((int) Math.sqrt(fields.length)) - 1) { // rechte Kante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            } else if (pieces[piece_index].edges[rotation][1] != 0 && y == ((int) Math.sqrt(fields.length)) - 1) { // rechte Kante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                            if (pieces[piece_index].edges[rotation][2] == 0 && x < ((int) Math.sqrt(fields.length)) - 1) { // Unterkante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            } else if (pieces[piece_index].edges[rotation][2] != 0 && x == ((int) Math.sqrt(fields.length)) - 1) { // Unterkante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
                            if (pieces[piece_index].edges[rotation][3] == 0 && y > 0) { // linke Kante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            } else if (pieces[piece_index].edges[rotation][3] != 0 && y == 0) { // linke Kante
                                solver.addHardClause(new VecInt(new int[]{-pieceVar}));
                            }
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

                                if (index <= fields.length) {
                                    subString1 = subString1 + "------------------";
                                    subString2 = subString2 + "/       " + pieces[piece_index].edges[rotation][0] + "        /";
                                    subString3 = subString3 + "/       " + pieces[piece_index].symbols[rotation][0] + "        /";
                                    if (pieces[piece_index].piece_id < 10) {
                                        subString4 = subString4 + "/  " + pieces[piece_index].edges[rotation][3] + " " + pieces[piece_index].symbols[rotation][3] + " (" + pieces[piece_index].piece_id + ") " + pieces[piece_index].symbols[rotation][1] + " " + pieces[piece_index].edges[rotation][1] + "   /";
                                    } else {
                                        subString4 = subString4 + "/  " + pieces[piece_index].edges[rotation][3] + " " + pieces[piece_index].symbols[rotation][3] + " (" + pieces[piece_index].piece_id + ") " + pieces[piece_index].symbols[rotation][1] + " " + pieces[piece_index].edges[rotation][1] + "  /";
                                    }
                                    subString5 = subString5 + "/       " + pieces[piece_index].symbols[rotation][2] + "        /";
                                    subString6 = subString6 + "/       " + pieces[piece_index].edges[rotation][2] + "        /";
                                    subString7 = subString7 + "------------------";
                                    index++;
                                    if (index % (int) Math.sqrt(fields.length) == 0) {
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

                                // Ausgabe der Belegung aller Felder
                                System.out.println("Feld (" + fields[field_index].x + ", " + fields[field_index].y + ") -> Puzzleteil " + pieces[piece_index].piece_id + " Rotation " + rotation);
                                // Belegung aller Felder abspeichern für Auswertung
                                placement[placementIndex] = ("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);
                                placementIndex++;
                                // Belegung der gegenwärtig gelösten Zeile abspeichern
                                if (fields[field_index].x == currentRow) {
                                    singleRowPlacement[singleRowPlacementIndex] = ("field_" + field_index + "_piece_" + pieces[piece_index].piece_id + "_rotation_" + rotation);
                                    singleRowPlacementIndex++;
                                }
                            }
                        }
                    }
                }

                System.out.println(outputstring);

                // Später entfernen
///////////////////////////
                System.out.println("Belegung der gelösten Zeile:");
                for ( int i = 0; i < singleRowPlacement.length; i++){
                    System.out.println(singleRowPlacement[i]);
                }
                // Umwandlung in eine Liste
                List<String> tmpList = Arrays.asList(singleRowPlacement);
                System.out.println("Belegung als Liste: " + tmpList);

                System.out.println("Anzahl bisheriger Aufrufe des RowSolvers: " + numberOfCalls);
                numberOfCalls++;

                forbiddenRows.get(currentRow).add(tmpList);
                solvedRows.add(tmpList);

                System.out.println("\n//////////////////////////////////////////////////////////////////////////////////");
                System.out.println("//////////////////////////////////////////////////////////////////////////////////");
                System.out.println("//////////////////////////////////////////////////////////////////////////////////\n");

                if ( currentRow < ersterWert -1 ) {

                    solveRow(pieces, fields, ersterWert, currentRow + 1, solvedRows, forbiddenRows, numberOfCalls);

                } else if ( currentRow == ersterWert -1 ){

                    PlacementManager placementManager = new PlacementManager();

                    //Auswertung der gefundenen Belegung
                    int dimension = (int)Math.sqrt(fields.length);
                    int violations = placementManager.countViolations(fields, pieces, dimension, placement);
                    System.out.println("Gesamtzahl verletzter Eigenschaften: " + violations);
                }


////////////////////////////

            } else {
                if ( currentRow > 0){
                    //System.out.println("Anzahl gelöster Zeilen: " + solvedRows.size() + " Anzahl an Reihen mit verbotenen Belegungen: " + forbiddenRows.get(2).size());
                    solvedRows.remove(currentRow-1); // Löscht die Belegung aus der vorherigen Zeile um nach einer neuen Belegung zu suchen
                    forbiddenRows.get(currentRow).clear(); // Löscht die Belegung aller Verbote der gegenwärtigen Zeile für leere List bei Neuaufruf
                    System.out.println("Keine Lösung für Zeile " + currentRow + " gefunden. Gehe zurück zu Zeile: " + (currentRow-1) + " und suche nach einer neuen Belegung");

                    System.out.println("\n//////////////////////////////////////////////////////////////////////////////////");
                    System.out.println("//////////////////////////////////////////////////////////////////////////////////");
                    System.out.println("//////////////////////////////////////////////////////////////////////////////////\n");

                    solveRow(pieces, fields, ersterWert, currentRow - 1, solvedRows, forbiddenRows, numberOfCalls);

                } else {
                    System.out.println("Keine weitere Lösung für Zeile " + currentRow + " gefunden.");
                    System.exit(0);
                }

            }


 */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
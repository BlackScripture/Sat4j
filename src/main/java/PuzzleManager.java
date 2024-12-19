//PuzzleManager dient zum erstellen der Puzzleteile und der Felder

import java.util.*;

class PuzzlePiece {
    int piece_id; // ID eines Puzzleteils
    int[][] edges; // Kantenfarben in allen Rotationen, basically int[rotation][colors]
    int[][] symbols; // Kantensymbole in allen Rotationen, basically int[rotation][symbols]

    public PuzzlePiece(int id, int[][] edges, int[][] symbols) {
        this.piece_id = id;
        this.edges = edges;
        this.symbols = symbols;
    }
}

class PuzzleField {
    int x; // x-Koordinate eines PuzzleFeldes
    int y; // y-Koordinate eines Puzzlefeldes

    public PuzzleField(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class PuzzleAndPieces_1xn{
    PuzzleField[] puzzle; // Array mit allen Feldern
    PuzzlePiece[] pieces; // Array mit allen Puzzleteilen

    public PuzzleAndPieces_1xn(PuzzleField[] puzzle, PuzzlePiece[] pieces){
        this.puzzle = puzzle;
        this.pieces = pieces;
    }
}

class PuzzleAndPieces{
    PuzzleField[] puzzle; // Array mit allen Feldern
    PuzzlePiece[] pieces; // Array mit allen Puzzleteilen

    public PuzzleAndPieces(PuzzleField[] puzzle, PuzzlePiece[] pieces){
        this.puzzle = puzzle;
        this.pieces = pieces;
    }
}

public class PuzzleManager {

    public PuzzleAndPieces createPuzzleAndPieces(int ersterWert, int zweiterWert){

//Erstelle nun für jede Kantenkombination eine Farbe

        long seed = 64L;
        Random random = new Random(seed);
        System.out.println(random.nextInt(zweiterWert));

        // Liste, die jede Zahl von 1 bis 5 mindestens einmal enthält
        List<Integer> values = new ArrayList<>();

        // Restliche waagerechten Werte zufällig hinzufügen
        for (int i = 0; i < (ersterWert * (ersterWert - 1)); i++) {
            values.add(random.nextInt(zweiterWert) + 1); // Zufallszahl zwischen 1 und zweiterWert
        }
        // Restlichen senkrechten Werte hinzufügen
        for (int i = 0; i < ((ersterWert - 1) * ersterWert); i++) {
            values.add(random.nextInt(zweiterWert) + 1); // Zufallszahl zwischen 1 und zweiterWert
        }
        // Erzwinge Mindestvorkommen jeder Farbe
        for (int i = 1; i <= zweiterWert; i++) {
            values.add(i);
        }
        // Durch erzwungene Werte sind zweiterWert viele Farben zu viel generiert worden
        for (int i = 0; i <= zweiterWert - 1; i++) {
            values.remove(0);
        }
        // Liste zufällig mischen
        Collections.shuffle(values,random);

        // Ausgabe der gemischten Werte
        System.out.println("Zufällig generierte Farben: " + values);


//erstelle zufällige Symbolbelegung für jedes anliegende Kantenpaar

        int random_symbol = random.nextInt(2) + 1;

        List<Integer> symbs = new ArrayList<>();
        // Restliche waagerechten Werte zufällig hinzufügen
        for (int i = 0; i < (ersterWert * (ersterWert - 1)); i++) {
            symbs.add(random.nextInt(2) + 1); // Zufallszahl zwischen 1 und 2
        }
        // Restlichen senkrechten Werte hinzufügen
        for (int i = 0; i < ((ersterWert - 1) * ersterWert); i++) {
            symbs.add(random.nextInt(2) + 1); // Zufallszahl zwischen 1 und 2
        }
        // Liste zufällig mischen
        Collections.shuffle(symbs);

        // Ausgabe der gemischten Werte
        System.out.println("Zufällig generierte Symbole: " + symbs);


//erstelle die Puzzlefelder

        int fields_neu_index = 0;
        PuzzleField[] fields_neu = new PuzzleField[ersterWert * ersterWert];

        for (int x_Wert = 0; x_Wert < ersterWert; x_Wert++) {
            for (int y_Wert = 0; y_Wert < ersterWert; y_Wert++) {
                fields_neu[fields_neu_index] = new PuzzleField(x_Wert, y_Wert);
                fields_neu_index++;
            }
        }
        System.out.print("Generierte Felder: ");
        for (int i = 0; i < fields_neu.length; i++) {
            System.out.print(" " + fields_neu[i].x + fields_neu[i].y);
        }
        System.out.println("");

//erstelle IDs der Puzzleteile

        List<Integer> id_array = new ArrayList<>();
        for (int i = 1; i <= (ersterWert * ersterWert); i++) {
            id_array.add(i);
        }
        Collections.shuffle(id_array, random);
        System.out.println("Randomisierte ID Zuweisung: " + id_array);

//belege alle Puzzleteile leer
        PuzzlePiece[] pieces_random = new PuzzlePiece[(ersterWert * ersterWert)];
        //Teile mit placeholder values belegen
        for (int i = 0; i < ersterWert * ersterWert; i++) {
            pieces_random[i] = new PuzzlePiece(0,
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}
            );
        }
        for (int i = 0; i < ersterWert * ersterWert; i++) {
            pieces_random[i].piece_id = id_array.get(i);
        }


//Schreibe nun den Puzzleteilen ihre Farben und Symbole in ihren Rotationen zu
        int count_zeile = 0;
        int count_spalte = 0;
        int farbindex = 0;
        for (int i = 0; i < ersterWert * ersterWert - 1; i++) {
            if (count_spalte < ersterWert - 1 && count_zeile < ersterWert - 1) {
                pieces_random[i].edges[0][1] = values.get(farbindex);
                pieces_random[i].edges[0][2] = values.get(farbindex + 1);
                pieces_random[i + 1].edges[0][3] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[0][0] = values.get(farbindex + 1);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[0][1] = 1;
                    pieces_random[i + 1].symbols[0][3] = 2;
                } else {
                    pieces_random[i].symbols[0][1] = 2;
                    pieces_random[i + 1].symbols[0][3] = 1;
                }

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[0][2] = 1;
                    pieces_random[i + ersterWert].symbols[0][0] = 2;
                } else {
                    pieces_random[i].symbols[0][2] = 2;
                    pieces_random[i + ersterWert].symbols[0][0] = 1;
                }

                pieces_random[i].edges[1][2] = values.get(farbindex);
                pieces_random[i].edges[1][3] = values.get(farbindex + 1);
                pieces_random[i + 1].edges[1][0] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[1][1] = values.get(farbindex + 1);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[1][2] = 1;
                    pieces_random[i + 1].symbols[1][0] = 2;
                } else {
                    pieces_random[i].symbols[1][2] = 2;
                    pieces_random[i + 1].symbols[1][0] = 1;
                }

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[1][3] = 1;
                    pieces_random[i + ersterWert].symbols[1][1] = 2;
                } else {
                    pieces_random[i].symbols[1][3] = 2;
                    pieces_random[i + ersterWert].symbols[1][1] = 1;
                }

                pieces_random[i].edges[2][3] = values.get(farbindex);
                pieces_random[i].edges[2][0] = values.get(farbindex + 1);
                pieces_random[i + 1].edges[2][1] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[2][2] = values.get(farbindex + 1);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[2][3] = 1;
                    pieces_random[i + 1].symbols[2][1] = 2;
                } else {
                    pieces_random[i].symbols[2][3] = 2;
                    pieces_random[i + 1].symbols[2][1] = 1;
                }

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[2][0] = 1;
                    pieces_random[i + ersterWert].symbols[2][2] = 2;
                } else {
                    pieces_random[i].symbols[2][0] = 2;
                    pieces_random[i + ersterWert].symbols[2][2] = 1;
                }

                pieces_random[i].edges[3][0] = values.get(farbindex);
                pieces_random[i].edges[3][1] = values.get(farbindex + 1);
                pieces_random[i + 1].edges[3][2] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[3][3] = values.get(farbindex + 1);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[3][0] = 1;
                    pieces_random[i + 1].symbols[3][2] = 2;
                } else {
                    pieces_random[i].symbols[3][0] = 2;
                    pieces_random[i + 1].symbols[3][2] = 1;
                }

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[3][1] = 1;
                    pieces_random[i + ersterWert].symbols[3][3] = 2;
                } else {
                    pieces_random[i].symbols[3][1] = 2;
                    pieces_random[i + ersterWert].symbols[3][3] = 1;
                }

                count_spalte++;
                farbindex = farbindex + 2;

            } else if (count_spalte == ersterWert - 1 && count_zeile < ersterWert - 1) {
                pieces_random[i].edges[0][2] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[0][0] = values.get(farbindex);

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[0][2] = 1;
                    pieces_random[i + ersterWert].symbols[0][0] = 2;
                } else {
                    pieces_random[i].symbols[0][2] = 2;
                    pieces_random[i + ersterWert].symbols[0][0] = 1;
                }

                pieces_random[i].edges[1][3] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[1][1] = values.get(farbindex);

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[1][3] = 1;
                    pieces_random[i + ersterWert].symbols[1][1] = 2;
                } else {
                    pieces_random[i].symbols[1][3] = 2;
                    pieces_random[i + ersterWert].symbols[1][1] = 1;
                }

                pieces_random[i].edges[2][0] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[2][2] = values.get(farbindex);

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[2][0] = 1;
                    pieces_random[i + ersterWert].symbols[2][2] = 2;
                } else {
                    pieces_random[i].symbols[2][0] = 2;
                    pieces_random[i + ersterWert].symbols[2][2] = 1;
                }

                pieces_random[i].edges[3][1] = values.get(farbindex);
                pieces_random[i + ersterWert].edges[3][3] = values.get(farbindex);

                if (symbs.get(farbindex + 1) == 1) {
                    pieces_random[i].symbols[3][1] = 1;
                    pieces_random[i + ersterWert].symbols[3][3] = 2;
                } else {
                    pieces_random[i].symbols[3][1] = 2;
                    pieces_random[i + ersterWert].symbols[3][3] = 1;
                }

                count_spalte = 0;
                count_zeile++;
                farbindex++;

            } else if (count_zeile == ersterWert - 1 && count_spalte < ersterWert - 1) {

                pieces_random[i].edges[0][1] = values.get(farbindex);
                pieces_random[i + 1].edges[0][3] = values.get(farbindex);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[0][1] = 1;
                    pieces_random[i + 1].symbols[0][3] = 2;
                } else {
                    pieces_random[i].symbols[0][1] = 2;
                    pieces_random[i + 1].symbols[0][3] = 1;
                }

                pieces_random[i].edges[1][2] = values.get(farbindex);
                pieces_random[i + 1].edges[1][0] = values.get(farbindex);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[1][2] = 1;
                    pieces_random[i + 1].symbols[1][0] = 2;
                } else {
                    pieces_random[i].symbols[1][2] = 2;
                    pieces_random[i + 1].symbols[1][0] = 1;
                }

                pieces_random[i].edges[2][3] = values.get(farbindex);
                pieces_random[i + 1].edges[2][1] = values.get(farbindex);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[2][3] = 1;
                    pieces_random[i + 1].symbols[2][1] = 2;
                } else {
                    pieces_random[i].symbols[2][3] = 2;
                    pieces_random[i + 1].symbols[2][1] = 1;
                }

                pieces_random[i].edges[3][0] = values.get(farbindex);
                pieces_random[i + 1].edges[3][2] = values.get(farbindex);

                if (symbs.get(farbindex) == 1) {
                    pieces_random[i].symbols[3][0] = 1;
                    pieces_random[i + 1].symbols[3][2] = 2;
                } else {
                    pieces_random[i].symbols[3][0] = 2;
                    pieces_random[i + 1].symbols[3][2] = 1;
                }

                count_spalte++;
                farbindex++;
            }
        }

        System.out.println("Initialisierte Puzzleteile (Farben):");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[0][0] + pieces_random[i].edges[0][1] + pieces_random[i].edges[0][2] + pieces_random[i].edges[0][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[1][0] + pieces_random[i].edges[1][1] + pieces_random[i].edges[1][2] + pieces_random[i].edges[1][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[2][0] + pieces_random[i].edges[2][1] + pieces_random[i].edges[2][2] + pieces_random[i].edges[2][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[3][0] + pieces_random[i].edges[3][1] + pieces_random[i].edges[3][2] + pieces_random[i].edges[3][3] + " ");
        }
        System.out.println("");
        System.out.println("Initialisierte Puzzleteile (Symbole):");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[0][0] + pieces_random[i].symbols[0][1] + pieces_random[i].symbols[0][2] + pieces_random[i].symbols[0][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[1][0] + pieces_random[i].symbols[1][1] + pieces_random[i].symbols[1][2] + pieces_random[i].symbols[1][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[2][0] + pieces_random[i].symbols[2][1] + pieces_random[i].symbols[2][2] + pieces_random[i].symbols[2][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[3][0] + pieces_random[i].symbols[3][1] + pieces_random[i].symbols[3][2] + pieces_random[i].symbols[3][3] + " ");
        }
        System.out.println("");

//Zufälliges rotieren der Puzzleteile

        PuzzlePiece[] pieces_random_rotation = new PuzzlePiece[(ersterWert * ersterWert)];
        //Teile mit placeholder values belegen
        for (int i = 0; i < ersterWert * ersterWert; i++) {
            pieces_random_rotation[i] = new PuzzlePiece(0,
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}
            );
        }
        for (int i = 0; i < ersterWert * ersterWert; i++) {
            pieces_random_rotation[i].piece_id = id_array.get(i);
        }

        //PuzzlePiece[] pieces_random_rotation = pieces_random;
        for (int i = 0; i < pieces_random.length; i++) {
            int orderVariant = random.nextInt(4);
            //System.out.println("Random Wert: " + orderVariant);
            if (orderVariant == 0) {
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[3];
            } else if (orderVariant == 1) {
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[3];
            } else if (orderVariant == 2) {
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[3];
            } else {
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[3];
            }
        }

        System.out.println("Farben nach Randomisierung:");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[0][0] + pieces_random_rotation[i].edges[0][1] + pieces_random_rotation[i].edges[0][2] + pieces_random_rotation[i].edges[0][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[1][0] + pieces_random_rotation[i].edges[1][1] + pieces_random_rotation[i].edges[1][2] + pieces_random_rotation[i].edges[1][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[2][0] + pieces_random_rotation[i].edges[2][1] + pieces_random_rotation[i].edges[2][2] + pieces_random_rotation[i].edges[2][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[3][0] + pieces_random_rotation[i].edges[3][1] + pieces_random_rotation[i].edges[3][2] + pieces_random_rotation[i].edges[3][3] + " ");
        }
        System.out.println("");

        System.out.println("Symbole nach Randomisierung:");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[0][0] + pieces_random_rotation[i].symbols[0][1] + pieces_random_rotation[i].symbols[0][2] + pieces_random_rotation[i].symbols[0][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[1][0] + pieces_random_rotation[i].symbols[1][1] + pieces_random_rotation[i].symbols[1][2] + pieces_random_rotation[i].symbols[1][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[2][0] + pieces_random_rotation[i].symbols[2][1] + pieces_random_rotation[i].symbols[2][2] + pieces_random_rotation[i].symbols[2][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[3][0] + pieces_random_rotation[i].symbols[3][1] + pieces_random_rotation[i].symbols[3][2] + pieces_random_rotation[i].symbols[3][3] + " ");
        }
        System.out.println("");

//mische Teile
        // Array in eine Liste umwandeln
        List<PuzzlePiece> piecesList = Arrays.asList(pieces_random_rotation);

        // Liste mischen
        Collections.shuffle(piecesList, random);

        // Liste zurück in ein Array umwandeln
        pieces_random_rotation = piecesList.toArray(new PuzzlePiece[0]);

        System.out.println("Mische Puzzleteile:");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[0][0] + pieces_random_rotation[i].edges[0][1] + pieces_random_rotation[i].edges[0][2] + pieces_random_rotation[i].edges[0][3] + " ");
        }
        System.out.println("\n\n\n\n");


        PuzzleAndPieces puzzleAndPieces = new PuzzleAndPieces(fields_neu, pieces_random_rotation);

        return puzzleAndPieces;
    }

    public PuzzleAndPieces createPuzzleAndPieces_1xn(int ersterWert, int zweiterWert){

//Erstelle nun für jede Kantenkombination eine Farbe

        long seed = 64L;
        Random random = new Random(seed);
        System.out.println(random.nextInt(zweiterWert));

        // Liste, die jede Zahl von 1 bis 5 mindestens einmal enthält
        List<Integer> values = new ArrayList<>();

        // Restliche waagerechten Werte zufällig hinzufügen
        for (int i = 0; i < (ersterWert - 1); i++) {
            values.add(random.nextInt(zweiterWert) + 1); // Zufallszahl zwischen 1 und zweiterWert
        }

        // Erzwinge Mindestvorkommen jeder Farbe
        for (int i = 1; i <= zweiterWert; i++) {
            values.add(i);
        }
        // Durch erzwungene Werte sind zweiterWert viele Farben zu viel generiert worden
        for (int i = 0; i <= zweiterWert - 1; i++) {
            values.remove(0);
        }
        // Liste zufällig mischen
        Collections.shuffle(values, random);

        // Ausgabe der gemischten Werte
        System.out.println("Zufällig generierte Farben: " + values);


//erstelle zufällige Symbolbelegung für jedes anliegende Kantenpaar

        int random_symbol = random.nextInt(2) + 1;

        List<Integer> symbs = new ArrayList<>();
        // Restliche waagerechten Werte zufällig hinzufügen
        for (int i = 0; i < (ersterWert - 1); i++) {
            symbs.add(random.nextInt(2) + 1); // Zufallszahl zwischen 1 und 2
        }

        // Liste zufällig mischen
        Collections.shuffle(symbs, random);

        // Ausgabe der gemischten Werte
        System.out.println("Zufällig generierte Symbole: " + symbs);


//erstelle die Puzzlefelder

        int fields_neu_index = 0;
        PuzzleField[] fields_neu = new PuzzleField[ersterWert];
        for (int y_Wert = 0; y_Wert < ersterWert; y_Wert++) {
            fields_neu[fields_neu_index] = new PuzzleField(0, y_Wert);
            fields_neu_index++;
        }

        System.out.print("Generierte Felder: ");
        for (int i = 0; i < fields_neu.length; i++) {
            System.out.print(" " + fields_neu[i].x + fields_neu[i].y);
        }
        System.out.println("");

//erstelle IDs der Puzzleteile

        List<Integer> id_array = new ArrayList<>();
        for (int i = 1; i <= (ersterWert); i++) {
            id_array.add(i);
        }
        Collections.shuffle(id_array, random);
        System.out.println("Randomisierte ID Zuweisung: " + id_array);

//belege alle Puzzleteile leer
        PuzzlePiece[] pieces_random = new PuzzlePiece[(ersterWert)];
        //Teile mit placeholder values belegen
        for (int i = 0; i < ersterWert; i++) {
            pieces_random[i] = new PuzzlePiece(0,
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}
            );
        }
        for (int i = 0; i < ersterWert; i++) {
            pieces_random[i].piece_id = id_array.get(i);
        }


//Schreibe nun den Puzzleteilen ihre Farben und Symbole in ihren Rotationen zu
        int farbindex = 0;
        int symbolindex = 0;
        for (int i = 0; i < ersterWert - 1; i++) {

            //rotation 0
            pieces_random[i].edges[0][1] = values.get(farbindex);
            pieces_random[i+1].edges[0][3] = values.get(farbindex);

            if (symbs.get(symbolindex) == 1){
                pieces_random[i].symbols[0][1] = 1;
                pieces_random[i+1].symbols[0][3] = 2;
            }else{
                pieces_random[i].symbols[0][1] = 2;
                pieces_random[i+1].symbols[0][3] = 1;
            }

            //rotation 1
            pieces_random[i].edges[1][2] = values.get(farbindex);
            pieces_random[i+1].edges[1][0] = values.get(farbindex);

            if (symbs.get(symbolindex) == 1){
                pieces_random[i].symbols[1][2] = 1;
                pieces_random[i+1].symbols[1][0] = 2;
            }else{
                pieces_random[i].symbols[1][2] = 2;
                pieces_random[i+1].symbols[1][0] = 1;
            }

            //rotation 2
            pieces_random[i].edges[2][3] = values.get(farbindex);
            pieces_random[i+1].edges[2][1] = values.get(farbindex);

            if (symbs.get(symbolindex) == 1){
                pieces_random[i].symbols[2][3] = 1;
                pieces_random[i+1].symbols[2][1] = 2;
            }else{
                pieces_random[i].symbols[2][3] = 2;
                pieces_random[i+1].symbols[2][1] = 1;
            }

            //rotation 3
            pieces_random[i].edges[3][0] = values.get(farbindex);
            pieces_random[i+1].edges[3][2] = values.get(farbindex);

            if (symbs.get(symbolindex) == 1){
                pieces_random[i].symbols[3][0] = 1;
                pieces_random[i+1].symbols[3][2] = 2;
            }else{
                pieces_random[i].symbols[3][0] = 2;
                pieces_random[i+1].symbols[3][2] = 1;
            }

            farbindex++;
            symbolindex++;
        }

        System.out.println("Initialisierte Puzzleteile (Farben):");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[0][0] + pieces_random[i].edges[0][1] + pieces_random[i].edges[0][2] + pieces_random[i].edges[0][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[1][0] + pieces_random[i].edges[1][1] + pieces_random[i].edges[1][2] + pieces_random[i].edges[1][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[2][0] + pieces_random[i].edges[2][1] + pieces_random[i].edges[2][2] + pieces_random[i].edges[2][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " edges:" + pieces_random[i].edges[3][0] + pieces_random[i].edges[3][1] + pieces_random[i].edges[3][2] + pieces_random[i].edges[3][3] + " ");
        }
        System.out.println("");
        System.out.println("Initialisierte Puzzleteile (Symbole):");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[0][0] + pieces_random[i].symbols[0][1] + pieces_random[i].symbols[0][2] + pieces_random[i].symbols[0][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[1][0] + pieces_random[i].symbols[1][1] + pieces_random[i].symbols[1][2] + pieces_random[i].symbols[1][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[2][0] + pieces_random[i].symbols[2][1] + pieces_random[i].symbols[2][2] + pieces_random[i].symbols[2][3] + " ");
            System.out.print(" id:" + pieces_random[i].piece_id + " symbols:" + pieces_random[i].symbols[3][0] + pieces_random[i].symbols[3][1] + pieces_random[i].symbols[3][2] + pieces_random[i].symbols[3][3] + " ");
        }
        System.out.println("");

//Zufälliges rotieren der Puzzleteile

        PuzzlePiece[] pieces_random_rotation = new PuzzlePiece[(ersterWert)];
        //Teile mit placeholder values belegen
        for (int i = 0; i < ersterWert; i++) {
            pieces_random_rotation[i] = new PuzzlePiece(0,
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                    new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}
            );
        }
        for (int i = 0; i < ersterWert ; i++) {
            pieces_random_rotation[i].piece_id = id_array.get(i);
        }

        //PuzzlePiece[] pieces_random_rotation = pieces_random;
        for (int i = 0; i < pieces_random.length; i++) {
            int orderVariant = random.nextInt(4);
            //System.out.println("Random Wert: " + orderVariant);
            if (orderVariant == 0) {
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[3];
            } else if (orderVariant == 1) {
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[3];
            } else if (orderVariant == 2) {
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[3];
            } else {
                pieces_random_rotation[i].edges[3] = pieces_random[i].edges[0];
                pieces_random_rotation[i].edges[0] = pieces_random[i].edges[1];
                pieces_random_rotation[i].edges[1] = pieces_random[i].edges[2];
                pieces_random_rotation[i].edges[2] = pieces_random[i].edges[3];
                pieces_random_rotation[i].symbols[3] = pieces_random[i].symbols[0];
                pieces_random_rotation[i].symbols[0] = pieces_random[i].symbols[1];
                pieces_random_rotation[i].symbols[1] = pieces_random[i].symbols[2];
                pieces_random_rotation[i].symbols[2] = pieces_random[i].symbols[3];
            }
        }

        System.out.println("Farben nach Randomisierung:");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[0][0] + pieces_random_rotation[i].edges[0][1] + pieces_random_rotation[i].edges[0][2] + pieces_random_rotation[i].edges[0][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[1][0] + pieces_random_rotation[i].edges[1][1] + pieces_random_rotation[i].edges[1][2] + pieces_random_rotation[i].edges[1][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[2][0] + pieces_random_rotation[i].edges[2][1] + pieces_random_rotation[i].edges[2][2] + pieces_random_rotation[i].edges[2][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[3][0] + pieces_random_rotation[i].edges[3][1] + pieces_random_rotation[i].edges[3][2] + pieces_random_rotation[i].edges[3][3] + " ");
        }
        System.out.println("");

        System.out.println("Symbole nach Randomisierung:");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[0][0] + pieces_random_rotation[i].symbols[0][1] + pieces_random_rotation[i].symbols[0][2] + pieces_random_rotation[i].symbols[0][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[1][0] + pieces_random_rotation[i].symbols[1][1] + pieces_random_rotation[i].symbols[1][2] + pieces_random_rotation[i].symbols[1][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[2][0] + pieces_random_rotation[i].symbols[2][1] + pieces_random_rotation[i].symbols[2][2] + pieces_random_rotation[i].symbols[2][3] + " ");
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " symbols:" + pieces_random_rotation[i].symbols[3][0] + pieces_random_rotation[i].symbols[3][1] + pieces_random_rotation[i].symbols[3][2] + pieces_random_rotation[i].symbols[3][3] + " ");
        }
        System.out.println("");

//mische Teile
        // Array in eine Liste umwandeln
        List<PuzzlePiece> piecesList = Arrays.asList(pieces_random_rotation);

        // Liste mischen
        Collections.shuffle(piecesList, random);

        // Liste zurück in ein Array umwandeln
        pieces_random_rotation = piecesList.toArray(new PuzzlePiece[0]);

        System.out.println("Mische Puzzleteile:");
        for (int i = 0; i < pieces_random.length; i++) {
            System.out.print(" id:" + pieces_random_rotation[i].piece_id + " edges:" + pieces_random_rotation[i].edges[0][0] + pieces_random_rotation[i].edges[0][1] + pieces_random_rotation[i].edges[0][2] + pieces_random_rotation[i].edges[0][3] + " ");
        }
        System.out.println("\n\n\n\n");


        PuzzleAndPieces puzzleAndPieces = new PuzzleAndPieces(fields_neu, pieces_random_rotation);

        return puzzleAndPieces;
    }
}



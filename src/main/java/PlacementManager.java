// PlacementManager dient zum auswerten der Belegung

import java.util.*;

public class PlacementManager {

    public int countViolations( PuzzleField[] fields, PuzzlePiece[] pieces, int dimension, String[] placement){

        // Liste zum Speichern der Ergebnisse
        List<Belegung> belegungen = new ArrayList<>();
        List<Integer> id_list = new ArrayList<Integer>();

        // Teile Belegungsstrings an den Unterstrichen "_" auf
        for (String platzierung : placement) {
            String[] parts = platzierung.split("_"); // Teile den String an den Unterstrichen
            if (parts.length == 6) { // Sicherstellen, dass das Format korrekt ist
                try {
                    int field = Integer.parseInt(parts[1]); // "1" von "field_1"
                    int piece = Integer.parseInt(parts[3]); // "2" von "piece_2"
                    int rotation = Integer.parseInt(parts[5]); // "3" von "rotation_3"

                    // Belegung erstellen und hinzufügen
                    belegungen.add(new Belegung(field, piece, rotation));
                    id_list.add(piece);
                } catch (NumberFormatException e) {
                    System.out.println("Fehler beim Splitten: " + platzierung);
                }
            } else {
                System.out.println("Format ungültig. Formattierung wie folgt: field_1_piece_1_rotation_2, gegeben jedoch: " + platzierung);
            }
        }

        // Belegungen nach field in aufsteigender Reihenfolge sortieren
        belegungen.sort(Comparator.comparingInt(Belegung::getField));

        //Erstelle Set mit allen IDs, da Set keine Duplikate erlaubt
        Set<Integer> uniqueSet = new HashSet<>(id_list);
        int dupicateId = id_list.size() - uniqueSet.size();
        System.out.println("Anzahl Duplikat-IDs: " + dupicateId);

  /*
        // Ergebnisse ausgeben
        for (Belegung b : belegungen) {
            System.out.println("Belegung: " + b);
        }

   */

        /*
        for (int currentField = 0; currentField < belegungen.size(); currentField++) {
            //System.out.println("Belegung " + belegungen.get(currentField));
            for (PuzzlePiece piece : pieces) {
                if (piece.piece_id == belegungen.get(currentField).piece) {
                    if (belegungen.get(currentField).field < dimension) {
                        if (piece.edges[belegungen.get(currentField).rotation][0] != 0) {
                            greviolationupperbody++;
                        }
                    }
                }
            }
        }

         */

        int howManyMisMatches = 0;

        int greyviolation = 0;
        for (Belegung belegung : belegungen) {

            for (PuzzlePiece piece : pieces){
                if (piece.piece_id == belegung.piece){
                    //prüfe ob obere Kante grau ist
                    if ( belegung.field < dimension ) {
                        if (piece.edges[belegung.rotation][0] != 0) {
                            greyviolation++;
                            howManyMisMatches++;
                        }
                    }
                    //prüfe ob linke Kante grau ist
                    if ( belegung.field % dimension == 0){
                        if (piece.edges[belegung.rotation][3] != 0){
                            greyviolation++;
                            howManyMisMatches++;
                        }
                    }
                    // prüfe ob rechte Kante grau ist
                    if ( belegung.field % dimension == dimension -1){
                        if (piece.edges[belegung.rotation][1] != 0){
                            greyviolation++;
                            howManyMisMatches++;
                        }
                    }
                    // prüfe ob untere Kante grau ist
                    if ( belegung.field >= ((dimension*dimension)-dimension)){
                        if (piece.edges[belegung.rotation][2] != 0){
                            greyviolation++;
                            howManyMisMatches++;
                        }
                    }
                }

            }
        }
        System.out.println("Anzahl äußerer Kanten ungleich grau: " + greyviolation);

        int colorviolation = 0;
        int colorviolationrow =  0;
        int colorviolationcolumn = 0;
        int symbolviolation = 0;
        int symbolviolationrow = 0;
        int symbolviolationcolumn = 0;

        //Überprüfe rechte Nachbarn
        for (int i = 0; i < belegungen.size()-2; i++) {

            if ( i % dimension < dimension-1) {
                Belegung current = belegungen.get(i);
                Belegung neighbour = belegungen.get(i + 1);

                for (PuzzlePiece piece1 : pieces) {
                    for (PuzzlePiece piece2 : pieces) {
                        if (piece1.piece_id == current.piece && piece2.piece_id == neighbour.piece) {
                            if (piece1.edges[current.rotation][1] != piece2.edges[neighbour.rotation][3]) {
                                colorviolationrow++;
                            }
                            if (piece1.symbols[current.rotation][1] == piece2.symbols[neighbour.rotation][3]) {
                                symbolviolationrow++;
                            }
                            if (piece1.edges[current.rotation][1] != piece2.edges[neighbour.rotation][3]) {
                                howManyMisMatches++;
                            }
                            else if (piece1.symbols[current.rotation][1] == piece2.symbols[neighbour.rotation][3]) {
                                howManyMisMatches++;
                            }
                        }
                    }
                }
            }
        }
        //Überprüfe untere Nachbarn
        for (int i = 0; i < belegungen.size()-2; i++) {

            if ( i + dimension < (dimension*dimension)) {
                Belegung current = belegungen.get(i);
                Belegung neighbour = belegungen.get(i + dimension);

                for (PuzzlePiece piece1 : pieces) {
                    for (PuzzlePiece piece2 : pieces) {
                        if (piece1.piece_id == current.piece && piece2.piece_id == neighbour.piece) {
                            if (piece1.edges[current.rotation][2] != piece2.edges[neighbour.rotation][0]) {
                                colorviolationcolumn++;
                            }
                            if (piece1.symbols[current.rotation][2] == piece2.symbols[neighbour.rotation][0]) {
                                symbolviolationcolumn++;
                            }
                            if (piece1.edges[current.rotation][2] != piece2.edges[neighbour.rotation][0]) {
                                howManyMisMatches++;
                            }
                            else if (piece1.symbols[current.rotation][2] == piece2.symbols[neighbour.rotation][0]) {
                                howManyMisMatches++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Anzahl ungleicher Nachbarfarben waagerecht: " + colorviolationrow);
        System.out.println("Anzahl ungleicher Nachbarfarben senkrecht: " + colorviolationcolumn);
        System.out.println("Anzahl gleicher benachbarter Symbole waagerecht: " + symbolviolationrow);
        System.out.println("Anzahl gleicher benachbarter Symbole senkrecht: " + symbolviolationcolumn);




        return howManyMisMatches;
    }

    public int countViolations1xnBorder( PuzzleField[] fields, PuzzlePiece[] pieces, int dimension, String[] placement){

        // Liste zum Speichern der Ergebnisse
        List<Belegung> belegungen = new ArrayList<>();
        List<Integer> id_list = new ArrayList<Integer>();

        // Teile Belegungsstrings an den Unterstrichen "_" auf
        for (String platzierung : placement) {
            String[] parts = platzierung.split("_"); // Teile den String an den Unterstrichen
            if (parts.length == 6) { // Sicherstellen, dass das Format korrekt ist
                try {
                    int field = Integer.parseInt(parts[1]); // "1" von "field_1"
                    int piece = Integer.parseInt(parts[3]); // "2" von "piece_2"
                    int rotation = Integer.parseInt(parts[5]); // "3" von "rotation_3"

                    // Belegung erstellen und hinzufügen
                    belegungen.add(new Belegung(field, piece, rotation));
                    id_list.add(piece);
                } catch (NumberFormatException e) {
                    System.out.println("Fehler beim Splitten: " + platzierung);
                }
            } else {
                System.out.println("Format ungültig. Formattierung wie folgt: field_1_piece_1_rotation_2, gegeben jedoch: " + platzierung);
            }
        }

        // Belegungen nach field in aufsteigender Reihenfolge sortieren
        belegungen.sort(Comparator.comparingInt(Belegung::getField));

        //Erstelle Set mit allen IDs, da Set keine Duplikate erlaubt
        Set<Integer> uniqueSet = new HashSet<>(id_list);
        int dupicateId = id_list.size() - uniqueSet.size();
        System.out.println("Anzahl Duplikat-IDs: " + dupicateId);

  /*
        // Ergebnisse ausgeben
        for (Belegung b : belegungen) {
            System.out.println("Belegung: " + b);
        }

   */

        /*
        for (int currentField = 0; currentField < belegungen.size(); currentField++) {
            //System.out.println("Belegung " + belegungen.get(currentField));
            for (PuzzlePiece piece : pieces) {
                if (piece.piece_id == belegungen.get(currentField).piece) {
                    if (belegungen.get(currentField).field < dimension) {
                        if (piece.edges[belegungen.get(currentField).rotation][0] != 0) {
                            greviolationupperbody++;
                        }
                    }
                }
            }
        }

         */

        int howManyMisMatches = 0;

        int greyviolation = 0;
        for (Belegung belegung : belegungen) {

            for (PuzzlePiece piece : pieces){
                if (piece.piece_id == belegung.piece){
                    //prüfe ob obere Kante grau ist

                        if (piece.edges[belegung.rotation][0] != 0) {
                            greyviolation++;
                            howManyMisMatches++;
                        }

                    //prüfe ob linke Kante grau ist
                    if ( belegung.field == 0){
                        if (piece.edges[belegung.rotation][3] != 0){
                            greyviolation++;
                            howManyMisMatches++;
                        }
                    }
                    // prüfe ob rechte Kante grau ist
                    if ( belegung.field == dimension-1){
                        if (piece.edges[belegung.rotation][1] != 0){
                            greyviolation++;
                            howManyMisMatches++;
                        }
                    }
                    // prüfe ob untere Kante grau ist

                        if (piece.edges[belegung.rotation][2] != 0){
                            greyviolation++;
                            howManyMisMatches++;
                        }

                }

            }
        }
        System.out.println("Anzahl äußerer Kanten ungleich grau: " + greyviolation);


        int colorviolationrow =  0;
        int symbolviolationrow = 0;

        //Überprüfe rechte Nachbarn
        for (int i = 0; i < belegungen.size()-2; i++) {


                Belegung current = belegungen.get(i);
                Belegung neighbour = belegungen.get(i + 1);

                for (PuzzlePiece piece1 : pieces) {
                    for (PuzzlePiece piece2 : pieces) {
                        if (piece1.piece_id == current.piece && piece2.piece_id == neighbour.piece) {
                            if (piece1.edges[current.rotation][1] != piece2.edges[neighbour.rotation][3]) {
                                colorviolationrow++;
                            }
                            if (piece1.symbols[current.rotation][1] == piece2.symbols[neighbour.rotation][3]) {
                                symbolviolationrow++;
                            }
                            if (piece1.edges[current.rotation][1] != piece2.edges[neighbour.rotation][3]) {
                                howManyMisMatches++;
                            }
                            else if (piece1.symbols[current.rotation][1] == piece2.symbols[neighbour.rotation][3]) {
                                howManyMisMatches++;
                            }
                        }
                    }
                }

        }

        System.out.println("Anzahl ungleicher Nachbarfarben waagerecht: " + colorviolationrow);
        System.out.println("Anzahl gleicher benachbarter Symbole waagerecht: " + symbolviolationrow);




        return howManyMisMatches;
    }
}

// Hilfsklasse zum speichern der Ergebnisse
class Belegung {
    int field;
    int piece;
    int rotation;

    public Belegung(int field, int piece, int rotation) {
        this.field = field;
        this.piece = piece;
        this.rotation = rotation;
    }

    public int getField() {
        return field;
    }

    @Override
    public String toString() {
        return "Belegung{" +
                "field=" + field +
                ", piece=" + piece +
                ", rotation=" + rotation +
                '}';
    }
}

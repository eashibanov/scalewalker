package GUI;

import javafx.util.Pair;

public class MusicalNote {
    // Base frequencies of each and every note
    static final Pair<String,Double>[] notes = new Pair[]{
            new Pair<>("C", 16.35),
            new Pair<>("C#", 17.32),
            new Pair<>("D", 18.35),
            new Pair<>("Eb", 19.45),
            new Pair<>("E", 20.6),
            new Pair<>("F", 21.83),
            new Pair<>("F#", 23.12),
            new Pair<>("G", 24.5),
            new Pair<>("G#", 25.96),
            new Pair<>("A", 27.5),
            new Pair<>("Bb", 29.14),
            new Pair<>("B", 30.87)
    };

    final static double INTERVAL_BETWEEN_NOTES = 1.05946;   // This constant is a power 12 root of 2

    static public String findANote(double val) {
        while (val > 31)
            val /= 2;

        // Divide a note until it is comparable with base frequencies
        for (int i = 0; i < 12; ++i) {
            if (notes[i].getValue() - notes[i].getValue() * (INTERVAL_BETWEEN_NOTES - 1) / 2 < val
                    && notes[i].getValue() + notes[i].getValue() * (INTERVAL_BETWEEN_NOTES - 1) / 2 > val)
                return notes[i].getKey();
        }

        return "no";
    }
}

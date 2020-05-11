package GUI;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.util.Pair;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;

public class Piano {

    int[] offsets = new int[] {0, 1, 3, 4, 5};  // Helper for piano keys positioning
    String prevKey = "";
    int prevIndex = -1;
    ArrayList<Integer> currentScale;
    public static final double windowSizeWidth  = (Math.min(Screen.getPrimary().getBounds().getWidth(),
            Screen.getPrimary().getBounds().getHeight()) - 50);
    public static final double windowSizeHeight = windowSizeWidth * 0.6;

    Rectangle prevNote = null;
    Pair<String, Rectangle>[] keys = new Pair[]{
            new Pair<>("C", new Rectangle()),
            new Pair<>("D", new Rectangle()),
            new Pair<>("E", new Rectangle()),
            new Pair<>("F", new Rectangle()),
            new Pair<>("G", new Rectangle()),
            new Pair<>("A", new Rectangle()),
            new Pair<>("B", new Rectangle()),
            new Pair<>("C#", new Rectangle()),
            new Pair<>("Eb", new Rectangle()),
            new Pair<>("F#", new Rectangle()),
            new Pair<>("G#", new Rectangle()),
            new Pair<>("Bb", new Rectangle())
    };

    public Piano(AnchorPane ap) {

        Rectangle background = new Rectangle(windowSizeWidth * 0.05, windowSizeHeight / 7.3, windowSizeWidth * 0.9, windowSizeHeight * 0.8);
        background.setFill(Color.GRAY);
        ap.getChildren().add(background);

        for (int i = 0; i < 7; ++i) {
            keys[i].getValue().setStroke(Color.BLACK);
            keys[i].getValue().setFill(Color.WHITE);
            keys[i].getValue().setX(windowSizeWidth / 16 + i * windowSizeWidth / 8);
            keys[i].getValue().setY(windowSizeHeight / 6.6);
            keys[i].getValue().setWidth(windowSizeWidth / 8);
            keys[i].getValue().setHeight(windowSizeHeight / 1.3);
            ap.getChildren().add(keys[i].getValue());
        }

        for (int i = 7; i < 12; ++i) {
            keys[i].getValue().setWidth(windowSizeWidth * 0.075);
            keys[i].getValue().setStroke(Color.BLACK);
            keys[i].getValue().setFill(Color.BLACK);
            keys[i].getValue().setX(windowSizeWidth * 0.15 + offsets[i - 7] * windowSizeWidth / 8);
            keys[i].getValue().setY(windowSizeHeight / 6.6);
            keys[i].getValue().setHeight(windowSizeHeight * 0.55);
            ap.getChildren().add(keys[i].getValue());
        }

    }

    public synchronized void paintKey(String key) {

        Rectangle note = null;

        int i;

        for (i = 0; i < 12; ++i)
            if (keys[i].getKey().equals(key)) {
                note = keys[i].getValue();
                break;
            }

        if (note == null)
            return;

        if (currentScale != null && currentScale.size() != 0 && !currentScale.contains(i))
            note.setFill(Color.TOMATO);
        else
            note.setFill(Color.CYAN);

        if (prevIndex == i)
            return;

        if (prevNote != null && prevIndex >= 0) {
            if (currentScale != null && currentScale.contains(prevIndex)) {
                if (prevIndex < 7)
                    keys[prevIndex].getValue().setFill(Color.LIGHTGOLDENRODYELLOW);
                else
                    keys[prevIndex].getValue().setFill(Color.SANDYBROWN);
            }
            else if (prevIndex < 7)
                keys[prevIndex].getValue().setFill(Color.WHITE);
            else
                keys[prevIndex].getValue().setFill(Color.BLACK);
        }

        prevNote = note;
        prevKey = key;
        prevIndex = i;
    }

    public void setCurrentScale(Integer[] scale) {
        currentScale = new ArrayList<>();
        Collections.addAll(currentScale, scale);
        for (int i = 0; i < 12; ++i)
            if (currentScale.contains(i)) {
                if (i < 7)
                    keys[i].getValue().setFill(Color.LIGHTGOLDENRODYELLOW);
                else
                    keys[i].getValue().setFill(Color.SANDYBROWN);
            }
            else if (i < 7)
                keys[i].getValue().setFill(Color.WHITE);
            else
                keys[i].getValue().setFill(Color.BLACK);
    }
}

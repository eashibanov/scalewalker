package GUI;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        piano = new Piano(mainPane);
        var sizeW = Piano.windowSizeWidth;
        var sizeH = Piano.windowSizeHeight;
        noiseGateButton.setMinWidth(sizeW * 0.14375);
        noiseGateButton.setMaxWidth(sizeW * 0.14375);
        noiseGateButton.setLayoutX(sizeW / 16);
        noiseGateButton.setLayoutY(sizeH / 30);

        startButton.setMinWidth(sizeW * 0.14375);
        startButton.setMaxWidth(sizeW * 0.14375);
        startButton.setLayoutX(sizeW / 16 + sizeW * 0.175);
        startButton.setLayoutY(sizeH / 30);

        fileButton.setMinWidth(sizeW * 0.14375);
        fileButton.setMaxWidth(sizeW * 0.14375);
        fileButton.setLayoutX(sizeW / 16 + sizeW * 0.175 * 2);
        fileButton.setLayoutY(sizeH / 30);

        scaleButton.setMinWidth(sizeW * 0.14375);
        scaleButton.setMaxWidth(sizeW * 0.14375);
        scaleButton.setLayoutX(sizeW / 16 + sizeW * 0.175 * 3);
        scaleButton.setLayoutY(sizeH / 30);

        text.setMinWidth(sizeW / 5.5);
        text.setMaxWidth(Piano.windowSizeWidth / 5.5);
        text.setLayoutX(sizeW / 16 + sizeW * 0.175 * 4);
        text.setLayoutY(sizeH / 30);
    }

    final FileChooser fileChooser = new FileChooser();
    Sequencer sequencer;
    public Button noiseGateButton;
    public Button startButton;
    public AnchorPane mainPane;
    public Button scaleButton;
    public TextField text;
    public Button fileButton;
    FrequencyDetector detector;
    Piano piano;
    boolean recording = false;
    boolean isPlayingMidi = false;

    /**
     * A method that starts the FrequencyDetector thread which loops continuously
     */
    public void detect() {

        if (detector == null) {
            detector = new FrequencyDetector(piano);
            detector.setDaemon(true);
        }

        if (!detector.isSet)
            return;

        if (!detector.rec) {
            startButton.setStyle("-fx-background-color: #ffaaaa;");
            startButton.setText("Pause recording");
            detector.rec = true;
            try {
                detector.start();
            }
            catch (IllegalThreadStateException ex) {} // basically the thread is running already
            recording = true;
        }

        else {
            detector.rec = false;
            startButton.setStyle("-fx-background-color: #ffffff;");
            startButton.setText("Resume recording");
        }

    }

    /**
     * Initializes the gate of FrequencyDetector class. Does not start the thread
     */
    public void setGate() {

        if (detector == null) {
            detector = new FrequencyDetector(piano);
            detector.setDaemon(true);
        }

        if (detector.isSet)
            detector.setGate(noiseGateButton);
    }

    /**
     * Initializes currentScale field in the piano class
     */
    public void setScale() {

        try {
            String[] notesArray;
            String notes = text.getText();
            notesArray = notes.split(" ");
            Integer[] nums = new Integer[notesArray.length];

            for (int i = 0; i < nums.length; ++i)
                nums[i] = Integer.parseInt(notesArray[i]);

            piano.setCurrentScale(nums);
        }
        catch (NumberFormatException ex) {
            text.setText("Bad input data!");
        }

    }

    /**
     * Method that opens a MIDI file
     */
    public void openFile() {

        if (isPlayingMidi) {
            sequencer.stop();
            isPlayingMidi = false;
            fileButton.setText("Play a MIDI");
            return;
        }


        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI Files", "*mid"));
        File file = fileChooser.showOpenDialog(fileButton.getScene().getWindow());
        if (file != null) {

            try {

                sequencer = MidiSystem.getSequencer();
                sequencer.open();

                InputStream is = new BufferedInputStream(new FileInputStream(file));
                sequencer.setSequence(is);
                sequencer.start();
                isPlayingMidi = true;
                fileButton.setText("Stop MIDI");

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                if (ex instanceof IOException)
                    alert.setHeaderText("Could not retrieve the file");
                else if (ex instanceof MidiUnavailableException)
                    alert.setHeaderText("MIDI sequencer is unavailable");
                else if (ex instanceof InvalidMidiDataException)
                    alert.setHeaderText("MIDI file is corrupted");
                else
                    alert.setHeaderText("Something went wrong...\n" + ex.getMessage());
                alert.showAndWait();
            }
        }
    }
}

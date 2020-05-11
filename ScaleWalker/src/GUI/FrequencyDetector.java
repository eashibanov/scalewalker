package GUI;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javax.sound.sampled.*;
import java.io.IOException;

public class FrequencyDetector extends Thread {
    boolean isRecording = false;
    AudioFormat format;
    DataLine.Info info;
    TargetDataLine targetLine;
    byte[] buf;
    int numberOfSamples;
    FFTFactory.JavaFFT fft;
    AudioInputStream audioStream;
    double toFrequency;
    double noiseGate = 0;
    double freqOffset;
    final Piano piano;
    public volatile boolean isSet = false;
    public volatile boolean rec = false;    // Dedicated for threads communication

    @Override
    public void run() {
        try {
            detect();
        } catch (IOException e) {
            System.out.println("Bad mic!");
        }
    }

    public FrequencyDetector(Piano piano) {

        this.piano = piano;
        try {
            SetDevice();
            isSet = true;
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("The input device is incompatible with this application");
            alert.showAndWait();
        }
    }

    void SetDevice() throws LineUnavailableException {
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
        info = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open();
        targetLine.start();
        audioStream = new AudioInputStream(targetLine);
        buf = new byte[1024 * 8];
        numberOfSamples = buf.length / format.getFrameSize();
        fft = new FFTFactory.JavaFFT(numberOfSamples);
        freqOffset = format.getSampleRate() / buf.length;
        toFrequency = format.getSampleRate() / (buf.length / 2) ;
        isRecording = true;
    }

    double record(boolean isForRecording) throws IOException {
        int bytesRead = 0;
        while (bytesRead != buf.length)                 // Reading until the buffer fills with data fully
            bytesRead = audioStream.read(buf);
        final float[] samples = FFT_Transformer.decode(buf, format);
        final float[][] transformed = fft.transform(samples);
        final double[] magnitudes = FFT_Transformer.toMagnitudes(transformed[0], transformed[1]);
        int max = 3;

        // Looking for a magnitude spike
        for (int i = 5; i < 1024; ++i)
            if (magnitudes[i] > magnitudes[max])
                max = i;

        return isForRecording ? (magnitudes[max] > noiseGate * 1.3 ? (max * toFrequency) : 0) : magnitudes[max];
    }

    public void detect() throws IOException {

        while (true) {
            if (rec) {
                double freq = record(true);
                if (freq != 0) {
                    piano.paintKey(MusicalNote.findANote(freq));
                    System.out.println(MusicalNote.findANote(freq) + " = " + freq);
                }
            }
        }
    }

    public void setGate(Button button) {
        try {
            noiseGate = record(false);
            button.setText("Gate set!");
        }
        catch (IOException ex) {
            button.setText("Error gate");
        }
    }

}

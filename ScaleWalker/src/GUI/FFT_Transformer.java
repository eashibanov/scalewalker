package GUI;

import javax.sound.sampled.AudioFormat;

public class FFT_Transformer {

    static final float NORMALIZATION_FACTOR_2_BYTES = Short.MAX_VALUE + 1.0f;

    /**
     * Converts raw byte data to floats
     * @param buf data buffer
     * @param format
     * @return decoded data in floats
     */
    public static float[] decode(final byte[] buf, final AudioFormat format) {
        final float[] fbuf = new float[buf.length / format.getFrameSize()];
        for (int pos = 0; pos < buf.length; pos += format.getFrameSize()) {
            final int sample = format.isBigEndian()
                    ? byteToIntBigEndian(buf, pos, format.getFrameSize())
                    : byteToIntLittleEndian(buf, pos, format.getFrameSize());
            fbuf[pos / format.getFrameSize()] = sample / NORMALIZATION_FACTOR_2_BYTES;
        }
        return fbuf;
    }

    /**
     * Converts the FFT output data (real + imaginary) to a single array of magnitudes
     * @param realPart
     * @param imaginaryPart
     * @return double array of magnitudes
     */
    public static double[] toMagnitudes(final float[] realPart, final float[] imaginaryPart) {
        final double[] powers = new double[realPart.length / 2];
        for (int i = 0; i < powers.length; i++) {
            powers[i] = Math.sqrt(realPart[i] * realPart[i] + imaginaryPart[i] * imaginaryPart[i]);
        }
        return powers;
    }

    /**
     * Converts an array of bytes to little endian
     * @param buf data array
     * @param offset offset from data
     * @param bytesPerSample number of bytes per float
     * @return batch of bytes converted to small endian
     */
    public static int byteToIntLittleEndian(final byte[] buf, final int offset, final int bytesPerSample) {
        int sample = 0;
        for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
            final int aByte = buf[offset + byteIndex] & 0xff;
            sample += aByte << 8 * (byteIndex);
        }
        return sample;
    }

    /**
     * Converts an array of bytes to big endian
     * @param buf data array
     * @param offset offset from data
     * @param bytesPerSample number of bytes per float
     * @return batch of bytes converted to big endian
     */
    public static int byteToIntBigEndian(final byte[] buf, final int offset, final int bytesPerSample) {
        int sample = 0;
        for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
            final int aByte = buf[offset + byteIndex] & 0xff;
            sample += aByte << (8 * (bytesPerSample - byteIndex - 1));
        }
        return sample;
    }
}

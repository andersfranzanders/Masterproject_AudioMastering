package audioHelpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import other.AudioFormatNotSupportedException;

public class AudioStreamConverter {

	public int[][] convertAIStoIntArray(AudioInputStream audioInputStream) throws IOException,
			AudioFormatNotSupportedException {

		checkIfAudioFormatIsSupported(audioInputStream);

		// get Frame Length
		long frameLength = audioInputStream.getFrameLength();

		// getBytesPerFrame
		int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
		if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
			bytesPerFrame = 1;
		}

		byte frame[] = new byte[bytesPerFrame];
		int audioAsIntArray[][] = new int[2][(int) frameLength];

		int numBytesRead = 0;
		byte[] leftBytes = new byte[2];
		byte[] rightBytes = new byte[2];

		int samplePosition = 0;

		while ((numBytesRead = audioInputStream.read(frame)) != -1) {

			if (samplePosition == Integer.MAX_VALUE) {
				break;
			}

			leftBytes[0] = frame[0];
			leftBytes[1] = frame[1];
			rightBytes[0] = frame[2];
			rightBytes[1] = frame[3];

			int leftSample = (leftBytes[1] << 8) + (leftBytes[0] & 0x00ff);
			int rightSample = (rightBytes[1] << 8) + (rightBytes[0] & 0x00ff);

			audioAsIntArray[0][samplePosition] = leftSample;
			audioAsIntArray[1][samplePosition] = rightSample;
			samplePosition++;
		}
		return audioAsIntArray;
	}

	public int[][] cloneAudio(int[][] audio) {

		int numSamples = audio[0].length;
		int[][] clone = new int[2][numSamples];

		for (int sample = 0; sample < numSamples; sample++) {
			for (int channel = 0; channel < 2; channel++) {

				int value = audio[channel][sample];
				clone[channel][sample] = value;

			}
		}

		return clone;
	}

	public AudioInputStream convertIntArrayToAIS(int[][] audio) {

		int numSamples = audio[0].length;

		byte[] byteArray = new byte[numSamples * 4];

		int counter = 0;

		for (int sample = 0; sample < numSamples; sample++) {
			for (int channel = 0; channel < 2; channel++) {

				short x = (short) audio[channel][sample];
				byte b1 = (byte) x;
				byte b2 = (byte) (x >> 8);

				byteArray[counter] = b1;
				counter++;
				byteArray[counter] = b2;
				counter++;
			}
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

		AudioInputStream outStream = new AudioInputStream(stream, new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				44100f, 16, 2, 4, 44100f, false), numSamples);

		return outStream;
	}

	private void checkIfAudioFormatIsSupported(AudioInputStream audioInputStream)
			throws AudioFormatNotSupportedException {
		AudioFormat format = audioInputStream.getFormat();
		if (format.isBigEndian() != false || format.getChannels() != 2 || format.getEncoding() != Encoding.PCM_SIGNED
				|| format.getFrameSize() != 4) {
			throw new AudioFormatNotSupportedException();

		}
	}

	public double[][] convertIntToDoubleSignal(int[][] audioIn) {
		double[][] audioOut = new double[2][audioIn[0].length];

		for (int sample = 0; sample < audioIn[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				audioOut[channel][sample] = audioIn[channel][sample];
			}
		}
		return audioOut;
	}

	public int[][] convertDoubleToIntSignal(double[][] audioIn) {
		int[][] audioOut = new int[2][audioIn[0].length];

		for (int sample = 0; sample < audioIn[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				audioOut[channel][sample] = (int) audioIn[channel][sample];
			}
		}
		return audioOut;
	}

}

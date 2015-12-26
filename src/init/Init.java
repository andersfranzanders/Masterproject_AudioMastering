package init;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import other.AudioFormatNotSupportedException;
import other.Complex;
import other.FFT;
import synthesizer.BasicWaveSynthi;
import visualisation.AudioVisualisation;
import visualisation.CumulativeHistoViz;
import visualisation.FrequencyVisualisation;
import visualisation.HistoViz;
import visualisation.NumericalVisualizer;
import audioFX.Compressor;
import audioFX.Convoluter;
import audioFX.Filter;
import audioFX.Limiter;
import audioFX.Normalizer;
import audioFX.RMSCalculator;
import audioFX.SpectralAnalyzer;
import audioHelpers.AudioStreamConverter;

public class Init {

	String outFileName = "resources/CLHE Test 24.wav";
	File fileOut = new File(outFileName);

	Limiter limiter = new Limiter();
	Normalizer normalizer = new Normalizer();
	AudioStreamConverter converter = new AudioStreamConverter();
	Compressor compressor = new Compressor();
	RMSCalculator rmsCal = new RMSCalculator();
	SpectralAnalyzer spectralAnalyzer = new SpectralAnalyzer();
	AudioVisualisation vis1 = new AudioVisualisation();
	AudioVisualisation vis2 = new AudioVisualisation();
	FrequencyVisualisation specVis = new FrequencyVisualisation();
	BasicWaveSynthi synthi = new BasicWaveSynthi();
	Convoluter convoluter = new Convoluter();
	NumericalVisualizer numVis = new NumericalVisualizer();
	HistoViz histoViz = new HistoViz();
	CumulativeHistoViz cumHistoViz = new CumulativeHistoViz();
	Filter filter = new Filter();

	public void start() throws UnsupportedAudioFileException, IOException, AudioFormatNotSupportedException {
		// String fileName = "resources/Keule.wav";
		// String fileName = "resources/LPP.wav";
		// String fileNameToFilter = "resources/DK2_126_WKicks22.wav";
		// String aimSpecFileName = "resources/DK2_126_001_WoKicks22.wav";
		String fileNameToFilter = "resources/jeanLuc_short 01.wav";
		String aimSpecFileName = "resources/birthday_short 01.wav";
		// String fileNameToFilter = "resources/WorkD.wav";
		// String aimSpecFileName = "resources/WorkD.wav";
		// String fileNameToFilter = "";

		int[][] audioToFilter = produceAudio(fileNameToFilter);
		int[][] aimSpecAudio = produceAudio(aimSpecFileName);
		int[][] filteredSignal = testAutomaticFiltering(audioToFilter, aimSpecAudio);

		compressor.histogramMatching(filteredSignal, aimSpecAudio);

		writeFileToDrive(filteredSignal);

	}

	private void writeFileToDrive(int[][] filteredSignal) throws IOException {
		System.out.println("writing to drive");
		AudioInputStream outStream = converter.convertIntArrayToAIS(filteredSignal);
		AudioSystem.write(outStream, AudioFileFormat.Type.WAVE, fileOut);
	}

	private int[][] produceAudio(String fileNameToFilter) throws UnsupportedAudioFileException, IOException,
			AudioFormatNotSupportedException {
		File fileIn = new File(fileNameToFilter);
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
		printAudioInfo(audioInputStream);

		int[][] audio = converter.convertAIStoIntArray(audioInputStream);
		// int[][] audio = synthi.synthesizeSquare(1500, 0.005);
		// int[][] audio = synthi.synthesizeSine(1000, 1);
		// int[][] audio = synthi.synthesizeDiracImpulseInt(Short.MAX_VALUE,
		// 0.09);

		return audio;
	}

	private int[][] testAutomaticFiltering(int[][] audio1, int[][] audio2) {

		// normalizer.normalize(audio1, Short.MAX_VALUE, 0.2);
		// normalizer.normalize(audio2, Short.MAX_VALUE, 0.2);

		return filter.automaticallyAdaptSpectrum(audio1, audio2, (int) Math.pow(2, 11));

	}

	// private int[][] testAutomaticFiltering(int[][] audio) {
	// double[] meanAvfilterKernel =
	// convoluter.produceMeanAverageFilterkernel(20, (int) Math.pow(2, 8));
	// int[][] audio2 = filter.filterSongByConvolution(audio,
	// meanAvfilterKernel);
	//
	// int[][] finalAudio = filter.automaticallyAdaptSpectrum(audio2, audio);
	//
	// return finalAudio;
	// }

	private int[][] testFFTConvolutionOfWholeSong(int[][] audio) {

		double[] filterKernel = convoluter.produceMeanAverageFilterkernel(50, (int) Math.pow(2, 10));
		int[][] filteredAudio = filter.filterSongByFFTConvolution(audio, filterKernel, (int) Math.pow(2, 9));
		return filteredAudio;
	}

	private int[][] testConvolution(int[][] audio) {
		double[] filterKernel = convoluter.produceMeanAverageFilterkernel(50, (int) Math.pow(2, 10));
		return filter.filterSongByConvolution(audio, filterKernel);

	}

	private void printAudioInfo(AudioInputStream audioInputStream) {
		System.out.println("AudioInfo: " + audioInputStream.getFormat().toString());
	}

}

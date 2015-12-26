package audioFX;

import java.util.ArrayList;
import java.util.List;

import audioHelpers.AudioStreamConverter;
import other.Complex;
import other.FFT;
import visualisation.NumericalVisualizer;

public class Filter {

	AudioStreamConverter converter = new AudioStreamConverter();
	Convoluter convoluter = new Convoluter();
	SpectralAnalyzer spectralAnalyzer = new SpectralAnalyzer();
	NumericalVisualizer numVis = new NumericalVisualizer();

	public Filter() {

	}

	private Complex[] multPolarSpectrums(Complex[] signalSpectrum, Complex[] kernelSpectrum) {

		if (signalSpectrum.length != kernelSpectrum.length) {
			System.out.println("Spectrums are not of Equal length!");
		}
		Complex[] multiplicatedSpectrum = new Complex[signalSpectrum.length];

		for (int i = 0; i < signalSpectrum.length; i++) {
			double mag = signalSpectrum[i].re() * kernelSpectrum[i].re();
			double phase = signalSpectrum[i].im() + kernelSpectrum[i].im();
			multiplicatedSpectrum[i] = new Complex(mag, phase);
		}
		

		return multiplicatedSpectrum;
	}

	private List<Complex[]> multWholeSong(List<Complex[]> wholeSongAsSpecSegments, Complex[] polarSpecOfKernel) {
		List<Complex[]> wholeMultiplicatedSignal = new ArrayList<Complex[]>();

		for (Complex[] polarSpecSegment : wholeSongAsSpecSegments) {
			Complex[] multiplicatedSegment = multPolarSpectrums(polarSpecSegment, polarSpecOfKernel);
			wholeMultiplicatedSignal.add(multiplicatedSegment);
		}

		return wholeMultiplicatedSignal;
	}

	public int[][] filterSongByFFTConvolution(int[][] audio, double[] filterKernel, int lengthOfFFT) {

		Complex[] polarSpecOfKernel = spectralAnalyzer.getPolarSpectrumOfFilterKernel(filterKernel);
		List<Complex[]> wholeLeftChannelAsSpecSegments = spectralAnalyzer.turnWholeAudioInPolarSpecSegments(audio,
				lengthOfFFT, 0);
		List<Complex[]> wholeRightChannelAsSpecSegments = spectralAnalyzer.turnWholeAudioInPolarSpecSegments(audio,
				lengthOfFFT, 1);
		List<Complex[]> wholeFilteredLeftChannel = multWholeSong(wholeLeftChannelAsSpecSegments, polarSpecOfKernel);
		List<Complex[]> wholeFilteredRightChannel = multWholeSong(wholeRightChannelAsSpecSegments, polarSpecOfKernel);
		return spectralAnalyzer.calInverseFFTofWholeSong(wholeFilteredLeftChannel, wholeFilteredRightChannel);
	}

	public int[][] filterSongByConvolution(int[][] audio, double[] filterKernel) {

		double[][] audioAsDouble = converter.convertIntToDoubleSignal(audio);
		double[][] convolutedSignal = convoluter.convolute(audioAsDouble, filterKernel);
		int[][] convolutedSignalAsInt = converter.convertDoubleToIntSignal(convolutedSignal);

		return convolutedSignalAsInt;
	}

	public Complex[] dividePolarSpectrums(Complex[] Y, Complex[] X) {

		Complex[] H = new Complex[Y.length];
		for (int i = 0; i < H.length; i++) {
			if (X[i].re() == 0) {
				X[i].setRe(1E-20);
			}
			double re = Y[i].re() / X[i].re();
			double im = Y[i].im() - X[i].im();

			H[i] = new Complex(re, im);
		}

		return H;
	}

	public int[][] automaticallyAdaptSpectrum(int[][] x, int[][] y, int lengthOfFFT) {

		// produce spectrums of audioToFilter, aimSong and the filterkernel;
		Complex[] averagedY = spectralAnalyzer.getAveragedPolarSpectrum(y, lengthOfFFT);
		System.out.println("calculated Average of song 1");
		Complex[] averagedX = spectralAnalyzer.getAveragedPolarSpectrum(x, lengthOfFFT);
		System.out.println("calculated Average of song 2");
		Complex[] tempH = dividePolarSpectrums(averagedY, averagedX);
		System.out.println("calculated Average Spectrums. begin filtering song");

		// set the phase-information of the polar Spectrum To Zero
		for (int i = 0; i < tempH.length; i++) {
			tempH[i].setIm(0);
		}

		// generate the Filterkernel
		double[] temp_h = spectralAnalyzer.calInverseFFTofPolarSpectrum(tempH);
		// double[] h = generateCustomFilterKernel(temp_h, 60);
		double[] h = generateCustomFilterKernelWithPadding(temp_h, 60, lengthOfFFT*2);

		// Filter the Signal
		//double[][] xAsDouble = converter.convertIntToDoubleSignal(x);
		//double[][] filtered_x = convoluter.convolute(xAsDouble, h);
		//int[][] finalAudio = converter.convertDoubleToIntSignal(filtered_x);
		
		int[][] finalAudio = filterSongByFFTConvolution(x, h, lengthOfFFT);
		System.out.println("filtered song");


		return finalAudio;

	}

	private double[] generateCustomFilterKernelWithPadding(double[] temp_h, int kernelLengthWithoutPadding,
			int kernelLengthWithPadding) {

		double[] hWithoutPadding = generateCustomFilterKernel(temp_h, kernelLengthWithoutPadding);

		double[] h = new double[kernelLengthWithPadding];
		for (int i = 0; i < kernelLengthWithPadding; i++) {
			if(i < kernelLengthWithoutPadding){
				h[i] = hWithoutPadding[i];
			}else{
				h[i] = 0;
			}
		}

		return h;
	}

	private double[] generateCustomFilterKernel(double[] inKernel, int kernelLength) {
		double[] outKernel = new double[kernelLength];
		double[] tempKernel = new double[inKernel.length];

		// roate Kernel to the right
		for (int i = 0; i < inKernel.length; i++) {
			int index = i + (kernelLength / 2);
			if (index > (inKernel.length - 1)) {
				index = index - inKernel.length;
			}
			tempKernel[index] = inKernel[i];
		}
		for (int i = 0; i < kernelLength; i++) {
			double value = tempKernel[i];
			value = value * (0.54 - 0.46 * Math.cos(2 * Math.PI * i / kernelLength));
			outKernel[i] = value;
		}

		// numVis.show(outKernel, "OutKernel");

		return outKernel;
	}
}

package audioFX;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

import other.Complex;
import other.FFT;

public class SpectralAnalyzer extends AudioFX {

	private Complex[] convertRectangularToPolarSpectrum(Complex[] rectangularSpectrum) {

		Complex[] polarSpectrum = new Complex[rectangularSpectrum.length];
		for (int i = 0; i < polarSpectrum.length; i++) {

			double re = rectangularSpectrum[i].re();
			double im = rectangularSpectrum[i].im();

			double mag = rectangularSpectrum[i].abs();

			if (re == 0) {
				re = 1E-20;
			}
			double phase = Math.atan(im / re);

			if (re < 0 && im < 0) {
				phase = phase - Math.PI;
			}
			if (re < 0 && im >= 0) {
				phase = phase + Math.PI;
			}

			polarSpectrum[i] = new Complex(mag, phase);

		}
		return polarSpectrum;
	}

	public Complex[] convertPolarSpecToRecSpec(Complex[] polarSpec) {
		Complex[] rectSpec = new Complex[polarSpec.length];
		for (int i = 0; i < rectSpec.length; i++) {
			double re = polarSpec[i].re() * Math.cos(polarSpec[i].im());
			double im = polarSpec[i].re() * Math.sin(polarSpec[i].im());

			rectSpec[i] = new Complex(re, im);
		}

		return rectSpec;

	}

	private Complex[] padComplexAudioWithZeroes(Complex[] audio) {

		Complex[] paddedAudio = new Complex[audio.length * 2];

		for (int i = 0; i < audio.length; i++) {
			paddedAudio[i] = audio[i];
			paddedAudio[i + audio.length] = new Complex(0, 0);
		}
		return paddedAudio;
	}

	private Complex[] calRectangularFFT(int[][] audio, int lengthOfFFT, int segmentNumber, int channel) {

		Complex[] audioAsComplex = new Complex[lengthOfFFT];
		for (int i = 0; i < lengthOfFFT; i++) {
			int value = audio[channel][lengthOfFFT * segmentNumber + i];
			audioAsComplex[i] = new Complex(value, 0);
		}

		Complex[] paddedAudio = padComplexAudioWithZeroes(audioAsComplex);

		Complex[] rectangularComplexSpectrum = FFT.fft(paddedAudio);
		Complex[] rectangularRealSpectrum = turnComplexIntoRealSpectrum(rectangularComplexSpectrum);

		return rectangularRealSpectrum;
	}

	private Complex[] turnComplexIntoRealSpectrum(Complex[] rectangularComplexSpectrum) {
		Complex[] rectangularRealSpectrum = new Complex[(rectangularComplexSpectrum.length / 2) + 1];

		for (int i = 0; i < rectangularRealSpectrum.length; i++) {
			rectangularRealSpectrum[i] = rectangularComplexSpectrum[i];
		}
		return rectangularRealSpectrum;
	}

	public Complex[] calPolarSpectrum(int[][] audio, int lengthOfFFT, int segmentNumber, int channel) {

		Complex[] rectangularSpectrum = calRectangularFFT(audio, lengthOfFFT, segmentNumber, channel);
		Complex[] polarSpectrum = convertRectangularToPolarSpectrum(rectangularSpectrum);

		return polarSpectrum;
	}

	public Complex[] getPolarSpectrumOfFilterKernel(double[] filterKernel) {

		Complex[] audioAsComplex = new Complex[filterKernel.length];
		for (int i = 0; i < filterKernel.length; i++) {
			audioAsComplex[i] = new Complex(filterKernel[i], 0);
		}

		Complex[] rectangularComplexSpectrum = FFT.fft(audioAsComplex);
		Complex[] rectangularRealSpectrum = turnComplexIntoRealSpectrum(rectangularComplexSpectrum);
		Complex[] polarSpectrum = convertRectangularToPolarSpectrum(rectangularRealSpectrum);

		return polarSpectrum;
	}

	public int[] calInverseFFTofRectangularSpectrum(Complex[] rectangularRealSpectrum) {

		Complex[] rectangularComplexSpectrum = createComplexFromRealSpec(rectangularRealSpectrum);

		Complex[] inverse = FFT.ifft(rectangularComplexSpectrum);

		int[] audio = new int[inverse.length];
		for (int i = 0; i < inverse.length; i++) {
			audio[i] = (int) inverse[i].re();
		}

		return audio;

	}
	
	public double[] calInverseFFTofRectangularSpectrumD(Complex[] rectangularRealSpectrum) {

		Complex[] rectangularComplexSpectrum = createComplexFromRealSpec(rectangularRealSpectrum);

		Complex[] inverse = FFT.ifft(rectangularComplexSpectrum);

		double[] audio = new double[inverse.length];
		for (int i = 0; i < inverse.length; i++) {
			audio[i] = inverse[i].re();
		}

		return audio;

	}
	

	private Complex[] createComplexFromRealSpec(Complex[] rectangularRealSpectrum) {
		Complex[] rectangularComplexSpectrum = new Complex[(rectangularRealSpectrum.length * 2) - 2];

		for (int i = 0; i < rectangularRealSpectrum.length; i++) {
			double real = rectangularRealSpectrum[i].re();
			double im = rectangularRealSpectrum[i].im();

			Complex positiveFrequ = new Complex(real, im);
			Complex negativeFrequ = new Complex(real, -im);

			rectangularComplexSpectrum[i] = positiveFrequ;
			if (i > 0) {
				rectangularComplexSpectrum[rectangularComplexSpectrum.length - i] = negativeFrequ;
			}

		}
		return rectangularComplexSpectrum;
	}

	public List<Complex[]> turnWholeAudioInPolarSpecSegments(int[][] audio, int lengthOfFFT, int channel) {

		int lengthOfSegments = audio[0].length / lengthOfFFT;
		List<Complex[]> wholeAudioInPolarSpecSegments = new ArrayList<Complex[]>();
		for (int segment = 0; segment < lengthOfSegments; segment++) {
			Complex[] polarSpec = calPolarSpectrum(audio, lengthOfFFT, segment, channel);

			wholeAudioInPolarSpecSegments.add(polarSpec);
		}

		return wholeAudioInPolarSpecSegments;
	}

	public int[][] calInverseFFTofWholeSong(List<Complex[]> polarSpectrumsLeft, List<Complex[]> polarSpectrumsRight) {

		List<int[]> listOfLeftSegments = getInverseFFTOfChannel(polarSpectrumsLeft);
		List<int[]> listOfRightSegments = getInverseFFTOfChannel(polarSpectrumsLeft);

		int lengthOfAudio = (polarSpectrumsLeft.size() + 1) * (polarSpectrumsLeft.get(0).length - 1);

		int[][] audio = addSegmentsToReconstructWholeSong(listOfLeftSegments, listOfRightSegments, lengthOfAudio);

		return audio;
	}

	private List<int[]> getInverseFFTOfChannel(List<Complex[]> polarSpectrumsLeft) {
		List<int[]> listOfAudioSegments = new ArrayList<int[]>();

		for (Complex[] polarSpec : polarSpectrumsLeft) {
			Complex[] rectSpec = convertPolarSpecToRecSpec(polarSpec);
			int[] audioSegment = calInverseFFTofRectangularSpectrum(rectSpec);
			listOfAudioSegments.add(audioSegment);
		}
		return listOfAudioSegments;
	}

	private int[][] addSegmentsToReconstructWholeSong(List<int[]> listOfLeftSegments, List<int[]> listOfRightSegments,
			int lengthOfAudio) {
		int[][] audio = new int[2][lengthOfAudio];

		addChannel(listOfLeftSegments, audio, 0);
		addChannel(listOfRightSegments, audio, 1);
		
		return audio;
	}

	private void addChannel(List<int[]> listOfSegments, int[][] audio, int channel) {
		int indexCount = 0;
		for (int[] audioSegment : listOfSegments) {
			for (int i = 0; i < audioSegment.length; i++) {
				int index = indexCount * (audioSegment.length / 2);
				audio[channel][index + i] += audioSegment[i];
			}
			indexCount++;
		}
	}

	public Complex[] getAveragedPolarSpectrum(int[][] audio, int lengthOfFFT) {
		Complex[] averageSpectrumLeft = getAvaeragePolarSpectrumOfChannel(audio, lengthOfFFT, 0);
		Complex[] averageSpectrumRight = getAvaeragePolarSpectrumOfChannel(audio, lengthOfFFT, 1);
		Complex[] averageSpectrum = getAverageSpectrumOfBothChannels(averageSpectrumLeft, averageSpectrumRight);
		
		return averageSpectrum;
	}

	private Complex[] getAverageSpectrumOfBothChannels(Complex[] averageSpectrumLeft, Complex[] averageSpectrumRight) {
		Complex[] averageSpectrum = new Complex[averageSpectrumLeft.length];
		
		for(int i = 0; i < averageSpectrum.length; i++){
			
			double re = (averageSpectrumLeft[i].re() + averageSpectrumRight[i].re()) / 2;
			double im = (averageSpectrumLeft[i].im() + averageSpectrumRight[i].im()) / 2;
			averageSpectrum[i] = new Complex(re, im);
		}
		
		return averageSpectrum;
	}

	private Complex[] getAvaeragePolarSpectrumOfChannel(int[][] audio, int lengthOfFFT, int channel) {
		
		List<Complex[]> polarSpecSegmentsOfChannel = turnWholeAudioInPolarSpecSegments(audio, lengthOfFFT, channel);
		
		Complex[] averageSpectrum = new Complex[polarSpecSegmentsOfChannel.get(0).length];
		for(int i = 0; i < averageSpectrum.length; i++ ){
			averageSpectrum[i] = new Complex(0, 0);
		}
		
		for(Complex[] specSegment: polarSpecSegmentsOfChannel){
			for(int i = 0; i < specSegment.length; i++){
				double re = specSegment[i].re() / polarSpecSegmentsOfChannel.size();
				double im = specSegment[i].im() / polarSpecSegmentsOfChannel.size();
				averageSpectrum[i].setRe(averageSpectrum[i].re() + re);
				averageSpectrum[i].setIm(averageSpectrum[i].im() + im);
			}
		}

		
		return averageSpectrum;
	}

	public double[] calInverseFFTofPolarSpectrum(Complex[] polarX) {
		Complex[] recX = convertPolarSpecToRecSpec(polarX);
		double[] x = calInverseFFTofRectangularSpectrumD(recX);
		return x;
	}

}

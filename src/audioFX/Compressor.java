package audioFX;

import visualisation.CumulativeHistoViz;
import visualisation.HistoViz;

public class Compressor extends AudioFX {

	public void compress(int[][] audio, float cutOff, float ratio) {

		short cutOffAsShort = (short) (Short.MAX_VALUE * cutOff);
		int numSamples = audio[0].length;

		for (int sample = 0; sample < numSamples; sample++) {
			for (int channel = 0; channel < 2; channel++) {

				short value = (short) audio[channel][sample];

				if (value > cutOffAsShort) {
					short diff = (short) (value - cutOffAsShort);
					short CompressedValue = (short) (diff * ratio);
					audio[channel][sample] = cutOffAsShort + CompressedValue;
				}
				if (value < -cutOffAsShort) {
					short diff = (short) (value + cutOffAsShort);
					short CompressedValue = (short) (diff * ratio);
					audio[channel][sample] = -cutOffAsShort + CompressedValue;
				}
			}
		}

	}

	public void automaticlyAlignDynamics(int[][] x, int[][] y, double alignment) {

		Normalizer normalizer = new Normalizer();
		RMSCalculator rmsCal = new RMSCalculator();

		normalizer.normalize(x);
		normalizer.normalize(y);
		// limitSmallPeaks(x, 0.0001);

		double rmsOfY = rmsCal.calculateRMS(y);
		int i = 0;
		while ((rmsCal.calculateRMS(x) / rmsOfY) < alignment) {

			System.out.println("---------------  Round of Compression: " + i);
			System.out.println("RMS of y: " + rmsOfY);
			System.out.println("Rms of x before compression: " + rmsCal.calculateRMS(x));
			compress(x, 0.6f, 0.5f);
			normalizer.normalize(x);
			System.out.println("Rms of x after compression: " + rmsCal.calculateRMS(x));
		}

	}

	private void limitSmallPeaks(int[][] x, double percentageToCut) {
		Limiter limiter = new Limiter();
		int[] histogram = createHistogram(x, 0);
		short limiterPosition = calculateLimiterPosition(x, percentageToCut, histogram);
		float limiterPositionAsPercentage = (float) limiterPosition / Short.MAX_VALUE;
		System.out.println("Allower maxvalue: " + Short.MAX_VALUE);
		System.out.println("Found value where to cut: " + limiterPosition);
		System.out.println("Will limit at: " + limiterPositionAsPercentage);
		limiter.limit(x, limiterPositionAsPercentage);

	}

	private short calculateLimiterPosition(int[][] x, double percentageToCut, int[] histogram) {
		double percentage = 0;
		for (int i = (histogram.length - 1); i >= 0; i--) {
			percentage += ((double) histogram[i] / x[0].length);
			if (percentage >= percentageToCut) {
				return (short) i;
			}

		}
		return Short.MAX_VALUE;
	}

	private short calculateLimiterPositionNew(int[][] x, double percentageToCut, int[] histogram) {
		int aimNumber = (int) (percentageToCut * x[0].length);
		int counter = 0;
		for (int i = (histogram.length - 1); i >= 0; i--) {
			counter += histogram[i];
			if (counter >= aimNumber) {
				return (short) i;
			}

		}
		return Short.MAX_VALUE;
	}

	public int[] createHistogram(int[][] x, int channel) {
		int[] histogram = new int[Short.MAX_VALUE + 1];

		for (int i = 0; i < x[channel].length; i++) {
			int value = x[channel][i];
			if (value < 0) {
				value = -value;
			}
			histogram[value] = (histogram[value] + 1);
		}
		return histogram;
	}

	public void histogramMatching(int[][] x, int[][] y) {
		RMSCalculator rms = new RMSCalculator();
		
		Normalizer normalizer = new Normalizer();
		normalizer.normalize(x);
		normalizer.normalize(y);
		System.out.println("RMS of X before " + rms.calculateRMS(x));
		
		matchingForChannel(x, y, 0);
		matchingForChannel(x, y, 1);

		
		System.out.println("RMS of X after" + rms.calculateRMS(x));
		System.out.println("RMS of Y " + rms.calculateRMS(y));

	}

	private void matchingForChannel(int[][] x, int[][] y, int channel) {

		HistoViz histoViz = new HistoViz();

		double[] cumulativeHistogramX = getCumulativeNormalizedHistogram(x, channel);
		double[] cumulativeHistogramY = getCumulativeNormalizedHistogram(y, channel);

		int[] matchedHistogramOfX = matchHistograms(cumulativeHistogramX, cumulativeHistogramY);
		histoViz.visualizeHistogram(matchedHistogramOfX, "Originale Transferfunction");
		int[] decreasedHisto = decreaseSlopeOfTranserFunction(matchedHistogramOfX, 2);
		int[] entNoisedTransfer = decreaseNoiseOfTranserFunction(decreasedHisto, 3);
		
		applyMatchedHistogram(x, entNoisedTransfer, channel);
	}

	private int[] decreaseNoiseOfTranserFunction(int[] transferFunc, int m) {

		int[] entNoisedTransfer = new int[transferFunc.length];

		for (int i = 0; i < transferFunc.length; i++) {
			int valueFromTransfer = transferFunc[i];
			int linearValue = i * m;
			if (linearValue < valueFromTransfer) {
				entNoisedTransfer[i] = linearValue;
			} else {
				entNoisedTransfer[i] = valueFromTransfer;
			}

		}

		HistoViz histoViz2 = new HistoViz();
		histoViz2.visualizeHistogram(entNoisedTransfer, "Transfer without Noise");
		return entNoisedTransfer;

	}

	private int[] decreaseSlopeOfTranserFunction(int[] matchedHistogram, int limit) {

		// Produce ableitung

		int[] ableitung = new int[matchedHistogram.length];
		int offset = matchedHistogram[1];

		for (int i = 0; i < (matchedHistogram.length - 1); i++) {
			int current = matchedHistogram[i];
			int prev = 0;
			if (i != 0) {
				prev = matchedHistogram[i - 1];
			}
			ableitung[i] = current - prev;
		}

		// HistoViz histoViz = new HistoViz();
		// histoViz.visualizeHistogram(ableitung, "Ableitung");

		// limit Ableitung
		int limited = 0;
		int[] limitedAbleitung = new int[ableitung.length];
		for (int i = 0; i < ableitung.length; i++) {
			int value = ableitung[i];
			// if (i < Short.MAX_VALUE * 0.75) {
			if (value > limit) {
				value = limit;
				limited++;
			}
			// }
			limitedAbleitung[i] = value;

		}
		// HistoViz histoViz3 = new HistoViz();
		// histoViz3.visualizeHistogram(ableitung, "limited Ableitung");

		// integrate albeitung;
		int[] integral = new int[matchedHistogram.length];
		int sum = offset;
		for (int i = 0; i < matchedHistogram.length; i++) {
			sum += limitedAbleitung[i];
			integral[i] = sum;
		}
		// normalizeIntegral
		double factor = (double) Short.MAX_VALUE / integral[integral.length - 1];
		System.out.println(factor);
		for (int i = 0; i < integral.length; i++) {
			integral[i] = (int) (integral[i] * factor);
		}
		// integral[0] = 0;

		// System.out.println("limited: " + (double)limited / integral.length);
		// System.out.println("original Zahl: " + matchedHistogram[(int)
		// (Short.MAX_VALUE * 0.2)]);
		// System.out.println("integrierte Zahl: " + integral[(int)
		// (Short.MAX_VALUE * 0.2)]);
		// System.out.println("original Zahl: " + matchedHistogram[(int)
		// (Short.MAX_VALUE * 0.5)]);
		// System.out.println("integrierte Zahl: " + integral[(int)
		// (Short.MAX_VALUE * 0.5)]);
		// System.out.println("original Zahl: " + matchedHistogram[(int)
		// (Short.MAX_VALUE )]);
		// System.out.println("integrierte Zahl: " + integral[(int)
		// (Short.MAX_VALUE)]);

		HistoViz histoViz2 = new HistoViz();
		histoViz2.visualizeHistogram(integral, "Integral");
		return integral;
	}

	private void applyMatchedHistogram(int[][] x, int[] matchedHistogramOfX, int channel) {

		for (int i = 0; i < x[channel].length; i++) {
			int value = x[channel][i];
			if (value >= 0) {
				value = matchedHistogramOfX[value];
				// value = (matchedHistogramOfX[value] + value) / 2;
			} else {
				value = -matchedHistogramOfX[-value];
				// value = -((matchedHistogramOfX[-value] - value) / 2);
			}
			x[channel][i] = value;
		}

	}

	private int[] matchHistograms(double[] cumulativeHistogramX, double[] cumulativeHistogramY) {

		int[] matchedHistogram = new int[cumulativeHistogramX.length];

		for (int i = 0; i < cumulativeHistogramX.length; i++) {
			double value = cumulativeHistogramX[i];
			int foundValue = Short.MAX_VALUE;
			// for (int h = 0; h < cumulativeHistogramY.length; h++) {
			for (int h = 1; h < cumulativeHistogramY.length; h++) {
				if (cumulativeHistogramY[h] > value) {
					double ding1 = cumulativeHistogramY[h] - value;

					double ding2 = value - cumulativeHistogramY[h - 1];
					if (ding1 > ding2) {
						foundValue = h - 1;
					} else {
						foundValue = h;
					}

					break;
				}
			}
			matchedHistogram[i] = foundValue;

		}

		return matchedHistogram;
	}

	public double[] getCumulativeNormalizedHistogram(int[][] x, int channel) {

		int[] histogram = createHistogram(x, channel);

		// create cumulative Histogram
		long[] cumulativeHistogram = new long[histogram.length];
		long sum = 0;
		for (int i = 0; i < histogram.length; i++) {
			sum += histogram[i];
			cumulativeHistogram[i] = sum;
		}

		// normalize Cumulative Histogram to 1
		double[] normalizedCumulativeHistogram = new double[cumulativeHistogram.length];

		for (int i = 0; i < cumulativeHistogram.length; i++) {
			normalizedCumulativeHistogram[i] = (double) ((double) cumulativeHistogram[i] / (double) x[channel].length);
		}

		return normalizedCumulativeHistogram;
	}

	public double[] getCumulativeNormalizedHistogram(int[][] x, int channel, double limitation) {

		int[] histogram = createHistogram(x, channel);

		limitHisto(histogram, limitation);

		// create cumulative Histogram
		long[] cumulativeHistogram = new long[histogram.length];
		long sum = 0;
		for (int i = 0; i < histogram.length; i++) {
			sum += histogram[i];
			cumulativeHistogram[i] = sum;
		}

		// normalize Cumulative Histogram to 1
		double[] normalizedCumulativeHistogram = new double[cumulativeHistogram.length];

		for (int i = 0; i < cumulativeHistogram.length; i++) {
			normalizedCumulativeHistogram[i] = (double) ((double) cumulativeHistogram[i] / (double) x[channel].length);
		}

		return normalizedCumulativeHistogram;
	}

	private void limitHisto(int[] histogram, double limitation) {
		System.out.println("Limiting Histo --------------");
		// search for maximum value
		int maxValue = 0;
		for (int i = 0; i < histogram.length; i++) {
			int value = histogram[i];
			if (value > maxValue) {
				maxValue = value;
			}
		}
		System.out.println("Found max Value: " + maxValue);

		// limit Histo according to the new maxValue;
		int maxAllowed = (int) (maxValue * limitation);
		System.out.println("New allowed Max: " + maxAllowed);
		int cuttedSamples = 0;
		for (int i = 0; i < histogram.length; i++) {
			int value = histogram[i];
			if (value > maxAllowed) {
				histogram[i] = maxAllowed;
				cuttedSamples += value - maxAllowed;
			}
		}
		System.out.println("Cutted Samples : " + cuttedSamples);
		// distribute cutted samples over whole histo
		int distribution = (int) ((double) cuttedSamples / histogram.length);
		System.out.println("Will distribute: " + distribution);
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = histogram[i] + distribution;
		}

	}

	public void prelimitSignal(int[][] x, int[][] y) {
		Normalizer normalizer = new Normalizer();
		normalizer.normalize(x);
		normalizer.normalize(y);

		RMSCalculator rmsCal = new RMSCalculator();
		System.out.println((rmsCal.calculateRMS(x) / rmsCal.calculateRMS(y)));
		if ((rmsCal.calculateRMS(x) / rmsCal.calculateRMS(y)) < 0.3d) {
			System.out.println("Limiting small Peaks!!");
			limitSmallPeaks2(x, 0.0001);
			normalizer.normalize(x);
		}

	}

	private void limitSmallPeaks2(int[][] x, double percentageToCut) {

		int[] histogramLeft = createHistogram(x, 0);
		int[] histogramRight = createHistogram(x, 1);
		short limiterPositionLeft = calculateLimiterPositionNew(x, percentageToCut, histogramLeft);
		short limiterPositionRight = calculateLimiterPositionNew(x, percentageToCut, histogramRight);
		System.out.println("Short Max: " + Short.MAX_VALUE);
		System.out.println("Left Limiter Pos: " + limiterPositionLeft);
		System.out.println("Right Limiter Pos: " + limiterPositionRight);
		float finalLimiterPosition = 1.0f;
		if (limiterPositionLeft > limiterPositionRight) {
			System.out.println("Taking Left Limiterposition");
			finalLimiterPosition = (float) (limiterPositionLeft / (float) Short.MAX_VALUE);
		} else {
			System.out.println("Taking Right Limiterposition");
			finalLimiterPosition = (float) (limiterPositionRight / (float) Short.MAX_VALUE);
		}
		System.out.println("Will limit at: " + finalLimiterPosition);

		Limiter limiter = new Limiter();
		limiter.limit(x, finalLimiterPosition);

	}
}

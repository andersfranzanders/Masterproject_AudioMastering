package audioFX;

public class Convoluter {

	public double[][] convolute(double[][] audioIn, double[] filterKernel) {

		double[][] audioOut = new double[2][audioIn[0].length + filterKernel.length - 1];
		for (int channel = 0; channel < 2; channel++) {
			for (int i = 0; i < audioOut[0].length; i++) {
				double value = 0;
				for (int j = 0; j < filterKernel.length; j++) {
					if ((i - j) >= 0 && (i - j) < audioIn[channel].length) {
						value += (double) filterKernel[j] * audioIn[channel][i - j];
					}
				}
				audioOut[channel][i] = value;
			}
		}

		return audioOut;
	}

	public double[] produceMeanAverageFilterkernel(int pointsToAverage, int kernelLength) {
		double[] filterKernel = new double[kernelLength];

		for (int i = 0; i < pointsToAverage; i++) {
			filterKernel[i] = (double) 1 / pointsToAverage;
		}
		return filterKernel;
	}

}

package iirStuff;

import other.Complex;
import audioFX.Filter;
import audioFX.SpectralAnalyzer;
import synthesizer.BasicWaveSynthi;
import visualisation.FrequencyVisualisation;
import visualisation.NumericalVisualizer;

public class InitIIR {
	
	static BasicWaveSynthi synthi = new BasicWaveSynthi();
	static NumericalVisualizer numViz = new NumericalVisualizer();
	static FrequencyVisualisation freqViz = new FrequencyVisualisation();
	static FrequencyVisualisation freqViz2 = new FrequencyVisualisation();
	static Filter filter = new Filter();
	static SpectralAnalyzer specAnalyzer = new SpectralAnalyzer();
	
	public static void main(String[] args){
		
		int[][] x = produceSignal();
		//numViz.show(x, "x");
		double[] x_mag = filterImpuls(x);
		freqViz.visualizeAudio(x_mag);
		
		//double[] aFactors = new double[]{1.0, -1.414, 1.0};
		//double[] bFactors = new double[]{1.278, -0.810};
		double[] aFactors = filter.getZeroeFactorsForBiQuad(0.0, 0.0*Math.PI);
		double[] bFactors = filter.getPoleFactorsForBiQuad(0.6, 0.5*Math.PI);
		double[][] y = filter.filterbyIIR(x, aFactors, bFactors);
		
		int[][] yNormalized = normalizeSignal(y);
		
		aFactors = filter.getZeroeFactorsForBiQuad(0.0, 0.0*Math.PI);
		bFactors = filter.getPoleFactorsForBiQuad(0.45, 0.3*Math.PI);
		y = filter.filterbyIIR(yNormalized, aFactors, bFactors);
		yNormalized = normalizeSignal(y);
		
		aFactors = filter.getZeroeFactorsForBiQuad(0.0, 0.0*Math.PI);
		bFactors = filter.getPoleFactorsForBiQuad(0.35, 0.1*Math.PI);
		y = filter.filterbyIIR(yNormalized, aFactors, bFactors);
		yNormalized = normalizeSignal(y);
		
		double[] y_mag = filterImpuls(yNormalized);
		freqViz2.visualizeAudio(y_mag);
		
	}

	

	private static int[][] normalizeSignal(double[][] y) {
		double loudestSample = 0;
		for(Double sample: y[0]){
			if(sample > loudestSample){
				loudestSample = sample;
			}
		}
		System.out.println("Loudest Sample " + loudestSample);
		double ratio = Short.MAX_VALUE/loudestSample;
		int[][] normalizedY = new int[2][y[0].length];
		for(int i = 0; i < y[0].length; i++){
			normalizedY[0][i] = (int) (y[0][i] * ratio);
		
		}
		return normalizedY;
	}



	private static int[][] produceSignal() {
		return synthi.synthesizeDiracImpulseInt(1, (int)Math.pow(2, 10));
		//return synthi.synthesizeSine(1000, 0.1);
	}

	private static double[] filterImpuls(int[][] x) {
		Complex[] spectrum = specAnalyzer.calPolarSpectrum(x, (int)Math.pow(2, 9), 0, 0);
		double[] mag = new double[spectrum.length];
		for(int i = 0; i < spectrum.length; i++){
			mag[i] = (int) spectrum[i].re();
		}
		//numViz.show(mag, "Spec of Dirac");
		
		return mag;
	}

}

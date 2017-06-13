package arbeit;

import other.Complex;
import other.FFT;
import audioFX.SpectralAnalyzer;
import synthesizer.BasicWaveSynthi;
import visualisation.NumericalVisualizer;

public class VisMain {

	static BasicWaveSynthi synthi = new BasicWaveSynthi();
	static NumericalVisualizer numVis = new NumericalVisualizer();
	static SpectralAnalyzer spectralAnalyzer = new SpectralAnalyzer();
	
	
	public static void main(String[] args) {
		
		
		 int[][] audio = synthi.synthesizeSquare(10000, 0.0002);
		 numVis.show(audio, "Zeit");
		 Complex[] spec = spectralAnalyzer.calRectangularFFT(audio, 4, 0, 0);
		 FFT.show(spec, "Spec");
	}

}

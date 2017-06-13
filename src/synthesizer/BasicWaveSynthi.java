package synthesizer;

public class BasicWaveSynthi {

	public int[][] synthesizeSquare(double frequInHz, double doubleTimeInSec) {

		int sampleRate = 44100;

		int[][] audio = new int[2][(int) (sampleRate * doubleTimeInSec)];

		int period = (int) (sampleRate / frequInHz);

		for (int sample = 0; sample < audio[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				if (sample % period < (period / 2)) {
					audio[channel][sample] = 1;
				} else {
					audio[channel][sample] = -1;
				}
			}
		}

		return audio;
	}
	
	public double[][] synthesizeDiracImpulse(int maxValue, int numSamples) {

		double[][] audio = new double[2][numSamples];
		for (int sample = 0; sample < audio[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				if(sample == 0){
					audio[channel][sample] = maxValue;
				}else{
					audio[channel][sample] = 0;
				}
			}
		}
		
		return audio;

	}
	public int[][] synthesizeDiracImpulseInt(int maxValue, int numSamples) {

		int[][] audio = new int[2][numSamples];
		for (int sample = 0; sample < audio[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				if(sample == 0){
					audio[channel][sample] = maxValue;
				}else{
					audio[channel][sample] = 0;
				}
			}
		}
		
		return audio;

	}


	public double[][] synthesizeDiracImpulse(int maxValue, double timeInSec) {

		int sampleRate = 44100;
		double[][] audio = new double[2][(int) (sampleRate * timeInSec)];
		for (int sample = 0; sample < audio[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				if(sample == 0){
					audio[channel][sample] = maxValue;
				}else{
					audio[channel][sample] = 0;
				}
			}
		}
		
		return audio;

	}
	public int[][] synthesizeDiracImpulseInt(int maxValue, double timeInSec) {

		int sampleRate = 44100;
		int[][] audio = new int[2][(int) (sampleRate * timeInSec)];
		for (int sample = 0; sample < audio[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				if(sample == 0){
					audio[channel][sample] = maxValue;
				}else{
					audio[channel][sample] = 0;
				}
			}
		}
		
		return audio;

	}

	public int[][] synthesizeSine(double frequInHz, double doubleTimeInSec) {
		int sampleRate = 44100;

		int[][] audio = new int[2][(int) (sampleRate * doubleTimeInSec)];

		int period = (int) (sampleRate / frequInHz);

		for (int sample = 0; sample < audio[0].length; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				int value = (int) (Short.MAX_VALUE * Math.sin(((double) sample / period) * 2 * Math.PI));
				// System.out.println(value);
				audio[channel][sample] = value;
			}
		}

		return audio;
	}

}

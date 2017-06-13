package audioFX;

public class Normalizer extends AudioFX{
	
	public void normalize(int[][] audio){
		
		int numSamples = audio[0].length;
		
		int maxValueFoundInSignal = calculateMaxValue(audio, numSamples);

		gainAudio(audio, numSamples, maxValueFoundInSignal, Short.MAX_VALUE);
		
	}
	
	public void normalize(int[][] audio, int maxValue, double percentageOfMaxValue){
		
		int numSamples = audio[0].length;
		
		int maxValueFoundInSignal = calculateMaxValue(audio, numSamples);
		maxValue = (int) (maxValue * percentageOfMaxValue);
		gainAudio(audio, numSamples, maxValueFoundInSignal, maxValue);
		
	}
	


	private void gainAudio(int[][] audio, int numSamples, int maxValueFoundInSignal, int maxValue) {
		
		for (int sample = 0; sample < numSamples; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				int value = audio[channel][sample];
				double calculatedValue = ((double) value / maxValueFoundInSignal) * maxValue;
				audio[channel][sample] = (short) calculatedValue;
			}
		}
	}

	private int calculateMaxValue(int[][] audio, int numSamples) {
		
		int currentMaxPos = 0;
		int currentMaxNeg = 0;
		int finalMax = 0;

		for (int sample = 0; sample < numSamples; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				int value = audio[channel][sample];
				if (value > currentMaxPos) {
					currentMaxPos = value;
				}
				if (value < currentMaxNeg) {
					currentMaxNeg = value;
				}
			}
		}

		if (currentMaxPos > -currentMaxNeg) {
			finalMax = currentMaxPos;
		} else {
			finalMax = -currentMaxNeg;
		}
		return finalMax;
	}

}

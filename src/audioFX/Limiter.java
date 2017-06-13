package audioFX;

public class Limiter extends AudioFX{
	
	public void limit(int[][] audio, float cutOff){
		
		int maxValue = (int) (Short.MAX_VALUE * cutOff);

		int numSamples = audio[0].length;
		
		for (int sample = 0; sample < numSamples; sample++) {
			for (int channel = 0; channel < 2; channel++) {
				
				int value = audio[channel][sample];
				
				if (value > maxValue) {
					audio[channel][sample] = maxValue;
				}
				if (value < -maxValue) {
					audio[channel][sample] = (short) -maxValue;
				}
			}
		}

	}

}

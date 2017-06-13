package audioFX;

public class RMSCalculator extends AudioFX {

	public double calculateRMS(int[][] audio) {

		double rms = 0;
		double leftRMS = 0;
		double rightRMS = 0;
		int numSamples = audio[0].length;
		long leftSumOfSquares = 0;
		long rightSumOfSquares = 0;

		for (int sample = 0; sample < numSamples; sample++) {
			int leftValue = audio[0][sample];
			leftSumOfSquares += leftValue * leftValue;
			int rightValue = audio[1][sample];
			rightSumOfSquares += rightValue * rightValue;
		}

		leftRMS = Math.sqrt((leftSumOfSquares / numSamples));
		rightRMS = Math.sqrt((rightSumOfSquares / numSamples));

		rms = (leftRMS + rightRMS) / 2;
		rms = rms / Short.MAX_VALUE;

		return rms;
	}
	
	public double[] calculateRMSplusMinus(int[][] audio){
		
		
		
		
		return null;
		
	}

}

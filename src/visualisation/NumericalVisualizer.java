package visualisation;

public class NumericalVisualizer {

	public void show(int[][] audio, String title) {

		System.out.println(title + " ------------------");

		for (int i = 0; i < audio[0].length; i++) {
			System.out.println(audio[0][i]);
		}
	}
	public void show(double[][] audio, String title) {

		System.out.println(title + " ------------------");

		for (int i = 0; i < audio[0].length; i++) {
			System.out.println(audio[0][i]);
		}
	}

	public void show(int[] audio, String title) {

		System.out.println(title + " ------------------");

		for (int i = 0; i < audio.length; i++) {
			System.out.println(audio[i]);
		}
	}
	
	public void show(double[] audio, String title) {

		System.out.println(title + " ------------------");

		for (int i = 0; i < audio.length; i++) {
			System.out.println(audio[i]);
		}
	}

}

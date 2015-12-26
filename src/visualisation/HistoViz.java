package visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import visualisation.AudioVisualisation.MyPanel;

public class HistoViz {

	int histoBin = 50;
	int windowLength = (Short.MAX_VALUE+1) / histoBin;
	int windowHeight = 500;

	public void visualizeHistogram(int[] histo, String title) {

		JFrame jFrame = new JFrame();
		jFrame.setBackground(Color.BLACK);
		MyPanel panel = new MyPanel(histo);
		jFrame.add(panel);

		jFrame.setSize(windowLength + 25, windowHeight + 25);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		jFrame.setTitle(title);

	}

	class MyPanel extends JPanel {

		int[] histo;

		MyPanel(int[] histo) {
			this.histo = histo;
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
		}

		public void paintComponent(Graphics g) {

			g.setColor(Color.WHITE);

			int[] histoForVis = prepareHistoForVis(histo);

			for (int i = 0; i < windowLength; i++) {

				g.drawLine(i, windowHeight, i, windowHeight - histoForVis[i]);
			}
			
			g.setColor(Color.RED);
			for(int i = 0; i < windowLength; i++){
				g.drawLine(0, windowHeight, windowLength,0);
			}

		}

		private int[] prepareHistoForVis(int[] histo) {

			int highestSample = 0;
			for (int i = 0; i < histo.length; i++) {
				int temp = histo[i];
				if (temp > highestSample) {
					highestSample = temp;
				}
			}

			int[] histoForVis = new int[windowLength];
			for (int i = 0; i < histoForVis.length; i++) {
				int max = 0;

				for (int j = i * histoBin; j < (i * histoBin) + histoBin; j++) {

					int value = histo[j];
					if (value > max) {
						max = value;
					}

				}

				double tempValue = (double) max / highestSample;
				max = (int) ((double) tempValue * windowHeight);
				histoForVis[i] = max;
			}

			return histoForVis;
		}

	}

}

package visualisation;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import visualisation.HistoViz.MyPanel;

public class CumulativeHistoViz {
	
	int histoBin = 50;
	int windowLength = (Short.MAX_VALUE+1) / histoBin;
	int windowHeight = 500;

	public void visualizeHistogram(double[] histo, String title) {

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

		double[] histo;

		MyPanel(double[] histo) {
			this.histo = histo;
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
		}

		public void paintComponent(Graphics g) {

			g.setColor(Color.WHITE);

			int[] histoForVis = prepareHistoForVis(histo);

			for (int i = 0; i < windowLength; i++) {

				g.drawLine(i, 500, i, 500 - histoForVis[i]);
			}

		}

		private int[] prepareHistoForVis(double[] histo) {

			double highestSample = 0;
			for (int i = 0; i < histo.length; i++) {
				double temp = histo[i];
				if (temp > highestSample) {
					highestSample = temp;
				}
			}

			int[] histoForVis = new int[windowLength];
			for (int i = 0; i < histoForVis.length; i++) {
				double max = 0;

				for (int j = i * histoBin; j < (i * histoBin) + histoBin; j++) {

					double value = histo[j];
					if (value > max) {
						max = value;
					}

				}

				double tempValue = (double) max / highestSample;
				max = ((double) tempValue * windowHeight);
				histoForVis[i] = (int)max;
			}

			return histoForVis;
		}

	}

}

package visualisation;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import visualisation.AudioVisualisation.MyPanel;

public class FrequencyVisualisation {

	int windowLength;
	int windowHeight = 400;

	public void visualizeAudio(double[] frequData) {

		JFrame jFrame = new JFrame();
		jFrame.setBackground(Color.BLACK);
		FrequPanel panel = new FrequPanel(frequData);
		jFrame.add(panel);
		windowLength = frequData.length;

		jFrame.setSize(windowLength, windowHeight + 25);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);

	}

	class FrequPanel extends JPanel {

		double[] frequData;

		FrequPanel(double[] frequData) {
			this.frequData = frequData;
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
		}

		public void paintComponent(Graphics g) {

			g.setColor(Color.WHITE);

			int frequPoint = 0;
			double hightestSample = normalizeFreqData();
			
			for (int i = 0; i < windowLength - 1; i++) {

				double frequAmp = frequData[frequPoint];

				frequPoint++;

				int height = (int) ((double)(windowHeight-10) / hightestSample * (frequAmp));
				int iToDraw = i;
				int nextIToDraw = i+1;
				
				//int iToDraw = (int) ((double) 55 * Math.log(i) / Math.log(2));
				//int nextIToDraw = (int) ((double) 55 * Math.log(i + 1) / Math.log(2));

				g.fillRect(iToDraw, 400 - height, nextIToDraw - iToDraw, height);
			}

		}

		private double normalizeFreqData() {
			double best = 0;
			for(Double i: frequData){
				if(i>best){
					best = i;
				}
			}
			
			return best;
			
		}

	}
}

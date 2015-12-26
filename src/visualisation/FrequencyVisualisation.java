package visualisation;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import visualisation.AudioVisualisation.MyPanel;

public class FrequencyVisualisation {

	
	int windowLength;
	int windowHeight = 400;
	
	public void visualizeAudio(int[] frequData){
		
		JFrame jFrame = new JFrame();
		jFrame.setBackground(Color.BLACK);
		FrequPanel panel = new FrequPanel(frequData);
		jFrame.add(panel);
		windowLength = frequData.length;

		
		jFrame.setSize(windowLength, windowHeight+25);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		
	}
	
class FrequPanel extends JPanel{
		
		int[] frequData;
		
		FrequPanel(int[] frequData){
			this.frequData = frequData;
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
		}
		
		 public void paintComponent(Graphics g){
			 
			 g.setColor(Color.WHITE);
			 
			 int frequPoint = 0;
			 int numberOfFrequPoints = frequData.length;
			 int jumpFrequPoints = numberOfFrequPoints / windowLength;
			 //System.out.println(jumpFrequPoints);
			 
			// int maxFrequ = calculateMaxFrequ(jumpFrequPoints);
			// System.out.println(calculateMaxFrequ(jumpFrequPoints) - Short.MAX_VALUE);
			 int maxFrequ = 512 * Short.MAX_VALUE;
			// System.out.println("MAx : " + maxFrequ);
			 
			 for(int i = 0; i < windowLength-1; i++){
				 
				 int frequAmp = frequData[frequPoint];
				
				 

					 frequPoint++;

				 //System.out.print(frequAmp + " ");
				 int height = (int) ( windowHeight * (((double)frequAmp /maxFrequ) ));
				// System.out.println(height);
				 //System.out.println(height);
				 
				int iToDraw = (int) ((double)55* Math.log(i)/Math.log(2));
				int nextIToDraw = (int) ((double)55* Math.log(i+1)/Math.log(2));
				 
				// g.drawLine(iToDraw, 400, iToDraw, 400-height);
				 g.fillRect(iToDraw, 400 - height, nextIToDraw - iToDraw, height);
			 }
			// g.drawRect(0, 10, 50, 90);
			 //g.drawLine(50, 400, 50, 200);
			// g.drawLine(0, 100, windowLength, 100);
		 }

		private int calculateMaxFrequ(int jumpFrequPoints) {
			
			int maxValue = 0;
			for (int i = 0; i < frequData.length; i++){
				int value = frequData[i];
				// for (int j = 0; j < jumpFrequPoints;j++){
					// i++;
				 //}
				if (value > maxValue){
					maxValue = value;
				}
			}
			return maxValue;
		}
		
		
	}
}

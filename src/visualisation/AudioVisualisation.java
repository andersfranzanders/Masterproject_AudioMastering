package visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AudioVisualisation {
	
	int windowLength = 1200;
	int windowHeight = 200;
	
	public void visualizeAudio(int[][] audioData){
		
		JFrame jFrame = new JFrame();
		jFrame.setBackground(Color.BLACK);
		MyPanel panel = new MyPanel(audioData);
		jFrame.add(panel);

		
		jFrame.setSize(windowLength, windowHeight + 25);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		
		
		
	}
	
	
	class MyPanel extends JPanel{
		
		int[][] audioData;
		
		MyPanel(int[][] audioData){
			this.audioData = audioData;
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
		}
		
		 public void paintComponent(Graphics g){
			 
			 g.setColor(Color.WHITE);
			 
			 int sample = 0;
			 int numberOfSamples = audioData[0].length - 1;
			 int jumpSamples = numberOfSamples / windowLength;
			 int halfHeight = windowHeight / 2;
			 
			 for(int i = 0; i < windowLength; i++){
				 
				 int sampleHight = audioData[0][sample];
				 
				 for (int j = 0; j < jumpSamples;j++){
					 sample++;
				 }
				 
			
				 int nextsampleHight = audioData[0][sample];
				 int y = (int) (halfHeight + halfHeight * ((float)sampleHight / Short.MAX_VALUE));
				 int nextY = (int) (halfHeight + halfHeight * ((float)nextsampleHight / Short.MAX_VALUE));
				 
				 g.drawLine(i, y, i+1, nextY);
			 }
			 
			// g.drawLine(0, 100, windowLength, 100);
		 }
		
		
	}

}

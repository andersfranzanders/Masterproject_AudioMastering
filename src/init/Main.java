package init;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import other.AudioFormatNotSupportedException;

public class Main {

	static String pathToFiles = "resources/lpp_ungemastert/";
	static String aimSpecFileName = "resources/lpp_ungemastert/Burn It Up_short.wav";
	static String outPath = "resources/lpp_out/";
	static String masteredPostfix = " (burnItUp)";

	public static void main(String[] args) {

		// int value = (int) (Short.MAX_VALUE * Math.sin(((double)20000/44100) *
		// Math.PI));

		// System.out.println(value);

		List<String> songsToMaster = new ArrayList<String>();
		songsToMaster.add("Dancehall_REEEMIX preView 01");
		songsToMaster.add("LPP - Bendecido seas tu (Remix)");
		songsToMaster.add("Los Papas Protestantes - Crystal y Diamantes 020309");
		songsToMaster.add("LPP - Oda a la belleza (2008 Version)");
		songsToMaster.add("LPP - Oda a la belleza (original)");
		songsToMaster.add("LPP - Passion (absolute letzte)");
		songsToMaster.add("LPP - Pisco (Remix)");
		songsToMaster.add("LPP - Sin miedo, duro");
		songsToMaster.add("Los Papas Protestantes - Como eh");
		
		
		
		for (String songToMaster : songsToMaster) {
			
			String fileNameToFilter = pathToFiles + songToMaster + ".wav";
			String outFileName = outPath + songToMaster + masteredPostfix + ".wav";
			
			
			System.gc();

			Init init = new Init();
			try {
				init.start(fileNameToFilter, aimSpecFileName, outFileName);
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AudioFormatNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

package jrmr.vds.model.dnn.io;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.swing.JLabel;

import jrmr.vds.model.config.Config;

public class WordObfuscationUtils {

	Config config;

	public WordObfuscationUtils() {
		config = new Config();
	}

	public void generateCNNTrainData() {
		writeDnnWordObfuscationDataToCsv("config\\obf_numbers.txt","word_obf_numbers");
		writeDnnWordObfuscationDataToCsv("config\\obf_letters.txt","word_obf_letters");		
	}
	
	public void writeDnnWordObfuscationDataToCsv(String inputFile, String outputFile) {   	

		File outputFile1 = new File("D:\\workspace\\vds.deeplearning1\\resources\\" + outputFile + "_input.txt");
		File outputFile2 = new File("D:\\workspace\\vds.deeplearning1\\resources\\" + outputFile + "_target.txt");

		FileWriter fileWriter1 = null;
		PrintWriter printWriter1 = null;
		FileWriter fileWriter2 = null;
		PrintWriter printWriter2 = null;

		try {

			fileWriter1 = new FileWriter(outputFile1);
			printWriter1 = new PrintWriter(fileWriter1);
			fileWriter2 = new FileWriter(outputFile2);
			printWriter2 = new PrintWriter(fileWriter2);
			
			String[] fontsVector = new String[10];
			fontsVector[0] = "Courier";
			fontsVector[1] = "Lucida";
			fontsVector[2] = "Arial";
			fontsVector[3] = "Consolas";
			fontsVector[4] = "Comic Sans MS";
			fontsVector[5] = "Impact";
			fontsVector[6] = "Tahoma";
			fontsVector[7] = "Verdana";
			fontsVector[8] = "SketchFlow Print";
			fontsVector[9] = "MS Gothic";			
			

			for (int l = 0; l < 6000; l++) {			

				FileInputStream fiStream;
				fiStream = new FileInputStream(inputFile);
				DataInputStream diStream = new DataInputStream(fiStream);		
				BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
				String docLine;

				while ((docLine = docReader.readLine()) != null) 
				{
										
					String[] wordPair = docLine.split("[ \n\r]");
					
					System.out.println(wordPair[0] + " / " + l);
					
					int fontNumber = l % 10;
					
					int[] v = getStringRepresentation(wordPair[0],fontsVector[fontNumber]);
					String dataLine = "";
					for (int i = 0; i < v.length; i++) {
						if (i == 0) {
							dataLine = Integer.toString(v[i]);
						} else {
							dataLine = dataLine + "," + Integer.toString(v[i]);
						}						
					}
					printWriter1.println(dataLine);

					printWriter2.println(wordPair[1]);
				}

				docReader.close();
				diStream.close();
				fiStream.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter1) 
					fileWriter1.close();
				if (null != fileWriter2) 
					fileWriter2.close();				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

//	public int[] getStringRepresentation(String s) {
//
//		Font font = new Font("lucida", Font.PLAIN, 24);        
//		FontMetrics metrics = new JLabel().getFontMetrics(font);
//		int width = 28;
//		int height = 28;
//
//		int[] sVector = new int[width * height];
//		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
//		Graphics2D g2d = bi.createGraphics();
//		g2d.setFont(font);
//		g2d.setColor(Color.black);
//		g2d.drawString(s, (width - metrics.stringWidth(s)) / 2, height - 8);
//		g2d.dispose();        
//		int k = 0;
//		for(int j = 0; j < height; j++){
//			for(int i = 0; i < width; i++){				
//				if (bi.getRGB(i,j) != 0) {           		
//					sVector[k] = 255;					
//				} else {
//					sVector[k] = 0;
//				} 
////				System.out.print(bi.getRGB(i,j) != 0 ? "255," : "000,");
//				System.out.print(bi.getRGB(i,j) != 0 ? "###" : " . ");
//				k++;
//			}
//			System.out.print("\n");
//		}      
//		return sVector;
//	}

	public int[] getStringRepresentation(String s, String fontName) {

		Font font = new Font(fontName, Font.PLAIN, 24);        
		FontMetrics metrics = new JLabel().getFontMetrics(font);
		int width = 28;
		int height = 28;

		int[] sVector = new int[width * height];
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		g2d.setColor(Color.black);
		g2d.drawString(s, (width - metrics.stringWidth(s)) / 2, height - 8);
		g2d.dispose();        
		int k = 0;
		for(int j = 0; j < height; j++){
			for(int i = 0; i < width; i++){				
				if (bi.getRGB(i,j) != 0) {           		
					sVector[k] = 255;					
				} else {
					sVector[k] = 0;
				} 
//				System.out.print(bi.getRGB(i,j) != 0 ? "255," : "000,");
//				System.out.print(bi.getRGB(i,j) != 0 ? "###" : " . ");
				k++;
			}
//			System.out.print("\n");
		}      
		return sVector;
	}
	
	
}

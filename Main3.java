package pitchDetection;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;

import ddf.minim.analysis.WindowFunction;
import ddf.minim.ugens.FilePlayer;

public class Main3 extends PApplet
{
	FilePlayer player;
	Minim minim;
	AudioInput in;
	float min;
	float max;
	                         
	float[] frequencies = {110.00f, 116.54f, 123.47f, 130.81f, 138.59f, 146.83f, 155.56f, 164.81f, 174.61f, 185.00f, 196.00f, 207.65f, 220.00f, 233.08f, 246.94f, 261.63f, 277.18f, 293.66f, 311.13f, 329.63f, 349.23f, 369.99f, 392f, 415.30f, 440.00f, 466.16f, 493.88f, 523.25f, 554.37f, 587.33f, 622.25f, 659.25f, 698.26f, 739.99f};
	//String[] spellings = {"D,", "E,", "F,", "G,", "A,", "B,", "C", "D", "E", "F", "G", "A", "B","c", "d", "e", "f", "g", "a", "b", "c'", "d'", "e'", "f'", "g'", "a'", "b'", "c''", "d''"}; 	
	String[] spellings = {"A","A#","B","C","C#","D","D#","E","F","F#","G","G#","A","A#","B","C","C#","D","D#","E","F","F#","G","G#","A","A#","B","C","C#","D","D#","E","F","F#"};
	
	int sampleRate = 10000;
	int frameSize = 1024;
	FFT fft;
	private int rectX, rectY;
	private int rectSize = 90;

	private int rectColor;
	private int rectHighlight;
	private int currentColor;
	private int baseColor;
	boolean rectOver = false;
	ArrayList<String> obj = new ArrayList<String>();
	
	Map<String, Integer> letterToNumber = new HashMap<String, Integer>();
	Map<Integer, String> numberToLetter = new HashMap<Integer, String>();
	
	List<String> inputValues = new ArrayList<String>();
	List<List<String>> targetValues = new ArrayList<List<String>>();
	
	List<String> titanic =  Arrays.asList("E","D#","E","D#","E","F#","G#","F#","E","D#","E","B");
	List<String> smokeABowl =  Arrays.asList("C", "D#", "F", "C", "D#", "F#", "F", "C", "D#", "F", "D#", "C");
	List<String> FCD = Arrays.asList("C#", "B", "C#", "F#", "D", "C#", "D", "C#", "B",
			"D", "C#", "D", "F#", "B", "A", "B", "A", "G#", "B", "A", "B");
	List<String> roch = Arrays.asList("C", "D", "C", "D", "C", "A#", "G#", "A#", "E", "G");
	
	List<String> songNames = Arrays.asList("Titanic", "Smoke on the Water", "Final CountDown", "Roch");

	
	//private int counter = 0;
	
	public void setup()
	{
		
		size(frameSize, 500);
		smooth();
		minim = new Minim(this);
		
		in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
		fft = new FFT(frameSize, sampleRate);
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		obj.add("0");
		for (int i = 1; i < 13; i++) {
			letterToNumber.put(spellings[i-1], i);
			numberToLetter.put(i, spellings[i-1]);
		}		
		targetValues.add(titanic);
		targetValues.add(smokeABowl);
		targetValues.add(FCD);
		targetValues.add(roch);
	}
	
	public String spell(float frequency)
	{

		float minDiff = Float.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0 ; i < frequencies.length; i ++)
		{
			float diff = Math.abs(frequencies[i] - frequency);
			if (diff < minDiff)
			{
				minDiff = diff;
				minIndex = i;
			}
		}
		return spellings[minIndex];
	}
	
	public int countZeroCrossings()
	{
		int count = 0;
		
		for (int i = 1 ; i < in.bufferSize(); i ++)
		{
			if (in.left.get(i - 1) > 0 && in.left.get(i) <= 0)
			{
				count ++;
			}
		}		
		return count;		
	}
	
	public float FFTFreq()
	{
		// Find the higest entry in the FFT and convert to a frequency
		float maxValue = Float.MIN_VALUE;
		int maxIndex = -1;
		for (int i = 0 ; i < fft.specSize() ; i ++)
		{
			if (fft.getBand(i) > maxValue)
			{
				maxValue = fft.getBand(i);
				maxIndex = i;
			}
		}
		return fft.indexToFreq(maxIndex);
	}
	
	public void draw()
	{ 
		if (Character.toLowerCase(key) == 's'){
			background(0);
			stroke(255);
			float average = 0;
			
			for (int i = 0 ; i < in.bufferSize(); i ++)
			{
				float sample = in.left.get(i);
				if (sample < min)
				{
					min = sample;
				}
				if (sample > max)
				{
					max = sample;
				}
				sample *= 100.0;
				line(i, height / 2, i,  (height / 2) + sample);
				average += Math.abs(in.left.get(i));
				//point(i, (height / 2) + sample);
			}

			
			average /= in.bufferSize();
			
			fft.window(FFT.HAMMING);
			fft.forward(in.left);
			stroke(0, 255, 255);
			for (int i = 0 ; i < fft.specSize() ; i ++)
			{
				line(i, height, i, height - fft.getBand(i)*100);
			}
			
			fill(255);
			text("Amp: " + average, 10, 10);
			int zeroC = countZeroCrossings();		
			
			if (average > 0.001f)
			{
				float freqByZeroC = ((float) sampleRate / (float)in.bufferSize()) * (float) zeroC;
				//System.out.println(freqByZeroC);
				text("Zero crossings: " + zeroC, 10, 30);
				text("Freq by zero crossings: " + freqByZeroC, 10, 50);
				text("Spelling by zero crossings: " + spell(freqByZeroC), 10, 70);
				float freqByFFT = FFTFreq();
				
				String fftSpell = spell(freqByFFT);
				if (freqByFFT >= 110){
					//counter+= 1;
					text("Freq by FFT: " + freqByFFT, 10, 90);
					text("Spelling by FFT: " + fftSpell, 10, 110);
					if (obj.size() > 0) {
						if (fftSpell!= obj.get(obj.size()-1)){
							obj.add(fftSpell);
							inputValues.add(fftSpell);
						}
					}
					
				}else{
					text("Freq by FFT: " + "N/A", 10, 90);
					text("Spelling by FFT: " + "N/A", 10, 110);
				}
										
			}
			float smallRadius = 50;
			float bigRadius = (smallRadius * 2) + (average * 500);
			
			stroke(0, 255, 0);
			fill(0, 255, 0);
			ellipse(width / 2, height / 2, bigRadius, bigRadius);
			stroke(0);
			fill(0);
			ellipse(width / 2, height / 2, smallRadius, smallRadius);
		}
				
		//ellipse(width/2, height/2, smallRadius, smallRadius);
	}
	
	public void keyReleased(){
		
		if (Character.toLowerCase(key) == 'r'){
			System.out.println("R BUTTON PRESSED");
			in.close();
			//setup2();
			
			for (int i = 0; i < obj.size(); i++){
				System.out.print(obj.get(i));
				
			}
			
			// Transpose
			if (targetValues.isEmpty()) {
				return;
				// TODO: Throw an exception?
			}
			
			
			
			//ArrayList<ArrayList<String>> listOSongs = new ArrayList<>();
			
			//String song1_filename = "C:\\Users\\Aman\\Desktop\\SomeSong.txt";

			//ArrayList<String> song1 = new ArrayList<String>();
			/*
			try {
				BufferedReader read_line_by_line = new BufferedReader(new FileReader(song1_filename));
	            String line = null; // variable to reference the shit below
	            System.out.println("hi");
				while((line = read_line_by_line.readLine()) != null) {
					System.out.println("HALP:" + line);
					song1 = new ArrayList<String>(Arrays.asList(line.split(" ")));
	            }   
			}
			catch (Exception e) {
				System.out.println("sdf");
			}
			
			System.out.println("yesshit");
			for(String s : song1) {
				System.out.print(s);
			}
			*/
			
/*			firstdifference();*/
			
			// Calculate Average!
			
			// Vector of the differences between each element.
			int counter = 0;
			Map<Integer, Double> averages = new HashMap<Integer, Double>();
			for (List<String> l : targetValues ) {
				int length = l.size() < inputValues.size() ? l.size() : inputValues.size();
				for (int j = 0; j < length; j++) {
					if (averages.get(counter) == null) {
						averages.put(counter, 
								(double)(Math.abs(letterToNumber.get(inputValues.get(j)) 
								-letterToNumber.get(l.get(j)))));
					} else {
						double current = averages.get(counter);
						averages.put(counter, current +(double)(Math.abs(
								letterToNumber.get(inputValues.get(j)) 
								-letterToNumber.get(l.get(j)))));
					}
				}
				averages.put(counter, averages.get(counter) / length);
				System.out.println("Average: " + averages.get(counter) + " for array " + counter);
				counter++;
			}
			
			// Go through the averages and find the lowest one.
			Double min = Collections.min(averages.values());
			int idx = -1;
			for (int j = 0; j < averages.values().size(); j++) {
				if (averages.get(j) == min) {
					System.out.println("The song is " + songNames.get(j) + ".");
				}
			}


		}
		
	}

	private void firstdifference() {
		// Difference between first indicies
					
		int firstdifference = letterToNumber.get(targetValues.get(0))
						- letterToNumber.get(inputValues.get(0));
		System.out.println("Difference: " + firstdifference);
		int i = 0;
		for (String n : inputValues) {
			inputValues.set(i, numberToLetter.get((letterToNumber.get(inputValues.get(i)) + firstdifference)));
			i++;
		}
					
		System.out.println(inputValues);
		System.out.println(targetValues);
		
	}

}
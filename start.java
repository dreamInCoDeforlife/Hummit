package pitchDetection;

import java.util.ArrayList;

import processing.core.PApplet;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.*;
import ddf.minim.analysis.WindowFunction;
import ddf.minim.ugens.FilePlayer;

public class start extends PApplet{

	private int rectX, rectY;
	private int rectSize = 90;

	private int rectColor;
	private int rectHighlight;
	private int currentColor;
	private int baseColor;
	boolean rectOver = false;

	public void setup(){
	  size(800,400);
	  rectColor = color(102);
	  rectHighlight = color(51);
	  baseColor = color(0);
	  currentColor = baseColor;
	  rectX = width/2 - rectSize-10;
	  rectY = height/2-rectSize/2;
	  ellipseMode(CENTER);
	}

	public void draw(){
	  update(mouseX, mouseY);
	  background(currentColor);
	  if (rectOver){
	    fill(rectHighlight);
	  }else{
	    fill(rectColor);
	  }
	  stroke(255);
	  rect(rectX, rectY, rectSize, rectSize);

	}

	public void update(int x, int y){
	  if(overRect(rectX, rectY, rectSize, rectSize)){
	    rectOver = true;
	  }else{
	    rectOver = false;
	  }
	}

	public void mousePressed(){
		
		recordingWindow();
		
		
		
	}

	boolean overRect(int x , int y, int width, int height){
	  if (mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height){
	    return true;
	  }else{
	    return false;
	  }
	}
	

	public void recordingWindow() {
		
		PApplet.main(Main2.class.getName());	
		
	}
}

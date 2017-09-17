package pitchDetection;
import ddf.minim.*;
import ddf.minim.ugens.*;
import processing.core.PApplet;
public class Record extends PApplet{
	Minim minim;

	// for recording
	AudioInput in;
	AudioRecorder recorder;
	boolean recorded;

	// for playing back
	AudioOutput out;
	FilePlayer player;



	public void setup()
	{
	  size(800, 400);

	  minim = new Minim(this);
	  
	  // get a stereo line-in: sample buffer length of 2048
	  // default sample rate is 44100, default bit depth is 16
	  in = minim.getLineIn(Minim.STEREO, 2048);
	  
	  // create an AudioRecorder that will record from in to the filename specified.
	  // the file will be located in the sketch's main folder.
	  
	  recorder = minim.createRecorder(in, "myrecording.wav");
	  
	  // get an output we can playback the recording on
	  out = minim.getLineOut( Minim.STEREO );
	  
	  textFont(createFont("Arial", 12));
	}

	public void draw()
	{
	  background(0); 
	  stroke(255);
	  // draw the waveforms
	  // the values returned by left.get() and right.get() will be between -1 and 1,
	  // so we need to scale them up to see the waveform
	  for(int i = 0; i < in.left.size()-1; i++)
	  {
	    line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
	    line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
	  }
	  
	  if ( recorder.isRecording() )
	  {
	    text("Now recording, press the r key to stop recording.", 5, 15);
	  }
	  else if ( !recorded )
	  {
	    text("Press the r key to start recording.", 5, 15);
	  }
	  else
	  {
	    text("Press the s key to save the recording to disk and play it back in the sketch", 5, 15);
	  }
	}

	public void keyReleased()
	{
	  if ( !recorded && key == 'r' ) 
	  {

	    if ( recorder.isRecording() ) 
	    {
	      recorder.endRecord();
	      recorded = true;
	    }
	    else 
	    {
	      recorder.beginRecord();
	    }
	  }
	  if ( recorded && key == 's' )
	  {

	    if ( player != null )
	    {
	    	
	        player.unpatch( out );
	        player.close();
	    }
	    player = new FilePlayer( recorder.save() );
	    
	    player.patch( out );
	    player.play();
	    
	    PApplet.main(Main2.class.getName());
	  }
	  if (recorded && key == 't'){
		  recorded = false;
		  draw();
		  
	  }
	}

	public void startWindow() {
		PApplet.main(Record.class.getName());
	}
}

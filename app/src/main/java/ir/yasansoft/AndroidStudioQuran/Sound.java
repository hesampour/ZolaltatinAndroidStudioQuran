package ir.yasansoft.AndroidStudioQuran;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Sound {
	MediaPlayer m = new MediaPlayer();

	   public void playID(int id,Context context,String path) {
		   
        //   resetNowplayingColor();
		   m = new MediaPlayer();
		   if(m.isPlaying())
		   {
			   m.stop();
			   m.release();
			   m.reset();
		   }
		   HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context,"Hefz");
       	mDbHelper.createDatabase();       
       	mDbHelper.open(); 
       	String command = "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from parhizkar where _id= "+ id;;
       	Cursor testdata = mDbHelper.getData(command); 
       	String Start_audio = Utility.GetColumnValue(testdata, "Start_audio");
       	String End_audio = Utility.GetColumnValue(testdata, "End_audio");
       	String[] start = Start_audio.split(":");
       	int min = Integer.parseInt(start[0]);
       	int sec = Integer.parseInt(start[1]);
       	int mil = Integer.parseInt(start[2]);
       	
       	String[] end = End_audio.split(":");
      	int minE = Integer.parseInt(end[0]);
       	int secE = Integer.parseInt(end[1]);
       	int milE = Integer.parseInt(end[2]);
       	
       	int startA = (((((min*60)+sec)*1000)+mil)*12);
       	Log.d("start", "" +startA);
       	int endA =  (((((minE*60)+secE)*1000)+milE)*12);
    	Log.d("end", "" +endA);
		        try {
		            if (m.isPlaying()) {
		                m.stop();
		                m.release();
		                m = new MediaPlayer();
		            }
		            
		            File sdcard = Environment.getExternalStorageDirectory();
		            File file = new File(sdcard,path);
		            if(file.exists())
		            {
		            	FileDescriptor descriptor =  new FileInputStream(file).getFD();

		            	m.setDataSource(descriptor, startA, endA - startA);
		            	m.prepare();
		            	m.setVolume(1f, 1f); 
		            	m.setLooping(false);
		            	m.start();
		            }
		            else
		            {
		            	Toast.makeText(context, "فایل صوتی پیدا نشد 2", Toast.LENGTH_SHORT).show();
		            }
		            
		            
		        } catch (Exception e) {
		        }
		    }
	   
	   public boolean isPlaying()
	   {
		   if(m.isPlaying())
		   {
			   return true;
		   }
		   else return false;
	   }
	   
	   public void stop() 
	   {
		   m.stop();
		   m.release();
	   }
	   public void pause()
	   {
		   m.pause();
	   }
	   
	   public void restart()
	   {
		   m.start();
	   }
}

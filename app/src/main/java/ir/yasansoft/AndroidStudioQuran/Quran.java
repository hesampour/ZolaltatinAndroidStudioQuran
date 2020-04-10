package ir.yasansoft.AndroidStudioQuran;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class Quran implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3916055232404262796L;
	int CurrentPlayinID = 1;
	int CurrentPage=1;
	int currentSura=1;
	int currentVerse=1;
	int tekrar = 1;
	int currentTekrar = 1;
	String ghari = "Parhizkar";
	boolean isPaused = false;
	boolean isStopped = false;
	Boolean isPlaying = false;
	int prev_Start_Text= 0;
	int prev_End_text = 0;
	//MultiAutoCompleteTextView  pageText ;
	Handler TimerHandler;
	Context context;
	public int FontSize = 17;
	//Sound sound = new Sound();
	MediaPlayer mediaplayer = new MediaPlayer();

	public Quran(Context context,MultiAutoCompleteTextView pageText)
	{
		this.context = context;
		//this.pageText = pageText;
	}
	@SuppressLint({ "SdCardPath" })
	   public pageInformation Loadpage(int pageNum,MultiAutoCompleteTextView  pageText )
	    {
		  Log.d("Quran" , "" + pageNum  );
		  pageInformation info = new pageInformation();
		  info.pageNum = pageNum;
	    	CurrentPage = pageNum;
			HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context , "QuranDB");
	    	mDbHelper.createDatabase();       
	    	mDbHelper.open(); 
	    	String command = "select isSpecial,AyahText from Arabic2 where pageNum=" + pageNum;
	    	Cursor cur = mDbHelper.getData(command);
	    	mDbHelper.close();
			HefzDatabase.Adapter mdbHefz = new HefzDatabase.Adapter(context,"Hefz");
	    	mdbHefz.createDatabase();       
	    	mdbHefz.open();
	    	command =  "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where pageNum= " + pageNum;;
	    	Cursor curHefz = mdbHefz.getData(command); 
	    	mdbHefz.close();
	    	try
	    	{
	    		currentSura =Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("Sura")));
	    		currentVerse =Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("VerseID")));
	    		info.suraNum = currentSura;
	    		info.verseNum = currentVerse;
	    	}
	    	catch(Exception ex)
	    	{
	    		info.suraNum = 0;
	    		info.verseNum = 0;
	    	}
	    	String text = "";
	    	int count = cur.getCount();
	    	cur.moveToFirst();
	    	LinkedList<Integer> isSpecialStart = new LinkedList<Integer>();
	    	LinkedList<Integer> isSpecialEnd = new LinkedList<Integer>();
	    	while(count>0)
	    	{
	    		int start = text.length();
	    		text += cur.getString(cur.getColumnIndex("AyahText"));
	    		int end = text.length();
	    		text += "\n";
	    		if(Integer.parseInt(cur.getString(cur.getColumnIndex("isSpecial"))) == 1)
	    		{
	    			isSpecialStart.addLast(start);
	    			isSpecialEnd.addLast(end);
	    		}
	    		cur.moveToNext();
	    		count--;
	    	}
	    	
	    	Iterator<Integer> itStart = isSpecialStart.iterator();
	    	Iterator<Integer> itEnd = isSpecialEnd.iterator();
	    	pageText.setText(text);
	    	String pageFont;
	    	int pageint = pageNum;
	    	if(pageint<10)
	    	{
	    		pageFont = "Zolaltarin/Font/QCF_P00" + pageNum + ".TTF";
	    	}
	    	else if(pageint<100)
	    	{
	    		pageFont = "Zolaltarin/Font/QCF_P0" + pageNum + ".TTF";
	    	}
	    	else
	    	{
	    		pageFont = "Zolaltarin/Font/QCF_P" + pageNum + ".TTF";
	    	}
			//String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath() +  "/";
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File("/mnt/sdcard", pageFont);
			Typeface face = Typeface.createFromFile(file);
	    	//Typeface face=Typeface.createFromAsset(getAssets(), pageFont); 
	    	pageText.setTypeface(face);
	    
	    	pageText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	    	pageText.setTextColor(Color.BLACK);
	    	
	    	Spannable s = (Spannable)pageText.getText();
	    	while(itStart.hasNext())
	    	{
	    		//File sdcard = Environment.getExternalStorageDirectory();

	    		File file2 = new File(sdcard,"Zolaltarin/Font/QCF_BSML.TTF");
	    		Typeface face2 = Typeface.createFromFile(file2);

	        	//Typeface face2 = Typeface.createFromAsset(sdcard, "Fonts/QCF_BSML.TTF");  
	        	int start = itStart.next();
	        	int end = itEnd.next();
	        	s.setSpan(new CustomTypefaceSpan("", face2), start, end,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        	s.setSpan(new ForegroundColorSpan(0xFFFF0000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    		
	    	}
	    	// pageText.setText(pageText);
	    	mDbHelper.close();
	    	pageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSize);
	    	return info;
	    }

	public pageInformation LoadpageLandscape(int pageNum,MultiAutoCompleteTextView oddPage,MultiAutoCompleteTextView evenPage){
		int oddPagenumber;
		int evenPagenumber;
		if(pageNum%2 == 0){
			oddPagenumber = pageNum - 1;
			evenPagenumber = pageNum;
		} else{
			oddPagenumber = pageNum;
			evenPagenumber = pageNum+1;
		}
		CurrentPage = oddPagenumber;
		pageInformation info = this.Loadpage(oddPagenumber,oddPage);
		this.Loadpage(evenPagenumber,evenPage);
		return info;
	}
	  
	  
	  public int findPageNumFromSuraAndVerse(int suraID , int verseID)
	  {

		  HefzDatabase.Adapter mdbHefz = new HefzDatabase.Adapter(context,"Hefz");
	    	mdbHefz.createDatabase();       
	    	mdbHefz.open();
	    	String command =  "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where Sura= " + suraID + " AND " + "VerseID= " + verseID;
	    	Cursor curHefz = mdbHefz.getData(command); 
	    	mdbHefz.close();
	    	curHefz.moveToFirst();
	    	try
	    	{
	    		return Integer.parseInt(curHefz.getString(curHefz.getColumnIndex("pageNum")));
	    	}
	    	catch (Exception e) {
				// TODO: handle exception
		    	return 1;
			}
	  }
	  
	  
	  private void playID(int id,MultiAutoCompleteTextView pageText)
	  {
		  try {
		  isPlaying = true;
		    CurrentPlayinID = id;
		  HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context, "Hefz");
	       	mDbHelper.createDatabase();       
	       	mDbHelper.open(); 
	       	String command =  "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from parhizkar where _id= "+ id;;
	       	Cursor cur = mDbHelper.getData(command); 
	       	mDbHelper.close();
	       	int pageNum =Integer.parseInt( cur.getString(cur.getColumnIndex("pageNum")));
	       	if(pageNum != CurrentPage )
	       	{
	       		CurrentPage = pageNum;
	       		Loadpage(pageNum,pageText);

	       	}
	    	int start_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("Start_Text")));
	    	int End_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("End_Text")));
	    	prev_Start_Text = start_Text;
	    	prev_End_text = End_Text;
	    	Spannable s = (Spannable)pageText.getText();

	    	s.setSpan(new ForegroundColorSpan(0xFFFF0000), start_Text, End_Text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	//sound.playID(id, context,path);
		  String Start_audio = Utility.GetColumnValue(cur, "Start_audio");
		  String End_audio = Utility.GetColumnValue(cur, "End_audio");
		  String juz = cur.getString(cur.getColumnIndex("juz"));
		  String[] start = Start_audio.split(":");
		  int min = Integer.parseInt(start[0]);
		  int sec = Integer.parseInt(start[1]);
		  int mil = Integer.parseInt(start[2]);

		  String[] end = End_audio.split(":");
		  int minE = Integer.parseInt(end[0]);
		  int secE = Integer.parseInt(end[1]);
		  int milE = Integer.parseInt(end[2]);

		  int startA = (((((min*60)+sec)*1000)+mil)*12);
		  int endA =  (((((minE*60)+secE)*1000)+milE)*12);
//		  mediaplayer =  new MediaPlayer();
//
//			  if (mediaplayer.isPlaying()) {
//				  mediaplayer.stop();
//				  mediaplayer.release();
//				  mediaplayer = new MediaPlayer();
//			  }
			  File sdcard = Environment.getExternalStorageDirectory();
			  String path = "Zolaltarin/Sounds/Parhizkar/" + juz + ".mp3";
			  File file = new File(sdcard,path);
			  if(file.exists())
			  {
				  FileDescriptor descriptor =  new FileInputStream(file).getFD();
				  mediaplayer.setDataSource(descriptor, startA, endA - startA);
				  mediaplayer.prepare();
				//  mediaplayer.setVolume(1f, 1f);
				//  mediaplayer.setLooping(false);
				  mediaplayer.start();
			  }
			  else
			  {
				  Toast.makeText(context, "فایل صوتی پیدا نشد 2", Toast.LENGTH_SHORT).show();
			  }
		  } catch (Exception e) {
			  Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		  }
	  }

	  public void play(MultiAutoCompleteTextView pageText)
	  {
		  if(isPaused)
		  {
			  isPlaying = true;
			  isPaused = false;
			  mediaplayer.start();
			  setTimer(pageText);
		  }

		  else
		  {
		    if(isStopped)
			  {
				  resetCurrentColor(pageText);
				  isStopped = false;
			  }
			HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context,"Hefz");
		  	mDbHelper.createDatabase();       
	      	mDbHelper.open();
	      	String command = "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where _id="  + CurrentPlayinID;
	      	Cursor cur = mDbHelper.getData(command); 
	      	mDbHelper.close();
	      	CurrentPlayinID = Integer.parseInt( cur.getString(cur.getColumnIndex("_id")));
	      	mediaplayer.reset();
	      	playID(CurrentPlayinID,pageText);
	      	setTimer(pageText);
		  }
	  }
	  
	  public void setTimer(final MultiAutoCompleteTextView pageText)
	  {
		  TimerHandler = new Handler();
		  TimerHandler.postDelayed(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                if(!mediaplayer.isPlaying())
	                {
						mediaplayer.reset();
						Spannable s = (Spannable)pageText.getText();
						s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						//s.setSpan(new ForegroundColorSpan(Color.BLACK), prev_Start_Text, prev_End_text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

						//resetCurrentColor(pageText);
						if(currentTekrar < tekrar)
						{
							currentTekrar +=1;
						}
						else
						{
							currentTekrar = 1;
							CurrentPlayinID+=1;
						}
						playID(CurrentPlayinID,pageText);
	                }

	                TimerHandler.postDelayed(this, 1000);
	            }
	        }, 1000);
	  }
	  
	  public void stopTimer()
	  {
		  TimerHandler.removeCallbacksAndMessages(null);
		//  TimerHandler = new Handler();
	  }
	  
	  public void stop()
	  {
		  if(mediaplayer.isPlaying())
		  {
		  stopTimer();
		  mediaplayer.stop();
		  isStopped = true;
		  isPlaying = false;
		  }
	  }
	  
	  public void pause()
	  {
		  if(mediaplayer.isPlaying())
		  {
			  isPaused = true;
			  stopTimer();
			  mediaplayer.pause();
			  isPlaying = false;
		  }
	  }
	  public boolean isPlaying()
	  {
		  return isPlaying;
	  }
	  
	  
	  public void resetCurrentColor(MultiAutoCompleteTextView pageText)
	  {
		  HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context,"Hefz");
	       	mDbHelper.createDatabase();       
	       	mDbHelper.open(); 
	       	String command = "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from parhizkar where _id= "+ CurrentPlayinID;
	       	Cursor cur = mDbHelper.getData(command); 
	       	mDbHelper.close();
	    	int Start_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("Start_Text")));
	    	int End_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("End_Text")));
	    	if(End_Text - Start_Text > 0)
	    	{
	    		Spannable s = (Spannable)pageText.getText();
				s.setSpan(new ForegroundColorSpan(Color.BLACK), Start_Text, End_Text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	}
	  }
	  
	  
	  public void setPosition(int position,MultiAutoCompleteTextView pageText)
	  {

		  HefzDatabase.Adapter mdbHefz = new HefzDatabase.Adapter(context,"Hefz");
	    	mdbHefz.createDatabase();       
	    	mdbHefz.open();
	    	String command =  "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where pageNum= " + CurrentPage;;
	    	Cursor curHefz = mdbHefz.getData(command); 
	    	mdbHefz.close();
	    	int count = curHefz.getCount();
	    	curHefz.moveToFirst();
	    	int Start;
	    	int End;
	    	Boolean flag = false;
	    	while(count>0  && flag ==false)
	    	{
	    		Start =Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("Start_Text")));
	    		End = Integer.parseInt(curHefz.getString(curHefz.getColumnIndex("End_Text")));
	    		if( position > Start && position<End )
	    		{
	    			resetCurrentColor(pageText);
	    			CurrentPlayinID = Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("_id")));;
	    			currentSura = Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("Sura")));
	    			currentVerse = Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("VerseID")));
	    			Spannable s = (Spannable)pageText.getText();
	    			s.setSpan(new ForegroundColorSpan(Color.RED), Start, End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			flag = true;
	    		}
	    		curHefz.moveToNext();
	    		count--;
	    	}
	  }
	  
	  
	  public int findVerseNumFromSura(int SuraID)
	  {

		  HefzDatabase.Adapter mdb = new HefzDatabase.Adapter(context,"QuranDB");
	    	mdb.createDatabase();       
	    	mdb.open();
	    	String command = "select _id from page where SuraID=" + SuraID;
	    	Cursor cur = mdb.getData(command); 
	    	int count = cur.getCount();
	    	mdb.close();
	    	return count;
		  
	  }
}

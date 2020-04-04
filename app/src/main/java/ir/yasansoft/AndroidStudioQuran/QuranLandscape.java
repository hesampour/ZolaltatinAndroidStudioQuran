package ir.yasansoft.AndroidStudioQuran;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class QuranLandscape {

    /**
     *
     */
    private static final long serialVersionUID = 3916055232404262796L;
    int CurrentPlayinID = 1;
    static int CurrentPage=1;
    int currentSura=1;
    int currentVerse=1;
    int tekrar = 1;
    int currentTekrar = 1;
    String ghari = "Parhizkar";
    boolean isPaused = false;
    boolean isStopped = false;
    Boolean isPlaying = false;
    MultiAutoCompleteTextView pageText1;
    MultiAutoCompleteTextView pageText2;
    //MultiAutoCompleteTextView  pageText ;
    Handler TimerHandler;
    Context context;
    public int FontSize = 17;
    Sound sound = new Sound();
    public QuranLandscape(Context context, MultiAutoCompleteTextView pageText1, MultiAutoCompleteTextView pageText2)
    {
        this.context = context;
        this.pageText1 = pageText1;
        this.pageText2 = pageText2;
    }
    @SuppressLint({ "SdCardPath" })
    public pageInformation Loadpage(int pageNum,MultiAutoCompleteTextView  pageText )
    {
        Log.d("Quran" , "" + pageNum  );
        pageInformation info = new pageInformation();
        info.pageNum = pageNum;
        this.CurrentPage = pageNum;
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
            this.currentSura =Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("Sura")));
            this.currentVerse =Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("VerseID")));
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

    public pageInformation LoadpageLandscape(int pageNum){
        int oddPagenumber;
        int evenPagenumber;
        if(pageNum%2 == 0){
            oddPagenumber = pageNum - 1;
            evenPagenumber = pageNum;
        } else{
            oddPagenumber = pageNum;
            evenPagenumber = pageNum+1;
        }
        this.CurrentPage = oddPagenumber;
        Log.d("CurrentPage" ,Integer.toString(CurrentPage));

        pageInformation info = this.Loadpage(oddPagenumber,pageText1);
        this.Loadpage(evenPagenumber,pageText2);
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


    private void playID(int id,String path)
    {
        isPlaying = true;
        this.CurrentPlayinID = id;
        HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context, "Hefz");
        mDbHelper.createDatabase();
        mDbHelper.open();
        String command =  "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from parhizkar where _id= "+ id;;
        Cursor cur = mDbHelper.getData(command);
        mDbHelper.close();
        int pageNum =Integer.parseInt( cur.getString(cur.getColumnIndex("pageNum")));
        if(pageNum != CurrentPage && pageNum%2!=0 )
        {
            this.CurrentPage = pageNum;
            LoadpageLandscape(pageNum);
        } else if(pageNum%2 == 0){
            this.CurrentPage = pageNum;
        }
        int start_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("Start_Text")));
        int End_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("End_Text")));
        if(this.CurrentPage%2 == 0) {
            Spannable s = (Spannable) pageText2.getText();
            s.setSpan(new ForegroundColorSpan(0xFFFF0000), start_Text, End_Text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else{
            Spannable s = (Spannable) pageText1.getText();
            s.setSpan(new ForegroundColorSpan(0xFFFF0000), start_Text, End_Text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sound.playID(id, context,path);

    }

    public void playNext(MultiAutoCompleteTextView pageText)
    {
        if(this.currentTekrar < tekrar)
        {
            this.currentTekrar +=1;
        }
        else
        {
            this.currentTekrar = 1;
            this.CurrentPlayinID+=1;
        }
        HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context,"Hefz");
        mDbHelper.createDatabase();
        mDbHelper.open();
        String command = "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where _id= " + CurrentPlayinID ;
        Cursor cur = mDbHelper.getData(command);
        mDbHelper.close();
        String juz = cur.getString(cur.getColumnIndex("juz"));
        File sdcard = Environment.getExternalStorageDirectory();
        String path = "Zolaltarin/Sounds/Parhizkar/" + juz + ".mp3";
        File file = new File(sdcard,path);
        if(file.exists())
        {
            playID(this.CurrentPlayinID,path);
        }
        else
        {
            Toast.makeText(context, "فایل صوتی پیدا نشد", Toast.LENGTH_LONG).show();
        }
    }

    public void play(MultiAutoCompleteTextView pageText)
    {
        if(isPaused)
        {
            isPlaying = true;
            isPaused = false;
            sound.restart();
            setTimer(pageText);
        }

        else
        {
            if(isStopped)
            {
                resetCurrentColor();
                isStopped = false;
            }
            HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context,"Hefz");
            mDbHelper.createDatabase();
            mDbHelper.open();
            String command = "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where _id="  + CurrentPlayinID;
            Cursor cur = mDbHelper.getData(command);
            mDbHelper.close();
            this.CurrentPlayinID = Integer.parseInt( cur.getString(cur.getColumnIndex("_id")));
            String juz = cur.getString(cur.getColumnIndex("juz"));
            File sdcard = Environment.getExternalStorageDirectory();
            String path = "Zolaltarin/Sounds/Parhizkar/" + juz + ".mp3";
            File file = new File(sdcard,path);
            if(file.exists())
            {
                playID(CurrentPlayinID,path);
                setTimer(pageText);
            }
            else
            {
                Toast.makeText(context, path, Toast.LENGTH_LONG).show();

            }
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
                if(!sound.isPlaying())
                {
                    resetCurrentColor();
                    playNext(pageText);
                }
                TimerHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    public void stopTimer()
    {
        TimerHandler.removeCallbacksAndMessages(null);
        //  TimerHandler = new Handler();
    }

    public void stop()
    {
        if(sound.isPlaying())
        {
            stopTimer();
            sound.stop();
            isStopped = true;
            isPlaying = false;
        }
    }

    public void pause()
    {
        if(sound.isPlaying())
        {
            isPaused = true;
            stopTimer();
            sound.pause();
            isPlaying = false;
        }
    }
    public boolean isPlaying()
    {
        return isPlaying;
    }


    public void resetCurrentColor()
    {
        HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(context,"Hefz");
        mDbHelper.createDatabase();
        mDbHelper.open();
        String command = "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from parhizkar where _id= "+ CurrentPlayinID;
        Cursor cur = mDbHelper.getData(command);
        mDbHelper.close();
        int Start_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("Start_Text")));
        int End_Text =Integer.parseInt( cur.getString(cur.getColumnIndex("End_Text")));
        int pageNum =Integer.parseInt( cur.getString(cur.getColumnIndex("pageNum")));
        if(End_Text - Start_Text > 0)
        {
            if(pageNum%2 == 0) {
                Spannable s = (Spannable) pageText2.getText();
                s.setSpan(new ForegroundColorSpan(Color.BLACK), Start_Text, End_Text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                Spannable s = (Spannable) pageText1.getText();
                s.setSpan(new ForegroundColorSpan(Color.BLACK), Start_Text, End_Text, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

    }


    public void setPosition(int position,boolean isOdd)
    {
        Log.d("position" ,Integer.toString(this.CurrentPage));

        if(isOdd && this.CurrentPage%2 == 0){
            this.CurrentPage--;
        } else if(!isOdd && this.CurrentPage%2 != 0){
            this.CurrentPage++;
        }
        Log.d("positionNext" ,Integer.toString(this.CurrentPage));

        HefzDatabase.Adapter mdbHefz = new HefzDatabase.Adapter(context,"Hefz");
        mdbHefz.createDatabase();
        mdbHefz.open();
        String command =  "SELECT _id, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from " + ghari + " where pageNum= " + this.CurrentPage;;
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
                resetCurrentColor();
                this.CurrentPlayinID = Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("_id")));;
                this.currentSura = Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("Sura")));
                this.currentVerse = Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("VerseID")));
                int pageNum =Integer.parseInt( curHefz.getString(curHefz.getColumnIndex("pageNum")));
                if(pageNum%2 == 0) {
                    Spannable s = (Spannable) pageText2.getText();
                    s.setSpan(new ForegroundColorSpan(Color.RED), Start, End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else{
                    Spannable s = (Spannable) pageText1.getText();
                    s.setSpan(new ForegroundColorSpan(Color.RED), Start, End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
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

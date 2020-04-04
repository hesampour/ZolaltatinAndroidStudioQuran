package ir.yasansoft.AndroidStudioQuran;

import java.io.*;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.GestureDetector;
public class MainActivity extends SwipeActivity   {
	MultiAutoCompleteTextView pageText;
	MultiAutoCompleteTextView pageText1;
	MultiAutoCompleteTextView pageText2;

	GestureDetector gestureDetector;

	MediaPlayer m = new MediaPlayer();
	EditText editText_position;
	Spinner spinner_Tekrar;
	Spinner spinner_Sura;
	Spinner spinner_Verse;
	Spinner spinner_FontSize;
	EditText edittext_suraName;
	int id = 9;
	 ListView listView ;
	int currentSuraID = 1;
	int fontSize = 17;
	int pageNum = 1;
	Quran quran = null;
	QuranLandscape quranlandscape = null;
	Button button_play;
	Button button_stop;
	boolean isStart = true;
	@SuppressLint({ "SdCardPath", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		Spinner spinner_PageNum = (Spinner) findViewById(R.id.spinner_pageNum);
		spinner_Tekrar = (Spinner) findViewById(R.id.spinner_Tekrar);
		//spinner_Verse = (Spinner) findViewById(R.id.spinner_Aya);
		//Spinner spinner_Sura = (Spinner) findViewById(R.id.spinner_SuraNum);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		edittext_suraName = (EditText)findViewById(R.id.editText_suraName);

		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// In landscape
			pageText1 = (MultiAutoCompleteTextView) findViewById(R.id.oddPage);
			pageText2 = (MultiAutoCompleteTextView) findViewById(R.id.evenPage);

			pageText2.setCursorVisible(false);
			pageText2.setSelected(false);
			disableSoftInputFromAppearing(pageText2);
			pageText2.setFocusable(true);
			pageText2.setClickable(true);
			pageText2.setLongClickable(true);
			//registerForContextMenu(pageText2);

			pageText1.setCursorVisible(false);
			pageText1.setSelected(false);
			disableSoftInputFromAppearing(pageText1);
			pageText1.setFocusable(true);
			pageText1.setClickable(true);
			pageText1.setLongClickable(true);
			//registerForContextMenu(pageText1);
		} else {
			// In portrait
			pageText = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
			pageText.setCursorVisible(false);
			pageText.setSelected(false);
			disableSoftInputFromAppearing(pageText);
			pageText.setFocusable(true);
			pageText.setClickable(true);
			pageText.setLongClickable(true);
			//registerForContextMenu(pageText);
		}
		//spinner_Sura = (Spinner) findViewById(R.id.spinner_SuraNum);
		//spinner_Verse = (Spinner) findViewById(R.id.spinner_Aya);
		//pageText.setEditableFactory(null);
		//pageText.setFocusable(true);
		//pageText.setEnabled(true);
		//pageText.setInputType(0);
		//pageText.setFreezesText(true);
		// pageText.setTextIsSelectable(false);

		// pageText.clearFocus();
		// InputMethodManager inputManager = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// set Font size

		// SlidingMenu menu = new SlidingMenu(this);
		// menu.setMode(SlidingMenu.RIGHT);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN );
		// menu.setFadeDegree(0.35f);
		// menu.attachToActivity(this, SlidingMenu.TOUCHMODE_NONE);
		//
		// menu.setMenu(R.layout.menu_frame);
		try {
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, "Zolaltarin/quran.bin");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			saveInfo save = (saveInfo) ois.readObject();
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				quranlandscape = new QuranLandscape(getBaseContext(),pageText1,pageText2);
				quranlandscape.CurrentPage = save.CurrentPage;
				quranlandscape.CurrentPlayinID = save.CurrentPlayinID;
				quranlandscape.currentSura = save.currentSura;
				quranlandscape.currentTekrar = save.currentTekrar;
				quranlandscape.currentVerse = save.currentVerse;
				quranlandscape.isPaused = false;
				quranlandscape.isPlaying = false;
				quranlandscape.isStopped = false;
			} else {
				quran = new Quran(getBaseContext(), pageText);
				quran.CurrentPage = save.CurrentPage;
				quran.CurrentPlayinID = save.CurrentPlayinID;
				quran.currentSura = save.currentSura;
				quran.currentTekrar = save.currentTekrar;
				quran.currentVerse = save.currentVerse;
				quran.isPaused = false;
				quran.isPlaying = false;
				quran.isStopped = false;
			}




			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			if(width == 768 && height == 1024 )
			{
				quran.FontSize = 40;

			}
			if(width == 1024 && height == 768 )
			{
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					quranlandscape.FontSize = 27;
				}else{
					quran.FontSize = 24;
				}
            }
			if(width == 1196 && height == 720 )
			{
				quran.FontSize = 76;
			}
			else if(width==720 && height == 1184)
			{
				quran.FontSize = 44;
			}
			else if(width==600 && height == 976)
			{
				quran.FontSize = 39;
			}
			Toast.makeText(getBaseContext(), " " + width + " , height =" + height,
					Toast.LENGTH_LONG).show();


			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// In landscape
				quran.LoadpageLandscape(save.CurrentPage,this.pageText1,this.pageText2);
			} else {
				// In portrait
				quran.Loadpage(save.CurrentPage,this.pageText);
			}


//			if (save.isPlaying) {
//				try {
//					quran.play(pageText);
//					quran.isPlaying = true;
//				} catch (Exception e) {
//					quran.isPlaying = false;
//				}
//			}
		} catch (Exception ex) {
			quran = new Quran(getBaseContext(), pageText);
          //  quranlandscape = new QuranLandscape(getBaseContext(),pageText1,pageText2);
           // quranlandscape.isPlaying = false;
			quran.isPlaying = false;
			quran = new Quran(getBaseContext(), pageText);
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			if(width == 768 && height == 1024 )
			{
				quran.FontSize = 40;
			}
			if(width == 1024 && height == 768 )
			{
				quran.FontSize = 24;
				//quranlandscape.FontSize = 27;
			}
			if(width == 1196 && height == 720 )
			{
				quran.FontSize = 76;
			}
			else if(width==720 && height == 1184)
			{
				quran.FontSize = 44;
			}
			else if(width==600 && height == 976)
			{
				quran.FontSize = 39;
			}
		}

		// end set Fontsize
		// pageText.setInputType(InputType.TYPE_NULL); // disable soft input
		// page spinner
		String[] array_spinner = new String[604];
		for (int i = 0; i < 604; i++) {
			array_spinner[i] = "" + (i + 1);
		}

		ArrayAdapter<?> aa = new ArrayAdapter<Object>(this,
				android.R.layout.simple_spinner_item, array_spinner);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_PageNum.setAdapter(aa);
		spinner_PageNum.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				pageInformation info;
				int orientation = getResources().getConfiguration().orientation;
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					// In landscape
					info = quranlandscape.LoadpageLandscape(position + 1);
				} else {
					// In portrait
					info = quran.Loadpage(position + 1,pageText);
				}
				if(edittext_suraName != null) {
					edittext_suraName.setText(info.getSuraName());
				}
				pageNum = position;
				if (quran.isPaused) {
					quran.stop();
					quran.isPaused = false;
					quran.isPlaying = false;
				}
				if (info.suraNum != 0) {
					// spinner_Sura.setSelection(info.suraNum - 1 );
					// spinner_Verse.setSelection(info.verseNum - 1 );
				}
				if (quran.isPlaying()) {
					HefzDatabase.Adapter mDbHelper = new HefzDatabase.Adapter(
							getBaseContext(), "Hefz");
					mDbHelper.createDatabase();
					mDbHelper.open();
					String command = "SELECT ID, juz, pageNum, Sura, VerseID, Start_audio, End_audio, Start_Text, End_Text from parhizkar where _id= "
							+ quran.CurrentPlayinID;
					Cursor cur = mDbHelper.getData(command);
					mDbHelper.close();
					if (quran.CurrentPage == Integer.parseInt(cur.getString(cur
							.getColumnIndex("pageNum")))) {
						int start_Text = Integer.parseInt(cur.getString(cur
								.getColumnIndex("Start_Text")));
						int End_Text = Integer.parseInt(cur.getString(cur
								.getColumnIndex("End_Text")));
						Spannable s = pageText.getText();
						s.setSpan(new ForegroundColorSpan(0xFFFF0000),
								start_Text, End_Text,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}




		});



		/*
		 * pageText.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub boolean mHasPerformedLongPress = false;
		 * Runnable mPendingCheckForLongPress = null; [' switch
		 * (event.getAction()) { case MotionEvent.ACTION_UP:
		 * 
		 * if (!mHasPerformedLongPress) { // This is a tap, so remove the
		 * longpress check if (mPendingCheckForLongPress != null) {
		 * v.removeCallbacks(mPendingCheckForLongPress); } // v.performClick();
		 * }
		 * 
		 * break; case MotionEvent.ACTION_DOWN: if( mPendingCheckForLongPress ==
		 * null) { mPendingCheckForLongPress = new Runnable() { public void
		 * run() { //do your job } }; }
		 * 
		 * 
		 * mHasPerformedLongPress = false;
		 * v.postDelayed(mPendingCheckForLongPress,
		 * ViewConfiguration.getLongPressTimeout());
		 * 
		 * break; case MotionEvent.ACTION_MOVE: final int x = (int)
		 * event.getX(); final int y = (int) event.getY();
		 * 
		 * // Be lenient about moving outside of buttons int slop =
		 * ViewConfiguration.get(v.getContext()).getScaledTouchSlop(); if ((x <
		 * 0 - slop) || (x >= v.getWidth() + slop) || (y < 0 - slop) || (y >=
		 * v.getHeight() + slop)) {
		 * 
		 * if (mPendingCheckForLongPress != null) { v.
		 * removeCallbacks(mPendingCheckForLongPress); } } break; default:
		 * return false; } return false; } });
		 */
		// end page spinner
		// pageText.setOnTouchListener(new OnTouchListener() {

		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// int inType = pageText.getInputType(); // backup the input type
		// pageText.onTouchEvent(event); // call native handler
		// pageText.setInputType(inType); // restore input type
		// / return true; // consume touch even
		// }
		// });

		if(pageText != null) {
			pageText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (!quran.isPlaying() && !quran.isPaused) {
							quran.setPosition(pageText.getSelectionStart(), pageText);
						}
					} catch (Exception e) {
					}
				}
			});
			pageText.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					final PopupMenu popup = new PopupMenu(getBaseContext(),findViewById(R.id.spinner_pageNum));
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
								case R.id.play:
									try {
										quran.play(pageText);
										Log.d("quran", "playing");
									} catch (Exception e) {
									}
									break;
								case R.id.pause:
									try {
										quran.pause();
									} catch (Exception e) {
									}
									break;
								case R.id.stop:
									try {
										quran.stop();
									} catch (Exception e) {
									}
							}
							return true;
						}
					});
//					MenuInflater inflater = popup.getMenuInflater();
//					inflater.inflate(R.menu.popupmenu, popup.getMenu());
//					popup.show();
					popup.inflate(R.menu.menu);
					popup.show();
					return true;
				}
			});

			pageText.setCustomSelectionActionModeCallback(new Callback() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					int temp = (pageText.getSelectionStart() + pageText
							.getSelectionEnd()) / 2;
					pageText.setSelection(temp, temp);
					if (!quran.isPlaying() && !quran.isPaused) {
						quran.setPosition(pageText.getSelectionStart(), pageText);
					}
					//openOptionsMenu();

					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}

			});
		}

		if(pageText1 != null) {
			pageText1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (!quranlandscape.isPlaying() && !quranlandscape.isPaused) {
							quranlandscape.setPosition(pageText1.getSelectionStart(),true);
						}
					} catch (Exception e) {
					}
				}
			});
			pageText1.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					final PopupMenu popup = new PopupMenu(getBaseContext(),findViewById(R.id.spinner_pageNum));
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
								case R.id.play:
									try {
										quranlandscape.play(pageText1);
										Log.d("quran", "playing");
									} catch (Exception e) {
									}
									break;
								case R.id.pause:
									try {
										quranlandscape.pause();
									} catch (Exception e) {
									}
									break;
								case R.id.stop:
									try {
										quranlandscape.stop();
									} catch (Exception e) {
									}
							}
							return true;
						}
					});
					popup.inflate(R.menu.popupmenu);
					popup.show();
					return true;
				}
			});

			pageText1.setCustomSelectionActionModeCallback(new Callback() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					int temp = (pageText1.getSelectionStart() + pageText1
							.getSelectionEnd()) / 2;
					pageText1.setSelection(temp, temp);
					if (!quranlandscape.isPlaying() && !quranlandscape.isPaused) {
						quranlandscape.setPosition(pageText1.getSelectionStart(),true);
					}
					//openOptionsMenu();

					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}

			});
		}

		if(pageText2 != null) {
			pageText2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (!quranlandscape.isPlaying() && !quranlandscape.isPaused) {
							quranlandscape.setPosition(pageText2.getSelectionStart(),false);
						}
					} catch (Exception e) {
					}
				}
			});
			pageText2.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					//openOptionsMenu();
					//openContextMenu(v);

					final PopupMenu popup = new PopupMenu(getBaseContext(),findViewById(R.id.spinner_pageNum));
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
								case R.id.play:
									try {
										quranlandscape.play(pageText2);
										Log.d("quran", "playing");
									} catch (Exception e) {
									}
									break;
								case R.id.pause:
									try {
										quranlandscape.pause();
									} catch (Exception e) {
									}
									break;
								case R.id.stop:
									try {
										quranlandscape.stop();
									} catch (Exception e) {
									}
							}
							return true;
						}
					});
					popup.inflate(R.menu.popupmenu);
					popup.show();
					return true;
				}
			});

			pageText2.setCustomSelectionActionModeCallback(new Callback() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					int temp = (pageText2.getSelectionStart() + pageText2
							.getSelectionEnd()) / 2;
					pageText2.setSelection(temp, temp);
					if (!quran.isPlaying() && !quran.isPaused) {
						quran.setPosition(pageText2.getSelectionStart(), pageText2);
					}
					//openOptionsMenu();

					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}

			});
		}
		String[] array = new String[10];
		for (int i = 0; i < 10; i++) {
			array[i] = "" + (i + 1);
		}
		aa = new ArrayAdapter<Object>(this,
				android.R.layout.simple_spinner_item, array);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if(spinner_Tekrar!=null) {
			spinner_Tekrar.setAdapter(aa);
			spinner_Tekrar.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
										   int position, long id) {
					// TODO Auto-generated method stub
					quran.tekrar = position + 1;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}
			});
			spinner_Tekrar.setSelection(quran.currentTekrar - 1);
			// spinner_Verse.setSelection(quran.currentVerse-1);
		}
        spinner_PageNum.setSelection(quranlandscape.CurrentPage - 1);
		//


	

		// quran.Loadpage(quran.CurrentPage);
	}








	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflader = getMenuInflater();
		inflader.inflate(R.menu.menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Log.d("quran", "" + item.getItemId());
		switch (item.getItemId()) {
		case R.id.play:
			try {
				quran.play(pageText);
				Log.d("quran", "playing");
			} catch (Exception e) {
			}
			break;
		case R.id.pause:
			try {
				quran.pause();
			} catch (Exception e) {
			}
			break;
		case R.id.stop:
			try {
				quran.stop();
			} catch (Exception e) {
			}
			/*
			 * case R.id.settings: setContentView(R.layout.settings);
			 * spinner_FontSize = (Spinner)findViewById(R.id.spinner_pageNum);
			 * String[] varray=new String[80]; for(int i= 0; i< 80;i++) {
			 * varray[i] = "" + (i+10); }
			 * ArrayAdapter<?> Fontadapter = new
			 * ArrayAdapter<Object>(getBaseContext
			 * (),android.R.layout.simple_spinner_item, varray);
			 * Fontadapter.setDropDownViewResource
			 * (android.R.layout.simple_spinner_dropdown_item);
			 * spinner_FontSize.setAdapter(Fontadapter);
			 * spinner_FontSize.setOnItemSelectedListener(new
			 * OnItemSelectedListener() {
			 * 
			 * @Override public void onItemSelected(AdapterView<?> arg0, View
			 * arg1, int position, long arg3) { // TODO Auto-generated method
			 * stub fontSize = position + 10;
			 * //pageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, position +
			 * 10); //setContentView(R.layout.activity_main); }
			 * 
			 * @Override public void onNothingSelected(AdapterView<?> arg0) { //
			 * TODO Auto-generated method stub
			 * 
			 * } }); Button back = (Button)findViewById(R.id.button_back);
			 * back.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { // TODO Auto-generated
			 * method stub //setContentView(R.layout.activity_main);
			 * onCreate(null); quran.FontSize = fontSize; } });
			 */
		}

		return super.onContextItemSelected(item);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.play:
			try {
				quran.play(pageText);
				Log.d("quran", "playing");
			} catch (Exception e) {
			}
			break;
		case R.id.pause:
			try {
				quran.pause();
			} catch (Exception e) {
			}
			break;
		case R.id.stop:
			try {
				quran.stop();
			} catch (Exception e) {
			}
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	// @SuppressLint("NewApi")
	// @Override
	/*
	 * public void onConfigurationChanged(Configuration newConfig) {
	 * //super.onConfigurationChanged(newConfig); Display display =
	 * ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
	 * int orientation = display.getRotation(); if (orientation ==
	 * Surface.ROTATION_90 || orientation == Surface.ROTATION_270) { // TODO:
	 * add logic for landscape mode here quran.FontSize = 78;
	 * 
	 * quran.Loadpage(pageNum); } else if(orientation == Surface.ROTATION_0 ||
	 * orientation == Surface.ROTATION_180) { quran.FontSize = 45;
	 * quran.Loadpage(pageNum); } }
	 */
	@Override
	@SuppressLint({ "SdCardPath" })
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			saveInfo save = new saveInfo();
			save.CurrentPage = quran.CurrentPage;
			save.CurrentPlayinID = quran.CurrentPlayinID;
			save.currentSura = quran.currentSura;
			save.currentTekrar = quran.currentTekrar;
			save.currentVerse = quran.currentVerse;
			save.ghari = quran.ghari;
			save.isPaused = quran.isPaused;
			save.isPlaying = quran.isPlaying;
			save.isStopped = quran.isStopped;
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, "Zolaltarin/quran.bin");
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file)); // Select where you wish to
			// save the file...
			oos.writeObject(save); // write the class as an 'object'
			oos.flush(); // flush the stream to insure all of the information
			// was written to 'save.bin'
			oos.close();// close the stream
			try {
				quran.stop();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(e.toString(), "");
		}

		super.onDestroy();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putSerializable("quran", value)
	}

	public void initiateSuraNames(Spinner spin) {

		// Typeface tf = Farsi.GetFarsiFont(context);
		// spin

	}

	@SuppressLint("NewApi")
	public static void disableSoftInputFromAppearing(EditText editText) {
		if (Build.VERSION.SDK_INT >= 11) {
			editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
			editText.setTextIsSelectable(true);
		} else {
			editText.setRawInputType(InputType.TYPE_NULL);
			editText.setFocusable(true);
		}
	}

	@Override
	protected void previous() {
		// TODO Auto-generated method stub
		Spinner spinner_PageNum = (Spinner) findViewById(R.id.spinner_pageNum);

		int position = spinner_PageNum.getSelectedItemPosition();
		if (position < 603) {
			spinner_PageNum.setSelection(position + 1);
		}
		position = spinner_PageNum.getSelectedItemPosition();
		Toast.makeText(getBaseContext(), "صفحه " + (position + 1),
				Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void next() {
		// TODO Auto-generated method stub
		Spinner spinner_PageNum = (Spinner) findViewById(R.id.spinner_pageNum);
		int position = spinner_PageNum.getSelectedItemPosition();
		if (position > 0) {
			spinner_PageNum.setSelection(position - 1);
		}
		position = spinner_PageNum.getSelectedItemPosition();
		Toast.makeText(getBaseContext(), "صفحه " + (position + 1),
				Toast.LENGTH_SHORT).show();
	}



}


class MyDelayedAction implements Runnable {
	private final long delayMs = 1200;

	@Override
	public void run() {
		try {
			Thread.sleep(delayMs); // Sleep for a while
			doBusinessLogic(); // If the thread is still around after the sleep,
			// do the work
		} catch (InterruptedException e) {
			return;
		}
	}

	private void doBusinessLogic() {
		// Make sure this logic is as quick as possible, or delegate it to some
		// other class
		// through Broadcasted Intents, because if the user lets go while the
		// work is happenening,
		// the thread will be interrupted.
	}
}
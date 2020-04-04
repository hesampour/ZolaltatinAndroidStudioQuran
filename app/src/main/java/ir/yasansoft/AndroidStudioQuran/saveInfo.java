package ir.yasansoft.AndroidStudioQuran;

import java.io.*;
public class saveInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 780285471809006636L;
	int CurrentPlayinID = 1;
	int CurrentPage;
	int currentSura;
	int currentVerse;
	int tekrar = 1;
	int currentTekrar = 1;
	String ghari = "Parhizkar";
	boolean isPaused = false;
	boolean isStopped = false;
	Boolean isPlaying = false;
}

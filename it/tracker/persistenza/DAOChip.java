package it.tracker.persistenza;

import it.tracker.Costanti;
import it.tracker.modello.*;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.WhiteNoise;
import com.jsyn.unitgen.RedNoise;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class DAOChip {

	private static final String fileFrequenze = "P:/Java/Text Tracker/Frequenza Note.txt";
	private static final String fileProva = "P:/Java/Text Tracker/Noise Hit.txt";

	private DAOChip(){}

	public static Chip caricaChip() {
		Map<String, Double> mappaFrequenze = caricaMappaFrequenze();
		BufferedReader outStream = null;
		Chip chip = null;
		try {
			outStream = new BufferedReader(new FileReader(fileProva));
			chip = leggiInfo(outStream);
			//System.out.println(chip.getBpm() + " " + chip.getDurata());
			String linea = outStream.readLine();
			//System.out.println(linea);
			inizializzaCanali(linea, chip);
			while ((linea = outStream.readLine()) != null) {
				leggiLinea(linea, chip, mappaFrequenze);	
			}
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
		return chip;
	}

	private static Map<String, Double> caricaMappaFrequenze() {
		Map<String, Double> mappa = new HashMap<String, Double>();
		BufferedReader outStream = null;
		try {
			outStream = new BufferedReader(new FileReader(fileFrequenze));
			String linea = null;
			while ((linea = outStream.readLine()) != null) {
				mappa.put(linea, Double.parseDouble(outStream.readLine()));
			}
		} catch(IOException ioe) {
			System.out.println(ioe);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch(IOException ioe){}
			}
		}
		return mappa;
	}

	private static Chip leggiInfo(BufferedReader outStream) {
		int bpm = 60;
		int durata = 0;
		try {
			bpm = Integer.parseInt(outStream.readLine().split("=")[1].trim());
			durata = Integer.parseInt(outStream.readLine().split("=")[1].trim());
			outStream.readLine();
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
		return new Chip(bpm, durata);
	}

	private static void inizializzaCanali(String linea, Chip chip) {
		String[] tokens = linea.split(":");
		for (int i = 0; i < tokens.length; i++) {
			//System.out.println(tokens[i].trim());
			if (tokens[i].trim().equals("pulse")) {
				chip.aggiungiCanale(new Canale(new PulseOscillator(), Costanti.PULSE, chip.getBpm()));
			} else if (tokens[i].trim().equals("triangle")) {
				chip.aggiungiCanale(new Canale(new TriangleOscillator(), Costanti.TRIANGLE, chip.getBpm()));
			} else if (tokens[i].trim().equals("noise")) {
				chip.aggiungiCanale(new Canale(new RedNoise(), Costanti.PULSE, chip.getBpm()));
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	private static void leggiLinea(String linea, Chip chip, Map<String, Double> mappa) {
		String[] tokens = linea.split(":");
		for (int i = 0; i < tokens.length; i++) {
			leggiCanale(tokens[i].trim(), chip, mappa, i);
		}
	}

	private static void leggiCanale(String linea, Chip chip, Map<String, Double> mappa, int numCanale) {
		String[] tokens = linea.split(" ");
		chip.getCanale(numCanale).aggiungiNota(new Nota(mappa.get(tokens[0]), Double.parseDouble(tokens[1])));
		chip.getCanale(numCanale).aggiungiEffetto(new Effetto(tokens[2], Double.parseDouble(tokens[3])));
	}

}
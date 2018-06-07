package it.tracker.persistenza;

import it.tracker.Costanti;
import it.tracker.modello.*;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.RedNoise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class DAOChip {

	private static final String fileFrequenze = "D:/Dario/Text Tracker/Frequenza Note.txt";
	private static final String fileProva = "D:/Dario/Text Tracker/Rain.txt";

	private static final Logger logger = LoggerFactory.getLogger(DAOChip.class);

	private DAOChip(){}

	public static Chip caricaChip() {
		Map<String, Double> mappaFrequenze = caricaMappaFrequenze();
		BufferedReader inStream = null;
		Chip chip = null;
		try {
			inStream = new BufferedReader(new FileReader(fileProva));
			chip = leggiInfo(inStream);
			logger.debug(chip.getBpm() + " " + chip.getDurata());
			String linea = inStream.readLine();
			logger.debug(linea);
			inizializzaCanali(linea, chip);
			while ((linea = inStream.readLine()) != null) {
				leggiLinea(linea, chip, mappaFrequenze);	
			}
		} catch(IOException ioe) {
			logger.error(ioe.toString());
		}
		return chip;
	}

	private static Map<String, Double> caricaMappaFrequenze() {
		Map<String, Double> mappa = new HashMap<String, Double>();
		BufferedReader inStream = null;
		try {
			inStream = new BufferedReader(new FileReader(fileFrequenze));
			String linea = null;
			while ((linea = inStream.readLine()) != null) {
				mappa.put(linea, Double.parseDouble(inStream.readLine()));
			}
		} catch(IOException ioe) {
			logger.error(ioe.toString());
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch(IOException ioe){}
			}
		}
		return mappa;
	}

	private static Chip leggiInfo(BufferedReader inStream) {
		int bpm = 60;
		int durata = 0;
		try {
			bpm = Integer.parseInt(inStream.readLine().split("=")[1].trim());
			durata = Integer.parseInt(inStream.readLine().split("=")[1].trim());
			inStream.readLine();
		} catch(IOException ioe) {
			logger.error(ioe.toString());
		}
		return new Chip(bpm, durata);
	}

	private static void inizializzaCanali(String linea, Chip chip) {
		String[] tokens = linea.split(":");
		for (int i = 0; i < tokens.length; i++) {
			logger.debug(tokens[i].trim());
			if (tokens[i].trim().equals("pulse")) {
				chip.aggiungiCanale(new Canale(new PulseOscillator(), Costanti.PULSE, chip.getBpm()));
			} else if (tokens[i].trim().equals("triangle")) {
				chip.aggiungiCanale(new Canale(new TriangleOscillator(), Costanti.TRIANGLE, chip.getBpm()));
			} else if (tokens[i].trim().equals("noise")) {
				chip.aggiungiCanale(new Canale(new RedNoise(), Costanti.PULSE, chip.getBpm()));
			} else {
				throw new IllegalArgumentException(); //Da sostituire
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
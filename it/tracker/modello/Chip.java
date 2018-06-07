package it.tracker.modello;

import it.tracker.modello.Canale;
import it.tracker.Costanti;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.LineOut;
import java.util.List;
import java.util.ArrayList;

public class Chip {

	private Synthesizer synthesizer = JSyn.createSynthesizer();
	private List<Canale> canali = new ArrayList<Canale>();
	private LineOut lineOut = new LineOut();
	private int bpm;
	private double tick;
	private int durata;

	public Chip(int bpm, int durata) {
		this.bpm = bpm;
		this.tick = ((double)60 / bpm) / 4;
		this.durata = durata; 
		this.synthesizer.start();
		this.synthesizer.add(this.lineOut);
	}

	public void aggiungiCanale(Canale canale) {
		this.canali.add(canale);
	}

	public void start() {
		for (Canale canale : this.canali) {
			this.synthesizer.add(canale.getOscillator());
			this.synthesizer.add(canale.getAmplitudeEnv());
			this.synthesizer.add(canale.getLineOut());
			canale.connetti(this.lineOut);
		}
	}

	public void play() {
		this.lineOut.start();
		try {
			for (int i = 0; i < this.durata; i++) {
				double time = this.synthesizer.getCurrentTime();
				for (Canale canale : this.canali) {
					canale.playNext();
				}
				this.synthesizer.sleepUntil(time + tick);
			}
		} catch(InterruptedException e) {
			System.out.println(e);
		}
	}

	public void stop() {
		this.synthesizer.stop();
	}

	public Canale getCanale(int i) {
		return this.canali.get(i);
	}

	public int getBpm() {
		return this.bpm;
	}

	public int getDurata() {
		return this.durata;
	}
}
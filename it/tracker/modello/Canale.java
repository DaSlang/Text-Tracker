package it.tracker.modello;

import com.jsyn.unitgen.*;

import it.tracker.modello.Nota;
import it.tracker.Costanti;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.WhiteNoise;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.data.SegmentedEnvelope; 
import java.util.List;
import java.util.ArrayList;

public class Canale {

	private UnitOscillator oscillator;
	private VariableRateMonoReader amplitudeEnv = new VariableRateMonoReader();
	private FilterLowPass lineOut = new FilterLowPass();
	private int tipo;
	private int bpm;
	private double attack = 0;
	private double release;
	private List<Nota> score = new ArrayList<Nota>();
	private List<Effetto> effetti = new ArrayList<Effetto>();

	public Canale(UnitOscillator oscillator, int tipo, int bpm) {
		this.oscillator = oscillator;
		this.tipo = tipo;
		this.oscillator.output.connect(this.lineOut.input);
		this.bpm = bpm;
		this.release = ((double)60 / this.bpm) / 2;
		this.amplitudeEnv.output.connect(this.lineOut.amplitude);
		this.lineOut.amplitude.set(0);
		this.lineOut.frequency.set(22000);
	}

	public void aggiungiNota(Nota nota) {
		this.score.add(nota);
	}

	public void aggiungiEffetto(Effetto effetto) {
		this.effetti.add(effetto);
	}

	public void connetti(LineOut lineOut) {
		this.lineOut.output.connect(0, lineOut.input, 0);
		this.lineOut.output.connect(0, lineOut.input, 1);
	}

	public void playNext() {
		if (this.score.isEmpty()) {
			this.lineOut.amplitude.set(0);
			return;
		}
		Effetto effetto = this.effetti.remove(0);
		this.processaEffetto(effetto);
		Nota nota = this.score.remove(0);
		if (nota.getFrequenza() != Costanti.HOLD) {
			this.oscillator.frequency.set(nota.getFrequenza());
			this.amplitudeEnv.dataQueue.clear();
		}
		double[] data = {this.attack, nota.getAmpiezza(), this.release, 0.0};
		SegmentedEnvelope env = new SegmentedEnvelope(data);
		this.amplitudeEnv.dataQueue.queue(env, 0, 2);
	}

	private void processaEffetto(Effetto effetto) {
		if (effetto.getTipo().equals("--")) {
			return;
		} else if (effetto.getTipo().equals("RT")) {
			if (effetto.getValore() == -1) {
				this.release = ((double)60 / this.bpm) / 2;
			} else {
				this.release = effetto.getValore();
			}
		} else if (effetto.getTipo().equals("AT")) {
			if (effetto.getValore() == -1) {
				this.attack = 0.0;
			} else {
				this.attack = effetto.getValore();
			}
		} else if (effetto.getTipo().equals("WD") && this.tipo == Costanti.PULSE) {
			this.setWidth(effetto.getValore());
		}
	}

	private void setWidth(Double valore) {
		if (valore == 0) {
			((PulseOscillator)this.oscillator).width.set(-0.75);
		} else if (valore == 1) {
			((PulseOscillator)this.oscillator).width.set(-0.50);
		} else {
			((PulseOscillator)this.oscillator).width.set(0);
		}
	}

	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public int tipo() {
		return this.tipo;
	}

	public UnitOscillator getOscillator() {
		return this.oscillator;
	}

	public VariableRateMonoReader getAmplitudeEnv() {
		return this.amplitudeEnv;
	}

	public FilterLowPass getLineOut() {
		return this.lineOut;
	}
}
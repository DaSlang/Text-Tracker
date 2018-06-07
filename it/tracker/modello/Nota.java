package it.tracker.modello;

public class Nota {

	private double frequenza;
	private double ampiezza;

	public Nota(double frequenza, double ampiezza) {
		this.frequenza = frequenza;
		this.ampiezza = ampiezza;
	}

	public double getFrequenza() {
		return this.frequenza;
	}

	public double getAmpiezza() {
		return this.ampiezza;
	}

}
package it.tracker.modello;

public class Effetto {

	private String tipo;
	private double valore;

	public Effetto(String tipo, double valore) {
		this.tipo = tipo;
		this.valore = valore;
	}

	public String getTipo() {
		return this.tipo;
	}

	public double getValore() {
		return this.valore;
	}

}
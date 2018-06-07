package it.tracker.controllo;

import it.tracker.modello.Chip;
import it.tracker.persistenza.DAOChip;

public class Principale {

	public static void main(String[] args) {
		Principale p = new Principale();
		p.esegui();
	}

	public void esegui() {
		Chip chip = DAOChip.caricaChip();
		if (chip == null) {
			System.out.println("Chip null");
			return;
		}
		chip.start();
		chip.play();
		chip.stop();
	}

}
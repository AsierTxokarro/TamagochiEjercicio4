package org.cuatrovientos.Tamagochi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class LanzadorTamagochi {

	private static final int NUM_TAMAGOCHIS = 3;

	public static void main(String[] args) {
		List<Tamagochi> tamagochis = new ArrayList<>();
		List<Thread> hilos = new ArrayList<>();

		for (int i = 0; i < NUM_TAMAGOCHIS; i++) {
			Tamagochi tama = new Tamagochi(i, "tama " + i);
			Thread hilo = new Thread(tama);
			tamagochis.add(tama);
			hilos.add(hilo);
			hilo.start();
		}

		// Hilo auxiliar del cuidador: responde las sumas (a veces se equivoca)
		Thread respondedor = new Thread(() -> {
			Random rnd = new Random();
			try {
				while (true) {
					Thread.sleep(300);
					for (Tamagochi t : tamagochis) {
						if (t.hayPregunta()) {
							int resultado = t.getSumandoA() + t.getSumandoB();
							if (rnd.nextInt(3) == 0) {
								resultado += 1; // fallo a propósito
							}
							t.responder(resultado);
						}
					}
				}
			} catch (InterruptedException e) {
				// termina
			}
		});
		respondedor.setDaemon(true);
		respondedor.start();

		Scanner scan = new Scanner(System.in);
		int opcion = -1;
		while (opcion != 0) {
			System.out.println("\n--- CUIDADOR ---");
			System.out.println("1. Dar manzana");
			System.out.println("2. Jugar");
			System.out.println("3. Limpiar");
			System.out.println("4. Ver estado de todos");
			System.out.println("5. Matar");
			System.out.println("0. Salir");
			opcion = leerNumero(scan);

			if (opcion >= 1 && opcion <= 3 || opcion == 5) {
				System.out.print("Id del tamagochi (0-" + (NUM_TAMAGOCHIS - 1) + "): ");
				int id = leerNumero(scan);
				if (id < 0 || id >= NUM_TAMAGOCHIS) {
					System.out.println("Id no valido");
					continue;
				}
				Tamagochi t = tamagochis.get(id);
				if (!t.estaVivo()) {
					System.out.println("Ese tamagochi ya ha muerto");
					continue;
				}
				switch (opcion) {
				case 1 -> t.darManzana();
				case 2 -> t.jugar();
				case 3 -> t.limpiar();
				case 5 -> t.matar();
				}
			} else if (opcion == 4) {
				for (Tamagochi t : tamagochis) {
					System.out.println(t.getEstado());
				}
			}
		}

		// Al salir: mata a todos y espera a que terminen
		for (Tamagochi t : tamagochis) {
			t.matar();
		}
		for (Thread h : hilos) {
			try {
				h.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Cuidador termina.");
		scan.close();
	}

	private static int leerNumero(Scanner scan) {
		try {
			return Integer.parseInt(scan.nextLine().trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
package org.cuatrovientos.Tamagochi;

import java.util.Random;

public class Tamagochi implements Runnable {
	private int id;
	private String nombre;
	private volatile int sucio = 0;
	private Random rnd = new Random();
	private long timesStampInicio;
	private long timesStampUltimaLimpieza;
	private long msComer;

	private int manzanasPendientes = 0;
	private int partidasPendientes = 0;
	private int limpiezasPendientes = 0;


	private static final long VIDA_MAXIMA_MS = 300000;
	private static final long INTERVALO_SUCIEDAD_MS = 20000;
	private static final long BANO_MS = 5000;
	private static final int SUCIEDAD_AVISO = 5;
	private static final int SUCIEDAD_MAXIMA = 10;

	private boolean avisoDado = false;

	// Juego
	private int sumandoA;
	private int sumandoB;
	private boolean preguntaAbierta = false;
	private int respuestaRecibida;

	// Estado y muerte
	private volatile String estado = "NACIENDO";
	private volatile boolean muertePedida = false;

	public Tamagochi(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
		this.msComer = rnd.nextLong(1000, 5000);
	}

	@Override
	public void run() {
		timesStampInicio = System.currentTimeMillis();
		timesStampUltimaLimpieza = timesStampInicio;
		estado = "OCIOSO";
		logConTimestamp("ha nacido");
		String causa;

		try {
			while (true) {
				Thread.sleep(500);
				calcularSuciedad();

				// Comprobaciones de muerte (solo aquí, cuando está ocioso)
				if (tiempoVivido() >= VIDA_MAXIMA_MS) {
					causa = "de viejo";
					break;
				}
				if (sucio >= SUCIEDAD_MAXIMA) {
					causa = "de suciedad";
					break;
				}
				if (muertePedida) {
					causa = "porque el cuidador lo ha matado";
					break;
				}

				if (sucio >= SUCIEDAD_AVISO && !avisoDado) {
					logConTimestamp("Cuidado, el tamagochi esta muy sucio: " + sucio);
					avisoDado = true;
				}

				if (comerManzana()) {
					estado = "COMIENDO";
					logConTimestamp("Empieza a comer (tardara " + msComer + " ms)");
					Thread.sleep(msComer);
					logConTimestamp("Termina de comer");
					estado = "OCIOSO";
				}
				if (cogerPartida()) {
					estado = "JUGANDO";
					jugarSuma();
					estado = "OCIOSO";
				}
				if (cogerLimpieza()) {
					estado = "LIMPIANDOSE";
					logConTimestamp("Empieza a banarse");
					Thread.sleep(BANO_MS);
					timesStampUltimaLimpieza = System.currentTimeMillis();
					sucio = 0;
					avisoDado = false;
					logConTimestamp("Termina de banarse, suciedad a 0");
					estado = "OCIOSO";
				}
			}
		} catch (InterruptedException e) {
			causa = "interrumpido";
		}

		estado = "MUERTO";
		logConTimestamp("Ha muerto " + causa);
	}

	private long tiempoVivido() {
		return System.currentTimeMillis() - timesStampInicio;
	}

	private void calcularSuciedad() {
		long msSucio = System.currentTimeMillis() - timesStampUltimaLimpieza;
		sucio = (int) Math.min(msSucio / INTERVALO_SUCIEDAD_MS, SUCIEDAD_MAXIMA);
	}

	// ---------- Manzanas ----------
	public synchronized void darManzana() {
		manzanasPendientes += 1;
	}

	private synchronized boolean comerManzana() {
		if (manzanasPendientes > 0) {
			manzanasPendientes -= 1;
			return true;
		}
		return false;
	}

	// ---------- Limpiar ----------
	public synchronized void limpiar() {
		limpiezasPendientes += 1;
	}

	private synchronized boolean cogerLimpieza() {
		if (limpiezasPendientes > 0) {
			limpiezasPendientes -= 1;
			return true;
		}
		return false;
	}

	// ---------- Jugar ----------
	public synchronized void jugar() {
		partidasPendientes += 1;
	}

	private synchronized boolean cogerPartida() {
		if (partidasPendientes > 0) {
			partidasPendientes -= 1;
			return true;
		}
		return false;
	}

	private synchronized boolean preguntarSuma() throws InterruptedException {
		sumandoA = rnd.nextInt(1, 5);
		sumandoB = rnd.nextInt(1, 5);
		preguntaAbierta = true;
		logConTimestamp("Pregunta: " + sumandoA + " + " + sumandoB);
		while (preguntaAbierta) {
			wait();
		}
		return respuestaRecibida == sumandoA + sumandoB;
	}

	public synchronized void responder(int respuesta) {
		respuestaRecibida = respuesta;
		preguntaAbierta = false;
		notify();
	}

	public synchronized boolean hayPregunta() {
		return preguntaAbierta;
	}

	public synchronized int getSumandoA() {
		return sumandoA;
	}

	public synchronized int getSumandoB() {
		return sumandoB;
	}

	private void jugarSuma() throws InterruptedException {
		boolean acierto = false;
		logConTimestamp("Empieza a jugar");
		while (!acierto) {
			acierto = preguntarSuma();
			if (!acierto) {
				logConTimestamp("Respuesta equivocada, sigue jugando");
			}
		}
		logConTimestamp("Ha acertado, termina de jugar");
	}

	// ---------- Matar y estado ----------
	public void matar() {
		muertePedida = true; // se atiende cuando esté ocioso
	}

	public boolean estaVivo() {
		return !estado.equals("MUERTO");
	}

	public String getEstado() {
		long restante = Math.max(0, VIDA_MAXIMA_MS - tiempoVivido()) / 1000;
		return "Tama " + id + " (" + nombre + ") -> " + estado
				+ " | suciedad " + sucio + "/" + SUCIEDAD_MAXIMA
				+ " | vida restante " + restante + "s";
	}

	private void logConTimestamp(String log) {
		System.out.println(id + " Hilo: " + nombre + " tiempo vivido: " + tiempoVivido() + ": " + log);
	}
}
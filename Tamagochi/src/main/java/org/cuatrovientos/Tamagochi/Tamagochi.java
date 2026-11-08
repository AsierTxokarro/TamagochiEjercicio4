package org.cuatrovientos.Tamagochi;

import java.util.Random;

public class Tamagochi implements Runnable{
	private int id;
	private String nombre;
	private int sucio = 0;
	private Random rnd = new Random();
	private long timesStampInicio;
	private long timesStampUltimaLimpieza;
	private static final long VIDA_MAXIMA_MS = 60000;
	private static final long INTERVALO_SUCIEDAD_MS = 2000;
	private static final int SUCIEDAD_AVISO = 5;
	private static final int SUCIEDAD_MAXIMA = 10;
	private boolean avisoDado = false;
	
	public Tamagochi(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		timesStampInicio = System.currentTimeMillis();
		timesStampUltimaLimpieza = System.currentTimeMillis();
		logConTimestamp("ha nacido");
		while (( tiempoVivido() < VIDA_MAXIMA_MS) && sucio < SUCIEDAD_MAXIMA) {
			try {
				Thread.sleep(500);
				calcularSuciedad();
				if(sucio >= SUCIEDAD_AVISO && !avisoDado) {
					
					logConTimestamp("Cuidado el tamagochi esta bastante sucio " + sucio);
					avisoDado = true;
				}
				logConTimestamp("Suciedad: " +sucio);
				
				
			} catch (InterruptedException e) {
				logConTimestamp("Error en participante, para de correr al instante");
			}
		}
		if(sucio >= SUCIEDAD_MAXIMA) {
			logConTimestamp("Ha muerto de sucio");
		}else {
			logConTimestamp("Ha muerto de viejo");
		}
		
		
	}
	private long tiempoVivido() {
	 return	System.currentTimeMillis() - timesStampInicio;
	 
	}
	private void calcularSuciedad() {
		long ultimaLimpieza = System.currentTimeMillis() - timesStampUltimaLimpieza;
		sucio = (int) Math.min(ultimaLimpieza / INTERVALO_SUCIEDAD_MS, SUCIEDAD_MAXIMA);
	}
	
	  private void logConTimestamp(String log){
	    	System.out.println( id +" Hilo: "+nombre+" tiempo vivido: "+tiempoVivido()+": "+log);
	    }

}

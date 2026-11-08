package org.cuatrovientos.Tamagochi;

import java.util.Random;

public class Tamagochi implements Runnable{
	private int id;
	private String nombre;
	private int sucio = 0;
	private Random rnd = new Random();
	private long timesStampInicio;
	private static final long VIDA_MAXIMA_MS = 20000;
	
	public Tamagochi(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		timesStampInicio = System.currentTimeMillis();
		logConTimestamp("ha nacido");
		while (( tiempoVivido() < VIDA_MAXIMA_MS)) {
			try {
				Thread.sleep(500);
				
				logConTimestamp("Suciedad: "+sucio);
				
				
			} catch (InterruptedException e) {
				logConTimestamp("Error en participante, para de correr al instante");
			}
		}
		logConTimestamp("Ha muerto de viejo");
		
	}
	private long tiempoVivido() {
	 return	System.currentTimeMillis() - timesStampInicio;
	 
	}
	
	  private void logConTimestamp(String log){
	    	System.out.println( id +" Hilo: "+nombre+" tiempo vivido: "+tiempoVivido()+": "+log);
	    }

}

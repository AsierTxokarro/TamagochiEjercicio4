package org.cuatrovientos.Tamagochi;

public class LanzadorTamagochi {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

				for (int i = 0; i< 3; i++) {
					
					Tamagochi tama = new Tamagochi(i, "tama "+ i);
					
					
					Thread lanzaTama = new Thread(tama);
					lanzaTama.start();
					
				
				}
	}

}

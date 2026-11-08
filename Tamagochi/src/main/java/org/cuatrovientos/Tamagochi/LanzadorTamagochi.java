package org.cuatrovientos.Tamagochi;

import java.util.ArrayList;
import java.util.List;

public class LanzadorTamagochi {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Tamagochi> tamagochis = new ArrayList<>();
		
		for (int i = 0; i< 3; i++) {
					
			Tamagochi tama = new Tamagochi(i, "tama "+ i);
			tamagochis.add(tama);
					
			Thread lanzaTama = new Thread(tama);
			lanzaTama.start();
					
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Tamagochi t: tamagochis) {
			t.darManzana();
		}
	}

}

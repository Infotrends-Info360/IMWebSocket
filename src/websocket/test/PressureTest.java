package websocket.test;

public class PressureTest extends Thread {
	public static int i = 0;;
	@Override
	public void run() {
		
		while(true){
			i++;
			System.out.println("TEST"+i);
			
		}// end of while 
	}// end of run()
	
	public static void main(String[] args){
		Thread PressureTest = new PressureTest();
		PressureTest.start();
	}

}

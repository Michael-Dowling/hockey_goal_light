package goalLight;

import java.util.Scanner;

public class Driver {

	private static int delay ;

	public static void main(String[]args) throws Exception {
		if (GoalScored.raspPi){	//if running on Raspberry Pi (i.e. not debugging on computer)
			try{
				//use external jar to setup led since wiringPi gives error otherwise
				//when also provisioning gpio pin for led (as is done in GoalScored class)
		    	Runtime.getRuntime().exec("sudo java -jar lcdConfig.jar");
		    	GoalScored.led.blink(500,2000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Schedule s = RetrieveStats.getSchedule();
		delay = 40000; 	//default delay setting
		new Thread(new readConsole()).start();
		
		while(true) {
			try {
				RetrieveStats.gameUpdate(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}
	}

	public static int getDelay(){
		return delay;
	}
	public static void setDelay(int d){
		delay = d;
	}

	//allows user to modify aspects of program during runtime (e.g. change delay)
	public static class readConsole implements Runnable {

		public void run(){
			Scanner input = new Scanner(System.in);

			System.out.println("Enter Delay");
			Driver.setDelay(input.nextInt()*1000);
			input.nextLine();
			System.out.println("Delay set to " + (Driver.getDelay()/1000) + "s");

			while(true){

				String command = input.nextLine();
				
				//Can add more possible inputs as required
				switch(command){
				case "delay" :
					System.out.println("Enter new delay");
					Driver.setDelay(input.nextInt()*1000);
					System.out.println("Delay set to " + (Driver.getDelay()/1000) + "s");
					break;
				default :
					System.out.println("Invalid command");	
				}
				input.nextLine();
			}	



		}
	}
}

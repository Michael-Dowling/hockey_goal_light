package goalLight;

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class GoalScored implements Runnable {
	
	private int ind;
	private Game game;
	private String teamScored;
	public final static int LCD_ROWS = 2;
	public final static int LCD_COLUMNS = 16;
	public final static int LCD_BITS = 4;
	public final static boolean raspPi = false;	//TODO: at some point would be nice to check this programmatically
	public static final GpioLcdDisplay lcd = new GpioLcdDisplay(LCD_ROWS,    // number of row supported by LCD
			LCD_COLUMNS,       // number of columns supported by LCD
			RaspiPin.GPIO_11,  // LCD RS pin
			RaspiPin.GPIO_10,  // LCD strobe pin
			RaspiPin.GPIO_00,  // LCD data bit D4
			RaspiPin.GPIO_01,  // LCD data bit D5
			RaspiPin.GPIO_02,  // LCD data bit D6
			RaspiPin.GPIO_03); // LCD data bit D7
	public static final GpioController gpio = GpioFactory.getInstance();
	// provision gpio pin #24 as an output pin and turn on
	public static final GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07);
	

	public GoalScored(Game g, String team, int index){

		game = g; //beware of privacy leak, probably not ideal to do this
		teamScored = team;
		ind = index;	//play index of goal that was scored
	}

	public void run(){
		try {
			Thread.sleep(Driver.getDelay());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		String description;
		try {
			description = RetrieveStats.getGoalDescription(ind, game);
		} catch (Exception e) {
			e.printStackTrace();
			description = "Error getting players";
		}

		System.out.println(description);

		if(raspPi){
			try{
			notifyGoal(description);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			     
		}
	}
	
	private String getScore(){
		return game.getAwayAbr()+" "+game.getScore("away")+" - "+game.getHomeAbr()+" "+game.getScore("home");
	}

	private void notifyGoal(String scorers) throws InterruptedException{
		lcd.clear();

		led.setShutdownOptions(true, PinState.LOW);	//set shutdown state for led

		led.blink(500,8000);
		Thread.sleep(8000);

		lcd.writeln(0, getScore(),LCDTextAlignment.ALIGN_CENTER);
		scrollText(1,scorers);
		lcd.clear();
	}
	
	private static void scrollText(int row,String s) throws InterruptedException{
		if(s.length()>16){
			lcd.write(row,s.substring(0, 16));
			Thread.sleep(1000);
			for(int i = 1;i<=s.length()-16;i+=2){
				lcd.write(row, s.substring(i,i+16));
				Thread.sleep(500);
			}
			if(s.length()%2==1){
				lcd.write(row,s.substring(s.length()-16, s.length()-1));
			}
			Thread.sleep(3000);
		}
		else{
			lcd.write(row, s);
			Thread.sleep(10000);
		}
	}
}

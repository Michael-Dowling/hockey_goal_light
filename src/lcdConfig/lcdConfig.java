package lcdConfig;

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.RaspiPin;

public class lcdConfig {
	public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;

    public static void main(String args[]){       
        // initialize LCD
        final GpioLcdDisplay lcd = new GpioLcdDisplay(2,    // number of row supported by LCD
                                                16,       // number of columns supported by LCD
                                                RaspiPin.GPIO_11,  // LCD RS pin
                                                RaspiPin.GPIO_10,  // LCD strobe pin
                                                RaspiPin.GPIO_00,  // LCD data bit D4
                                                RaspiPin.GPIO_01,  // LCD data bit D5
                                                RaspiPin.GPIO_02,  // LCD data bit D6
                                                RaspiPin.GPIO_03); // LCD data bit D7
        
        lcd.clear();
        //confirm lcd setup
        lcd.write(0,"lcd configured");
        try{
        	Thread.sleep(5000);
        }catch(Exception e){
        	e.printStackTrace();
        }
        lcd.clear();
        
    }
    
}

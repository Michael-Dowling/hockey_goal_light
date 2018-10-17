package goalLight;

import java.util.ArrayList;
import java.util.Date;

/*Right now this class just holds an ArrayList:
 * in future can be updated to add functionality and store info about all the games
 * that are occurring in a day, which is why this class exists
 */
public class Schedule {
	int numGames;
	ArrayList<Game> arSchedule = new ArrayList<Game>();
	
	public Schedule(){
		numGames = 0;

	}
	public void addGame(Game g){
		numGames++;
		arSchedule.add(g);
	}
	
}

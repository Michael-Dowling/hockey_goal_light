package goalLight;

import java.util.ArrayList;

/*Right now this class just holds an ArrayList, contains methods for getting number of games, etc:
 * in future can be extended to add functionality and store info about all the games
 * that are occurring in a day (i.e. method to return scores of all active games, certain games, etc.)
 */
public class Schedule {
	private int numGames;
	private ArrayList<Game> sched;
	
	public Schedule(){
		numGames = 0;
		sched = new ArrayList<Game>();
	}
	
	public void addGame(Game g){
		numGames++;
		sched.add(g);
	}
	
	public int getNumGames(){
		return numGames;
	}
	
	public Game getGame(int ind){
		return sched.get(ind);
	}
	
}

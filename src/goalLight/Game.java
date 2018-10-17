package goalLight;

public class Game {
	private String homeTeam;	//full home team name (e.g. "Toronto Maple Leafs")
	private String awayTeam;
	private String hAbr;	//home team abbreviation (e.g. "TOR" for Toronto)
	private String aAbr;
	private int homeScore;
	private int awayScore;
	private String status;	//i.e. "Scheduled", "In Progress" etc.
	private String URL;	//URL of game JSON file
	
	public Game(String away, String home,String gameStatus, String url,String awayAbr,String homeAbr) {
		homeTeam = home;
		awayTeam = away;
		status = gameStatus;
		URL = url;
		homeScore = 0;
		awayScore = 0;
		hAbr = homeAbr;
		aAbr = awayAbr;
	}
	
	public Game(String away, String home, String gameStatus, String url, int aScore, int hScore,String awayAbr,String homeAbr){
		homeTeam = home;
		awayTeam = away;
		status = gameStatus;
		URL = url;
		homeScore = hScore;
		awayScore = aScore;
		aAbr = awayAbr;
		hAbr = homeAbr;
	}
	
	public String getHometeam() {
		return homeTeam;
	}
	public String getAwayTeam() {
		return awayTeam;
	}
	
	public String getHomeAbr(){
		return hAbr;
	}
	public String getAwayAbr(){
		return aAbr;
	}

	public void setScore(String team, int score) {
		if(team.equalsIgnoreCase("home")) {
			homeScore = score;
		}
		if(team.equalsIgnoreCase("away")) {
			awayScore = score;
		}
	}
	public int getScore(String team) {
		if(team.equalsIgnoreCase("home")) {
			return homeScore;
		}
		if(team.equalsIgnoreCase("away")) {
			return awayScore;
		}
		else {
			System.out.println("Team must be either home or away");
			return 100;
		}
	}
	public String getURL() {
		return URL;
	}
	public void setStatus(String status) {
		this.status = status; 
	}
	public String getStatus() {
		return status;
	}
	public String winningTeam() {
		if(homeScore > awayScore)
		return homeTeam;
		else if(homeScore < awayScore)
			return awayTeam;
		else return "It's a tie game";
	}
	
}

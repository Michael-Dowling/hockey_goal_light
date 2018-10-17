package goalLight;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL; 

import org.json.JSONArray;
import org.json.JSONObject;


public class RetrieveStats {

	public static JSONObject parseNHL(String url) throws Exception{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("Game_Schedule", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		if (responseCode  == 404){
			System.out.println("Error 404");
			return null;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		JSONObject info = new JSONObject(response.toString());
		return info;
	}


	//Create schedule object of days games
	//gameStatus can be "Scheduled", "In Progress", "In Progress - Critical" or "Final"
	public static Schedule getSchedule(){	
		try{
			JSONObject info = parseNHL("http://statsapi.web.nhl.com/api/v1/schedule");

			JSONArray dates = info.getJSONArray("dates");
			JSONArray games = dates.getJSONObject(0).getJSONArray("games");
			Schedule s = new Schedule();
			for(int i = 0; i < games.length(); i++){
				JSONObject game = games.getJSONObject(i);
				String home = game.getJSONObject("teams").getJSONObject("home").getJSONObject("team").getString("name");
				String away = game.getJSONObject("teams").getJSONObject("away").getJSONObject("team").getString("name");
				String gameStatus = game.getJSONObject("status").getString("detailedState");
				String url = "http://statsapi.web.nhl.com" + game.getString("link");
				JSONObject ga = parseNHL(url);
				String aAbr = ga.getJSONObject("gameData").getJSONObject("teams").getJSONObject("away").getString("abbreviation");
				String hAbr = ga.getJSONObject("gameData").getJSONObject("teams").getJSONObject("home").getString("abbreviation");
				Game g;
				if(!gameStatus.equalsIgnoreCase("Scheduled")){
						int awayScore = game.getJSONObject("teams").getJSONObject("away").getInt("score");
						int homeScore = game.getJSONObject("teams").getJSONObject("home").getInt("score");
						g = new Game(away,home,gameStatus,url,awayScore,homeScore,aAbr,hAbr);
				}
				else{//game hasn't started yet
					g = new Game(away,home,gameStatus,url,aAbr,hAbr);
				}
				s.addGame(g);
			}
			return s;

		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	//TODO: check for overturned goals - determine if the json file shows anything about them (would throw off score checking)!!!!
	public static void gameUpdate(Schedule s) throws Exception{
		for(int i = 0; i < s.getNumGames();i++){
			Game game = s.getGame(i);
						
			//check game hasn't already ended
			if(!game.getStatus().equals("Final")){
				JSONObject gameInfo = parseNHL(game.getURL());
				
				String status = gameInfo.getJSONObject("gameData").getJSONObject("status").getString("detailedState");
				
				if(game.getStatus() != status)	game.setStatus(status);	//update game status
				if(status.startsWith("In Progress")){
					
					//get current home and away score
					JSONObject teams = gameInfo.getJSONObject("liveData").getJSONObject("boxscore").getJSONObject("teams");
					int homeScore = teams.getJSONObject("home").getJSONObject("teamStats").getJSONObject("teamSkaterStats").getInt("goals");
					int awayScore = teams.getJSONObject("away").getJSONObject("teamStats").getJSONObject("teamSkaterStats").getInt("goals");
					
					//if away/home score has changed since last update
					if(game.getScore("home") != homeScore || game.getScore("away") != awayScore){
						//Update the scores
						game.setScore("home",homeScore);
						game.setScore("away",awayScore);

						JSONArray scoringPlays = gameInfo.getJSONObject("liveData").getJSONObject("plays").getJSONArray("scoringPlays");
						int activityInd = scoringPlays.getInt(scoringPlays.length()-1);
						
						JSONObject goal = gameInfo.getJSONObject("liveData").getJSONObject("plays").getJSONArray("allPlays").getJSONObject(activityInd);
						String teamScored = goal.getJSONObject("team").getString("name");
			
						Runnable r = new GoalScored(game,teamScored,activityInd);
						new Thread(r).start();			
					}
				}
			}
		}
	}
	
	public static String[] getScorers(int ind, Game game) throws Exception{
		//get latest game JSON file
		JSONObject gameInfo = parseNHL(game.getURL());
		try{
			JSONObject goal = gameInfo.getJSONObject("liveData").getJSONObject("plays").getJSONArray("allPlays").getJSONObject(ind);
			JSONArray players = goal.getJSONArray("players");
			int numPlayers = players.length()-1;	

			if(goal.getJSONObject("result").getBoolean("emptyNet"))	numPlayers++;	//TODO: figure out why for some reason "emptyNet" not found when the net is actually empty, works on normal goals
			String[] scorers = new String[numPlayers];
			scorers[0] = players.getJSONObject(0).getJSONObject("player").getString("fullName");

			if(numPlayers>1){
				scorers[1] = players.getJSONObject(1).getJSONObject("player").getString("fullName");
				if (numPlayers > 2){
					scorers[2] = players.getJSONObject(2).getJSONObject("player").getString("fullName");
				}
			}
			else if(!GoalScored.raspPi){
				try{
					FileWriter fw = new FileWriter("C:/Users/Michael/Documents/JSON - No Assist/" + game.getAwayTeam() + game.getScore("away") + "-" + game.getScore("home") + ".json");
					fw.write(gameInfo.toString());
					fw.close();
				}catch(Exception e){
					e.printStackTrace();
				}			
			}		
			return scorers;
		}catch(Exception e){
			if(!GoalScored.raspPi){
				FileWriter f = new FileWriter("C:/Users/Michael/Documents/JSON - Error/" + game.getAwayTeam() + game.getScore("away") + "-" + game.getScore("home") + "ERROR.json");
				f.write(gameInfo.toString());
				f.close();
			}
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getGoalDescription(int ind, Game game) throws Exception{
		JSONObject gameInfo = parseNHL(game.getURL());
		JSONObject goal = gameInfo.getJSONObject("liveData").getJSONObject("plays").getJSONArray("allPlays").getJSONObject(ind);
		return goal.getJSONObject("result").getString("description");
	}
}


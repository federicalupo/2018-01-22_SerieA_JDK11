package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {

	public List<Season> listAllSeasons() {
		String sql = "SELECT season, description FROM seasons order by season";
		List<Season> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Season(res.getInt("season"), res.getString("description")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Team(res.getString("team")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void punteggio(Team team, Map<Integer, Integer> punteggi ){
		String sql ="select season, hometeam, awayteam, ftr " + 
				"from matches " + 
				"where hometeam =? " + 
				"or awayteam =? " + 
				"order by season";
		

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, team.getTeam());
			st.setString(2, team.getTeam());
			
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Integer anno = res.getInt("season");
				String esito = res.getString("ftr");
				
				if(esito.compareTo("D")==0) { //pareggiato
					punteggi.put(anno, punteggi.get(anno)+1);
				}else {
					if((res.getString("hometeam").compareTo(team.getTeam())==0) && (esito.compareTo("H")==0) ) { //se gioca a casa e vince
						punteggi.put(anno, punteggi.get(anno)+3);	
					}else if((res.getString("awayteam").compareTo(team.getTeam())==0) && (esito.compareTo("A")==0)) { //se gioca ospite e vince
						punteggi.put(anno, punteggi.get(anno)+3); 
					}	
				}
				
			}
		 	
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}
	
	/**Per avere annate in cui gioca
	 * 
	 * @param team
	 * @return
	 */
	public List<Integer> stagioni(Team team){
		String sql ="select distinct season " + 
				"from matches " + 
				"where hometeam =? " + 
				"or awayteam =? " + 
				"order by season";
		
		List<Integer> stagioni = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, team.getTeam());
			st.setString(2, team.getTeam());
			
			ResultSet res = st.executeQuery();

			while (res.next()) {
				stagioni.add(res.getInt("season"));
				
			}
		 	
			conn.close();
			return stagioni;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}


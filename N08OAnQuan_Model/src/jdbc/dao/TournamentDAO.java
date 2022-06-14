package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import model.AwardedPrize;
import model.Club;
import model.Round;
import model.Tournament;

public class TournamentDAO extends DAO{

	public TournamentDAO() {
		super();
	}
	
	public boolean addTour(Tournament tour, Club cl) {
		boolean result = true;
		String sqlAddTour = "INSERT INTO tournament "
				+ " (id_creator, name, description, startDate, endDate, isPublic, id_club) "
				+ "VALUES (?,?,?,?,?,?,?);";
		String sqlAddRound = "INSERT INTO round "
				+ " (id_tour, name, startDate, endDate) "
				+ "VALUES (?,?,?,?)";
		String sqlAddAwardedPrize = "INSERT INTO awardedprize "
				+ " (date, id_prize, id_tour) "
				+ "VALUES (?,?,?);";
		try {
			PreparedStatement ps = conn.prepareStatement(sqlAddTour, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, tour.getCreator().getId());
			ps.setString(2, tour.getName());
			ps.setString(3, tour.getDescription());
			ps.setTimestamp(4, (Timestamp) tour.getStartDate());
			ps.setTimestamp(5, (Timestamp) tour.getEndDate());
			ps.setBoolean(6, tour.isPublic());
			ps.setInt(7, cl.getId());
			
			conn.setAutoCommit(false);
			ps.executeUpdate();
			ResultSet generatedKey = ps.getGeneratedKeys();
			
			if (generatedKey.next()) {
				tour.setId(generatedKey.getInt(1));
				
				for (int i = 0; i < tour.getListRound().size(); i++) {
					Round round = tour.getListRound().get(i);
					
					ps = conn.prepareStatement(sqlAddRound, Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, tour.getId());
					ps.setString(2, round.getName());
					ps.setTimestamp(3, (Timestamp) round.getStartDate());
					ps.setTimestamp(4, (Timestamp) round.getEndDate());
					
					ps.executeUpdate();
					generatedKey = ps.getGeneratedKeys();
					if(generatedKey.next()) {
						round.setId(generatedKey.getInt(1));
					}
				}
				
				for (int i = 0; i < tour.getListPrize().size(); i++) {
					AwardedPrize ap = tour.getListPrize().get(i);
					
					ps = conn.prepareStatement(sqlAddAwardedPrize, Statement.RETURN_GENERATED_KEYS);
					ps.setTimestamp(1, (Timestamp) ap.getDate());
					ps.setInt(2, ap.getPrize().getId());
					ps.setInt(3, tour.getId());
					
					ps.executeUpdate();
					generatedKey = ps.getGeneratedKeys();
					if(generatedKey.next()) {
						ap.setId(generatedKey.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			result = false;
			try {
				conn.rollback();
			} catch (SQLException ex) {
				result = false;
			}
		} finally {
			try {
				conn.setAutoCommit(true);
			}catch (SQLException e) {
				result = false;
			}
		}
		
		return result;
	}
}

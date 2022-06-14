package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import model.Club;
import model.JoinedPlayer;
import model.Player;

public class ClubDAO extends DAO {

    public ClubDAO() {
        super();
    }

    public int addClub(Club cl) {
        int result = 0;
        String sql = "INSERT INTO club "
                + "(name, description, createdDate, isPublic) "
                + "VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cl.getName());
            ps.setString(2, cl.getDescription());
            ps.setTimestamp(3, (Timestamp) cl.getCreatedDate());
            ps.setBoolean(4, cl.isPublic());

            ps.executeUpdate();
            ResultSet generatedKey = ps.getGeneratedKeys();

            if (generatedKey.next()) {
                cl.setId(generatedKey.getInt(1));
            }
            result = 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            //ten club da duoc su dung
            result = 2;
        } catch (SQLException e) {
            // TODO: handle exception
        }
        return result;
    }

    public boolean editClub(Club cl) {
        boolean result = false;
        String sql = "UPDATE club "
                + "SET name = ?, description = ?, isPublic = ?"
                + "WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cl.getName());
            ps.setString(2, cl.getDescription());
            ps.setBoolean(3, cl.isPublic());
            ps.setInt(4, cl.getId());

            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean removeClub(Club cl) {
        boolean result = false;
        String sql = "DELETE FROM club WHERE id = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cl.getId());
            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return result;
    }

    public ArrayList<Club> searchClub(String key) {
        ArrayList<Club> listClub = new ArrayList<>();
        String sql = "SELECT * FROM club WHERE "
                + "(id IN (SELECT id FROM club WHERE name LIKE ?)) "
                + "AND isPublic = 1;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Club club = new Club();
                club.setId(rs.getInt("id"));
                club.setName(rs.getString("name"));
                club.setDescription(rs.getString("description"));
                club.setCreatedDate(rs.getDate("createdDate"));
                listClub.add(club);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listClub;
    }
    
    public Club getClubMember(Club club) {
        String sqlGetMember = "SELECT * "
                + "FROM joinedplayer AS jp "
                + "JOIN player AS p ON p.id = jp.id_player "
                + "WHERE jp.id_club = ?;";
        try {
            PreparedStatement ps= conn.prepareStatement(sqlGetMember);
            ps.setInt(1, club.getId());
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                JoinedPlayer jp = new JoinedPlayer();
                jp.setId(rs.getInt("jp.id"));
                jp.setJoinedDate(rs.getDate("joinedDate"));
                jp.setRole(rs.getString("role"));                
                
                Player player = new Player();
                player.setId(rs.getInt("p.id"));
                player.setCountry(rs.getString("country"));
                player.setEmail(rs.getString("email"));
                player.setName(rs.getString("name"));
                player.setUrlAvartar(rs.getString("avatar"));
                player.setUsername(rs.getString("username"));
                
                jp.setPlayer(player);
                club.getListMember().add(jp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return club;
    }
}

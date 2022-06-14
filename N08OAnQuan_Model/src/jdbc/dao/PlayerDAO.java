package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import model.Club;

import model.Player;

public class PlayerDAO extends DAO {

    public PlayerDAO() {
        super();
    }

    public boolean checkLogin(Player p) {
        boolean result = false;
        String sqlCheck = "SELECT * FROM player WHERE username = ? AND password = ?";
        String sqlGetFriend = "SELECT DISTINCT * FROM player AS p WHERE "
                + "(p.id IN (SELECT f.id_player2 FROM friend AS f WHERE f.id_player1 = ?)) AND "
                + "(p.id IN (SELECT f.id_player1 FROM friend AS f WHERE f.id_player2 = ?))";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlCheck);
            ps.setString(1, p.getUsername());
            ps.setString(2, p.getPassword());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCountry(rs.getString("country"));
                p.setEmail(rs.getString("email"));
                p.setUrlAvartar(rs.getString("avatar"));

                ps = conn.prepareStatement(sqlGetFriend);
                ps.setInt(1, p.getId());
                ps.setInt(2, p.getId());
                rs = ps.executeQuery();

                ArrayList<Player> listFriend = new ArrayList<Player>();
                while (rs.next()) {
                    Player friend = new Player();
                    friend.setId(rs.getInt("id"));
                    friend.setName(rs.getString("name"));
                    friend.setCountry("country");
                    friend.setEmail(rs.getString("email"));
                    friend.setUrlAvartar(rs.getString("avatar"));
                    listFriend.add(friend);
                }

                p.setListFriend(listFriend);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String addPlayer(Player p) {
        String result = "false";
        String sqlInsert = "INSERT INTO player "
                + "(username, password, name, country, email, avatar) "
                + "VALUES (?,?,?,?,?,?);";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getUsername());
            ps.setString(2, p.getPassword());
            ps.setString(3, p.getName());
            ps.setString(4, p.getCountry());
            ps.setString(5, p.getEmail());
            ps.setString(6, p.getUrlAvartar());

            ps.executeUpdate();
            ResultSet generatedKey = ps.getGeneratedKeys();

            if (generatedKey.next()) {
                p.setId(generatedKey.getInt(1));
            }
            result = "ok";
        } catch (SQLIntegrityConstraintViolationException e) {
            //System.out.println("username đã được sử dụng");
            result = "used username";
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    public boolean editPlayer(Player p) {
        boolean result = false;
        String sql = "UPDATE player "
                + "SET password = ?, name = ?, country = ?, email = ?, avatar = ?"
                + "WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getPassword());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCountry());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getUrlAvartar());
            ps.setInt(6, p.getId());

            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean removePlayer(Player p) {
        boolean result = false;
        String sql = "DELETE FROM player WHERE id = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return result;
    }

    public ArrayList<Player> searchPlayer(String key) {
        ArrayList<Player> listPlayer = new ArrayList<>();
        String sql = "SELECT * FROM player WHERE name LIKE ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setCountry(rs.getString("country"));
                player.setEmail(rs.getString("email"));
                player.setName(rs.getString("name"));
                player.setUrlAvartar(rs.getString("avatar"));
                listPlayer.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listPlayer;
    }
    
    public ArrayList<Club> getJoinedClub(Player player) {
        String sql = "SELECT cl.* "
                + "FROM joinedplayer AS jp JOIN club AS cl  ON jp.id_club = cl.id "
                + "WHERE jp.id_player = ?;";
        ArrayList<Club> listClub = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {                
                Club cl = new Club();
                cl.setId(rs.getInt("id"));
                cl.setName(rs.getString("name"));
                cl.setDescription(rs.getString("description"));
                cl.setCreatedDate(rs.getDate("createdDate"));
                cl.setPublic(rs.getBoolean("isPublic"));
                
                listClub.add(cl);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listClub;
    }
    
    public boolean addFriend(Player p1, Player p2) {
        boolean result = false;
        String sql = "INSERT INTO friend (id_player1, id_player2) VALUES (?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p1.getId());
            ps.setInt(2, p2.getId());
            
            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
        }
        return result;
    }
    
    public boolean addFriendDeny(Player p1, Player p2) {
        boolean result = false;
        String sql = "DELETE FROM friend WHERE id_player1 = ? AND id_player2 = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p2.getId());
            ps.setInt(2, p1.getId());
            
            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
        }
        return result;
    }
    
    public ArrayList<Player> getFriendRequests(Player player) {
        ArrayList<Player> listRequest = new ArrayList<>();
        String sql = "SELECT * FROM player AS p JOIN friend AS f ON p.id = f.id_player1 "
                + "WHERE f.id_player2 = ? AND "
                + "f.id_player1 NOT IN (SELECT DISTINCT id FROM player AS pl WHERE "
                + "(pl.id IN (SELECT f.id_player2 FROM friend AS f WHERE f.id_player1 = ?)) AND "
                + "(pl.id IN (SELECT f.id_player1 FROM friend AS f WHERE f.id_player2 = ?)));";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 3; i++) {
                ps.setInt(i, player.getId());
            }
            
            ResultSet rs= ps.executeQuery();
            while (rs.next()){
                Player friend = new Player();
                friend.setId(rs.getInt("id"));
                friend.setName(rs.getString("name"));
                
                listRequest.add(friend);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listRequest;
    }
}

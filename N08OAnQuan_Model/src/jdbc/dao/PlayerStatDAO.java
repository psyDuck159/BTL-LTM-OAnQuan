package jdbc.dao;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Player;
import model.PlayerStat;

public class PlayerStatDAO extends DAO {

    public PlayerStatDAO() {
        super();
    }

    public PlayerStat getPlayerStat(Player player) {
        PlayerStat pt = new PlayerStat();
        String sqlGetWinTotal = "{call get_win_total(?,?)}";
        String sqlGetMatchTotal = "{call get_match_total(?,?)}";
        try {
            CallableStatement ps = conn.prepareCall(sqlGetWinTotal);
            ps.setInt(1, player.getId());
            ps.setInt(2, Types.INTEGER);
            ps.execute();

            pt.setId(player.getId());
            pt.setName(player.getName());
            pt.setWinTotal(ps.getInt(2));

            ps = conn.prepareCall(sqlGetMatchTotal);
            ps.setInt(1, player.getId());
            ps.setInt(2, Types.INTEGER);
            ps.execute();

            pt.setMatchTotal(ps.getInt(2));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return pt;
    }
    
    public ArrayList<PlayerStat> getRank() {
        ArrayList<PlayerStat> result = new ArrayList<>();
        String sql = "{call get_rank()}";
        try {
            CallableStatement cs = conn.prepareCall(sql);
            
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()) {
                PlayerStat pl = new PlayerStat();
                pl.setId(rs.getInt("id"));
                pl.setName(rs.getString("name"));
                pl.setUsername(rs.getString("username"));
                pl.setWinTotal(rs.getInt("winTotal"));
                pl.setCountry(rs.getString("country"));
                pl.setLoseTotal(rs.getInt("loseTotal"));
                pl.setMatchTotal(rs.getInt("matchTotal"));
                pl.setTotalScore(rs.getInt("scoreTotal"));
                
                result.add(pl);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
//    public static void main(String[] args) {		
//            ArrayList<PlayerStat> pt = (new PlayerStatDAO()).getRank();
//            System.out.println("size " + pt.size());
//    }
}

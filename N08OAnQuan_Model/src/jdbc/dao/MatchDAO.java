package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import model.Capture;
import model.Match;
import model.PlayingPlayer;
import model.Round;

public class MatchDAO extends DAO {

    public MatchDAO() {
        super();
    }

    public boolean addMatch(Match match) {
        boolean result = true;
        Round round = match.getRound();
        String sqlAddMatch = "INSERT INTO tblmatch "
                + " (id_round, date, time) "
                + "VALUES (?,?,?)";
        String sqlAddPLaying = "INSERT INTO playingplayer "
                + "(id_player, id_match) "
                + "VALUES (?,?);";
        String sqlAddCapture = "INSERT INTO tblcapture "
                + "(orderTurn, count, id_playingPlayer) "
                + "VALUES (?,?,?)";

        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sqlAddMatch, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, round.getId());
            ps.setTimestamp(2, new Timestamp(match.getDate().getTime()));
            ps.setInt(3, match.getTime());

            ps.executeUpdate();
            ResultSet generatedKey = ps.getGeneratedKeys();
            if (generatedKey.next()) {
                match.setId(generatedKey.getInt(1));
            }
            System.out.println("Match:"+match.getId());
            for (int i = 0; i < match.getPlayers().length; i++) {
                PlayingPlayer pp = match.getPlayers()[i];

                ps = conn.prepareStatement(sqlAddPLaying, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, pp.getPlayer().getId());
                ps.setInt(2, match.getId());

                ps.executeUpdate();
                generatedKey = ps.getGeneratedKeys();
                if (generatedKey.next()) {
                    pp.setId(generatedKey.getInt(1));
                }
                System.out.println("Player"+pp.getPlayer().getName()+":"+pp.getId());
                System.out.println("Size cap"+pp.getListCapture().size());
                for (int j = 0; j < pp.getListCapture().size(); j++) {
                    Capture cap = pp.getListCapture().get(j);
                    ps = conn.prepareStatement(sqlAddCapture, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, cap.getOrder());
                    ps.setInt(2, cap.getCount());
                    ps.setInt(3, pp.getId());
                    System.out.println("cap" + pp.getPlayer().getName()+":"+cap.getCount());
                    ps.executeUpdate();
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        cap.setId(generatedKeys.getInt(1));
                    }
                }

            }
            //conn.commit();
        } catch (Exception e) {
            // TODO: handle exception
            result = false;
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                result = false;
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                result = false;
                e.printStackTrace();
            }
        }
        return result;
    }
}

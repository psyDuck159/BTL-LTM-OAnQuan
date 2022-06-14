package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Prize;

public class PrizeDAO extends DAO{

	public PrizeDAO() {
		super();
	}
	
	public boolean addPrize(Prize pr) {
		boolean result = false;
		String sql = "INSERT INTO prize "
				+ "(name, price, point) "
				+ "VALUES (?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, pr.getName());
			ps.setFloat(2, pr.getPrice());
			ps.setInt(3, pr.getPoint());
			
			ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if(generatedKeys.next()) {
				pr.setId(generatedKeys.getInt(1));
			}
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean editPrize(Prize pr) {
		boolean result = false;
		String sql = "UPDATE prize "
				+ "SET name = ?, price = ?, point = ?"
				+ "WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, pr.getName());
			ps.setFloat(2, pr.getPrice());
			ps.setInt(3, pr.getPoint());
			ps.setInt(4, pr.getId());
			
			ps.executeUpdate();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean removePrize(Prize pr) {
		boolean result = false;
		String sql = "DELETE FROM prize WHERE id = ?;";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, pr.getId());
			ps.executeUpdate();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return result;
	}
	
	public ArrayList<Prize> searchPrize(String key) {
		ArrayList<Prize> listPrize = new ArrayList<>();
        String sql = "SELECT * FROM prize WHERE name LIKE ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                Prize prize = new Prize();
                prize.setId(rs.getInt("id"));
                prize.setPrice(rs.getFloat("price"));
    			prize.setPoint(rs.getInt("point"));
    			prize.setName(rs.getString("name"));
    
                listPrize.add(prize);
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return listPrize;
	}
}

package ie.its.loginuser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ie.its.generic.GenericDao;

public class LoginUserDaoImpl extends GenericDao<LoginUser> {

	

	@Override
	protected String getTableName() {// TODO Auto-generated method stub
		return "loginuser";
	}

	@Override
	protected String[] getColumnNames() {
		return new String[]{"id","username","password","type"};
	}

	@Override
	protected String[] getColumnValues(LoginUser entity) {
		return new String[]{""+entity.getId(),
				"'"+entity.getUserName()+"'",
				"md5('"+entity.getPassword()+"')",
				"'"+ entity.getType()+"'"};
	}

	@Override
	protected LoginUser retrieveEntity(ResultSet rs) throws SQLException {
		LoginUser loginUser = new LoginUser();
		loginUser.setId(rs.getLong("id"));
		loginUser.setUserName(rs.getString("username"));
		// did not retrieve password, because it is encypted in the DB 
		loginUser.setType(rs.getString("type"));
		
		return loginUser;
	}

	@Override
	protected String getIdValue(LoginUser entity) {
		
		return ""+entity.getId();
	}
	
	public LoginUser getValidUser(String username, String password) throws SQLException{
		String sql= "select * from "+ getTableName()+ " where "
				+ "username = '"+ username +"' and "
				+ "password = md5('"+password +"'); ";
		
		System.out.println(sql);
		
		List<LoginUser> list = executeQuery(sql);
		
		if (list.size() == 0){ 
			return null;
		}
		return list.get(0);
	}
	
	public static void main(String[] args) {
		LoginUser loginUser = new LoginUser(1,"joe","pass","tech");
		LoginUserDaoImpl dao = new LoginUserDaoImpl();
		
		try {
			dao.create(loginUser);
			
			loginUser = dao.getValidUser("joe", "wrong");
			System.out.println("we got (wrong password): "+loginUser);
			
			loginUser = dao.getValidUser("joe", "pass");
			System.out.println("we got (correct password): "+loginUser);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

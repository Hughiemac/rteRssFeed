package ie.its.generic;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDao<T> {
	
	protected abstract String getTableName();
	protected abstract String[] getColumnNames();
	protected abstract String[] getColumnValues(T entity);
	protected abstract T retrieveEntity(ResultSet rs) throws SQLException;
	protected String getIdColumn(){ return "id";}
	protected abstract String getIdValue(T entity);

	
	
	protected static final String dbHost="localhost";
	protected static final String dbDatabase="its3";
	protected static final String dbUser="root";
	protected static final String dbPassword="";
	protected static final String dbUrl="jdbc:mysql://" + dbHost
			+ "/" + dbDatabase
			+ "?user=" + dbUser
			+ "&password=" + dbPassword;



	protected Connection getConnection() throws SQLException{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(dbUrl);
	}
	
	// used by create and the query methods
	protected String getColumnNamesAsString(){
		StringBuilder sb = new StringBuilder();
		String names[] = getColumnNames();
		sb.append(names[0]);
		for (int i= 1; i< names.length; i++ ){
			sb.append(","+names[i]);
		}
		return sb.toString();
	}
	
	// used by create
	protected String getColumnValuesAsString(T entity){
		StringBuilder sb = new StringBuilder();
		String values[] = getColumnValues(entity);
		sb.append(values[0]);
		for (int i= 1; i< values.length; i++ ){
			sb.append(","+values[i]);
		}
		return sb.toString();
	}
	
	// used by update statement
	private String getUpdateString(T entity){
		StringBuilder sb = new StringBuilder();
		String names[] = getColumnNames();
		String values[] = getColumnValues(entity);
		 
		sb.append(names[0]+"="+values[0]);
		for (int i= 1; i< values.length; i++ ){
			sb.append(" , " + names[i]+"="+values[i]);
		}
		return sb.toString();
		
	}
	protected void executeUpdate(String sql) throws SQLException{
		Connection connection= null;
		Statement statement= null;
		System.out.println(sql);
		
		try{
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql);
		}finally{
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
	}
	

	
	public void create(T entity)throws SQLException{
		String sql =  "insert into "+getTableName()+ " "
				+ "("+getColumnNamesAsString()+") "
						+ "values ("+getColumnValuesAsString(entity)+");";
		executeUpdate(sql);
	}
	public void update (T entity)throws SQLException{
		String sql =  "update "+getTableName()+ " "
				+ "set "+getUpdateString(entity) +" "
						+ "where "+getIdColumn()+"="+getIdValue(entity)+";";
		executeUpdate(sql);
	}
	public void delete (T entity)throws SQLException{
		String sql =  "delete from "+getTableName()+" "
				+ "where "+getIdColumn()+ "="+getIdValue(entity)+";";
		executeUpdate(sql);

	}
	
	
	protected List<T> executeQuery(String sql) throws SQLException{
		List<T> returnList = new ArrayList<T>();
		Connection connection= null;
		Statement statement= null;
		ResultSet rs= null;

		try{
			connection = getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);

			while(rs.next()){
				T entity = this.retrieveEntity(rs);
				returnList.add(entity);
			}
		}finally{
			if(rs != null) rs.close();
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}

		return returnList;
	}
	public List<T> getAll() throws SQLException{
		String sql = "select "+getColumnNamesAsString()+ " from "+getTableName()+";";
		return executeQuery(sql);
	}
	public T findById(long id) throws SQLException{
		String sql = "select "+getColumnNamesAsString()+ " from "+getTableName()+" "
				+ "where "+getIdColumn() +"="+id+";";
		List<T> list = executeQuery(sql);
		if (list.size()== 0) return null;
		
		return list.get(0);
		
		
	}
	
public long createWithAutoIncrement(T entity) throws SQLException{
		
		String sql =  "insert into "+getTableName()+ " "
				+ "("+getColumnNamesAsString()+") "
						+ "values ("+getColumnValuesAsString(entity)+");";
			long id = executeCreateWithAutoIncrement(sql);
			return id;
	}
	
	protected long executeCreateWithAutoIncrement(String sql) throws SQLException{
		Connection connection= null;
		Statement statement= null;
		ResultSet rs = null;
		System.out.println(sql);
		long autoIncKey = -1;
		try{
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs = statement.getGeneratedKeys();
			
			if (rs.next()) {
			    autoIncKey = rs.getInt(1);
			} else {
			    System.out.println("no key generated");
			}
			System.out.println(autoIncKey);
			
		}finally{
			if (rs != null) rs.close();
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
		return autoIncKey;
	}
	
}

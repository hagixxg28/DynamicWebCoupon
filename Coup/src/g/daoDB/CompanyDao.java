package g.daoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import b.exceptions.DaoExceptions.CustomerDoesNotExistException;
import b.exceptions.DaoExceptions.DaoException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Company;
import d.beanShells.Coupon;
import f.dao.ICompanyDao;

public class CompanyDao implements ICompanyDao {
	private ConnectionPool pool = ConnectionPool.getPool();
	private Extractor extractor = Extractor.getExtractor();

	public CompanyDao() {
		super();
	}

	@Override
	public void createCompany(Company comp) throws DaoException {
		String sql1 = "INSERT INTO company  (comp_id,name,password,email) VALUES (?,?,?,?)";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql1);) {

			stmt.setLong(1, comp.getId());
			stmt.setString(2, comp.getCompName());
			stmt.setString(3, comp.getPassword());
			stmt.setString(4, comp.getEmail());
			stmt.executeUpdate();

			System.out.println(comp + " has been added");

		} catch (SQLException e) {
			throw new DaoException("Id is already in use or null variables has been placed");
		} finally {
			pool.returnConnection(con);

		}

	}

	@Override
	public void removeCompany(Company comp) throws DaoException {
		String sql = ("DELETE FROM company WHERE comp_id=?");
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setLong(1, comp.getId());
			stmt.executeUpdate();
			comp.getCoupons().clear();

		} catch (SQLException e) {
			throw new DaoException("Company was not found");

		} finally {
			pool.returnConnection(con);
		}
	}

	@Override
	public void updateCompany(Company comp) throws DaoException {
		String sql = "UPDATE company SET name=?, password=?,email=? WHERE comp_id=?";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setString(1, comp.getCompName());
			stmt.setString(2, comp.getPassword());
			stmt.setString(3, comp.getEmail());
			stmt.setLong(4, comp.getId());
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("This customer does not exist");

		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Company readCompany(Company comp) throws DaoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Company company = null;
		try {

			connection = pool.getConnection();
			String sql = "SELECT * FROM company WHERE comp_id = ? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, comp.getId());
			resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			company = extractor.extractCompanyFromResultSet(resultSet);

		} catch (SQLException e) {
			throw new DaoException("No company was found");

		} finally {
			pool.returnConnection(connection);
		}
		return company;
	}

	@Override
	public Collection<Company> getAllCompanies() throws DaoException {
		Collection<Company> collection = new ArrayList<Company>();
		String sql = "SELECT * FROM company";
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet resultSet = stmt.executeQuery(sql);) {

			while (resultSet.next()) {
				Company other = null;
				other = extractor.extractCompanyFromResultSet(resultSet);
				collection.add(other);
			}

		} catch (SQLException e) {
			throw new DaoException("There are no companies");

		} finally {
			pool.returnConnection(con);
		}
		return collection;
	}

	@Override
	public Boolean login(Long id, String password) throws DaoException {
		String sql = "SELECT password FROM company WHERE comp_id=?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			connection = pool.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				System.out.println("Wrong pass");
				return false;
			}
			String str = resultSet.getString("password");
			if (password.equals(str)) {
				return true;
			} else {
				System.out.println("Wrong pass");
				return false;
			}

		} catch (SQLException e) {
			throw new DaoException("Unable to login-Wrong password or Id");

		} finally {
			pool.returnConnection(connection);
		}
	}

	@Override
	public Collection<Coupon> getAllCoupons(Company comp) throws DaoException {
		Collection<Coupon> collection = new ArrayList<Coupon>();
		String sql = "SELECT company_coupon.coup_id FROM company_coupon INNER JOIN coupon"
				+ " ON coupon.coup_id=company_coupon.coup_id" + " WHERE comp_id=?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = pool.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, comp.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Coupon other = new Coupon();
				other.setId(resultSet.getLong("coup_id"));
				collection.add(other);
			}
		} catch (SQLException e) {
			throw new DaoException("No coupons were found under this company");
		}
		String sql2 = "Select *  FROM coupon where coup_id=?";
		for (Coupon coupon : collection) {
			try {
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setLong(1, coupon.getId());
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					coupon = extractor.extractCouponFromResultSet(resultSet);
				}

			} catch (SQLException e) {
				throw new DaoException("No coupons were found under this company");
			} finally {
				pool.returnConnection(connection);
			}
		}
		return collection;
	}

	@Override
	public Boolean companyExists(Company comp) throws DaoException {
		ArrayList<Long> list = new ArrayList<>();
		String sql = "SELECT comp_id FROM company WHERE comp_id=?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = pool.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, comp.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				list.add((resultSet.getLong("comp_id")));
			}
			for (Long long1 : list) {
				if (comp.getId() == long1) {
					return true;
				}
			}

		} catch (SQLException e) {
			throw new DaoException("Error occurred at companyExists method");
		} finally {
			pool.returnConnection(connection);
		}
		return false;
	}

}

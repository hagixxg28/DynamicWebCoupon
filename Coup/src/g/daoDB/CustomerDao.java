package g.daoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import b.exceptions.DaoExceptions.CustomerAlreadyExistsException;
import b.exceptions.DaoExceptions.CustomerDoesNotExistException;
import b.exceptions.DaoExceptions.DaoException;
import b.exceptions.DaoExceptions.NoCustomersException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import f.dao.ICustomerDao;

public class CustomerDao implements ICustomerDao {
	private ConnectionPool pool = ConnectionPool.getPool();
	private Extractor extractor = Extractor.getExtractor();

	public CustomerDao() {
		super();
	}

	@Override
	public void createCustomer(Customer cust) throws DaoException {
		String sql = "INSERT INTO customer  (cust_id,name,password) VALUES (?,?,?)";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setLong(1, cust.getId());
			stmt.setString(2, cust.getCustName());
			stmt.setString(3, cust.getPassword());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CustomerAlreadyExistsException("This customer already exists");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeCustomer(Customer cust) throws DaoException {
		String sql = String.format("DELETE FROM customer WHERE cust_id=%d", cust.getId());
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("This customer does not exist");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCustomer(Customer cust) throws DaoException {
		String sql = "UPDATE customer SET name=?, password=? WHERE cust_id=?";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setString(1, cust.getCustName());
			stmt.setString(2, cust.getPassword());
			stmt.setLong(3, cust.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("This customer does not exist");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Customer getCustomer(Customer cust) throws DaoException {
		Customer customer = new Customer();
		String sql = String.format("SELECT * FROM customer WHERE cust_id=%d", cust.getId());
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet resultSet = stmt.executeQuery();) {
			while (resultSet.next()) {
				customer = extractor.extractCustomerFromResultSet(resultSet);
				customer.setCoupons(getCoupons(customer));
			}
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("This customer does not exist");
		} finally {
			pool.returnConnection(con);
		}
		return customer;

	}

	@Override
	public Collection<Customer> getAllCustomer() throws DaoException {
		Collection<Customer> collection = new ArrayList<Customer>();
		String sql = "SELECT * FROM customer";
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet resultSet = stmt.executeQuery(sql);) {
			while (resultSet.next()) {
				Customer customer = null;
				customer = extractor.extractCustomerFromResultSet(resultSet);
				collection.add(customer);
			}
		} catch (SQLException e) {
			throw new NoCustomersException("There are no customers at the Database");
		} finally {
			pool.returnConnection(con);
		}
		return collection;

	}

	@Override
	public Collection<Customer> getAllCustomerWithCoupons() throws DaoException {
		Collection<Customer> collection = new ArrayList<Customer>();
		String sql = "SELECT * FROM customer";
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet resultSet = stmt.executeQuery(sql);) {
			while (resultSet.next()) {
				Customer customer = null;
				customer = extractor.extractCustomerFromResultSet(resultSet);
				customer.setCoupons(getCoupons(customer));
				if (!customer.getCoupons().isEmpty()) {
					collection.add(customer);
				}
			}
		} catch (SQLException e) {
			throw new NoCustomersException("There are no customers");
		} finally {
			pool.returnConnection(con);
		}
		return collection;

	}

	@Override
	public Boolean login(Long id, String password) throws DaoException {
		String sql = "SELECT password FROM customer WHERE cust_id=?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = pool.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, id);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			String str = resultSet.getString("password");
			if (password.equals(str)) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("No customer with this id found");
		} finally {
			pool.returnConnection(connection);
		}
	}

	@Override
	public Collection<Coupon> getCoupons(Customer cust) throws DaoException {
		Collection<Coupon> collection = new ArrayList<Coupon>();
		String sql = "SELECT customer_coupon.coup_id FROM customer_coupon INNER JOIN coupon"
				+ " ON coupon.coup_id=customer_coupon.coup_id" + " WHERE cust_id=?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = pool.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, cust.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Coupon other = new Coupon();
				other.setId(resultSet.getLong("coup_id"));
				collection.add(other);
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
				} finally {
					pool.returnConnection(connection);
				}
			}
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("Couldn't find a customer with this coupon id");
		}
		return collection;
	}

	@Override
	public Boolean customerExists(Customer cust) throws DaoException {
		ArrayList<Long> list = new ArrayList<>();
		String sql = "SELECT cust_id FROM customer WHERE cust_id=" + cust.getId();
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				list.add((rs.getLong("cust_id")));
			}
			for (Long long1 : list) {
				if (cust.getId() == long1) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("This customer does not exist");
		} finally {
			pool.returnConnection(con);
		}
		return false;
	}
}

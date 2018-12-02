package g.daoDB;

import java.sql.ResultSet;
import java.sql.SQLException;

import b.exceptions.DaoExceptions.DaoException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.CouponType;
import f.dao.IResultExtractor;

public class Extractor implements IResultExtractor {
	private static Extractor instance;

	private Extractor() {
		super();
	}

	public static Extractor getExtractor() {
		if (instance == null) {
			instance = new Extractor();
		}
		return instance;
	}

	@Override
	public Company extractCompanyFromResultSet(ResultSet resultSet) throws DaoException, SQLException {
		Company company = new Company();
		company.setId(resultSet.getLong("comp_id"));
		company.setCompName(resultSet.getString("name"));
		company.setPassword(resultSet.getString("password"));
		company.setEmail(resultSet.getString("email"));
		return company;
	}

	@Override
	public Customer extractCustomerFromResultSet(ResultSet resultSet) throws DaoException, SQLException {
		Customer customer = new Customer();
		customer.setId(resultSet.getLong("cust_id"));
		customer.setCustName(resultSet.getString("name"));
		customer.setPassword(resultSet.getString("password"));
		return customer;
	}

	@Override
	public Coupon extractCouponFromResultSet(ResultSet resultSet) throws DaoException, SQLException {
		Coupon coupon = new Coupon();
		coupon.setId(resultSet.getLong("coup_id"));
		coupon.setTitle(resultSet.getString("title"));
		coupon.setStartDate(resultSet.getDate("start_date"));
		coupon.setEndDate(resultSet.getDate("end_date"));
		coupon.setAmount(resultSet.getInt("amount"));
		coupon.setType(CouponType.typeSort(resultSet.getString("type")));
		coupon.setMessage(resultSet.getString("message"));
		coupon.setPrice(resultSet.getDouble("price"));
		coupon.setImage(resultSet.getString("image"));
		return coupon;
	}

}

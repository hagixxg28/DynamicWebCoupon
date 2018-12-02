package f.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import b.exceptions.DaoExceptions.DaoException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;

public interface IResultExtractor {

	Company extractCompanyFromResultSet(ResultSet resultSet) throws DaoException, SQLException;

	Customer extractCustomerFromResultSet(ResultSet resultSet) throws DaoException, SQLException;

	Coupon extractCouponFromResultSet(ResultSet resultSet) throws DaoException, SQLException;
}

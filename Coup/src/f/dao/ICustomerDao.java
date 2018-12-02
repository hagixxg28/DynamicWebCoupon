package f.dao;

import java.util.Collection;

import b.exceptions.DaoExceptions.DaoException;
import d.beanShells.Coupon;
import d.beanShells.Customer;

public interface ICustomerDao {
	void createCustomer(Customer cust) throws DaoException;

	void removeCustomer(Customer cust) throws DaoException;

	void updateCustomer(Customer cust) throws DaoException;

	Customer getCustomer(Customer cust) throws DaoException;

	Collection<Customer> getAllCustomer() throws DaoException;

	Collection<Customer> getAllCustomerWithCoupons() throws DaoException;

	Boolean login(Long id, String password) throws DaoException;

	Collection<Coupon> getCoupons(Customer cust) throws DaoException;

	Boolean customerExists(Customer cust) throws DaoException;

}

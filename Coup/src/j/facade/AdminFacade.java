package j.facade;

import java.util.Collection;

import b.exceptions.DaoExceptions.CompanyAlreadyExistsException;
import b.exceptions.DaoExceptions.CompanyDoesNotExistException;
import b.exceptions.DaoExceptions.CustomerAlreadyExistsException;
import b.exceptions.DaoExceptions.CustomerDoesNotExistException;
import b.exceptions.DaoExceptions.DaoException;
import b.exceptions.DaoExceptions.NoCompaniesException;
import b.exceptions.DaoExceptions.NoCustomersException;
import b.exceptions.FacadeExceptions.FacadeException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import f.dao.ICompanyDao;
import f.dao.ICouponDao;
import f.dao.ICustomerDao;
import g.daoDB.CompanyDao;
import g.daoDB.CouponDao;
import g.daoDB.CustomerDao;

public class AdminFacade implements CouponClientFacade {

	private ICustomerDao custDb = new CustomerDao();
	private ICompanyDao compDb = new CompanyDao();
	private ICouponDao coupDb = new CouponDao();

	public AdminFacade() {

	}

	public void createCompany(Company comp) throws FacadeException {
		try {
			if (!compDb.companyExists(comp)) {
				compDb.createCompany(comp);
			} else {
				throw new CompanyAlreadyExistsException("A company with this name already exists");
			}
		} catch (DaoException e) {
			throw new FacadeException("Something went wrong");
		}
	}

	public void removeCompany(Company comp) throws FacadeException {
		try {
			if (compDb.companyExists(comp)) {
				compDb.removeCompany(comp);
				for (Coupon coup : compDb.getAllCoupons(comp)) {
					coupDb.fullyRemoveCoupon(coup);
				}
			} else {
				throw new CompanyDoesNotExistException("This company dosen't exist");
			}
		} catch (DaoException e) {
			throw new FacadeException("Company was not found");
		}
	}

	public void updateCompany(Company comp) throws FacadeException {
		try {
			if (compDb.companyExists(comp)) {
				compDb.updateCompany(comp);
			} else {
				throw new CompanyDoesNotExistException("This company dosen't exist");
			}
		} catch (DaoException e) {
			throw new FacadeException("Company was not found");
		}
	}

	public Company getCompany(Company comp) throws FacadeException {
		try {
			if (compDb.companyExists(comp)) {
				return compDb.readCompany(comp);
			} else {
				throw new CompanyDoesNotExistException("This company dosen't exist");
			}
		} catch (DaoException e) {
			throw new FacadeException("Company was not found");
		}
	}

	public Collection<Company> getAllCompanies() throws FacadeException {
		try {
			if (!compDb.getAllCompanies().isEmpty()) {
				return compDb.getAllCompanies();
			} else {
				throw new NoCompaniesException("There are no companies on the database");
			}
		} catch (DaoException e) {
			throw new FacadeException("No companies were found");
		}
	}

	public void createCustomer(Customer cust) throws FacadeException {
		try {
			if (!custDb.customerExists(cust)) {
				custDb.createCustomer(cust);
			} else {
				throw new CustomerAlreadyExistsException("A customer with this id already exists");
			}
		} catch (DaoException e) {
			throw new FacadeException("A customer with this id already exists");
		}
	}

	public void removeCustomer(Customer cust) throws FacadeException {
		try {
			if (custDb.customerExists(cust)) {
				custDb.removeCustomer(cust);
				if (!cust.getCouponHistory().isEmpty()) {
					cust.getCouponHistory().clear();
				}
			} else {
				throw new CustomerDoesNotExistException("This customer does not exist");
			}
		} catch (DaoException e) {
			throw new FacadeException("This customer does not exist");
		}

	}

	public void updateCustomer(Customer cust) throws FacadeException {
		try {
			if (custDb.customerExists(cust)) {
				custDb.updateCustomer(cust);
			} else {
				throw new CustomerDoesNotExistException("This customer does not exist");
			}
		} catch (DaoException e) {
			throw new FacadeException("This customer does not exist");
		}
	}

	public Customer getCustomer(Customer cust) throws FacadeException {
		try {
			if (custDb.customerExists(cust)) {
				return custDb.getCustomer(cust);
			} else {
				throw new CustomerDoesNotExistException("This customer does not exist");
			}
		} catch (DaoException e) {
			throw new FacadeException("Something went wrong");
		}
	}

	public Collection<Customer> getAllCustomer() throws FacadeException {
		try {
			if (!custDb.getAllCustomer().isEmpty()) {
				return custDb.getAllCustomer();
			} else {
				throw new NoCustomersException("there are no customers on the database");
			}
		} catch (DaoException e) {
			throw new FacadeException("There are no customers on the Database");
		}
	}
}

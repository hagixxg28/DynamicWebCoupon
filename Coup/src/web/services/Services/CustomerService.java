package web.services.Services;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import b.exceptions.FacadeExceptions.FacadeException;
import d.beanShells.Coupon;
import j.facade.CustomerFacade;

@Path("Customer")
public class CustomerService {
	@Context
	HttpServletRequest req;

	@Context
	HttpServletResponse res;

	@Path("Coupons")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCoupons() throws FacadeException {
		Collection<Coupon> collection = null;
		HttpSession session = req.getSession(false);
		CustomerFacade customerFacade = (CustomerFacade) session.getAttribute("facade");
		collection = customerFacade.getAllCoupons();
		return collection;
	}

}

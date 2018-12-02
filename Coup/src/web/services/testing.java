package web.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import b.exceptions.FacadeExceptions.FacadeException;
import j.facade.AdminFacade;
import k.couponSystem.CouponSystem;

@Path("hello")
public class testing {
	private CouponSystem sys = CouponSystem.getInstance();
	private AdminFacade admin = new AdminFacade();

	// http://localhost:8080/Coup/rest/hello/test
	@Path("test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll() {
		try {
			return admin.getAllCustomer().toString();
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

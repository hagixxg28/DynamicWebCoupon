package i.threads;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import b.exceptions.DaoExceptions.DaoException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Coupon;
import f.dao.ICouponDao;
import g.daoDB.CouponDao;

public class DailyCouponExpirationTask implements Runnable {

	private ICouponDao coupDb = new CouponDao();
	private boolean quit = false;

	public DailyCouponExpirationTask() {
	}

	@Override
	public void run() {
		// USE only DAO commands and remove the SQL shell from here
		while (!quit) {
			Collection<Long> list = new ArrayList<>();
			try {
				try {
					list = coupDb.getAllExpiredCoupons();
				} catch (DaoException e1) {
					System.err.println("Failed to run get existing expired coupon at thread");
					e1.printStackTrace();
				}
				if (!list.isEmpty()) {
					System.out.println("Found " + list.size() + " coupons to delete, deleting.");
					for (Long long1 : list) {
						Coupon coup = new Coupon();
						coup.setId(long1);
						try {
							coupDb.fullyRemoveCoupon(coup);
						} catch (DaoException e) {
							System.err.println("Failed to delete existing coupon at thread");
							e.printStackTrace();
						}
					}
					System.out.println("Finished deleting, going to sleep");
					Thread.sleep(86400000);
				} else {
					System.out.println("No coupons to delete, going to sleep");
					Thread.sleep(86400000);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread has been interrupted, shutting down");
			} finally {
				System.out.println(Thread.currentThread().getName() + " is closing");
			}
		}
	}

	public void stop() {
		quit = true;
		Thread.currentThread().interrupt();
	}

}

package edu.hcmute.webpr.util;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Gỡ JDBC driver của ứng dụng khi Tomcat nạp lại WAR. */
@WebListener
public class JdbcDriverCleanup_24133059 implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        ClassLoader applicationLoader = getClass().getClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == applicationLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    event.getServletContext().log("Không thể gỡ JDBC driver khi dừng ứng dụng", e);
                }
            }
        }
    }
}

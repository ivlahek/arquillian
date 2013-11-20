package org.arquillian.example.jpaexample;

import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

/**
 * @author ivlahek
 */
public class LocalContextFactory {
    private LocalContextFactory() {
    }

    static {
        try {
            createLocalContext();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LocalContext createLocalContext() throws NamingException, ClassNotFoundException {
        LocalContext ctx = new LocalContext();
        Class.forName("com.mysql.jdbc.Driver");
        NamingManager.setInitialContextFactoryBuilder(ctx);
        return ctx;

    }

}

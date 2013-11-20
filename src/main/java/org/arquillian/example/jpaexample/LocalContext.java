package org.arquillian.example.jpaexample;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.naming.*;
import javax.naming.ldap.LdapName;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import java.util.*;

/**
 * @author ivlahek
 */
class LocalContext extends InitialContext implements InitialContextFactoryBuilder, InitialContextFactory {

    Map<Object, Object> dataSources;

    LocalContext() throws NamingException {
        super();
        dataSources = new HashMap<Object, Object>();
    }

    public void addDataSource() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName("test");
        mysqlDataSource.setPassword("0000");
        mysqlDataSource.setPort(3306);
        mysqlDataSource.setUser("root");
        mysqlDataSource.setServerName("127.0.0.1");

        dataSources.put("jdbc/arquillian", mysqlDataSource);
    }

    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> hsh) throws NamingException {
        dataSources.putAll(hsh);
        return this;
    }

    public Context getInitialContext(Hashtable<?, ?> arg0) throws NamingException {
        return this;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName("test");
        mysqlDataSource.setPassword("0000");
        mysqlDataSource.setPort(3306);
        mysqlDataSource.setUser("root");
        mysqlDataSource.setServerName("127.0.0.1");
        return mysqlDataSource;
    }

    public NameParser getNameParser(String name) {
        return new NameParser() {
            @Override
            public Name parse(String name) throws NamingException {
                return new CompoundName(name, new Properties());
            }
        };
    }

}
package org.arquillian.example.jpaexample;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;

/**
 * @author ivlahek
 */
@ApplicationScoped
public class EntityManagerProducer {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @PostConstruct
    public void initialize() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test");
        createEntityManager();
    }

    @PreDestroy
    public void contextDestroyed(ServletContextEvent sce) {
        entityManagerFactory.close();
    }

    @Produces
    public EntityManager createEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("Context is not initialized yet.");
        }
        entityManager = entityManagerFactory.createEntityManager();
        return this.entityManager;
    }
}

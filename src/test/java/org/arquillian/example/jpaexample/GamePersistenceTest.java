package org.arquillian.example.jpaexample;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.*;


/**
 * @author ivlahek
 */
@RunWith(Arquillian.class)
public class GamePersistenceTest {
    private static final String[] GAME_TITLES = {"Super Mario Brothers", "Mario Kart", "F-Zero"};
    @Inject
    EntityManager em;

    @PersistenceContext
    EntityManager em1;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] files = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importDependencies(ScopeType.COMPILE)
                .resolve()
                .withTransitivity()
                .asFile();

        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test2.war")
                .addAsLibraries(files)
                .addPackage(Game.class.getPackage())
                .addAsManifestResource("persistence.xml", "persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println(webArchive.toString(true));
        return webArchive;
    }

    private static void assertContainsAllGames(Collection<Game> retrievedGames) {
        Assert.assertEquals(GAME_TITLES.length, retrievedGames.size());
        final Set<String> retrievedGameTitles = new HashSet<String>();
        for (Game game : retrievedGames) {
            System.out.println("* " + game);
            retrievedGameTitles.add(game.getTitle());
        }
        Assert.assertTrue(retrievedGameTitles.containsAll(Arrays.asList(GAME_TITLES)));
    }

    @Before
    public void preparePersistenceTest() throws Exception {
//        LocalContext ctx = LocalContextFactory.createLocalContext();
//        ctx.addDataSource();
        clearData();
        insertData();
        startTransaction();
    }

    @After
    public void commitTransaction() throws Exception {
        em.getTransaction().commit();
    }

    @Test
    @UsingDataSet("test.xml")
//    @DataSource("jdbc/arquillian")
    public void shouldFindAllGamesUsingJpqlQuery() throws Exception {
        // given
        String fetchingAllGamesInJpql = "select g from Game g order by g.id";

        // when
        System.out.println("Selecting (using JPQL)...");
        List<Game> games = em.createQuery(fetchingAllGamesInJpql, Game.class).getResultList();

        // then
        System.out.println("Found " + games.size() + " games (using JPQL):");
        assertContainsAllGames(games);
    }

    @Test
    public void shouldFindAllGamesUsingCriteriaApi() throws Exception {
        // given
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Game> criteria = builder.createQuery(Game.class);

        Root<Game> game = criteria.from(Game.class);
        criteria.select(game);
        // TIP: If you don't want to use the JPA 2 Metamodel,
        // you can change the get() method call to get("id")
        criteria.orderBy(builder.asc(game.get(Game_.id)));
        // No WHERE clause, which implies select all

        // when
        System.out.println("Selecting (using Criteria)...");
        List<Game> games = em.createQuery(criteria).getResultList();

        // then
        System.out.println("Found " + games.size() + " games (using Criteria):");
        assertContainsAllGames(games);
    }

    private void clearData() throws Exception {
        em.getTransaction().begin();
        em.joinTransaction();
        System.out.println("Dumping old records...");
        em.createQuery("delete Game g where g.id > 0").executeUpdate();
        em.getTransaction().commit();
    }

    private void insertData() throws Exception {
        em.getTransaction().begin();
        em.joinTransaction();
        System.out.println("Inserting records...");
        for (String title : GAME_TITLES) {
            Game game = new Game(title);
            em.persist(game);
        }
        em.getTransaction().commit();
        // clear the persistence context (first-level cache)
        em.clear();
    }

    private void startTransaction() throws Exception {
        em.getTransaction().begin();
        em.joinTransaction();
    }
}
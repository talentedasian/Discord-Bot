package botCommands.botMemberJoin;

import org.hibernate.cfg.Configuration;

public class SessionFactory {

    private static org.hibernate.SessionFactory sessionFactory;

    public SessionFactory() {
    }

    static {
        try {
            // Build a SessionFactory object from session-factory config
            // defined in the hibernate.cfg.xml file. In this file we
            // register the JDBC connection information, connection pool,
            // the hibernate dialect that we used and the mapping to our
            // hbm.xml file for each pojo (plain old java object).
            Configuration config = new Configuration();
            sessionFactory = config.configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Error in creating SessionFactory object."
                    + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static org.hibernate.SessionFactory getSessionFactory() {
        return sessionFactory;

    }

}

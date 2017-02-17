package br.com.maxig.model.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Versão do hibernate 5
 * @author gustavo
 *
 */
public class HibernateUtil {
    
    private static SessionFactory sessionFactory;

    static {
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        Metadata metadata = new MetadataSources(standardRegistry)
//                .addAnnotatedClass(Pessoa.class)
//                .addAnnotatedClassName("org.hibernate.example.Customer")
//                .addResource("org/hibernate/example/Order.hbm.xml")
//                .addResource("org/hibernate/example/Product.orm.xml")
                .getMetadataBuilder()
//                .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
                .build();

        SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();

        // Supply an SessionFactory-level Interceptor
        //sessionFactoryBuilder.applyInterceptor(new CustomSessionFactoryInterceptor());

        // Add a custom observer
        //sessionFactoryBuilder.addSessionFactoryObservers(new CustomSessionFactoryObserver());

        // Apply a CDI BeanManager ( for JPA event listeners )
        //sessionFactoryBuilder.applyBeanManager(getBeanManager());

        sessionFactory = sessionFactoryBuilder.build();
    }
    
    public static Session getSession(){
//        return sessionFactory.withOptions().interceptor(new LoggingInterceptor()).openSession();
    	return sessionFactory.openSession();
    }

}

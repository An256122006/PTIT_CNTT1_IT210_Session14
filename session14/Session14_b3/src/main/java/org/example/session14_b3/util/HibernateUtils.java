package org.example.session14_b3.util;


import org.example.session14_b3.model.Product;
import org.example.session14_b3.model.Order;

import org.hibernate.SessionFactory;

import org.hibernate.cfg.Configuration;


public class HibernateUtils {
    private static final SessionFactory factory;

    static {
        factory = new Configuration()
                .configure()
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();

    }

    public static SessionFactory getFactory(){
        return factory;
    }

}

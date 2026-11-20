package org.example.model;

import org.example.database.DatabaseConnection;
import org.example.exceptions.EventNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class HibernateEventDAO {

    private final DatabaseConnection databaseConnection;
    private SessionFactory sessionFactory;

    public HibernateEventDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void initialize() throws HibernateException {
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", databaseConnection.getUrl());
        configuration.setProperty("hibernate.connection.username", databaseConnection.getUser());
        configuration.setProperty("hibernate.connection.password", getDatabasePassword());
        sessionFactory = configuration.buildSessionFactory();
    }

    public Event addEvent(Event event) throws HibernateException {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(event);
                transaction.commit();
                return event;
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
    }

    public List<Event> getAllEvents() throws HibernateException {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("from Event order by id", Event.class).getResultList();
        }
    }

    public Event findEventById(int id) throws HibernateException, EventNotFoundException {
        try (Session session = getSessionFactory().openSession()) {
            Event event = session.get(Event.class, id);
            if (event == null) {
                throw new EventNotFoundException("Aucun événement trouvé avec l'identifiant " + id + ".");
            }
            return event;
        }
    }

    public List<Event> findEventsByName(String name) throws HibernateException {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Event where lower(name) = lower(:name) order by id",
                            Event.class
                    )
                    .setParameter("name", name)
                    .getResultList();
        }
    }

    public Event updateEvent(Event updatedEvent) throws HibernateException, EventNotFoundException {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Event event = session.get(Event.class, updatedEvent.getId());
                if (event == null) {
                    throw new EventNotFoundException(
                            "Aucun événement trouvé avec l'identifiant " + updatedEvent.getId() + "."
                    );
                }
                event.setName(updatedEvent.getName());
                event.setDescription(updatedEvent.getDescription());
                event.setDate(updatedEvent.getDate());
                event.setLocation(updatedEvent.getLocation());
                transaction.commit();
                return event;
            } catch (RuntimeException | EventNotFoundException exception) {
                rollback(transaction);
                throw exception;
            }
        }
    }

    public void deleteEvent(int id) throws HibernateException, EventNotFoundException {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Event event = session.get(Event.class, id);
                if (event == null) {
                    throw new EventNotFoundException("Aucun événement trouvé avec l'identifiant " + id + ".");
                }
                session.remove(event);
                transaction.commit();
            } catch (RuntimeException | EventNotFoundException exception) {
                rollback(transaction);
                throw exception;
            }
        }
    }

    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    private SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new IllegalStateException("Hibernate n'a pas été initialisé.");
        }
        return sessionFactory;
    }

    private String getDatabasePassword() {
        try {
            return databaseConnection.getPassword();
        } catch (java.sql.SQLException exception) {
            throw new HibernateException(exception.getMessage(), exception);
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}

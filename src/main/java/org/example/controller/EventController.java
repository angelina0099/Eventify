package org.example.controller;

import org.example.exceptions.EventNotFoundException;
import org.example.database.DatabaseConnection;
import org.example.files.EventFileManager;
import org.example.model.Event;
import org.example.model.EventDAO;
import org.example.model.HibernateEventDAO;
import org.example.model.JdbcEventDAO;
import org.example.view.EventView;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;

public class EventController {

    private final EventView view;
    private final EventDAO eventDAO;
    private final EventFileManager fileManager;
    private final JdbcEventDAO jdbcEventDAO;
    private final HibernateEventDAO hibernateEventDAO;

    public EventController(EventView view, EventDAO eventDAO, EventFileManager fileManager,
                           JdbcEventDAO jdbcEventDAO, HibernateEventDAO hibernateEventDAO) {
        this.view = view;
        this.eventDAO = eventDAO;
        this.fileManager = fileManager;
        this.jdbcEventDAO = jdbcEventDAO;
        this.hibernateEventDAO = hibernateEventDAO;
    }

    public void start() {
        String choice;

        do {
            view.displayMenu();
            choice = view.readChoice();

            switch (choice) {
                case "1":
                    addEvent();
                    break;
                case "2":
                    view.displayEvents(eventDAO.getAllEvents());
                    break;
                case "3":
                    searchEvents();
                    break;
                case "4":
                    updateEvent();
                    break;
                case "5":
                    deleteEvent();
                    break;
                case "6":
                    filterEvents();
                    break;
                case "7":
                    sortEvents();
                    break;
                case "8":
                    saveEventsToFile();
                    break;
                case "9":
                    loadEventsFromFile();
                    break;
                case "10":
                    manageEventsWithJdbc();
                    break;
                case "11":
                    manageEventsWithHibernate();
                    break;
                case "0":
                    break;
                default:
                    view.displayMessage("Choix invalide. Veuillez réessayer.");
            }
        } while (!"0".equals(choice));

        view.displayGoodbyeMessage();
    }

    private void addEvent() {
        Event event = eventDAO.addEvent(
                view.readRequiredText("Nom : "),
                view.readRequiredText("Description : "),
                view.readDate("Date (AAAA-MM-JJ) : "),
                view.readRequiredText("Lieu : ")
        );
        view.displayMessage("Événement ajouté : " + event);
    }

    private void searchEvents() {
        if (view.readSearchType() == 1) {
            int id = view.readPositiveInteger("Identifiant : ");
            try {
                view.displayMessage(eventDAO.findEventById(id).toString());
            } catch (EventNotFoundException exception) {
                view.displayMessage(exception.getMessage());
            }
        } else {
            String name = view.readRequiredText("Nom : ");
            view.displayEvents(eventDAO.findEventsByName(name));
        }
    }

    private void updateEvent() {
        int id = view.readPositiveInteger("Identifiant de l'événement à modifier : ");
        String name = view.readRequiredText("Nouveau nom : ");
        String description = view.readRequiredText("Nouvelle description : ");
        LocalDate date = view.readDate("Nouvelle date (AAAA-MM-JJ) : ");
        String location = view.readRequiredText("Nouveau lieu : ");

        try {
            Event event = eventDAO.updateEvent(id, name, description, date, location);
            view.displayMessage("Événement modifié : " + event);
        } catch (EventNotFoundException exception) {
            view.displayMessage(exception.getMessage());
        }
    }

    private void deleteEvent() {
        int id = view.readPositiveInteger("Identifiant de l'événement à supprimer : ");
        try {
            eventDAO.deleteEvent(id);
            view.displayMessage("Événement supprimé.");
        } catch (EventNotFoundException exception) {
            view.displayMessage(exception.getMessage());
        }
    }

    private void filterEvents() {
        String location = view.readLocationFilter();
        view.displayEvents(eventDAO.filterEventsByLocation(location));
    }

    private void sortEvents() {
        if (view.readSortType() == 1) {
            view.displayEvents(eventDAO.sortEventsByName());
        } else {
            view.displayEvents(eventDAO.sortEventsByDate());
        }
    }

    private void saveEventsToFile() {
        try {
            fileManager.saveEvents(eventDAO.getAllEvents(), Path.of("events.txt"));
            view.displayMessage("Événements enregistrés dans events.txt.");
        } catch (IOException exception) {
            view.displayMessage("Erreur lors de l'enregistrement du fichier : " + exception.getMessage());
        }
    }

    private void loadEventsFromFile() {
        try {
            eventDAO.replaceEvents(fileManager.loadEvents(Path.of("events.txt")));
            view.displayMessage("Événements chargés depuis events.txt.");
        } catch (IOException exception) {
            view.displayMessage("Erreur lors de la lecture du fichier : " + exception.getMessage());
        }
    }

    private void manageEventsWithJdbc() {
        try {
            jdbcEventDAO.createTableIfNeeded();
        } catch (SQLException exception) {
            view.displayMessage("Connexion JDBC impossible : " + exception.getMessage());
            return;
        }

        String choice;
        do {
            view.displayJdbcMenu();
            choice = view.readChoice();
            try {
                switch (choice) {
                    case "1":
                        addJdbcEvent();
                        break;
                    case "2":
                        view.displayEvents(jdbcEventDAO.getAllEvents());
                        break;
                    case "3":
                        searchJdbcEvents();
                        break;
                    case "4":
                        updateJdbcEvent();
                        break;
                    case "5":
                        deleteJdbcEvent();
                        break;
                    case "0":
                        break;
                    default:
                        view.displayMessage("Choix invalide. Veuillez réessayer.");
                }
            } catch (SQLException exception) {
                view.displayMessage("Erreur JDBC : " + exception.getMessage());
            }
        } while (!"0".equals(choice));
    }

    private void addJdbcEvent() throws SQLException {
        Event event = new Event(
                0,
                view.readRequiredText("Nom : "),
                view.readRequiredText("Description : "),
                view.readDate("Date (AAAA-MM-JJ) : "),
                view.readRequiredText("Lieu : ")
        );
        view.displayMessage("Événement ajouté dans PostgreSQL : " + jdbcEventDAO.addEvent(event));
    }

    private void searchJdbcEvents() throws SQLException {
        if (view.readSearchType() == 1) {
            int id = view.readPositiveInteger("Identifiant : ");
            try {
                view.displayMessage(jdbcEventDAO.findEventById(id).toString());
            } catch (EventNotFoundException exception) {
                view.displayMessage(exception.getMessage());
            }
        } else {
            String name = view.readRequiredText("Nom : ");
            view.displayEvents(jdbcEventDAO.findEventsByName(name));
        }
    }

    private void updateJdbcEvent() throws SQLException {
        int id = view.readPositiveInteger("Identifiant de l'événement à modifier : ");
        Event event = new Event(
                id,
                view.readRequiredText("Nouveau nom : "),
                view.readRequiredText("Nouvelle description : "),
                view.readDate("Nouvelle date (AAAA-MM-JJ) : "),
                view.readRequiredText("Nouveau lieu : ")
        );
        try {
            view.displayMessage("Événement modifié : " + jdbcEventDAO.updateEvent(event));
        } catch (EventNotFoundException exception) {
            view.displayMessage(exception.getMessage());
        }
    }

    private void deleteJdbcEvent() throws SQLException {
        int id = view.readPositiveInteger("Identifiant de l'événement à supprimer : ");
        try {
            jdbcEventDAO.deleteEvent(id);
            view.displayMessage("Événement supprimé de PostgreSQL.");
        } catch (EventNotFoundException exception) {
            view.displayMessage(exception.getMessage());
        }
    }

    private void manageEventsWithHibernate() {
        try {
            hibernateEventDAO.initialize();
        } catch (HibernateException exception) {
            view.displayMessage("Initialisation Hibernate impossible : " + exception.getMessage());
            return;
        }

        try {
            String choice;
            do {
                view.displayHibernateMenu();
                choice = view.readChoice();
                try {
                    switch (choice) {
                        case "1":
                            addHibernateEvent();
                            break;
                        case "2":
                            view.displayEvents(hibernateEventDAO.getAllEvents());
                            break;
                        case "3":
                            searchHibernateEvents();
                            break;
                        case "4":
                            updateHibernateEvent();
                            break;
                        case "5":
                            deleteHibernateEvent();
                            break;
                        case "0":
                            break;
                        default:
                            view.displayMessage("Choix invalide. Veuillez réessayer.");
                    }
                } catch (HibernateException exception) {
                    view.displayMessage("Erreur Hibernate : " + exception.getMessage());
                }
            } while (!"0".equals(choice));
        } finally {
            hibernateEventDAO.close();
        }
    }

    private void addHibernateEvent() {
        Event event = new Event(
                0,
                view.readRequiredText("Nom : "),
                view.readRequiredText("Description : "),
                view.readDate("Date (AAAA-MM-JJ) : "),
                view.readRequiredText("Lieu : ")
        );
        view.displayMessage("Événement ajouté avec Hibernate : " + hibernateEventDAO.addEvent(event));
    }

    private void searchHibernateEvents() {
        if (view.readSearchType() == 1) {
            int id = view.readPositiveInteger("Identifiant : ");
            try {
                view.displayMessage(hibernateEventDAO.findEventById(id).toString());
            } catch (EventNotFoundException exception) {
                view.displayMessage(exception.getMessage());
            }
        } else {
            String name = view.readRequiredText("Nom : ");
            view.displayEvents(hibernateEventDAO.findEventsByName(name));
        }
    }

    private void updateHibernateEvent() {
        int id = view.readPositiveInteger("Identifiant de l'événement à modifier : ");
        Event event = new Event(
                id,
                view.readRequiredText("Nouveau nom : "),
                view.readRequiredText("Nouvelle description : "),
                view.readDate("Nouvelle date (AAAA-MM-JJ) : "),
                view.readRequiredText("Nouveau lieu : ")
        );
        try {
            view.displayMessage("Événement modifié : " + hibernateEventDAO.updateEvent(event));
        } catch (EventNotFoundException exception) {
            view.displayMessage(exception.getMessage());
        }
    }

    private void deleteHibernateEvent() {
        int id = view.readPositiveInteger("Identifiant de l'événement à supprimer : ");
        try {
            hibernateEventDAO.deleteEvent(id);
            view.displayMessage("Événement supprimé avec Hibernate.");
        } catch (EventNotFoundException exception) {
            view.displayMessage(exception.getMessage());
        }
    }
}

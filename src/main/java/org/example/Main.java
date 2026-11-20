package org.example;

import org.example.controller.EventController;
import org.example.database.DatabaseConnection;
import org.example.files.EventFileManager;
import org.example.model.EventDAO;
import org.example.model.HibernateEventDAO;
import org.example.model.JdbcEventDAO;
import org.example.view.EventView;

public class Main {

    public static void main(String[] args) {
        EventView view = new EventView();
        EventDAO eventDAO = new EventDAO();
        EventFileManager fileManager = new EventFileManager();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        JdbcEventDAO jdbcEventDAO = new JdbcEventDAO(databaseConnection);
        HibernateEventDAO hibernateEventDAO = new HibernateEventDAO(databaseConnection);
        EventController controller = new EventController(
                view, eventDAO, fileManager, jdbcEventDAO, hibernateEventDAO
        );
        controller.start();
    }
}

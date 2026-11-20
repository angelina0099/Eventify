package org.example.view;

import org.example.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class EventView {

    private final Scanner scanner = new Scanner(System.in);

    public void displayMenu() {
        System.out.println("========================================");
        System.out.println("              EVENTIFY");
        System.out.println("          EVENT MANAGEMENT");
        System.out.println("========================================");
        System.out.println("1. Ajouter un événement");
        System.out.println("2. Afficher tous les événements");
        System.out.println("3. Rechercher un événement");
        System.out.println("4. Modifier un événement");
        System.out.println("5. Supprimer un événement");
        System.out.println("6. Filtrer les événements");
        System.out.println("7. Trier les événements");
        System.out.println("8. Enregistrer les événements dans un fichier");
        System.out.println("9. Lire les événements depuis un fichier");
        System.out.println("10. Gestion avec JDBC");
        System.out.println("11. Gestion avec Hibernate");
        System.out.println("0. Quitter");
        System.out.print("\nVotre choix : ");
    }

    public void displayJdbcMenu() {
        System.out.println("\n--- Gestion des événements avec JDBC ---");
        System.out.println("1. Ajouter un événement");
        System.out.println("2. Afficher tous les événements");
        System.out.println("3. Rechercher un événement");
        System.out.println("4. Modifier un événement");
        System.out.println("5. Supprimer un événement");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix : ");
    }

    public void displayHibernateMenu() {
        System.out.println("\n--- Gestion des événements avec Hibernate ---");
        System.out.println("1. Ajouter un événement");
        System.out.println("2. Afficher tous les événements");
        System.out.println("3. Rechercher un événement");
        System.out.println("4. Modifier un événement");
        System.out.println("5. Supprimer un événement");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix : ");
    }

    public String readChoice() {
        return scanner.nextLine();
    }

    public int readPositiveInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // The prompt is repeated below for invalid input.
            }
            System.out.println("Veuillez saisir un entier positif.");
        }
    }

    public String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Cette valeur ne peut pas être vide.");
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException exception) {
                System.out.println("Date invalide. Utilisez le format AAAA-MM-JJ.");
            }
        }
    }

    public void displayEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("Aucun événement à afficher.");
            return;
        }
        events.forEach(System.out::println);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public int readSearchType() {
        while (true) {
            System.out.println("1. Rechercher par identifiant");
            System.out.println("2. Rechercher par nom");
            System.out.print("Votre choix : ");
            String choice = scanner.nextLine();
            if ("1".equals(choice)) {
                return 1;
            }
            if ("2".equals(choice)) {
                return 2;
            }
            System.out.println("Choix invalide. Saisissez 1 ou 2.");
        }
    }

    public String readLocationFilter() {
        return readRequiredText("Lieu à rechercher : ");
    }

    public int readSortType() {
        while (true) {
            System.out.println("1. Trier par nom");
            System.out.println("2. Trier par date");
            System.out.print("Votre choix : ");
            String choice = scanner.nextLine();
            if ("1".equals(choice)) {
                return 1;
            }
            if ("2".equals(choice)) {
                return 2;
            }
            System.out.println("Choix invalide. Saisissez 1 ou 2.");
        }
    }

    public void displayUpcomingFeatureMessage() {
        System.out.println("Cette fonctionnalité sera ajoutée lors d'une prochaine étape.");
    }

    public void displayGoodbyeMessage() {
        System.out.println("Au revoir !");
    }
}

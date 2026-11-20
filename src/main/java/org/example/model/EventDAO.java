package org.example.model;

import org.example.exceptions.EventNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventDAO {

    private final ArrayList<Event> events = new ArrayList<>();
    private int nextId = 1;

    public Event addEvent(String name, String description, LocalDate date, String location) {
        Event event = new Event(nextId, name, description, date, location);
        events.add(event);
        nextId++;
        return event;
    }

    public ArrayList<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    public void replaceEvents(List<Event> loadedEvents) {
        events.clear();
        events.addAll(loadedEvents);
        nextId = events.stream()
                .mapToInt(Event::getId)
                .max()
                .orElse(0) + 1;
    }

    public Event findEventById(int id) throws EventNotFoundException {
        for (Event event : events) {
            if (event.getId() == id) {
                return event;
            }
        }
        throw new EventNotFoundException("Aucun événement trouvé avec l'identifiant " + id + ".");
    }

    public ArrayList<Event> findEventsByName(String name) {
        return events.stream()
                .filter(event -> event.getName().equalsIgnoreCase(name))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Event> filterEventsByLocation(String location) {
        return events.stream()
                .filter(event -> event.getLocation().toLowerCase().contains(location.toLowerCase()))
                .toList();
    }

    public List<Event> sortEventsByName() {
        return events.stream()
                .sorted(Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Event> sortEventsByDate() {
        return events.stream()
                .sorted(Comparator.comparing(Event::getDate))
                .toList();
    }

    public Event updateEvent(int id, String name, String description, LocalDate date, String location)
            throws EventNotFoundException {
        Event event = findEventById(id);
        event.setName(name);
        event.setDescription(description);
        event.setDate(date);
        event.setLocation(location);
        return event;
    }

    public void deleteEvent(int id) throws EventNotFoundException {
        Event event = findEventById(id);
        events.remove(event);
    }
}

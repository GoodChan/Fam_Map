package Shared.Server.Requests;

import Shared.Server.Model.Event;
import Shared.Server.Model.Person;
import Shared.Server.Model.User;

public class LoadRequest {
    User users[];
    Person persons[];
    Event events[];

    /**
     * Constructs a /load response of users, persons and events.
     * @param users
     * @param persons
     * @param events
     */
    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}


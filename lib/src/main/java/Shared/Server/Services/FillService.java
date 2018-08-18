package Shared.Server.Services;

import Shared.Server.Model.Event;
import Shared.Server.Model.Person;
import Shared.Server.Model.User;
import Shared.Server.DAO.Database;
import Shared.Server.DAO.EventDAO;
import Shared.Server.DAO.PersonDAO;
import Shared.Server.DAO.UserDAO;
import com.google.gson.Gson;
import Shared.Server.Responses.Response;
import Shared.Server.Responses.MessageResponse;

import java.io.*;
import java.sql.*;
import java.lang.Math;

public class FillService extends SuperServices {

    /**
     * URL Path: /fill/[username]/{generations}
     * Example: /fill/susan/3
     * Description: Populates the server's database with generated data for the specified user name.
     * The required "username" parameter must be a user already registered with the server. If there
     * is any data in the database already associated with the given user name, it is deleted. The
     * optional generations parameter lets the caller specify the number of generations of ancestors
     * to be generated, and must be a non-negative integer (the default is 4, which results in 31 new
     * persons each with associated events).
     * HTTP Method: POST
     * Auth Token Required: No
     * Request Body: None
     * Errors: Invalid username or generations parameter, Internal server error
     *
     * @param userName
     * @param generations
     * @return
     * @throws internalServerError
     */

    private final int CURR_YEAR = 2018;
    public Response fill(String userName, int generations) {
        int GENERATION_SPACE = 26;
        Database db = new Database();
        Filler filler = new Filler(); //class that sets up and manages the files with names and locations
        boolean willCommit = true;

        try {
            filler.Filler(); //sets up names
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            try {
                db.openConnection();
                //checks user exists, if not throws exception
                User currUser = new UserDAO().readUser(userName, db.getConn());
                //removes user's events and people
                new PersonDAO().deleteUserInfo(userName, db.getConn());
                new EventDAO().deleteUserEvents(userName, db.getConn());
                //create a mother and father at the same time
                ParentPair parents[] = new ParentPair[2];
                /*parents[0] = new ParentPair("", "");
                parents[1] = new ParentPair("", "");
                parents[2] = new ParentPair("", "");
                parents[3] = new ParentPair("", "");*/
                //the first generation's birth date:  current year(2018) - (generations to fill * amount of space between generations)
                int date = CURR_YEAR - (generations * GENERATION_SPACE);

                //generates oldest to youngest by layer in a tree, like a reverse breadth search
                for (int i = (generations - 1); i >= 0; --i) {
                    //creates and adds for this layer
                    parents = fillWithFile(userName, (double)i, db.getConn(), filler, parents, date);
                    date = date + GENERATION_SPACE; //children are born 26 years(GENERATION_SPACE) later
                }

                //manually adds the current user
                Person userPerson = new Person(currUser.getPersonID(), currUser.getUserName(), currUser.getFirstName(), currUser.getLastName(),
                        currUser.getGender(), parents[0].getFather(), parents[0].getMother(), "");
                new PersonDAO().createPerson(userPerson, db.getConn());

                willCommit = true;
                return new MessageResponse("Successfully added " + filler.getNumPeopleGenerated() + " people and "
                        + filler.getNumEventsGenerated() + " Events to the database."); //TODO check how many events and people are created is correct
            } catch (Database.DatabaseException e) {
                e.printStackTrace();
                willCommit = false;
                MessageResponse messageResponse = new MessageResponse(e.getMessage());
                return messageResponse;
            }
            finally {
                db.closeConnection(willCommit);
            }
        } catch (Database.DatabaseException ex) {
            MessageResponse messageResponse = new MessageResponse("Internal Server Error");
            return messageResponse;
        }
    }

    /*
    generate names based on given file by converting from Json using Gson
    make sure parents are m f pairs and not siblings
    make one male and one female with a marriage event per generation count of i
     */

    private ParentPair[] fillWithFile(String userName, double generation, Connection conn, Filler filler, ParentPair[] parents, int referenceDate) throws Database.DatabaseException {
        final int AGE_WHEN_MARRIED = 22;
        final int DEATH_AGE = 80;
        final int NUM_EVENTS_ADDED = 6;
        String motherOfMale = "";
        String fatherOfMale = "";
        String motherOfFemale = "";
        String fatherOfFemale = "";

        int numPairs = (int)(Math.pow(2, generation)); //finds the number of couples that needed generating for this layer
        ParentPair newParents[] = new ParentPair[numPairs];
        int parentCount = 0;

        for (int i = 0; i < numPairs; ++i) {
            //gets current layer's parents from the previous layer's array
            if ((parents.length > 0) && (parents.length > i) && (parents[i] != null)) {
                motherOfMale = parents[parentCount].getMother();
                fatherOfMale = parents[parentCount].getFather();
                ++parentCount;
                motherOfFemale = parents[parentCount].getMother();
                fatherOfFemale = parents[parentCount].getFather();
                ++parentCount;
            }

            //grabs names from the filler
            String femaleName = filler.getfNames();
            String surName = filler.getSurNames();
            String maleName = filler.getmNames();

            Person femalePerson = new Person (userName, femaleName, surName, "f", fatherOfFemale, motherOfFemale,(maleName + " " + surName));
            Person male = new Person (userName, maleName, surName, "m", fatherOfMale, motherOfMale, femalePerson.getPersonID());
            femalePerson.setSpouse(male.getPersonID());
            newParents[i] = new ParentPair(femalePerson.getPersonID(), male.getPersonID());

            new PersonDAO().createPerson(femalePerson, conn);
            new PersonDAO().createPerson(male, conn);
            //increase the people generated count by 2 since we just created 2 people
            filler.setNumPeopleGenerated(filler.getNumPeopleGenerated() + 2);

            //creates events
            Filler.Location l = filler.getLocation();
            Filler.Location two = filler.getLocation();
            Filler.Location three = filler.getLocation();
            Event marriage = new Event(userName, femalePerson.getPersonID(), l.getLatitude(), l.getLongitude(), l.getCountry(),
                    l.getCity(), "marriage", Integer.toString(referenceDate));
            Event marriage2 = new Event(userName, male.getPersonID(), l.getLatitude(), l.getLongitude(),
                    l.getCountry(), l.getCity(), "marriage", Integer.toString(referenceDate));
            Event birth = new Event(userName, femalePerson.getPersonID(), two.getLatitude(), two.getLongitude(),
                    two.getCountry(), two.getCity(), "birth", Integer.toString(referenceDate - AGE_WHEN_MARRIED));
            Event birth2 = new Event(userName, male.getPersonID(), three.getLatitude(), three.getLongitude(),
                    three.getCountry(), three.getCity(), "birth", Integer.toString(referenceDate - AGE_WHEN_MARRIED));

            EventDAO eventDAO = new EventDAO();
            eventDAO.createEvent(marriage, conn);
            eventDAO.createEvent(marriage2, conn);
            eventDAO.createEvent(birth, conn);
            eventDAO.createEvent(birth2, conn);

            if ((referenceDate + DEATH_AGE) <= CURR_YEAR) {
                Event death = new Event(userName, femalePerson.getPersonID(), l.getLatitude(), l.getLongitude(),
                        l.getCountry(), l.getCity(), "death", Integer.toString(referenceDate + DEATH_AGE));
                Event death2 = new Event(userName, male.getPersonID(), l.getLatitude(), l.getLongitude(),
                        l.getCountry(), l.getCity(), "death", Integer.toString(referenceDate + DEATH_AGE));
                eventDAO.createEvent(death, conn);
                eventDAO.createEvent(death2, conn);
            }

            filler.setNumEventsGenerated(filler.getNumEventsGenerated() + NUM_EVENTS_ADDED); //adds 6 events at a time
        }
        return newParents;
    }

    //class to store each child's parents as a pair
    private class ParentPair {
        private String Mother;
        private String Father;
        private int date = 0;

        public ParentPair(String Mother, String Father) {
            this.Mother = Mother;
            this.Father = Father;
        }

        public String getMother() {
            return Mother;
        }

        public void setMother(String first) {
            this.Mother = first;
        }

        public String getFather() {
            return Father;
        }

        public void setFather(String second) {
            this.Father = second;
        }
    }

    private class Filler {

        private Names maleNames;
        private Names femaleNames;
        private Names surNames;
        private Locations locations;

        private int maleNameIndex = 0; //index to an array;
        private int femaleNameIndex = 0;
        private int surNameIndex = 0;
        private int locationIndex = 0;
        private int numEventsGenerated = 0;
        private int numPeopleGenerated = 0;


        private void Filler() throws IOException {
            //m = male, f = female, s = surname
            String mNamesPath = "C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\lib\\mnames.json";
            String fNamesPath = "C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\lib\\fnames.json";
            String surNamesPath = "C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\lib\\snames.json";
            String locationsPath = "C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\lib\\locations.json";
            String mNamesContent = "";
            String fNamesContent = "";
            String surNamesContent = "";
            String locationsContent = "";

            mNamesContent = fileToString(mNamesPath);
            fNamesContent = fileToString(fNamesPath);
            surNamesContent = fileToString(surNamesPath);
            locationsContent = fileToString(locationsPath);

            Gson gson = new Gson();
            maleNames = gson.fromJson(mNamesContent, Names.class);
            femaleNames = gson.fromJson(fNamesContent, Names.class);
            surNames = gson.fromJson(surNamesContent, Names.class);
            locations = gson.fromJson(locationsContent, Locations.class);
        }

        public String fileToString(String path) throws IOException{
            InputStream is = new FileInputStream(path);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String curLine = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (curLine != null) {
                sb.append(curLine).append("\n");
                curLine = buf.readLine();
            }

            return sb.toString();
        }

        public String getmNames() {
            if ((maleNameIndex + 1) >= maleNames.getData().length) {
                maleNameIndex = 0;
            }
            return maleNames.getData()[maleNameIndex++];
        }

        public String getfNames() {
            if ((femaleNameIndex + 1) >= femaleNames.getData().length) {
                femaleNameIndex = 0;
            }
            return femaleNames.getData()[femaleNameIndex++];
        }

        public String getSurNames() {
            if ((surNameIndex + 1) >= surNames.getData().length) {
                surNameIndex = 0;
            }
            return surNames.getData()[surNameIndex++];
        }

        public Location getLocation() {
            if ((locationIndex + 1) >= locations.getData().length) {
                locationIndex = 0;
            }
            return locations.getData()[locationIndex++];
        }

        public int getNumEventsGenerated() {
            return numEventsGenerated;
        }

        public void setNumEventsGenerated(int numEventsGenerated) {
            this.numEventsGenerated = numEventsGenerated;
        }

        public int getNumPeopleGenerated() {
            return numPeopleGenerated;
        }

        public void setNumPeopleGenerated(int numPeopleGenerated) {
            this.numPeopleGenerated = numPeopleGenerated;
        }

        //Model class for file data
        private class Names {
            private String data[];

            public Names(String[] data) {
                this.data = data;
            }

            public String[] getData() {
                return data;
            }

            public void setData(String[] data) {
                this.data = data;
            }
        }

        //Model class for file data
        private class Locations {
            private Location data[];

            public Locations(Location[] data) {
                this.data = data;
            }

            public Location[] getData() {
                return data;
            }

            public void setData(Location[] data) {
                this.data = data;
            }
        }

        //Model class for file data nested in location's array
        private class Location {
            private String country = "";
            private String city = "";
            private String latitude = "";
            private String longitude = "";

            Location(String country, String city, String latitude, String longitude) {
                this.country = country;
                this.city = city;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }
        }
    }


}

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //parse all the JSON Files into JSON Objects
        JSONArray peopleArrayJSON = readFileContainingJSONArray("PEOPLE.json");
        JSONArray diseasesArrayJSON = readFileContainingJSONArray("DISEASES.json");
        JSONArray dosagesArrayJSON = readFileContainingJSONArray("DOSAGES.json");

        //parse all the JSON Objects into their respective objects
        ArrayList<Dosage> dosageArray = getDosagesFromJSON(dosagesArrayJSON);
        ArrayList<Disease> diseaseArray = getDiseasesFromJSON(diseasesArrayJSON, dosageArray);
        ArrayList<Person> peopleArray = getPeopleFromJSON(peopleArrayJSON, diseaseArray);

        printPeople(peopleArray);

        //COMMAND PROGRAM
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, Please enter a command. Enter 1 to see the possible commands.");

        while (scanner.hasNext()) {
            String cmd = scanner.nextLine();

            if (cmd.equals("STOP")) {
                System.out.println("Goodbye!");
                return;
            } else if (cmd.startsWith("PRESCRIPTION")) {
                if (cmd.startsWith("PRESCRIPTION_COST")) {
                    handleCostCommand(cmd, peopleArray);
                } else {
                    handlePrescriptionCommand(cmd, peopleArray);
                }
            } else if (cmd.startsWith("DISEASE")) {
                if (cmd.startsWith("DISEASE_GRANDPARENTS")) {
                    handleGrandparentsDiseaseCommand(cmd, peopleArray);
                } else {
                    handleDiseaseCommand(cmd, peopleArray);
                }
            } else if (cmd.equals("1")) {
                System.out.println("PRESCRIPTION <PERSON_ID> : Prints all the prescriptions that this person needs to take.\n" +
                        "PRESCRIPTION_COST <PERSON_ID> : Prints the cost of all the prescriptions this person needs to take.\n" +
                        "DISEASE <NAME> : Prints all the patients with the given disease name.\n" +
                        "DISEASE_GRANDPARENTS <NAME> : Prints all the grandparents with the given disease name\n" +
                        "STOP: Exits the program");
            } else {
                System.out.println("Invalid Command");
            }

            System.out.println("Please enter a command. Enter 1 to see the possible commands.");
        }
    }

    /**
     * handles a command that is of the form DISEASE_GRANDPARENTS <NAME>
     * Prints to console all the people in the given Person array that
     * have a disease with the given NAME, and are grandparents
     * <p>
     * Prints 'Invalid Command', if the command is not of the proper form
     * Prints 'Invalid Disease Name', if no grandparents have the given disease name
     *
     * @param cmd         the command
     * @param peopleArray the people array to search through
     */
    private static void handleGrandparentsDiseaseCommand(String cmd, ArrayList<Person> peopleArray) {
        String param = extractParameter(cmd.substring("DISEASE_GRANDPARENTS".length() + 1, cmd.length()));

        if (param == null) {
            System.out.println("Invalid Command");
        } else {
            String diseaseName = param;
            ArrayList<Person> diseasedGrandparents = getGrandParentsWithDisease(diseaseName, peopleArray);
            ArrayList<Person> diseasedPatients = getPeopleWithDisease(diseaseName, peopleArray);

            if (diseasedPatients.isEmpty()) {
                // no patients had the given disease name
                System.out.println("Invalid Disease Name.");
            } else {
                if (diseasedGrandparents.isEmpty()) {
                    System.out.println("No grandparents have this disease.");
                } else {
                    diseasedGrandparents.forEach(System.out::println);
                }
            }
        }
    }

    /**
     * handles a command that is of the form DISEASE <NAME>
     * Prints to console all the people in the given Person array that
     * have a disease with the given NAME
     * <p>
     * Prints 'Invalid Command', if the command is not of the proper form
     * Prints 'Invalid Disease Name', if no patients have the given disease name
     *
     * @param cmd         the command
     * @param peopleArray the people array to search through
     */
    private static void handleDiseaseCommand(String cmd, ArrayList<Person> peopleArray) {
        String param = extractParameter(cmd.substring("DISEASE".length() + 1, cmd.length()));

        if (param == null) {
            System.out.println("Invalid Command");
        } else {
            String diseaseName = param;
            ArrayList<Person> diseased = getPeopleWithDisease(diseaseName, peopleArray);

            if (diseased.isEmpty()) {
                System.out.println("Invalid Disease Name.");
            } else {
                diseased.forEach(System.out::println);
            }
        }
    }

    /**
     * returns a list of all people in the given Person array that have a disease
     * with the given Disease name
     *
     * @param diseaseName the name of the disease to look for
     * @param people      the person array to look through
     * @return an array of people with the given disease name
     */
    private static ArrayList<Person> getPeopleWithDisease(String diseaseName, ArrayList<Person> people) {
        ArrayList<Person> result = new ArrayList<>();

        for (Person p : people) {
            if (p.hasDisease(diseaseName))
                result.add(p);
        }

        return result;
    }

    /**
     * returns a list of all people in the given Person array that have a disease
     * with the given Disease name AND are grandparents
     *
     * @param diseaseName the name of the disease to look for
     * @param people      the person array to look through
     * @return an array of people with the given disease name who are grandparents
     */
    private static ArrayList<Person> getGrandParentsWithDisease(String diseaseName, ArrayList<Person> people) {
        ArrayList<Person> diseased = getPeopleWithDisease(diseaseName, people);
        ArrayList<Person> result = new ArrayList<>();

        for (Person p : diseased) {
            if (p.isGrandparent())
                result.add(p);
        }

        return result;
    }

    /**
     * handles a command that is of the form PRESCRIPTION_COST <PERSON_ID>
     * Prints to console the cost of all the medicine that the person with
     * PERSON_ID needs to take based on the diseases they have
     * <p>
     * Prints 'Invalid Command', if the command is not of the proper form
     * Prints 'Invalid Person ID', if no patients have the given ID
     *
     * @param cmd    the command
     * @param people the people array to search through
     */
    private static void handleCostCommand(String cmd, ArrayList<Person> people) {
        String param = extractParameter(cmd.substring("PRESCRIPTION_COST".length() + 1, cmd.length()));

        if (param == null) {
            System.out.println("Invalid Command");
        } else {
            try {
                int id = Integer.parseInt(param);
                Person p = findPerson(id, people);

                System.out.println(p.getPrescriptionCost());
            } catch (Exception e) {
                //either the given parameter was not an int or we couldn't find the person
                System.out.println("Invalid Person ID.");
            }
        }
    }

    /**
     * handles a command that is of the form PRESCRIPTION <PERSON_ID>
     * Prints to console all of the prescriptions that the person with
     * PERSON_ID needs to have filled out based on the diseases they have
     * <p>
     * If the person has a multiple diseases that require the same medicine,
     * the medicine dosages are combined and the medicine is only listed once
     * <p>
     * Prints 'Invalid Command', if the command is not of the proper form
     * Prints 'Invalid Person ID', if no patients have the given ID
     *
     * @param cmd    the command
     * @param people the people array to search through
     */
    private static void handlePrescriptionCommand(String cmd, ArrayList<Person> people) {
        String param = extractParameter(cmd.substring("PRESCRIPTION".length() + 1, cmd.length()));

        if (param == null) {
            System.out.println("Invalid Command");
        } else {
            try {
                int id = Integer.parseInt(param);
                Person p = findPerson(id, people);

                System.out.println(p.getCombinedPrescriptions());
            } catch (Exception e) {
                //either the given parameter was not an int or we couldn't find the person
                System.out.println("Invalid Person ID.");
            }
        }
    }

    /**
     * returns a person with the given ID in the given Person array
     * <p>
     * throws an IllegalArgumentException if the given ID doesn't exist
     *
     * @param id     the id to look for
     * @param people the Person to search through
     */
    private static Person findPerson(int id, ArrayList<Person> people) {
        for (Person p : people) {
            if (p.getId() == id)
                return p;
        }

        //invalid id given
        throw new IllegalArgumentException();
    }

    /**
     * given a string of the form "<...>" where ... is the parameter,
     * returns the parameter
     * <p>
     * returns null if the given string is not of the form "<...>"
     *
     * @param s the string to parse the parameter out of
     */
    private static String extractParameter(String s) {
        if (s.length() < 3 || s.charAt(0) != '<' || s.charAt(s.length() - 1) != '>') {
            return null;
        }

        return s.substring(1, s.length() - 1);
    }

    /**
     * Prints the given Person array so that each person is on a new line
     *
     * @param people the Person array to print
     */
    private static void printPeople(ArrayList<Person> people) {
        for (Person p : people)
            System.out.println(p);
    }

    /**
     * given an JSONArray of integers, returns an arrayList of the same integers
     */
    private static ArrayList<Integer> jsonArrayIntConverter(JSONArray array) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (Object o : array) {
            result.add((Integer) o);
        }

        return result;
    }

    /**
     * given an JSONArray representing a list of Persons
     * <p>
     * returns an list of Person objects with:
     * the children IDs mapped to Person objects
     * the disease IDs mapped to Disease objects
     * the spouse ID mapped to a Person (null if -1)
     *
     * @param peopleArrayJSON the JSONArray that is to be converted
     * @param allDiseases     a list of all the diseases that exist
     * @return a properly mapped list of Persons
     */
    private static ArrayList<Person> getPeopleFromJSON(JSONArray peopleArrayJSON, ArrayList<Disease> allDiseases) {
        ArrayList<Person> peopleArray = new ArrayList<>();

        for (Object o : peopleArrayJSON) {
            JSONObject jsonPerson = (JSONObject) o;

            int id = jsonPerson.getInt("personId");
            String name = jsonPerson.getString("name");
            int spouseId = jsonPerson.getInt("spouseId");
            Gender gender = jsonPerson.getString("gender").equals("M") ? Gender.MALE : Gender.FEMALE;
            ArrayList<Integer> childrenIDs = jsonArrayIntConverter(jsonPerson.getJSONArray("childrenIds"));
            ArrayList<Integer> diseaseIDs = jsonArrayIntConverter(jsonPerson.getJSONArray("diseaseIds"));

            //creates Person objects where only the ID field is filled in
            //to be later mapped to a filled Person object once we have a complete list of patients
            ArrayList<Person> children = createIncompletePersonArray(childrenIDs);
            Person spouse = (spouseId == -1) ? null : new Person(spouseId);

            //maps the disease IDs to actual Disease objects
            ArrayList<Disease> diseases = getDiseasesFromIDs(diseaseIDs, allDiseases);

            Person toAdd = new Person(id, name, spouse, gender, children, diseases);

            peopleArray.add(toAdd);
        }

        //properly fills in the Person objects in the children and spouse fields for each patient
        mapChildren(peopleArray);
        mapSpouses(peopleArray);

        return peopleArray;
    }

    /**
     * given a list of person IDs, returns a list of person objects which
     * are completely empty besides the ID field
     * <p>
     * the purpose of this is to be able to store the IDs of the children when
     * creating the initial list of patients
     * <p>
     * WARNING: DO NOT USE ELSEWHERE BESIDES WHEN INITIATING THE LIST OF PATIENTS
     *
     * @param ids the Ids to create mostly empty Person objects with
     * @return a list of mostly empty Person objects with the given IDs
     */
    private static ArrayList<Person> createIncompletePersonArray(ArrayList<Integer> ids) {
        ArrayList<Person> result = new ArrayList<>();

        for (int id : ids) {
            result.add(new Person(id));
        }

        return result;
    }

    /**
     * Given an array of people in which each Person's children field
     * contains Person objects where only the ID field is filled in,
     * <p>
     * properly fills in the rest of the fields for each child object given that
     * the child object exists in the given Person array
     * <p>
     * WARNING: DO NOT USE ELSEWHERE BESIDES WHEN INITIATING THE LIST OF PATIENTS
     *
     * @param people the given person array that needs to be properly mapped
     */
    private static void mapChildren(ArrayList<Person> people) {
        for (Person p : people) {
            for (Person child : p.getChildren()) {
                Person filledInChild = findPerson(child.getId(), people);

                child.setSpouse(filledInChild.getSpouse());
                child.setChildren(filledInChild.getChildren());
                child.setDiseases(filledInChild.getDiseases());
                child.setGender(filledInChild.getGender());
                child.setName(filledInChild.getName());
            }
        }
    }

    /**
     * Given an array of people in which each Person's spouse field
     * contains a Person object where only the ID field is filled in,
     * <p>
     * properly fills in the rest of the fields for each spouse object given that
     * the spouse exists in the given Person array
     * <p>
     * WARNING: DO NOT USE ELSEWHERE BESIDES WHEN INITIATING THE LIST OF PATIENTS
     *
     * @param people the given person array that needs to be properly mapped
     */
    private static void mapSpouses(ArrayList<Person> people) {
        for (Person p : people) {
            if (p.getSpouse() != null) {
                Person filledInSpouse = findPerson(p.getSpouse().getId(), people);
                Person emptySpouse = p.getSpouse();

                emptySpouse.setName(filledInSpouse.getName());
                emptySpouse.setGender(filledInSpouse.getGender());
                emptySpouse.setDiseases(filledInSpouse.getDiseases());
                emptySpouse.setChildren(filledInSpouse.getChildren());
                emptySpouse.setSpouse(filledInSpouse.getSpouse());
            }
        }
    }

    /**
     * given a list of disease IDs and a list of all possible diseases,
     * <p>
     * returns a list of diseases that map to the IDs given
     *
     * @param ids         the IDs that need to be mapped
     * @param allDiseases a list of all diseases
     * @return a list of Diseases that have the given IDs
     */
    private static ArrayList<Disease> getDiseasesFromIDs(ArrayList<Integer> ids, ArrayList<Disease> allDiseases) {
        ArrayList<Disease> diseases = new ArrayList<>();

        for (Disease d : allDiseases) {
            if (ids.contains(d.getId())) {
                diseases.add(new Disease(d.getName(), d.getId(), d.getPrescriptions()));
            }
        }

        return diseases;
    }

    /**
     * given a JSONArray representing a list of diseases and given a list of Dosages,
     * <p>
     * returns a list of Diseases where each disease keeps track of the medicines
     * needed to treat it as well as the dosages of said medicines
     * <p>
     * this medicine information (including dosage) is kept in a Prescription object
     *
     * @param diseaseArrayJson the JSONArray that is to be converted
     * @param dosages          a list of all the dosages
     * @return a list of Disease objects
     */
    private static ArrayList<Disease> getDiseasesFromJSON(JSONArray diseaseArrayJson, ArrayList<Dosage> dosages) {
        ArrayList<Disease> diseaseArray = new ArrayList<>();

        for (Object o : diseaseArrayJson) {
            JSONObject jsonDisease = (JSONObject) o;

            int id = jsonDisease.getInt("diseaseId");
            String diseaseName = jsonDisease.getString("diseaseName");
            ArrayList<Prescription> prescriptions = new ArrayList<>();

            JSONArray medicineArrayJson = jsonDisease.getJSONArray("medications");

            for (Object j : medicineArrayJson) {
                JSONObject jsonMedicine = (JSONObject) j;

                int medicineId = jsonMedicine.getInt("medicationId");
                String medicineName = jsonMedicine.getString("name");
                int costPerUnit = jsonMedicine.getInt("costPerUnit");
                int dosage = getDosage(id, medicineId, dosages);

                prescriptions.add(new Prescription(medicineId, medicineName, dosage, costPerUnit));
            }

            diseaseArray.add(new Disease(diseaseName, id, prescriptions));
        }

        return diseaseArray;
    }

    /**
     * converts a JSONArray representing a list of dosages into
     * an ArrayList of Dosage objects
     *
     * @param dosageArrayJson the JSONArray to be converted
     * @return a list of Dosage objects
     */
    private static ArrayList<Dosage> getDosagesFromJSON(JSONArray dosageArrayJson) {
        ArrayList<Dosage> dosageArray = new ArrayList<>();

        for (Object o : dosageArrayJson) {
            JSONObject jsonDosage = (JSONObject) o;

            int diseaseId = jsonDosage.getInt("diseaseId");
            int medicationId = jsonDosage.getInt("medicationId");
            String dosageString = jsonDosage.getString("dosage");
            int dosage = Integer.parseInt(dosageString.split(" ")[0]);

            dosageArray.add(new Dosage(diseaseId, medicationId, dosage));
        }

        return dosageArray;
    }

    /**
     * given a diseaseID, medicineId and list of all dosages,
     * returns the appropriate dosage, -1 if not found
     */
    private static int getDosage(int diseaseId, int medicineId, ArrayList<Dosage> dosages) {
        for (Dosage d : dosages) {
            if (d.medicationId == medicineId && d.diseaseId == diseaseId) {
                return d.dosage;
            }
        }

        return -1; //could not find dosage
    }

    /**
     * given a fileName of a JSON file containing a JSON Array,
     * converts that file's contents into a JSONArray object
     * <p>
     * if the file is not found, returns null
     */
    private static JSONArray readFileContainingJSONArray(String fileName) {
        FileReader reader;
        BufferedReader bufferedReader;
        String jsonString = "";

        try {
            reader = new FileReader(fileName);

            bufferedReader = new BufferedReader(reader);
            jsonString = bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read in JSON File: " + fileName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read in JSON File: " + fileName);
            e.printStackTrace();
        }

        //if nothing was readable then return null
        if (jsonString.equals(""))
            return null;

        return new JSONArray(jsonString);
    }
}

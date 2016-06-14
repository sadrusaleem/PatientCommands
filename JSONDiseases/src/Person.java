import java.util.ArrayList;

/**
 * USED TO REPRESENT A PERSON OBJECT
 */
public class Person {
    private int id;
    private String name;
    private Person spouse;
    private Gender gender;
    private ArrayList<Person> children;
    private ArrayList<Disease> diseases;

    public Person(int id, String name, Person spouse, Gender gender, ArrayList<Person> children, ArrayList<Disease> diseases) {
        this.id = id;
        this.name = name;
        this.spouse = spouse;
        this.gender = gender;
        this.children = children;
        this.diseases = diseases;
    }

    //ONLY USE THIS CONSTRUCTOR IF THE REST OF THE FIELDS ARE UNKNOWN
    public Person(int id){
        this.id = id;
        this.name = null;
        this.spouse = null;
        this.gender = null;
        this.children = null;
        this.diseases = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouseID) {
        this.spouse = spouseID;
    }

    public ArrayList<Person> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Person> children) {
        this.children = children;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ArrayList<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(ArrayList<Disease> diseases) {
        this.diseases = diseases;
    }

    @Override
    public String toString() {
        return "Person{" +
                "\n\tid=" + id +
                "\n\tname='" + name + '\'' +
                "\n\tspouseID=" + ((getSpouse() != null) ? spouse.getId() : "NO SPOUSE") +
                "\n\tgender=" + gender +
                "\n\tchildren=" + getChildrenIDsString() +
                "\n\tdiseases=" + diseases +
                "\n\tcombined prescriptions=" + this.getCombinedPrescriptions() +
                '}';
    }

    /**
     * returns a String of the form "[...]" where ... is the list of children IDs
     * separated by commas
     */
    private String getChildrenIDsString(){
        String result = "[";

        for(int i=0; i<children.size(); i++){
            result += children.get(i).getId();

            if(i != children.size() - 1)
                result += ", ";
        }

        result += "]";

        return result;
    }

    /**
     * returns a list of prescriptions this person is taking in which each medicine is only listed once
     * and dosage amounts of the same medicines are totaled
     */
    public ArrayList<Prescription> getCombinedPrescriptions(){
        ArrayList<Prescription> combinedPrescriptions = new ArrayList<>();

        for(Disease d : getDiseases()){
            for(Prescription p : d.getPrescriptions()){
                combinePrescription(p, combinedPrescriptions);
            }
        }

        return combinedPrescriptions;
    }

    /**
     * combines the given prescription with the given list of prescriptions by:
     * updating the dosage if the medicine is already in the list
     * else just adding the prescription to the list
     */
    private void combinePrescription(Prescription p, ArrayList<Prescription> existingList){
        for(Prescription current : existingList){
            if(current.getMedicineId() == p.getMedicineId()){
                current.setDosage(current.getDosage() + p.getDosage());
                return;
            }
        }

        //if we get here, then the medicine id did not already exist in the existing list
        existingList.add(new Prescription(p.getMedicineId(), p.getMedicineName(), p.getDosage(), p.getCostPerUnit()));
    }

    /**
     * returns the cost of all the medicine this person should be taking
     * based on their diseases
     */
    public int getPrescriptionCost(){
        ArrayList<Prescription> prescriptions = getCombinedPrescriptions();

        int cost=0;

        for(Prescription p : prescriptions){
            cost += (p.getCostPerUnit() * p.getDosage());
        }

        return cost;
    }

    /**
     * determines if this Person has a disease with the given disease name
     * @param diseaseName the disease name to look for
     */
    public boolean hasDisease(String diseaseName){
        for(Disease d : this.getDiseases()){
            if(d.getName().equals(diseaseName)){
                return true;
            }
        }

        return false;
    }

    /**
     * determines if this person is a grandparent
     * (has atleast one child that has children)
     */
    public boolean isGrandparent(){
        for(Person p : children){
            if(p.children.size() > 0)
                return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != person.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

enum Gender {
    MALE, FEMALE
}

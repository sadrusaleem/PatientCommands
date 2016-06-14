import java.util.ArrayList;

/**
 * USED TO REPRESENT A DISEASE OBJECT
 * Contains a list of Prescriptions which details the medicines (and dosages)
 * required to treat this disease
 */
public class Disease{
    private int id;
    private String name;
    private ArrayList<Prescription> prescriptions;

    public Disease(String name, int id, ArrayList<Prescription> prescriptions) {
        this.name = name;
        this.id = id;
        this.prescriptions = prescriptions;
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

    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(ArrayList<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

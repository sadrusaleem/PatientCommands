/**
 * USED TO REPRESENT AN ENTRY IN THE DOSAGES.json file
 */
public class Dosage {
    int diseaseId;
    int medicationId;
    int dosage;

    public Dosage(int diseaseId, int medicationId, int dosage) {
        this.diseaseId = diseaseId;
        this.medicationId = medicationId;
        this.dosage = dosage;
    }
}

/**
 * Used to represent a medicine and it's dosage
 */
public class Prescription{
    private int medicineId;
    private String medicineName;
    private int costPerUnit;
    private int dosage;

    public Prescription(int medicineId, String medicineName, int dosage, int costPerUnit) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.costPerUnit = costPerUnit;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public int getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(int costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "medicineId=" + medicineId +
                ", medicineName='" + medicineName + '\'' +
                ", costPerUnit=" + costPerUnit +
                ", dosage=" + dosage +
                '}';
    }
}

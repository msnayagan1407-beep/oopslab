import java.util.*;

// ---------- Inheritance: base class ----------
abstract class Person {
    private String name;
    private int age;
    private String phone;

    public Person(String name, int age, String phone) {
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    // Encapsulation: private fields + getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getPhone() { return phone; }

    public abstract String getRole();
}

class Patient extends Person {
    private final int patientId;

    public Patient(int patientId, String name, int age, String phone) {
        super(name, age, phone);
        this.patientId = patientId;
    }

    public int getPatientId() { return patientId; }
    @Override public String getRole() { return "Patient"; }

    @Override public String toString() {
        return String.format("P%d | %s | Age %d | %s", patientId, getName(), getAge(), getPhone());
    }
}

class Doctor extends Person {
    private final int doctorId;
    private final String specialization;
    private final List<String> slots = new ArrayList<>();

    public Doctor(int doctorId, String name, int age, String phone,
                  String specialization, String... slotTimes) {
        super(name, age, phone);
        this.doctorId = doctorId;
        this.specialization = specialization;
        slots.addAll(Arrays.asList(slotTimes));
    }

    public int getDoctorId() { return doctorId; }
    public String getSpecialization() { return specialization; }
    public List<String> getSlots() { return Collections.unmodifiableList(slots); }
    public boolean removeSlot(String s) { return slots.remove(s); }
    public void addSlot(String s) { slots.add(s); }
    @Override public String getRole() { return "Doctor"; }

    @Override public String toString() {
        return String.format("D%d | Dr. %s | %s | Slots: %s", doctorId, getName(), specialization, slots);
    }
}

class Appointment {
    enum Status { BOOKED, COMPLETED, CANCELLED }

    private static int counter = 1000;
    private final int appointmentId;
    private final Patient patient;
    private final Doctor doctor;
    private final String date;
    private final String time;
    private Status status;

    public Appointment(Patient patient, Doctor doctor, String date, String time) {
        this.appointmentId = ++counter;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.status = Status.BOOKED;
    }

    public int getAppointmentId() { return appointmentId; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public Status getStatus() { return status; }
    public void setStatus(Status s) { this.status = s; }

    @Override public String toString() {
        return String.format("A%d | Patient: %s | Doctor: Dr. %s (%s) | %s %s | %s",
                appointmentId, patient.getName(), doctor.getName(),
                doctor.getSpecialization(), date, time, status);
    }
}

// ---------- Collections: ArrayList, HashMap ----------
class HospitalManager {
    private final Map<Integer, Patient> patients = new HashMap<>();
    private final Map<Integer, Doctor> doctors = new LinkedHashMap<>();
    private final List<Appointment> appointments = new ArrayList<>();
    private int patientCounter = 0;

    public void addDoctor(Doctor d) { doctors.put(d.getDoctorId(), d); }

    public Patient registerPatient(String name, int age, String phone) {
        Patient p = new Patient(++patientCounter, name, age, phone);
        patients.put(p.getPatientId(), p);
        return p;
    }

    public Collection<Doctor> getDoctors() { return doctors.values(); }
    public Collection<Patient> getPatients() { return patients.values(); }

    public Appointment book(int patientId, int doctorId, String date, String time) {
        Patient p = patients.get(patientId);
        Doctor d = doctors.get(doctorId);
        if (p == null) { System.out.println("Invalid patient ID."); return null; }
        if (d == null) { System.out.println("Invalid doctor ID."); return null; }
        for (Appointment a : appointments) {
            if (a.getDoctor() == d && a.getDate().equals(date) && a.getTime().equals(time)
                    && a.getStatus() == Appointment.Status.BOOKED) {
                System.out.println("Slot already booked for this doctor on that date.");
                return null;
            }
        }
        if (!d.getSlots().contains(time)) {
            System.out.println("Doctor not available at " + time + ". Slots: " + d.getSlots());
            return null;
        }
        Appointment a = new Appointment(p, d, date, time);
        appointments.add(a);
        return a;
    }

    public boolean updateStatus(int appointmentId, Appointment.Status s) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId() == appointmentId) { a.setStatus(s); return true; }
        }
        return false;
    }

    public void displayAppointments() {
        if (appointments.isEmpty()) { System.out.println("No appointments yet."); return; }
        for (Appointment a : appointments) System.out.println(a);
    }
}

public class HospitalAppointmentSystem {
    public static void main(String[] args) {
        HospitalManager hm = new HospitalManager();
        hm.addDoctor(new Doctor(1, "Meena", 42, "9000000001", "Cardiology", "10:00", "11:00", "14:00"));
        hm.addDoctor(new Doctor(2, "Karthik", 38, "9000000002", "Orthopedics", "09:30", "12:00", "15:30"));
        hm.addDoctor(new Doctor(3, "Lakshmi", 45, "9000000003", "Pediatrics", "10:30", "13:00", "16:00"));

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Hospital Appointment Management ===");
            System.out.println("1. Register Patient\n2. View Doctors\n3. Book Appointment");
            System.out.println("4. View Appointments\n5. Complete/Cancel Appointment\n6. View Patients\n0. Exit");
            System.out.print("Choice: ");
            if (!sc.hasNextLine()) break;
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Name: "); String n = sc.nextLine();
                    System.out.print("Age: "); int age = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Phone: "); String ph = sc.nextLine();
                    Patient p = hm.registerPatient(n, age, ph);
                    System.out.println("Registered: " + p);
                    break;
                case "2":
                    for (Doctor d : hm.getDoctors()) System.out.println(d);
                    break;
                case "3":
                    System.out.print("Patient ID: "); int pid = Integer.parseInt(sc.nextLine().trim());
                    for (Doctor d : hm.getDoctors()) System.out.println(d);
                    System.out.print("Doctor ID: "); int did = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Date (dd-mm-yyyy): "); String date = sc.nextLine().trim();
                    System.out.print("Time (HH:mm): "); String time = sc.nextLine().trim();
                    Appointment a = hm.book(pid, did, date, time);
                    if (a != null) System.out.println("Booked: " + a);
                    break;
                case "4":
                    hm.displayAppointments();
                    break;
                case "5":
                    System.out.print("Appointment ID: "); int aid = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("1=Completed, 2=Cancelled: ");
                    Appointment.Status st = sc.nextLine().trim().equals("1")
                            ? Appointment.Status.COMPLETED : Appointment.Status.CANCELLED;
                    System.out.println(hm.updateStatus(aid, st) ? "Status updated." : "Appointment not found.");
                    break;
                case "6":
                    for (Patient pt : hm.getPatients()) System.out.println(pt);
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

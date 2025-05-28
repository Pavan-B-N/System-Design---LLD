// Design ParkingLotDesign
/*
 * A parking lot management system is designed to handle the operations of parking vehicles, collecting payments, and managing available space efficiently. The system should be able to accommodate different types of vehicles, provide payment options, and ensure a smooth user experience.
 * 
 * Setup:

• The parking lot has multiple slots available for parking.
• Different types of vehicles (bike, car, truck) can occupy different slot sizes.
• Each vehicle is issued a parking ticket upon entry.
• The system calculates the parking fee based on the duration of stay and vehicle type.

‍

Exit and Payment:

• A vehicle needs to make a payment before exiting.
• Multiple payment methods (Cash, Card, UPI) should be supported.
• Once payment is successful, the vehicle is allowed to exit, and the parking slot is freed.
 */

//  Step1: requirements
/*
• A parking lot with multiple slot types.

• Support for bikes, cars, and trucks.

• Dynamic slot allocation based on vehicle size.

• Payment processing with multiple methods.

• Entry ticket issuance and exit validation.
 */

 
/*
✅ Design Goals
Singleton: ParkingLot (only one instance of parking lot)

Factory: VehicleFactory to create vehicles

Strategy:

PaymentStrategy (Cash, Card)

ParkingFeeStrategy (Different rates per vehicle)


 */

 
import java.util.*;

enum VehicleType {
    CAR,
    BIKE,
    TRUCK
}

abstract class Vehicle {
    private String number;
    private VehicleType type;

    public Vehicle(String number, VehicleType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public VehicleType getType() {
        return type;
    }
}
class Car extends Vehicle {
    public Car(String number) {
        super(number, VehicleType.CAR);
    }
}

class Bike extends Vehicle {
    public Bike(String number) {
        super(number, VehicleType.BIKE);
    }
}

// Factory Pattern = Creational Design Pattern
class VehicleFactory {
    public static Vehicle createVehicle(String number, VehicleType type) {
        switch (type) {
            case CAR: return new Car(number);
            case BIKE: return new Bike(number);
            default: throw new IllegalArgumentException("Invalid type");
        }
    }
}


class ParkingSlot {
    private boolean occupied;
    private Vehicle vehicle;
    private boolean isPaid;
    private int parkedHours;

    public boolean isFree() {
        return !occupied;
    }

    public void assignVehicle(Vehicle v, int hours) {
        occupied = true;
        vehicle = v;
        isPaid = false;
        parkedHours = hours;
    }

    public void removeVehicle() {
        occupied = false;
        vehicle = null;
        isPaid = false;
        parkedHours = 0;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getParkedHours() {
        return parkedHours;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void markPaid() {
        isPaid = true;
    }
}


class ParkingLot {
    private static ParkingLot instance;
    private List<ParkingSlot> slots;

    private ParkingLot(int capacity) {
        slots = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            slots.add(new ParkingSlot());
        }
    }

    public static ParkingLot getInstance(int capacity) {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    instance = new ParkingLot(capacity);
                }
            }
        }
        return instance;
    }

    public ParkingSlot park(Vehicle vehicle, int hours) {
        for (ParkingSlot slot : slots) {
            if (slot.isFree()) {
                slot.assignVehicle(vehicle, hours);
                System.out.println("Parked: " + vehicle.getNumber());
                return slot;
            }
        }
        System.out.println("No available slots.");
        return null;
    }

    public void leave(ParkingSlot slot) {
        if (slot == null) return;
        if (!slot.isPaid()) {
            System.out.println("Payment not done! Please pay before exit.");
            return;
        }
        slot.removeVehicle();
        System.out.println("Vehicle exited successfully.");
    }
}

interface ParkingFeeStrategy {
    double calculate(VehicleType type, int hours);
}

class SimpleFeeStrategy implements ParkingFeeStrategy {
    public double calculate(VehicleType type, int hours) {
        switch (type) {
            case CAR: return hours * 10.0;
            case BIKE: return hours * 5.0;
            case TRUCK: return hours * 20.0;
            default: return 0.0;
        }
    }
}

interface PaymentStrategy {
    void pay(double amount);
}

class CashPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " in Cash.");
    }
}

class CardPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via Card.");
    }
}

public class ParkingLotDesign {
    public static void main(String[] args) {
        // Step 1: Get parking lot instance
        ParkingLot lot = ParkingLot.getInstance(3);

        // Step 2: Create a vehicle
        Vehicle car = VehicleFactory.createVehicle("KA01AB1234", VehicleType.CAR);
        int hours = 4; // manually passed duration

        // Step 3: Park vehicle
        ParkingSlot slot = lot.park(car, hours);

        if (slot != null) {
            // Step 4: Calculate fee
            ParkingFeeStrategy feeStrategy = new SimpleFeeStrategy();
            double fee = feeStrategy.calculate(car.getType(), hours);
            System.out.println("Total Fee: ₹" + fee);

            // Step 5: Pay fee
            PaymentStrategy payment = new CardPayment();
            payment.pay(fee);
            slot.markPaid(); // mark payment complete

            // Step 6: Exit
            lot.leave(slot);
        }
    }
}

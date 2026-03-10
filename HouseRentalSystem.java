import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Generic Interface
interface Storage<T> {
    void addItem(T item);
    T getItem(int index);
    boolean searchItem(T item);
}

// Generic Class implementing the interface
class MyList<T> implements Storage<T> {
    private ArrayList<T> items = new ArrayList<>();

    @Override
    public void addItem(T item) {
        items.add(item);
    }

    @Override
    public T getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    @Override
    public boolean searchItem(T item) {
        return items.contains(item);
    }

    public List<T> getAllItems() {
        return items;
    }
}

// Generic Method to display any type of list
class DisplayHelper {
    public static <T> void display(List<T> list) {
        for (T element : list) {
            System.out.println(element);
        }
    }
}

//Domain-specific class- House
class House {
    private String houseId;
    private String location;
    private double rentAmount;

    public House(String houseId, String location, double rentAmount) {
        this.houseId = houseId;
        this.location = location;
        this.rentAmount = rentAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof House) {
            House h = (House) obj;
            return this.houseId.equals(h.houseId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "House ID: " + houseId + ", Location: " + location + ", Rent: ₹" + rentAmount;
    }
}

// Main class
public class HouseRentalSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MyList<House> houseList = new MyList<>();
        System.out.println("========Rental House Management System========"); 
        System.out.println("Enter number of houses to add: ");
        int n = sc.nextInt();
        sc.nextLine();  

      
        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for House " + (i + 1));
            System.out.print("Enter House ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Location: ");
            String location = sc.nextLine();
            System.out.print("Enter Rent Amount: ");
            double rent = sc.nextDouble();
            sc.nextLine();  

            houseList.addItem(new House(id, location, rent));
        }

        System.out.println("\n--- All Houses ---");
        DisplayHelper.display(houseList.getAllItems());

        // Retrieve by index
        System.out.print("\nEnter index to retrieve house (starting from 0): ");
        int index = sc.nextInt();
        sc.nextLine(); 
        House retrievedHouse = houseList.getItem(index);
        System.out.println(retrievedHouse != null ? retrievedHouse : "Invalid Index!");

        // Search item
        System.out.print("\nEnter House ID to search: ");
        String searchId = sc.nextLine();
        House searchHouse = new House(searchId, "", 0);
        System.out.println("Found: " + houseList.searchItem(searchHouse));

        // Demonstrating String type
        MyList<String> tenantNames = new MyList<>();
        tenantNames.addItem("Rahul");
        tenantNames.addItem("Anita");
        tenantNames.addItem("John");

        // Demonstrating Integer type
        MyList<Integer> rentAmounts = new MyList<>();
        rentAmounts.addItem(15000);
        rentAmounts.addItem(12000);
        rentAmounts.addItem(18000);

        System.out.println("\n--- Tenant Names ---");
        DisplayHelper.display(tenantNames.getAllItems());

        System.out.println("\n--- Rent Amounts ---");
        DisplayHelper.display(rentAmounts.getAllItems());

        sc.close();
    }
}

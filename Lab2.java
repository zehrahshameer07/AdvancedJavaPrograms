import java.util.*;
import java.util.stream.*;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;  

public class Lab2 {

    static class House {
        String owner;
        String location;
        double rent;
        int rooms;

        House(String owner, String location, double rent, int rooms) {
            this.owner = owner;
            this.location = location;
            this.rent = rent;
            this.rooms = rooms;
        }

        @Override
        public String toString() {
            return owner + " | " + location + " | Rs." + rent + " | Rooms: " + rooms;
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        List<House> houseList = new ArrayList<>();
        houseList.add(new House("Arjun", "Koramangala", 20000, 2));
        houseList.add(new House("Priya", "Indiranagar", 35000, 3));
        houseList.add(new House("Rahul", "Marathahalli", 15000, 1));
        houseList.add(new House("Meera", "Whitefield", 30000, 3));

        // --- Lambda Expressions ---

        //  No-argument lambda
        Runnable showMessage = () -> System.out.println("\n--- Welcome to Smart House Rental Finder Bangalore ---");
        showMessage.run();

        // One-argument lambda
        Consumer<String> notifyUser = msg -> System.out.println("Notification: " + msg);
        notifyUser.accept("Tip: You can search by location, budget, or number of rooms!");

        //  Two-argument lambda
        BiFunction<Double, Integer, Double> calculateRoomBasedRent = (rent, rooms) -> rent + (rooms * 2000);
        System.out.println("Example Calculation: Base Rent 15000 + (3 rooms * 2000) = Rs." 
                            + calculateRoomBasedRent.apply(15000.0, 3));

        //  Block lambda 
        interface RentChecker {
            String check(double rent);
        }
        RentChecker rentCheck = (rent) -> {
            if (rent <= 20000)
                return "Budget-Friendly";
            else if (rent <= 30000)
                return "Moderate";
            else
                return "Premium";
        };
        System.out.println("Rent Status of Rs.35000: " + rentCheck.check(35000));

       
        System.out.print("\nDo you want to add a new house listing? (yes/no): ");
        if (sc.next().equalsIgnoreCase("yes")) {
            sc.nextLine(); 
            
            System.out.print("Enter owner name: ");
            String owner = sc.nextLine();
            
            System.out.print("Enter location: ");
            String loc = sc.nextLine();
            
            System.out.print("Enter rent: ");
            double rent = sc.nextDouble();
            
            System.out.print("Enter number of rooms: ");
            int rooms = sc.nextInt();

            houseList.add(new House(owner, loc, rent, rooms));
            notifyUser.accept("House added successfully!");
        }

      
sc.nextLine(); 
System.out.print("\nEnter maximum rent for filtering: Rs.");
double maxRent = sc.nextDouble();

System.out.print("Enter minimum number of rooms: ");
int minRooms = sc.nextInt();

sc.nextLine();
System.out.print("Enter preferred location (Koramangala/Indiranagar/skip): ");
String preferredLoc = sc.nextLine().trim();


Predicate<House> smartFilter = h -> 
        h.rent <= maxRent &&
        h.rooms >= minRooms &&
        (preferredLoc.isEmpty() || h.location.equalsIgnoreCase(preferredLoc));

System.out.println("\n--- Smart Filtered Houses Based on Your Input ---");
houseList.stream()
         .filter(smartFilter)
         .forEach(System.out::println);


      
        //  Filtering houses by budget
        System.out.print("\nEnter your maximum budget: Rs.");
        double budget = sc.nextDouble();
        List<House> budgetHouses = houseList.stream()
                .filter(h -> h.rent <= budget)
                .collect(Collectors.toList());
        System.out.println("\n--- Houses Within Budget ---");
        if(budgetHouses.isEmpty()) System.out.println("No houses found within this budget.");
        else budgetHouses.forEach(System.out::println);

        //  Search by location
        sc.nextLine();
        System.out.print("\nEnter location to search (Koramangala, Indiranagar, etc): ");
        String searchLoc = sc.nextLine();
        List<House> locationHouses = houseList.stream()
                .filter(h -> h.location.equalsIgnoreCase(searchLoc))
                .collect(Collectors.toList());
        System.out.println("\n--- Houses in " + searchLoc + " ---");
        if(locationHouses.isEmpty()) System.out.println("No houses found in this location.");
        else locationHouses.forEach(System.out::println);

        //  Sorting houses by rent
        System.out.println("\n--- Houses Sorted by Rent (Low to High) ---");
        houseList.stream()
                .sorted(Comparator.comparingDouble(h -> h.rent))
                .forEach(System.out::println);

        //  Collect owners list
        List<String> ownerNames = houseList.stream()
                .map(h -> h.owner)
                .collect(Collectors.toList());
        System.out.println("\nOwner Names: " + ownerNames);

        //  Reducing: Total combined rent
        double totalRent = houseList.stream()
                .map(h -> h.rent)
                .reduce(0.0, Double::sum);
        System.out.println("Total rent value of all listed houses: Rs." + totalRent);

        //  Collect unique locations
        Set<String> uniqueLocations = houseList.stream()
                .map(h -> h.location)
                .collect(Collectors.toSet());
        System.out.println("Available Locations in Bangalore: " + uniqueLocations);

        //  Owner to Rent Map
        Map<String, Double> ownerRentMap = houseList.stream()
                .collect(Collectors.toMap(h -> h.owner, h -> h.rent, (a, b) -> a));
        System.out.println("Owner-Rent Map: " + ownerRentMap);

        sc.close();
    }
}

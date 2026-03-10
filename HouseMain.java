public class HouseMain {

    public static void main(String[] args) {

        HouseBean h = new HouseBean();

        h.setHouseId(101);
        h.setOwnerName("Rahul Sharma");
        h.setLocation("Bangalore");
        h.setRent(15000);

        System.out.println("House ID: " + h.getHouseId());
        System.out.println("Owner Name: " + h.getOwnerName());
        System.out.println("Location: " + h.getLocation());
        System.out.println("Rent Amount: " + h.getRent());
    }
}
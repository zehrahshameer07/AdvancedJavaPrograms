public class House {
    private int houseId;
    private String address;
    private double rent;
    private int ownerId;

    public House(int houseId, String address,
                 double rent, int ownerId) {
        this.houseId = houseId;
        this.address = address;
        this.rent = rent;
        this.ownerId = ownerId;
    }

    public int getHouseId() { return houseId; }
    public String getAddress() { return address; }
    public double getRent() { return rent; }
    public int getOwnerId() { return ownerId; }
}

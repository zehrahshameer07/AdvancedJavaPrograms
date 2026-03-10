public class HouseRecord {

    private int houseId;
    private String address;
    private double rent;
    private String owner;
    private String contact;

    public HouseRecord(int houseId, String address,
                       double rent, String owner, String contact) {
        this.houseId = houseId;
        this.address = address;
        this.rent = rent;
        this.owner = owner;
        this.contact = contact;
    }

    public int getHouseId() {
        return houseId;
    }

    public String getAddress() {
        return address;
    }

    public double getRent() {
        return rent;
    }

    public String getOwner() {
        return owner;
    }

    public String getContact() {
        return contact;
    }
}

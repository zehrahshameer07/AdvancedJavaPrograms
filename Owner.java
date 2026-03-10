public class Owner {
    private int ownerId;
    private String ownerName;
    private String contact;

    public Owner(int ownerId, String ownerName, String contact) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.contact = contact;
    }

    public int getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getContact() { return contact; }
}

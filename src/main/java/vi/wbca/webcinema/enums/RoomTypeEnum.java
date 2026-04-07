package vi.wbca.webcinema.enums;

public enum RoomTypeEnum {
    STANDARD(1.0),
    VIP(1.5),
    IMAX(2.0);

    private final double priceMultiplier;

    RoomTypeEnum(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }
}

package it.cs.unipd.utils;

/**
 * Created by matteo on 04/06/14.
 */
public class Settings {

    private String sex;
    private String age;
    private String height;
    private String shoes;
    private String hand;
    private String action;
    private String origin;
    private String destination;

    public Settings(String sex, String age, String height, String shoes, String hand, String action,
                    String origin, String destination) {

        this.sex = sex; this.age = age; this.height = height; this.shoes = shoes;
        this.hand = hand; this.action = action; this.origin = origin; this.destination = destination;
    }

    public String getSex() {
        return this.sex;
    }

    public String getAge() {
        return this.age;
    }

    public String getHeight() {
        return this.height;
    }

    public String getShoes() {
        return this.shoes;
    }

    public String getHand() {
        return this.hand;
    }

    public String getAction() {
        return this.action;
    }

    public String getOrigin() {
        return this.origin;
    }

    public String getDestination() {
        return this.destination;
    }

    @Override
    public String toString() {
        return sex + "," + age + "," + height + "," + shoes + "," + hand + "," + action + "," +
                origin + "," + destination;
    }
}

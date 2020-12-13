package lavaPlayer;

import net.dv8tion.jda.api.entities.TextChannel;

public class BooleanMass {

    static boolean mass;

    public static boolean isMass() {
        return mass;
    }

    public static void setMass(boolean mass) {
        BooleanMass.mass = mass;
    }

    public static void settingMass (TextChannel channel, boolean mass) {
        setMass(mass);

        if (mass) {
            channel.sendMessage("BAWAL NA MAG-INGAY **TANG INA** MISA NA").queue();
        } else {
            channel.sendMessage("MAG INGAY NA TAPOS NA ANG MISA **TANG INA** NEXT WEEK NAMAN").queue();

        }
    }
}

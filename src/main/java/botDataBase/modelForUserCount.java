package botDataBase;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class modelForUserCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String memberName;

    private int swearCounter;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getSwearCounter() {
        return swearCounter;
    }

    public void setSwearCounter(int swearCounter) {
        this.swearCounter = swearCounter;
    }


    @Override
    public String toString() {
        return "modelForUserCount{" +
                "id=" + id +
                ", memberName='" + memberName + '\'' +
                ", swearCounter=" + swearCounter +
                '}';
    }

    public modelForUserCount() {
    }
}

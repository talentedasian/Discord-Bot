package botDataBase;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class ModelForUserCount implements Serializable {

    @Id
    private long id;

    @Nonnull
    private String memberName;

    @Nullable
    private int swearCounter;

    @Nonnull
    private boolean added;

    private int messageCount;

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

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
        return "ModelForUserCount{" +
                "id=" + id +
                ", memberName='" + memberName + '\'' +
                ", swearCounter=" + swearCounter +
                ", added=" + added +
                ", messageCount=" + messageCount +
                '}';
    }

    public ModelForUserCount() {
    }


}

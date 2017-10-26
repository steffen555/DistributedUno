import java.io.Serializable;

public interface Player extends Serializable{
    Move receiveMove(Communicator communicator);
}

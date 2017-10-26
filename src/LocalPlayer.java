public class LocalPlayer extends Player {
    @Override
    public Move receiveMove(Communicator communicator) {
        return communicator.receiveMoveFromLocalUser(this);
    }
}

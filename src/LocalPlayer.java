public class LocalPlayer implements Player {
    @Override
    public Move receiveMove(Communicator communicator) {
        return communicator.receiveMoveFromLocalUser();
    }
}

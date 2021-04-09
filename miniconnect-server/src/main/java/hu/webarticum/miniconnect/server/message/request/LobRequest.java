package hu.webarticum.miniconnect.server.message.request;

public final class LobRequest implements Request {

    private final int id;

    private final long length;


    public LobRequest(int id, long length) {
        this.id = id;
        this.length = length;
    }


    public int id() {
        return id;
    }

    public long length() {
        return length;
    }

}
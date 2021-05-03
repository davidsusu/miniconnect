package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public class LobResultResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int lobId;

    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final String variableName;


    public LobResultResponse(
            long sessionId,
            int lobId,
            boolean success,
            String errorCode,
            String errorMessage,
            String variableName) {

        this.sessionId = sessionId;
        this.lobId = lobId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.variableName = variableName;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int lobId() {
        return lobId;
    }

    public boolean success() {
        return success;
    }

    public String errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public String getVariableName() {
        return variableName;
    }

}

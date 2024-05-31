package ma.n1akai.dorosonline.util;

public class UiState<T> {

    State state;
    String message;
    T data;

    public UiState(State state) {
        this.state = state;
    }

    public UiState(State state, String message) {
        this.state = state;
        this.message = message;
    }

    public UiState(State state, T data) {
        this.state = state;
        this.data = data;
    }



    public State getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}

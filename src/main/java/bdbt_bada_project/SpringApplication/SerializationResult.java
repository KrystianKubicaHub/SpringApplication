package bdbt_bada_project.SpringApplication;

public class SerializationResult {
    private final boolean isSerializable;
    private final String errorMessage;

    public SerializationResult(boolean isSerializable, String errorMessage) {
        this.isSerializable = isSerializable;
        this.errorMessage = errorMessage;
    }

    public boolean isSerializable() {
        return isSerializable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "SerializationResult{" +
                "isSerializable=" + isSerializable +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}


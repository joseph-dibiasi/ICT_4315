package dtos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
 * ParkingResponse is a DTO responsible for communicating the status of a command execution back to the client.
 * In addition to a status code, a message is included to provide additional information about the result of the command execution.
 */
public class ParkingResponse {

    private static final Gson GSON = new GsonBuilder().create();

    private int statusCode;
    private String message;

    public ParkingResponse() {
        this(200, "");
    }

    public ParkingResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static ParkingResponse fromJson(String json) {
        return GSON.fromJson(json, ParkingResponse.class);
    }

    @Override
    public String toString() {
        return "ParkingResponse{statusCode=" + statusCode + ", message='" + message + "'}";
    }
}

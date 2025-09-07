package learning.outcome.utils;

import java.time.LocalDate;

public class ApiResponse<T> {
 private String sucess;
  private  String message;
   private T data;
    private LocalDate   timestamp;

    public ApiResponse(String sucess, String message, T data) {
        this.sucess = sucess;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String sucess,String message) {
        this.sucess = sucess;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("true",message);
    }
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("true",message,data);
    }
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("false",message);
    }

    public ApiResponse(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public String getSucess() {
        return sucess;
    }

    public void setSucess(String sucess) {
        this.sucess = sucess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }
}


package properties;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class Session {
    private int id;
    private LocalDateTime dateAndTime;
    private int filmId;

    public String getDay(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateAndTime.format(formatter);
    }

    public String getTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateAndTime.format(formatter);
    }
}

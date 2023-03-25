package app.shared;

import app.adapters.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseModel {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_MODIFIED_AT = "modifiedAt";
    public String id;
    @JsonSerialize(using = DateSerializer.class)
    private LocalDateTime createdAt;
    @JsonSerialize(using = DateSerializer.class)
    private LocalDateTime modifiedAt;
}
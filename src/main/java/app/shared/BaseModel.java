package app.shared;

import app.adapters.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel extends ReactivePanacheMongoEntityBase {
  public static final String FIELD_ID = "_id";
  public static final String FIELD_CREATED_AT = "createdAt";
  public static final String FIELD_MODIFIED_AT = "modifiedAt";
  @BsonId
  public String id;
  @JsonSerialize(using = DateSerializer.class)
  private LocalDateTime createdAt;
  @JsonSerialize(using = DateSerializer.class)
  private LocalDateTime modifiedAt;
}
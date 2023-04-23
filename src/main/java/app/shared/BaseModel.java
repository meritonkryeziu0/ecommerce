package app.shared;

import app.adapters.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;

@Data
public class BaseModel extends ReactivePanacheMongoEntityBase {
  public static final String FIELD_ID = "_id";
  public static final String FIELD_MODIFIED_AT = "modifiedAt";
  @BsonId
  public String id;
  @JsonSerialize(using = DateSerializer.class)
  private LocalDateTime createdAt;
  @JsonSerialize(using = DateSerializer.class)
  private LocalDateTime modifiedAt;
}
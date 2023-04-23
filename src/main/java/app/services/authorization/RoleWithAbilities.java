package app.services.authorization;

import app.mongodb.MongoCollections;
import app.shared.BaseModel;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.ROLES_COLLECTION)
public class RoleWithAbilities extends BaseModel {
  @BsonId
  private String role;
  private List<Ability> abilities;
}

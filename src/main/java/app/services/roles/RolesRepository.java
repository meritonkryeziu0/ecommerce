package app.services.roles;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.authorization.ability.Ability;
import app.services.authorization.ability.RoleWithAbilities;
import app.services.product.models.Product;
import com.mongodb.client.model.*;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RolesRepository {

  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<RoleWithAbilities> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.ROLES_COLLECTION, RoleWithAbilities.class);
  }

  public Uni<RoleWithAbilities> addAbilityToRole(String role, List<Ability> abilities){
    return getCollection().findOneAndUpdate(
        Filters.eq(RoleWithAbilities.FIELD_ROLE, role),
        Updates.addEachToSet(RoleWithAbilities.FIELD_ABILITIES, abilities),
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );
  }

}
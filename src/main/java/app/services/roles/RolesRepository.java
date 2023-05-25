package app.services.roles;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.services.authorization.ability.Ability;
import app.services.roles.models.RoleWithAbilities;
import app.shared.SuccessResponse;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
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

  public Uni<RoleWithAbilities> removeAbilityFromRole(String role, String abilityId){
    return getCollection().findOneAndUpdate(Filters.eq(RoleWithAbilities.FIELD_ROLE, role),
        Updates.pull(RoleWithAbilities.FIELD_ABILITIES, Filters.eq(RoleWithAbilities.FIELD_ABILITY_ID, abilityId)),
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
  }
}
package app.services.accounts.models;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum Roles {
  @FieldNameConstants.Include Admin,
  @FieldNameConstants.Include Everyone,
  @FieldNameConstants.Include User;
}

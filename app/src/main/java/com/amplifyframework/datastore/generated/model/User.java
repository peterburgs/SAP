package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users")
public final class User implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField EMAIL = field("email");
  public static final QueryField USERNAME = field("username");
  public static final QueryField AVATAR_KEY = field("avatarKey");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String email;
  private final @ModelField(targetType="String", isRequired = true) String username;
  private final @ModelField(targetType="String", isRequired = true) String avatarKey;
  private final @ModelField(targetType="ProjectParticipant") @HasMany(associatedWith = "member", type = ProjectParticipant.class) List<ProjectParticipant> projects = null;
  private final @ModelField(targetType="Task") @HasMany(associatedWith = "assignee", type = Task.class) List<Task> tasks = null;
  public String getId() {
      return id;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getUsername() {
      return username;
  }
  
  public String getAvatarKey() {
      return avatarKey;
  }
  
  public List<ProjectParticipant> getProjects() {
      return projects;
  }
  
  public List<Task> getTasks() {
      return tasks;
  }
  
  private User(String id, String email, String username, String avatarKey) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.avatarKey = avatarKey;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getEmail(), user.getEmail()) &&
              ObjectsCompat.equals(getUsername(), user.getUsername()) &&
              ObjectsCompat.equals(getAvatarKey(), user.getAvatarKey());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getEmail())
      .append(getUsername())
      .append(getAvatarKey())
      .toString()
      .hashCode();
  }
  
//  @Override
//   public String toString() {
//    return new StringBuilder()
//      .append("User {")
//      .append("id=" + String.valueOf(getId()))
//      .append("email=" + String.valueOf(getEmail()))
//      .append("username=" + String.valueOf(getUsername()))
//      .append("avatarKey=" + String.valueOf(getAvatarKey()))
//      .append("}")
//      .toString();
//  }


    @Override
    public String toString() {
        return username;
    }

    public static EmailStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static User justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new User(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      email,
      username,
      avatarKey);
  }
  public interface EmailStep {
    UsernameStep email(String email);
  }
  

  public interface UsernameStep {
    AvatarKeyStep username(String username);
  }
  

  public interface AvatarKeyStep {
    BuildStep avatarKey(String avatarKey);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements EmailStep, UsernameStep, AvatarKeyStep, BuildStep {
    private String id;
    private String email;
    private String username;
    private String avatarKey;
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          email,
          username,
          avatarKey);
    }
    
    @Override
     public UsernameStep email(String email) {
        Objects.requireNonNull(email);
        this.email = email;
        return this;
    }
    
    @Override
     public AvatarKeyStep username(String username) {
        Objects.requireNonNull(username);
        this.username = username;
        return this;
    }
    
    @Override
     public BuildStep avatarKey(String avatarKey) {
        Objects.requireNonNull(avatarKey);
        this.avatarKey = avatarKey;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String email, String username, String avatarKey) {
      super.id(id);
      super.email(email)
        .username(username)
        .avatarKey(avatarKey);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder username(String username) {
      return (CopyOfBuilder) super.username(username);
    }
    
    @Override
     public CopyOfBuilder avatarKey(String avatarKey) {
      return (CopyOfBuilder) super.avatarKey(avatarKey);
    }
  }
  
}

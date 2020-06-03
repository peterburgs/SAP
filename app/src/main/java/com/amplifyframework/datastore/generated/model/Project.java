package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
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

/** This is an auto generated class representing the Project type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Projects")
public final class Project implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField KEY = field("key");
  public static final QueryField AVATAR_KEY = field("avatarKey");
  public static final QueryField CREATED_AT = field("createdAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String key;
  private final @ModelField(targetType="String", isRequired = true) String avatarKey;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime createdAt;
  private final @ModelField(targetType="ProjectParticipant") @HasMany(associatedWith = "project", type = ProjectParticipant.class) List<ProjectParticipant> members = null;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getKey() {
      return key;
  }
  
  public String getAvatarKey() {
      return avatarKey;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public List<ProjectParticipant> getMembers() {
      return members;
  }
  
  private Project(String id, String name, String key, String avatarKey, Temporal.DateTime createdAt) {
    this.id = id;
    this.name = name;
    this.key = key;
    this.avatarKey = avatarKey;
    this.createdAt = createdAt;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Project project = (Project) obj;
      return ObjectsCompat.equals(getId(), project.getId()) &&
              ObjectsCompat.equals(getName(), project.getName()) &&
              ObjectsCompat.equals(getKey(), project.getKey()) &&
              ObjectsCompat.equals(getAvatarKey(), project.getAvatarKey()) &&
              ObjectsCompat.equals(getCreatedAt(), project.getCreatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getKey())
      .append(getAvatarKey())
      .append(getCreatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Project {")
      .append("id=" + String.valueOf(getId()))
      .append("name=" + String.valueOf(getName()))
      .append("key=" + String.valueOf(getKey()))
      .append("avatarKey=" + String.valueOf(getAvatarKey()))
      .append("createdAt=" + String.valueOf(getCreatedAt()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
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
  public static Project justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Project(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      key,
      avatarKey,
      createdAt);
  }
  public interface NameStep {
    KeyStep name(String name);
  }
  

  public interface KeyStep {
    AvatarKeyStep key(String key);
  }
  

  public interface AvatarKeyStep {
    BuildStep avatarKey(String avatarKey);
  }
  

  public interface BuildStep {
    Project build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep createdAt(Temporal.DateTime createdAt);
  }
  

  public static class Builder implements NameStep, KeyStep, AvatarKeyStep, BuildStep {
    private String id;
    private String name;
    private String key;
    private String avatarKey;
    private Temporal.DateTime createdAt;
    @Override
     public Project build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Project(
          id,
          name,
          key,
          avatarKey,
          createdAt);
    }
    
    @Override
     public KeyStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public AvatarKeyStep key(String key) {
        Objects.requireNonNull(key);
        this.key = key;
        return this;
    }
    
    @Override
     public BuildStep avatarKey(String avatarKey) {
        Objects.requireNonNull(avatarKey);
        this.avatarKey = avatarKey;
        return this;
    }
    
    @Override
     public BuildStep createdAt(Temporal.DateTime createdAt) {
        this.createdAt = createdAt;
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
    private CopyOfBuilder(String id, String name, String key, String avatarKey, Temporal.DateTime createdAt) {
      super.id(id);
      super.name(name)
        .key(key)
        .avatarKey(avatarKey)
        .createdAt(createdAt);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder key(String key) {
      return (CopyOfBuilder) super.key(key);
    }
    
    @Override
     public CopyOfBuilder avatarKey(String avatarKey) {
      return (CopyOfBuilder) super.avatarKey(avatarKey);
    }
    
    @Override
     public CopyOfBuilder createdAt(Temporal.DateTime createdAt) {
      return (CopyOfBuilder) super.createdAt(createdAt);
    }
  }
  
}

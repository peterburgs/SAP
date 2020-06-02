package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasOne;

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
@Index(name = "byLeader", fields = {"leaderID"})
public final class Project implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField KEY = field("key");
  public static final QueryField LEADER_ID = field("leaderID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String key;
  private final @ModelField(targetType="ID", isRequired = true) String leaderID;
  private final @ModelField(targetType="User") @HasOne(associatedWith = "id", type = User.class) User leader = null;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getKey() {
      return key;
  }
  
  public String getLeaderId() {
      return leaderID;
  }
  
  public User getLeader() {
      return leader;
  }
  
  private Project(String id, String name, String key, String leaderID) {
    this.id = id;
    this.name = name;
    this.key = key;
    this.leaderID = leaderID;
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
              ObjectsCompat.equals(getLeaderId(), project.getLeaderId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getKey())
      .append(getLeaderId())
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
      .append("leaderID=" + String.valueOf(getLeaderId()))
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
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      key,
      leaderID);
  }
  public interface NameStep {
    KeyStep name(String name);
  }
  

  public interface KeyStep {
    LeaderIdStep key(String key);
  }
  

  public interface LeaderIdStep {
    BuildStep leaderId(String leaderId);
  }
  

  public interface BuildStep {
    Project build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements NameStep, KeyStep, LeaderIdStep, BuildStep {
    private String id;
    private String name;
    private String key;
    private String leaderID;
    @Override
     public Project build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Project(
          id,
          name,
          key,
          leaderID);
    }
    
    @Override
     public KeyStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public LeaderIdStep key(String key) {
        Objects.requireNonNull(key);
        this.key = key;
        return this;
    }
    
    @Override
     public BuildStep leaderId(String leaderId) {
        Objects.requireNonNull(leaderId);
        this.leaderID = leaderId;
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
    private CopyOfBuilder(String id, String name, String key, String leaderId) {
      super.id(id);
      super.name(name)
        .key(key)
        .leaderId(leaderId);
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
     public CopyOfBuilder leaderId(String leaderId) {
      return (CopyOfBuilder) super.leaderId(leaderId);
    }
  }
  
}

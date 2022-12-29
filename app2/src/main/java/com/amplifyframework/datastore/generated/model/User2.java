package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the User2 type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "User2s")
public final class User2 implements Model {
  public static final QueryField ID = field("User2", "id");
  public static final QueryField GENDER = field("User2", "Gender");
  public static final QueryField SID = field("User2", "sid");
  public static final QueryField AGE = field("User2", "Age");
  public static final QueryField EMAIL = field("User2", "Email");
  public static final QueryField ETHNIC = field("User2", "Ethnic");
  public static final QueryField NAME = field("User2", "Name");
  private final @ModelField(targetType="ID", isRequired = true, authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) String id;
  private final @ModelField(targetType="String", isRequired = true, authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) String Gender;
  private final @ModelField(targetType="String", authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) String sid;
  private final @ModelField(targetType="Int", isRequired = true, authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) Integer Age;
  private final @ModelField(targetType="AWSEmail", isRequired = true, authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) String Email;
  private final @ModelField(targetType="String", isRequired = true, authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) String Ethnic;
  private final @ModelField(targetType="String", isRequired = true, authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE })
  }) String Name;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getGender() {
      return Gender;
  }
  
  public String getSid() {
      return sid;
  }
  
  public Integer getAge() {
      return Age;
  }
  
  public String getEmail() {
      return Email;
  }
  
  public String getEthnic() {
      return Ethnic;
  }
  
  public String getName() {
      return Name;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private User2(String id, String Gender, String sid, Integer Age, String Email, String Ethnic, String Name) {
    this.id = id;
    this.Gender = Gender;
    this.sid = sid;
    this.Age = Age;
    this.Email = Email;
    this.Ethnic = Ethnic;
    this.Name = Name;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User2 user2 = (User2) obj;
      return ObjectsCompat.equals(getId(), user2.getId()) &&
              ObjectsCompat.equals(getGender(), user2.getGender()) &&
              ObjectsCompat.equals(getSid(), user2.getSid()) &&
              ObjectsCompat.equals(getAge(), user2.getAge()) &&
              ObjectsCompat.equals(getEmail(), user2.getEmail()) &&
              ObjectsCompat.equals(getEthnic(), user2.getEthnic()) &&
              ObjectsCompat.equals(getName(), user2.getName()) &&
              ObjectsCompat.equals(getCreatedAt(), user2.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user2.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getGender())
      .append(getSid())
      .append(getAge())
      .append(getEmail())
      .append(getEthnic())
      .append(getName())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User2 {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("Gender=" + String.valueOf(getGender()) + ", ")
      .append("sid=" + String.valueOf(getSid()) + ", ")
      .append("Age=" + String.valueOf(getAge()) + ", ")
      .append("Email=" + String.valueOf(getEmail()) + ", ")
      .append("Ethnic=" + String.valueOf(getEthnic()) + ", ")
      .append("Name=" + String.valueOf(getName()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static GenderStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static User2 justId(String id) {
    return new User2(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      Gender,
      sid,
      Age,
      Email,
      Ethnic,
      Name);
  }
  public interface GenderStep {
    AgeStep gender(String gender);
  }
  

  public interface AgeStep {
    EmailStep age(Integer age);
  }
  

  public interface EmailStep {
    EthnicStep email(String email);
  }
  

  public interface EthnicStep {
    NameStep ethnic(String ethnic);
  }
  

  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    User2 build();
    BuildStep id(String id);
    BuildStep sid(String sid);
  }
  

  public static class Builder implements GenderStep, AgeStep, EmailStep, EthnicStep, NameStep, BuildStep {
    private String id;
    private String Gender;
    private Integer Age;
    private String Email;
    private String Ethnic;
    private String Name;
    private String sid;
    @Override
     public User2 build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User2(
          id,
          Gender,
          sid,
          Age,
          Email,
          Ethnic,
          Name);
    }
    
    @Override
     public AgeStep gender(String gender) {
        Objects.requireNonNull(gender);
        this.Gender = gender;
        return this;
    }
    
    @Override
     public EmailStep age(Integer age) {
        Objects.requireNonNull(age);
        this.Age = age;
        return this;
    }
    
    @Override
     public EthnicStep email(String email) {
        Objects.requireNonNull(email);
        this.Email = email;
        return this;
    }
    
    @Override
     public NameStep ethnic(String ethnic) {
        Objects.requireNonNull(ethnic);
        this.Ethnic = ethnic;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.Name = name;
        return this;
    }
    
    @Override
     public BuildStep sid(String sid) {
        this.sid = sid;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String gender, String sid, Integer age, String email, String ethnic, String name) {
      super.id(id);
      super.gender(gender)
        .age(age)
        .email(email)
        .ethnic(ethnic)
        .name(name)
        .sid(sid);
    }
    
    @Override
     public CopyOfBuilder gender(String gender) {
      return (CopyOfBuilder) super.gender(gender);
    }
    
    @Override
     public CopyOfBuilder age(Integer age) {
      return (CopyOfBuilder) super.age(age);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder ethnic(String ethnic) {
      return (CopyOfBuilder) super.ethnic(ethnic);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder sid(String sid) {
      return (CopyOfBuilder) super.sid(sid);
    }
  }
  
}

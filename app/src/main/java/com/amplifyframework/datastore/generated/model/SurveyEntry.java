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

/** This is an auto generated class representing the SurveyEntry type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "SurveyEntries", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byUser", fields = {"userID"})
public final class SurveyEntry implements Model {
  public static final QueryField ID = field("SurveyEntry", "id");
  public static final QueryField DATE = field("SurveyEntry", "date");
  public static final QueryField SURVEY1 = field("SurveyEntry", "survey1");
  public static final QueryField SURVEY2 = field("SurveyEntry", "survey2");
  public static final QueryField SURVEY3 = field("SurveyEntry", "survey3");
  public static final QueryField USER_ID = field("SurveyEntry", "userID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime date;
  private final @ModelField(targetType="AWSJSON", isRequired = true) String survey1;
  private final @ModelField(targetType="AWSJSON", isRequired = true) String survey2;
  private final @ModelField(targetType="AWSJSON", isRequired = true) String survey3;
  private final @ModelField(targetType="ID", isRequired = true) String userID;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public Temporal.DateTime getDate() {
      return date;
  }
  
  public String getSurvey1() {
      return survey1;
  }
  
  public String getSurvey2() {
      return survey2;
  }
  
  public String getSurvey3() {
      return survey3;
  }
  
  public String getUserId() {
      return userID;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private SurveyEntry(String id, Temporal.DateTime date, String survey1, String survey2, String survey3, String userID) {
    this.id = id;
    this.date = date;
    this.survey1 = survey1;
    this.survey2 = survey2;
    this.survey3 = survey3;
    this.userID = userID;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      SurveyEntry surveyEntry = (SurveyEntry) obj;
      return ObjectsCompat.equals(getId(), surveyEntry.getId()) &&
              ObjectsCompat.equals(getDate(), surveyEntry.getDate()) &&
              ObjectsCompat.equals(getSurvey1(), surveyEntry.getSurvey1()) &&
              ObjectsCompat.equals(getSurvey2(), surveyEntry.getSurvey2()) &&
              ObjectsCompat.equals(getSurvey3(), surveyEntry.getSurvey3()) &&
              ObjectsCompat.equals(getUserId(), surveyEntry.getUserId()) &&
              ObjectsCompat.equals(getCreatedAt(), surveyEntry.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), surveyEntry.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getDate())
      .append(getSurvey1())
      .append(getSurvey2())
      .append(getSurvey3())
      .append(getUserId())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("SurveyEntry {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("survey1=" + String.valueOf(getSurvey1()) + ", ")
      .append("survey2=" + String.valueOf(getSurvey2()) + ", ")
      .append("survey3=" + String.valueOf(getSurvey3()) + ", ")
      .append("userID=" + String.valueOf(getUserId()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static DateStep builder() {
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
  public static SurveyEntry justId(String id) {
    return new SurveyEntry(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      date,
      survey1,
      survey2,
      survey3,
      userID);
  }
  public interface DateStep {
    Survey1Step date(Temporal.DateTime date);
  }
  

  public interface Survey1Step {
    Survey2Step survey1(String survey1);
  }
  

  public interface Survey2Step {
    Survey3Step survey2(String survey2);
  }
  

  public interface Survey3Step {
    UserIdStep survey3(String survey3);
  }
  

  public interface UserIdStep {
    BuildStep userId(String userId);
  }
  

  public interface BuildStep {
    SurveyEntry build();
    BuildStep id(String id);
  }
  

  public static class Builder implements DateStep, Survey1Step, Survey2Step, Survey3Step, UserIdStep, BuildStep {
    private String id;
    private Temporal.DateTime date;
    private String survey1;
    private String survey2;
    private String survey3;
    private String userID;
    @Override
     public SurveyEntry build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new SurveyEntry(
          id,
          date,
          survey1,
          survey2,
          survey3,
          userID);
    }
    
    @Override
     public Survey1Step date(Temporal.DateTime date) {
        Objects.requireNonNull(date);
        this.date = date;
        return this;
    }
    
    @Override
     public Survey2Step survey1(String survey1) {
        Objects.requireNonNull(survey1);
        this.survey1 = survey1;
        return this;
    }
    
    @Override
     public Survey3Step survey2(String survey2) {
        Objects.requireNonNull(survey2);
        this.survey2 = survey2;
        return this;
    }
    
    @Override
     public UserIdStep survey3(String survey3) {
        Objects.requireNonNull(survey3);
        this.survey3 = survey3;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userID = userId;
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
    private CopyOfBuilder(String id, Temporal.DateTime date, String survey1, String survey2, String survey3, String userId) {
      super.id(id);
      super.date(date)
        .survey1(survey1)
        .survey2(survey2)
        .survey3(survey3)
        .userId(userId);
    }
    
    @Override
     public CopyOfBuilder date(Temporal.DateTime date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder survey1(String survey1) {
      return (CopyOfBuilder) super.survey1(survey1);
    }
    
    @Override
     public CopyOfBuilder survey2(String survey2) {
      return (CopyOfBuilder) super.survey2(survey2);
    }
    
    @Override
     public CopyOfBuilder survey3(String survey3) {
      return (CopyOfBuilder) super.survey3(survey3);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
  }
  
}

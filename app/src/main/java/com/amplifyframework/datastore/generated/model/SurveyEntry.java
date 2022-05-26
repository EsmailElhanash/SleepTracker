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
public final class SurveyEntry implements Model {
  public static final QueryField ID = field("SurveyEntry", "id");
  public static final QueryField USER_ID = field("SurveyEntry", "userId");
  public static final QueryField DATE = field("SurveyEntry", "date");
  public static final QueryField SURVEY1 = field("SurveyEntry", "survey1");
  public static final QueryField SURVEY2 = field("SurveyEntry", "survey2");
  public static final QueryField SURVEY3 = field("SurveyEntry", "survey3");
  public static final QueryField USER_SURVEYS_ID = field("SurveyEntry", "userSurveysId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String userId;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime date;
  private final @ModelField(targetType="AWSJSON", isRequired = true) String survey1;
  private final @ModelField(targetType="AWSJSON", isRequired = true) String survey2;
  private final @ModelField(targetType="AWSJSON", isRequired = true) String survey3;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID") String userSurveysId;
  public String getId() {
      return id;
  }
  
  public String getUserId() {
      return userId;
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
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getUserSurveysId() {
      return userSurveysId;
  }
  
  private SurveyEntry(String id, String userId, Temporal.DateTime date, String survey1, String survey2, String survey3, String userSurveysId) {
    this.id = id;
    this.userId = userId;
    this.date = date;
    this.survey1 = survey1;
    this.survey2 = survey2;
    this.survey3 = survey3;
    this.userSurveysId = userSurveysId;
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
              ObjectsCompat.equals(getUserId(), surveyEntry.getUserId()) &&
              ObjectsCompat.equals(getDate(), surveyEntry.getDate()) &&
              ObjectsCompat.equals(getSurvey1(), surveyEntry.getSurvey1()) &&
              ObjectsCompat.equals(getSurvey2(), surveyEntry.getSurvey2()) &&
              ObjectsCompat.equals(getSurvey3(), surveyEntry.getSurvey3()) &&
              ObjectsCompat.equals(getCreatedAt(), surveyEntry.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), surveyEntry.getUpdatedAt()) &&
              ObjectsCompat.equals(getUserSurveysId(), surveyEntry.getUserSurveysId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getDate())
      .append(getSurvey1())
      .append(getSurvey2())
      .append(getSurvey3())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getUserSurveysId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("SurveyEntry {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("survey1=" + String.valueOf(getSurvey1()) + ", ")
      .append("survey2=" + String.valueOf(getSurvey2()) + ", ")
      .append("survey3=" + String.valueOf(getSurvey3()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("userSurveysId=" + String.valueOf(getUserSurveysId()))
      .append("}")
      .toString();
  }
  
  public static UserIdStep builder() {
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
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      userId,
      date,
      survey1,
      survey2,
      survey3,
      userSurveysId);
  }
  public interface UserIdStep {
    DateStep userId(String userId);
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
    BuildStep survey3(String survey3);
  }
  

  public interface BuildStep {
    SurveyEntry build();
    BuildStep id(String id);
    BuildStep userSurveysId(String userSurveysId);
  }
  

  public static class Builder implements UserIdStep, DateStep, Survey1Step, Survey2Step, Survey3Step, BuildStep {
    private String id;
    private String userId;
    private Temporal.DateTime date;
    private String survey1;
    private String survey2;
    private String survey3;
    private String userSurveysId;
    @Override
     public SurveyEntry build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new SurveyEntry(
          id,
          userId,
          date,
          survey1,
          survey2,
          survey3,
          userSurveysId);
    }
    
    @Override
     public DateStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userId = userId;
        return this;
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
     public BuildStep survey3(String survey3) {
        Objects.requireNonNull(survey3);
        this.survey3 = survey3;
        return this;
    }
    
    @Override
     public BuildStep userSurveysId(String userSurveysId) {
        this.userSurveysId = userSurveysId;
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
    private CopyOfBuilder(String id, String userId, Temporal.DateTime date, String survey1, String survey2, String survey3, String userSurveysId) {
      super.id(id);
      super.userId(userId)
        .date(date)
        .survey1(survey1)
        .survey2(survey2)
        .survey3(survey3)
        .userSurveysId(userSurveysId);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
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
     public CopyOfBuilder userSurveysId(String userSurveysId) {
      return (CopyOfBuilder) super.userSurveysId(userSurveysId);
    }
  }
  
}

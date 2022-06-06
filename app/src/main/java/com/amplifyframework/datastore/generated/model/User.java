package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.annotations.HasMany;

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

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class User implements Model {
  public static final QueryField ID = field("User", "id");
  public static final QueryField SID = field("User", "sid");
  public static final QueryField CONSENT = field("User", "consent");
  public static final QueryField OFF_DAY = field("User", "offDay");
  public static final QueryField WORKDAY = field("User", "workday");
  public static final QueryField SURVEY_LAST_UPDATE = field("User", "surveyLastUpdate");
  public static final QueryField SURVEY_LAST_UPDATE2 = field("User", "surveyLastUpdate2");
  public static final QueryField RETAKE_SURVEY_PERIOD = field("User", "retakeSurveyPeriod");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String sid;
  private final @ModelField(targetType="String") String consent;
  private final @ModelField(targetType="DayGroup", isRequired = true) DayGroup offDay;
  private final @ModelField(targetType="DayGroup", isRequired = true) DayGroup workday;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime surveyLastUpdate;
  private final @ModelField(targetType="SurveyUpdateLastCase2") SurveyUpdateLastCase2 surveyLastUpdate2;
  private final @ModelField(targetType="Int", isRequired = true) Integer retakeSurveyPeriod;
  private final @ModelField(targetType="SurveyEntry") @HasMany(associatedWith = "userID", type = SurveyEntry.class) List<SurveyEntry> surveys = null;
  private final @ModelField(targetType="TrackerPeriod") @HasMany(associatedWith = "userID", type = TrackerPeriod.class) List<TrackerPeriod> tracker = null;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getSid() {
      return sid;
  }
  
  public String getConsent() {
      return consent;
  }
  
  public DayGroup getOffDay() {
      return offDay;
  }
  
  public DayGroup getWorkday() {
      return workday;
  }
  
  public Temporal.DateTime getSurveyLastUpdate() {
      return surveyLastUpdate;
  }
  
  public SurveyUpdateLastCase2 getSurveyLastUpdate2() {
      return surveyLastUpdate2;
  }
  
  public Integer getRetakeSurveyPeriod() {
      return retakeSurveyPeriod;
  }
  
  public List<SurveyEntry> getSurveys() {
      return surveys;
  }
  
  public List<TrackerPeriod> getTracker() {
      return tracker;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private User(String id, String sid, String consent, DayGroup offDay, DayGroup workday, Temporal.DateTime surveyLastUpdate, SurveyUpdateLastCase2 surveyLastUpdate2, Integer retakeSurveyPeriod) {
    this.id = id;
    this.sid = sid;
    this.consent = consent;
    this.offDay = offDay;
    this.workday = workday;
    this.surveyLastUpdate = surveyLastUpdate;
    this.surveyLastUpdate2 = surveyLastUpdate2;
    this.retakeSurveyPeriod = retakeSurveyPeriod;
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
              ObjectsCompat.equals(getSid(), user.getSid()) &&
              ObjectsCompat.equals(getConsent(), user.getConsent()) &&
              ObjectsCompat.equals(getOffDay(), user.getOffDay()) &&
              ObjectsCompat.equals(getWorkday(), user.getWorkday()) &&
              ObjectsCompat.equals(getSurveyLastUpdate(), user.getSurveyLastUpdate()) &&
              ObjectsCompat.equals(getSurveyLastUpdate2(), user.getSurveyLastUpdate2()) &&
              ObjectsCompat.equals(getRetakeSurveyPeriod(), user.getRetakeSurveyPeriod()) &&
              ObjectsCompat.equals(getCreatedAt(), user.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSid())
      .append(getConsent())
      .append(getOffDay())
      .append(getWorkday())
      .append(getSurveyLastUpdate())
      .append(getSurveyLastUpdate2())
      .append(getRetakeSurveyPeriod())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("sid=" + String.valueOf(getSid()) + ", ")
      .append("consent=" + String.valueOf(getConsent()) + ", ")
      .append("offDay=" + String.valueOf(getOffDay()) + ", ")
      .append("workday=" + String.valueOf(getWorkday()) + ", ")
      .append("surveyLastUpdate=" + String.valueOf(getSurveyLastUpdate()) + ", ")
      .append("surveyLastUpdate2=" + String.valueOf(getSurveyLastUpdate2()) + ", ")
      .append("retakeSurveyPeriod=" + String.valueOf(getRetakeSurveyPeriod()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static SidStep builder() {
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
  public static User justId(String id) {
    return new User(
      id,
      null,
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
      sid,
      consent,
      offDay,
      workday,
      surveyLastUpdate,
      surveyLastUpdate2,
      retakeSurveyPeriod);
  }
  public interface SidStep {
    OffDayStep sid(String sid);
  }
  

  public interface OffDayStep {
    WorkdayStep offDay(DayGroup offDay);
  }
  

  public interface WorkdayStep {
    RetakeSurveyPeriodStep workday(DayGroup workday);
  }
  

  public interface RetakeSurveyPeriodStep {
    BuildStep retakeSurveyPeriod(Integer retakeSurveyPeriod);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id);
    BuildStep consent(String consent);
    BuildStep surveyLastUpdate(Temporal.DateTime surveyLastUpdate);
    BuildStep surveyLastUpdate2(SurveyUpdateLastCase2 surveyLastUpdate2);
  }
  

  public static class Builder implements SidStep, OffDayStep, WorkdayStep, RetakeSurveyPeriodStep, BuildStep {
    private String id;
    private String sid;
    private DayGroup offDay;
    private DayGroup workday;
    private Integer retakeSurveyPeriod;
    private String consent;
    private Temporal.DateTime surveyLastUpdate;
    private SurveyUpdateLastCase2 surveyLastUpdate2;
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          sid,
          consent,
          offDay,
          workday,
          surveyLastUpdate,
          surveyLastUpdate2,
          retakeSurveyPeriod);
    }
    
    @Override
     public OffDayStep sid(String sid) {
        Objects.requireNonNull(sid);
        this.sid = sid;
        return this;
    }
    
    @Override
     public WorkdayStep offDay(DayGroup offDay) {
        Objects.requireNonNull(offDay);
        this.offDay = offDay;
        return this;
    }
    
    @Override
     public RetakeSurveyPeriodStep workday(DayGroup workday) {
        Objects.requireNonNull(workday);
        this.workday = workday;
        return this;
    }
    
    @Override
     public BuildStep retakeSurveyPeriod(Integer retakeSurveyPeriod) {
        Objects.requireNonNull(retakeSurveyPeriod);
        this.retakeSurveyPeriod = retakeSurveyPeriod;
        return this;
    }
    
    @Override
     public BuildStep consent(String consent) {
        this.consent = consent;
        return this;
    }
    
    @Override
     public BuildStep surveyLastUpdate(Temporal.DateTime surveyLastUpdate) {
        this.surveyLastUpdate = surveyLastUpdate;
        return this;
    }
    
    @Override
     public BuildStep surveyLastUpdate2(SurveyUpdateLastCase2 surveyLastUpdate2) {
        this.surveyLastUpdate2 = surveyLastUpdate2;
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
    private CopyOfBuilder(String id, String sid, String consent, DayGroup offDay, DayGroup workday, Temporal.DateTime surveyLastUpdate, SurveyUpdateLastCase2 surveyLastUpdate2, Integer retakeSurveyPeriod) {
      super.id(id);
      super.sid(sid)
        .offDay(offDay)
        .workday(workday)
        .retakeSurveyPeriod(retakeSurveyPeriod)
        .consent(consent)
        .surveyLastUpdate(surveyLastUpdate)
        .surveyLastUpdate2(surveyLastUpdate2);
    }
    
    @Override
     public CopyOfBuilder sid(String sid) {
      return (CopyOfBuilder) super.sid(sid);
    }
    
    @Override
     public CopyOfBuilder offDay(DayGroup offDay) {
      return (CopyOfBuilder) super.offDay(offDay);
    }
    
    @Override
     public CopyOfBuilder workday(DayGroup workday) {
      return (CopyOfBuilder) super.workday(workday);
    }
    
    @Override
     public CopyOfBuilder retakeSurveyPeriod(Integer retakeSurveyPeriod) {
      return (CopyOfBuilder) super.retakeSurveyPeriod(retakeSurveyPeriod);
    }
    
    @Override
     public CopyOfBuilder consent(String consent) {
      return (CopyOfBuilder) super.consent(consent);
    }
    
    @Override
     public CopyOfBuilder surveyLastUpdate(Temporal.DateTime surveyLastUpdate) {
      return (CopyOfBuilder) super.surveyLastUpdate(surveyLastUpdate);
    }
    
    @Override
     public CopyOfBuilder surveyLastUpdate2(SurveyUpdateLastCase2 surveyLastUpdate2) {
      return (CopyOfBuilder) super.surveyLastUpdate2(surveyLastUpdate2);
    }
  }
  
}

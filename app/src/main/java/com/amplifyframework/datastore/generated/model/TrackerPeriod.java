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

/** This is an auto generated class representing the TrackerPeriod type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "TrackerPeriods", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byUser", fields = {"userID"})
public final class TrackerPeriod implements Model {
  public static final QueryField ID = field("TrackerPeriod", "id");
  public static final QueryField WAKE_UP_TIME = field("TrackerPeriod", "WakeUpTime");
  public static final QueryField SLEEP_TIME = field("TrackerPeriod", "SleepTime");
  public static final QueryField AVERAGE_MOVEMENT_COUNT = field("TrackerPeriod", "AverageMovementCount");
  public static final QueryField SLEEP_DURATION = field("TrackerPeriod", "SleepDuration");
  public static final QueryField DURATION_IN_NUMBERS = field("TrackerPeriod", "DurationInNumbers");
  public static final QueryField DISTURBANCES_COUNT = field("TrackerPeriod", "DisturbancesCount");
  public static final QueryField TOTAL_MOVEMENTS = field("TrackerPeriod", "TotalMovements");
  public static final QueryField ACCELEROMETER_LAST_READING = field("TrackerPeriod", "AccelerometerLastReading");
  public static final QueryField CREATED_AT = field("TrackerPeriod", "createdAt");
  public static final QueryField USER_ID = field("TrackerPeriod", "userID");
  public static final QueryField ACTUAL_SLEEP_TIME = field("TrackerPeriod", "ActualSleepTime");
  public static final QueryField ACTUAL_WAKE_UP_TIME = field("TrackerPeriod", "ActualWakeUpTime");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String WakeUpTime;
  private final @ModelField(targetType="String", isRequired = true) String SleepTime;
  private final @ModelField(targetType="String") String AverageMovementCount;
  private final @ModelField(targetType="String") String SleepDuration;
  private final @ModelField(targetType="String") String DurationInNumbers;
  private final @ModelField(targetType="String") String DisturbancesCount;
  private final @ModelField(targetType="Int") Integer TotalMovements;
  private final @ModelField(targetType="Float") Double AccelerometerLastReading;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime createdAt;
  private final @ModelField(targetType="ID", isRequired = true) String userID;
  private final @ModelField(targetType="Session") @HasMany(associatedWith = "trackerperiodID", type = Session.class) List<Session> Sessions = null;
  private final @ModelField(targetType="DeviceState") @HasMany(associatedWith = "trackerperiodID", type = DeviceState.class) List<DeviceState> DeviceStates = null;
  private final @ModelField(targetType="SubPeriod") @HasMany(associatedWith = "trackerperiodID", type = SubPeriod.class) List<SubPeriod> SubPeriods = null;
  private final @ModelField(targetType="String") String ActualSleepTime;
  private final @ModelField(targetType="String") String ActualWakeUpTime;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getWakeUpTime() {
      return WakeUpTime;
  }
  
  public String getSleepTime() {
      return SleepTime;
  }
  
  public String getAverageMovementCount() {
      return AverageMovementCount;
  }
  
  public String getSleepDuration() {
      return SleepDuration;
  }
  
  public String getDurationInNumbers() {
      return DurationInNumbers;
  }
  
  public String getDisturbancesCount() {
      return DisturbancesCount;
  }
  
  public Integer getTotalMovements() {
      return TotalMovements;
  }
  
  public Double getAccelerometerLastReading() {
      return AccelerometerLastReading;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public String getUserId() {
      return userID;
  }
  
  public List<Session> getSessions() {
      return Sessions;
  }
  
  public List<DeviceState> getDeviceStates() {
      return DeviceStates;
  }
  
  public List<SubPeriod> getSubPeriods() {
      return SubPeriods;
  }
  
  public String getActualSleepTime() {
      return ActualSleepTime;
  }
  
  public String getActualWakeUpTime() {
      return ActualWakeUpTime;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private TrackerPeriod(String id, String WakeUpTime, String SleepTime, String AverageMovementCount, String SleepDuration, String DurationInNumbers, String DisturbancesCount, Integer TotalMovements, Double AccelerometerLastReading, Temporal.DateTime createdAt, String userID, String ActualSleepTime, String ActualWakeUpTime) {
    this.id = id;
    this.WakeUpTime = WakeUpTime;
    this.SleepTime = SleepTime;
    this.AverageMovementCount = AverageMovementCount;
    this.SleepDuration = SleepDuration;
    this.DurationInNumbers = DurationInNumbers;
    this.DisturbancesCount = DisturbancesCount;
    this.TotalMovements = TotalMovements;
    this.AccelerometerLastReading = AccelerometerLastReading;
    this.createdAt = createdAt;
    this.userID = userID;
    this.ActualSleepTime = ActualSleepTime;
    this.ActualWakeUpTime = ActualWakeUpTime;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      TrackerPeriod trackerPeriod = (TrackerPeriod) obj;
      return ObjectsCompat.equals(getId(), trackerPeriod.getId()) &&
              ObjectsCompat.equals(getWakeUpTime(), trackerPeriod.getWakeUpTime()) &&
              ObjectsCompat.equals(getSleepTime(), trackerPeriod.getSleepTime()) &&
              ObjectsCompat.equals(getAverageMovementCount(), trackerPeriod.getAverageMovementCount()) &&
              ObjectsCompat.equals(getSleepDuration(), trackerPeriod.getSleepDuration()) &&
              ObjectsCompat.equals(getDurationInNumbers(), trackerPeriod.getDurationInNumbers()) &&
              ObjectsCompat.equals(getDisturbancesCount(), trackerPeriod.getDisturbancesCount()) &&
              ObjectsCompat.equals(getTotalMovements(), trackerPeriod.getTotalMovements()) &&
              ObjectsCompat.equals(getAccelerometerLastReading(), trackerPeriod.getAccelerometerLastReading()) &&
              ObjectsCompat.equals(getCreatedAt(), trackerPeriod.getCreatedAt()) &&
              ObjectsCompat.equals(getUserId(), trackerPeriod.getUserId()) &&
              ObjectsCompat.equals(getActualSleepTime(), trackerPeriod.getActualSleepTime()) &&
              ObjectsCompat.equals(getActualWakeUpTime(), trackerPeriod.getActualWakeUpTime()) &&
              ObjectsCompat.equals(getUpdatedAt(), trackerPeriod.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getWakeUpTime())
      .append(getSleepTime())
      .append(getAverageMovementCount())
      .append(getSleepDuration())
      .append(getDurationInNumbers())
      .append(getDisturbancesCount())
      .append(getTotalMovements())
      .append(getAccelerometerLastReading())
      .append(getCreatedAt())
      .append(getUserId())
      .append(getActualSleepTime())
      .append(getActualWakeUpTime())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("TrackerPeriod {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("WakeUpTime=" + String.valueOf(getWakeUpTime()) + ", ")
      .append("SleepTime=" + String.valueOf(getSleepTime()) + ", ")
      .append("AverageMovementCount=" + String.valueOf(getAverageMovementCount()) + ", ")
      .append("SleepDuration=" + String.valueOf(getSleepDuration()) + ", ")
      .append("DurationInNumbers=" + String.valueOf(getDurationInNumbers()) + ", ")
      .append("DisturbancesCount=" + String.valueOf(getDisturbancesCount()) + ", ")
      .append("TotalMovements=" + String.valueOf(getTotalMovements()) + ", ")
      .append("AccelerometerLastReading=" + String.valueOf(getAccelerometerLastReading()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("userID=" + String.valueOf(getUserId()) + ", ")
      .append("ActualSleepTime=" + String.valueOf(getActualSleepTime()) + ", ")
      .append("ActualWakeUpTime=" + String.valueOf(getActualWakeUpTime()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static WakeUpTimeStep builder() {
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
  public static TrackerPeriod justId(String id) {
    return new TrackerPeriod(
      id,
      null,
      null,
      null,
      null,
      null,
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
      WakeUpTime,
      SleepTime,
      AverageMovementCount,
      SleepDuration,
      DurationInNumbers,
      DisturbancesCount,
      TotalMovements,
      AccelerometerLastReading,
      createdAt,
      userID,
      ActualSleepTime,
      ActualWakeUpTime);
  }
  public interface WakeUpTimeStep {
    SleepTimeStep wakeUpTime(String wakeUpTime);
  }
  

  public interface SleepTimeStep {
    CreatedAtStep sleepTime(String sleepTime);
  }
  

  public interface CreatedAtStep {
    UserIdStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UserIdStep {
    BuildStep userId(String userId);
  }
  

  public interface BuildStep {
    TrackerPeriod build();
    BuildStep id(String id);
    BuildStep averageMovementCount(String averageMovementCount);
    BuildStep sleepDuration(String sleepDuration);
    BuildStep durationInNumbers(String durationInNumbers);
    BuildStep disturbancesCount(String disturbancesCount);
    BuildStep totalMovements(Integer totalMovements);
    BuildStep accelerometerLastReading(Double accelerometerLastReading);
    BuildStep actualSleepTime(String actualSleepTime);
    BuildStep actualWakeUpTime(String actualWakeUpTime);
  }
  

  public static class Builder implements WakeUpTimeStep, SleepTimeStep, CreatedAtStep, UserIdStep, BuildStep {
    private String id;
    private String WakeUpTime;
    private String SleepTime;
    private Temporal.DateTime createdAt;
    private String userID;
    private String AverageMovementCount;
    private String SleepDuration;
    private String DurationInNumbers;
    private String DisturbancesCount;
    private Integer TotalMovements;
    private Double AccelerometerLastReading;
    private String ActualSleepTime;
    private String ActualWakeUpTime;
    @Override
     public TrackerPeriod build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new TrackerPeriod(
          id,
          WakeUpTime,
          SleepTime,
          AverageMovementCount,
          SleepDuration,
          DurationInNumbers,
          DisturbancesCount,
          TotalMovements,
          AccelerometerLastReading,
          createdAt,
          userID,
          ActualSleepTime,
          ActualWakeUpTime);
    }
    
    @Override
     public SleepTimeStep wakeUpTime(String wakeUpTime) {
        Objects.requireNonNull(wakeUpTime);
        this.WakeUpTime = wakeUpTime;
        return this;
    }
    
    @Override
     public CreatedAtStep sleepTime(String sleepTime) {
        Objects.requireNonNull(sleepTime);
        this.SleepTime = sleepTime;
        return this;
    }
    
    @Override
     public UserIdStep createdAt(Temporal.DateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userID = userId;
        return this;
    }
    
    @Override
     public BuildStep averageMovementCount(String averageMovementCount) {
        this.AverageMovementCount = averageMovementCount;
        return this;
    }
    
    @Override
     public BuildStep sleepDuration(String sleepDuration) {
        this.SleepDuration = sleepDuration;
        return this;
    }
    
    @Override
     public BuildStep durationInNumbers(String durationInNumbers) {
        this.DurationInNumbers = durationInNumbers;
        return this;
    }
    
    @Override
     public BuildStep disturbancesCount(String disturbancesCount) {
        this.DisturbancesCount = disturbancesCount;
        return this;
    }
    
    @Override
     public BuildStep totalMovements(Integer totalMovements) {
        this.TotalMovements = totalMovements;
        return this;
    }
    
    @Override
     public BuildStep accelerometerLastReading(Double accelerometerLastReading) {
        this.AccelerometerLastReading = accelerometerLastReading;
        return this;
    }
    
    @Override
     public BuildStep actualSleepTime(String actualSleepTime) {
        this.ActualSleepTime = actualSleepTime;
        return this;
    }
    
    @Override
     public BuildStep actualWakeUpTime(String actualWakeUpTime) {
        this.ActualWakeUpTime = actualWakeUpTime;
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
    private CopyOfBuilder(String id, String wakeUpTime, String sleepTime, String averageMovementCount, String sleepDuration, String durationInNumbers, String disturbancesCount, Integer totalMovements, Double accelerometerLastReading, Temporal.DateTime createdAt, String userId, String actualSleepTime, String actualWakeUpTime) {
      super.id(id);
      super.wakeUpTime(wakeUpTime)
        .sleepTime(sleepTime)
        .createdAt(createdAt)
        .userId(userId)
        .averageMovementCount(averageMovementCount)
        .sleepDuration(sleepDuration)
        .durationInNumbers(durationInNumbers)
        .disturbancesCount(disturbancesCount)
        .totalMovements(totalMovements)
        .accelerometerLastReading(accelerometerLastReading)
        .actualSleepTime(actualSleepTime)
        .actualWakeUpTime(actualWakeUpTime);
    }
    
    @Override
     public CopyOfBuilder wakeUpTime(String wakeUpTime) {
      return (CopyOfBuilder) super.wakeUpTime(wakeUpTime);
    }
    
    @Override
     public CopyOfBuilder sleepTime(String sleepTime) {
      return (CopyOfBuilder) super.sleepTime(sleepTime);
    }
    
    @Override
     public CopyOfBuilder createdAt(Temporal.DateTime createdAt) {
      return (CopyOfBuilder) super.createdAt(createdAt);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder averageMovementCount(String averageMovementCount) {
      return (CopyOfBuilder) super.averageMovementCount(averageMovementCount);
    }
    
    @Override
     public CopyOfBuilder sleepDuration(String sleepDuration) {
      return (CopyOfBuilder) super.sleepDuration(sleepDuration);
    }
    
    @Override
     public CopyOfBuilder durationInNumbers(String durationInNumbers) {
      return (CopyOfBuilder) super.durationInNumbers(durationInNumbers);
    }
    
    @Override
     public CopyOfBuilder disturbancesCount(String disturbancesCount) {
      return (CopyOfBuilder) super.disturbancesCount(disturbancesCount);
    }
    
    @Override
     public CopyOfBuilder totalMovements(Integer totalMovements) {
      return (CopyOfBuilder) super.totalMovements(totalMovements);
    }
    
    @Override
     public CopyOfBuilder accelerometerLastReading(Double accelerometerLastReading) {
      return (CopyOfBuilder) super.accelerometerLastReading(accelerometerLastReading);
    }
    
    @Override
     public CopyOfBuilder actualSleepTime(String actualSleepTime) {
      return (CopyOfBuilder) super.actualSleepTime(actualSleepTime);
    }
    
    @Override
     public CopyOfBuilder actualWakeUpTime(String actualWakeUpTime) {
      return (CopyOfBuilder) super.actualWakeUpTime(actualWakeUpTime);
    }
  }
  
}

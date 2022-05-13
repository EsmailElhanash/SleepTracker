package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
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

/** This is an auto generated class representing the TrackerPeriod type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "TrackerPeriods", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class TrackerPeriod implements Model {
  public static final QueryField ID = field("TrackerPeriod", "id");
  public static final QueryField USER_ID = field("TrackerPeriod", "userId");
  public static final QueryField WAKE_UP_TIME = field("TrackerPeriod", "WakeUpTime");
  public static final QueryField SLEEP_TIME = field("TrackerPeriod", "SleepTime");
  public static final QueryField ENDED = field("TrackerPeriod", "ended");
  public static final QueryField AVERAGE_MOVEMENT_COUNT = field("TrackerPeriod", "AverageMovementCount");
  public static final QueryField SLEEP_DURATION = field("TrackerPeriod", "SleepDuration");
  public static final QueryField DURATION_IN_NUMBERS = field("TrackerPeriod", "DurationInNumbers");
  public static final QueryField DISTURBANCES_COUNT = field("TrackerPeriod", "DisturbancesCount");
  public static final QueryField TOTAL_MOVEMENTS = field("TrackerPeriod", "TotalMovements");
  public static final QueryField ACCELEROMETER_LAST_READING = field("TrackerPeriod", "AccelerometerLastReading");
  public static final QueryField USER_TRACKER_ID = field("TrackerPeriod", "userTrackerId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String userId;
  private final @ModelField(targetType="String", isRequired = true) String WakeUpTime;
  private final @ModelField(targetType="String", isRequired = true) String SleepTime;
  private final @ModelField(targetType="String") String ended;
  private final @ModelField(targetType="String") String AverageMovementCount;
  private final @ModelField(targetType="String") String SleepDuration;
  private final @ModelField(targetType="String") String DurationInNumbers;
  private final @ModelField(targetType="String") String DisturbancesCount;
  private final @ModelField(targetType="SubPeriod") @HasMany(associatedWith = "trackerPeriodSubPeriodsId", type = SubPeriod.class) List<SubPeriod> SubPeriods = null;
  private final @ModelField(targetType="DeviceState") @HasMany(associatedWith = "trackerPeriodDeviceStatesId", type = DeviceState.class) List<DeviceState> DeviceStates = null;
  private final @ModelField(targetType="Int") Integer TotalMovements;
  private final @ModelField(targetType="Float") Double AccelerometerLastReading;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID") String userTrackerId;
  public String getId() {
      return id;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public String getWakeUpTime() {
      return WakeUpTime;
  }
  
  public String getSleepTime() {
      return SleepTime;
  }
  
  public String getEnded() {
      return ended;
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
  
  public List<SubPeriod> getSubPeriods() {
      return SubPeriods;
  }
  
  public List<DeviceState> getDeviceStates() {
      return DeviceStates;
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
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getUserTrackerId() {
      return userTrackerId;
  }
  
  private TrackerPeriod(String id, String userId, String WakeUpTime, String SleepTime, String ended, String AverageMovementCount, String SleepDuration, String DurationInNumbers, String DisturbancesCount, Integer TotalMovements, Double AccelerometerLastReading, String userTrackerId) {
    this.id = id;
    this.userId = userId;
    this.WakeUpTime = WakeUpTime;
    this.SleepTime = SleepTime;
    this.ended = ended;
    this.AverageMovementCount = AverageMovementCount;
    this.SleepDuration = SleepDuration;
    this.DurationInNumbers = DurationInNumbers;
    this.DisturbancesCount = DisturbancesCount;
    this.TotalMovements = TotalMovements;
    this.AccelerometerLastReading = AccelerometerLastReading;
    this.userTrackerId = userTrackerId;
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
              ObjectsCompat.equals(getUserId(), trackerPeriod.getUserId()) &&
              ObjectsCompat.equals(getWakeUpTime(), trackerPeriod.getWakeUpTime()) &&
              ObjectsCompat.equals(getSleepTime(), trackerPeriod.getSleepTime()) &&
              ObjectsCompat.equals(getEnded(), trackerPeriod.getEnded()) &&
              ObjectsCompat.equals(getAverageMovementCount(), trackerPeriod.getAverageMovementCount()) &&
              ObjectsCompat.equals(getSleepDuration(), trackerPeriod.getSleepDuration()) &&
              ObjectsCompat.equals(getDurationInNumbers(), trackerPeriod.getDurationInNumbers()) &&
              ObjectsCompat.equals(getDisturbancesCount(), trackerPeriod.getDisturbancesCount()) &&
              ObjectsCompat.equals(getTotalMovements(), trackerPeriod.getTotalMovements()) &&
              ObjectsCompat.equals(getAccelerometerLastReading(), trackerPeriod.getAccelerometerLastReading()) &&
              ObjectsCompat.equals(getCreatedAt(), trackerPeriod.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), trackerPeriod.getUpdatedAt()) &&
              ObjectsCompat.equals(getUserTrackerId(), trackerPeriod.getUserTrackerId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getWakeUpTime())
      .append(getSleepTime())
      .append(getEnded())
      .append(getAverageMovementCount())
      .append(getSleepDuration())
      .append(getDurationInNumbers())
      .append(getDisturbancesCount())
      .append(getTotalMovements())
      .append(getAccelerometerLastReading())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getUserTrackerId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("TrackerPeriod {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("WakeUpTime=" + String.valueOf(getWakeUpTime()) + ", ")
      .append("SleepTime=" + String.valueOf(getSleepTime()) + ", ")
      .append("ended=" + String.valueOf(getEnded()) + ", ")
      .append("AverageMovementCount=" + String.valueOf(getAverageMovementCount()) + ", ")
      .append("SleepDuration=" + String.valueOf(getSleepDuration()) + ", ")
      .append("DurationInNumbers=" + String.valueOf(getDurationInNumbers()) + ", ")
      .append("DisturbancesCount=" + String.valueOf(getDisturbancesCount()) + ", ")
      .append("TotalMovements=" + String.valueOf(getTotalMovements()) + ", ")
      .append("AccelerometerLastReading=" + String.valueOf(getAccelerometerLastReading()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("userTrackerId=" + String.valueOf(getUserTrackerId()))
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
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      userId,
      WakeUpTime,
      SleepTime,
      ended,
      AverageMovementCount,
      SleepDuration,
      DurationInNumbers,
      DisturbancesCount,
      TotalMovements,
      AccelerometerLastReading,
      userTrackerId);
  }
  public interface UserIdStep {
    WakeUpTimeStep userId(String userId);
  }
  

  public interface WakeUpTimeStep {
    SleepTimeStep wakeUpTime(String wakeUpTime);
  }
  

  public interface SleepTimeStep {
    BuildStep sleepTime(String sleepTime);
  }
  

  public interface BuildStep {
    TrackerPeriod build();
    BuildStep id(String id);
    BuildStep ended(String ended);
    BuildStep averageMovementCount(String averageMovementCount);
    BuildStep sleepDuration(String sleepDuration);
    BuildStep durationInNumbers(String durationInNumbers);
    BuildStep disturbancesCount(String disturbancesCount);
    BuildStep totalMovements(Integer totalMovements);
    BuildStep accelerometerLastReading(Double accelerometerLastReading);
    BuildStep userTrackerId(String userTrackerId);
  }
  

  public static class Builder implements UserIdStep, WakeUpTimeStep, SleepTimeStep, BuildStep {
    private String id;
    private String userId;
    private String WakeUpTime;
    private String SleepTime;
    private String ended;
    private String AverageMovementCount;
    private String SleepDuration;
    private String DurationInNumbers;
    private String DisturbancesCount;
    private Integer TotalMovements;
    private Double AccelerometerLastReading;
    private String userTrackerId;
    @Override
     public TrackerPeriod build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new TrackerPeriod(
          id,
          userId,
          WakeUpTime,
          SleepTime,
          ended,
          AverageMovementCount,
          SleepDuration,
          DurationInNumbers,
          DisturbancesCount,
          TotalMovements,
          AccelerometerLastReading,
          userTrackerId);
    }
    
    @Override
     public WakeUpTimeStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userId = userId;
        return this;
    }
    
    @Override
     public SleepTimeStep wakeUpTime(String wakeUpTime) {
        Objects.requireNonNull(wakeUpTime);
        this.WakeUpTime = wakeUpTime;
        return this;
    }
    
    @Override
     public BuildStep sleepTime(String sleepTime) {
        Objects.requireNonNull(sleepTime);
        this.SleepTime = sleepTime;
        return this;
    }
    
    @Override
     public BuildStep ended(String ended) {
        this.ended = ended;
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
     public BuildStep userTrackerId(String userTrackerId) {
        this.userTrackerId = userTrackerId;
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
    private CopyOfBuilder(String id, String userId, String wakeUpTime, String sleepTime, String ended, String averageMovementCount, String sleepDuration, String durationInNumbers, String disturbancesCount, Integer totalMovements, Double accelerometerLastReading, String userTrackerId) {
      super.id(id);
      super.userId(userId)
        .wakeUpTime(wakeUpTime)
        .sleepTime(sleepTime)
        .ended(ended)
        .averageMovementCount(averageMovementCount)
        .sleepDuration(sleepDuration)
        .durationInNumbers(durationInNumbers)
        .disturbancesCount(disturbancesCount)
        .totalMovements(totalMovements)
        .accelerometerLastReading(accelerometerLastReading)
        .userTrackerId(userTrackerId);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
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
     public CopyOfBuilder ended(String ended) {
      return (CopyOfBuilder) super.ended(ended);
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
     public CopyOfBuilder userTrackerId(String userTrackerId) {
      return (CopyOfBuilder) super.userTrackerId(userTrackerId);
    }
  }
  
}

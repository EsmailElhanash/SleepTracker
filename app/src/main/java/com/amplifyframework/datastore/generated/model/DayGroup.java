package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the DayGroup type in your schema. */
public final class DayGroup {
  private final String SleepTime;
  private final String WakeUpTime;
  private final List<String> Days;
  public String getSleepTime() {
      return SleepTime;
  }
  
  public String getWakeUpTime() {
      return WakeUpTime;
  }
  
  public List<String> getDays() {
      return Days;
  }
  
  private DayGroup(String SleepTime, String WakeUpTime, List<String> Days) {
    this.SleepTime = SleepTime;
    this.WakeUpTime = WakeUpTime;
    this.Days = Days;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      DayGroup dayGroup = (DayGroup) obj;
      return ObjectsCompat.equals(getSleepTime(), dayGroup.getSleepTime()) &&
              ObjectsCompat.equals(getWakeUpTime(), dayGroup.getWakeUpTime()) &&
              ObjectsCompat.equals(getDays(), dayGroup.getDays());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getSleepTime())
      .append(getWakeUpTime())
      .append(getDays())
      .toString()
      .hashCode();
  }
  
  public static SleepTimeStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(SleepTime,
      WakeUpTime,
      Days);
  }
  public interface SleepTimeStep {
    WakeUpTimeStep sleepTime(String sleepTime);
  }
  

  public interface WakeUpTimeStep {
    DaysStep wakeUpTime(String wakeUpTime);
  }
  

  public interface DaysStep {
    BuildStep days(List<String> days);
  }
  

  public interface BuildStep {
    DayGroup build();
  }
  

  public static class Builder implements SleepTimeStep, WakeUpTimeStep, DaysStep, BuildStep {
    private String SleepTime;
    private String WakeUpTime;
    private List<String> Days;
    @Override
     public DayGroup build() {
        
        return new DayGroup(
          SleepTime,
          WakeUpTime,
          Days);
    }
    
    @Override
     public WakeUpTimeStep sleepTime(String sleepTime) {
        Objects.requireNonNull(sleepTime);
        this.SleepTime = sleepTime;
        return this;
    }
    
    @Override
     public DaysStep wakeUpTime(String wakeUpTime) {
        Objects.requireNonNull(wakeUpTime);
        this.WakeUpTime = wakeUpTime;
        return this;
    }
    
    @Override
     public BuildStep days(List<String> days) {
        Objects.requireNonNull(days);
        this.Days = days;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String sleepTime, String wakeUpTime, List<String> days) {
      super.sleepTime(sleepTime)
        .wakeUpTime(wakeUpTime)
        .days(days);
    }
    
    @Override
     public CopyOfBuilder sleepTime(String sleepTime) {
      return (CopyOfBuilder) super.sleepTime(sleepTime);
    }
    
    @Override
     public CopyOfBuilder wakeUpTime(String wakeUpTime) {
      return (CopyOfBuilder) super.wakeUpTime(wakeUpTime);
    }
    
    @Override
     public CopyOfBuilder days(List<String> days) {
      return (CopyOfBuilder) super.days(days);
    }
  }
  
}

package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the DeviceState type in your schema. */
public final class DeviceState {
  private final String Time;
  private final String State;
  public String getTime() {
      return Time;
  }
  
  public String getState() {
      return State;
  }
  
  private DeviceState(String Time, String State) {
    this.Time = Time;
    this.State = State;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      DeviceState deviceState = (DeviceState) obj;
      return ObjectsCompat.equals(getTime(), deviceState.getTime()) &&
              ObjectsCompat.equals(getState(), deviceState.getState());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getTime())
      .append(getState())
      .toString()
      .hashCode();
  }
  
  public static TimeStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(Time,
      State);
  }
  public interface TimeStep {
    StateStep time(String time);
  }
  

  public interface StateStep {
    BuildStep state(String state);
  }
  

  public interface BuildStep {
    DeviceState build();
  }
  

  public static class Builder implements TimeStep, StateStep, BuildStep {
    private String Time;
    private String State;
    @Override
     public DeviceState build() {
        
        return new DeviceState(
          Time,
          State);
    }
    
    @Override
     public StateStep time(String time) {
        Objects.requireNonNull(time);
        this.Time = time;
        return this;
    }
    
    @Override
     public BuildStep state(String state) {
        Objects.requireNonNull(state);
        this.State = state;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String time, String state) {
      super.time(time)
        .state(state);
    }
    
    @Override
     public CopyOfBuilder time(String time) {
      return (CopyOfBuilder) super.time(time);
    }
    
    @Override
     public CopyOfBuilder state(String state) {
      return (CopyOfBuilder) super.state(state);
    }
  }
  
}

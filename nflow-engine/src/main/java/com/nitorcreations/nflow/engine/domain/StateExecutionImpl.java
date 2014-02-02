package com.nitorcreations.nflow.engine.domain;

import org.joda.time.DateTime;

import com.nitorcreations.nflow.engine.workflow.StateExecution;
import com.nitorcreations.nflow.engine.workflow.WorkflowState;

public class StateExecutionImpl implements StateExecution {

  private final WorkflowInstance instance;
  private DateTime nextActivation;
  private WorkflowState nextState;
  private String nextStateReason;
  private boolean saveTrace = true;
  
  public StateExecutionImpl(WorkflowInstance instance) {
    this.instance = instance;
  }
  
  public DateTime getNextActivation() {
    return this.nextActivation;
  }
  
  public String getNextState() {
    return this.nextState != null ? this.nextState.toString() : null;
  }
  
  public String getNextStateReason() {
    return this.nextStateReason;
  }
  
  public boolean isSaveTrace() {
    return this.saveTrace;
  }

  @Override
  public String getRequestData() {
    return instance.getRequestData();
  }

  @Override
  public String getVariable(String name) {
    // TODO
    return null;
  }

  @Override
  public void setVariable(String name, String value) {
    // TODO 
    ;
  }

  @Override
  public void setNextActivation(DateTime activation) {
    this.nextActivation = activation;
    
  }

  @Override
  public void setNextState(WorkflowState state) {
    this.nextState = state;
  }

  @Override
  public void setNextStateReason(String reason) {
    this.nextStateReason = reason;
  }

  @Override
  public void setSaveTrace(boolean saveTrace) {
    this.saveTrace = saveTrace;
  } 
  
}
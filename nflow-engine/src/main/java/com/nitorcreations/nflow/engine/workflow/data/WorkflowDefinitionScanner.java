package com.nitorcreations.nflow.engine.workflow.data;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;
import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import com.nitorcreations.nflow.engine.workflow.Mutable;
import com.nitorcreations.nflow.engine.workflow.StateExecution;
import com.nitorcreations.nflow.engine.workflow.StateVar;
import com.nitorcreations.nflow.engine.workflow.WorkflowDefinition;
import com.nitorcreations.nflow.engine.workflow.data.WorkflowStateMethod.StateParameter;

public class WorkflowDefinitionScanner {
  private static final Set<Type> knownImmutableTypes = new HashSet<>();
  {
    knownImmutableTypes.addAll(asList(Boolean.TYPE, Boolean.class, Byte.TYPE, Byte.class, Character.TYPE, Character.class, Short.TYPE, Short.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Float.TYPE, Float.class, Double.TYPE, Double.class, String.class, BigDecimal.class, BigInteger.class, Enum.class));
  }

  public Map<String, WorkflowStateMethod> getStateMethods(@SuppressWarnings("rawtypes") Class<? extends WorkflowDefinition> definition) {
    final Map<String, WorkflowStateMethod> methods = new HashMap<>();
    doWithMethods(definition, new MethodCallback() {
      @Override
      public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
        List<StateParameter> params = new ArrayList<>();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 1; i < genericParameterTypes.length; ++i) {
          for (Annotation a : parameterAnnotations[i]) {
            if (StateVar.class.equals(a.annotationType())) {
              StateVar stateInfo = (StateVar) a;
              Type type = genericParameterTypes[i];
              Class<?> clazz = parameterTypes[i];
              boolean mutable = false;
              boolean readOnly = isReadOnly(type);
              if (Mutable.class.isAssignableFrom(clazz)) {
                ParameterizedType pType = (ParameterizedType) type;
                type = pType.getActualTypeArguments()[0];
                readOnly = false;
                mutable = true;
              }
              params.add(new StateParameter(stateInfo.value(), type, defaultValue(stateInfo, clazz), stateInfo.readOnly() || readOnly, mutable));
              break;
            }
          }
        }
        if (params.size() != genericParameterTypes.length - 1) {
          throw new IllegalStateException("Not all parameter names could be resolved for " + method + ". Maybe missing @StateVar annotation?");
        }
        methods.put(method.getName(), new WorkflowStateMethod(method, params.toArray(new StateParameter[params.size()])));
      }
    }, new WorkflowTransitionMethod());
    return methods;
  }

  boolean isReadOnly(Type type) {
    return knownImmutableTypes.contains(type);
  }

  Object defaultValue(StateVar stateInfo, Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return invokeMethod(findMethod(primitiveToWrapper(clazz), "valueOf", String.class), null, "0");
    }
    if (stateInfo != null && stateInfo.instantiateNull()) {
      try {
        Constructor<?> ctr = clazz.getConstructor();
        ctr.newInstance();
        return ctr;
      } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        // ignore
      }
    }
    return null;
  }

  static final class WorkflowTransitionMethod implements MethodFilter {
    @Override
    public boolean matches(Method method) {
      int mod = method.getModifiers();
      Class<?>[] parameterTypes = method.getParameterTypes();
      return isPublic(mod) && !isStatic(mod) && parameterTypes.length >= 1 && StateExecution.class.equals(parameterTypes[0]);
    }
  }
}
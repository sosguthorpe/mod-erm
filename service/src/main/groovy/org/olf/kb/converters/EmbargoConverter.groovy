package org.olf.kb.converters

import org.olf.kb.Embargo

import grails.databinding.converters.ValueConverter
import groovy.transform.CompileStatic

@CompileStatic
class EmbargoConverter implements ValueConverter {
  
  @Override
  boolean canConvert(Object value) {
    value instanceof String
  }

  @Override
  Object convert(Object value) {
    Embargo.parse(value as String)
  }

  @Override
  Class<?> getTargetType() {
    Embargo
  }
}

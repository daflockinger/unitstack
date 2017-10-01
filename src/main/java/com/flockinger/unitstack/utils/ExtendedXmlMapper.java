package com.flockinger.unitstack.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

public class ExtendedXmlMapper extends XmlMapper {
  /**
   * 
   */
  private static final long serialVersionUID = 2702259965235192305L;


  @SuppressWarnings("resource")
  public String writeValueAsString(Object value, Map<String,String> prefixes) throws JsonProcessingException {
    // alas, we have to pull the recycler directly here...
    SegmentedStringWriter sw = new SegmentedStringWriter(_jsonFactory._getBufferRecycler());
    try {
      JsonGenerator jsonGenerator = _jsonFactory.createGenerator(sw);
      if(jsonGenerator instanceof ToXmlGenerator) {
        ToXmlGenerator toXmlGenerator = (ToXmlGenerator) jsonGenerator;
        prefixes.entrySet().forEach(entry -> addStaxNamespacePrefix(entry,toXmlGenerator));
      }
      
      _configAndWriteValue(jsonGenerator, value);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) { // shouldn't really happen, but is declared as possibility so:
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
    return sw.getAndClear();
  }
  
  private void addStaxNamespacePrefix(Entry<String,String> entry, ToXmlGenerator toXmlGenerator) {
    try {
      toXmlGenerator.getStaxWriter().setPrefix(entry.getKey(), entry.getValue());
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }
}

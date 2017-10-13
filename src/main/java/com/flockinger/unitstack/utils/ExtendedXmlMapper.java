/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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

/**
 * Extends XML mapper with the functionality of adding
 * custom prefixes for defined namespaces.
 *
 */
public class ExtendedXmlMapper extends XmlMapper {
  /**
   * 
   */
  private static final long serialVersionUID = 2702259965235192305L;


  /**
   * Serialize Object to XML String with custom namespace prefixes.
   * 
   * @param value
   * @param prefixes
   * @return
   * @throws JsonProcessingException
   */
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

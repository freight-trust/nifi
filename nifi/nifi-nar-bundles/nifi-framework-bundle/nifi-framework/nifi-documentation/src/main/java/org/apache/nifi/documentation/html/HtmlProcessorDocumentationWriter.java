/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.documentation.html;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.documentation.ReadsAttribute;
import org.apache.nifi.annotation.documentation.ReadsAttributes;
import org.apache.nifi.annotation.documentation.WritesAttribute;
import org.apache.nifi.annotation.documentation.WritesAttributes;
import org.apache.nifi.components.ConfigurableComponent;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.Relationship;

/**
 * Writes documentation specific for a Processor. This includes everything for a
 * ConfigurableComponent as well as Relationship information.
 *
 *
 */
public class HtmlProcessorDocumentationWriter extends HtmlDocumentationWriter {

    @Override
    protected void writeAdditionalBodyInfo(final ConfigurableComponent configurableComponent,
            final XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        final Processor processor = (Processor) configurableComponent;
        writeRelationships(processor, xmlStreamWriter);
        writeAttributeInfo(processor, xmlStreamWriter);
    }

    /**
     * Writes all the attributes that a processor says it reads and writes
     * 
     * @param processor
     *            the processor to describe
     * @param xmlStreamWriter
     *            the xml stream writer to use
     * @throws XMLStreamException
     *             thrown if there was a problem writing the XML
     */
    private void writeAttributeInfo(Processor processor, XMLStreamWriter xmlStreamWriter)
            throws XMLStreamException {

        handleReadsAttributes(xmlStreamWriter, processor);
        handleWritesAttributes(xmlStreamWriter, processor);
    }

    /**
     * Writes out just the attributes that are being read in a table form.
     * 
     * @param xmlStreamWriter
     *            the xml stream writer to use
     * @param processor
     *            the processor to describe
     * @throws XMLStreamException
     */
    private void handleReadsAttributes(XMLStreamWriter xmlStreamWriter, final Processor processor)
            throws XMLStreamException {
        List<ReadsAttribute> attributesRead = getReadsAttributes(processor);

        writeSimpleElement(xmlStreamWriter, "h3", "Reads Attributes: ");
        if (attributesRead.size() > 0) {
            xmlStreamWriter.writeStartElement("table");
            xmlStreamWriter.writeStartElement("tr");
            writeSimpleElement(xmlStreamWriter, "th", "Name");
            writeSimpleElement(xmlStreamWriter, "th", "Description");
            xmlStreamWriter.writeEndElement();
            for (ReadsAttribute attribute : attributesRead) {
                xmlStreamWriter.writeStartElement("tr");
                writeSimpleElement(xmlStreamWriter, "td",
                        StringUtils.defaultIfBlank(attribute.attribute(), "Not Specified"));
                writeSimpleElement(xmlStreamWriter, "td",
                        StringUtils.defaultIfBlank(attribute.description(), "Not Specified"));
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();

        } else {
            xmlStreamWriter.writeCharacters("None specified.");
        }
    }

    /**
     * Writes out just the attributes that are being written to in a table form.
     * 
     * @param xmlStreamWriter
     *            the xml stream writer to use
     * @param processor
     *            the processor to describe
     * @throws XMLStreamException
     */
    private void handleWritesAttributes(XMLStreamWriter xmlStreamWriter, final Processor processor)
            throws XMLStreamException {
        List<WritesAttribute> attributesRead = getWritesAttributes(processor);

        writeSimpleElement(xmlStreamWriter, "h3", "Writes Attributes: ");
        if (attributesRead.size() > 0) {
            xmlStreamWriter.writeStartElement("table");
            xmlStreamWriter.writeStartElement("tr");
            writeSimpleElement(xmlStreamWriter, "th", "Name");
            writeSimpleElement(xmlStreamWriter, "th", "Description");
            xmlStreamWriter.writeEndElement();
            for (WritesAttribute attribute : attributesRead) {
                xmlStreamWriter.writeStartElement("tr");
                writeSimpleElement(xmlStreamWriter, "td",
                        StringUtils.defaultIfBlank(attribute.attribute(), "Not Specified"));
                writeSimpleElement(xmlStreamWriter, "td",
                        StringUtils.defaultIfBlank(attribute.description(), "Not Specified"));
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();

        } else {
            xmlStreamWriter.writeCharacters("None specified.");
        }
    }

    /**
     * Collects the attributes that a processor is reading from.
     * 
     * @param processor
     *            the processor to describe
     * @return the list of attributes that processor is reading
     */
    private List<ReadsAttribute> getReadsAttributes(Processor processor) {
        List<ReadsAttribute> attributes = new ArrayList<>();

        ReadsAttributes readsAttributes = processor.getClass().getAnnotation(ReadsAttributes.class);
        if (readsAttributes != null) {
            for (ReadsAttribute readAttribute : readsAttributes.value()) {
                attributes.add(readAttribute);
            }
        }

        ReadsAttribute readsAttribute = processor.getClass().getAnnotation(ReadsAttribute.class);
        if (readsAttribute != null) {
            attributes.add(readsAttribute);
        }

        return attributes;
    }

    /**
     * Collects the attributes that a processor is writing to.
     * 
     * @param processor
     *            the processor to describe
     * @return the list of attributes the processor is writing
     */
    private List<WritesAttribute> getWritesAttributes(Processor processor) {
        List<WritesAttribute> attributes = new ArrayList<>();

        WritesAttributes writesAttributes = processor.getClass().getAnnotation(WritesAttributes.class);
        if (writesAttributes != null) {
            for (WritesAttribute writeAttribute : writesAttributes.value()) {
                attributes.add(writeAttribute);
            }
        }

        WritesAttribute writeAttribute = processor.getClass().getAnnotation(WritesAttribute.class);
        if (writeAttribute != null) {
            attributes.add(writeAttribute);
        }

        return attributes;
    }

    /**
     * Writes a table describing the relations a processor has.
     * 
     * @param processor
     *            the processor to describe
     * @param xmlStreamWriter
     *            the stream writer to use
     * @throws XMLStreamException
     *             thrown if there was a problem writing the xml
     */
    private void writeRelationships(final Processor processor, final XMLStreamWriter xmlStreamWriter)
            throws XMLStreamException {

        writeSimpleElement(xmlStreamWriter, "h3", "Relationships: ");

        if (processor.getRelationships().size() > 0) {
            xmlStreamWriter.writeStartElement("table");
            xmlStreamWriter.writeStartElement("tr");
            writeSimpleElement(xmlStreamWriter, "th", "Name");
            writeSimpleElement(xmlStreamWriter, "th", "Description");
            xmlStreamWriter.writeEndElement();

            for (Relationship relationship : processor.getRelationships()) {
                xmlStreamWriter.writeStartElement("tr");
                writeSimpleElement(xmlStreamWriter, "td", relationship.getName());
                writeSimpleElement(xmlStreamWriter, "td", relationship.getDescription());
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
        } else {
            xmlStreamWriter.writeCharacters("This processor has no relationships.");
        }
    }
}

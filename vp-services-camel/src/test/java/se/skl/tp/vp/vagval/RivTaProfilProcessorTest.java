/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * <http://cehis.se/>
 * <p>
 * This file is part of SKLTP.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skl.tp.vp.vagval;

import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class RivTaProfilProcessorTest {
    @Test
    public void testRiv21To20TransformerSpecial() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/GetSubjectOfCareRequest21.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/GetSubjectOfCareRequest20.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_ELEM, RivTaProfilProcessor.RIV20_ELEM);

        this.executeComparison(data, expected);
    }

    @Test
    public void testRiv21To20Transformer() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/PingForConfiguration-request-rivtabp21-input.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/PingForConfiguration-expected-rivtabp20-result.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_ELEM, RivTaProfilProcessor.RIV20_ELEM);

        this.executeComparison(data, expected);
    }

    @Test
    public void testRiv20To21Transformer() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/PingForConfiguration-request-input.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/PingForConfiguration-expected-result.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_ELEM, RivTaProfilProcessor.RIV21_ELEM);

        this.executeComparison(data, expected);

    }

    @Test
    public void riv21To20WhenNamsespaceIsInAddressingElement() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("testfiles/Rivta21Request.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/Rivta20Request.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_ELEM, RivTaProfilProcessor.RIV20_ELEM);

        this.executeComparison(data, expected);
    }

    @Test
    public void riv20To21WhenNamsespaceIsInAddressingElement() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("testfiles/Rivta20Request.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/Rivta21Request.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_ELEM, RivTaProfilProcessor.RIV21_ELEM);

        this.executeComparison(data, expected);
    }

    @Test
    public void riv21To20WhenNamsespaceIsInHeaderElement() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("testfiles/Rivta21RequestNamespaceInHeader.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/Rivta20RequestNamespaceInHeader.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_ELEM, RivTaProfilProcessor.RIV20_ELEM);

        System.out.println(new String(data.toByteArray(), "UTF-8"));

        this.executeComparison(data, expected);
    }

    @Test
    public void riv20To21WhenNamsespaceIsInHeaderElement() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("testfiles/Rivta20RequestNamespaceInHeader.xml");
        final XMLStreamReader xstream = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        final URL resultFile = Thread.currentThread().getContextClassLoader()
                .getResource("testfiles/Rivta21RequestNamespaceInHeader.xml");
        final XMLEventReader expected = XMLInputFactory.newInstance().createXMLEventReader(resultFile.openStream());

        final ByteArrayOutputStream data = RivTaProfilProcessor.transformXml(xstream,
                RivTaProfilProcessor.RIV20_NS, RivTaProfilProcessor.RIV21_NS, RivTaProfilProcessor.RIV20_ELEM, RivTaProfilProcessor.RIV21_ELEM);

        this.executeComparison(data, expected);
    }

    private void executeComparison(final ByteArrayOutputStream transformed, final XMLEventReader expected)
            throws Exception {

        final XMLEventReader result = XMLInputFactory.newInstance().createXMLEventReader(
                new ByteArrayInputStream(transformed.toByteArray()), "UTF-8");

        System.out.println("Comparing xml results");
        while (expected.hasNext()) {
            final XMLEvent e1 = expected.nextEvent();
            final XMLEvent e2 = result.nextEvent();

            if (e1.isStartElement()) {

                final StartElement se1 = e1.asStartElement();
                final StartElement se2 = e2.asStartElement();

                System.out.println(se1.getName().getLocalPart() + " == " + se2.getName().getLocalPart());

                assertEquals(se1.getName().getLocalPart(), se2.getName().getLocalPart());
                assertEquals(se1.getName().getPrefix(), se2.getName().getPrefix());
                assertEquals(se1.getName().getNamespaceURI(), se2.getName().getNamespaceURI());

                @SuppressWarnings("rawtypes")
                final Iterator ns1 = se1.getNamespaces();
                @SuppressWarnings("rawtypes")
                final Iterator ns2 = se2.getNamespaces();

                while (ns1.hasNext()) {
                    final Namespace n1 = (Namespace) ns1.next();
                    final Namespace n2 = (Namespace) ns2.next();

                    System.out.println(n1.getName() + " == " + n2.getName());

                    assertEquals(n1.getPrefix(), n2.getPrefix());
                    assertEquals(n1.getValue(), n2.getValue());

                }
            }

            if (e1.isEndElement()) {

                final EndElement ee1 = e1.asEndElement();
                final EndElement ee2 = e2.asEndElement();

                System.out.println(ee1.getName().getLocalPart() + " == " + ee2.getName().getLocalPart());

                assertEquals(ee1.getName().getLocalPart(), ee2.getName().getLocalPart());
                assertEquals(ee1.getName().getPrefix(), ee2.getName().getPrefix());
                assertEquals(ee1.getName().getNamespaceURI(), ee2.getName().getNamespaceURI());
            }
        }
    }
}

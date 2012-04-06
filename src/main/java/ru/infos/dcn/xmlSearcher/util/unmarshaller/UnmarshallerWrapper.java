package ru.infos.dcn.xmlSearcher.util.unmarshaller;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Artemij Chugreev
 * Date: 27.03.12
 * Time: 14:13
 * email: artemij.chugreev@gmail.com
 * skype: achugr
 */
public class UnmarshallerWrapper {
    private Class classToProvide;
    private Unmarshaller unmarshaller;

    public UnmarshallerWrapper(Class classToProvide) throws JAXBException {
        this.classToProvide = classToProvide;
        JAXBContext jaxbContext = JAXBContext.newInstance(classToProvide.getPackage().getName());
        unmarshaller =  jaxbContext.createUnmarshaller();
    }

    public <T> T unmarshall (String filePath) throws JAXBException {
        JAXBElement<T> doc = (JAXBElement<T>) unmarshaller.unmarshal(new StreamSource(new File(filePath)), classToProvide);
        return doc.getValue();
    }

    public <T> T unmarshall(File file) throws JAXBException {
        JAXBElement<T> doc = (JAXBElement<T>) unmarshaller.unmarshal(new StreamSource(file), classToProvide);
        return doc.getValue();
    }
}

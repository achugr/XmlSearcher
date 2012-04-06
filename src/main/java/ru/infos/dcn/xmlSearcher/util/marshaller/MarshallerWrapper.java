package ru.infos.dcn.xmlSearcher.util.marshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.File;

/**
 * Artemij Chugreev
 * Date: 27.03.12
 * Time: 13:07
 * email: artemij.chugreev@gmail.com
 * skype: achugr
 */
public class MarshallerWrapper {
    private Marshaller marshaller;
    
    public MarshallerWrapper(Class classToMarshall) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(classToMarshall.getPackage().getName());
        marshaller =  jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    public void marshal(Object object, String filePath) throws JAXBException {
        QName qtag = new QName(object.getClass().getPackage().getName(), object.getClass().getSimpleName().toLowerCase());
        JAXBElement jaxbElement = new JAXBElement(qtag, object.getClass(), object);
        marshaller.marshal(jaxbElement, new File(filePath));
    }

    public void marshal(Object object, File file) throws JAXBException {
        QName qtag = new QName(object.getClass().getPackage().getName(), object.getClass().getSimpleName().toLowerCase());
        JAXBElement jaxbElement = new JAXBElement(qtag, object.getClass(), object);
        marshaller.marshal(jaxbElement, file);
    }
}

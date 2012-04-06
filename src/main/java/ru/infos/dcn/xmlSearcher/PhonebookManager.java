package ru.infos.dcn.xmlSearcher;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.infos.dcn.BloomFilter;
import ru.infos.dcn.beans.Contact;
import ru.infos.dcn.beans.Phonebook;
import ru.infos.dcn.exception.FilterFullException;
import ru.infos.dcn.xmlSearcher.util.marshaller.MarshallerWrapper;
import ru.infos.dcn.xmlSearcher.util.unmarshaller.UnmarshallerWrapper;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Artemij Chugreev
 * Date: 27.03.12
 * Time: 13:28
 * email: artemij.chugreev@gmail.com
 * skype: achugr
 */
public class PhonebookManager {
//    bloom filter for increase speed of determine the existence field in phone book
    BloomFilter bloomFilter;
//    file with phone book
    private File file;
//    id of last contact in phone book
    private int lastContactId = 0;
    private static final Logger log = Logger.getLogger(PhonebookManager.class);

    /**
     * create Phone book manager
     * @param filePath file with phone book
     */
    public PhonebookManager(String filePath) {
        file = new File(filePath);
        bloomFilter = new BloomFilter(1000, 0.001, null, true);
    }

    /**
     *
     * @param contact
     * @throws JAXBException
     * @throws FilterFullException
     */
    public void addRecord(Contact contact) throws JAXBException, FilterFullException {
        UnmarshallerWrapper unmarshallerWrapper = new UnmarshallerWrapper(Phonebook.class);
        Phonebook phonebook = unmarshallerWrapper.unmarshall(file);
        phonebook.getContact().add(contact);

//        TODO maybe it's useful to use reflection here
        bloomFilter.put(contact.getName());
        bloomFilter.put(contact.getPhoneNumber());
        bloomFilter.put(contact.getSurname());

        MarshallerWrapper marshallerWrapper = new MarshallerWrapper(Phonebook.class);
        marshallerWrapper.marshal(phonebook, file);
    }

    /**
     * verify that argument exist in bloom filter
     * @param record which existence you will verify
     * @return true - if exist, false - otherwise
     */
    public boolean doesExist(String record) {
        if (bloomFilter.exist(record)) {
            return true;
        }
        return false;
    }


//    TODO xPath -expression is awful
    /**
     * Searching in phone book file
     * @param contactFieldToSearch in which you will search (like ContactField.NAME)
     * @param valueToSearch which yout will search (like "tema", or "+77777777")
     * @return List<Contact>, which field "contactFieldToSearch" contains text "valueToSearch"
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     */
    public List<Contact> search(ContactField contactFieldToSearch, String valueToSearch) throws FileNotFoundException, XPathExpressionException {
    }

    /**
     * fill bloom filter from file with phone book
     * all text nodes from phone book added in bloom filter
     * @param reserveSize reserve size of filter (size of filter = number of nodes in phone book + reserve size)(you will probably want to fill a phone book with new contacts)
     * @throws XPathExpressionException
     * @throws FileNotFoundException
     */
    public void fillBloomFilter(int reserveSize) throws XPathExpressionException, FileNotFoundException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource inputSource = new InputSource(new FileReader(file));
//        extract all text nodes from file
        NodeList nodeList = (NodeList)  xPath.evaluate("descendant::contact/node()/text()", inputSource, XPathConstants.NODESET);
        List<Object> records = new ArrayList<Object>();
//        add all node values to list
        for (int i = 0; i < nodeList.getLength(); i++) {
            records.add(nodeList.item(i).getNodeValue());
        }
//        fill bloom filter with this list of nodes values
        bloomFilter = BloomFilter.newInstance(records, reserveSize, 0.0001, null, true);
    }

    /**
     * add contact in phone book
     * @param contact you want to add
     * @return id of last added element
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     * @throws JAXBException
     * @throws FilterFullException if filter is full
     */
    public int addContact(Contact contact) throws FileNotFoundException, XPathExpressionException, JAXBException, FilterFullException {
        if (contact.getName() != null) {
            bloomFilter.put(contact.getName());
        }
        if (contact.getSurname() != null) {
            bloomFilter.put(contact.getSurname());
        }
        if (contact.getPhoneNumber() != null) {
            bloomFilter.put(contact.getPhoneNumber());
        }

        if (lastContactId == 0) {
//            if it's first contact you want add, phone book manager want to know last contact id
            lastContactId = lastContactId();
//            set id of contact
            contact.setId(String.valueOf(++lastContactId));
        } else {
//            set id of contact
            contact.setId(String.valueOf(++lastContactId));
        }
//        unmarshall phone book
        UnmarshallerWrapper unmarshallerWrapper = new UnmarshallerWrapper(Phonebook.class);
        Phonebook phonebook = unmarshallerWrapper.unmarshall(file);
//        add contact
        phonebook.getContact().add(contact);
//        marshall phonebook
        MarshallerWrapper marshallerWrapper = new MarshallerWrapper(Phonebook.class);
        marshallerWrapper.marshal(phonebook, file);
//        return last contact id
        return lastContactId;
    }

    /**
     * find last contact id in phone book
     * @return
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     */
     int lastContactId() throws FileNotFoundException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource inputSource = new InputSource(new FileReader(file));
//        evaluate expression for extract last contact id attribute
        String id = xPath.evaluate("//contact[last()]/attribute::id", inputSource);
        return Integer.parseInt(id);
    }

    /**
     * fill phone book for tests
     * @param numOfContacts num of contacts
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     * @throws FilterFullException
     */
    private void fillPhonebookWithRandom(int numOfContacts) throws JAXBException, FileNotFoundException, XPathExpressionException, FilterFullException {
        Random random = new Random();
        Contact contact;
        for(int i=0; i<numOfContacts; i++){
            contact = new Contact();
            contact.setName(String.valueOf(random.nextInt()));
            contact.setSurname(String.valueOf(random.nextInt()));
            contact.setPhoneNumber(String.valueOf(random.nextInt()));
            this.addContact(contact);
        }
    }

    /**
     * does element exist in phone book
     * @param contactField field type to search
     * @param value field value to search
     * @param useBloomFilter true - if you wan't use bloom filter, false - otherwise
     * @return true - if exist, false - otherwise
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     */
    private boolean doesExistInPhonebook(ContactField contactField, String value, boolean useBloomFilter) throws FileNotFoundException, XPathExpressionException {
        if(useBloomFilter){
            return bloomFilter.exist(value);
        } else {
            List<Contact> contacts = this.search(contactField, value);
            return !contacts.isEmpty();
        }
    }


    public static void main(String[] args) throws JAXBException, FilterFullException, FileNotFoundException, XPathExpressionException {
        PhonebookManager phonebookManager = new PhonebookManager("test.xml");
        phonebookManager.fillBloomFilter(12000);
        {
            long start = System.currentTimeMillis();
            System.out.println(phonebookManager.doesExistInPhonebook(ContactField.NAME, "1146927241", true));
            long stop = System.currentTimeMillis();
            System.out.println("time in millis: " + (stop-start));
        }
        {
            long start = System.currentTimeMillis();
            System.out.println(phonebookManager.doesExistInPhonebook(ContactField.NAME, "1146927241", false));
            long stop = System.currentTimeMillis();
            System.out.println("time in millis: " + (stop-start));
        }
    }

}

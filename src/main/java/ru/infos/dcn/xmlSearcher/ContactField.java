package ru.infos.dcn.xmlSearcher;

/**
 * Artemij Chugreev
 * Date: 06.04.12
 * Time: 8:28
 * email: artemij.chugreev@gmail.com
 * skype: achugr
 */
public enum ContactField {
    NAME("name"),
    SURNAME("surname"),
    PHONE_NUMBER("phoneNumber");

    private final String field;
    
    ContactField(String field){
        this.field = field;
    }

    public String value(){
        return field;
    }
}

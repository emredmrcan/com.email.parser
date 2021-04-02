package com.email.parser;

import org.jetbrains.annotations.NotNull;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public abstract class MailServerTestHelper {
    private static final String STORE_PROTOCOL = "imap";
    private String username;
    private String password;
    private MailServerType mailServerType;
    private Properties properties;
    private Session session;

    protected void setUpWithOutlookCredentials() throws Exception {
        username = "*****";
        password = "*****";
        mailServerType = MailServerType.OUTLOOK;
        properties = getProperties();
        session = Session.getDefaultInstance(properties);
    }

    protected void setUpWithGmailCredentials() throws Exception {
        username = "*****";
        password = "*****";
        mailServerType = MailServerType.GMAIL;
        properties = getProperties();
        session = Session.getDefaultInstance(properties);
    }

    @NotNull
    private Properties getProperties() throws Exception {
        if (mailServerType.equals(MailServerType.GMAIL))
            return propertiesForGmail();
        else if (mailServerType.equals(MailServerType.OUTLOOK))
            return propertiesForOutlook();
        else
            throw new Exception("Unknown mail server type:" + mailServerType.name());
    }

    private Properties propertiesForGmail() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        return properties;
    }

    private Properties propertiesForOutlook() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.host", "outlook.office365.com");
        properties.put("mail.imap.port", "993");
        return properties;
    }

    Store connectStore() throws MessagingException {
        Store store = session.getStore(STORE_PROTOCOL);
        store.connect(username, password);
        return store;
    }

    Folder folder(String folderName) throws MessagingException {
        Store store = connectStore();
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return folder;
    }
}

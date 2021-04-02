package com.email.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class MailServerTest extends MailServerTestHelper {

    private static final String FOLDER_NAME = "Client";
    private static final String OUTPUT_FILE = "clients.csv";

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        //setUpWithOutlookCredentials();
        setUpWithGmailCredentials();
    }

    @Test
    void getMessageCount() throws MessagingException {
        Folder inbox = folder("INBOX");
        int messageCount = inbox.getMessageCount();
        assertTrue(messageCount > 0);
    }

    @Test
    void getFirstMessageBody() throws Exception {
        Folder inbox = folder(FOLDER_NAME);
        Message[] messages = inbox.getMessages();
        MimeMessageParser parser = new MimeMessageParser((MimeMessage) messages[0]).parse();
        String htmlContent = parser.getHtmlContent();
        String plainContent = parser.getPlainContent();
        System.out.println(plainContent);
        assertTrue(plainContent.length() > 0);
    }

    @Test
    void availableFolders() throws MessagingException {
        Folder[] f = connectStore().getDefaultFolder().list();
        Arrays.stream(f).forEach(s -> System.out.println(">>" + s.getFullName()));
        assertTrue(Arrays.stream(f).anyMatch(s -> s.getFullName().equals(FOLDER_NAME)));
    }

    @Test
    void parseAndCreateClient() throws Exception {
        List<ResultData> resultDataList = resultData();
        assertTrue(resultDataList.size() > 0);

        Client firstClient = resultDataList.get(0).getClient();
        assertEquals(firstClient.getName(), "Emre Demircan");
        assertEquals(firstClient.getEmail(), "edemircan.inbox@gmail.com");
        assertEquals(firstClient.getTel(), "333444");
        assertEquals(firstClient.getCity(), "Izmir");
        assertEquals(firstClient.getApplicationSource(), "www.example.com.tr");
    }

    @Test
    @Disabled
    void createCSVwithClients() throws MessagingException, IOException {
        String[] HEADERS = {"Full Name", "Email", "Phone Number", "City", "Application Source", "Subject", "From"};
        List<ResultData> resultData = resultData();
        FileWriter out = new FileWriter(OUTPUT_FILE);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(HEADERS))) {
            for (ResultData rd : resultData) {
                Client c = rd.getClient();
                printer.printRecord(c.getName(), c.getEmail(), c.getTel(), c.getCity(), c.getApplicationSource(), rd.getSubject(), rd.getFrom());
            }
        }
    }

    private List<ResultData> resultData() throws MessagingException {
        Folder inbox = folder(FOLDER_NAME);
        Message[] messages = inbox.getMessages();
        List<ResultData> resultDataList = new ArrayList<>(messages.length);

        Arrays.stream(messages).forEach(m -> {
            try {
                MimeMessageParser parser = new MimeMessageParser((MimeMessage) m).parse();
                resultDataList.add(new ResultData(safeSubject(parser), safeFrom(parser), Client.from(parser.getPlainContent())));
            } catch (Exception e) {
                System.out.println("Could not parse the email message correctly: " + e.getMessage());
            }

        });
        return resultDataList;
    }

    private String safeSubject(MimeMessageParser parser) {
        try {
            return parser.getSubject();
        } catch (Exception e) {
            System.out.println("Could not get the subject. " + e.getMessage());
            return "";
        }
    }

    private String safeFrom(MimeMessageParser parser) {
        try {
            return parser.getFrom();
        } catch (Exception e) {
            System.out.println("Could not get the from. " + e.getMessage());
            return "";
        }
    }
}
package net.java.sip.communicator.plugin.openmeetings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;

import net.java.sip.communicator.util.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OpenmeetingsPluginSoapClient
{

    String serverUrl;
    
    Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

    private static final String NAMESPACE_PREFIX = "openmeetings";

    public OpenmeetingsPluginSoapClient()
    {
        super();
    }

    public String getSID(String username, String password) throws Exception
    {
        final SOAPMessage soapMessage = getSoapMessage();
        soapMessage.getSOAPBody().addChildElement("getSession", NAMESPACE_PREFIX);
        soapMessage.saveChanges();
        
        final SOAPBody responseBody = getSOAPResponseBody(soapMessage, getUserServiceUrl());

        String sid = null;

        final Node getSessionResponse = responseBody.getFirstChild();
        final Node returnResult = getSessionResponse.getFirstChild();

        final NodeList childNodes = returnResult.getChildNodes();
        sid = childNodes.item(5).getTextContent();

        return sid;
    }

    public void logMessage(SOAPMessage msg)
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msg.writeTo(out);
            logger.info(new String(out.toByteArray()) + "\n\n");
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

    private String login(final String sid, final String username,
        final String password) throws SOAPException, IOException
    {
        final SOAPMessage soapMessage = getSoapMessage();
        final SOAPBody soapBody = soapMessage.getSOAPBody();
        final SOAPElement loginElement =
            soapBody.addChildElement("loginUser", NAMESPACE_PREFIX);

        loginElement.addChildElement("SID", NAMESPACE_PREFIX).addTextNode(sid);
        loginElement.addChildElement("username", NAMESPACE_PREFIX).addTextNode(
            username);
        loginElement.addChildElement("userpass", NAMESPACE_PREFIX).addTextNode(
            password);
        soapMessage.saveChanges();

        final SOAPBody soapResponseBody = getSOAPResponseBody(soapMessage, getUserServiceUrl());
        final String textContent = soapResponseBody.getFirstChild().getTextContent();
        if (!textContent.equals("1"))
            JOptionPane.showMessageDialog(null,
                    OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.ERROR_LOGIN_MSG") 
                    + " - Reason: " + this.getErrorCode(sid, textContent));

        return textContent;
    }

    public String getInvitationHash(final String username,
        final String password, final String displayedName, final String RoomID)
            throws Exception
    {
        final SOAPMessage soapMessage = getSoapMessage();
        final SOAPBody soapBody = soapMessage.getSOAPBody();
        final SOAPElement requestElement =
            soapBody.addChildElement("getInvitationHash", NAMESPACE_PREFIX);

        logger.info(username + ":" + displayedName);

        String sid = getSID(username, password);
        String error_id = null;
        try
        {
            error_id = login(sid, username, password);
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
        }

        if (!error_id.equals("1"))
        {
            logger.info("User cant login!");
            return null;
        }

        String room_id = RoomID;
        if ((room_id == null) || (room_id.trim().isEmpty()))
            room_id = getAvailableRooms(sid);
        if (room_id == null)
        {
            logger.error("No rooms available in openmeetings");
            return null;
        } else
        {
            room_id = room_id.trim();
            logger.info("Found openmeetings conference room ID " + room_id);
        }

        requestElement.addChildElement("SID", NAMESPACE_PREFIX)
            .addTextNode(sid);
        requestElement.addChildElement("username", NAMESPACE_PREFIX)
            .addTextNode(displayedName);
        requestElement.addChildElement("room_id", NAMESPACE_PREFIX)
            .addTextNode(room_id);
        soapMessage.saveChanges();

        final SOAPBody soapResponseBody = getSOAPResponseBody(soapMessage, getJabberServiceUrl());
        final String textContent = soapResponseBody.getFirstChild().getTextContent();
        logger.info("INVITATION RESPONSE =  " + textContent);
        return textContent;
    }

    private String getAvailableRooms(final String sid)
        throws SOAPException,
        IOException,
        TransformerException
    {
        final SOAPMessage soapMessage = getSoapMessage();
        final SOAPBody soapBody = soapMessage.getSOAPBody();
        final SOAPElement elemCodeElement =
            soapBody.addChildElement("getAvailableRooms", NAMESPACE_PREFIX);
        elemCodeElement.addChildElement("SID", "rooms").addTextNode(sid);
        soapMessage.saveChanges();

        final SOAPBody soapResponseBody = getSOAPResponseBody(soapMessage, getJabberServiceUrl());
        final Node getFirstRoomResult = soapResponseBody.getFirstChild().getFirstChild();
        if (getFirstRoomResult == null)
            return null;

        String rooms_id = new String();
        final NodeList childNodes = getFirstRoomResult.getChildNodes();
        int count = childNodes.getLength();
        for (int i = 0; i < count; ++i)
        {
            String nodeName = childNodes.item(i).getNodeName();
            if (nodeName.contains("rooms_id"))
            {
                rooms_id = childNodes.item(i).getTextContent();
            }
        }
        System.out.println("GET_AVAILABLE_ROOMS RESULT =  " + rooms_id);

        return rooms_id;
    }

    private String getErrorCode(final String sid, final String error_id)
        throws SOAPException,
        IOException
    {
        final SOAPMessage soapMessage = getSoapMessage();
        final SOAPBody soapBody = soapMessage.getSOAPBody();
        final SOAPElement errorCodeElement =
            soapBody.addChildElement("getErrorByCode", NAMESPACE_PREFIX);
        errorCodeElement.addChildElement("SID", NAMESPACE_PREFIX).addTextNode(
            sid);
        errorCodeElement.addChildElement("errorid", NAMESPACE_PREFIX)
            .addTextNode(error_id);
        errorCodeElement.addChildElement("language_id", NAMESPACE_PREFIX)
            .addTextNode("0");
        soapMessage.saveChanges();

        final SOAPBody soapResponseBody = getSOAPResponseBody(soapMessage, getUserServiceUrl());
        return soapResponseBody.getFirstChild().getTextContent();
    }
    
    private SOAPBody getSOAPResponseBody(SOAPMessage soapMessage, String url)
        throws SOAPException,
        IOException
    {
        logger.info("\nSOAP request to " + url);
        logMessage(soapMessage);

      
        final SOAPConnection soapConnection = getSoapConnectionInstance();
        final SOAPMessage soapMessageReply =
            soapConnection.call(soapMessage, url);
        logger.info("\nSOAP response");
        logMessage(soapMessageReply);
        soapConnection.close();
        
        return soapMessageReply.getSOAPBody();
    }
    

    private SOAPConnection getSoapConnectionInstance()
        throws UnsupportedOperationException,
        SOAPException
    {

        final SOAPConnectionFactory soapConnectionFactory =
            SOAPConnectionFactory.newInstance();
        final SOAPConnection soapConnection =
            soapConnectionFactory.createConnection();

        return soapConnection;
    }

    private SOAPMessage getSoapMessage() throws SOAPException
    {
        final MessageFactory messageFactory =
            javax.xml.soap.MessageFactory.newInstance();
        final SOAPMessage soapMessage = messageFactory.createMessage();

        // Object for message parts
        final SOAPPart soapPart = soapMessage.getSOAPPart();
        final SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("xsd",
            "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsd",
            "http://basic.beans.data.app.openmeetings.org/xsd");
        envelope.addNamespaceDeclaration("xsd",
            "http://basic.beans.persistence.app.openmeetings.org/xsd");
        envelope.addNamespaceDeclaration("xsi",
            "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("enc",
            "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("env",
            "http://schemas.xmlsoap.org/soap/envelop/");

        envelope.addNamespaceDeclaration(NAMESPACE_PREFIX,
            "http://services.axis.openmeetings.org");
        envelope.addNamespaceDeclaration("rooms",
            "http://rooms.beans.persistence.app.openmeetings.org/xsd");

        envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

        return soapMessage;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }
    
    public void setProxy(String proxy) throws Exception
    {
        logger.info("SOAP proxy [" + proxy + "]");
        if ((proxy != null) && (proxy.length() > 0))
        {
            URI u = new URI(proxy);
            logger.info("SOAP proxy host " + u.getHost());
            logger.info("SOAP proxy port " + u.getPort());
            System.getProperties().put("http.proxyHost", u.getHost());
            System.getProperties().put("http.proxyPort", Integer.toString(u.getPort()));
        }
    }

    private String getServerUrl()
    {
        return serverUrl;
    }

    private String getUserServiceUrl()
    {
        String url = getServerUrl() + "services/UserService?wsdl";
        System.out.println("URL = " + url);
        return url;
    }

    private String getJabberServiceUrl()
    {
        String url = getServerUrl() + "services/JabberService?wsdl";
        System.out.println("URL = " + url);
        return url;
    }
}
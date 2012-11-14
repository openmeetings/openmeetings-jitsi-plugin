/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.openmeetings;

import java.awt.event.*;

import net.java.sip.communicator.service.protocol.Message;
import net.java.sip.communicator.service.protocol.Contact;
import net.java.sip.communicator.service.protocol.OperationSetBasicInstantMessaging;
import net.java.sip.communicator.service.protocol.ProtocolProviderService;

import javax.swing.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;

import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.util.*;

public class OpenmeetingsPluginMenuItem
    extends AbstractPluginComponent
    implements ActionListener
{
    private JMenuItem menuItem;

    private MetaContact metaContact;

    private BundleContext bc;

    /**
     * Creates an instance of <tt>OpenmeetingsPluginMenuItem</tt>.
     * 
     * @param bc_ bundle context
     */
    public OpenmeetingsPluginMenuItem(BundleContext bc_)
    {
        super(Container.CONTAINER_CONTACT_RIGHT_BUTTON_MENU);
        this.bc = bc_;
        OpenmeetingsConfigManager.getInstance().setContext(bc_);
    }

    /**
     * Listens for events triggered by user clicks on this menu item. Opens the
     * <tt>PluginDialog</tt>.
     */
    public void actionPerformed(ActionEvent e)
    {
        Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

        String invitationUrl = null;
        String inviterDisplayedName = OpenmeetingsConfigManager.getInstance().
                getDisplayedName().trim();
        if (inviterDisplayedName.length() == 0)
            inviterDisplayedName = OpenmeetingsConfigManager.getInstance()
                    .getLogin();

        logger.info("getting invitation for inviter "
            + inviterDisplayedName);

        try
        {
            invitationUrl =
                OpenmeetingsConfigManager.getInstance().getInvitationUrl(
                    inviterDisplayedName);
        }
        catch (Exception e1)
        {
            logger.info(e1.getMessage());
        }
        if (invitationUrl == null)
        {
            logger.error("Can't get invitation URL");
            return;
        }

        Contact to = metaContact.getDefaultContact();
        String invitationUrlForSend = null;
        try
        {
            invitationUrlForSend =
                OpenmeetingsConfigManager.getInstance().getInvitationUrl(
                    to.getDisplayName());
        }
        catch (Exception e1)
        {
            logger.info(e1.getMessage());
        }

        ServiceReference uiServiceRef = bc.getServiceReference(UIService.class.getName());
        UIService uiService = (UIService) bc.getService(uiServiceRef);
        Chat chat = uiService.getChat(metaContact.getDefaultContact());
        chat.setChatVisible(true);

        ProtocolProviderService jabberProvider = OpenmeetingsPluginMenuItem.getJabberProtocol(bc, logger);
        if (jabberProvider == null)
        {
            return;
        }

        OperationSetBasicInstantMessaging basicInstMsgImpl = jabberProvider
                    .getOperationSet(OperationSetBasicInstantMessaging.class);

        String invitationLinkStr = OpenmeetingsPluginActivator.resourceService
            .getI18NString("plugin.openmeetings.INVITATION_LINK");

        String message =
            OpenmeetingsPluginActivator.resourceService
                .getI18NString("plugin.openmeetings.INVITE_MESSAGE") + "\n" +
                "<a href=\"" + invitationUrlForSend + "\">" + invitationLinkStr + "</a>";

        Message msg = basicInstMsgImpl.createMessage(message,
            OperationSetBasicInstantMessaging.HTML_MIME_TYPE,
            OperationSetBasicInstantMessaging.DEFAULT_MIME_ENCODING, null);
        basicInstMsgImpl.sendInstantMessage(to, msg);

        message = OpenmeetingsPluginActivator.resourceService
            .getI18NString("plugin.openmeetings.ENTER_MESSAGE") + "\n" +
            "<a href=\"" + invitationUrl + "\">" + invitationLinkStr + "</a>";
        chat.addMessage(null, System.currentTimeMillis(), Chat.SYSTEM_MESSAGE,
            message, "text/html");
        
    }

    /*
     * Sets the OpenMeetings server.
     */
    public void setOMserver(String OMserver)
    {
        OpenmeetingsConfigManager.getInstance().setServer(OMserver);
    }

    /*
     * Sets the OpenMeetings protocol prefix.
     */
    public void setOMprotoPrefix(String OMprotoPrefix)
    {
        OpenmeetingsConfigManager.getInstance().setProtoPrefix(OMprotoPrefix);
    }

    /*
     * Sets the OpenMeetings URI context.
     */
    public void setOMuriContext(String OMuriContext)
    {
        OpenmeetingsConfigManager.getInstance().setOmUriContext(OMuriContext);
    }

    /*
     * Sets the OpenMeetings URI context.
     */
    public void setOMproxy(String OMproxy)
    {
        OpenmeetingsConfigManager.getInstance().setProxy(OMproxy);
    }

    /*
     * Sets the OpenMeetings SOAP login username.
     */
    public void setOMlogin(String OMlogin)
    {
        OpenmeetingsConfigManager.getInstance().setLogin(OMlogin);
    }

    /*
     * Sets the OpenMeetings SOAP login user password.
     */
    public void setOMpassword(String OMpassword)
    {
        OpenmeetingsConfigManager.getInstance().setPassword(OMpassword);
    }

    /*
     * Sets the name to display for the user who invites others to a conference.
     */
    public void setOMdisplayedName(String displayedName)
    {
        OpenmeetingsConfigManager.getInstance().setDisplayedName(displayedName);
    }

    /*
     * Sets the conference room ID.
     */
    public void setOMRoomID(String RoomID)
    {
        OpenmeetingsConfigManager.getInstance().setRoomID(RoomID);
    }

    /*
     * Implements PluginComponent#getComponent().
     */
    public Object getComponent()
    {
        if (menuItem == null)
        {
            menuItem = new JMenuItem(getName());
            menuItem.addActionListener(this);
        }
        return menuItem;
    }

    /*
     * Implements PluginComponent#getName().
     */
    public String getName()
    {
        return OpenmeetingsPluginActivator.resourceService
            .getI18NString("plugin.openmeetings.MENU_ITEM");
    }

    /**
     * Sets the current <tt>MetaContact</tt>. This in the case of the contact
     * right button menu container would be the underlying contact in the
     * contact list.
     * 
     * @param metaContact the <tt>MetaContact</tt> to set.
     * 
     * @see PluginComponent#setCurrentContact(MetaContact)
     */
    public void setCurrentContact(MetaContact metaContact)
    {
        this.metaContact = metaContact;
    }

    public static ProtocolProviderService getJabberProtocol(BundleContext bc, Logger logger)
    {
        ServiceReference cRef[];
        try
        {
            cRef = bc.getServiceReferences(ProtocolProviderService.class.getName(), null);
        }
        catch (InvalidSyntaxException e1)
        {
            logger.error(e1.getMessage());
            return null;
        }

        ProtocolProviderService jabberProvider = null;
        for (ServiceReference aCRef : cRef)
        {
            ProtocolProviderService provider = (ProtocolProviderService) bc.getService(aCRef);
            if (provider.getClass().getName().contains("Jabber"))
            {
                jabberProvider = provider;
                break;
            }
        }

        if (null == jabberProvider)
        {
            logger.error("cannot find jabber service");
        }

        return jabberProvider;
    }
}

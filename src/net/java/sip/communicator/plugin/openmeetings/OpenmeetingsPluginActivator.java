/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.openmeetings;

import java.util.*;

import net.java.sip.communicator.impl.gui.event.PluginComponentEvent;
import net.java.sip.communicator.impl.gui.main.contactlist.ContactListPane;
import net.java.sip.communicator.plugin.otr.OtrActivator;
import net.java.sip.communicator.service.credentialsstorage.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.internal.GuiServiceActivator;
import net.java.sip.communicator.service.resources.*;
import org.jitsi.service.resources.ResourceManagementService;
import net.java.sip.communicator.service.resources.ResourceManagementServiceUtils;
//import org.jitsi.service.resources.*;
import org.jitsi.service.configuration.*;
import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

public class OpenmeetingsPluginActivator
    implements BundleActivator
{
    public static BundleContext bundleContext;

    public static ResourceManagementService resourceService;

    Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

    /**
     * Indicates if the openmeetings configuration form should be disabled, i.e.
     * not visible to the user.
     */
    private static final String DISABLED_PROP
        = "net.java.sip.communicator.plugin.openmeetings.DISABLED";

    /**
     * Indicates the OpemMeetings server.
     */
    public static final String SERVER_PROP
        = "net.java.sip.communicator.plugin.openmeetings.SERVER";

    /**
     * Indicates the OpemMeetings protocol prefix.
     */
    public static final String PROTOCOL_PREFIX_PROP
        = "net.java.sip.communicator.plugin.openmeetings.PROTOCOL_PREFIX";

    /**
     * Indicates the OpemMeetings URI context.
     */
    public static final String OM_URI_CONTEXT_PROP
        = "net.java.sip.communicator.plugin.openmeetings.OM_URI_CONTEXT";

    /**
     * Indicates the OpemMeetings proxy.
     */
    public static final String PROXY_PROP
        = "net.java.sip.communicator.plugin.openmeetings.PROXY";

    /**
     * Indicates the OpemMeetings SOAP login user.
     */
    public static final String LOGIN_PROP
        = "net.java.sip.communicator.plugin.openmeetings.LOGIN";

    /**
     * If defined, use the display name instead of the login user name.
     */
    public static final String DISPLAYED_NAME_PROP
        = "net.java.sip.communicator.plugin.openmeetings.DISPLAYED_NAME";

    /**
     * If defined, use the OpenMeetings Room ID.
     */
    public static final String ROOM_ID_PROP
        = "net.java.sip.communicator.plugin.openmeetings.ROOM_ID";

    /**
     * Called when this bundle is started so the Framework can perform the
     * bundle-specific activities necessary to start this bundle. In the case of
     * our example plug-in we create our menu item and register it as a plug-in
     * component in the right button menu of the contact list.
     */
    public void start(BundleContext bc) throws Exception
    {
        bundleContext = bc;
        resourceService =
            ResourceManagementServiceUtils
                .getService(OpenmeetingsPluginActivator.bundleContext);

        OpenmeetingsPluginMenuItem openMeetingsPlugin =
            new OpenmeetingsPluginMenuItem(bc);

        Hashtable<String, String> containerFilter =
            new Hashtable<String, String>();
        containerFilter.put(Container.CONTAINER_ID,
            Container.CONTAINER_CONTACT_RIGHT_BUTTON_MENU.getID());

        bc.registerService(PluginComponent.class.getName(), openMeetingsPlugin,
            containerFilter);

        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(ConfigurationForm.FORM_TYPE,
            ConfigurationForm.ADVANCED_TYPE);
        if(!getConfigService().getBoolean(DISABLED_PROP, false))
        {
        bc.registerService(
            ConfigurationForm.class.getName(),
            new LazyConfigurationForm(
                "net.java.sip.communicator.plugin.openmeetings.OpenmeetingsConfigPanel",
                getClass().getClassLoader(), "plugin.skinmanager.PLUGIN_ICON",
                "plugin.openmeetings.PLUGIN_NAME", 1002, true), properties);
        }
        openMeetingsPlugin.setOMserver(getConfigService().getString(SERVER_PROP, ""));
        openMeetingsPlugin.setOMprotoPrefix(getConfigService().getString(PROTOCOL_PREFIX_PROP, ""));
        openMeetingsPlugin.setOMuriContext(getConfigService().getString(OM_URI_CONTEXT_PROP, ""));
        openMeetingsPlugin.setOMproxy(getConfigService().getString(PROXY_PROP, ""));
        openMeetingsPlugin.setOMlogin(getConfigService().getString(LOGIN_PROP, ""));
        // ProvisioningService takes care of .PASSWORD
        openMeetingsPlugin.setOMdisplayedName(getConfigService().getString(DISPLAYED_NAME_PROP, ""));
        openMeetingsPlugin.setOMRoomID(getConfigService().getString(ROOM_ID_PROP, ""));
    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle. In the case of
     * our example plug-in we have nothing to do here.
     */
    public void stop(BundleContext bc) throws Exception
    {

    }

    /**
     * Returns a reference to a ConfigurationService implementation currently
     * registered in the bundle context or null if no such implementation was
     * found.
     * 
     * @return a currently valid implementation of the ConfigurationService.
     */
    public static ConfigurationService getConfigService()
    {
        return ServiceUtils.getService(bundleContext,
            ConfigurationService.class);
    }

    /**
     * Returns a reference to a CredentialsStorageService implementation
     * currently registered in the bundle context or null if no such
     * implementation was found.
     * 
     * @return a currently valid implementation of the
     *         CredentialsStorageService.
     */
    public static CredentialsStorageService getCredService()
    {
        return ServiceUtils.getService(bundleContext,
            CredentialsStorageService.class);
    }
}

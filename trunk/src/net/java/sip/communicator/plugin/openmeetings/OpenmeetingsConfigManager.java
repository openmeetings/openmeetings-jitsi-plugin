package net.java.sip.communicator.plugin.openmeetings;

// import net.java.sip.communicator.impl.protocol.zeroconf.MessageZeroconfImpl;
// import net.java.sip.communicator.service.configuration.ConfigurationService;
import org.jitsi.service.configuration.*;
import net.java.sip.communicator.util.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Locale;

public class OpenmeetingsConfigManager
{
    Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

    private String server;

    private String protoPrefix;

    private String omUriContext;

    private String login;

    private String password;

    private String displayedName;

    private String proxy;

    private String RoomID;

    private OpenmeetingsPluginSoapClient soapClient;

    private static BundleContext bundleContext;

    private static ConfigurationService configurationService = null;

    private static OpenmeetingsConfigManager instance;

    private OpenmeetingsConfigManager()
    {

        super();
        soapClient = new OpenmeetingsPluginSoapClient();
    }

    public static OpenmeetingsConfigManager getInstance()
    {
        if (instance == null)
        {
            instance = new OpenmeetingsConfigManager();
        }
        return instance;
    }

    public String createInvitationUrl(String hash) throws Exception
    {
        final String url =
            getProtoPrefix() + getServer() + getOmUriContext() +"?invitationHash=" + hash;
        System.out.println("INVITATION URL = " + url);
        return url;
    }

    public String getInvitationUrl(String displayedName) throws Exception
    {
        String protoPrefix = getProtoPrefix();
        String server = getServer();
        String uriContext = getOmUriContext();
        soapClient.setServerUrl(protoPrefix + server + uriContext);
        soapClient.setProxy(getProxy());

        String invitationHash = null;

        try
        {
            invitationHash =
                soapClient.getInvitationHash(getLogin(), getPassword(),
                    displayedName, getRoomID());
        }
        catch (Exception e)
        {
            logger.error(e);
        }

        if (invitationHash == null)
            return null;

        String invitationUrl = createInvitationUrl(invitationHash);
        return addLanguageTag(invitationUrl);
    }

    private String addLanguageTag(String invitationUrl) {
        String language = Locale.getDefault().getLanguage();
        int id = 1;
        if ("ar".equals(language)) {
            id = 14;
        } else if ("bg".equals(language)) {
            id = 30;
        } else if ("cs".equals(language)) {
            id = 22;
        } else if ("de".equals(language)) {
            id = 2;
        } else if ("el".equals(language)) {
            id = 26;
        } else if ("es".equals(language)) {
            id = 8;
        } else if ("fr".equals(language)) {
            id = 4;
        } else if ("id".equals(language)) {
            id = 16;
        } else if ("it".equals(language)) {
            id = 5;
        } else if ("nl".equals(language)) {
            id = 27;
        } else if ("pl".equals(language)) {
            id = 25;
        } else if ("pt".equals(language)) {
            id = 6;
        } else if ("ro".equals(language)) {
            id = 1;
        } else if ("ru".equals(language)) {
            id = 9;
        } else if ("si".equals(language)) {
            id = 1;
        } else if ("sq".equals(language)) {
            id = 1;
        } else if ("tr".equals(language)) {
            id = 18;
        } else if ("zh".equals(language)) {
            id = 11;
        }
        return invitationUrl + "&language=" + id;
    }

    public static ConfigurationService getConfigurationService()
    {
        if (configurationService == null)
        {
            ServiceReference confReference =
                bundleContext.getServiceReference(ConfigurationService.class
                    .getName());
            configurationService =
                (ConfigurationService) bundleContext.getService(confReference);
        }
        return configurationService;
    }

    public void setServer(String server)
    {
        this.server = server;
        getConfigurationService().
                setProperty(OpenmeetingsPluginActivator.SERVER_PROP, server);
    }

    public String getServer()
    {
        String value =
            (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.SERVER_PROP);
        if (null == value)
        {
            value = "";
        }
        server = value;
        return server;
    }

    public void setProtoPrefix(String protoPrefix)
    {
        this.protoPrefix = protoPrefix;
        getConfigurationService().setProperty(
            OpenmeetingsPluginActivator.PROTOCOL_PREFIX_PROP, protoPrefix);
    }

    public String getProtoPrefix()
    {
        String value =
            (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.PROTOCOL_PREFIX_PROP);
        if (null == value)
        {
            value = "";
        }
        protoPrefix = value;
        return protoPrefix;
    }

    public void setOmUriContext(String omUriContext)
    {
        this.omUriContext = omUriContext;
        getConfigurationService().setProperty(
            OpenmeetingsPluginActivator.OM_URI_CONTEXT_PROP, omUriContext);
    }

    public String getOmUriContext()
    {
        String value =
            (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.OM_URI_CONTEXT_PROP);
        if (null == value)
        {
            value = "";
        }
        if (!value.endsWith("/"))
        {
            value += "/";
        }
        omUriContext = value;
        return omUriContext;
    }

    public void setLogin(String login)
    {
        this.login = login;
        getConfigurationService().setProperty(
                OpenmeetingsPluginActivator.LOGIN_PROP, login);
    }

    public String getLogin()
    {
        login = (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.LOGIN_PROP);
        return login;
    }

    public boolean setPassword(String password)
    {
        if (password == null)
            return false;
        return OpenmeetingsPluginActivator.getCredService().
                storePassword("net.java.sip.communicator.plugin.openmeetings", 
                password);
    }

    public String getPassword()
    {
        return OpenmeetingsPluginActivator.getCredService().
                loadPassword("net.java.sip.communicator.plugin.openmeetings");
    }

    public void setDisplayedName(String displayedName)
    {
        this.displayedName = displayedName.trim();
        getConfigurationService().setProperty(
                OpenmeetingsPluginActivator.DISPLAYED_NAME_PROP, 
                this.displayedName);
    }

    public String getDisplayedName()
    {
        displayedName = (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.DISPLAYED_NAME_PROP);
        return displayedName;
    }

    public void setProxy(String proxy)
    {
        this.proxy = proxy;
        getConfigurationService().setProperty(
                OpenmeetingsPluginActivator.PROXY_PROP, proxy);
    }

    public String getProxy()
    {
        proxy = (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.PROXY_PROP);
        return proxy;
    }

    public void setRoomID(String RoomID)
    {
        this.RoomID = RoomID;
        getConfigurationService().setProperty(
                OpenmeetingsPluginActivator.ROOM_ID_PROP, RoomID);
    }

    public String getRoomID()
    {
        RoomID = (String) getConfigurationService().getProperty(
                OpenmeetingsPluginActivator.ROOM_ID_PROP);
        return RoomID;
    }

    public void setContext(BundleContext bc)
    {
        bundleContext = bc;
    }
}

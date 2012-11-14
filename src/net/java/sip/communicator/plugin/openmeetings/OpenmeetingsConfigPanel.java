package net.java.sip.communicator.plugin.openmeetings;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.java.sip.communicator.util.Logger;
import net.java.sip.communicator.util.swing.*;

import org.osgi.framework.*;

public class OpenmeetingsConfigPanel
    extends TransparentPanel
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

    private final JTextField teServer = new JTextField(20);

    private final JTextField teLogin = new JTextField(20);

    private final JPasswordField tePassword = new JPasswordField(20);

    private final JTextField tedisplayedName = new JTextField(20);

    private final JTextField teProxy = new JTextField(20);
 
    private final JTextField teRoomID = new JTextField(20);

    private final JTextField fakeField = new JTextField(20);

    private final JButton btOk = new JButton(
        OpenmeetingsPluginActivator.resourceService
            .getI18NString("plugin.openmeetings.BUTTON_OK"));

    private String server;

    private String login;

    private String password;

    private String displayedName;

    private String proxy;

    private String RoomID;

    public OpenmeetingsConfigPanel()
        throws Exception
    {
        super(new BorderLayout());

        Dimension prefSize = new Dimension(105, 30);
        JPanel headerPanel = new TransparentPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));

        JLabel lblHeader =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.CONFIG_HEADER"));

        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(lblHeader);
        lblHeader.setPreferredSize(new Dimension(200, 30));

        JPanel serverPanel = new TransparentPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.LINE_AXIS));
        JLabel lblServer =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.SERVER"));
        lblServer.setPreferredSize(prefSize);
        serverPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        serverPanel.add(lblServer);
        serverPanel.add(teServer);

        JPanel loginPanel = new TransparentPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.LINE_AXIS));
        JLabel lblLogin =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.LOGIN"));
        lblLogin.setPreferredSize(prefSize);
        loginPanel.setAlignmentX(LEFT_ALIGNMENT);
        loginPanel.add(lblLogin);
        loginPanel.add(teLogin);

        JPanel passwordPanel = new TransparentPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel,
            BoxLayout.LINE_AXIS));
        JLabel lblPassword =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.PASSWORD"));
        lblPassword.setPreferredSize(prefSize);
        passwordPanel.setAlignmentX(LEFT_ALIGNMENT);
        passwordPanel.add(lblPassword);
        passwordPanel.add(tePassword);
        
        JPanel displayedNamePanel = new TransparentPanel();
        displayedNamePanel.setLayout(new BoxLayout(displayedNamePanel, 
                BoxLayout.LINE_AXIS));
        JLabel lbldisplayedName =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.DISPLAYED_NAME"));
        lbldisplayedName.setPreferredSize(prefSize);
        displayedNamePanel.setAlignmentX(LEFT_ALIGNMENT);
        displayedNamePanel.add(lbldisplayedName);
        displayedNamePanel.add(tedisplayedName);

        JPanel proxyPanel = new TransparentPanel();
        proxyPanel.setLayout(new BoxLayout(proxyPanel, BoxLayout.LINE_AXIS));
        JLabel lblProxy =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.PROXY"));
        lblProxy.setPreferredSize(prefSize);
        proxyPanel.setAlignmentX(LEFT_ALIGNMENT);
        proxyPanel.add(lblProxy);
        proxyPanel.add(teProxy);        

        JPanel RoomIDPanel = new TransparentPanel();
        RoomIDPanel.setLayout(new BoxLayout(RoomIDPanel, 
                BoxLayout.LINE_AXIS));
        JLabel lblRoomID =
            new JLabel(
                OpenmeetingsPluginActivator.resourceService
                    .getI18NString("plugin.openmeetings.ROOM_ID"));
        lblRoomID.setPreferredSize(prefSize);
        RoomIDPanel.setAlignmentX(LEFT_ALIGNMENT);
        RoomIDPanel.add(lblRoomID);
        RoomIDPanel.add(teRoomID);

        OpenmeetingsConfigManager cfg = OpenmeetingsConfigManager.getInstance();
        String serverUri = cfg.getServer();
        if (!serverUri.isEmpty())
        {
            serverUri =
                cfg.getProtoPrefix() + serverUri + cfg.getOmUriContext();
        }

        teServer.setText(serverUri);
        teLogin.setText(OpenmeetingsConfigManager.getInstance().getLogin());
        tePassword.setText(OpenmeetingsConfigManager.getInstance()
                .getPassword());
        tedisplayedName.setText(OpenmeetingsConfigManager.getInstance()
                .getDisplayedName());
        teProxy.setText(OpenmeetingsConfigManager.getInstance()
                .getProxy());
        teRoomID.setText(OpenmeetingsConfigManager.getInstance()
                .getRoomID());

        JPanel buttonPanel = new TransparentPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
        btOk.addActionListener(new ButtonOkListener());
        btOk.setAlignmentX(LEFT_ALIGNMENT);
        btOk.setPreferredSize(new Dimension(50, 30));
        // buttonPanel.add( Box.createRigidArea( new Dimension( 60, 5)));
        buttonPanel.add(btOk);
        buttonPanel.add(fakeField);
        fakeField.setVisible(false);

        JPanel omPanel = new TransparentPanel();
        omPanel.setLayout(new BoxLayout(omPanel, BoxLayout.PAGE_AXIS));
        omPanel.add(headerPanel);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(serverPanel, BorderLayout.WEST);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(loginPanel);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(passwordPanel);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(displayedNamePanel);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(proxyPanel);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(RoomIDPanel);
        omPanel.add(Box.createRigidArea(new Dimension(20, 5)));
        omPanel.add(buttonPanel);

        add(omPanel, BorderLayout.PAGE_START);
    }

    public void setServer(String server)
    {

        this.server = server;
    }

    public String getServer()
    {
        return server;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getLogin()
    {
        return login;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setDisplayedName(String displayedName)
    {
        this.displayedName = displayedName;
    }

    public String getDisplayedName()
    {
        return displayedName;
    }

    public void setProxy(String proxy)
    {
        this.proxy = proxy;
    }

    public String getProxy()
    {
        return proxy;
    }

    public void setRoomID(String RoomID)
    {
        this.RoomID = RoomID;
    }

    public String getRoomID()
    {
        return RoomID;
    }

    private class ButtonOkListener
        implements ActionListener
    {
        /**
         * Invoked when an action occurs.
         * 
         * @param e <tt>ActionEvent</tt>.
         */
        public void actionPerformed(ActionEvent e)
        {
            String protoPrefix = "http://";
            String serverUri = teServer.getText();
            String uriContext = "/";
            if (serverUri.startsWith("http://"))
            {
                protoPrefix = "http://";
                serverUri = serverUri.substring(protoPrefix.length());
            }
            else if (serverUri.startsWith("https://"))
            {
                protoPrefix = "https://";
                serverUri = serverUri.substring(protoPrefix.length());
            }
            int slashPos = serverUri.indexOf('/');
            if (slashPos >= 0)
            {
                uriContext = serverUri.substring(slashPos);
                serverUri = serverUri.substring(0, slashPos);
            }

            OpenmeetingsConfigManager cfg =
                OpenmeetingsConfigManager.getInstance();
            cfg.setServer(serverUri);
            cfg.setProtoPrefix(protoPrefix);
            cfg.setOmUriContext(uriContext);
            cfg.setLogin(teLogin.getText());
            cfg.setDisplayedName(tedisplayedName.getText());
            cfg.setProxy(teProxy.getText());
            if (!cfg.setPassword(new String(tePassword.getPassword())))
                logger.error("Cannot set password");
            cfg.setRoomID(teRoomID.getText());
        }
    }

    private class ButtonCancelListener
        implements ActionListener
    {
        /**
         * Invoked when an action occurs.
         * 
         * @param e <tt>ActionEvent</tt>.
         */
        public void actionPerformed(ActionEvent e)
        {

        }
    }
}

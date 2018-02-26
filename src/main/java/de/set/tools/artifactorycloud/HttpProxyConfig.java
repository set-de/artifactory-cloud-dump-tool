package de.set.tools.artifactorycloud;

import org.jfrog.artifactory.client.ProxyConfig;

public class HttpProxyConfig extends ProxyConfig {

    public HttpProxyConfig(final String host, final int port, final String user, final String password) {
        super(host, port, "http", user, password); //$NON-NLS-1$
    }

}

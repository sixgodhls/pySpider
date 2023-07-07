package okhttp3.internal.platform;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Protocol;
import org.conscrypt.Conscrypt;
import org.conscrypt.OpenSSLProvider;

/* loaded from: classes.dex */
public class ConscryptPlatform extends Platform {
    private ConscryptPlatform() {
    }

    private Provider getProvider() {
        return new OpenSSLProvider();
    }

    @Override // okhttp3.internal.platform.Platform
    public X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
        if (!Conscrypt.isConscrypt(sslSocketFactory)) {
            return super.trustManager(sslSocketFactory);
        }
        try {
            Object sp = readFieldOrNull(sslSocketFactory, Object.class, "sslParameters");
            if (sp != null) {
                return (X509TrustManager) readFieldOrNull(sp, X509TrustManager.class, "x509TrustManager");
            }
            return null;
        } catch (Exception e) {
            throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on Conscrypt", e);
        }
    }

    @Override // okhttp3.internal.platform.Platform
    public void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
        if (Conscrypt.isConscrypt(sslSocket)) {
            if (hostname != null) {
                Conscrypt.setUseSessionTickets(sslSocket, true);
                Conscrypt.setHostname(sslSocket, hostname);
            }
            List<String> names = Platform.alpnProtocolNames(protocols);
            Conscrypt.setApplicationProtocols(sslSocket, (String[]) names.toArray(new String[0]));
            return;
        }
        super.configureTlsExtensions(sslSocket, hostname, protocols);
    }

    @Override // okhttp3.internal.platform.Platform
    @Nullable
    public String getSelectedProtocol(SSLSocket sslSocket) {
        if (Conscrypt.isConscrypt(sslSocket)) {
            return Conscrypt.getApplicationProtocol(sslSocket);
        }
        return super.getSelectedProtocol(sslSocket);
    }

    @Override // okhttp3.internal.platform.Platform
    public SSLContext getSSLContext() {
        try {
            return SSLContext.getInstance("TLS", getProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No TLS provider", e);
        }
    }

    public static Platform buildIfSupported() {
        try {
            Class.forName("org.conscrypt.ConscryptEngineSocket");
            if (!Conscrypt.isAvailable()) {
                return null;
            }
            Conscrypt.setUseEngineSocketByDefault(true);
            return new ConscryptPlatform();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
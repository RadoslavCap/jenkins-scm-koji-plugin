package hudson.plugins.scm.koji.model;

import hudson.plugins.scm.koji.Constants;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@XmlAccessorType(XmlAccessType.FIELD)
public class RPM implements java.io.Serializable {

    @XmlElement(name = Constants.name)
    private final String name;
    @XmlElement(name = Constants.version)
    private final String version;
    @XmlElement(name = Constants.release)
    private final String release;
    @XmlElement(name = Constants.nvr)
    private final String nvr;
    @XmlElement(name = Constants.arch)
    private final String arch;
    @XmlElement(name = Constants.filename)
    private final String filename;
    private String url;
    private String hashSum;

    public RPM(String name, String version, String release, String nvr, String arch, String filename) {
        this.name = name;
        this.version = version;
        this.release = release;
        this.nvr = nvr;
        this.arch = arch;
        this.filename = filename;
    }

    public RPM() {
        this.name = null;
        this.version = null;
        this.release = null;
        this.nvr = null;
        this.arch = null;
        this.filename = null;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getRelease() {
        return release;
    }

    public String getNvr() {
        return nvr;
    }

    public String getArch() {
        return arch;
    }

    @Override
    public String toString() {
        if (Suffix.INSTANCE.endsWithSuffix(nvr)) {
            return nvr;
        }
        Suffix suffix = Suffix.INSTANCE;
        return nvr + '.' + arch + '.' + suffix.getSuffix(url);
    }

    public String getFilename(String suffix) {
        return Optional.ofNullable(filename).orElse(nvr + '.' + arch + "." + suffix);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasUrl() {
        return url != null;
    }

    public void setHashSum(File file) throws IOException, NoSuchAlgorithmException {
        if (file == null) {
            hashSum = null;
            return;
        }
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream stream = new FileInputStream(file);
        stream.read(buffer);
        stream.close();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(buffer);
        byte[] digest = messageDigest.digest(buffer);
        StringBuilder builder = new StringBuilder();
        for (byte bytes : digest) {
            builder.append(String.format("%02x", bytes));
        }
        hashSum = builder.toString();
    }

    public String getHashSum() {
        return hashSum;
    }

    public static enum Suffix {
        INSTANCE;

        private final String[] suffixes = {"rpm", "tarxz", "msi"};

        public String[] getSuffixes() {
            return suffixes;
        }

        public String getSuffix(String url) {
            for (String suffix : suffixes) {
                if (url.endsWith(suffix)) {
                    return suffix;
                }
            }
            return "";
        }

        public boolean endsWithSuffix(String url) {
            for (String suffix : suffixes) {
                if (url.endsWith(suffix)) {
                    return true;
                }
            }
            return false;
        }
    }
}

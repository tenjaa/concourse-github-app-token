package eu.neufeldt.concoursegithubcredentials.model;

public class InRequest {
    public final Source source;
    public final Version version;

    public InRequest(Source source, Version version) {
        this.source = source;
        this.version = version;
    }

    public static class Source {
        public final int appId;
        public final int installationId;
        public final String privateKey;

        public Source(int appId, int installationId, String privateKey) {
            this.appId = appId;
            this.installationId = installationId;
            this.privateKey = privateKey;
        }
    }
}

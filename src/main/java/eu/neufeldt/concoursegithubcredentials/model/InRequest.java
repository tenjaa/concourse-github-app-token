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
        public final String user;
        public final String org;
        public final String privateKey;

        public Source(int appId, String user, String org, String privateKey) {
            this.appId = appId;
            this.user = user;
            this.org = org;
            this.privateKey = privateKey;
        }
    }
}

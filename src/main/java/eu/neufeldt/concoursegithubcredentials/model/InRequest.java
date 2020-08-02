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
        public final String privateKey;
        public final String user;
        public final String org;

        public Source(int appId, String privateKey, String user, String org) {
            this.appId = appId;
            this.privateKey = privateKey;
            this.user = user;
            this.org = org;
        }
    }
}

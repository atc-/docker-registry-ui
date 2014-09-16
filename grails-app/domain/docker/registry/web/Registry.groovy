package docker.registry.web

import groovyx.net.http.HTTPBuilder

class Registry {
    String host = "localhost"
    int port
    String apiVersion = "v1"
    String username
    String password

    /**
     * Whether the registry is reachable or not.
     */
    transient boolean unreachable = false

    def repositoryService

    static constraints = {
        username nullable: true
        password nullable: true
    }

    static transients = ['toUrl', 'repositories', 'ping', 'fromUrl', 'setAsUnreachable']

    def toUrl() {
        def urlString = "http://${this.host}:${this.port}/${this.apiVersion}"
        if (username) {
            if (password) urlString = urlString.replace("://", "://$username:$password@")
            else urlString = urlString.replace("://", "://$username@")
        }
        urlString
    }

    /**
     * Sets the unreachable value to false in case the registry is unreachable during search.
     */
    def setAsUnreachable() {
        unreachable = true
    }

    def getRepositories() {
        repositoryService.index(this)
    }

    def ping() {
        repositoryService.ping(this)
    }

    /**
     * Static factory method for creating an instance from a URL.
     * @param urlStr a url in the format: http://hostOrIP:OptionalPort/v1/
     **/
    static def fromUrl(final String urlStr) {
        if (urlStr?.endsWith("/v1/")) { //FIXME this won't work for new api versions
            def url = urlStr.toURL()
            if (url) {
                def auth = url.userInfo?.split(":")
                return new Registry(
                        host: url.host,
                        port: url.port == -1 ? 80 : url.port,
                        apiVersion: url.path.replaceAll("\\p{Punct}", ""),
                        username: auth?.length > 0 ? auth[0] : null,
                        password: auth?.length > 1 ? auth[1] : null
                )
            }
        }
        null
    }

    @Override
    public String toString() {
        return "Registry{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", apiVersion='" + apiVersion + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", repositoryService=" + repositoryService +
                ", version=" + version +
                '}';
    }
}

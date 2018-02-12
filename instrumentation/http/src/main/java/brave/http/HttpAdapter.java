package brave.http;

import brave.internal.Nullable;
import java.net.URI;

public abstract class HttpAdapter<Req, Resp> {

  /**
   * The HTTP method, or verb, such as "GET" or "POST" or null if unreadable.
   *
   * <p>Conventionally associated with the key "http.method"
   */
  @Nullable public abstract String method(Req request);

  /**
   * The absolute http path, without any query parameters or null if unreadable. Ex.
   * "/objects/abcd-ff"
   *
   * <p>Conventionally associated with the key "http.path"
   */
  @Nullable public String path(Req request) {
    String url = url(request);
    if (url == null) return null;
    return URI.create(url).getPath(); // TODO benchmark
  }

  /**
   * The entire URL, including the scheme, host and query parameters if available or null if
   * unreadable.
   *
   * <p>Conventionally associated with the key "http.url"
   */
  @Nullable public abstract String url(Req request);

  /**
   * Returns one value corresponding to the specified header, or null.
   */
  @Nullable public abstract String requestHeader(Req request, String name);

  /**
   * An expression representing an application endpoint, used to group similar requests together.
   *
   * <p>For example, the template "/products/{key}", would match "/products/1" and "/products/2".
   * There is no format required for the encoding, as it is sometimes application defined. The
   * important part is that the value namespace is low cardinality.
   *
   * <p>Conventionally associated with the key "http.template"
   *
   * <p>Eventhough the template is associated with the request, not the response, this is present
   * on the response object. The reasons is that many server implementations process the request
   * before they can identify the route route.
   */
  // BRAVE5: It isn't possible for a user to easily consume HttpServerAdapter, which is why this
  // method, while generally about the server, is pushed up to the HttpAdapter. The signatures for
  // sampling and parsing could be changed to make it more convenient.
  @Nullable public String template(Resp response) {
    return null;
  }

  /**
   * The HTTP status code or null if unreadable.
   *
   * <p>Conventionally associated with the key "http.status_code"
   */
  @Nullable public abstract Integer statusCode(Resp response);

  HttpAdapter() {
  }
}

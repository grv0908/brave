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
   * An expression such as "/items/:itemId" representing an application endpoint, conventionally
   * associated with the tag key "http.route".
   *
   * <p>The http route groups similar requests together, so results in limited cardinality, often
   * better choice for a span name vs the http method. However, the route can be absent on redirect
   * or file-not-found. Also, not all frameworks include support for http route expressions,
   * and some don't expose templates programmatically for readback.
   *
   * <p>For example, the route "/users/{userId}", matches "/users/25f4c31d" and "/users/e3c553be".
   * If a span name function used the http path instead, it could DOS-style attack vector on your
   * span name index, as it would grow unbounded vs "/users/{userId}". Even if different frameworks
   * use different formats, like "/users/[0-9a-f]+" or "/users/:userId", the cardinality is still
   * fixed with regards to request count.
   *
   * <p>Eventhough the route is associated with the request, not the response, this is present
   * on the response object. The reasons is that many server implementations process the request
   * before they can identify the route route.
   */
  // BRAVE5: It isn't possible for a user to easily consume HttpServerAdapter, which is why this
  // method, while generally about the server, is pushed up to the HttpAdapter. The signatures for
  // sampling and parsing could be changed to make it more convenient.
  @Nullable public String route(Resp response) {
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


package ubp.edu.com.ar.finalproyect.utils;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Httpful {

    private final Client client;
    private WebTarget target;
    private String method = HttpMethod.GET;
    private Entity<?> entity;
    private final Map<String, String> extraHeaders = new HashMap<>();
    private final Gson gson = new Gson();

    public Httpful(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.target = this.client.target(baseUrl);
    }

    public Httpful path(String path) {
        this.target = this.target.path(path);
        return this;
    }

    public Httpful addQueryParam(String key, String value) {
        this.target = this.target.queryParam(key, value);
        return this;
    }

    public Httpful method(String method) {
        this.method = method;
        return this;
    }

    public Httpful bearer(String token) {
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("El token Bearer no puede ser nulo/vacío");
        this.extraHeaders.put(HttpHeaders.AUTHORIZATION, "Bearer " + token.trim());
        return this;
    }

    public Httpful apiKey(String headerName, String value) {
        if (headerName == null || headerName.isBlank())
            throw new IllegalArgumentException("El nombre del header de API Key no puede ser nulo/vacío");
        this.extraHeaders.put(headerName, value);
        return this;
    }

    public Httpful header(String name, String value) {
        this.extraHeaders.put(name, value);
        return this;
    }

    public Httpful get() {
        this.method = HttpMethod.GET;
        this.entity = null;
        return this;
    }

    public Httpful delete() {
        this.method = HttpMethod.DELETE;
        this.entity = null;
        return this;
    }

    public Httpful post(Object body) {
        this.method = HttpMethod.POST;
        this.entity = Entity.entity(gson.toJson(body), MediaType.APPLICATION_JSON);
        return this;
    }

    public Httpful put(Object body) {
        this.method = HttpMethod.PUT;
        this.entity = Entity.entity(gson.toJson(body), MediaType.APPLICATION_JSON);
        return this;
    }


    public Httpful postWrapped(String wrapperKey, Object body) {
        JsonObject wrapped = new JsonObject();
        wrapped.add(wrapperKey, gson.toJsonTree(body));
        this.method = HttpMethod.POST;
        this.entity = Entity.entity(gson.toJson(wrapped), MediaType.APPLICATION_JSON);
        return this;
    }

    public Httpful putWrapped(String wrapperKey, Object body) {
        JsonObject wrapped = new JsonObject();
        wrapped.add(wrapperKey, gson.toJsonTree(body));
        this.method = HttpMethod.PUT;
        this.entity = Entity.entity(gson.toJson(wrapped), MediaType.APPLICATION_JSON);
        return this;
    }

    public <T> T execute(Class<T> responseType) {
        return executeRequest(responseType);
    }

    public <T> T execute(Type responseType) {
        return executeRequest(responseType);
    }


    public <T> T executeUnwrapped(String wrapperKey, Class<T> responseType) {
        Response response = sendRequest();
        try {
            int status = response.getStatus();

            if (status >= 200 && status < 300) {
                if (status == 204 || response.getLength() == 0) {
                    return null;
                }
                String json = response.readEntity(String.class);
                if (json == null || json.isBlank())
                    return null;

                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                JsonElement element = jsonObject.get(wrapperKey);

                if (element == null) {
                    throw new RuntimeException("No se encontró la clave '" + wrapperKey + "' en la respuesta JSON");
                }

                return gson.fromJson(element, responseType);
            }

            // Error handling
            String errorBody = safeReadBody(response);
            String message = "Error HTTP " + status;
            String detail = extractMessageFromJson(errorBody);
            if (!detail.isEmpty())
                message += " - " + detail;

            throw new RuntimeException(message + (errorBody.isEmpty() ? "" : (": " + errorBody)));
        } finally {
            response.close();
        }
    }


    public <T> T executeUnwrapped(String wrapperKey, Type responseType) {
        Response response = sendRequest();
        try {
            int status = response.getStatus();

            if (status >= 200 && status < 300) {
                if (status == 204 || response.getLength() == 0) {
                    return null;
                }
                String json = response.readEntity(String.class);
                if (json == null || json.isBlank())
                    return null;

                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                JsonElement element = jsonObject.get(wrapperKey);

                if (element == null) {
                    throw new RuntimeException("No se encontró la clave '" + wrapperKey + "' en la respuesta JSON");
                }

                return gson.fromJson(element, responseType);
            }

            // Error handling
            String errorBody = safeReadBody(response);
            String message = "Error HTTP " + status;
            String detail = extractMessageFromJson(errorBody);
            if (!detail.isEmpty())
                message += " - " + detail;

            throw new RuntimeException(message + (errorBody.isEmpty() ? "" : (": " + errorBody)));
        } finally {
            response.close();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T executeRequest(Object responseType) {
        Response response = sendRequest();
        try {
            int status = response.getStatus();

            // Éxito si 2xx
            if (status >= 200 && status < 300) {
                // 204 No Content
                if (status == 204 || response.getLength() == 0) {
                    return null;
                }
                String json = response.readEntity(String.class);
                if (json == null || json.isBlank())
                    return null;

                if (responseType instanceof Class<?>) {
                    return this.gson.fromJson(json, (Class<T>) responseType);
                } else {
                    return this.gson.fromJson(json, (Type) responseType);
                }
            }

            // Error
            String errorBody = safeReadBody(response);
            String message = "Error HTTP " + status;
            String detail = extractMessageFromJson(errorBody);
            if (!detail.isEmpty())
                message += " - " + detail;

            throw new RuntimeException(message + (errorBody.isEmpty() ? "" : (": " + errorBody)));
        } finally {
            response.close();
        }
    }

    private Response sendRequest() {
        Invocation.Builder builder = this.target
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Headers extra (Authorization, API Key, etc.)
        this.extraHeaders.forEach(builder::header);

        return switch (this.method) {
            case HttpMethod.GET -> builder.get();
            case HttpMethod.POST -> builder.post(this.entity);
            case HttpMethod.PUT -> builder.put(this.entity);
            case HttpMethod.DELETE -> builder.delete();
            default -> throw new RuntimeException("Método HTTP no soportado: " + this.method);
        };
    }

    private static String safeReadBody(Response response) {
        try {
            String s = response.readEntity(String.class);
            return s == null ? "" : s;
        } catch (Exception e) {
            return "";
        }
    }

    private String extractMessageFromJson(String body) {
        try {
            if (body == null || body.isBlank())
                return "";
            JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
            // campos comunes en APIs
            if (obj.has("message"))
                return obj.get("message").getAsString();
            if (obj.has("error_description"))
                return obj.get("error_description").getAsString();
            if (obj.has("error"))
                return obj.get("error").getAsString();
        } catch (Exception ignored) {
        }
        return "";
    }

}
